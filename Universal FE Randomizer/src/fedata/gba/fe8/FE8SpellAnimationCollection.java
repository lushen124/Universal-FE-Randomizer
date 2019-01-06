package fedata.gba.fe8;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import fedata.gba.GBAFESpellAnimationCollection;
import fedata.general.FEModifiableData;
import util.Diff;
import util.DiffCompiler;

public class FE8SpellAnimationCollection implements GBAFESpellAnimationCollection {
	
	public enum Animation {
		NONE(0x00), THROWN_AXE(0x01), ARROW(0x02), JAVELIN(0x03), JAVELIN2(0x04), JAVELIN3(0x05), JAVELIN4(0x06), JAVELIN5(0x07), JAVELIN6(0x08), JAVELIN7(0x09),
		JAVELIN8(0x0A), JAVELIN9(0x0B), JAVELIN10(0x0C), JAVELIN11(0x0D), DANCE(0x0E), DANCE2(0x0F), BALLISTA(0x10), FIRE(0x016), ELFIRE(0x17),
		THUNDER(0x19), BOLTING(0x1A), FIMBULVETR(0x1B), FLUX(0x1D), NOSFERATU(0x1E), LIGHTNING(0x1F), PURGE(0x20), DIVINE(0x22),
		ECLIPSE(0x24), FENRIR(0x25), SHINE(0x33), LUNA(0x34), EXCALIBUR(0x35), AURA(0x37), IVALDI(0x3F), DEMON_LIGHT(0x40), GLEIPNIR(0x41),
		CRIMSON_EYE(0x42), EVIL_EYE(0x43), SHADOWSHOT(0x44), DEMON_SURGE(0x45), NAGLFAR(0x46), WRETCHED_AIR(0x47), MAP_ONLY(0xFFFE), NONE2(0xFFFF);
		
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
			Animation[] magicAnimations = {FIRE, ELFIRE, THUNDER, BOLTING, FIMBULVETR, FLUX, LIGHTNING, PURGE, DIVINE, SHINE, EVIL_EYE, CRIMSON_EYE, SHADOWSHOT, DEMON_SURGE};
			return magicAnimations[rng.nextInt(magicAnimations.length)];
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
		
		public void setAnimationUsed(Animation animation) {
			setAnimationValue(animation.value);
		}
		
		public void setAnimationValue(int animationValue) {
			data[4] = (byte)(animationValue & 0xFF);
			data[5] = (byte)((animationValue & 0xFF00) >> 8);
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
	
	public FE8SpellAnimationCollection(byte[] data, long originalOffset) {
		super();
	
		int currentOffset = 0;
		
		entries = new HashMap<Integer, SpellAnimationEntry>();
		
		for (int i = 0; i < FE8Data.NumberOfSpellAnimations; i++) {
			SpellAnimationEntry entry = new SpellAnimationEntry(Arrays.copyOfRange(data, currentOffset, currentOffset + FE8Data.BytesPerSpellAnimation), currentOffset + originalOffset);
			
			entries.put(entry.getWeaponID(), entry);
			
			currentOffset += FE8Data.BytesPerSpellAnimation;
		}
	}
	
	public int getAnimationValueForID(int itemID) {
		return entries.get(itemID).getAnimationRaw();
	}
	
	public Animation getAnimationForID(int itemID) {
		return entries.get(itemID).getAnimation();
	}
	
	public void setAnimationValueForID(int itemID, int animationValue) {
		setAnimationForID(itemID, Animation.animationWithID(animationValue));
	}
	
	public void setAnimationForID(int itemID, Animation animation) {
		entries.get(itemID).setAnimationUsed(animation);
	}
	
	public void commit() {
		for (SpellAnimationEntry entry : entries.values()) {
			entry.commitChanges();
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (SpellAnimationEntry entry : entries.values()) {
			entry.commitChanges();
			if (entry.hasCommittedChanges()) {
				Diff charDiff = new Diff(entry.getAddressOffset(), entry.getData().length, entry.getData(), null);
				compiler.addDiff(charDiff);
			}
		}
	}
}
