package fedata.gba.fe6;

import java.util.ArrayList;
import java.util.List;

import fedata.gba.GBAFEWorldMapData;
import fedata.gba.GBAFEWorldMapPortraitData;
import fedata.gba.GBAFEWorldMapSpriteData;
import io.FileHandler;
import util.FileReadHelper;

public class FE6WorldMapEvent implements GBAFEWorldMapData  {
	
	public static class FE6WorldMapPortrait implements GBAFEWorldMapPortraitData  {
		
		private byte[] originalData;
		private byte[] data;
		
		private long originalOffset;
		
		private Boolean wasModified = false;
		private Boolean hasChanges = false;
		
		public FE6WorldMapPortrait(byte[] data, long offset) {
			originalData = data.clone();
			this.data = data.clone();
			
			originalOffset = offset;
		}

		@Override
		public int getFaceID() {
			return data[12] & 0xFF;
		}

		@Override
		public void setFaceID(int newFaceID) {
			data[12] = (byte)(newFaceID & 0xFF);
			wasModified = true;
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
	}
	
	private List<FE6WorldMapPortrait> portraitList = new ArrayList<FE6WorldMapPortrait>();
	
	public FE6WorldMapEvent(FileHandler handler, long offset) {
		// We need one jump.
		long pointerTableOffset = FileReadHelper.readAddress(handler, offset);
		
		// All events end with ASMC 0x9345D, so we should keep going until we see that.
		handler.setNextReadOffset(pointerTableOffset);
		for(;;) {
			// Read until we find the terminator.
			byte opcode = handler.continueReadingNextByte();
			// Add commands as necessary.
			// ASMC (0x17) - 8 bytes - This could be our terminator if it jumps to the right place.
			if (opcode == 0x17) {
				handler.continueReadingBytes(3);
				// Address is on bytes 4 - 7.
				long address = FileReadHelper.readAddress(handler);
				if (address == 0x9345DL) { break; }
			}
			// SHOWPORTRAIT (0x51) - 16 bytes - This is what we came here for.
			else if (opcode == 0x51) {
				long instructionOffset = handler.getNextReadOffset() - 1;
				portraitList.add(new FE6WorldMapPortrait(handler.readBytesAtOffset(instructionOffset, 16), instructionOffset));
			}
			// These opcodes are 8 bytes and we don't care about them (right now).
			// STAL (0x2), ASMWORLDMAP (0x4B), ? (0x4D), TEXTWM (0x56), HIGHLIGHT (0x5B), REMOVE3 (0x63), REMOVE1 (0x52), SHOWARROW (0x5A), ZOOMTO (0x4F), REMOVE2 (0x5F), MUEN (0x39)
			// GOTO (0x1C), LABEL (0x1B), REMOVE4 (0x61)
			else if (opcode == 0x2 || opcode == 0x4B || opcode == 0x4D || opcode == 0x56 || opcode == 0x5B || opcode == 0x63 || opcode == 0x52 || opcode == 0x5A || opcode == 0x4F || opcode == 0x5F || opcode == 0x39 ||
					opcode == 0x1C || opcode == 0x1B || opcode == 0x61) {
				handler.continueReadingBytes(7);
			}
			// These opcodes are 4 bytes and we don't care about them (right now).
			// ? (0x41), ? (0x42), TEXTBOXTOBOTTOM (0x57), REMOVETEXTBOX (0x5D), ? (0x59), TEXTBOXTOTOP (0x58), ? (0xA), ? (0x4E), ZOOMOUT (0x50)
			else if (opcode == 0x41 || opcode == 0x42 || opcode == 0x57 || opcode == 0x5D || opcode == 0x59 || opcode == 0x58 || opcode == (byte)0xA || opcode == 0x4E || opcode == 0x50) {
				handler.continueReadingBytes(3);
			}
			// These opcodes are 16 bytes and we don't care about them (right now).
			// PLACEDOT (0x5E), PLACEFLAG (0x60)
			else if (opcode == 0x5E || opcode == 0x60) {
				handler.continueReadingBytes(15);
			}
			// These opcodes are 20 bytes and we don't care about them (right now).
			// SHOWMAPTEXT (0x62)
			else if (opcode == 0x62) {
				handler.continueReadingBytes(19);
			}
			// These opcodes are 12 bytes and we don't care about them (right now).
			// GOTO_IFEF (0x23)
			else if (opcode == 0x23) {
				handler.continueReadingBytes(11);
			}
			// These need to be handled if they get this far.
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
	
	public GBAFEWorldMapSpriteData[] allSprites() {
		return new GBAFEWorldMapSpriteData[] {}; // This isn't supported in FE6.
	}

}
