package io.gcn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fedata.gcnwii.fe9.FE9ScriptScene;
import io.FileHandler;
import util.ByteArrayBuilder;
import util.DebugPrinter;
import util.WhyDoesJavaNotHaveThese;

public class GCNCMBFileHandler extends GCNFileHandler {
	
	private Map<Long, String> stringsByAddress; // These are the exact locations in the file. When accessing, remember to offset by start of strings table.
	private Map<String, Long> addressesByString;

	private String name;
	
	private long stringTableOffset;
	private long scriptTableOffset;
	
	private byte[] fullData;
	
	private ByteArrayBuilder newStringData;
	
	private List<FE9ScriptScene> scenes;
	
	// Anatomy of a CMB file.
	// 0x0 ~ 0x2C - Header
	// 		0x0 ~ 0x4 - magic word 'cmb'
	//		variable bytes - name (null terminated)
	//		~ 0x24 - unknown
	//		0x24 ~ 0x28 - String Table Offset
	//		0x28 ~ 0x2C bytes - Script Table Offset
	// 0x2C ~ Script Table offset - String table.
	// Rest of the file is the scripts.
	
	public GCNCMBFileHandler(GCNFSTFileEntry entry, FileHandler handler, GCNISOHandler isoHandler) throws GCNISOException {
		super(entry, handler);
		
		stringsByAddress = new HashMap<Long, String>();
		addressesByString = new HashMap<String, Long>();
		
		// Verify Header. This is the first 0x2C bytes.
		byte[] header = readBytesAtOffset(0, 0x4);
		if (WhyDoesJavaNotHaveThese.byteArrayHasPrefix(header, new byte[] {(byte)'c', (byte)'m', (byte)'b', 0})) {
			byte[] nameBytes = continueReadingBytesUpToNextTerminator(0x2C);
			name = WhyDoesJavaNotHaveThese.stringFromAsciiBytes(nameBytes);
			
			setNextReadOffset(0x24);
			byte[] stringTableOffsetBytes = continueReadingBytes(4);
			stringTableOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(stringTableOffsetBytes, true);
			byte[] scriptTableOffsetBytes = continueReadingBytes(4);
			scriptTableOffset = WhyDoesJavaNotHaveThese.longValueFromByteArray(scriptTableOffsetBytes, true);
			
			ByteArrayBuilder fullData = new ByteArrayBuilder();
			setNextReadOffset(0);
			while (getNextReadOffset() < getFileLength()) {
				fullData.appendBytes(continueReadingBytes(1024));
			}
			this.fullData = fullData.toByteArray();
			
			newStringData = new ByteArrayBuilder();
			
			setNextReadOffset(stringTableOffset);
			while (getNextReadOffset() < scriptTableOffset) {
				long currentOffset = getNextReadOffset();
				byte[] stringBytes = continueReadingBytesUpToNextTerminator(scriptTableOffset);
				String stringValue = WhyDoesJavaNotHaveThese.stringFromShiftJIS(stringBytes);
				stringsByAddress.put(currentOffset, stringValue);
				addressesByString.put(stringValue, currentOffset);
			}
			
			scenes = new ArrayList<FE9ScriptScene>();
			
			int scriptTableOffset = (int)getScriptTableOffset();
			int currentSceneIndex = 0;
			
			byte[] nextHeaderOffset = cmb_readBytesAtOffset(scriptTableOffset, 4);
			int next = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(nextHeaderOffset, true);
			
			while (next != 0) {
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "Loading script " + currentSceneIndex);
				FE9ScriptScene currentScene = new FE9ScriptScene(this, scriptTableOffset + (currentSceneIndex * 4));
				scenes.add(currentScene);
				
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tPointer Offset: 0x" + Integer.toHexString(currentScene.getPointerOffset()).toUpperCase());
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tHeader Offset: 0x" + Integer.toHexString(currentScene.getSceneHeaderOffset()).toUpperCase());
				
				if (currentScene.getIdentifierOffset() == 0) {
					DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tIdentifier Offset: 0x" + Integer.toHexString(currentScene.getIdentifierOffset()).toUpperCase());
				} else {
					DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tIdentifier offset: 0x" + Integer.toHexString(currentScene.getIdentifierOffset()).toUpperCase() + " (" + currentScene.getIdentifierName() + ")");
				}
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tScript Offset: 0x" + Integer.toHexString(currentScene.getScriptOffset()).toUpperCase());
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tParent Offset: 0x" + Integer.toHexString(currentScene.getParentOffset()).toUpperCase());
				
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tScene Kind: 0x" + Integer.toHexString(currentScene.getSceneKind()).toUpperCase());
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tNumber of Arguments: " + currentScene.getNumberOfArgs());
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tNumber of Parameters: " + currentScene.getParameterCount());
				
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tScene Index: 0x" + Integer.toHexString(currentScene.getSceneIndex()).toUpperCase());
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tVariable Count: " + currentScene.getVarCount());
				
				for (int i = 0; i < currentScene.getParams().length; i++) {
					DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\t\tParameter: 0x" + Integer.toHexString(currentScene.getParams()[i]).toUpperCase());
				}
				
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tRaw script: " + WhyDoesJavaNotHaveThese.displayStringForBytes(currentScene.getScriptBytes()));
			
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tDisassembled:\n" + String.join("", currentScene.getInstructions().stream().map( instruction -> {
					return instruction.displayString() + "\n";
				}).collect(Collectors.toList())));
				
				currentSceneIndex++;
				nextHeaderOffset = cmb_readBytesAtOffset(scriptTableOffset + (currentSceneIndex * 4), 4);
				next = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(nextHeaderOffset, true);
			}
			
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "Finished loading scripts for " + getName());
			
		} else {
			throw new GCNISOException("Invalid CMB file header.");
		}
	}
	
	public String getName() {
		return name;
	}
	
	public List<FE9ScriptScene> getScenes() {
		return scenes;
	}
	
	public void addString(String string) {
		if (string == null) { return; }
		if (addressesByString.keySet().contains(string)) {
			return;
		}
		
		long targetAddress = scriptTableOffset + newStringData.getBytesWritten();
		
		byte[] stringData = WhyDoesJavaNotHaveThese.asciiBytesFromString(string);
		newStringData.appendBytes(stringData);
		newStringData.appendByte((byte)0);
		
		addressesByString.put(string, targetAddress);
		stringsByAddress.put(targetAddress, string);
		
		return;
	}
	
	public byte[] newBuild() {
		boolean hasChanged = scenes.stream().anyMatch(scene -> { return scene.hasChanges(); });
		if (!hasChanged) { return fullData; }
		
		ByteArrayBuilder builder = new ByteArrayBuilder();
		
		// Write the first 0x28 bytes. These will not have changed.
		builder.appendBytes(WhyDoesJavaNotHaveThese.subArray(fullData, 0, 0x28));
		// The script table offset might have changed though.
		// Pad the new string data to byte align.
		while (newStringData.getBytesWritten() % 4 != 0) { newStringData.appendByte((byte)0); }
		
		// Update script table's offset in the header.
		long newScriptTableOffset = scriptTableOffset + newStringData.getBytesWritten();
		builder.appendBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(newScriptTableOffset, true, 4));
		
		// What immediately follows those offsets is the string table.
		// Write the original string data first.
		byte[] originalStringData = WhyDoesJavaNotHaveThese.subArray(fullData, (int)stringTableOffset, (int)(scriptTableOffset - stringTableOffset));
		builder.appendBytes(originalStringData);
		// Write the added string data.
		builder.appendBytes(newStringData.toByteArray());
		
		// The script table uses absolute pointers to point to each scene header, and the scene headers use absolute pointers to point to
		// the name (if applicable), the script bytes, and the parent (if applicable).
		
		// The pointers to the scene headers will be offset by the strings added, as well as any expanded scripts preceding it.
		int addedOffset = newStringData.getBytesWritten();
		for (FE9ScriptScene scene : scenes) {
			int headerOffset = scene.getSceneHeaderOffset();
			headerOffset += addedOffset;
			builder.appendBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(headerOffset, true, 4));
			
			// Update offsets we will write later as well.
			scene.setSceneHeaderOffset(headerOffset);
			if (scene.getIdentifierOffset() != 0) { scene.setIdentifierOffset(scene.getIdentifierOffset() + addedOffset); }
			if (scene.getParentOffset() != 0) { scene.setParentOffset(scene.getParentOffset() + addedOffset); }
			if (scene.getScriptOffset() != 0) { scene.setScriptOffset(scene.getScriptOffset() + addedOffset); }
			
			int originalScriptLength = scene.getOriginalScriptBytes().length;
			int newScriptLength = scene.getScriptBytes().length;
			addedOffset += (newScriptLength - originalScriptLength);
		}
		
		// The pointers are terminated with a 0 address.
		builder.appendBytes(new byte[] {0, 0, 0, 0});
		
		// Scripts are stored header+identifier, then script bytes.
		// These will be more complex, because the script length can change, and that can throw off future scripts.
		for (FE9ScriptScene scene: scenes) {
			while(builder.getBytesWritten() != scene.getSceneHeaderOffset()) {
				builder.appendByte((byte)0);
			}
			
			builder.appendBytes(scene.buildHeader());
			
			while (builder.getBytesWritten() != scene.getScriptOffset()) {
				builder.appendByte((byte)0);
			}
			
			builder.appendBytes(scene.buildScriptBytes());
		}
		
		fullData = builder.toByteArray();
		scenes.forEach(scene -> {scene.commit();});
		newStringData.clear();
		scriptTableOffset = newScriptTableOffset;
		
		return fullData;
	}
	
	public byte[] referenceToString(String string, int numBytes) {
		if (string == null) {
			return null;
		}
		
		Long addressForString = addressesByString.get(string);
		if (addressForString != null) {
			return WhyDoesJavaNotHaveThese.byteArrayFromLongValue(addressForString - stringTableOffset, false, numBytes);
		}
		
		return null;
	}
	
	public String stringForOffset(byte[] offsetBytes) {
		long offsetValue = WhyDoesJavaNotHaveThese.longValueFromByteArray(offsetBytes, false) + stringTableOffset;
		return stringsByAddress.get(offsetValue);
	}
	
	public List<Long> offsetsForString(String string) {
		if (string == null) {
			return null;
		}
		
		Long addressForString = addressesByString.get(string);
		if (addressForString != null) {
			return offsetsForBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(addressForString - stringTableOffset, false, 2));
		}
		
		return null;
	}
	
	public byte[] cmb_readBytesAtOffset(long offset, int length) {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		for (int i = 0; i < length; i++) {
			builder.appendByte(fullData[(int)(offset + i)]);
		}
		return builder.toByteArray();
	}
	
	public byte[] cmb_readBytesUpToNextTerminator(int offset) {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		int index = offset;
		byte currentByte = fullData[index++];
		while (currentByte != 0) {
			builder.appendByte(currentByte);
			currentByte = fullData[index++];
		}
		
		builder.appendByte((byte)0);
		return builder.toByteArray();
	}
	
	public void cmb_writeBytesToOffset(long offset, byte[] bytesToWrite) {
		for (int i = 0; i < bytesToWrite.length; i++) {
			fullData[(int)(offset + i)] = bytesToWrite[i];
		}
	}
	
	public List<Long> offsetsForBytes(byte[] bytesToSearch) {
		List<Long> offsets = new ArrayList<Long>();
		long currentOffset = scriptTableOffset;
		while (currentOffset < fullData.length) {
			Long nextOffset = advanceToNextInstance(bytesToSearch, currentOffset);
			if (nextOffset != null) {
				offsets.add(nextOffset);
			} else {
				break;
			}
			currentOffset = nextOffset + 1;
		}
		return offsets;
	}
	
	public long getScriptTableOffset() {
		return scriptTableOffset;
	}
	
	private Long advanceToNextInstance(byte[] bytesToSearch, long startingOffset) {
		byte currentByte = 0;
		long currentOffset = startingOffset;
		while (currentOffset < fullData.length) {
			currentByte = fullData[(int)currentOffset];
			int index = 0;
			while (index < bytesToSearch.length && bytesToSearch[index] == currentByte) {
				if (++index >= bytesToSearch.length) {
					return currentOffset;
				}
				if (currentOffset + index >= fullData.length) { break; }
				currentByte = fullData[(int)(currentOffset + index)];
			}
			currentOffset++;
		}
		
		return null;
	}
	
	@Override
	public long getFileLength() {
		if (fullData == null) { return super.getFileLength(); }
		return fullData.length;
	}
}
