package random.snes.fe4.randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import fedata.snes.fe4.FE4Class;
import fedata.snes.fe4.FE4Data;
import fedata.snes.fe4.FE4Data.Item;
import fedata.snes.fe4.FE4EnemyCharacter;
import fedata.snes.fe4.FE4StaticCharacter;
import random.snes.fe4.loader.CharacterDataLoader;
import random.snes.fe4.loader.ClassDataLoader;
import random.snes.fe4.loader.ItemMapper;
import ui.model.fe4.FE4EnemyBuffOptions;
import ui.model.fe4.FE4EnemyBuffOptions.EnemyScalingOptions;

public class FE4EnemyBuffer {
	
	static final int rngSalt = 9600;
	
	public static void buffEnemyStats(FE4EnemyBuffOptions options, CharacterDataLoader charData, ClassDataLoader classData) {
		// Increase class growths to account for minions and plain bosses.
		for (FE4Class classObject : classData.allValidClasses()) {
			if (options.scalingOption == EnemyScalingOptions.FLAT) {
				classObject.setHPGrowth(classObject.getHPGrowth() + options.scalingAmount);
				classObject.setSTRGrowth(classObject.getSTRGrowth() + options.scalingAmount);
				classObject.setMAGGrowth(classObject.getMAGGrowth() + options.scalingAmount);
				classObject.setSKLGrowth(classObject.getSKLGrowth() + options.scalingAmount);
				classObject.setSPDGrowth(classObject.getSPDGrowth() + options.scalingAmount);
				classObject.setDEFGrowth(classObject.getDEFGrowth() + options.scalingAmount);
				classObject.setRESGrowth(classObject.getRESGrowth() + options.scalingAmount);
			} else {
				classObject.setHPGrowth((int)Math.floor(classObject.getHPGrowth() * (options.scalingAmount / 100.0 + 1)));
				classObject.setSTRGrowth((int)Math.floor(classObject.getSTRGrowth() * (options.scalingAmount / 100.0 + 1)));
				classObject.setMAGGrowth((int)Math.floor(classObject.getMAGGrowth() * (options.scalingAmount / 100.0 + 1)));
				classObject.setSKLGrowth((int)Math.floor(classObject.getSKLGrowth() * (options.scalingAmount / 100.0 + 1)));
				classObject.setSPDGrowth((int)Math.floor(classObject.getSPDGrowth() * (options.scalingAmount / 100.0 + 1)));
				classObject.setDEFGrowth((int)Math.floor(classObject.getDEFGrowth() * (options.scalingAmount / 100.0 + 1)));
				classObject.setRESGrowth((int)Math.floor(classObject.getRESGrowth() * (options.scalingAmount / 100.0 + 1)));
			}
		}
		
		for (FE4StaticCharacter holyBoss : charData.getHolyBossCharacters()) {
			if (options.scalingOption == EnemyScalingOptions.FLAT) {
				int increaseAmount = (int)Math.ceil(options.scalingAmount / 10.0);
				
				holyBoss.setBaseHP(holyBoss.getBaseHP() + increaseAmount * 2);
				holyBoss.setBaseSTR(holyBoss.getBaseSTR() + increaseAmount);
				holyBoss.setBaseMAG(holyBoss.getBaseMAG() + increaseAmount);
				holyBoss.setBaseSKL(holyBoss.getBaseSKL() + increaseAmount);
				holyBoss.setBaseSPD(holyBoss.getBaseSPD() + increaseAmount);
				holyBoss.setBaseDEF(holyBoss.getBaseDEF() + increaseAmount);
				holyBoss.setBaseRES(holyBoss.getBaseRES() + increaseAmount);
				holyBoss.setBaseLCK(holyBoss.getBaseLCK() + increaseAmount);
			} else {
				holyBoss.setBaseHP((int)Math.floor(holyBoss.getBaseHP() * (options.scalingAmount / 100.0 + 1)));
				holyBoss.setBaseSTR((int)Math.floor(holyBoss.getBaseSTR() * (options.scalingAmount / 100.0 + 1)));
				holyBoss.setBaseMAG((int)Math.floor(holyBoss.getBaseMAG() * (options.scalingAmount / 100.0 + 1)));
				holyBoss.setBaseSKL((int)Math.floor(holyBoss.getBaseSKL() * (options.scalingAmount / 100.0 + 1)));
				holyBoss.setBaseSPD((int)Math.floor(holyBoss.getBaseSPD() * (options.scalingAmount / 100.0 + 1)));
				holyBoss.setBaseDEF((int)Math.floor(holyBoss.getBaseDEF() * (options.scalingAmount / 100.0 + 1)));
				holyBoss.setBaseRES((int)Math.floor(holyBoss.getBaseRES() * (options.scalingAmount / 100.0 + 1)));
				holyBoss.setBaseLCK((int)Math.floor(holyBoss.getBaseLCK() * (options.scalingAmount / 100.0 + 1)));
			}
		}
	}

	public static void improveEquipment(FE4EnemyBuffOptions options, CharacterDataLoader charData, Random rng) {
		for (FE4EnemyCharacter enemy : charData.getMinions()) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(enemy.getCharacterID());
			if (fe4Char.minionChapter() > 0 && rng.nextInt(100) < options.improvementChance) {
				int item1ID = enemy.getEquipment1();
				FE4Data.Item item1 = FE4Data.Item.valueOf(item1ID);
				if (item1 == null) { continue; }
				if (item1.isWeapon()) {
					Set<FE4Data.Item> choices = new HashSet<FE4Data.Item>();
					FE4Data.CharacterClass fe4Class = FE4Data.CharacterClass.valueOf(enemy.getClassID());
					choices.addAll(Arrays.asList(fe4Class.usableItems(null, null, null)));
					if (choices.stream().filter(item -> (item.getRank().isHigher(item1.getRank()))).count() > 0) {
						choices.removeIf(item -> (item.getRank().isHigher(item1.getRank()) == false));
					} else if (!Collections.disjoint(choices, FE4Data.Item.interestingWeapons)) {
						choices.retainAll(FE4Data.Item.interestingWeapons);
					} else if (!Collections.disjoint(choices, FE4Data.Item.powerfulWeapons)) {
						choices.retainAll(FE4Data.Item.powerfulWeapons);
					}
					
					if (enemy.isFemale() == false) { choices.removeAll(FE4Data.Item.femaleOnlyWeapons); }
					choices.removeAll(FE4Data.Item.playerOnlySet);
					
					List<FE4Data.Item> itemList = choices.stream().sorted(new Comparator<FE4Data.Item>() {
						@Override
						public int compare(Item o1, Item o2) {
							return Integer.compare(o1.ID, o2.ID);
						}
					}).collect(Collectors.toList());
					
					if (!itemList.isEmpty()) {
						enemy.setEquipment1(itemList.get(rng.nextInt(itemList.size())).ID);
					}
				}
			}
		}
	}
	
	private static class HolyBossDrop {
		FE4StaticCharacter holyBoss;
		FE4Data.Item holyWeapon;
		
		private HolyBossDrop(FE4StaticCharacter boss, FE4Data.Item drop) {
			holyBoss = boss;
			holyWeapon = drop;
		}
	}
	
	public static void forceMajorBloodOnHolyBosses(FE4EnemyBuffOptions options, boolean useFreeInventoryForDrops, CharacterDataLoader charData, ItemMapper itemMap, Random rng) {
		List<HolyBossDrop> potentialDrops = new ArrayList<HolyBossDrop>();
		Map<FE4Data.Character, FE4Data.HolyBlood> assignedBlood = new HashMap<FE4Data.Character, FE4Data.HolyBlood>();
		
		for (FE4StaticCharacter holyBoss : charData.getHolyBossCharacters()) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(holyBoss.getCharacterID());
			
			List<FE4Data.HolyBloodSlot1> slot1Blood = FE4Data.HolyBloodSlot1.slot1HolyBlood(holyBoss.getHolyBlood1Value());
			List<FE4Data.HolyBloodSlot2> slot2Blood = FE4Data.HolyBloodSlot2.slot2HolyBlood(holyBoss.getHolyBlood2Value());
			List<FE4Data.HolyBloodSlot3> slot3Blood = FE4Data.HolyBloodSlot3.slot3HolyBlood(holyBoss.getHolyBlood3Value());
			
			if (holyBoss.getCharacterID() == FE4Data.Character.JULIUS_CH10.ID) { continue; }
			if (holyBoss.getCharacterID() == FE4Data.Character.JULIUS_FINAL.ID) { continue; }
			// Just to make sure Ch. 6 doesn't start out crazy, Danann shouldn't have one either.
			if (holyBoss.getCharacterID() == FE4Data.Character.DANANN.ID) { continue; }
			// Byron shouldn't have one for obvious reasons.
			if (holyBoss.getCharacterID() == FE4Data.Character.BYRON.ID) { continue; }
			
			List<FE4Data.HolyBlood> bloodList = new ArrayList<FE4Data.HolyBlood>();
			bloodList.addAll(slot1Blood.stream().map(blood -> (blood.bloodType())).distinct().collect(Collectors.toList()));
			bloodList.addAll(slot2Blood.stream().map(blood -> (blood.bloodType())).distinct().collect(Collectors.toList()));
			bloodList.addAll(slot3Blood.stream().map(blood -> (blood.bloodType())).distinct().collect(Collectors.toList()));
			
			slot1Blood.clear();
			slot2Blood.clear();
			slot3Blood.clear();
			
			List<FE4Data.Item> holyWeapons = new ArrayList<FE4Data.Item>(); 
			
			if (bloodList.isEmpty()) {
				if (assignedBlood.get(fe4Char) != null) {
					bloodList.add(assignedBlood.get(fe4Char));
				} else if (FE4Data.Character.HolyBossesThatReceiveNewHolyBlood.contains(fe4Char)) {
					FE4Data.CharacterClass charClass = FE4Data.CharacterClass.valueOf(holyBoss.getClassID());
					List<FE4Data.HolyBlood> bloodChoices = new ArrayList<FE4Data.HolyBlood>(Arrays.asList(charClass.supportedHolyBlood()));
					Collections.sort(bloodChoices, FE4Data.HolyBlood.defaultComparator);
					bloodChoices.remove(FE4Data.HolyBlood.NAGA);
					bloodChoices.remove(FE4Data.HolyBlood.BRAGI);
					if (!bloodChoices.isEmpty()) {
						FE4Data.HolyBlood newBlood = bloodChoices.get(rng.nextInt(bloodChoices.size()));
						bloodList.add(newBlood);
						for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
							assignedBlood.put(linked, newBlood);
						}
					}
				}
			}
			
			for (FE4Data.HolyBlood blood : bloodList) {
				FE4Data.HolyBloodSlot1 slot1 = FE4Data.HolyBloodSlot1.blood(blood, true);
				FE4Data.HolyBloodSlot2 slot2 = FE4Data.HolyBloodSlot2.blood(blood, true);
				FE4Data.HolyBloodSlot3 slot3 = FE4Data.HolyBloodSlot3.blood(blood, true);
				
				if (slot1 != null) { slot1Blood.add(slot1); }
				else if (slot2 != null) { slot2Blood.add(slot2); }
				else if (slot3 != null) { slot3Blood.add(slot3); }
				else { continue; }
				
				holyWeapons.add(blood.holyWeapon);
			}
			
			FE4Data.CharacterClass fe4Class = FE4Data.CharacterClass.valueOf(holyBoss.getClassID());
			Set<FE4Data.Item> usableByClass = new HashSet<FE4Data.Item>(Arrays.asList(fe4Class.usableItems(slot1Blood, slot2Blood, slot3Blood))); 
			
			holyBoss.setHolyBlood1Value(FE4Data.HolyBloodSlot1.valueForSlot1HolyBlood(slot1Blood));
			holyBoss.setHolyBlood2Value(FE4Data.HolyBloodSlot2.valueForSlot2HolyBlood(slot2Blood));
			holyBoss.setHolyBlood3Value(FE4Data.HolyBloodSlot3.valueForSlot3HolyBlood(slot3Blood));
			
			holyWeapons.retainAll(usableByClass);
			
			if (!holyWeapons.isEmpty()) {
				FE4Data.Item randomHolyWeapon = holyWeapons.get(rng.nextInt(holyWeapons.size()));
				holyBoss.setEquipment1(randomHolyWeapon.ID);
				
				// Make sure Sigurd's holy weapon doesn't drop in Gen 1.
				FE4Data.Item sigurdHolyWeapon = itemMap.getItemAtIndex(FE4Data.HolyWeaponInventoryIDs.get(FE4Data.Item.TYRFING));
				boolean canDrop = (randomHolyWeapon.ID != sigurdHolyWeapon.ID) || fe4Char.isGen2();
				
				if (canDrop) {
					if (useFreeInventoryForDrops && FE4Data.Character.HolyBossesThatCanDropHolyWeapons.contains(fe4Char)) {
						if (holyBoss.getEquipment3() != FE4Data.Item.NONE.ID) {
							// Clear it out and add it to the list of potential drops.
							potentialDrops.add(new HolyBossDrop(holyBoss, randomHolyWeapon));
							itemMap.freeInventoryID(holyBoss.getEquipment3());
							holyBoss.setEquipment3(FE4Data.Item.NONE.ID);
						} else {
							potentialDrops.add(new HolyBossDrop(holyBoss, randomHolyWeapon));
						}
					} else if (holyBoss.getEquipment3() != FE4Data.Item.NONE.ID && FE4Data.Character.HolyBossesWithFreeDrops.contains(fe4Char)) {
						itemMap.setItemAtIndex(holyBoss.getEquipment3(), randomHolyWeapon);
					}
				}
			}
		}
		
		int totalDrops = potentialDrops.size();
		for (int i = 0; i < totalDrops; i++) {
			HolyBossDrop drop = potentialDrops.get(rng.nextInt(potentialDrops.size()));
			potentialDrops.remove(drop);
			Integer inventoryID = itemMap.obtainFreeInventoryID(drop.holyWeapon);
			if (inventoryID == null) { break; } // We only have a finite set of these, so if we run out, we're done.
			drop.holyBoss.setEquipment3(inventoryID);
		}
	}
	
	
}
