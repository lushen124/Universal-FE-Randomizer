package io.gcn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.FileHandler;
import io.FileWriter;
import util.ByteArrayBuilder;
import util.LZ77;
import util.WhyDoesJavaNotHaveThese;

public class GCNCMPFileHandler extends GCNFileHandler {
	
	private class CMPFileEntry {
		public long namePointer;
		public long filePointer;
		public long fileLength;
		
		public CMPFileEntry(byte[] data) {
			namePointer = WhyDoesJavaNotHaveThese.longValueFromByteArray(Arrays.copyOfRange(data, 4, 8), false);
			filePointer = WhyDoesJavaNotHaveThese.longValueFromByteArray(Arrays.copyOfRange(data, 8, 12), false);
			fileLength = WhyDoesJavaNotHaveThese.longValueFromByteArray(Arrays.copyOfRange(data, 12, 16), false);
		}
		
		public byte[] toByteArray() {
			return new byte[] {
					0, 0, 0, 0,
					(byte)((namePointer >> 24) & 0xFF), (byte)((namePointer >> 16) & 0xFF), (byte)((namePointer >> 8) & 0xFF), (byte)(namePointer & 0xFF),
					(byte)((filePointer >> 24) & 0xFF), (byte)((filePointer >> 16) & 0xFF), (byte)((filePointer >> 8) & 0xFF), (byte)(filePointer & 0xFF),
					(byte)((fileLength >> 24) & 0xFF), (byte)((fileLength >> 16) & 0xFF), (byte)((fileLength >> 8) & 0xFF), (byte)(fileLength & 0xFF)
			};
		}
	}
	
	private List<CMPFileEntry> filesPackaged;
	private List<String> filenames;
	private Map<String, CMPFileEntry> fileMap;
	private Map<String, GCNFileHandler> cachedHandlers;
	
	private byte[] cachedBuild;

	public GCNCMPFileHandler(GCNFSTFileEntry entry, FileHandler handler, GCNISOHandler isoHandler) throws GCNISOException {
		super(entry, handler);
		
		byte[] decompressed = LZ77.decompress(handler.readBytesAtOffset(entry.fileOffset, (int)entry.fileSize));
		assert WhyDoesJavaNotHaveThese.stringFromAsciiBytes(Arrays.copyOfRange(decompressed, 0, 4)) == "pack" : "Invalid file format for GCNCMPFileHandler.";
		
		filesPackaged = new ArrayList<CMPFileEntry>();
		filenames = new ArrayList<String>();
		fileMap = new HashMap<String, CMPFileEntry>();
		cachedHandlers = new HashMap<String, GCNFileHandler>();
		
		int numberOfFiles = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(Arrays.copyOfRange(decompressed, 4, 6), false);
		int offset = 0x8;
		for (int i = 0; i < numberOfFiles; i++) {
			byte[] entryData = Arrays.copyOfRange(decompressed, offset, offset + 0x10);
			CMPFileEntry cmpFileEntry = new CMPFileEntry(entryData);
			filesPackaged.add(cmpFileEntry);
			
			int nameStart = (int)cmpFileEntry.namePointer;
			int nameEnd = nameStart;
			byte currentValue = 0;
			do {
				currentValue = decompressed[++nameEnd];
			} while (currentValue != 0);
			
			String name = WhyDoesJavaNotHaveThese.stringFromAsciiBytes(Arrays.copyOfRange(decompressed, nameStart, nameEnd));
			fileMap.put(name, cmpFileEntry);
			filenames.add(name);
			GCNFileHandler gcnFileHandler;
			try {
				gcnFileHandler = isoHandler.handlerForFileWithName(name);
			} catch (GCNISOException e) {
				gcnFileHandler = new GCNByteArrayHandler(entry, handler, Arrays.copyOfRange(decompressed, (int)cmpFileEntry.filePointer, (int)cmpFileEntry.filePointer + (int)cmpFileEntry.fileLength));
			}
			cachedHandlers.put(name, gcnFileHandler);
			offset += entryData.length;
		}
	}
	
	public List<String> getNames() {
		return new ArrayList<String>(filenames);
	}
	
	public GCNFileHandler getChildHandler(String name) {
		return cachedHandlers.get(name);
	}
	
	public byte[] buildRaw() {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		// Build the header. The first four bytes are "pack" in ascii,
		// followed by a short for the number of files.
		builder.appendBytes(new byte[] { 'p', 'a', 'c', 'k',
				(byte)((filesPackaged.size() >> 8) & 0xFF), (byte)(filesPackaged.size() & 0xFF), 0, 0});
		
		// Update all of the file entries first before we write them.
		long offset = -1;
		for (String name : filenames) {
			CMPFileEntry fileEntry = fileMap.get(name);
			GCNFileHandler fileHandler = cachedHandlers.get(name);
			fileEntry.fileLength = fileHandler.getFileLength();
			if (offset != -1) {
				fileEntry.filePointer = offset;
			}
			offset = fileEntry.filePointer + fileEntry.fileLength;
			
			// It looks like every file starts aligned to 0. Not sure if this is true
			// for all CMP files, but it's true for system.cmp. Moreover, 
			// all files are aligned on a multiple of 0x20. Again, not sure if this is
			// necessarily true, but since it's true for system.cmp, we'll preserve
			// that behavior.
			if (offset % 0x20 != 0) { offset += (0x20 - (offset % 0x20)); }
			
			// Might as well write them while we're iterating.
			builder.appendBytes(fileEntry.toByteArray());
		}
		
		// The strings table follows immediately afterwards. We're going to take a few shortcuts.
		// Namely, that the order of the entries is the same order they appear
		// in the strings table as well as the actual data. This doesn't seem to be a requirement
		// but like above, it's true for system.cmp.
		for (String name : filenames) {
			CMPFileEntry fileEntry = fileMap.get(name);
			while (builder.getBytesWritten() < fileEntry.namePointer) { builder.appendByte((byte)0); }
			builder.appendBytes(WhyDoesJavaNotHaveThese.asciiBytesFromString(name));
			builder.appendByte((byte)0);
		}
		
		byte[] fileData = new byte[1024];
		
		// Data is also assumed to be written in order of files.
		for (String name : filenames) {
			CMPFileEntry fileEntry = fileMap.get(name);
			GCNFileHandler fileHandler = cachedHandlers.get(name);
			while (builder.getBytesWritten() < fileEntry.filePointer) { builder.appendByte((byte)0); }
			fileHandler.setNextReadOffset(0);
			fileHandler.beginBatchRead();
			int bytesRead = fileHandler.continueReadingBytes(fileData);
			do {
				builder.appendBytes(fileData, bytesRead);
				bytesRead = fileHandler.continueReadingBytes(fileData);
			} while (bytesRead > 0);
			
			fileHandler.endBatchRead();
		}
		
		return builder.toByteArray();
	}
	
	public byte[] build() {
		if (cachedBuild == null) { cachedBuild = LZ77.compress(buildRaw(), 0xFFF); }
		return cachedBuild;
	}

	@Override
	public long getFileLength() {
		if (cachedBuild != null) { return cachedBuild.length; }
		else { return super.getFileLength(); }
	}
	
	
}
