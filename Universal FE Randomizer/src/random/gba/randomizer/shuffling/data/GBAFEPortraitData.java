package random.gba.randomizer.shuffling.data;

import fedata.gba.AbstractGBAData;
import util.FileReadHelper;

/**
 * Data class containing the Generic information of a GBA Portrait Data Entry,
 * mainly contains pointers. This one can be used For FE7 & FE8, FE6 has small
 * differences and extends this class with those differences.
 */
public class GBAFEPortraitData extends AbstractGBAData {
	private int faceId; // Portrait Id
	private boolean separateMouthFrames; // mouth frames only are separate for FE7 & 8 not FE6
	private byte[] newPalette;

	public GBAFEPortraitData(byte[] originalData, long offset, int faceId, boolean separateMouthFrames) {
		this.data = originalData;
		this.originalData = originalData;
		this.originalOffset = offset;
		this.faceId = faceId;
		this.separateMouthFrames = separateMouthFrames;
	}

	public int getFaceId() {
		return faceId;
	}

	public boolean useSeparateMouthFrames() {
		return separateMouthFrames;
	}

	public byte[] getFacialFeatureCoordinates() {
		return new byte[] { this.data[0x14], this.data[0x15], this.data[0x16], this.data[0x17] };
	}

	public void setFacialFeatureCoordinates(byte[] coordinates) {
		this.data[0x14] = coordinates[0];
		this.data[0x15] = coordinates[1];
		this.data[0x16] = coordinates[2];
		this.data[0x17] = coordinates[3];
		markModified();
	}

	public byte[] getMainPortraitPointer() {
		return new byte[] { this.data[0x0], this.data[0x0 + 1], this.data[0x0 + 2], this.data[0x0 + 3] };
	}

	public void setMainPortraitPointer(byte[] pointer) {
		this.data[0x0] = pointer[0];
		this.data[0x0 + 1] = pointer[1];
		this.data[0x0 + 2] = pointer[2];
		this.data[0x0 + 3] = pointer[3];
		markModified();
	}

	public byte[] getMiniPortraitPointer() {
		return new byte[] { this.data[0x4], this.data[0x4 + 1], this.data[0x4 + 2], this.data[0x4 + 3] };
	}

	public void setMiniPortraitPointer(byte[] pointer) {
		this.data[0x4] = pointer[0];
		this.data[0x4 + 1] = pointer[1];
		this.data[0x4 + 2] = pointer[2];
		this.data[0x4 + 3] = pointer[3];
		markModified();
	}

	public byte[] getMouthFramesPointer() {
		return new byte[] { this.data[0xC], this.data[0xC + 1], this.data[0xC + 2], this.data[0xC + 3] };
	}

	public void setMouthFramesPointer(byte[] pointer) {
		this.data[0xC] = pointer[0];
		this.data[0xC + 1] = pointer[1];
		this.data[0xC + 2] = pointer[2];
		this.data[0xC + 3] = pointer[3];
		markModified();
	}

	
	public long getPalettePointerAsLong() {
		return FileReadHelper.wordValue(
				new byte[] { this.data[0x8], this.data[0x8 + 1], this.data[0x8 + 2], this.data[0x8 + 3] }, true);
	}
	
	public byte[] getPalettePointer() {
		return new byte[] { this.data[0x8], this.data[0x8 + 1], this.data[0x8 + 2], this.data[0x8 + 3] };
	}
	
	public void setPalettePointer(byte[] pointer) {
		this.data[0x8] = pointer[0];
		this.data[0x8 + 1] = pointer[1];
		this.data[0x8 + 2] = pointer[2];
		this.data[0x8 + 3] = pointer[3];
	}

	
	public byte[] getNewPalette() {
		return newPalette;
	}

	public void setNewPalette(byte[] newPalette) {
		this.newPalette = newPalette;
		markModified();
	}

}
