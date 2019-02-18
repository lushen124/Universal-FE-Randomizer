package fedata.gba.fe7;

import java.util.ArrayList;
import java.util.List;

import fedata.gba.GBAFEWorldMapData;
import fedata.gba.GBAFEWorldMapPortraitData;
import io.FileHandler;
import util.FileReadHelper;

public class FE7WorldMapEvent implements GBAFEWorldMapData {
	
	public static class FE7WorldMapPortrait implements GBAFEWorldMapPortraitData {

		private byte[] originalData;
		private byte[] data;
		
		private long originalOffset;
		
		private Boolean wasModified = false;
		private Boolean hasChanges = false;
		
		public FE7WorldMapPortrait(byte[] data, long offset) {
			originalData = data.clone();
			this.data = data.clone();
			
			originalOffset = offset;
		}
		
		@Override
		public void resetData() {
			wasModified = false;
			data = originalData;
		}

		@Override
		public void commitChanges() {
			if (wasModified) {
				hasChanges = true;
			}
			
			wasModified = false;
		}

		@Override
		public byte[] getData() {
			return data;
		}

		@Override
		public Boolean hasCommittedChanges() {
			return hasChanges;
		}

		@Override
		public Boolean wasModified() {
			return wasModified;
		}

		@Override
		public long getAddressOffset() {
			return originalOffset;
		}

		@Override
		public int getFaceID() {
			return data[8] & 0xFF;
		}

		@Override
		public void setFaceID(int newFaceID) {
			data[8] = (byte)(newFaceID & 0xFF);
			wasModified = true;
		}
	}
	
	private List<FE7WorldMapPortrait> portraitList = new ArrayList<FE7WorldMapPortrait>();
	
	public FE7WorldMapEvent(FileHandler handler, long offset) {
		// We need one jump.
		long pointerTableOffset = FileReadHelper.readAddress(handler, offset);
		
		// We keep reading until we encounter an opcode of 00.
		handler.setNextReadOffset(pointerTableOffset);
		for (;;) {
			byte opcode = handler.continueReadingNextByte();
			// 0x00 terminates the event.
			if (opcode == 0x00) {
				break;
			}
			// This is what we came here for.
			// SHOWPORTRAIT (0xB1) - 20 bytes
			else if (opcode == (byte)0xB1) {
				long address = handler.getNextReadOffset() - 1;
				portraitList.add(new FE7WorldMapPortrait(handler.readBytesAtOffset(address, 20), address));
			}
			// We'll need to do this later...
			// PUTSPRITE (0xB7) - 20 bytes
			else if (opcode == (byte)0xB7) {
				// TODO: Replace map sprites appropriately.
				handler.continueReadingBytes(19);
			}
			// These opcodes are 4 bytes and we don't care about them (right now).
			// ? (0x87), STAL (0x02), ? (0x89), TEXTBOXTOBOTTOM (0xB4), SCRO (0x14), ? (0xCA), MUEN (0x7C), ? (0xAD), ? (0xAE), TEXTBOXTOTOP(0xB5)
			// ? (0xB6)
			else if (opcode == (byte)0x87 || opcode == 0x02 || opcode == (byte)0x89 || opcode == (byte)0xB4 || opcode == 0x14 || opcode == (byte)0xCA || opcode == 0x7C || opcode == (byte)0xAD || opcode == (byte)0xAE || opcode == (byte)0xB5 ||
					opcode == (byte)0xB6) {
				handler.continueReadingBytes(3);
			}
			// This one is a little suspicious since, Event Assembler doesn't think it's a command but data?
			// This command is found in Chapter 19xx. Its context makes me think it's an unknown command that takes two parameters (0x1 and 0x10) and is therefore, 12 bytes long.
			else if (opcode == (byte)0xC4) {
				handler.continueReadingBytes(11);
			}
			// These opcodes are 8 bytes and we don't care about them (right now).
			// ASMWORLDMAP (0x99), TEXTWM (0xB3), RIPPLE (0xC9), ? (0xBE), ?ASM (0x42), REMOVETEXTBOX (0xBC), GOTO_IFNEM (0x50), GOTO (0x45), LABEL (0x44)
			else if (opcode == (byte)0x99 || opcode == (byte)0xB3 || opcode == (byte)0xC9 || opcode == (byte)0xBE || opcode == 0x42 || opcode == (byte)0xBC || opcode == 0x50 || opcode == 0x45 || opcode == 0x44) {
				handler.continueReadingBytes(7);
			}
			// These opcodes are 12 bytes and we don't care about them (right now).
			// ? (0xC2), REMSPRITE (0xB8), ? (0xC3), GOTO_IFET (0x4C)
			else if (opcode == (byte)0xC2 || opcode == (byte)0xB8 || opcode == (byte)0xC3 || opcode == 0x4C) {
				handler.continueReadingBytes(11);
			}
			// These opcodes are 16 bytes and we don't care about them (right now).
			// LOADWM (0xAC), ? (0xC1), REMOVEPORTRAIT (0xB2), HIGHLIGHT (0xB9), FADETOWM (0xC8)
			else if (opcode == (byte)0xAC || opcode == (byte)0xC1 || opcode == (byte)0xB2 || opcode == (byte)0xB9 || opcode == (byte)0xC8) {
				handler.continueReadingBytes(15);
			}
			// These opcodes are 20 bytes and we don't care about them (right now).
			// PLACEDOT (0xBD)
			else if (opcode == (byte)0xBD) {
				handler.continueReadingBytes(19);
			}
			else {
				assert false : "Unhandled world map event opcode 0x" + Integer.toHexString(opcode);
			}
		}
	}

	@Override
	public GBAFEWorldMapPortraitData[] allPortraits() {
		if (portraitList.isEmpty()) { return new GBAFEWorldMapPortraitData[] {}; }
		return portraitList.toArray(new GBAFEWorldMapPortraitData[portraitList.size()]);
	}

}
