package io.gcn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.FileHandler;
import io.FileWriter;
import util.DebugPrinter;
import util.Diff;
import util.DiffCompiler;
import util.WhyDoesJavaNotHaveThese;

enum GCNFSTEntryType {
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
	
	private long bootDolOffset;
	
	private GCNFSTDirectoryEntry rootEntry;
	private Map<String, GCNFSTEntry> fstLookup;
	private Map<String, GCNFileHandler> cachedFileHandlers;
	private List<GCNFSTEntry> entryList; // This will make recompilation easier.
	
	private List<Diff> executableDiffs;
	
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
		
		bootDolOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(handler.readBytesAtOffset(0x420, 4), false);
		
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
		
		rootEntry = new GCNFSTDirectoryEntry();
		rootEntry.type = GCNFSTEntryType.ROOT;
		rootEntry.entryOffset = fstOffset;
		rootEntry.nameOffset = -1;
		rootEntry.parentOffset = -1;
		rootEntry.nextOffset = -1;
		
		entryList = new ArrayList<GCNFSTEntry>();
		entryList.add(rootEntry);
		
		GCNFSTDirectoryEntry currentDirectory = rootEntry;
		
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
				GCNFSTFileEntry fileEntry = new GCNFSTFileEntry();
				fileEntry.type = GCNFSTEntryType.FILE;
				fileEntry.entryOffset = currentOffset;
				fileEntry.nameOffset = filenameOffset;
				byte[] offsetData = handler.continueReadingBytes(0x4);
				fileEntry.fileOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(offsetData, false);
				byte[] sizeData = handler.continueReadingBytes(0x4);
				fileEntry.fileSize = WhyDoesJavaNotHaveThese.longValueFromByteArray(sizeData, false);
				
				// Just to be safe, set the updated offsets to be the same.
				// This is to make sure we make a distinction between where we read from and where we write to,
				// as those two can be different, due to changes in file sizes.
				fileEntry.updatedOffset = fileEntry.fileOffset;
				fileEntry.updatedSize = fileEntry.fileSize;
				
				currentDirectory.childEntries.add(fileEntry);
				fileEntry.parentEntry = currentDirectory;
				
				DebugPrinter.log(DebugPrinter.Key.GCN_HANDLER, "Loaded File " + fstNameOfEntry(fileEntry) + " at FST offset 0x" + Long.toHexString(currentOffset).toUpperCase()+ ".");
				DebugPrinter.log(DebugPrinter.Key.GCN_HANDLER, "File Offset: 0x" + Long.toHexString(fileEntry.fileOffset).toUpperCase() + ". File Size: " + fileEntry.fileSize + " bytes.");
				
				entryList.add(fileEntry);
			} else if (flags == 0x01) { // This is a directory entry.
				GCNFSTDirectoryEntry dirEntry = new GCNFSTDirectoryEntry();
				dirEntry.type = GCNFSTEntryType.DIRECTORY;
				dirEntry.entryOffset = currentOffset;
				dirEntry.nameOffset = filenameOffset;
				byte[] parentOffsetIndex = handler.continueReadingBytes(0x4);
				dirEntry.parentOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(parentOffsetIndex, false);
				byte[] nextOffsetIndex = handler.continueReadingBytes(0x4);
				dirEntry.nextOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(nextOffsetIndex, false);
				
				currentDirectory.childEntries.add(dirEntry);
				dirEntry.parentEntry = currentDirectory;
				
				DebugPrinter.log(DebugPrinter.Key.GCN_HANDLER, "Loaded Directory " + fstNameOfEntry(dirEntry) + " at FST offset 0x" + Long.toHexString(currentOffset).toUpperCase()+ ".");
				DebugPrinter.log(DebugPrinter.Key.GCN_HANDLER, "Parent: " + fstNameOfEntry(currentDirectory) + " at offset: 0x" + Long.toHexString(currentDirectory.entryOffset).toUpperCase() + ". Next entry offset: 0x" + Long.toHexString(dirEntry.nextOffset).toUpperCase() + ".");
				
				// Entries directly after a directory entry are files that belong in that directory.
				currentDirectory = dirEntry;
				
				entryList.add(dirEntry);
			}
		}
		
		for (int i = 0; i < entryList.size(); i++) {
			DebugPrinter.log(DebugPrinter.Key.GCN_HANDLER, "[" + i + "] Entry: " + fstNameOfEntry(entryList.get(i)));
		}
		
		// Build the FST map.
		populateFSTMap();
		
		executableDiffs = new ArrayList<Diff>();
	}
	
	public void addExecutableDiff(Diff diff) {
		if (diff.address <= (fstOffset - bootDolOffset) && diff.address + diff.length <= (fstOffset - bootDolOffset)) {
			executableDiffs.add(diff);
			executableDiffs.sort(new Comparator<Diff>() {
				@Override
				public int compare(Diff o1, Diff o2) {
					return Long.compare(o1.address, o2.address);
				}
			});
		} else {
			System.err.println("Invalid executable diff added! Offset: 0x" + Long.toHexString(diff.address));
		}
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
	
	public List<GCNFSTEntry> entriesWithFilename(String filename) throws GCNISOException {
		List<GCNFSTEntry> result = new ArrayList<GCNFSTEntry>();
		
		for (String key : fstLookup.keySet()) {
			if (key.contains(filename.toLowerCase())) {
				result.add(fstLookup.get(key));
			}
		}
		
		return result;
	}
	
	public GCNFileHandler handlerForFSTEntry(GCNFSTEntry entry) throws GCNISOException {
		if (entry == null) { return null; }
		String filename = fstNameOfEntry(entry);
		return handlerForFileWithName(filename);
	}
	
	public GCNFileHandler handlerForFileWithName(String filename) throws GCNISOException {
		if (!filename.startsWith("/")) {
			filename = "/" + filename;
		}
		
		if (cachedFileHandlers == null) { cachedFileHandlers = new HashMap<String, GCNFileHandler>(); }
		GCNFileHandler fileHandler = cachedFileHandlers.get(filename);
		if (fileHandler != null) { return fileHandler; }
		
		GCNFSTEntry entry = fstLookup.get(filename.toLowerCase());
		if (entry == null) {
			// CMP files assume a current directory of where the CMP file lies.
			// They may request a file without the absolute path.
			// If there's only one of those files, then return that one.
			// Note: These files aren't necessary equivalent, so we can't trust this for now.
//			final String name = filename.toLowerCase();
//			if (fstLookup.keySet().stream().filter(key -> { return key.endsWith(name); }).count() == 1) {
//				return handlerForFileWithName(fstLookup.keySet().stream().filter(key -> { return key.endsWith(name); }).findAny().get());
//			}
			
			// We may not have loaded CMP files yet, so see if it's in a CMP file, and if it is, try loading it first and then trying again.
			if (filename.contains(".cmp") && !filename.endsWith(".cmp")) {
				// Remove path components until we get to the .cmp file.
				String cmpFilename = filename;
				while (!cmpFilename.endsWith(".cmp")) {
					cmpFilename = cmpFilename.substring(0, cmpFilename.lastIndexOf("/"));
				}
				entry = fstLookup.get(cmpFilename.toLowerCase());
			
				if (entry != null) {
					GCNCMPFileHandler cmpHandler = new GCNCMPFileHandler((GCNFSTFileEntry)entry, handler, this);
					// Register all contained files as well.
					for (String name : cmpHandler.getNames()) {
						GCNFileHandler handler = cmpHandler.getChildHandler(name);
						if (cachedFileHandlers.containsKey(cmpFilename + "/" + name)) {
							// We've already loaded this. If we still don't have it, it doesn't exist.
							throw new GCNISOException("File does not exist: " + filename);
						}
						cachedFileHandlers.put(cmpFilename + "/" + name, handler);
					}
					
					cachedFileHandlers.put(cmpFilename, cmpHandler);
					
					return handlerForFileWithName(filename);
				} else {
					throw new GCNISOException("No CMP file found for: " + filename);
				}
			} else {
				throw new GCNISOException("File does not exist: " + filename);
			}
		}
		if (entry.type == GCNFSTEntryType.FILE) {
			if (filename.endsWith(".cmp")) {
				GCNCMPFileHandler cmpHandler = new GCNCMPFileHandler((GCNFSTFileEntry)entry, handler, this);
				// Register all contained files as well.
				for (String name : cmpHandler.getNames()) {
					GCNFileHandler handler = cmpHandler.getChildHandler(name);
					cachedFileHandlers.put(filename + "/" + name, handler);
				}
				
				fileHandler = cmpHandler;
			} else if (filename.endsWith(".cmb")) {
				GCNCMBFileHandler cmbHandler = new GCNCMBFileHandler((GCNFSTFileEntry)entry, handler, this);
				fileHandler = cmbHandler;
			} else if (filename.endsWith(".m")) {
				GCNMessageFileHandler messageHandler = new GCNMessageFileHandler((GCNFSTFileEntry)entry, handler, this);
				fileHandler = messageHandler;
			} else {
				fileHandler = new GCNFileHandler((GCNFSTFileEntry)entry, handler);
			}
		} else {
			throw new GCNISOException("GCNFileHandler does not support handlers for directories.");
		}
		
		cachedFileHandlers.put(filename, fileHandler);
		return fileHandler;
	}
	
	public void recompile(String destination, GCNISOHandlerRecompilationDelegate delegate) {
		// Rebuild the FST first.
		if (delegate != null) { delegate.onProgressUpdate(0.1); delegate.onStatusUpdate("Recompiling ISO..."); }
		
//		int indexOfPathSeparator = destination.lastIndexOf(File.separator);
//		String path = destination.substring(0, indexOfPathSeparator);
//		try {
//			FileWriter testWriter = new FileWriter(path + File.separator + "test.bin");
//			testWriter.write(((GCNCMPFileHandler)handlerForFileWithName("/zdbx.cmp")).buildRaw());
//			testWriter.finish();
//			
//			testWriter = new FileWriter(path + File.separator + "testSlowCompressed.bin");
//			testWriter.write(((GCNCMPFileHandler)handlerForFileWithName("/zdbx.cmp")).build());
//			testWriter.finish();
//			
//		} catch (IOException | GCNISOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		// Without necessarily allocating all of the space for the data in memory, we just need to figure out the
		// offsets in the ISO where they should end up. Since we're not adding any files, the FST is mostly ok. The
		// only change we need to make is to file entries because they specify file_length, which we may change
		// and file_offset, which will change as a result of the former.
		List<GCNFSTFileEntry> fileDataOrder = new ArrayList<GCNFSTFileEntry>();
		
		// We'll use -1 as the data offset because the position of the first file we come across should
		// not have changed, so we'll anchor it to that. Following files can have their offsets changed.
		if (delegate != null) { delegate.onProgressUpdate(0.2); delegate.onStatusUpdate("Recalculating File Offsets..."); }
		try {
			recalculateFileOffsets(rootEntry, -1, fileDataOrder, delegate);
		} catch (GCNISOException e1) {
			assert false : "Failed to recalculate File Offsets.";
			if (delegate != null) { delegate.onError("Encountered error while recalculating file offsets.\n\n" + e1.getMessage()); }
			return;
		}
		
		// Start writing. Our header shouldn't have changed because our FST hasn't moved.
		// So we can copy everything prior to the FST directly.
		try {
			if (delegate != null) { delegate.onProgressUpdate(0.3); delegate.onStatusUpdate("Writing Header..."); }
			FileWriter writer = new FileWriter(destination);
			InputStream stream = handler.getInputStream(0);
			// Write up to the boot.dol offset. We'll apply changes to boot.dol after that.
			writer.copyFromStream(stream, (int)bootDolOffset);
			long lastWrittenOffset = 0; // Offset relative to start of boot.dol, not the ISO.
			// TODO: Test this logic with more than one diff.
			for (Diff diff : executableDiffs) {
				writer.copyFromStream(stream, (int)(diff.address - lastWrittenOffset));
				lastWrittenOffset += (diff.address - lastWrittenOffset);
				if (diff.requiredOldValues != null) {
					byte[] oldValues = stream.readNBytes(diff.requiredOldValues.length);
					if (WhyDoesJavaNotHaveThese.byteArraysAreEqual(oldValues, diff.requiredOldValues)) {
						writer.write(diff.changes);
					} else {
						System.err.println("Mismatched diff required old values! Dropping Diff at offset 0x" + Long.toHexString(diff.address));
						writer.write(oldValues);
					}
					lastWrittenOffset += oldValues.length;
				} else {
					writer.write(diff.changes);
					lastWrittenOffset += diff.changes.length;
					stream.skipNBytes(diff.changes.length);
				}
			}
			if (lastWrittenOffset + bootDolOffset < fstOffset) {
				writer.copyFromStream(stream, (int)(fstOffset - bootDolOffset - lastWrittenOffset));
			}
			
			// Sanity check.
			if (writer.getBytesWritten() != fstOffset) {
				System.err.println("Failed to write the correct number of bytes before the fst. Expected: " + fstOffset + " Actual: " + writer.getBytesWritten());
			}
			
			stream.close();
			
			// Write the entries.
			for (int i = 0; i < entryList.size(); i++) {
				if (delegate != null) { 
					delegate.onProgressUpdate(0.3 + 0.2 * (double)i / (double)entryList.size()); 
					delegate.onStatusUpdate("Writing Entries (" + fstNameOfEntry(entryList.get(i)) + ")...");
				}
				writeEntry(entryList.get(i), writer);
			}
			
			if (delegate != null) { delegate.onProgressUpdate(0.6); delegate.onStatusUpdate("Writing FST Strings..."); }
			// Copy the strings table for the filenames. This goes up until the file offset of the first file.
			stream = handler.getInputStream(fstStringTableOffset);
			writer.copyFromStream(stream, (int)(fileDataOrder.get(0).fileOffset - fstStringTableOffset));
			stream.close();
			
			byte[] dataChunk = new byte[1024];
			
			// Write all of the file data.
			for (int i = 0; i < fileDataOrder.size(); i++) {
				GCNFSTFileEntry fileEntry = fileDataOrder.get(i);
				if (delegate != null) { 
					delegate.onProgressUpdate(0.6 + 0.4 * (double)i / (double)fileDataOrder.size()); 
					delegate.onStatusUpdate("Writing File Data (" + fstNameOfEntry(fileEntry) + ")...");
				}
				while (writer.getBytesWritten() < fileEntry.updatedOffset) { writer.write((byte)0); }
				
				long beginningOffset = writer.getBytesWritten();
				String name = fstNameOfEntry(fileEntry);
				DebugPrinter.log(DebugPrinter.Key.GCN_HANDLER, "Writing " + name + " data at offset 0x" + Long.toHexString(beginningOffset));
				
				GCNFileHandler fileHandler = handlerForFileWithName(fstNameOfEntry(fileEntry));
				if (fileHandler instanceof GCNCMPFileHandler) {
					GCNCMPFileHandler cmpFileHandler = (GCNCMPFileHandler)fileHandler;
					writer.write(cmpFileHandler.build());
				} else if (fileHandler instanceof GCNCMBFileHandler) {
					GCNCMBFileHandler cmbFileHandler = (GCNCMBFileHandler)fileHandler;
					writer.write(cmbFileHandler.newBuild());
				} else if (fileHandler instanceof GCNMessageFileHandler) {
					GCNMessageFileHandler messageFileHandler = (GCNMessageFileHandler)fileHandler;
					writer.write(messageFileHandler.orderedBuild());
				} else {
					fileHandler.setNextReadOffset(0);
					fileHandler.beginBatchRead();
					int bytesRead = fileHandler.continueReadingBytes(dataChunk);
					do {
						writer.write(dataChunk, bytesRead);
						bytesRead = fileHandler.continueReadingBytes(dataChunk);
					} while (bytesRead > 0);
					
					fileHandler.endBatchRead();
				}
				long bytesWritten = writer.getBytesWritten() - beginningOffset;
				DebugPrinter.log(DebugPrinter.Key.GCN_HANDLER, "Wrote " + bytesWritten + " btyes");
			}
			
			writer.finish();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (delegate != null) { delegate.onError("Unable to open file.\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (delegate != null) { delegate.onError("Error writing file.\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); }
			e.printStackTrace();
		} catch (GCNISOException e) {
			// TODO Auto-generated catch block
			if (delegate != null) { delegate.onError("Error processing ISO file.\n\nStack Trace:\n\n" + String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(element -> (element.toString())).limit(5).collect(Collectors.toList()))); }
			e.printStackTrace();
		}
	}
	
	// This writes using the updated offsets, so they should be calculated before writing.
	private void writeEntry(GCNFSTEntry entry, FileWriter writer) throws IOException {
		byte[] entryData = new byte[0xC];
		long filenameOffset = entry.nameOffset;
		if (entry.type != GCNFSTEntryType.ROOT) {
			entryData[1] = (byte)((filenameOffset >> 16) & 0xFF);
			entryData[2] = (byte)((filenameOffset >> 8) & 0xFF);
			entryData[3] = (byte)((filenameOffset) & 0xFF);
		}
		
		if (entry.type == GCNFSTEntryType.FILE) {
			GCNFSTFileEntry file = (GCNFSTFileEntry)entry;
			entryData[0] = 0;
			entryData[4] = (byte)((file.updatedOffset >> 24) & 0xFF);
			entryData[5] = (byte)((file.updatedOffset >> 16) & 0xFF);
			entryData[6] = (byte)((file.updatedOffset >> 8) & 0xFF);
			entryData[7] = (byte)((file.updatedOffset) & 0xFF);
			entryData[8] = (byte)((file.updatedSize >> 24) & 0xFF);
			entryData[9] = (byte)((file.updatedSize >> 16) & 0xFF);
			entryData[10] = (byte)((file.updatedSize >> 8) & 0xFF);
			entryData[11] = (byte)((file.updatedSize) & 0xFF);
		} else {
			GCNFSTDirectoryEntry directory = (GCNFSTDirectoryEntry)entry;
			entryData[0] = 1;
			if (directory.type == GCNFSTEntryType.ROOT) {
				entryData[8] = (byte)((fstEntryCount >> 24) & 0xFF);
				entryData[9] = (byte)((fstEntryCount >> 16) & 0xFF);
				entryData[10] = (byte)((fstEntryCount >> 8) & 0xFF);
				entryData[11] = (byte)((fstEntryCount) & 0xFF);
			} else {
				entryData[4] = (byte)((directory.parentOffset >> 24) & 0xFF);
				entryData[5] = (byte)((directory.parentOffset >> 16) & 0xFF);
				entryData[6] = (byte)((directory.parentOffset >> 8) & 0xFF);
				entryData[7] = (byte)((directory.parentOffset) & 0xFF);
				entryData[8] = (byte)((directory.nextOffset >> 24) & 0xFF);
				entryData[9] = (byte)((directory.nextOffset >> 16) & 0xFF);
				entryData[10] = (byte)((directory.nextOffset >> 8) & 0xFF);
				entryData[11] = (byte)((directory.nextOffset) & 0xFF);
			}
		}
		
		writer.write(entryData);
	}
	
	private long recalculateFileOffsets(GCNFSTEntry entry, long currentDataOffset, List<GCNFSTFileEntry> fileDataOrder, GCNISOHandlerRecompilationDelegate delegate) throws GCNISOException {
		if (entry.type == GCNFSTEntryType.ROOT || entry.type == GCNFSTEntryType.DIRECTORY) {
			GCNFSTDirectoryEntry directory = (GCNFSTDirectoryEntry)entry;
			for (int i = 0; i < directory.childEntries.size(); i++) {
				// pass along the current data offset for any files coming later.
				currentDataOffset = recalculateFileOffsets(directory.childEntries.get(i), currentDataOffset, fileDataOrder, delegate);
			}
		} else {
			GCNFSTFileEntry file = (GCNFSTFileEntry)entry;
			String name = fstNameOfEntry(file);
			if (delegate != null) { delegate.onStatusUpdate("Recalculating File Offsets... (" + name + ")"); }
			GCNFileHandler handler = handlerForFileWithName(name);
			if (handler instanceof GCNCMPFileHandler) {
				((GCNCMPFileHandler)handler).build();
			}
			if (handler instanceof GCNCMBFileHandler) {
				((GCNCMBFileHandler) handler).newBuild();
			}
			if (handler instanceof GCNMessageFileHandler) {
				((GCNMessageFileHandler) handler).orderedBuild();
			}
			fileDataOrder.add(file);
			if (currentDataOffset == -1) { currentDataOffset = file.fileOffset; } // Initialize the offset to the first file's offset.
			else { file.updatedOffset = currentDataOffset; }
			file.updatedSize = handler.getFileLength();
			
			DebugPrinter.log(DebugPrinter.Key.GCN_HANDLER, "Entry " + fstNameOfEntry(entry) + " at offset 0x" + Long.toHexString(currentDataOffset) + " length: " + handler.getFileLength());
			
			currentDataOffset += handler.getFileLength();
			
			// Files are byte aligned (i.e. they must begin on a 0, 4, 8, or C offset.)
			int additionalPaddingNeeded = 4 - (int)(currentDataOffset % 4);
			if (additionalPaddingNeeded % 4 != 0) { currentDataOffset += additionalPaddingNeeded; }
		}
		
		return currentDataOffset;
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
	
	public String fstNameOfEntry(GCNFSTEntry entry) {
		if (entry.type == GCNFSTEntryType.ROOT) {
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
		fstLookup = new HashMap<String, GCNFSTEntry>();
		populateFSTMap(rootEntry);
	}
	
	private void populateFSTMap(GCNFSTEntry entry) {
		if (entry.nameOffset != -1) {
			String name = fstNameOfEntry(entry).toLowerCase();
			
			assert fstLookup.get(name) == null : "Duplicate name detected." + name;
			fstLookup.put(name, entry);
		}
		
		if (entry.type == GCNFSTEntryType.DIRECTORY || entry.type == GCNFSTEntryType.ROOT) {
			GCNFSTDirectoryEntry dirEntry = (GCNFSTDirectoryEntry) entry;
			for (GCNFSTEntry child : dirEntry.childEntries) {
				populateFSTMap(child);
			}
		}
	}

	public void debugPrintFST() {
		int level = 0;
		GCNFSTEntry node = rootEntry;
		debugPrintHelper(node, level);
	}
	
	private void debugPrintHelper(GCNFSTEntry node, int level) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++) {
			sb.append("  ");
		}
		sb.append(fstNameFromNameOffset(node.nameOffset));
		DebugPrinter.log(DebugPrinter.Key.GCN_HANDLER, sb.toString());
		
		if (node.type == GCNFSTEntryType.DIRECTORY || node.type == GCNFSTEntryType.ROOT) {
			GCNFSTDirectoryEntry dirEntry = (GCNFSTDirectoryEntry) node;
			for (GCNFSTEntry child : dirEntry.childEntries) {
				debugPrintHelper(child, level + 1);
			}
		}
	}
}
