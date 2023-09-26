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
		super(data, originalOffset);
	}
	
	public int getPostMoveX() {
		return data[0] & 0x3F;
	}
	
	
	public void setPostMoveX(int newX) {
		// Discard anything but the last byte
		newX = newX & 0xFF;
		
		// Get the Y portion of the current byte
		int startingY = getPostMoveY();
		
		int ySubvalue = startingY & 0x00F;
		int xSubvalue = newX;
		
		int positionData = (ySubvalue << 6) | xSubvalue; 
		data[0] = (byte) positionData;
		wasModified = true;
	}
	
	public int getPostMoveY() {
		int part1 = (data[1] & 0x0F) ;
		int part2 = ((data[0] & 0xC0) >> 6);
		return (( part1 | part2) ) & 0xFFF;
	}

	public void setPostMoveY(int newY) {
		// Save the Special Data, we don't currently edit this
		int specialData = getPostMoveY() & 0xF0;

		// Change Byte 2
		// take bits 3-6
		int ySubvalueByte2 = (newY & 0x3C) >> 2;
		// prefix the Y Value with the special data
		int positionDataByte2 = ((specialData << 4) | ySubvalueByte2) & 0xFF;
		data[1] = (byte) positionDataByte2;

		// Change Byte 1
		int xSubvalueByte1 = getPostMoveX() & 0x3F;
		int ySubvalueByte1 = newY & 0x03;
		int positionDataByte1 = ((ySubvalueByte1 << 6) | xSubvalueByte1) & 0xFF;
		data[0] = (byte) (positionDataByte1 & 0xFF);
		wasModified = true;
	}
	
}
