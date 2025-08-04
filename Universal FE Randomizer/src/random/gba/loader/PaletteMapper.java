package random.gba.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fedata.gba.GBAFECharacterData;
import util.DebugPrinter;
import util.WhyDoesJavaNotHaveThese;

// For use with FE6 and FE7 only.
public class PaletteMapper {
	
	public enum ClassType {
		UNPROMOTED, PROMOTED, UNPROMOTED_ONLY;
	}
	
	private static class WaitListItem {
		GBAFECharacterData character;
		ClassType requiredType;
		int sizeNeeded;
		
		private WaitListItem(GBAFECharacterData character, ClassType type, int sizeNeeded) {
			this.character = character;
			this.requiredType = type;
			this.sizeNeeded = sizeNeeded;
		}
	}
	
	private Map<Integer, List<Integer>> recycledPaletteIDsBySize = new HashMap<Integer, List<Integer>>();
	private List<Integer> emptyPaletteIDs = new ArrayList<Integer>();
	private List<WaitListItem> waitList = new ArrayList<WaitListItem>();
	
	private CharacterDataLoader charData;
	
	private Map<Integer, Integer> registeredPaletteLengths = new HashMap<Integer, Integer>();
	private Map<Integer, Long> registeredPaletteOffsets = new HashMap<Integer, Long>();
	
	public PaletteMapper(CharacterDataLoader charData, List<Integer> emptyPaletteIDs) {
		this.charData = charData;
		
		this.emptyPaletteIDs = emptyPaletteIDs;
	}
	
	public void registerPalette(int paletteID, int length, Long offset) {
		if (paletteID == 0) { return; }
		assert registeredPaletteLengths.get(paletteID) == null : "Attempted to register an already-registered palette ID!";
		registeredPaletteLengths.put(paletteID, length);
		if (offset != null) {
			registeredPaletteOffsets.put(paletteID, offset);
		}
	}
	
	public Integer getPaletteLength(int paletteID) {
		if (paletteID == 0) { return null; }
		assert registeredPaletteLengths.containsKey(paletteID) : "Requesting palette length for unregistered palette ID!";
		return registeredPaletteLengths.get(paletteID);
	}
	
	public Long getPaletteOffset(int paletteID) {
		if (paletteID == 0) { return null; }
		return registeredPaletteOffsets.get(paletteID);
	}
	
	public void flushWaitListWithEmptyPaletteIDs() {
		while (!waitList.isEmpty()) {
			WaitListItem queuedItem = waitList.remove(0);
			
			int paletteID = 0;
			
			Integer recycled = requestRecycledPaletteID(queuedItem.sizeNeeded,true);
			if (recycled != null) {
				paletteID = recycled;
			} else {
				Integer empty = requestEmptyPaletteID(queuedItem.sizeNeeded);
				if (empty != null) {
					paletteID = empty;
				}
			}
			
			// Since this is the last chance for palettes to be assigned, we will use any palette IDs that have been recycled and
			// remap them to the end of the ROM.
			if (paletteID == 0) {
				// Grab any palette ID that's not used. We will unregister the old address and write the new address (at the end of the ROM).
				recycled = requestRecycledPaletteID(0);
				if (recycled != null) {
					paletteID = recycled;
					DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Final recycling palette 0x" + Integer.toHexString(paletteID) + " for character 0x" + Integer.toHexString(queuedItem.character.getID()) + " (" + queuedItem.character.displayString() + ") of type: " + queuedItem.requiredType.name() + " actual size: " + queuedItem.sizeNeeded);
					// Remove the old offset, since we won't need it anymore.
					registeredPaletteOffsets.remove(paletteID);
					// Set the new length to what we need.
					registeredPaletteLengths.put(paletteID, queuedItem.sizeNeeded);
				} else {
					System.err.println("No palettes available to fulfill palette for character:" + queuedItem.character.displayString());
				}
			}
			
			GBAFECharacterData[] linked = charData.linkedCharactersForCharacter(queuedItem.character);
			for (GBAFECharacterData linkedChar : linked) {
				if (queuedItem.requiredType == ClassType.PROMOTED) {
					linkedChar.setPromotedPaletteIndex(paletteID);
				} else if (queuedItem.requiredType == ClassType.UNPROMOTED) {
					linkedChar.setUnpromotedPaletteIndex(paletteID);
				} else {
					assert false : "Unpromoted-only is not a valid type to wait on.";
				}
			}
		}
	}
	
	public void setCharacterToUnpromotedOnlyClass(int characterID, int unpromotedClassID, int unpromotedSize) {
		GBAFECharacterData character = charData.characterWithID(characterID);
		
		// Free the promoted palette if we have one.
		freePaletteFromCharacter(character, ClassType.PROMOTED);
		allocatePaletteToCharacter(character, ClassType.UNPROMOTED, unpromotedSize);
	}
	
	public void setCharacterToUnpromotedClass(int characterID, int unpromotedClassID, int unpromotedSize, int promotedSize) {
		GBAFECharacterData character = charData.characterWithID(characterID);
		
		allocatePaletteToCharacter(character, ClassType.PROMOTED, promotedSize);
		allocatePaletteToCharacter(character, ClassType.UNPROMOTED, unpromotedSize);
	}
	
	public void setCharacterToPromotedClass(int characterID, int promotedClassID, int promotedSize) {
		GBAFECharacterData character = charData.characterWithID(characterID);
		
		freePaletteFromCharacter(character, ClassType.UNPROMOTED);
		allocatePaletteToCharacter(character, ClassType.PROMOTED, promotedSize);
	}
	
	private void freePaletteFromCharacter(GBAFECharacterData character, ClassType type) {		
		GBAFECharacterData[] linked = charData.linkedCharactersForCharacter(character);
		
		boolean didRecycle = false;
		
		switch (type) {
		case UNPROMOTED:
			int paletteID = character.getUnpromotedPaletteIndex();
			if (paletteID == 0) {
				DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Can't recycle unpromoted palette ID 0. Ignoring...");
				return;
			}
			if (paletteID != character.getPromotedPaletteIndex()) {
				recyclePaletteID(paletteID);
				DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Recycling unpromoted palette 0x" + Integer.toHexString(paletteID) + " from character 0x" + Integer.toHexString(character.getID()) + " (" + charData.debugStringForCharacter(character.getID()) + ")");
				didRecycle = true;
			} else {
				DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Unlinked unpromoted palette 0x" + Integer.toHexString(paletteID) + " since it's shared with the promoted palette.");
			}
			for (GBAFECharacterData linkedChar : linked) {
				linkedChar.setUnpromotedPaletteIndex(0);
			}
			break;
		case PROMOTED:
			paletteID = character.getPromotedPaletteIndex();
			if (paletteID == 0) {
				DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Can't recycle promoted palette with ID 0. Ignoring...");
			}
			if (paletteID != character.getUnpromotedPaletteIndex()) {
				recyclePaletteID(paletteID);
				DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Recycling promoted palette 0x" + Integer.toHexString(paletteID) + " from character 0x" + Integer.toHexString(character.getID()) + " (" + charData.debugStringForCharacter(character.getID()) + ")");
				didRecycle = true;
			} else {
				DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Unlinked promoted palette 0x" + Integer.toHexString(paletteID) + " since it's shared with the unpromoted palette.");
			}
			for (GBAFECharacterData linkedChar : linked) {
				linkedChar.setPromotedPaletteIndex(0);
			}
			break;
		case UNPROMOTED_ONLY:
			assert false : "Palettes must be freed from either Unpromoted or Promoted type.";
			return;
		}
		
		if (!waitList.isEmpty() && didRecycle) {
			WaitListItem queuedItem = waitList.remove(0);
			DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Processing waitlist item (Character: " + charData.debugStringForCharacter(queuedItem.character.getID()) + ", Type: " + queuedItem.requiredType.toString() + ", Size Needed: " + queuedItem.sizeNeeded + ")");
			allocatePaletteToCharacter(queuedItem.character, queuedItem.requiredType, queuedItem.sizeNeeded);
		}
	}
	
	private void allocatePaletteToCharacter(GBAFECharacterData character, ClassType type, int size) {
		if (character == null || type == null) { return; }
		
		DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Allocating palette for character 0x" + Integer.toHexString(character.getID()) + " (" + charData.debugStringForCharacter(character.getID()) + "). Type: " + type.toString() + "\tSize: " + size);
		
		int unpromotedPaletteID = character.getUnpromotedPaletteIndex();
		Integer unpromotedLength = getPaletteLength(unpromotedPaletteID);
		int promotedPaletteID = character.getPromotedPaletteIndex();
		Integer promotedLength = getPaletteLength(promotedPaletteID);
		
		if (type == ClassType.UNPROMOTED && unpromotedPaletteID != 0 && unpromotedLength != null) {
			if (unpromotedPaletteID == promotedPaletteID) {
				// These are awkward to deal with (Roy has this issue where promoted/unpromoted use the same palette, which works for Lord, but not for most other classes.)
				// Go ahead and separate these now.
				// If our current one works, then unlink the other. Otherwise, unlink this one.
				if (unpromotedLength >= size) {
					DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Unpromoted Palette OK! ID = 0x" + Integer.toHexString(unpromotedPaletteID) + " Available Length: " + unpromotedLength + "\tRequested Length: " + size);
					DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Unlinking shared promoted palette 0x" + Integer.toHexString(promotedPaletteID));
					character.setPromotedPaletteIndex(0);
					return;
				} else {
					DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Unlinking shared unpromoted palette 0x" + Integer.toHexString(unpromotedPaletteID) + " due to insufficient space.");
					character.setUnpromotedPaletteIndex(0);
				}
			} else if (unpromotedLength >= size) {
				DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Unpromoted Palette OK! ID = 0x" + Integer.toHexString(unpromotedPaletteID) + " Available Length: " + unpromotedLength + "\tRequested Length: " + size);
				return;
			} else {
				DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Unpromoted Palette is unsuitable (unpromoted: 0x" + Integer.toHexString(unpromotedPaletteID) + " promoted: 0x" + Integer.toHexString(promotedPaletteID) + ") (Available Length: " + unpromotedLength + " Required Length: " + size + ")");
				freePaletteFromCharacter(character, ClassType.UNPROMOTED);
			}
		}
		if (type == ClassType.PROMOTED && promotedPaletteID != 0) {
			if (unpromotedPaletteID == promotedPaletteID) {
				// Same as above, except for promoted, if we get to it first.
				if (promotedLength >= size) {
					DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Promoted Palette OK! ID = 0x" + Integer.toHexString(promotedPaletteID) + " Available Length: " + promotedLength + "\tRequested Length: " + size);
					DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Unlinking shared unpromoted palette 0x" + Integer.toHexString(unpromotedPaletteID));
					character.setUnpromotedPaletteIndex(0);
					return;
				} else {
					DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Unlinking shared promoted palette 0x" + Integer.toHexString(promotedPaletteID) + " due to insufficient space.");
					character.setPromotedPaletteIndex(0);
				}
			} else if (promotedLength >= size) {
				DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Promoted Palette OK! ID = 0x" + Integer.toHexString(promotedPaletteID) + " Available Length: " + promotedLength + "\tRequested Length: " + size);
				return;
			} else {
				DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Promoted Palette is unsuitable (unpromoted: 0x" + Integer.toHexString(unpromotedPaletteID) + " promoted: 0x" + Integer.toHexString(promotedPaletteID) + ") (Available Length: " + promotedLength + " Required Length: " + size + ")");
				freePaletteFromCharacter(character, ClassType.PROMOTED);
			}
		}
		
		int paletteID = 0;
		
		Integer recycledPaletteID = requestRecycledPaletteID(size,false);
		if (recycledPaletteID != null) {
			paletteID = recycledPaletteID;
		} else {
			Integer emptyPaletteID = requestEmptyPaletteID(size);
			if (emptyPaletteID != null) {
				paletteID = emptyPaletteID;
			}
		}
		
		if (paletteID == 0) {
			DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "No Palette ID available right now. Adding to waitlist. (" + charData.debugStringForCharacter(character.getID()) + ", " + type.toString() + ", " + size + ")");
			waitList.add(new WaitListItem(character, type, size));
			return;
		}
		
		DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Assigning palette ID 0x" + Integer.toHexString(paletteID) + " to " + charData.debugStringForCharacter(character.getID()));

		GBAFECharacterData[] linked = charData.linkedCharactersForCharacter(character);
		
		for (GBAFECharacterData linkedChar : linked) {
			switch (type) {
			case PROMOTED:
				linkedChar.setPromotedPaletteIndex(paletteID);
				break;
			case UNPROMOTED:
				linkedChar.setUnpromotedPaletteIndex(paletteID);
				break;
			case UNPROMOTED_ONLY:
				assert false : "Palettes can only be allocated to either Unpromoted or Promoted type.";
				break;
			}
		}
	}
	
	private Integer requestEmptyPaletteID(int size) {
		if (emptyPaletteIDs.isEmpty()) { return null; }
		int paletteID = emptyPaletteIDs.remove(0);
		registerPalette(paletteID, size, null);
		DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Consumed Empty Palette ID 0x" + Integer.toHexString(paletteID) + " (Registered Size: " + size + ")");
		return paletteID;
	}
	
	private void recyclePaletteID(int paletteID) {
		if (paletteID == 0) { return; }
		Integer paletteSize = getPaletteLength(paletteID);
		if (paletteSize != null) {
			List<Integer> paletteIDs = recycledPaletteIDsBySize.get(paletteSize);
			if (paletteIDs == null) {
				paletteIDs = new ArrayList<Integer>();
				recycledPaletteIDsBySize.put(paletteSize, paletteIDs);
			}
			paletteIDs.add(paletteID);
			
			DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Freed Palette ID 0x" + Integer.toHexString(paletteID) + " (Registered Size: " + paletteSize + ")");
		}
	}

	private Integer requestRecycledPaletteID(int sizeNeeded, boolean forceAssign) {
		if (recycledPaletteIDsBySize.keySet().stream().filter(length -> (length >= sizeNeeded)).min(WhyDoesJavaNotHaveThese.ascendingIntegerComparator).isPresent()) {
			int bestFit = recycledPaletteIDsBySize.keySet().stream().filter(length -> (length >= sizeNeeded)).min(WhyDoesJavaNotHaveThese.ascendingIntegerComparator).get();
			List<Integer> paletteIDs = recycledPaletteIDsBySize.get(bestFit);
			if (paletteIDs.isEmpty()) {
				recycledPaletteIDsBySize.remove(bestFit);
				return requestRecycledPaletteID(sizeNeeded, forceAssign);
			} else {
				int paletteID = paletteIDs.remove(0);
				DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Recycled Palette ID 0x" + Integer.toHexString(paletteID) + " (Available Length: " + bestFit + " Requested Size: " + sizeNeeded + ")");
				return paletteID;
			}
		} else if (recycledPaletteIDsBySize.size() > 0 && forceAssign == true) {
			int smallest = recycledPaletteIDsBySize.keySet().stream().min(WhyDoesJavaNotHaveThese.ascendingIntegerComparator).get();
			List<Integer> paletteIDs = recycledPaletteIDsBySize.get(smallest);
			if (paletteIDs.isEmpty()) {
				recycledPaletteIDsBySize.remove(smallest);
				return requestRecycledPaletteID(sizeNeeded, forceAssign);
			} else {
				int paletteID = paletteIDs.remove(0);
				DebugPrinter.log(DebugPrinter.Key.PALETTE_RECYCLER, "Recycled Palette ID 0x" + Integer.toHexString(paletteID) + " (Repointing)");
				registeredPaletteOffsets.remove(paletteID);
				registeredPaletteLengths.put(paletteID, sizeNeeded);
				return paletteID;
			}
		} else {
			return null;
		}
	}
}
