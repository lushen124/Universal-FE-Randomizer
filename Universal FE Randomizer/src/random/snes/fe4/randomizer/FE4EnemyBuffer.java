package random.snes.fe4.randomizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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
import ui.fe4.FE4EnemyBuffOptions;
import ui.fe4.FE4EnemyBuffOptions.EnemyScalingOptions;

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
	
	public static void forceMajorBloodOnHolyBosses(FE4EnemyBuffOptions options, CharacterDataLoader charData, ItemMapper itemMap, Random rng) {
		for (FE4StaticCharacter holyBoss : charData.getHolyBossCharacters()) {
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
				
				FE4Data.Character fe4Char = FE4Data.Character.valueOf(holyBoss.getCharacterID());
				if (holyBoss.getEquipment3() != FE4Data.Item.NONE.ID && FE4Data.Character.HolyBossesWithFreeDrops.contains(fe4Char)) {
					// If this boss drops an item, make him/her drop the holy weapon.
					itemMap.setItemAtIndex(holyBoss.getEquipment3(), randomHolyWeapon);
				}
			}
		}
	}
}
