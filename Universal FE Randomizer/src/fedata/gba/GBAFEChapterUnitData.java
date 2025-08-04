package fedata.gba;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import random.gba.loader.ItemDataLoader;

public abstract class GBAFEChapterUnitData extends AbstractGBAData {

	public int getCharacterNumber() {
		return data[0] & 0xFF;
	}

	public int getStartingClass() {
		return data[1] & 0xFF;
	}

	public void setStartingClass(int classID) {
		data[1] = (byte) (classID & 0xFF);
		wasModified = true;
	}

	public int getLeaderID() {
		return data[2] & 0xFF;
	}

	// Level and alliance are stored in byte 3, but we need to interpret it
	// properly.
	// LLLL LENA
	// L = Level (5 bits, 0 - 31)
	// E = Set if enemy
	// N = Set if NPC
	// A = Set if autolevel
	// Note: playable characters are defined as not enemies and not NPCs.
	public int getStartingLevel() {
		int value = data[3] & 0xFF;
		return value >> 3;
	}

	public void setStartingLevel(int newLevel) {
		int levelShifted = (newLevel << 3) & 0xF8;
		data[3] = (byte) ((byte) levelShifted | (data[3] & 0x7));
		wasModified = true;
	}

	public boolean isEnemy() {
		int value = data[3] & 0xFF;
		return (value & 0x4) != 0;
	}

	public boolean isNPC() {
		int value = data[3] & 0xFF;
		return (value & 0x2) != 0;
	}

	public boolean isAutolevel() {
		int value = data[3] & 0xFF;
		return (value & 0x1) != 0;
	}
	
	public void setAutolevel(boolean enabled) {
		if (enabled) {
			data[3] |= 0x1;
		} else {
			data[3] &= ~0x1;
		}
		wasModified = true;
	}
	
	public int getStartingX() {
		return data[4] & 0xFF;
	}

	public int getStartingY() {
		return data[5] & 0xFF;
	}
	
	public void setStartingX(int newX) {
		data[4] = (byte)(newX & 0xFF);
		wasModified = true;
	}
	
	public void setStartingY(int newY) {
		data[5] = (byte)(newY & 0xFF);
		wasModified = true;
	}
	
	public int getPostMoveX() {
		return data[6] & 0xFF;
	}
	
	public int getPostMoveY() {
		return data[7] & 0xFF;
	}
	
	public void setPostMoveX(int newX) {
		data[6] = (byte)(newX & 0xFF);
		wasModified = true;
	}
	
	public void setPostMoveY(int newY) {
		data[7] = (byte)(newY & 0xFF);
		wasModified = true;
	}


	public int getItem1() {
		return data[8] & 0xFF;
	}

	public void setItem1(int itemID) {
		data[8] = (byte)(itemID & 0xFF);
		wasModified = true;
	}

	public int getItem2() {
		return data[9] & 0xFF;
	}

	public void setItem2(int itemID) {
		data[9] = (byte)(itemID & 0xFF);
		wasModified = true;
	}

	public int getItem3() {
		return data[10] & 0xFF;
	}

	public void setItem3(int itemID) {
		data[10] = (byte)(itemID & 0xFF);
		wasModified = true;
	}

	public int getItem4() {
		return data[11] & 0xFF;
	}

	public void setItem4(int itemID) {
		data[11] = (byte)(itemID & 0xFF);
		wasModified = true;
	}


	public void giveItems(int[] itemIDs, ItemDataLoader itemData) {
		ArrayList<Integer> workingIDs = new ArrayList<Integer>();
		for (int i = 0; i < itemIDs.length; i++) {
			if (getItem1() == itemIDs[i] || getItem2() == itemIDs[i] || getItem3() == itemIDs[i]
					|| getItem4() == itemIDs[i]) {
				continue;
			}
			workingIDs.add(itemIDs[i]);
		}
		
		List<Integer> availableIndices = new ArrayList<Integer>();
		Set<GBAFEItemData> itemsToRetain = new HashSet<GBAFEItemData>(Arrays.asList(itemData.specialItemsToRetain()));
		
		if (itemsToRetain.stream().anyMatch((item) -> item.getID() == getItem4()) == false) {
			availableIndices.add(4);
		}
		if (itemsToRetain.stream().anyMatch((item) -> item.getID() == getItem3()) == false) {
			availableIndices.add(3);
		}
		if (itemsToRetain.stream().anyMatch((item) -> item.getID() == getItem2()) == false) {
			availableIndices.add(2);
		}
		if (itemsToRetain.stream().anyMatch((item) -> item.getID() == getItem1()) == false) {
			availableIndices.add(1);
		}

		while (workingIDs.isEmpty() == false && availableIndices.isEmpty() == false) {
			setItemSlot(availableIndices.remove(0), workingIDs.remove(0));
		}

		collapseItems();
	}
	
	public Integer getHighestRankItemID(ItemDataLoader itemData) {
		List<Integer> currentIDs = new ArrayList<Integer>();
		currentIDs.add(getItem1());
		currentIDs.add(getItem2());
		currentIDs.add(getItem3());
		currentIDs.add(getItem4());
		
		GBAFEItemData highestRank = currentIDs.stream().map(itemID -> itemData.itemWithID(itemID)).filter(item -> item != null).max(new Comparator<GBAFEItemData>() {
			@Override
			public int compare(GBAFEItemData o1, GBAFEItemData o2) {
				return o1.getWeaponRank().compare(o2.getWeaponRank());
			}
		}).orElse(null);
		
		if (highestRank == null) { return null; }
		
		return highestRank.getID();
	}
	
	public boolean replaceItemIDWithItemID(int itemIDToReplace, int replacementID) {
		if (getItem1() == itemIDToReplace) {
			setItem1(replacementID);
			return true;
		}
		if (getItem2() == itemIDToReplace) {
			setItem2(replacementID);
			return true;
		}
		if (getItem3() == itemIDToReplace) {
			setItem3(replacementID);
			return true;
		}
		if (getItem4() == itemIDToReplace) {
			setItem4(replacementID);
			return true;
		}
		
		return false;
	}
	
	private void setItemSlot(int slot, int itemID) {
		if (slot == 1) {
			setItem1(itemID);
		} else if (slot == 2) {
			setItem2(itemID);
		} else if (slot == 3) {
			setItem3(itemID);
		} else if (slot == 4) {
			setItem4(itemID);
		}
	}
	
	public void removeAllItems() {
		setItem1(0);
		setItem2(0);
		setItem3(0);
		setItem4(0);
	}

	public void removeItem(int itemID) {
		if (getItem1() == itemID) {
			setItem1(0);
		}
		if (getItem2() == itemID) {
			setItem2(0);
		}
		if (getItem3() == itemID) {
			setItem3(0);
		}
		if (getItem4() == itemID) {
			setItem4(0);
		}

		collapseItems();
	}

	public boolean hasItem(int itemID) {
		if (getItem1() == itemID) {
			return true;
		}
		if (getItem2() == itemID) {
			return true;
		}
		if (getItem3() == itemID) {
			return true;
		}
		if (getItem4() == itemID) {
			return true;
		}

		return false;
	}

	protected void collapseItems() {
		int[] items = new int[4];
		int counter = 0;
		
		if (getItem1() != 0) {
			items[counter] = getItem1();
			counter++;
		}
		if (getItem2() != 0) {
			items[counter] = getItem2();
			counter++;
		}
		if (getItem3() != 0) {
			items[counter] = getItem3();
			counter++;
		}
		if (getItem4() != 0) {
			items[counter] = getItem4();
			counter++;
		}
		
		setItem1(items[0]);
		setItem2(items[1]);
		setItem3(items[2]);
		setItem4(items[3]);
	}
	
	public abstract void setAIToHeal();
	public abstract void removeHealingAI();
	public abstract void setUnitToDropLastItem(boolean drop);
	
	public abstract boolean isAITargetingVillages();
}
