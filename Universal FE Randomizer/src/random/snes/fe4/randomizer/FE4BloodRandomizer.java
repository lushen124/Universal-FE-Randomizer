package random.snes.fe4.randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import fedata.snes.fe4.FE4Data;
import fedata.snes.fe4.FE4HolyBlood;
import fedata.snes.fe4.FE4StaticCharacter;
import fedata.snes.fe4.FE4Data.HolyBloodSlot1;
import fedata.snes.fe4.FE4Data.HolyBloodSlot2;
import fedata.snes.fe4.FE4Data.HolyBloodSlot3;
import random.general.WeightedDistributor;
import random.snes.fe4.loader.CharacterDataLoader;
import random.snes.fe4.loader.HolyBloodLoader;
import random.snes.fe4.loader.ItemMapper;
import ui.model.fe4.HolyBloodOptions;
import ui.model.fe4.HolyBloodOptions.STRMAGOptions;

public class FE4BloodRandomizer {
	
	static final int rngSalt = 12321;
	
	public static void randomizeHolyBloodGrowthBonuses(HolyBloodOptions options, HolyBloodLoader bloodData, CharacterDataLoader charData, Random rng) {
		List<FE4HolyBlood> bloodList = bloodData.allHolyBlood();
		Set<Integer> generatedHashes = new HashSet<Integer>();
		
		transferBloodToGrowths(charData, bloodData);
		
		for (FE4HolyBlood blood : bloodList) {
			int growthRemaining = options.growthTotal;
			growthRemaining -= options.hpBaseline;
			
			int hpBonus = options.hpBaseline;
			int strBonus = 0;
			int magBonus = 0;
			int sklBonus = 0;
			int spdBonus = 0;
			int lckBonus = 0;
			int defBonus = 0;
			int resBonus = 0;
			
			boolean reducedHPChance = options.hpBaseline > 0;
			
			WeightedDistributor<Integer> distributor = new WeightedDistributor<Integer>();
			if (reducedHPChance) { distributor.addItem(0, 1); }
			else { distributor.addItem(0, 3); }
			distributor.addItem(1, 3);
			distributor.addItem(2, 3);
			distributor.addItem(3, 3);
			distributor.addItem(4, 3);
			distributor.addItem(5, 3);
			distributor.addItem(6, 3);
			
			int attempt = 0;
			
			while (growthRemaining > 0) {
				int amount = Math.min(growthRemaining, options.chunkSize);
				growthRemaining -= amount;
				int field = distributor.getRandomItem(rng);
				
				switch (field) {
				case 0:
					hpBonus += amount;
					break;
				case 1:
					if (options.strMagOptions == STRMAGOptions.LIMIT_STR_MAG) {
						if (blood.getWeaponType().isPhysical()) { strBonus += amount; }
						else { magBonus += amount; }
					} else {
						if (rng.nextInt(2) == 0) { strBonus += amount; }
						else { magBonus += amount; }
					}
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
				
				// Lean towards what we've already pulled (up to 50%)
				if (distributor.chanceOfResult(field) < 0.5) {
					distributor.addItem(field, 2);
				}
				
				if (growthRemaining <= 0) {
					if (options.strMagOptions == STRMAGOptions.ADJUST_STR_MAG) {
						if ((blood.getWeaponType().isPhysical() && strBonus < magBonus) || (!blood.getWeaponType().isPhysical() && strBonus > magBonus)) {
							int swap = strBonus;
							strBonus = magBonus;
							magBonus = swap;
						}
					}
					if (options.generateUniqueBonuses) {
						// Verify hash to see if we've already generated this combination.
						int hash = hashValue(reducedHPChance ? 0 : hpBonus, strBonus, magBonus, sklBonus, spdBonus, lckBonus, defBonus, resBonus);
						// We give it 20 tries to try to get a unique one, In case there aren't enough combinations to go around.
						if (generatedHashes.contains(hash) && attempt < 20) {
							attempt++;
							// Reset
							growthRemaining = options.growthTotal - options.hpBaseline;
							hpBonus = options.hpBaseline;
							strBonus = 0;
							magBonus = 0;
							sklBonus = 0;
							spdBonus = 0;
							lckBonus = 0;
							defBonus = 0;
							resBonus = 0;
						} else {
							generatedHashes.add(hash);
						}
					}
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
		
		transferGrowthsToBlood(charData, bloodData);
	}
	
	private static void transferBloodToGrowths(CharacterDataLoader charData, HolyBloodLoader bloodData) {
		for (FE4StaticCharacter staticGen1 : charData.getGen1Characters()) {
			bloodGrowthsTransfer(staticGen1, bloodData, false);
		}
		for (FE4StaticCharacter staticGen2 : charData.getGen2CommonCharacters()) {
			bloodGrowthsTransfer(staticGen2, bloodData, false);
		}
		for (FE4StaticCharacter subChar : charData.getGen2SubstituteCharacters()) {
			bloodGrowthsTransfer(subChar, bloodData, false);
		}
		// Holy bosses are also static characters, but their growths don't really matter...
	}
	
	private static void transferGrowthsToBlood(CharacterDataLoader charData, HolyBloodLoader bloodData) {
		for (FE4StaticCharacter staticGen1 : charData.getGen1Characters()) {
			bloodGrowthsTransfer(staticGen1, bloodData, true);
		}
		for (FE4StaticCharacter staticGen2 : charData.getGen2CommonCharacters()) {
			bloodGrowthsTransfer(staticGen2, bloodData, true);
		}
		for (FE4StaticCharacter subChar : charData.getGen2SubstituteCharacters()) {
			bloodGrowthsTransfer(subChar, bloodData, true);
		}
	}
	
	private static void bloodGrowthsTransfer(FE4StaticCharacter staticChar, HolyBloodLoader bloodData, boolean subtract) {
		List<FE4Data.HolyBloodSlot1> slot1Blood = FE4Data.HolyBloodSlot1.slot1HolyBlood(staticChar.getHolyBlood1Value());
		List<FE4Data.HolyBloodSlot2> slot2Blood = FE4Data.HolyBloodSlot2.slot2HolyBlood(staticChar.getHolyBlood2Value());
		List<FE4Data.HolyBloodSlot3> slot3Blood = FE4Data.HolyBloodSlot3.slot3HolyBlood(staticChar.getHolyBlood3Value());
		
		slot1Blood.stream().forEach(fe4Blood -> {
			int multiplier = fe4Blood.isMajor() ? 2 : 1;
			if (subtract) { multiplier *= -1; }
			
			FE4HolyBlood blood = bloodData.holyBloodByType(fe4Blood.bloodType());
			staticChar.setHPGrowth(Math.max(0, staticChar.getHPGrowth() + blood.getHPGrowthBonus() * multiplier));
			staticChar.setSTRGrowth(Math.max(0, staticChar.getSTRGrowth() + blood.getSTRGrowthBonus() * multiplier));
			staticChar.setMAGGrowth(Math.max(0, staticChar.getMAGGrowth() + blood.getMAGGrowthBonus() * multiplier));
			staticChar.setSKLGrowth(Math.max(0, staticChar.getSKLGrowth() + blood.getSKLGrowthBonus() * multiplier));
			staticChar.setSPDGrowth(Math.max(0, staticChar.getSPDGrowth() + blood.getSPDGrowthBonus() * multiplier));
			staticChar.setDEFGrowth(Math.max(0, staticChar.getDEFGrowth() + blood.getDEFGrowthBonus() * multiplier));
			staticChar.setRESGrowth(Math.max(0, staticChar.getRESGrowth() + blood.getRESGrowthBonus() * multiplier));
			staticChar.setLCKGrowth(Math.max(0, staticChar.getLCKGrowth() + blood.getLCKGrowthBonus() * multiplier));
		});
		
		slot2Blood.stream().forEach(fe4Blood -> {
			int multiplier = fe4Blood.isMajor() ? 2 : 1;
			if (subtract) { multiplier *= -1; }
			
			FE4HolyBlood blood = bloodData.holyBloodByType(fe4Blood.bloodType());
			staticChar.setHPGrowth(Math.max(0, staticChar.getHPGrowth() + blood.getHPGrowthBonus() * multiplier));
			staticChar.setSTRGrowth(Math.max(0, staticChar.getSTRGrowth() + blood.getSTRGrowthBonus() * multiplier));
			staticChar.setMAGGrowth(Math.max(0, staticChar.getMAGGrowth() + blood.getMAGGrowthBonus() * multiplier));
			staticChar.setSKLGrowth(Math.max(0, staticChar.getSKLGrowth() + blood.getSKLGrowthBonus() * multiplier));
			staticChar.setSPDGrowth(Math.max(0, staticChar.getSPDGrowth() + blood.getSPDGrowthBonus() * multiplier));
			staticChar.setDEFGrowth(Math.max(0, staticChar.getDEFGrowth() + blood.getDEFGrowthBonus() * multiplier));
			staticChar.setRESGrowth(Math.max(0, staticChar.getRESGrowth() + blood.getRESGrowthBonus() * multiplier));
			staticChar.setLCKGrowth(Math.max(0, staticChar.getLCKGrowth() + blood.getLCKGrowthBonus() * multiplier));
		});
		
		slot3Blood.stream().forEach(fe4Blood -> {
			int multiplier = fe4Blood.isMajor() ? 2 : 1;
			if (subtract) { multiplier *= -1; }
			
			FE4HolyBlood blood = bloodData.holyBloodByType(fe4Blood.bloodType());
			staticChar.setHPGrowth(Math.max(0, staticChar.getHPGrowth() + blood.getHPGrowthBonus() * multiplier));
			staticChar.setSTRGrowth(Math.max(0, staticChar.getSTRGrowth() + blood.getSTRGrowthBonus() * multiplier));
			staticChar.setMAGGrowth(Math.max(0, staticChar.getMAGGrowth() + blood.getMAGGrowthBonus() * multiplier));
			staticChar.setSKLGrowth(Math.max(0, staticChar.getSKLGrowth() + blood.getSKLGrowthBonus() * multiplier));
			staticChar.setSPDGrowth(Math.max(0, staticChar.getSPDGrowth() + blood.getSPDGrowthBonus() * multiplier));
			staticChar.setDEFGrowth(Math.max(0, staticChar.getDEFGrowth() + blood.getDEFGrowthBonus() * multiplier));
			staticChar.setRESGrowth(Math.max(0, staticChar.getRESGrowth() + blood.getRESGrowthBonus() * multiplier));
			staticChar.setLCKGrowth(Math.max(0, staticChar.getLCKGrowth() + blood.getLCKGrowthBonus() * multiplier));
		});
	}
	
	private static Integer hashValue(int hp, int str, int mag, int skl, int spd, int lck, int def, int res) {
		List<Integer> stats = Arrays.asList(hp, str, mag, skl, spd, lck, def, res);
		Collections.sort(stats);
		
		int hashValue = stats.indexOf(hp) * (int)Math.pow(10, 0) +
				stats.indexOf(str) * (int)Math.pow(10, 1) +
				stats.indexOf(mag) * (int)Math.pow(10, 2) +
				stats.indexOf(skl) * (int)Math.pow(10, 3) +
				stats.indexOf(spd) * (int)Math.pow(10, 4) +
				stats.indexOf(def) * (int)Math.pow(10, 5) +
				stats.indexOf(res) * (int)Math.pow(10, 6) +
				stats.indexOf(lck) * (int)Math.pow(10, 7)
				;
		return hashValue;
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
	
	public static void assignHolyBlood(int majorBloodChance, int minorBloodChance, boolean matchClass, CharacterDataLoader charData, HolyBloodLoader bloodData, ItemMapper itemMap, Random rng) {
		List<FE4StaticCharacter> characterList = new ArrayList<FE4StaticCharacter>(charData.getGen1Characters());
		characterList.addAll(charData.getGen2CommonCharacters());
		characterList.addAll(charData.getGen2SubstituteCharacters());
		
		Set<Integer> idsProcessed = new HashSet<Integer>();
		
		Set<FE4Data.HolyBlood> restrictedBlood = new HashSet<FE4Data.HolyBlood>(); 
		
		for (FE4Data.Character uniqueChar : FE4Data.Character.CharactersRequiringUniqueBlood) {
			FE4StaticCharacter unique = charData.getStaticCharacter(uniqueChar);
			if (unique == null) { continue; }
			
			List<HolyBloodSlot1> restrictedSlot1 = FE4Data.HolyBloodSlot1.slot1HolyBlood(unique.getHolyBlood1Value());
			List<HolyBloodSlot2> restrictedSlot2 = FE4Data.HolyBloodSlot2.slot2HolyBlood(unique.getHolyBlood2Value());
			List<HolyBloodSlot3> restrictedSlot3 = FE4Data.HolyBloodSlot3.slot3HolyBlood(unique.getHolyBlood3Value());
			
			if (restrictedSlot1.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) { restrictedBlood.add(restrictedSlot1.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType()); }
			if (restrictedSlot2.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) { restrictedBlood.add(restrictedSlot2.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType()); }
			if (restrictedSlot3.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) { restrictedBlood.add(restrictedSlot3.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType()); }
		}
		
		for (FE4StaticCharacter staticChar : characterList) {
			if (idsProcessed.contains(staticChar.getCharacterID())) { continue; }
			
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
			FE4Data.CharacterClass targetClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID());
			
			List<FE4Data.HolyBloodSlot1> slot1Blood = FE4Data.HolyBloodSlot1.slot1HolyBlood(staticChar.getHolyBlood1Value());
			List<FE4Data.HolyBloodSlot2> slot2Blood = FE4Data.HolyBloodSlot2.slot2HolyBlood(staticChar.getHolyBlood2Value());
			List<FE4Data.HolyBloodSlot3> slot3Blood = FE4Data.HolyBloodSlot3.slot3HolyBlood(staticChar.getHolyBlood3Value());
			
			boolean hasMajorBlood = false;
			if (slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) { hasMajorBlood = true; }
			if (!hasMajorBlood && slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) { hasMajorBlood = true; }
			if (!hasMajorBlood && slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) { hasMajorBlood = true; }
			
			if (hasMajorBlood) { continue; }
			
			boolean hasMinorBlood = false;
			FE4Data.HolyBlood minorBloodType = null;
			if (slot1Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent()) {
				minorBloodType = slot1Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().get().bloodType();
				hasMinorBlood = true;
			}
			if (!hasMinorBlood && slot2Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent()) {
				minorBloodType = slot2Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().get().bloodType();
				hasMinorBlood = true;
			}
			if (!hasMinorBlood && slot3Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent()) {
				minorBloodType = slot3Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().get().bloodType();
				hasMinorBlood = true;
			}
			
			int rngValue = rng.nextInt(100);
			
			if (rngValue < majorBloodChance) {
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
					
					// Adjust personal growths downwards to compensate.
					staticChar.setHPGrowth(staticChar.getHPGrowth() - bloodData.holyBloodByType(majorBlood).getHPGrowthBonus());
					staticChar.setSTRGrowth(staticChar.getSTRGrowth() - bloodData.holyBloodByType(majorBlood).getSTRGrowthBonus());
					staticChar.setMAGGrowth(staticChar.getMAGGrowth() - bloodData.holyBloodByType(majorBlood).getMAGGrowthBonus());
					staticChar.setSKLGrowth(staticChar.getSKLGrowth() - bloodData.holyBloodByType(majorBlood).getSKLGrowthBonus());
					staticChar.setSPDGrowth(staticChar.getSPDGrowth() - bloodData.holyBloodByType(majorBlood).getSPDGrowthBonus());
					staticChar.setDEFGrowth(staticChar.getDEFGrowth() - bloodData.holyBloodByType(majorBlood).getDEFGrowthBonus());
					staticChar.setRESGrowth(staticChar.getRESGrowth() - bloodData.holyBloodByType(majorBlood).getRESGrowthBonus());
					staticChar.setLCKGrowth(staticChar.getLCKGrowth() - bloodData.holyBloodByType(majorBlood).getLCKGrowthBonus());
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
					bloodSet.remove(FE4Data.HolyBlood.NONE);
					List<FE4Data.HolyBlood> bloodList = new ArrayList<FE4Data.HolyBlood>(bloodSet);
					Collections.sort(bloodList, FE4Data.HolyBlood.defaultComparator);
					FE4Data.HolyBlood selectedBlood = bloodList.isEmpty() ? null : bloodList.get(rng.nextInt(bloodList.size()));
					
					FE4Data.HolyBloodSlot1 slot1 = FE4Data.HolyBloodSlot1.blood(selectedBlood, true);
					FE4Data.HolyBloodSlot2 slot2 = FE4Data.HolyBloodSlot2.blood(selectedBlood, true);
					FE4Data.HolyBloodSlot3 slot3 = FE4Data.HolyBloodSlot3.blood(selectedBlood, true);
					if (slot1 != null) { slot1Blood.add(slot1); }
					if (slot2 != null) { slot2Blood.add(slot2); }
					if (slot3 != null) { slot3Blood.add(slot3); }
					
					majorBlood = selectedBlood;
					
					// Adjust personal growths downwards to compensate.
					staticChar.setHPGrowth(staticChar.getHPGrowth() - bloodData.holyBloodByType(majorBlood).getHPGrowthBonus() * 2);
					staticChar.setSTRGrowth(staticChar.getSTRGrowth() - bloodData.holyBloodByType(majorBlood).getSTRGrowthBonus() * 2);
					staticChar.setMAGGrowth(staticChar.getMAGGrowth() - bloodData.holyBloodByType(majorBlood).getMAGGrowthBonus() * 2);
					staticChar.setSKLGrowth(staticChar.getSKLGrowth() - bloodData.holyBloodByType(majorBlood).getSKLGrowthBonus() * 2);
					staticChar.setSPDGrowth(staticChar.getSPDGrowth() - bloodData.holyBloodByType(majorBlood).getSPDGrowthBonus() * 2);
					staticChar.setDEFGrowth(staticChar.getDEFGrowth() - bloodData.holyBloodByType(majorBlood).getDEFGrowthBonus() * 2);
					staticChar.setRESGrowth(staticChar.getRESGrowth() - bloodData.holyBloodByType(majorBlood).getRESGrowthBonus() * 2);
					staticChar.setLCKGrowth(staticChar.getLCKGrowth() - bloodData.holyBloodByType(majorBlood).getLCKGrowthBonus() * 2);
				}
				
				if (majorBlood != null && 
						!restrictedBlood.contains(majorBlood) && 
						new HashSet<FE4Data.HolyBlood>(Arrays.asList(targetClass.supportedHolyBlood())).contains(majorBlood)) { // Only do this for characters getting major blood that aren't restricted.
					// See if this character has any chance for receiving an item specifically for him/her.
					for (FE4Data.Character recipient : FE4Data.EventItemInventoryIDsByRecipient.keySet()) {
						// They also can't be weakly linked to other characters (since we can potentially change a weapon that works for both units).
						// This also only has a 80% chance of happening.
						if (recipient.ID == fe4Char.ID && !FE4Data.WeaklyLinkedCharacters.containsKey(fe4Char) && rng.nextInt(5) != 0) {
							List<Integer> inventoryIDs = FE4Data.EventItemInventoryIDsByRecipient.get(recipient);
							int inventoryID = inventoryIDs.get(inventoryIDs.size() - 1);
							itemMap.setItemAtIndex(inventoryID, majorBlood.holyWeapon);
							break;
						}
					}
				}
			} else if (rngValue - majorBloodChance < minorBloodChance) {
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
					bloodSet.remove(FE4Data.HolyBlood.NONE);
					bloodSet.remove(minorBloodType);
				}
				List<FE4Data.HolyBlood> bloodList = new ArrayList<FE4Data.HolyBlood>(bloodSet);
				Collections.sort(bloodList, FE4Data.HolyBlood.defaultComparator);
				FE4Data.HolyBlood selectedBlood = bloodList.get(rng.nextInt(bloodList.size()));
				
				FE4Data.HolyBloodSlot1 slot1 = FE4Data.HolyBloodSlot1.blood(selectedBlood, false);
				FE4Data.HolyBloodSlot2 slot2 = FE4Data.HolyBloodSlot2.blood(selectedBlood, false);
				FE4Data.HolyBloodSlot3 slot3 = FE4Data.HolyBloodSlot3.blood(selectedBlood, false);
				if (slot1 != null) { slot1Blood.add(slot1); }
				if (slot2 != null) { slot2Blood.add(slot2); }
				if (slot3 != null) { slot3Blood.add(slot3); }
				
				// Adjust personal growths downwards to compensate.
				staticChar.setHPGrowth(staticChar.getHPGrowth() - bloodData.holyBloodByType(selectedBlood).getHPGrowthBonus());
				staticChar.setSTRGrowth(staticChar.getSTRGrowth() - bloodData.holyBloodByType(selectedBlood).getSTRGrowthBonus());
				staticChar.setMAGGrowth(staticChar.getMAGGrowth() - bloodData.holyBloodByType(selectedBlood).getMAGGrowthBonus());
				staticChar.setSKLGrowth(staticChar.getSKLGrowth() - bloodData.holyBloodByType(selectedBlood).getSKLGrowthBonus());
				staticChar.setSPDGrowth(staticChar.getSPDGrowth() - bloodData.holyBloodByType(selectedBlood).getSPDGrowthBonus());
				staticChar.setDEFGrowth(staticChar.getDEFGrowth() - bloodData.holyBloodByType(selectedBlood).getDEFGrowthBonus());
				staticChar.setRESGrowth(staticChar.getRESGrowth() - bloodData.holyBloodByType(selectedBlood).getRESGrowthBonus());
				staticChar.setLCKGrowth(staticChar.getLCKGrowth() - bloodData.holyBloodByType(selectedBlood).getLCKGrowthBonus());
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
