package fedata.gba.fe8;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fedata.gba.GBAFECharacterData;
import fedata.general.FEModifiableData;
import io.FileHandler;
import random.gba.loader.CharacterDataLoader;
import util.DebugPrinter;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;
import util.FreeSpaceManager;
import util.WhyDoesJavaNotHaveThese;

public class FE8SummonerModule {
	
	public enum SummonType {
		LYON(0x3B), KNOLL(0x3E), EWAN(0x3F);
		
		int ID;
		
		private static Map<Integer, SummonType> map;
		
		static {
			map = new HashMap<Integer, SummonType>();
			for (SummonType summon : SummonType.values()) {
				map.put(summon.ID, summon);
			}
		}
		
		private SummonType(int identifier) {
			ID = identifier;
		}
		
		public static SummonType summonTypeWithID(int summonID) {
			return map.get(summonID);
		}
		
		public static SummonType random(Random rng) {
			return SummonType.values()[rng.nextInt(SummonType.values().length)];
		}
	}
	
	private class SummonerEntry implements FEModifiableData {
		byte[] originalData;
		byte[] data;
		
		long originalOffset;
		
		Boolean wasModified = false;
		Boolean hasChanges = false;
		
		private SummonerEntry(FileHandler handler, long offset) {
			originalData = handler.readBytesAtOffset(offset, FE8Data.BytesPerSummonerEntry);
			data = originalData.clone();
			originalOffset = offset;
		}
		
		private SummonerEntry(int characterID, Random rng) {
			originalData = new byte[FE8Data.BytesPerSummonerEntry];
			for (int i = 0; i < FE8Data.BytesPerSummonerEntry; i++) {
				originalData[i] = 0;
			}
			
			originalData[0] = (byte)(characterID & 0xFF);
			originalData[1] = (byte)(SummonType.values()[rng.nextInt(SummonType.values().length)].ID & 0xFF);
			
			data = originalData.clone();
			
			originalOffset = -1;
		}
		
		public void resetData() {
			data = originalData.clone();
			wasModified = false;
		}

		public void commitChanges() {
			if (wasModified) {
				originalData = data.clone();
				hasChanges = true;
			}
			
			wasModified = false;
		}

		public byte[] getData() {
			return data;
		}

		public Boolean hasCommittedChanges() {
			return hasChanges;
		}

		public Boolean wasModified() {
			return wasModified;
		}

		public long getAddressOffset() {
			return originalOffset;
		}
		
		public int getCharacterID() {
			return data[0] & 0xFF;
		}
		
		public void setCharacterID(int characterID) {
			data[0] = (byte)(characterID & 0xFF);
			wasModified = true;
		}
		
		public SummonType getSummonType() {
			return SummonType.summonTypeWithID(data[1] & 0xFF);
		}
		
		public void setSummonType(SummonType type) {
			data[1] = (byte)(type.ID & 0xFF);
			wasModified = true;
		}
	}

	Map<Integer, SummonerEntry> entriesByCharacterID;
	List<SummonerEntry> retiredSummoners;
	int initialNumberOfEntries;
	
	public FE8SummonerModule(FileHandler handler) {
		entriesByCharacterID = new HashMap<Integer, SummonerEntry>();
		long pointerAddress = FE8Data.SummonerTablePointer;
		long tableAddress = FileReadHelper.readAddress(handler, pointerAddress);
		if (tableAddress != -1) {
			long currentAddress = tableAddress;
			byte[] data;
			do {
				data = handler.readBytesAtOffset(currentAddress, FE8Data.BytesPerSummonerEntry);
				if (data[0] != 0) {
					SummonerEntry newEntry = new SummonerEntry(handler, currentAddress);
					entriesByCharacterID.put(newEntry.getCharacterID(), newEntry);
				}
				currentAddress += FE8Data.BytesPerSummonerEntry;
			} while (data[0] != 0);
		}
		initialNumberOfEntries = entriesByCharacterID.size();
		retiredSummoners = new ArrayList<SummonerEntry>();
	}
	
	public void registerSummoner(int characterID, Random rng) {
		if (entriesByCharacterID.containsKey(characterID)) { return; }
		if (retiredSummoners.isEmpty()) {
			SummonerEntry newEntry = new SummonerEntry(characterID, rng);
			entriesByCharacterID.put(characterID, newEntry);
		} else {
			SummonerEntry recycledEntry = retiredSummoners.get(0);
			recycledEntry.setCharacterID(characterID);
			recycledEntry.setSummonType(SummonType.random(rng));
			retiredSummoners.remove(0);
			entriesByCharacterID.put(characterID, recycledEntry);
		}
	}
	
	public Set<Integer> getAllSummonerIDs() {
		return entriesByCharacterID.keySet();
	}

	public void unregisterSummoner(int characterID) {
		SummonerEntry entry = entriesByCharacterID.get(characterID);
		if (entry != null) {
			entriesByCharacterID.remove(characterID);
			retiredSummoners.add(entry);
		}
	}
	
	public SummonType getSummonType(int characterID) {
		if (!entriesByCharacterID.containsKey(characterID)) { return null; }
		return entriesByCharacterID.get(characterID).getSummonType();
	}
	
	public void validateSummoners(CharacterDataLoader charData, Random rng) {
		DebugPrinter.log(DebugPrinter.Key.FE8_SUMMONER_MODULE, "Validating summoners...");
		Set<Integer> oldSummonerCharacterIDSet = new HashSet<Integer>(getAllSummonerIDs());
		for (int characterID : oldSummonerCharacterIDSet) {
			GBAFECharacterData character = charData.characterWithID(characterID);
			FE8Data.CharacterClass charClass = FE8Data.CharacterClass.valueOf(character.getClassID());
			if (!(charClass == FE8Data.CharacterClass.SUMMONER || charClass == FE8Data.CharacterClass.SUMMONER_F || 
					charClass == FE8Data.CharacterClass.SHAMAN || charClass == FE8Data.CharacterClass.SHAMAN_F || 
					charClass == FE8Data.CharacterClass.NECROMANCER)) {
				unregisterSummoner(characterID);
				FE8Data.Character fe8Character = FE8Data.Character.valueOf(characterID);
				DebugPrinter.log(DebugPrinter.Key.FE8_SUMMONER_MODULE, "Unregistered old summoner " + fe8Character.toString() + "...");		
			}
		}
		
		for (GBAFECharacterData character : charData.playableCharacters()) {
			FE8Data.CharacterClass charClass = FE8Data.CharacterClass.valueOf(character.getClassID());
			if ((charClass == FE8Data.CharacterClass.SUMMONER || charClass == FE8Data.CharacterClass.SUMMONER_F || 
					charClass == FE8Data.CharacterClass.SHAMAN || charClass == FE8Data.CharacterClass.SHAMAN_F || 
					charClass == FE8Data.CharacterClass.NECROMANCER)) {
				registerSummoner(character.getID(), rng);
				FE8Data.Character fe8Character = FE8Data.Character.valueOf(character.getID());
				DebugPrinter.log(DebugPrinter.Key.FE8_SUMMONER_MODULE, "Registering new summoner " + fe8Character.toString() + "...");
			}
		}
	}
	
	public void commitChanges(DiffCompiler compiler, FreeSpaceManager freeSpace) {
		Boolean needsRepoint = false;
		for (SummonerEntry entry : entriesByCharacterID.values()) {
			// If we have any entries without original offsets, we repoint the table.
			if (entry.originalOffset == -1) {
				needsRepoint = true;
				break;
			}
		}
		
		if (needsRepoint) {
			// We need free space.
			long newTableOffset = 0;
			List<Integer> charIDs = new ArrayList<Integer>(entriesByCharacterID.keySet());
			charIDs.sort(new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					// TODO Auto-generated method stub
					return o1 - o2;
				}
				
			});
			for (int characterID : charIDs) {
				SummonerEntry entry = entriesByCharacterID.get(characterID);
				long offset = freeSpace.setValue(entry.getData(), "SUMMONER_0x" + Integer.toHexString(characterID));
				if (newTableOffset == 0) { newTableOffset = offset; }
			}
			
			// Repoint the table.
			byte[] addressBytes = WhyDoesJavaNotHaveThese.gbaAddressFromOffset(newTableOffset);
			Diff repointDiff = new Diff(FE8Data.SummonerTablePointer, addressBytes.length, addressBytes, null);
			// Make sure we write the address to all three pointers.
			Diff repointDiff2 = new Diff(FE8Data.SummonerTablePointer2, addressBytes.length, addressBytes, null);
			Diff repointDiff3 = new Diff(FE8Data.SummonerTablePointer3, addressBytes.length, addressBytes, null);
			compiler.addDiff(repointDiff);
			compiler.addDiff(repointDiff2);
			compiler.addDiff(repointDiff3);
			
			DebugPrinter.log(DebugPrinter.Key.FE8_SUMMONER_MODULE, "Repointing data to 0x" + Long.toHexString(newTableOffset) + "...");
		} else {
			// These all have original offsets, just write them in place.
			for (SummonerEntry entry : entriesByCharacterID.values()) {
				Diff newDiff = new Diff(entry.getAddressOffset(), entry.getData().length, entry.getData(), null);
				DebugPrinter.log(DebugPrinter.Key.FE8_SUMMONER_MODULE, "Overwrite summoner data at offset 0x" + Long.toHexString(entry.getAddressOffset()));
				compiler.addDiff(newDiff);
			}
		}
	}
}
