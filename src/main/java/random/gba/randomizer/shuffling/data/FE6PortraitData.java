package random.gba.randomizer.shuffling.data;


/**
 * Extension of the Generic GBA Portrait data for FE6 Specific differences
 */
public class FE6PortraitData extends GBAFEPortraitData {

	public FE6PortraitData(byte[] originalData, long offset, int faceId, boolean separateMouthFrames) {
		super(originalData, offset, faceId, false);
	}

	@Override
	public byte[] getFacialFeatureCoordinates() {
		return new byte[] {this.data[0xC], this.data[0xD]};
	}

	@Override
	public void setFacialFeatureCoordinates(byte[] coordinates) {
		this.data[0xC] = coordinates[0];
		this.data[0xD] = coordinates[1];
	}

	@Override
	public void setMouthFramesPointer(byte[] pointer) {
		throw new UnsupportedOperationException("FE6 doesn't use separate mouth frames");
	}
	
	@Override
	public byte[] getMouthFramesPointer() {
		throw new UnsupportedOperationException("FE6 doesn't use separate mouth frames");
	}

}
