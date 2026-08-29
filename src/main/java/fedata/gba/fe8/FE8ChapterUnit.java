package fedata.gba.fe8;

import java.util.ArrayList;
import java.util.List;

import fedata.gba.GBAFEChapterUnitData;

public class FE8ChapterUnit extends GBAFEChapterUnitData {
	
	List<FE8ChapterUnitMoveData> movements = new ArrayList<>();
	
	public FE8ChapterUnit(byte[] data, long originalOffset) {
		super();
		this.originalData = data;
		this.data = data;
		this.originalOffset = originalOffset;
	}
	
	@Override
	public int getItem1() {
		return data[12] & 0xFF;
	}
	
	@Override
	public void setItem1(int itemID) {
		data[12] = (byte) (itemID & 0xFF);
		wasModified = true;
	}

	@Override
	public int getItem2() {
		return data[13] & 0xFF;
	}

	@Override
	public void setItem2(int itemID) {
		data[13] = (byte) (itemID & 0xFF);
		wasModified = true;
	}

	@Override
	public int getItem3() {
		return data[14] & 0xFF;
	}

	@Override
	public void setItem3(int itemID) {
		data[14] = (byte) (itemID & 0xFF);
		wasModified = true;
	}

	@Override
	public int getItem4() {
		return data[15] & 0xFF;
	}

	@Override
	public void setItem4(int itemID) {
		data[15] = (byte) (itemID & 0xFF);
		wasModified = true;
	}

	@Override
	public int getLeaderID() {
		return 0; // FE8 doesn't use Leader IDs
	}

	// Presumably the Reinforcement pointer defines any movement necessary, not that
	// we care that much.

	// FE8 does things a bit differently. Each position is only 6 bits instead of 8
	// bits. We need to read bytes 4, 5 to get the data.
	@Override
	public int getStartingX() {
		// Little endian means the least significant bits are in the latter byte.
		// In terms of bit layout: 5555 5555 4444 4444
		// The only bits that matter are the latter 12 bits: 5555 4444 4444
		// Now we just split it down the middle, 6 bits each: 555544 444444
		// X is the second piece, Y is the first piece. yyyyyy xxxxxx
		int positionData = (((data[5] & 0xFF) << 8) | (data[4] & 0xFF)) & 0xFFF;
		return positionData & 0x3F;
	}

	@Override
	public int getStartingY() {
		int positionData = (((data[5] & 0xFF) << 8) | (data[4] & 0xFF)) & 0xFFF;
		return (positionData >> 6) & 0x3F;
	}

	@Override
	public void setStartingX(int newX) {
		int startingY = getStartingY();
		int positionData = (startingY << 6) | (newX & 0x3F);
		data[5] = (byte) ((positionData & 0xF00) >> 8);
		data[4] = (byte) (positionData & 0xFF);
		wasModified = true;
	}

	@Override
	public void setStartingY(int newY) {
		int startingX = getStartingX();
		int positionData = (newY << 6) | (startingX & 0x3F);
		data[5] = (byte) ((positionData & 0xF00) >> 8);
		data[4] = (byte) (positionData & 0xFF);
		wasModified = true;
	}

	public int getNumberMovements() {
		return data[7] & 0xFF;
	}
	
	@Override
	public int getPostMoveX() {
		if (this.movements.isEmpty()) {
			return 0;
		}

		return getPostMoveXForMove(0);
	}

	@Override
	public int getPostMoveY() {
		if (this.movements.isEmpty()) {
			return 0;
		}
		return getPostMoveYForMove(0);
	}
	
	public int getPostMoveXForMove(int move) {
		return movements.get(move).getPostMoveX();
	}
	
	public int getPostMoveYForMove(int move) {
		return movements.get(move).getPostMoveY();
	}
	
	public void setPostMoveX(int newX) {
		setPostMoveXForMove(0, newX);
	}
	public void setPostMoveXForMove(int move, int newX) {
		this.movements.get(0).setPostMoveX(newX);
		wasModified = true;
	}
	
	public void setPostMoveY(int newY) {
		setPostMoveXForMove(0, newY);
	}
	
	public void setPostMoveYForMove(int move, int newY) {
		this.movements.get(0).setPostMoveY(newY);
	}
	
	public List<FE8ChapterUnitMoveData> getMovements() {
		return this.movements;
	}
	
	public boolean hasMovementToPosition(int x, int y) {
		return this.movements.stream().anyMatch(mv -> mv.getPostMoveX() == x && mv.getPostMoveY() == y);
	}
	
	public void addMovement(FE8ChapterUnitMoveData moveData) {
		this.movements.add(moveData);
	}
	
	
	@Override
	protected void collapseItems() {
		List<Integer> items = new ArrayList<Integer>();

		if (getItem1() != 0) {
			if (FE8Data.Item.valueOf(getItem1()).isWeapon()) {
				items.add(0, getItem1());
			} else {
				items.add(getItem1());
			}
		}
		if (getItem2() != 0) {
			if (FE8Data.Item.valueOf(getItem2()).isWeapon()) {
				items.add(0, getItem2());
			} else {
				items.add(getItem2());
			}
		}
		if (getItem3() != 0) {
			if (FE8Data.Item.valueOf(getItem3()).isWeapon()) {
				items.add(0, getItem3());
			} else {
				items.add(getItem3());
			}
		}
		if (getItem4() != 0) {
			if (FE8Data.Item.valueOf(getItem4()).isWeapon()) {
				items.add(0, getItem4());
			} else {
				items.add(getItem4());
			}
		}

		setItem1(items.size() > 0 ? items.get(0) : 0);
		setItem2(items.size() > 1 ? items.get(1) : 0);
		setItem3(items.size() > 2 ? items.get(2) : 0);
		setItem4(items.size() > 3 ? items.get(3) : 0);
	}
	

	@Override
	public void setAIToHeal() {
		data[17] = (byte) 0x0E;
		wasModified = true;
	}

	@Override
	public void removeHealingAI() {
		data[17] = (byte) 0x00;
		wasModified = true;
	}

	@Override
	public void setUnitToDropLastItem(boolean drop) {
		if (drop) {
			data[19] |= 0x40;
		} else {
			data[19] &= ~0x40;
		}
	}

	public boolean isAITargetingVillages() {
		return data[20] == 0x04 || data[20] == 0x05;
	}
}
