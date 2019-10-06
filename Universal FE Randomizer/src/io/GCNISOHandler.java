package io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.DebugPrinter;
import util.DiffCompiler;
import util.WhyDoesJavaNotHaveThese;

enum FSTEntryType {
	ROOT, FILE, DIRECTORY
}

public class GCNISOHandler {
	
	private FileHandler handler;
	
	private String gameCode;
	private String gameName;
	
	private long fstOffset;
	private long fstSize;
	private long fstEntryCount;
	private long fstStringTableOffset;
	
	private FSTDirectoryEntry rootEntry;
	private Map<String, FSTEntry> fstLookup;
	
	public class GCNFileHandler {
		private FSTFileEntry fileEntry;
		private FileHandler handler;
		
		private DiffCompiler appliedDiffs;
		
		private long nextReadOffset = 0;
		
		public GCNFileHandler(FSTFileEntry file, FileHandler handler) {
			this.fileEntry = file;
			this.handler = handler;
			nextReadOffset += fileEntry.fileOffset;
		}
		
		public void setAppliedDiffs(DiffCompiler diffs) {
			appliedDiffs = diffs;
		}
		
		public void clearAppliedDiffs() {
			appliedDiffs = null;
		}
		
		public long getNextReadOffset() {
			return nextReadOffset - fileEntry.fileOffset;
		}
		
		public void setNextReadOffset(long newOffset) {
			nextReadOffset = newOffset + fileEntry.fileOffset;
		}
		
		public byte continueReadingNextByte() {
			long oldOffset = handler.getNextReadOffset();
			handler.setNextReadOffset(nextReadOffset);
			byte outputByte = handler.continueReadingNextByte();
			nextReadOffset++;
			handler.setNextReadOffset(oldOffset);
			return outputByte;
		}
		
		public byte[] continueReadingBytes(int numBytes) {
			long oldOffset = handler.getNextReadOffset();
			handler.setNextReadOffset(nextReadOffset);
			byte[] outputBytes = handler.continueReadingBytes(numBytes);
			nextReadOffset = handler.getNextReadOffset();
			handler.setNextReadOffset(oldOffset);
			
			if (appliedDiffs != null) {
				return appliedDiffs.byteArrayWithDiffs(outputBytes, nextReadOffset - numBytes);
			}
			
			return outputBytes;
		}
		
		public byte[] continueReadingBytesUpToNextTerminator(long maxOffset) {
			long oldOffset = handler.getNextReadOffset();
			handler.setNextReadOffset(nextReadOffset);
			byte[] outputBytes = handler.continueReadingBytesUpToNextTerminator(maxOffset + fileEntry.fileOffset);
			nextReadOffset = handler.getNextReadOffset();
			handler.setNextReadOffset(oldOffset);
			
			return outputBytes;
		}
		
		public byte[] readBytesAtOffset(long offset, int numBytes) {
			long oldOffset = handler.getNextReadOffset();
			byte[] outputBytes = handler.readBytesAtOffset(offset + fileEntry.fileOffset, numBytes);
			nextReadOffset = handler.getNextReadOffset();
			handler.setNextReadOffset(oldOffset);
			
			if (appliedDiffs != null) {
				return appliedDiffs.byteArrayWithDiffs(outputBytes, offset + fileEntry.fileOffset);
			}
			
			return outputBytes;
		}

		
		public long getFileLength() {
			return fileEntry.fileSize;
		}
		
	}
	
	private class FSTEntry {
		public FSTEntryType type;
		public long entryOffset;
		
		public long nameOffset;
		
		public FSTDirectoryEntry parentEntry;
	}
	
	private class FSTFileEntry extends FSTEntry {
		public long fileSize;
		public long fileOffset;
	}
	
	private class FSTDirectoryEntry extends FSTEntry {
		// These are index offsets. They are the offset from the beginning of fst.bin if multiplied by 0xC.
		public long parentOffset;
		public long nextOffset;
		
		public List<FSTEntry> childEntries;
		
		public FSTDirectoryEntry() {
			childEntries = new ArrayList<FSTEntry>();
		}
	}
	
	public GCNISOHandler(FileHandler fileHandler) throws GCNISOException {
		this.handler = fileHandler;
		
		// GCN ISOs have a DVD magic word of 0xC239F3D at address 0x1C.
		// Check for this, and if it doesn't match, throw an exception.
		byte[] dvdMagic = handler.readBytesAtOffset(0x1C, 4);
		if (!WhyDoesJavaNotHaveThese.byteArraysAreEqual(dvdMagic, new byte[] {(byte)0xC2, 0x33, (byte)0x9F, 0x3D})) {
			throw new GCNISOException("DVD Magic not found. Invalid Gamecube ISO file.");
		}
		
		// Game code is found in the first 4 bytes. Maker Code is the next 2 bytes after that.
		// We consider game code as the combination of both (ex. GFEE01 for FE9 USA).
		gameCode = WhyDoesJavaNotHaveThese.stringFromAsciiBytes(handler.readBytesAtOffset(0, 6));
		
		// Game name is a variable length string starting from 0x20. It can be a max of
		// 0x3E0 characters, for some reason.
		handler.setNextReadOffset(0x20);
		gameName = WhyDoesJavaNotHaveThese.stringFromAsciiBytes(handler.continueReadingBytesUpToNextTerminator(0x3FF));
		
		// fst.bin is the file that gives the metadata for the file system of the ISO.
		// Its offset is located at 0x424 and is 4 bytes long.
		fstOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(handler.readBytesAtOffset(0x424, 4), false);
		// The next 4 bytes are the length of fst.bin.
		fstSize = WhyDoesJavaNotHaveThese.longValueFromByteArray(handler.readBytesAtOffset(0x428, 4), false);
		
		// The first entry in fst.bin is the root.
		// Technically a directory, but different because it holds the number of entries in the FST.
		// Each entry is 0xC in length.
		byte[] rootEntryData = handler.readBytesAtOffset(fstOffset, 0xC);
		byte[] numEntriesData = Arrays.copyOfRange(rootEntryData, 0x8, 0xC);
		fstEntryCount = WhyDoesJavaNotHaveThese.longValueFromByteArray(numEntriesData, false);
		
		// What follows the entries is the strings table to determine filenames.
		fstStringTableOffset = fstEntryCount * 0xC + fstOffset;
		
		rootEntry = new FSTDirectoryEntry();
		rootEntry.type = FSTEntryType.ROOT;
		rootEntry.entryOffset = fstOffset;
		rootEntry.nameOffset = -1;
		rootEntry.parentOffset = -1;
		rootEntry.nextOffset = -1;
		
		FSTDirectoryEntry currentDirectory = rootEntry;
		
		// The first entry was the root. We start from 1 for actual entries.
		for (int i = 1; i < fstEntryCount; i++) {
			long currentOffset = handler.getNextReadOffset();
			// If we've reached the end of the current directory, the next entry should fall into the parent directory.
			// This needs to be a while loop if a directory ends with another directory.
			// We may need to pop out multiple levels.
			while (currentOffset == currentDirectory.nextOffset * 0xC + fstOffset) {
				currentDirectory = currentDirectory.parentEntry;
				if (currentDirectory == null) {
					DebugPrinter.log(DebugPrinter.Key.GCN_HANDLER, "Early exit when parsing FST.");
					break;
				}
			}
			
			// The first byte is the type.
			byte[] flagData = handler.continueReadingBytes(0x1);
			byte flags = flagData[0];
			byte[] filenameData = handler.continueReadingBytes(0x3);
			long filenameOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(filenameData, false);
			
			if (flags == 0x00) { // This is a file entry.
				FSTFileEntry fileEntry = new FSTFileEntry();
				fileEntry.type = FSTEntryType.FILE;
				fileEntry.entryOffset = currentOffset;
				fileEntry.nameOffset = filenameOffset;
				byte[] offsetData = handler.continueReadingBytes(0x4);
				fileEntry.fileOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(offsetData, false);
				byte[] sizeData = handler.continueReadingBytes(0x4);
				fileEntry.fileSize = WhyDoesJavaNotHaveThese.longValueFromByteArray(sizeData, false);
				
				currentDirectory.childEntries.add(fileEntry);
				fileEntry.parentEntry = currentDirectory;
			} else if (flags == 0x01) { // This is a directory entry.
				FSTDirectoryEntry dirEntry = new FSTDirectoryEntry();
				dirEntry.type = FSTEntryType.DIRECTORY;
				dirEntry.entryOffset = currentOffset;
				dirEntry.nameOffset = filenameOffset;
				byte[] parentOffsetIndex = handler.continueReadingBytes(0x4);
				dirEntry.parentOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(parentOffsetIndex, false);
				byte[] nextOffsetIndex = handler.continueReadingBytes(0x4);
				dirEntry.nextOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(nextOffsetIndex, false);
				
				currentDirectory.childEntries.add(dirEntry);
				dirEntry.parentEntry = currentDirectory;
				// Entries directly after a directory entry are files that belong in that directory.
				currentDirectory = dirEntry;
			}
		}
		
		// Build the FST map.
		populateFSTMap();
	}
	
	public FileHandler getBackingHandler() {
		return handler;
	}
	
	public String getGameCode() {
		return gameCode;
	}
	
	public String getGameName() {
		return gameName;
	}
	
	public GCNFileHandler handlerForFileWithName(String filename) throws GCNISOException {
		if (!filename.startsWith("/")) {
			filename = "/" + filename;
		}
		
		FSTEntry entry = fstLookup.get(filename.toLowerCase());
		if (entry == null) {
			throw new GCNISOException("File does not exist: " + filename);
		}
		if (entry.type == FSTEntryType.FILE) {
			return new GCNFileHandler((FSTFileEntry)entry, handler);
		} else {
			throw new GCNISOException("GCNFileHandler does not support handlers for directories.");
		}
	}
	
	private String fstNameFromNameOffset(long nameOffset) {
		if (nameOffset == -1) { return "<root>"; }
		
		long oldReadOffset = handler.getNextReadOffset();
		handler.setNextReadOffset(fstStringTableOffset + nameOffset);
		byte[] nameData = handler.continueReadingBytesUpToNextTerminator(fstOffset + fstSize);
		String name = WhyDoesJavaNotHaveThese.stringFromAsciiBytes(nameData);
		handler.setNextReadOffset(oldReadOffset);
		return name;
	}
	
	private String fstNameOfEntry(FSTEntry entry) {
		if (entry.type == FSTEntryType.ROOT) {
			return "";
		} else {
			if (entry.parentEntry != null) {
				return fstNameOfEntry(entry.parentEntry) + "/" + fstNameFromNameOffset(entry.nameOffset);
			} else {
				assert false : "There shouldn't be any orphan entries...";
				return fstNameFromNameOffset(entry.nameOffset);
			}
		}
	}
	
	private void populateFSTMap() {
		fstLookup = new HashMap<String, FSTEntry>();
		populateFSTMap(rootEntry);
	}
	
	private void populateFSTMap(FSTEntry entry) {
		if (entry.nameOffset != -1) {
			String name = fstNameOfEntry(entry).toLowerCase();
			
			assert fstLookup.get(name) == null : "Duplicate name detected." + name;
			fstLookup.put(name, entry);
		}
		
		if (entry.type == FSTEntryType.DIRECTORY || entry.type == FSTEntryType.ROOT) {
			FSTDirectoryEntry dirEntry = (FSTDirectoryEntry) entry;
			for (FSTEntry child : dirEntry.childEntries) {
				populateFSTMap(child);
			}
		}
	}

	public void debugPrintFST() {
		int level = 0;
		FSTEntry node = rootEntry;
		debugPrintHelper(node, level);
	}
	
	private void debugPrintHelper(FSTEntry node, int level) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++) {
			sb.append("  ");
		}
		sb.append(fstNameFromNameOffset(node.nameOffset));
		DebugPrinter.log(DebugPrinter.Key.GCN_HANDLER, sb.toString());
		
		if (node.type == FSTEntryType.DIRECTORY || node.type == FSTEntryType.ROOT) {
			FSTDirectoryEntry dirEntry = (FSTDirectoryEntry) node;
			for (FSTEntry child : dirEntry.childEntries) {
				debugPrintHelper(child, level + 1);
			}
		}
	}
}
