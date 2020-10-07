package fedata.gba.fe7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fedata.gba.GBAFESpellAnimationCollection;
import fedata.general.FEModifiableData;
import util.ByteArrayBuilder;
import util.Diff;
import util.DiffCompiler;
import util.FindAndReplace;
import util.FreeSpaceManager;
import util.WhyDoesJavaNotHaveThese;

public class FE7SpellAnimationCollection implements GBAFESpellAnimationCollection {
	
	public enum Animation {
		NONE(0x00), THROWN_AXE(0x01), ARROW(0x02), JAVELIN(0x03), JAVELIN2(0x04), JAVELIN3(0x05), JAVELIN4(0x06), JAVELIN5(0x07), JAVELIN6(0x08), JAVELIN7(0x09),
		JAVELIN8(0x0A), JAVELIN9(0x0B), JAVELIN10(0x0C), JAVELIN11(0x0D), DANCE(0x0E), DANCE2(0x0F), BALLISTA(0x10), FLAMETONGUE(0x13), FIRE(0x016), ELFIRE(0x17),
		FORBLAZE(0x18), THUNDER(0x19), BOLTING(0x1A), FIMBULVETR(0x1B), FLUX(0x1D), NOSFERATU(0x1E), LIGHTNING(0x1F), PURGE(0x20), AUREOLA(0x21), DIVINE(0x22),
		ECLIPSE(0x24), FENRIR(0x25), SHINE(0x33), LUNA(0x34), EXCALIBUR(0x35), GESPENST(0x36), AURA(0x37), LUCE(0x38), ERESHKIGAL(0x39), MAP_ONLY(0xFFFE), NONE2(0xFFFF);
		
		public int value;
		
		private static Map<Integer, Animation> map = new HashMap<Integer, Animation>();
		
		static {
			for (Animation animation : Animation.values()) {
				map.put(animation.value, animation);
			}
		}
		
		private Animation(final int value) { this.value = value; }
		
		public static Animation animationWithID(int value) {
			return map.get(value);
		}
		
		public static Animation randomMagicAnimation(Random rng) {
			Animation[] magicAnimations = {FIRE, ELFIRE, THUNDER, BOLTING, FIMBULVETR, FLUX, LIGHTNING, PURGE, DIVINE, SHINE};
			return magicAnimations[rng.nextInt(magicAnimations.length)];
		}
	}
	
	public enum Flash {
		WHITE(0x00), DARK(0x01), RED(0x02), GREEN(0x03), BLUE(0x04), YELLOW(0x05);
		
		public int value;
		
		private static Map<Integer, Flash> map = new HashMap<Integer, Flash>();
		
		static {
			for (Flash flash : Flash.values()) {
				map.put(flash.value, flash);
			}
		}
		
		private Flash(final int value) { this.value = value; }
		
		public static Flash flashWithID(int value) {
			return map.get(value);
		}
	}
	
	private class SpellAnimationEntry implements FEModifiableData {
		private byte[] originalData;
		private byte[] data;
		
		private long originalOffset;
		
		private Boolean wasModified = false;
		private Boolean hasChanges = false;
		
		public SpellAnimationEntry(byte[] data, long originalOffset) {
			super();
			this.originalData = data;
			this.data = data;
			this.originalOffset = originalOffset;
		}
		
		public int getWeaponID() {
			return data[0] & 0xFF;
		}
		
		public int getAnimationRaw() {
			return (data[4] & 0xFF) | ((data[5] & 0xFF) << 8);
		}
		
		public Animation getAnimation() {
			return Animation.animationWithID(getAnimationRaw());
		}
		
		@SuppressWarnings("unused")
		public int getFlashColor() {
			return data[14] & 0xFF;
		}
		
		public void setAnimationUsed(Animation animation) {
			setAnimationValue(animation.value);
			
			if (animation != Animation.NONE && animation != Animation.NONE2) {
				switch (animation) {
				case FIRE:
				case ELFIRE:
				case FORBLAZE:
				case FLAMETONGUE:
					setFlashColor(Flash.RED);
					break;
				case THUNDER:
				case BOLTING:
					setFlashColor(Flash.YELLOW);
					break;
				case ECLIPSE:
				case FENRIR:
				case FLUX:
				case NOSFERATU:
				case LUNA:
				case GESPENST:
				case ERESHKIGAL:
					setFlashColor(Flash.DARK);
					break;
				case FIMBULVETR:
					setFlashColor(Flash.BLUE);
					break;
				case EXCALIBUR:
					setFlashColor(Flash.GREEN);
					break;
				default:
					setFlashColor(Flash.WHITE);
					break;
				}
			}
		}
		
		public void setAnimationValue(int animationValue) {
			data[4] = (byte)(animationValue & 0xFF);
			data[5] = (byte)((animationValue & 0xFF00) >> 8);
			wasModified = true;
		}
		
		private void setFlashColor(Flash flashColor) {
			data[14] = (byte)(flashColor.value & 0xFF);
			wasModified = true;
		}
		
		public void resetData() {
			data = originalData;
			wasModified = false;
		}
		
		public void commitChanges() {
			if (wasModified) {
				hasChanges = true;
			}
			wasModified = false;
		}
		
		public Boolean hasCommittedChanges() {
			return hasChanges;
		}
		
		public byte[] getData() {
			return data;
		}
		
		public Boolean wasModified() {
			return wasModified;
		}
		
		public long getAddressOffset() {
			return originalOffset;
		}
	}
	
	private Map<Integer, SpellAnimationEntry> entries;
	private Map<Integer, SpellAnimationEntry> addedEntries;
	
	private long originalOffset;
	
	public FE7SpellAnimationCollection(byte[] data, long originalOffset) {
		super();
	
		this.originalOffset = originalOffset;
		
		int currentOffset = 0;
		
		entries = new HashMap<Integer, SpellAnimationEntry>();
		addedEntries = new HashMap<Integer, SpellAnimationEntry>();
		
		for (int i = 0; i < FE7Data.NumberOfSpellAnimations; i++) {
			SpellAnimationEntry entry = new SpellAnimationEntry(Arrays.copyOfRange(data, currentOffset, currentOffset + FE7Data.BytesPerSpellAnimation), currentOffset + originalOffset);
			
			entries.put(entry.getWeaponID(), entry);
			
			currentOffset += FE7Data.BytesPerSpellAnimation;
		}
	}
	
	public int getAnimationValueForID(int itemID) {
		if (entries.containsKey(itemID)) {
			return entries.get(itemID).getAnimationRaw();
		} else if (addedEntries.containsKey(itemID)) {
			return addedEntries.get(itemID).getAnimationRaw();
		}
		
		return 0xFF;
	}
	
	public Animation getAnimationForID(int itemID) {
		if (entries.containsKey(itemID)) {
			return entries.get(itemID).getAnimation();
		} else if (addedEntries.containsKey(itemID)) {
			return addedEntries.get(itemID).getAnimation();
		}
		
		return null;
	}
	
	public void setAnimationValueForID(int itemID, int animationValue) {
		setAnimationForID(itemID, Animation.animationWithID(animationValue));
	}
	
	public void setAnimationForID(int itemID, Animation animation) {
		if (entries.containsKey(itemID)) {
			entries.get(itemID).setAnimationUsed(animation);
		} else if (addedEntries.containsKey(itemID)) {
			addedEntries.get(itemID).setAnimationUsed(animation);
		}
	}
	
	public void commit() {
		for (SpellAnimationEntry entry : entries.values()) {
			entry.commitChanges();
		}
	}
	
	public void compileDiffs(DiffCompiler compiler, FreeSpaceManager freeSpace) {
		if (addedEntries.isEmpty()) {
			for (SpellAnimationEntry entry : entries.values()) {
				entry.commitChanges();
				if (entry.hasCommittedChanges()) {
					Diff charDiff = new Diff(entry.getAddressOffset(), entry.getData().length, entry.getData(), null);
					compiler.addDiff(charDiff);
				}
			}
		} else {
			// Needs a repoint.
			List<SpellAnimationEntry> orderedList = new ArrayList<SpellAnimationEntry>();
			orderedList.addAll(entries.values());
			orderedList.addAll(addedEntries.values());
			orderedList.sort(new Comparator<SpellAnimationEntry>() {
				@Override
				public int compare(SpellAnimationEntry o1, SpellAnimationEntry o2) {
					return Integer.compare(o1.getWeaponID(), o2.getWeaponID());
				}
			});
			
			Long newTableOffset = null;
			for (SpellAnimationEntry entry : orderedList) {
				entry.commitChanges();
				if (newTableOffset == null) {
					newTableOffset = freeSpace.setValue(entry.getData(), "Spell Animation for ID 0x" + Integer.toHexString(entry.getWeaponID()), true);
				} else {
					freeSpace.setValue(entry.getData(), "Spell Animation for ID 0x" + Integer.toHexString(entry.getWeaponID()), false);
				}
			}
			
			// Append a terminator entry.
			freeSpace.setValue(new byte[] {
					(byte)0xFF, (byte)0xFF, (byte)0x02, (byte)0, 
					(byte)0xFF, (byte)0xFF, (byte)0, (byte)0, 
					(byte)0, (byte)0, (byte)0, (byte)0,
					(byte)0x1, (byte)0, (byte)0, (byte)0}, "Spell Animation Terminator", false);
			
			// Update the pointers
			compiler.findAndReplace(new FindAndReplace(WhyDoesJavaNotHaveThese.gbaAddressFromOffset(originalOffset), WhyDoesJavaNotHaveThese.gbaAddressFromOffset(newTableOffset), true));
		}
	}

	@Override
	public void addAnimation(int itemID, int numberOfCharacters, int animationValue, int colorValue) {
		Flash flashColor = Flash.flashWithID(colorValue);
		assert flashColor != null;
		if (flashColor == null) { return; }
		
		if (entries.containsKey(itemID)) {
			setAnimationValueForID(itemID, animationValue);
			SpellAnimationEntry entry = entries.get(itemID);
			entry.data[2] = 2; // Update the number of characters to display.
			entry.data[8] = 0;
			entry.data[9] = 0;
			entry.data[10] = 0;
			entry.data[11] = 0; // Null out the alternate pointer.
			entry.data[12] = 1; // Set the map animation to return to original position.
			entry.data[13] = 0; // Map animation always goes towards the target.
			entry.data[14] = (byte)flashColor.value; // Update the flash color if necessary.
			entry.wasModified = true;
			
			return;
		}
		
		Animation animation = Animation.animationWithID(animationValue);
		assert animation != null;
		if (animation == null) { return; }
		
		ByteArrayBuilder entryBytes = new ByteArrayBuilder();
		// First byte is the weapon ID itself.
		entryBytes.appendByte((byte)(itemID & 0xFF));
		// Next is a 0 byte.
		entryBytes.appendByte((byte)0);
		// Then the number of characters.
		entryBytes.appendByte((byte)numberOfCharacters);
		// Then another 0.
		entryBytes.appendByte((byte)0);
		// Then the animation value.
		entryBytes.appendBytes(WhyDoesJavaNotHaveThese.byteArrayFromLongValue(animation.value, false, 2));
		// Then the alternate pointer, which we generally don't use (it's used for staves, which we don't touch).
		entryBytes.appendBytes(new byte[] {0, 0, 0, 0});
		// Then whether the animation returns to original position (used for staves and healing items). This is always on for us.
		entryBytes.appendByte((byte)0x1);
		// The position to face (we're only messing with weapons, so this is always towards target).
		entryBytes.appendByte((byte)0);
		// The enemy of the flash with map animations.
		entryBytes.appendByte((byte)flashColor.value);
		// Then a trailing 0.
		entryBytes.appendByte((byte)0);
		
		// This should be 16 bytes long.
		assert entryBytes.getBytesWritten() == FE7Data.BytesPerSpellAnimation;
		
		SpellAnimationEntry newEntry = new SpellAnimationEntry(entryBytes.toByteArray(), -1);
		addedEntries.put(itemID, newEntry);
	}
}
