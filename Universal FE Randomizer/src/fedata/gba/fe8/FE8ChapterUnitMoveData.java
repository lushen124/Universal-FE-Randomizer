package fedata.gba.fe8;

import fedata.gba.AbstractGBAData;
import util.WhyDoesJavaNotHaveThese;

public class FE8ChapterUnitMoveData extends AbstractGBAData {
	// 8 bytes each
	// Byte 1: Bits [YYXXXXXX]
	// Byte 2: Bits [SSSSYYYY] SSSS == Special (f.e. Item Drop)
	// Byte 3: Heavy / Normal Steps (1 / 0) 
	// Byte 4: Character Id who they are following 
	// Byte 5: ???? Just says FF in FEBuilder
	// Byte 6: ???? Just says FF in FEBuilder
	// Byte 7: Wait times (max 60sec)
	// Byte 8: ????
	
	
	
	public FE8ChapterUnitMoveData(long originalOffset, byte[] data) {
		this.data = data;
		this.originalData = data;
		this.originalOffset = originalOffset;
	}
	
	public FE8ChapterUnitMoveData(int postMoveX, int postMoveY, boolean isHeavySteps, int followingID) {
		originalOffset = -1;
		data = new byte[] {0, 0, isHeavySteps ? (byte)0x1 : (byte)0x0, (byte)(followingID & 0xFF), (byte)0xFF, (byte)0xFF, 0, 0};
		setPostMoveX(postMoveX);
		setPostMoveY(postMoveY);
		originalData = data;
	}
	
	public void markAsNeedingRepointing() {
		originalOffset = -1;
	}
	
	public int getPostMoveX() {
		return data[0] & 0x3F;
	}
	
	
	public void setPostMoveX(int newX) {
		// This holds at most 6 bits, so mask it to that.
		assert newX <= 0x3F: "Post move X is out of bounds (maximum value is " + 0x3f + ", but received " + newX + ")";
		if (newX > 0x3F) { return; }
		data[0] = (byte)((byte)(newX & 0x3F) | (byte)(data[0] & 0xC0));
		wasModified = true;
	}
	
	public int getPostMoveY() {
		int upper4Bits = ((data[1] & 0x0F) << 2) ;
		int lower2Bits = ((data[0] & 0xC0) >> 6);
		return (( upper4Bits | lower2Bits) ) & 0xFFF;
	}

	public void setPostMoveY(int newY) {
		// This holds at most 6 bits.
		assert newY <= 0x3F: "Post move Y is out of bounds (maximum value is " + 0x3f + ", but received " + newY + ")";
		if (newY > 0x3F) { return; }
		// The upper 4 bits are part of byte 1, while the bottom 2 bits are part of byte 0.
		byte upper4Bits = (byte)((newY & 0x3C) >> 2);
		byte lower2Bits = (byte)((newY & 0x3) << 6);
		data[0] = (byte)((data[0] & 0x3F) | lower2Bits);
		data[1] = (byte)((data[1] & 0xF0) | upper4Bits);
		wasModified = true;
	}
	
}
