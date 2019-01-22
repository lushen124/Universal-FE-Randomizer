package random.snes.fe4.randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import fedata.snes.fe4.FE4Data;
import fedata.snes.fe4.FE4HolyBlood;
import fedata.snes.fe4.FE4StaticCharacter;
import random.snes.fe4.loader.CharacterDataLoader;
import random.snes.fe4.loader.HolyBloodLoader;
import random.snes.fe4.loader.ItemMapper;

public class FE4BloodRandomizer {
	
	static final int rngSalt = 12321;
	
	public static void randomizeHolyBloodGrowthBonuses(int growthBonusTotal, HolyBloodLoader bloodData, Random rng) {
		List<FE4HolyBlood> bloodList = bloodData.allHolyBlood();
		for (FE4HolyBlood blood : bloodList) {
			int growthRemaining = growthBonusTotal;
			
			int hpBonus = 0;
			int strBonus = 0;
			int magBonus = 0;
			int sklBonus = 0;
			int spdBonus = 0;
			int lckBonus = 0;
			int defBonus = 0;
			int resBonus = 0;
			
			while (growthRemaining > 0) {
				int amount = 5;
				growthRemaining -= 5;
				int field = rng.nextInt(7);
				switch (field) {
				case 0:
					hpBonus += amount;
					break;
				case 1:
					if (blood.getWeaponType().isPhysical()) { strBonus += amount; }
					else { magBonus += amount; }
					break;
				case 2:
					sklBonus += amount;
					break;
				case 3:
					spdBonus += amount;
					break;
				case 4:
					lckBonus += amount;
					break;
				case 5:
					defBonus += amount;
					break;
				case 6:
					resBonus += amount;
					break;
				}
			}
			
			blood.setHPGrowthBonus(hpBonus);
			blood.setSTRGrowthBonus(strBonus);
			blood.setMAGGrowthBonus(magBonus);
			blood.setSKLGrowthBonus(sklBonus);
			blood.setSPDGrowthBonus(spdBonus);
			blood.setLCKGrowthBonus(lckBonus);
			blood.setDEFGrowthBonus(defBonus);
			blood.setRESGrowthBonus(resBonus);
		}
	}

	public static void randomizeHolyWeaponBonuses(HolyBloodLoader bloodData, Random rng) {
		List<FE4HolyBlood> bloodList = bloodData.allHolyBlood();
		for (FE4HolyBlood blood : bloodList) {
			
			FE4Data.Item holyWeapon = FE4Data.Item.valueOf(blood.getHolyWeaponID());
			if (holyWeapon.holyBloodForHolyWeapon() == FE4Data.HolyBlood.NAGA ||
					holyWeapon.holyBloodForHolyWeapon() == FE4Data.HolyBlood.LOPTOUS) {
				continue;
			}
			
			int holyWeaponBonuses = blood.getHolyWeaponSKLBonus() + blood.getHolyWeaponSPDBonus() + blood.getHolyWeaponDEFBonus() + blood.getHolyWeaponRESBonus() + blood.getHolyWeaponSTRBonus() + blood.getHolyWeaponMAGBonus();
			
			int strBonus = 0;
			int magBonus = 0;
			int sklBonus = 0;
			int spdBonus = 0;
			int defBonus = 0;
			int resBonus = 0;
			
			while (holyWeaponBonuses > 0) {
				int amount = 5;
				holyWeaponBonuses -= 5;
				int field = rng.nextInt(5);
				switch (field) {
				case 0:
					if (blood.getWeaponType().isPhysical()) { strBonus += amount; }
					else { magBonus += amount; }
					break;
				case 1:
					sklBonus += amount;
					break;
				case 2:
					spdBonus += amount;
					break;
				case 3:
					defBonus += amount;
					break;
				case 4:
					resBonus += amount;
					break;
				}
			}
			
			blood.setHolyWeaponSTRBonus(strBonus);
			blood.setHolyWeaponMAGBonus(magBonus);
			blood.setHolyWeaponSKLBonus(sklBonus);
			blood.setHolyWeaponSPDBonus(spdBonus);
			blood.setHolyWeaponDEFBonus(defBonus);
			blood.setHolyWeaponRESBonus(resBonus);
		}
	}
	
	public static void assignHolyBlood(int majorBloodChance, boolean matchClass, CharacterDataLoader charData, ItemMapper itemMap, Random rng) {
		List<FE4StaticCharacter> characterList = new ArrayList<FE4StaticCharacter>(charData.getGen1Characters());
		characterList.addAll(charData.getGen2CommonCharacters());
		characterList.addAll(charData.getGen2SubstituteCharacters());
		
		Set<Integer> idsProcessed = new HashSet<Integer>();
		
		for (FE4StaticCharacter staticChar : characterList) {
			if (idsProcessed.contains(staticChar.getCharacterID())) { continue; }
			
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
			
			List<FE4Data.HolyBloodSlot1> slot1Blood = FE4Data.HolyBloodSlot1.slot1HolyBlood(staticChar.getHolyBlood1Value());
			List<FE4Data.HolyBloodSlot2> slot2Blood = FE4Data.HolyBloodSlot2.slot2HolyBlood(staticChar.getHolyBlood2Value());
			List<FE4Data.HolyBloodSlot3> slot3Blood = FE4Data.HolyBloodSlot3.slot3HolyBlood(staticChar.getHolyBlood3Value());
			
			boolean hasMajorBlood = false;
			if (slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) { hasMajorBlood = true; }
			if (!hasMajorBlood && slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) { hasMajorBlood = true; }
			if (!hasMajorBlood && slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) { hasMajorBlood = true; }
			
			boolean hasMinorBlood = false;
			FE4Data.HolyBlood minorBloodType = null;
			if (slot1Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent()) {
				minorBloodType = slot1Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().get().bloodType();
				hasMajorBlood = true;
			}
			if (!hasMajorBlood && slot2Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent()) {
				minorBloodType = slot2Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().get().bloodType();
				hasMajorBlood = true;
			}
			if (!hasMajorBlood && slot3Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent()) {
				minorBloodType = slot3Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().get().bloodType();
				hasMajorBlood = true;
			}
			
			if (hasMajorBlood) { continue; }
			
			if (rng.nextInt(100) < majorBloodChance) {
				// assign major blood
				FE4Data.HolyBlood majorBlood = null;
				if (hasMinorBlood) {
					majorBlood = minorBloodType;
					FE4Data.HolyBloodSlot1 slot1 = FE4Data.HolyBloodSlot1.blood(minorBloodType, true);
					FE4Data.HolyBloodSlot2 slot2 = FE4Data.HolyBloodSlot2.blood(minorBloodType, true);
					FE4Data.HolyBloodSlot3 slot3 = FE4Data.HolyBloodSlot3.blood(minorBloodType, true);
					if (slot1 != null) {
						slot1Blood.remove(FE4Data.HolyBloodSlot1.blood(minorBloodType, false));
						slot1Blood.add(slot1);
					} else if (slot2 != null) {
						slot2Blood.remove(FE4Data.HolyBloodSlot2.blood(minorBloodType, false));
						slot2Blood.add(slot2);
					} else if (slot3 != null) {
						slot3Blood.remove(FE4Data.HolyBloodSlot3.blood(minorBloodType, false));
						slot3Blood.add(slot3);
					}
				} else {
					FE4Data.HolyBlood[] bloodChoices = null;
					if (matchClass) {
						FE4Data.CharacterClass charClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID());
						bloodChoices = charClass.supportedHolyBlood();
					} else {
						bloodChoices = FE4Data.HolyBlood.values();
					}
					
					Set<FE4Data.HolyBlood> bloodSet = new HashSet<FE4Data.HolyBlood>(Arrays.asList(bloodChoices));
					bloodSet.retainAll(Arrays.asList(fe4Char.limitedHolyBloodSelection()));
					List<FE4Data.HolyBlood> bloodList = new ArrayList<FE4Data.HolyBlood>(bloodSet);
					
					FE4Data.HolyBlood selectedBlood = bloodList.get(rng.nextInt(bloodChoices.length));
					
					FE4Data.HolyBloodSlot1 slot1 = FE4Data.HolyBloodSlot1.blood(selectedBlood, true);
					FE4Data.HolyBloodSlot2 slot2 = FE4Data.HolyBloodSlot2.blood(selectedBlood, true);
					FE4Data.HolyBloodSlot3 slot3 = FE4Data.HolyBloodSlot3.blood(selectedBlood, true);
					if (slot1 != null) { slot1Blood.add(slot1); }
					if (slot2 != null) { slot2Blood.add(slot2); }
					if (slot3 != null) { slot3Blood.add(slot3); }
					
					majorBlood = selectedBlood;
				}
				
				if (majorBlood != null) {
					// See if this character has any chance for receiving an item specifically for him/her.
					for (FE4Data.Character recipient : FE4Data.EventItemInventoryIDsByRecipient.keySet()) {
						// They also can't be weakly linked to other characters (since we can potentially change a weapon that works for both units).
						// This also only has a 50% chance of happening.
						if (recipient.ID == fe4Char.ID && !FE4Data.WeaklyLinkedCharacters.containsKey(fe4Char) && rng.nextInt(2) == 0) {
							List<Integer> inventoryIDs = FE4Data.EventItemInventoryIDsByRecipient.get(recipient);
							int inventoryID = inventoryIDs.get(inventoryIDs.size() - 1);
							itemMap.setItemAtIndex(inventoryID, majorBlood.holyWeapon);
						}
					}
				}
			} else {
				// assign minor blood
				FE4Data.HolyBlood[] bloodChoices = null;
				if (matchClass) {
					FE4Data.CharacterClass charClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID());
					bloodChoices = charClass.supportedHolyBlood();
				} else {
					bloodChoices = FE4Data.HolyBlood.values();
				}
				
				Set<FE4Data.HolyBlood> bloodSet = new HashSet<FE4Data.HolyBlood>(Arrays.asList(bloodChoices));
				
				if (hasMinorBlood) {
					bloodSet.remove(minorBloodType);
				}
				
				bloodSet.retainAll(Arrays.asList(fe4Char.limitedHolyBloodSelection()));
				
				if (bloodSet.isEmpty()) {
					bloodSet = new HashSet<FE4Data.HolyBlood>(Arrays.asList(FE4Data.HolyBlood.values()));
					bloodSet.remove(minorBloodType);
				}
				List<FE4Data.HolyBlood> bloodList = new ArrayList<FE4Data.HolyBlood>(bloodSet);
				
				FE4Data.HolyBlood selectedBlood = bloodList.get(rng.nextInt(bloodList.size()));
				
				FE4Data.HolyBloodSlot1 slot1 = FE4Data.HolyBloodSlot1.blood(selectedBlood, false);
				FE4Data.HolyBloodSlot2 slot2 = FE4Data.HolyBloodSlot2.blood(selectedBlood, false);
				FE4Data.HolyBloodSlot3 slot3 = FE4Data.HolyBloodSlot3.blood(selectedBlood, false);
				if (slot1 != null) { slot1Blood.add(slot1); }
				if (slot2 != null) { slot2Blood.add(slot2); }
				if (slot3 != null) { slot3Blood.add(slot3); }
			}
			
			for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
				FE4StaticCharacter character = charData.getStaticCharacter(linked);
				character.setHolyBlood1Value(FE4Data.HolyBloodSlot1.valueForSlot1HolyBlood(slot1Blood));
				character.setHolyBlood2Value(FE4Data.HolyBloodSlot2.valueForSlot2HolyBlood(slot2Blood));
				character.setHolyBlood3Value(FE4Data.HolyBloodSlot3.valueForSlot3HolyBlood(slot3Blood));	
				idsProcessed.add(character.getCharacterID());
			}
		}
		
		charData.commit();
	}
}
