package fedata.gba.fe8;

import java.util.ArrayList;
import java.util.List;

import fedata.gba.GBAFEWorldMapData;
import fedata.gba.GBAFEWorldMapPortraitData;
import fedata.gba.GBAFEWorldMapSpriteData;
import io.FileHandler;
import util.FileReadHelper;
import util.WhyDoesJavaNotHaveThese;

public class FE8WorldMapEvent implements GBAFEWorldMapData {
	
	private enum SpriteType {
		PUTSPRITE, WM_PUTSPRITE, WM_PUTMOVINGSPRITE
	}
	
	public static class FE8WorldMapSprite implements GBAFEWorldMapSpriteData {

		private byte[] originalData;
		private byte[] data;
		
		private long originalOffset;
		
		private Boolean wasModified = false;
		private Boolean hasChanges = false;
		
		private SpriteType type;
		
		public FE8WorldMapSprite(byte[] data, long offset, SpriteType type) {
			originalData = data.clone();
			this.data = data.clone();
			
			originalOffset = offset;
			this.type = type;
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
		public int getClassID() {
			switch (type) {
			case PUTSPRITE:
				return data[6] & 0xFF;
			case WM_PUTMOVINGSPRITE:
			case WM_PUTSPRITE:
				return data[4] & 0xFF;
			default:
				assert false : "This doesn't exist.";
			return 0;
			}
			
		}

		@Override
		public void setClassID(int newClassID) {
			switch (type) {
			case PUTSPRITE:
				data[6] = (byte)(newClassID & 0xFF);
				wasModified = true;
				break;
			case WM_PUTMOVINGSPRITE:
			case WM_PUTSPRITE:
				data[4] = (byte)(newClassID & 0xFF);
				wasModified = true;
				break;
			}
		}
		
	}

	public static class FE8WorldMapPortrait implements GBAFEWorldMapPortraitData {
		
		private byte[] originalData;
		private byte[] data;
		
		private long originalOffset;
		
		private Boolean wasModified = false;
		private Boolean hasChanges = false;
		
		public FE8WorldMapPortrait(byte[] data, long offset) {
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
			return data[6] & 0xFF;
		}

		@Override
		public void setFaceID(int newFaceID) {
			data[6] = (byte)(newFaceID & 0xFF);
			wasModified = true;
		}
	}
	
	private List<FE8WorldMapPortrait> portraitList = new ArrayList<FE8WorldMapPortrait>();
	private List<FE8WorldMapSprite> spriteList = new ArrayList<FE8WorldMapSprite>();
	
	public FE8WorldMapEvent(FileHandler handler, long offset) {
		// We need one jump.
		long pointerTableOffset = FileReadHelper.readAddress(handler, offset);
		
		// FE8 uses two halves of events. Each ends with an ENDA, so we keep reading until our second ENDA.
		handler.setNextReadOffset(pointerTableOffset);
		
		boolean terminateOnNextEnd = false;
		
		for(;;) {
			// Unlike FE6 and FE7, FE8's opcodes are 2 bytes. They have a minimum of 4 bytes, so we'll just read all 4 for the instruction.
			// Remember that we're in Little Endian. So an opcode of 0x1020 will show up as 20 10.
			byte[] opcode = handler.continueReadingBytes(4);
			// These are the two valid terminators that we can encounter.
			// ENDA (0x0120), ENDB(0x0121)
			if (WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x20, (byte)0x01}) || WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x21, (byte)0x01})) {
				if (terminateOnNextEnd) { break; }
				terminateOnNextEnd = true;
			}
			// This is what we came here for.
			// WM_SHOWPORTRAIT (0xC460) - 12 bytes
			else if (WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x60, (byte)0xC4})) {
				long address = handler.getNextReadOffset() - 4;
				portraitList.add(new FE8WorldMapPortrait(handler.readBytesAtOffset(address, 12), address));
			}
			// Will probably have to tackle this later.
			// PUTSPRITE (0x9E60) - 12 bytes
			else if (WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x60, (byte)0x9E})) {
				long address = handler.getNextReadOffset() - 4;
				spriteList.add(new FE8WorldMapSprite(handler.readBytesAtOffset(address, 12), address, SpriteType.PUTSPRITE));
			}
			// May be able to roll this into a universal sprite handler.
			// I don't think these load in new sprites.
			// WM_PUTMOVINGSPRITE (0xA8C0) - 24 bytes
			else if (WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0xC0, (byte)0xA8})) {
				//long address = handler.getNextReadOffset() - 4;
				//spriteList.add(new FE8WorldMapSprite(handler.readBytesAtOffset(address, 24), address, SpriteType.WM_PUTMOVINGSPRITE));
				handler.continueReadingBytes(20);
			}
			// There's a third type of sprite. o_o
			// I don't think these load in new sprites though, they just modify existing sprites.
			// WM_PUTSPRITE (0xA760) - 12 bytes
			else if (WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x60, (byte)0xA7})) {
				//long address = handler.getNextReadOffset() - 4;
				//spriteList.add(new FE8WorldMapSprite(handler.readBytesAtOffset(address, 12), address, SpriteType.WM_PUTSPRITE));
				handler.continueReadingBytes(8);
			}
			
			// I have a sneaking suspicion that the number of bytes an instruction takes is encoded into that first half of the first byte.
			// 0x20 (or 0x22) are all 4 bytes, 0x40 are all 8 bytes, 0x60 are all 12 bytes, and 0x80 are all 16 bytes.
			// In that case, we don't need to hard code all of these events...
			
			// These opcodes are 4 bytes and we don't care about them (right now).
			// EVBIT_MODIFY (0x1020), ? (0xC220), MUSCFAST (0x1322), STAL (0x0E20), MUSC (0x1220), WM_TEXTDECORATE (0xB320), WM_WAITFORTEXT (0xB120), WM_TEXTSTART (0xC720), TEXTEND (0x1D20), TEXTCONT (0x1C20), etc.
			else if (WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x20, (byte)0x10}) ||
					WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x20, (byte)0xC2}) ||
					WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x22, (byte)0x13}) ||
					WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x20, (byte)0x0E}) ||
					WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x20, (byte)0x12}) ||
					WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x20, (byte)0xB3}) ||
					WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x20, (byte)0xB1}) ||
					WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x20, (byte)0xC7}) ||
					WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x20, (byte)0x1D}) ||
					WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x20, (byte)0x1C}) ||
					
					(byte)(opcode[0] & 0xF0) == 0x20) {
				// We've already read all 4 bytes, so we're good.
			}
			// These opcodes are 8 bytes and we don't care about them (right now).
			// WM_CENTERCAMONLORD (0x8540), WM_FADEOUT (0xB240), WM_SHOWTEXTWINDOW (0xAF40), WM_TEXT (0xC640), WM_HIGHLIGHT (0xB840), WM_HIGHLIGHTCLEAR1 (0xB940), WM_HIGHLIGHTCLEAR2 (0xBA40),
			// WM_CLEARPORTRAIT (0xC540), etc.
			else if (WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x40, (byte)0x85}) ||
					WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x40, (byte)0xB2}) ||
					WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x40, (byte)0xAF}) ||
					WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x40, (byte)0xC6}) ||
					WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x40, (byte)0xB8}) ||
					WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x40, (byte)0xB9}) ||
					WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x40, (byte)0xBA}) ||
					WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x40, (byte)0xC5}) ||
					
					(byte)(opcode[0] & 0xF0) == 0x40) {
				handler.continueReadingBytes(4);
			}
			// These opcodes are 12 bytes and we don't care about them (right now).
			// WM_SPAWNLORD (0xC360), WM_SHOWDRAWNMAP (0xB460), PLACEDOT (0xBC60), etc.
			else if (WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x60, (byte)0xC3}) ||
					WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x60, (byte)0xB4}) ||
					WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x60, (byte)0xBC}) ||
					
					(byte)(opcode[0] & 0xF0) == 0x60) {
				handler.continueReadingBytes(8);
			}
			// These opcodes are 16 bytes and we don't care about them (right now).
			// WM_MOVECAM2 (0xB680), etc.
			else if (WhyDoesJavaNotHaveThese.byteArrayHasPrefix(opcode, new byte[] {(byte)0x80, (byte)0xB6}) ||
					
					(byte)(opcode[0] & 0xF0) == (byte)0x80) {
				handler.continueReadingBytes(12);
			}
			else {
				assert false : "Unhandled world map event opcode 0x" + WhyDoesJavaNotHaveThese.displayStringForBytes(opcode);
			}
			
		}
	}

	@Override
	public GBAFEWorldMapPortraitData[] allPortraits() {
		if (portraitList.isEmpty()) { return new GBAFEWorldMapPortraitData[] {}; }
		return portraitList.toArray(new GBAFEWorldMapPortraitData[portraitList.size()]);
	}
	
	public GBAFEWorldMapSpriteData[] allSprites() {
		if (spriteList.isEmpty()) { return new GBAFEWorldMapSpriteData[] {}; }
		return spriteList.toArray(new GBAFEWorldMapSpriteData[spriteList.size()]);
	}
}
