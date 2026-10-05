package random.gcnwii.fe9.randomizer;

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

import fedata.gba.GBAFEClassData;
import fedata.gcnwii.fe9.FE9ChapterArmy;
import fedata.gcnwii.fe9.FE9ChapterUnit;
import fedata.gcnwii.fe9.FE9Character;
import fedata.gcnwii.fe9.FE9Class;
import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9Item;
import fedata.gcnwii.fe9.FE9Skill;
import io.gcn.GCNCMBFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import random.gcnwii.fe9.loader.FE9ChapterDataLoader;
import random.gcnwii.fe9.loader.FE9CharacterDataLoader;
import random.gcnwii.fe9.loader.FE9ClassDataLoader;
import random.gcnwii.fe9.loader.FE9ItemDataLoader;
import random.gcnwii.fe9.loader.FE9ItemDataLoader.WeaponRank;
import random.gcnwii.fe9.loader.FE9ItemDataLoader.WeaponType;
import random.general.PoolDistributor;
import util.DebugPrinter;
import random.gcnwii.fe9.loader.FE9SkillDataLoader;
import random.gcnwii.fe9.loader.FE9ClassDataLoader.StatBias;

public class FE9ClassRandomizer {
	
	public static final int rngSalt = 2744;

	public static void randomizePlayableCharacters(boolean includeLords, boolean includeThieves, boolean includeSpecial, boolean forceDifferent,
			boolean mixRaces, boolean crossGenders, boolean assignEvenly, FE9CharacterDataLoader charData, FE9ClassDataLoader classData, FE9ChapterDataLoader chapterData, 
			FE9SkillDataLoader skillData, FE9ItemDataLoader itemData, Random rng) {
		
		Map<String, String> pidToJid = new HashMap<String, String>();
		
		boolean heronAssigned = false;
		
		PoolDistributor<FE9Class> classPool = new PoolDistributor<FE9Class>();
		classData.allValidClasses().stream().forEach(fe9Class -> classPool.addItem(fe9Class));
		
		for (FE9Character character : charData.allPlayableCharacters()) {
			if (!charData.isModifiableCharacter(character)) { continue; }
			
			String originalJID = charData.getJIDForCharacter(character);
			FE9Class originalClass = classData.classWithID(originalJID);
			boolean isFormerThief = classData.isThiefClass(originalClass);
			if (!includeLords && classData.isLordClass(originalClass)) { continue; }
			if (!includeThieves && classData.isThiefClass(originalClass)) { continue; }
			if (!includeSpecial && classData.isSpecialClass(originalClass)) { continue; }
			
			String pid = charData.getPIDForCharacter(character);
			
			String targetJID = pidToJid.get(pid);
			FE9Class newClass = null;
			if (targetJID != null) {
				charData.setJIDForCharacter(character, targetJID);
				newClass = classData.classWithID(targetJID);
			} else {
				List<FE9Class> possibleReplacements = possibleReplacementsForClass(originalClass, includeLords, includeThieves, includeSpecial, 
						forceDifferent, mixRaces, crossGenders, true, classData);
				if (heronAssigned) {
					possibleReplacements.removeIf(fe9class -> {
						return classData.getJIDForClass(fe9class).equals(FE9Data.CharacterClass.W_HERON.getJID());
					});
				}
				
				if (pid.equals(FE9Data.Character.KIERAN.getPID()) || pid.equals(FE9Data.Character.BROM.getPID()) || pid.equals(FE9Data.Character.NEPHENEE.getPID())) {
					// Kieran, Brom, and Nephenee start out in locked cells. Them being a thief will make their AI open doors automatically as Green units.
					possibleReplacements.removeIf(fe9Class -> {
						return classData.isThiefClass(fe9Class);
					});
				}
				
				DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Possible Classes: ");
				for (FE9Class charClass : possibleReplacements) {
					DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "\t" + classData.getJIDForClass(charClass));
				}
				
				if (rng.nextInt(4) == 0) {
					possibleReplacements.removeIf(fe9Class -> {
						return classData.isMountedClass(fe9Class);
					});
				}
				if (possibleReplacements.isEmpty()) { continue; }
				
				if (assignEvenly) {
					Set<FE9Class> replacementSet = new HashSet<FE9Class>(possibleReplacements);
					if (Collections.disjoint(replacementSet, classPool.possibleResults())) {
						classData.allValidClasses().stream().forEach(fe9Class -> classPool.addItem(fe9Class));
					}
					replacementSet.retainAll(classPool.possibleResults());
					List<FE9Class> classList = replacementSet.stream().sorted(new Comparator<FE9Class>() {
						@Override
						public int compare(FE9Class o1, FE9Class o2) {
							return classData.getJIDForClass(o1).compareTo(classData.getJIDForClass(o2));
						}
					}).collect(Collectors.toList());
					PoolDistributor<FE9Class> pool = new PoolDistributor<FE9Class>();
					for (FE9Class charClass : classList) {
						pool.addItem(charClass, classPool.itemCount(charClass));
					}
					newClass = pool.getRandomItem(rng, true);
					classPool.removeItem(newClass, false);
				} else {
					newClass = possibleReplacements.get(rng.nextInt(possibleReplacements.size()));
				}
				targetJID = classData.getJIDForClass(newClass);
				if (targetJID.equals(FE9Data.CharacterClass.W_HERON.getJID())) { 
					heronAssigned = true;
				}
				pidToJid.put(pid, targetJID);
				charData.setJIDForCharacter(character, targetJID);
				
				DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Randomized " + pid + " from " + originalJID + " to " + targetJID);
			}
			
			if (classData.isLaguzClass(newClass)) {
				int startingGauge = rng.nextInt(15) + 5;
				DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Set Laguz starting gauge to " + startingGauge);
				charData.setLaguzStartingGaugeForCharacter(character, startingGauge);
				if (classData.isLaguzClass(originalClass) && charData.getUnpromotedAIDForCharacter(character) != null) {
					DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Set unpromoted AID to " + classData.getUnpromotedAIDForClass(newClass));
					charData.setUnpromotedAIDForCharacter(character, classData.getUnpromotedAIDForClass(newClass));
				}
				DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Set promoted AID to " + classData.getPromotedAIDForClass(newClass));
				charData.setPromotedAIDForCharacter(character, classData.getPromotedAIDForClass(newClass));
			} else {
				DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Set unpromoted AID to " + classData.getUnpromotedAIDForClass(newClass));
				charData.setUnpromotedAIDForCharacter(character, classData.getUnpromotedAIDForClass(newClass));
				DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Set promoted AID to " + classData.getPromotedAIDForClass(newClass));
				charData.setPromotedAIDForCharacter(character, classData.getPromotedAIDForClass(newClass));
			}
			
			// Adjust bases and growths
			int hpBase = character.getBaseHP() + originalClass.getBaseHP() - newClass.getBaseHP();
			
			int strBase = 0;
			int magBase = 0;
			int strGrowth = character.getSTRGrowth();
			int magGrowth = character.getMAGGrowth();
			StatBias classStatBias = classData.statBiasForClass(newClass);
			if (classStatBias == StatBias.LEAN_PHYSICAL || classStatBias == StatBias.PHYSICAL_ONLY) {
				int effectiveSTR = character.getBaseSTR() + originalClass.getBaseSTR();
				int effectiveMAG = character.getBaseMAG() + originalClass.getBaseMAG();
				strBase = Math.max(effectiveSTR, effectiveMAG) - newClass.getBaseSTR();
				magBase = Math.min(effectiveSTR, effectiveMAG) - newClass.getBaseMAG();
				character.setSTRGrowth(Math.max(strGrowth, magGrowth));
				character.setMAGGrowth(Math.min(strGrowth, magGrowth));
			} else if (classStatBias == StatBias.LEAN_MAGICAL || classStatBias == StatBias.MAGICAL_ONLY) {
				int effectiveSTR = character.getBaseSTR() + originalClass.getBaseSTR();
				int effectiveMAG = character.getBaseMAG() + originalClass.getBaseMAG();
				strBase = Math.min(effectiveSTR, effectiveMAG) - newClass.getBaseSTR();
				magBase = Math.max(effectiveSTR, effectiveMAG) - newClass.getBaseMAG();
				character.setSTRGrowth(Math.min(strGrowth, magGrowth));
				character.setMAGGrowth(Math.max(strGrowth, magGrowth));
			} else {
				strBase = character.getBaseSTR() + originalClass.getBaseSTR() - newClass.getBaseSTR();
				magBase = character.getBaseMAG() + originalClass.getBaseMAG() - newClass.getBaseMAG();	
			}
			int sklBase = character.getBaseSKL() + originalClass.getBaseSKL() - newClass.getBaseSKL();
			int spdBase = character.getBaseSPD() + originalClass.getBaseSPD() - newClass.getBaseSPD();
			int lckBase = character.getBaseLCK() + originalClass.getBaseLCK() - newClass.getBaseLCK();
			int defBase = character.getBaseDEF() + originalClass.getBaseDEF() - newClass.getBaseDEF();
			int resBase = character.getBaseRES() + originalClass.getBaseRES() - newClass.getBaseRES();
			
			if (classData.isLaguzClass(originalClass) && !classData.isLaguzClass(newClass)) {
				// Laguz -> Beorc
				strBase += (int)Math.floor(classData.getLaguzSTROffset(originalClass) * 0.5);
				magBase += (int)Math.floor(classData.getLaguzMAGOffset(originalClass) * 0.5);
				sklBase += (int)Math.floor(classData.getLaguzSKLOffset(originalClass) * 0.5);
				spdBase += (int)Math.floor(classData.getLaguzSPDOffset(originalClass) * 0.5);
				defBase += (int)Math.floor(classData.getLaguzDEFOffset(originalClass) * 0.5);
				resBase += (int)Math.floor(classData.getLaguzRESOffset(originalClass) * 0.5);
				
				character.setBaseSKL(sklBase);
				character.setBaseSPD(spdBase);
				character.setBaseLCK(lckBase);
				character.setBaseDEF(defBase);
				character.setBaseRES(resBase);
				
			} else if (!classData.isLaguzClass(originalClass) && classData.isLaguzClass(newClass)) {
				// Beorc -> Laguz
				strBase -= classData.getLaguzSTROffset(newClass);
				magBase -= classData.getLaguzMAGOffset(newClass);
				sklBase -= classData.getLaguzSKLOffset(newClass);
				spdBase -= classData.getLaguzSPDOffset(newClass);
				defBase -= classData.getLaguzDEFOffset(newClass);
				resBase -= classData.getLaguzRESOffset(newClass);

				character.setBaseSKL(sklBase);
				character.setBaseSPD(spdBase);
				character.setBaseLCK(lckBase);
				character.setBaseDEF(defBase);
				character.setBaseRES(resBase);
			} else if (classData.isLaguzClass(originalClass) && classData.isLaguzClass(newClass)) {
				// Laguz -> Laguz
				character.setBaseHP(hpBase);
				character.setBaseSKL(sklBase);
				character.setBaseSPD(spdBase);
				character.setBaseLCK(lckBase);
				character.setBaseDEF(defBase);
				character.setBaseRES(resBase);
			}
			
			character.setBaseSTR(strBase);
			character.setBaseMAG(magBase);
			
			// Update weapon levels.
			String originalWeaponLevels = charData.getWeaponLevelStringForCharacter(character);
			DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Old Weapon Levels: " + originalWeaponLevels);
			String classWeaponLevels = classData.getWeaponLevelsForClass(newClass);
			DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Target Class Weapon Levels: " + classWeaponLevels);
			boolean canUseKnives = (classData.getSID1ForClass(newClass) != null && classData.getSID1ForClass(newClass).equals(FE9Data.Skill.EQUIP_KNIFE.getSID())) ||
					(classData.getSID2ForClass(newClass) != null && classData.getSID2ForClass(newClass).equals(FE9Data.Skill.EQUIP_KNIFE.getSID())) ||
					(classData.getSID3ForClass(newClass) != null && classData.getSID3ForClass(newClass).equals(FE9Data.Skill.EQUIP_KNIFE.getSID()));
			
			StringBuilder levels = new StringBuilder();
			// No characters should have a * rank in their character data.
			// * is used only for class data.
			String originalLevels = originalWeaponLevels.replace("-", "");
			int weaponTypesRemaining = classWeaponLevels.replace("-", "").replace("*", "").length() + (classWeaponLevels.contains("*") ? 1 : 0);
			
			StringBuilder originalBuilder = new StringBuilder();
			if (originalLevels.contains("E")) { originalBuilder.append('E'); }
			if (originalLevels.contains("D")) { originalBuilder.append('D'); }
			if (originalLevels.contains("C")) { originalBuilder.append('C'); }
			if (originalLevels.contains("B")) { originalBuilder.append('B'); }
			if (originalLevels.contains("A")) { originalBuilder.append('A'); }
			if (originalLevels.contains("S")) { originalBuilder.append('S'); }
			originalLevels = originalBuilder.toString();
			
			if (originalLevels.isEmpty()) {
				// Get a level based on their join time.
				FE9Data.Chapter joinChapter = FE9Data.Character.withPID(pid).joinChapter();
				switch (joinChapter) {
				case PROLOGUE: case CHAPTER_1: case CHAPTER_2: case CHAPTER_3: originalLevels = "E"; break;
				case CHAPTER_4: case CHAPTER_5: case CHAPTER_6: case CHAPTER_7: originalLevels = "ED"; break;
				case CHAPTER_8: case CHAPTER_9: case CHAPTER_10: case CHAPTER_11: originalLevels = "DC"; break;
				case CHAPTER_12: case CHAPTER_13: case CHAPTER_14: case CHAPTER_15: originalLevels = "DCB"; break;
				case CHAPTER_16: case CHAPTER_17: case CHAPTER_18: case CHAPTER_19: originalLevels = "CBA"; break;
				case CHAPTER_20: case CHAPTER_21: case CHAPTER_22: case CHAPTER_23: originalLevels = "BA"; break;
				case CHAPTER_24: case CHAPTER_25: case CHAPTER_26: case CHAPTER_27: originalLevels = "AS"; break;
				case CHAPTER_27_BK_FIGHT: case CHAPTER_28: case ENDGAME: originalLevels = "S"; break;
				default: originalLevels = "E";
				}
			}
			
			DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Weapon Level Set: " + originalLevels);
			
			FE9Skill addedWeaponDiscipline = null;
			
			boolean starFulfilled = false;
			int numberOfStars = (int)classWeaponLevels.chars().filter(c -> (c == '*')).count();
			for (int i = 0; i < originalWeaponLevels.length(); i++) {
				char newClassChar = classWeaponLevels.charAt(i);
				if (newClassChar == '-') { levels.append('-'); }
				else if (newClassChar == '*') {
					if (starFulfilled) { levels.append('-'); }
					else if (numberOfStars > 1 && rng.nextInt(numberOfStars) != 0) {
						levels.append('-');
						numberOfStars--;
					}
					else {
						int index = originalLevels.length() > 1 ? rng.nextInt(originalLevels.length() - 1) : 0;
						char rank = originalLevels.charAt(index);
						levels.append(rank);
						if (originalLevels.length() > 1) { originalLevels = originalLevels.replace("" + rank, ""); }
						starFulfilled = true;
						if (i == 0) { addedWeaponDiscipline = skillData.getSkillWithSID(FE9Data.Skill.EQUIP_SWORD.getSID()); }
						if (i == 1) { addedWeaponDiscipline = skillData.getSkillWithSID(FE9Data.Skill.EQUIP_LANCE.getSID()); }
						if (i == 2) { addedWeaponDiscipline = skillData.getSkillWithSID(FE9Data.Skill.EQUIP_AXE.getSID()); }
						if (i == 3) { addedWeaponDiscipline = skillData.getSkillWithSID(FE9Data.Skill.EQUIP_BOW.getSID()); }
						if (i == 7) { addedWeaponDiscipline = skillData.getSkillWithSID(FE9Data.Skill.EQUIP_STAFF.getSID()); }
						if (i == 8) { addedWeaponDiscipline = skillData.getSkillWithSID(FE9Data.Skill.EQUIP_KNIFE.getSID()); }
					}
				} else {
					if (weaponTypesRemaining > 1) {
						int index = rng.nextInt(originalLevels.length());
						char rank = originalLevels.charAt(index);
						levels.append(rank);
						if (originalLevels.length() > 1) { originalLevels = originalLevels.replace("" + rank, ""); }
					} else {
						char rank = originalLevels.charAt(originalLevels.length() - 1);
						levels.append(rank);
					}
					weaponTypesRemaining--;
				}
			}
			
			String finalWeaponLevelString = levels.toString();
			DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Final Weapon Levels: " + finalWeaponLevelString);
			charData.setWeaponLevelStringForCharacter(character, finalWeaponLevelString);
			
			// Update skills if necessary.
			List<FE9Skill> skillsToRemove = skillData.requiredSkillsForJID(originalJID);
			if (skillsToRemove != null && !skillsToRemove.isEmpty()) {
				for (FE9Skill skill : skillsToRemove) {
					if (charData.getSID1ForCharacter(character) != null && charData.getSID1ForCharacter(character).equals(skillData.getSID(skill))) {
						DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Removed " + charData.getSID1ForCharacter(character) + " from " + pid);
						charData.setSID1ForCharacter(character, null);
					} else if (charData.getSID2ForCharacter(character) != null && charData.getSID2ForCharacter(character).equals(skillData.getSID(skill))) {
						DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Removed " + charData.getSID2ForCharacter(character) + " from " + pid);
						charData.setSID2ForCharacter(character, null);
					} else if (charData.getSID3ForCharacter(character) != null && charData.getSID3ForCharacter(character).equals(skillData.getSID(skill))) {
						DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Removed " + charData.getSID3ForCharacter(character) + " from " + pid);
						charData.setSID3ForCharacter(character, null);
					}
				}
			}
			List<FE9Skill> necessarySkills = skillData.requiredSkillsForJID(targetJID);
			if (addedWeaponDiscipline != null) { necessarySkills.add(0, addedWeaponDiscipline); }
			if (necessarySkills != null && !necessarySkills.isEmpty()) {
				for (FE9Skill skill : necessarySkills) {
					if (charData.getSID1ForCharacter(character) == null) {
						charData.setSID1ForCharacter(character, skillData.getSID(skill));
						DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Added " + charData.getSID1ForCharacter(character) + " to " + pid);
					} else if (charData.getSID2ForCharacter(character) == null) {
						charData.setSID2ForCharacter(character, skillData.getSID(skill));
						DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Added " + charData.getSID2ForCharacter(character) + " to " + pid);
					} else if (charData.getSID3ForCharacter(character) == null) {
						charData.setSID3ForCharacter(character, skillData.getSID(skill));
						DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Added " + charData.getSID3ForCharacter(character) + " to " + pid);
					} else {
						break;
					}
				}
			}
			
			FE9Skill matchingOccultSkill = skillData.occultSkillForJID(targetJID);
			if (matchingOccultSkill != null) {
				FE9Skill skill1 = skillData.getSkillWithSID(charData.getSID1ForCharacter(character));
				FE9Skill skill2 = skillData.getSkillWithSID(charData.getSID2ForCharacter(character));
				FE9Skill skill3 = skillData.getSkillWithSID(charData.getSID3ForCharacter(character));
			
				if (skillData.isOccultSkill(skill1)) { charData.setSID1ForCharacter(character, skillData.getSID(matchingOccultSkill)); }
				if (skillData.isOccultSkill(skill2)) { charData.setSID2ForCharacter(character, skillData.getSID(matchingOccultSkill)); }
				if (skillData.isOccultSkill(skill3)) { charData.setSID3ForCharacter(character, skillData.getSID(matchingOccultSkill)); }
			}
			
			// Update chapter data (class, weapons, and equipment)
			for (FE9Data.Chapter chapter : FE9Data.Chapter.values())  {
				DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Processing Chapter: " + chapter.toString());
				for (FE9ChapterArmy army : chapterData.armiesForChapter(chapter)) {
					for (String unitID : army.getAllUnitIDs()) {
						FE9ChapterUnit unit = army.getUnitForUnitID(unitID);
						if (army.getPIDForUnit(unit).equals(pid)) {
							army.setJIDForUnit(unit, targetJID);
							
							List<FE9Item> weapons = new ArrayList<FE9Item>();
							if (itemData.laguzWeaponForJID(targetJID) != null) {
								weapons.add(itemData.laguzWeaponForJID(targetJID));
							} else {
								int weaponCount = 0;
								String iid1 = army.getWeapon1ForUnit(unit);
								String iid2 = army.getWeapon2ForUnit(unit);
								String iid3 = army.getWeapon3ForUnit(unit);
								String iid4 = army.getWeapon4ForUnit(unit);
								if (iid1 != null) { weaponCount++; }
								if (iid2 != null) { weaponCount++; }
								if (iid3 != null) { weaponCount++; }
								if (iid4 != null) { weaponCount++; }
								
								if (classData.isLaguzClass(originalClass) && !classData.isLaguzClass(newClass)) {
									weaponCount++; // Former Laguz units will not have any weapons, so we should give them one.
								}
								
								List<WeaponRank> equippedRanks = new ArrayList<WeaponRank>(); 
								if (iid1 != null) { equippedRanks.add(itemData.weaponRankForItem(itemData.itemWithIID(iid1))); }
								if (iid2 != null) { equippedRanks.add(itemData.weaponRankForItem(itemData.itemWithIID(iid2))); }
								if (iid3 != null) { equippedRanks.add(itemData.weaponRankForItem(itemData.itemWithIID(iid3))); }
								if (iid4 != null) { equippedRanks.add(itemData.weaponRankForItem(itemData.itemWithIID(iid4))); }
								equippedRanks.removeIf(rank -> (rank == WeaponRank.UNKNOWN || rank == WeaponRank.NONE));
								
								Map<WeaponType, WeaponRank> weaponLevelsMap = itemData.weaponLevelsForWeaponString(finalWeaponLevelString);
								if (!classData.canClassUseLightMagic(newClass)) { // Light magic keys off of staff ranks, but not every staff user uses light magic.
									weaponLevelsMap.remove(WeaponType.LIGHT);
								}
								List<WeaponType> types = weaponLevelsMap.keySet().stream().sorted(WeaponType.getComparator()).collect(Collectors.toList());
								// For whatever reason, Assassin doesn't have any weapon levels defined or the EQUIPKNIFE skill.
								if (canUseKnives || targetJID.equals(FE9Data.CharacterClass.ASSASSIN.getJID())) {
									types.add(WeaponType.KNIFE);
									weaponLevelsMap.put(WeaponType.KNIFE, itemData.highestRankInString(finalWeaponLevelString));
								}
								
								DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, weaponCount + " weapons to assign.");
								for (WeaponType type : types) {
									DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Type: " + type.toString() + " (" + weaponLevelsMap.get(type) + ")");
								}
								if (!types.isEmpty()) {
									for (int i = 0; i < weaponCount; i++) {
										WeaponType randomUsableType = types.get(rng.nextInt(types.size()));
										DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Selected type: " + randomUsableType.toString());
										WeaponRank usableRank = equippedRanks.size() > i ? equippedRanks.get(i) : weaponLevelsMap.get(randomUsableType);
										List<FE9Item> replacements = itemData.weaponsOfRankAndType(usableRank, randomUsableType);
										if (replacements.isEmpty()) {
											WeaponRank adjacentRank = usableRank.lowerRank();
											if (adjacentRank == WeaponRank.NONE && usableRank.higherRank().isLowerThan(weaponLevelsMap.get(randomUsableType))) {
												adjacentRank = usableRank.higherRank();
											}
											replacements = itemData.weaponsOfRankAndType(adjacentRank, randomUsableType);
										}
										List<FE9Item> specialWeapons = itemData.specialWeaponsForJID(targetJID);
										if (specialWeapons != null) { 
											replacements.addAll(specialWeapons.stream().filter(item -> {
												boolean isCorrectRank =!itemData.weaponRankForItem(item).isHigherThan(usableRank);
												boolean isCorrectType = itemData.weaponTypeForItem(item) == randomUsableType;
												if (isCorrectRank && isCorrectType) {
													DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Added weapon " + itemData.iidOfItem(item) + " as special class weapon.");
													return true;
												}
												return false;
											}).collect(Collectors.toList()));
										}
										
										DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Possible replacements: ");
										for (FE9Item weapon : replacements) {
											DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "\t" + itemData.iidOfItem(weapon));
										}
										
										if (!replacements.isEmpty()) {
											FE9Item weapon = replacements.get(rng.nextInt(replacements.size()));
											DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Selected: " + itemData.iidOfItem(weapon));
											weapons.add(weapon);
										}
									}
								}
							}
							
							DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Assigned Weapons: ");
							weapons.forEach(weapon -> {
								DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, itemData.iidOfItem(weapon));
							});
							
							army.setWeapon1ForUnit(unit, weapons.size() > 0 ? itemData.iidOfItem(weapons.get(0)) : null);
							army.setWeapon2ForUnit(unit, weapons.size() > 1 ? itemData.iidOfItem(weapons.get(1)) : null);
							army.setWeapon3ForUnit(unit, weapons.size() > 2 ? itemData.iidOfItem(weapons.get(2)) : null);
							army.setWeapon4ForUnit(unit, weapons.size() > 3 ? itemData.iidOfItem(weapons.get(3)) : null);
							
							List<FE9Item> equipment = new ArrayList<FE9Item>();
							if (!itemData.equipmentListForJID(targetJID).isEmpty()) {
								equipment.addAll(itemData.equipmentListForJID(targetJID));
							}
							if (isFormerThief) {
								equipment.addAll(itemData.formerThiefKit());
							}
							
							FE9Item item1 = itemData.itemWithIID(army.getItem1ForUnit(unit));
							FE9Item item2 = itemData.itemWithIID(army.getItem2ForUnit(unit));
							FE9Item item3 = itemData.itemWithIID(army.getItem3ForUnit(unit));
							FE9Item item4 = itemData.itemWithIID(army.getItem4ForUnit(unit));
							if (item1 != null && itemData.getImportantEquipment().contains(item1)) { equipment.add(item1); }
							if (item2 != null && itemData.getImportantEquipment().contains(item2)) { equipment.add(item2); }
							if (item3 != null && itemData.getImportantEquipment().contains(item3)) { equipment.add(item3); }
							if (item4 != null && itemData.getImportantEquipment().contains(item4)) { equipment.add(item4); }
							
							if (rng.nextInt(5) != 0) {
								List<FE9Item> items = itemData.potentialEquipmentListForJID(targetJID);
								equipment.add(items.get(rng.nextInt(items.size())));
							}
							if (rng.nextInt(100) < 10) {
								List<FE9Item> rareItems = itemData.rareEquipmentForJID(targetJID);
								equipment.add(rareItems.get(rng.nextInt(rareItems.size())));
							}
							if (rng.nextInt(100) == 0) {
								List<FE9Item> veryRare = itemData.veryRareEquipment();
								equipment.add(veryRare.get(rng.nextInt(veryRare.size())));
							}
							
							DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Assigned Equipment: ");
							equipment.forEach(equip -> {
								DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, itemData.iidOfItem(equip));
							});
							
							army.setItem1ForUnit(unit, equipment.size() > 0 ? itemData.iidOfItem(equipment.get(0)) : null);
							army.setItem2ForUnit(unit, equipment.size() > 1 ? itemData.iidOfItem(equipment.get(1)) : null);
							army.setItem3ForUnit(unit, equipment.size() > 2 ? itemData.iidOfItem(equipment.get(2)) : null);
							army.setItem4ForUnit(unit, equipment.size() > 3 ? itemData.iidOfItem(equipment.get(3)) : null);
							
							// Make sure thieves have the ability to unlock stuff.
							if (classData.isThiefClass(newClass)) {
								if (army.getSkill1ForUnit(unit) == null) {
									army.setSkill1ForUnit(unit, FE9Data.Skill.KEY_0.getSID());
								} else if (army.getSkill2ForUnit(unit) == null) {
									army.setSkill2ForUnit(unit, FE9Data.Skill.KEY_0.getSID());
								} else if (army.getSkill3ForUnit(unit) == null) {
									army.setSkill3ForUnit(unit, FE9Data.Skill.KEY_0.getSID());
								}
							}
						}
					}
				}
			}
		}
		
		chapterData.commitChanges();
		charData.commit();
	}
	
	public static void randomizeBossCharacters(boolean forceDifferent, boolean mixRaces, boolean crossGenders, FE9CharacterDataLoader charData, 
			FE9ClassDataLoader classData, FE9ChapterDataLoader chapterData, FE9SkillDataLoader skillData, FE9ItemDataLoader itemData, Random rng) {
		
		Map<String, String> pidToJid = new HashMap<String, String>();
		
		for (FE9Character character : charData.allBossCharacters()) {
			FE9Data.Character fe9Char = FE9Data.Character.withPID(charData.getPIDForCharacter(character));
			if (fe9Char == null) { continue; }
			if (fe9Char != null && fe9Char.isUnsafeForRandomization()) { continue; }
			String originalJID = charData.getJIDForCharacter(character);
			FE9Class originalClass = classData.classWithID(originalJID);
			
			String pid = charData.getPIDForCharacter(character);
			
			DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Randomizing Boss " + pid);
			
			String targetJID = pidToJid.get(pid);
			FE9Class newClass = null;
			if (targetJID != null) {
				charData.setJIDForCharacter(character, targetJID);
				newClass = classData.classWithID(targetJID);
			} else {
				for (FE9Data.Chapter chapter : FE9Data.Chapter.allChapters())  {
					for (FE9ChapterArmy army : chapterData.armiesForChapter(chapter)) {
						for (String unitID : army.getAllUnitIDs()) {
							FE9ChapterUnit unit = army.getUnitForUnitID(unitID);
							if (army.getPIDForUnit(unit).equals(pid)) {
								String armyJID = army.getJIDForUnit(unit);
								if (!armyJID.equals(originalJID)) {
									DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "JID mismatch from character data (" + originalJID + ") and army data (" + armyJID + "). Defaulting to " + armyJID);
									originalJID = armyJID;
									originalClass = classData.classWithID(armyJID);
									break;
								}
							}
						}
					}
				}
				
				List<FE9Class> possibleReplacements = possibleReplacementsForClass(originalClass, false, false, false, 
						forceDifferent, mixRaces, crossGenders, false, classData);
				possibleReplacements.removeIf(fe9class -> {
					return classData.isPacifistClass(fe9class);
				});
				DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Possible Classes: ");
				for (FE9Class charClass : possibleReplacements) {
					DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "\t" + classData.getJIDForClass(charClass));
				}
				if (possibleReplacements.isEmpty()) { continue; }
				newClass = possibleReplacements.get(rng.nextInt(possibleReplacements.size()));
				targetJID = classData.getJIDForClass(newClass);
				pidToJid.put(pid, targetJID);
				charData.setJIDForCharacter(character, targetJID);
				
				DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Randomized " + pid + " from " + originalJID + " to " + targetJID);
			}
			
			if (classData.isLaguzClass(newClass)) {
				int startingGauge = rng.nextInt(20);
				DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Set Laguz starting gauge to " + startingGauge);
				charData.setLaguzStartingGaugeForCharacter(character, startingGauge);
				if (classData.isLaguzClass(originalClass) && charData.getUnpromotedAIDForCharacter(character) != null) {
					DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Set unpromoted AID to " + classData.getUnpromotedAIDForClass(newClass));
					charData.setUnpromotedAIDForCharacter(character, classData.getUnpromotedAIDForClass(newClass));
				}
				DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Set promoted AID to " + classData.getPromotedAIDForClass(newClass));
				charData.setPromotedAIDForCharacter(character, classData.getPromotedAIDForClass(newClass));
			} else {
				if (classData.isPromotedClass(newClass)) {
					DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Set promoted AID to " + classData.getPromotedAIDForClass(newClass));
					charData.setPromotedAIDForCharacter(character, classData.getPromotedAIDForClass(newClass));
				} else {
					DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Set unpromoted AID to " + classData.getUnpromotedAIDForClass(newClass));
					charData.setUnpromotedAIDForCharacter(character, classData.getUnpromotedAIDForClass(newClass));
				}
				
			}
			
			// Adjust bases
			int hpBase = character.getBaseHP() + originalClass.getBaseHP() - newClass.getBaseHP();
			
			int strBase = 0;
			int magBase = 0;
			StatBias classStatBias = classData.statBiasForClass(newClass);
			if (classStatBias == StatBias.LEAN_PHYSICAL || classStatBias == StatBias.PHYSICAL_ONLY) {
				int effectiveSTR = character.getBaseSTR() + originalClass.getBaseSTR();
				int effectiveMAG = character.getBaseMAG() + originalClass.getBaseMAG();
				strBase = Math.max(effectiveSTR, effectiveMAG) - newClass.getBaseSTR();
				magBase = Math.min(effectiveSTR, effectiveMAG) - newClass.getBaseMAG();
			} else if (classStatBias == StatBias.LEAN_MAGICAL || classStatBias == StatBias.MAGICAL_ONLY) {
				int effectiveSTR = character.getBaseSTR() + originalClass.getBaseSTR();
				int effectiveMAG = character.getBaseMAG() + originalClass.getBaseMAG();
				strBase = Math.min(effectiveSTR, effectiveMAG) - newClass.getBaseSTR();
				magBase = Math.max(effectiveSTR, effectiveMAG) - newClass.getBaseMAG();
			} else {
				strBase = character.getBaseSTR() + originalClass.getBaseSTR() - newClass.getBaseSTR();
				magBase = character.getBaseMAG() + originalClass.getBaseMAG() - newClass.getBaseMAG();	
			}
			int sklBase = character.getBaseSKL() + originalClass.getBaseSKL() - newClass.getBaseSKL();
			int spdBase = character.getBaseSPD() + originalClass.getBaseSPD() - newClass.getBaseSPD();
			int lckBase = character.getBaseLCK() + originalClass.getBaseLCK() - newClass.getBaseLCK();
			int defBase = character.getBaseDEF() + originalClass.getBaseDEF() - newClass.getBaseDEF();
			int resBase = character.getBaseRES() + originalClass.getBaseRES() - newClass.getBaseRES();
			
			if (classData.isLaguzClass(originalClass) && !classData.isLaguzClass(newClass)) {
				// Laguz -> Beorc
				strBase += (int)Math.floor(classData.getLaguzSTROffset(originalClass) * 0.5);
				magBase += (int)Math.floor(classData.getLaguzMAGOffset(originalClass) * 0.5);
				sklBase += (int)Math.floor(classData.getLaguzSKLOffset(originalClass) * 0.5);
				spdBase += (int)Math.floor(classData.getLaguzSPDOffset(originalClass) * 0.5);
				defBase += (int)Math.floor(classData.getLaguzDEFOffset(originalClass) * 0.5);
				resBase += (int)Math.floor(classData.getLaguzRESOffset(originalClass) * 0.5);
				
				character.setBaseSKL(sklBase);
				character.setBaseSPD(spdBase);
				character.setBaseDEF(defBase);
				character.setBaseRES(resBase);
				
			} else if (!classData.isLaguzClass(originalClass) && classData.isLaguzClass(newClass)) {
				// Beorc -> Laguz
				strBase -= classData.getLaguzSTROffset(newClass);
				magBase -= classData.getLaguzMAGOffset(newClass);
				sklBase -= classData.getLaguzSKLOffset(newClass);
				spdBase -= classData.getLaguzSPDOffset(newClass);
				defBase -= classData.getLaguzDEFOffset(newClass);
				resBase -= classData.getLaguzRESOffset(newClass);
				
				character.setBaseSKL(sklBase);
				character.setBaseSPD(spdBase);
				character.setBaseDEF(defBase);
				character.setBaseRES(resBase);
			}
			
			character.setBaseSTR(strBase);
			character.setBaseMAG(magBase);
			
			// Update weapon levels.
			String originalWeaponLevels = charData.getWeaponLevelStringForCharacter(character);
			DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Old Weapon Levels: " + originalWeaponLevels);
			String classWeaponLevels = classData.getWeaponLevelsForClass(newClass);
			DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Target Class Weapon Levels: " + classWeaponLevels);
			boolean canUseKnives = (classData.getSID1ForClass(newClass) != null && classData.getSID1ForClass(newClass).equals(FE9Data.Skill.EQUIP_KNIFE.getSID())) ||
					(classData.getSID2ForClass(newClass) != null && classData.getSID2ForClass(newClass).equals(FE9Data.Skill.EQUIP_KNIFE.getSID())) ||
					(classData.getSID3ForClass(newClass) != null && classData.getSID3ForClass(newClass).equals(FE9Data.Skill.EQUIP_KNIFE.getSID()));
			
			StringBuilder levels = new StringBuilder();
			// No characters should have a * rank in their character data.
			// * is used only for class data.
			String originalLevels = originalWeaponLevels.replace("-", "");
			int weaponTypesRemaining = classWeaponLevels.replace("-", "").replace("*", "").length() + (classWeaponLevels.contains("*") ? 1 : 0);
			
			StringBuilder originalBuilder = new StringBuilder();
			if (originalLevels.contains("E")) { originalBuilder.append('E'); }
			if (originalLevels.contains("D")) { originalBuilder.append('D'); }
			if (originalLevels.contains("C")) { originalBuilder.append('C'); }
			if (originalLevels.contains("B")) { originalBuilder.append('B'); }
			if (originalLevels.contains("A")) { originalBuilder.append('A'); }
			if (originalLevels.contains("S")) { originalBuilder.append('S'); }
			originalLevels = originalBuilder.toString();
			
			if (originalLevels.isEmpty()) {
				// Get a level based on their join time.
				FE9Data.Character bossCharacter = FE9Data.Character.withPID(pid);
				if (bossCharacter == null) { continue; }
				FE9Data.Chapter joinChapter = bossCharacter.joinChapter();
				if (joinChapter == null) { originalLevels = "E"; }
				else {
					switch (joinChapter) {
					case PROLOGUE: case CHAPTER_1: case CHAPTER_2: case CHAPTER_3: originalLevels = "ED"; break;
					case CHAPTER_4: case CHAPTER_5: case CHAPTER_6: case CHAPTER_7: originalLevels = "EDC"; break;
					case CHAPTER_8: case CHAPTER_9: case CHAPTER_10: case CHAPTER_11: originalLevels = "DC"; break;
					case CHAPTER_12: case CHAPTER_13: case CHAPTER_14: case CHAPTER_15: originalLevels = "DCB"; break;
					case CHAPTER_16: case CHAPTER_17: case CHAPTER_18: case CHAPTER_19: originalLevels = "CBA"; break;
					case CHAPTER_20: case CHAPTER_21: case CHAPTER_22: case CHAPTER_23: originalLevels = "BAS"; break;
					case CHAPTER_24: case CHAPTER_25: case CHAPTER_26: case CHAPTER_27: originalLevels = "AS"; break;
					case CHAPTER_27_BK_FIGHT: case CHAPTER_28: case ENDGAME: originalLevels = "AS"; break;
					default: originalLevels = "E";
					}
				}
			}
			
			DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Weapon Level Set: " + originalLevels);
			
			FE9Skill addedWeaponDiscipline = null;
			
			boolean starFulfilled = false;
			int numberOfStars = (int)classWeaponLevels.chars().filter(c -> (c == '*')).count();
			for (int i = 0; i < originalWeaponLevels.length(); i++) {
				char newClassChar = classWeaponLevels.charAt(i);
				if (newClassChar == '-') { levels.append('-'); }
				else if (newClassChar == '*') {
					if (starFulfilled) { levels.append('-'); }
					else if (numberOfStars > 1 && rng.nextInt(numberOfStars) != 0) {
						levels.append('-');
						numberOfStars--;
					}
					else {
						int index = originalLevels.length() > 1 ? rng.nextInt(originalLevels.length() - 1) : 0;
						char rank = originalLevels.charAt(index);
						levels.append(rank);
						if (originalLevels.length() > 1) { originalLevels = originalLevels.replace("" + rank, ""); }
						starFulfilled = true;
						if (i == 0) { addedWeaponDiscipline = skillData.getSkillWithSID(FE9Data.Skill.EQUIP_SWORD.getSID()); }
						if (i == 1) { addedWeaponDiscipline = skillData.getSkillWithSID(FE9Data.Skill.EQUIP_LANCE.getSID()); }
						if (i == 2) { addedWeaponDiscipline = skillData.getSkillWithSID(FE9Data.Skill.EQUIP_AXE.getSID()); }
						if (i == 3) { addedWeaponDiscipline = skillData.getSkillWithSID(FE9Data.Skill.EQUIP_BOW.getSID()); }
						if (i == 7) { addedWeaponDiscipline = skillData.getSkillWithSID(FE9Data.Skill.EQUIP_STAFF.getSID()); }
						if (i == 8) { addedWeaponDiscipline = skillData.getSkillWithSID(FE9Data.Skill.EQUIP_KNIFE.getSID()); }
					}
				} else {
					if (weaponTypesRemaining > 1) {
						int index = rng.nextInt(originalLevels.length());
						char rank = originalLevels.charAt(index);
						levels.append(rank);
						if (originalLevels.length() > 1) { originalLevels = originalLevels.replace("" + rank, ""); }
					} else {
						char rank = originalLevels.charAt(originalLevels.length() - 1);
						levels.append(rank);
					}
					weaponTypesRemaining--;
				}
			}
			
			String finalWeaponLevelString = levels.toString();
			DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Final Weapon Levels: " + finalWeaponLevelString);
			charData.setWeaponLevelStringForCharacter(character, finalWeaponLevelString);
			
			// Update skills if necessary.
			List<FE9Skill> skillsToRemove = skillData.requiredSkillsForJID(originalJID);
			if (skillsToRemove != null && !skillsToRemove.isEmpty()) {
				for (FE9Skill skill : skillsToRemove) {
					if (charData.getSID1ForCharacter(character) != null && charData.getSID1ForCharacter(character).equals(skillData.getSID(skill))) {
						DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Removed " + charData.getSID1ForCharacter(character) + " from " + pid);
						charData.setSID1ForCharacter(character, null);
					} else if (charData.getSID2ForCharacter(character) != null && charData.getSID2ForCharacter(character).equals(skillData.getSID(skill))) {
						DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Removed " + charData.getSID2ForCharacter(character) + " from " + pid);
						charData.setSID2ForCharacter(character, null);
					} else if (charData.getSID3ForCharacter(character) != null && charData.getSID3ForCharacter(character).equals(skillData.getSID(skill))) {
						DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Removed " + charData.getSID3ForCharacter(character) + " from " + pid);
						charData.setSID3ForCharacter(character, null);
					}
				}
			}
			List<FE9Skill> necessarySkills = skillData.requiredSkillsForJID(targetJID);
			if (addedWeaponDiscipline != null) { necessarySkills.add(0, addedWeaponDiscipline); }
			if (necessarySkills != null && !necessarySkills.isEmpty()) {
				for (FE9Skill skill : necessarySkills) {
					if (charData.getSID1ForCharacter(character) == null) {
						charData.setSID1ForCharacter(character, skillData.getSID(skill));
						DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Added " + charData.getSID1ForCharacter(character) + " to " + pid);
					} else if (charData.getSID2ForCharacter(character) == null) {
						charData.setSID2ForCharacter(character, skillData.getSID(skill));
						DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Added " + charData.getSID2ForCharacter(character) + " to " + pid);
					} else if (charData.getSID3ForCharacter(character) == null) {
						charData.setSID3ForCharacter(character, skillData.getSID(skill));
						DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Added " + charData.getSID3ForCharacter(character) + " to " + pid);
					} else {
						break;
					}
				}
			}
			
			FE9Skill matchingOccultSkill = skillData.occultSkillForJID(targetJID);
			if (matchingOccultSkill != null) {
				FE9Skill skill1 = skillData.getSkillWithSID(charData.getSID1ForCharacter(character));
				FE9Skill skill2 = skillData.getSkillWithSID(charData.getSID2ForCharacter(character));
				FE9Skill skill3 = skillData.getSkillWithSID(charData.getSID3ForCharacter(character));
			
				if (skillData.isOccultSkill(skill1)) { charData.setSID1ForCharacter(character, skillData.getSID(matchingOccultSkill)); }
				if (skillData.isOccultSkill(skill2)) { charData.setSID2ForCharacter(character, skillData.getSID(matchingOccultSkill)); }
				if (skillData.isOccultSkill(skill3)) { charData.setSID3ForCharacter(character, skillData.getSID(matchingOccultSkill)); }
			}
			
			// Update chapter data (class, weapons, and equipment)
			for (FE9Data.Chapter chapter : FE9Data.Chapter.values())  {
				DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Processing Chapter: " + chapter.toString());
				for (FE9ChapterArmy army : chapterData.armiesForChapter(chapter)) {
					for (String unitID : army.getAllUnitIDs()) {
						FE9ChapterUnit unit = army.getUnitForUnitID(unitID);
						if (army.getPIDForUnit(unit).equals(pid)) {
							army.setJIDForUnit(unit, targetJID);
							
							List<FE9Item> weapons = new ArrayList<FE9Item>();
							if (itemData.laguzWeaponForJID(targetJID) != null) {
								weapons.add(itemData.laguzWeaponForJID(targetJID));
								// Bosses should stay in laguz form.
								if (army.getSkill1ForUnit(unit) == null) { army.setSkill1ForUnit(unit, FE9Data.Skill.FERAL.getSID()); }
								else if (army.getSkill2ForUnit(unit) == null) { army.setSkill2ForUnit(unit, FE9Data.Skill.FERAL.getSID()); }
								else if (army.getSkill3ForUnit(unit) == null) { army.setSkill3ForUnit(unit, FE9Data.Skill.FERAL.getSID()); }
							} else {
								int weaponCount = 0;
								String iid1 = army.getWeapon1ForUnit(unit);
								String iid2 = army.getWeapon2ForUnit(unit);
								String iid3 = army.getWeapon3ForUnit(unit);
								String iid4 = army.getWeapon4ForUnit(unit);
								if (iid1 != null) { weaponCount++; }
								if (iid2 != null) { weaponCount++; }
								if (iid3 != null) { weaponCount++; }
								if (iid4 != null) { weaponCount++; }
								
								if (classData.isLaguzClass(originalClass) && !classData.isLaguzClass(newClass)) {
									weaponCount++; // Former Laguz units will not have any weapons, so we should give them one.
								}
								
								List<WeaponRank> equippedRanks = new ArrayList<WeaponRank>(); 
								if (iid1 != null) { equippedRanks.add(itemData.weaponRankForItem(itemData.itemWithIID(iid1))); }
								if (iid2 != null) { equippedRanks.add(itemData.weaponRankForItem(itemData.itemWithIID(iid2))); }
								if (iid3 != null) { equippedRanks.add(itemData.weaponRankForItem(itemData.itemWithIID(iid3))); }
								if (iid4 != null) { equippedRanks.add(itemData.weaponRankForItem(itemData.itemWithIID(iid4))); }
								equippedRanks.removeIf(rank -> (rank == WeaponRank.UNKNOWN || rank == WeaponRank.NONE));
								
								Map<WeaponType, WeaponRank> weaponLevelsMap = itemData.weaponLevelsForWeaponString(finalWeaponLevelString);
								if (!classData.canClassUseLightMagic(newClass)) { // Light magic keys off of staff ranks, but not every staff user uses light magic.
									weaponLevelsMap.remove(WeaponType.LIGHT);
								}
								List<WeaponType> types = weaponLevelsMap.keySet().stream().sorted(WeaponType.getComparator()).collect(Collectors.toList());
								// For whatever reason, Assassin doesn't have any weapon levels defined or the EQUIPKNIFE skill.
								if (canUseKnives || targetJID.equals(FE9Data.CharacterClass.ASSASSIN.getJID())) {
									types.add(WeaponType.KNIFE);
									weaponLevelsMap.put(WeaponType.KNIFE, itemData.highestRankInString(finalWeaponLevelString));
								}
								
								// Don't give out staves to bosses.
								if (types.contains(WeaponType.STAFF) && types.size() > 1) {
									types.remove(WeaponType.STAFF);
								}
								
								DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, weaponCount + " weapons to assign.");
								for (WeaponType type : types) {
									DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Type: " + type.toString() + " (" + weaponLevelsMap.get(type) + ")");
								}
								
								boolean hasNonSiegeTome = false;
								boolean hasSiegeTome = false;
								
								boolean hasRangedOption = false;
								boolean hasMeleeOption = false;
								
								for (int i = 0; i < weaponCount; i++) {
									WeaponType randomUsableType = types.get(rng.nextInt(types.size()));
									DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Selected type: " + randomUsableType.toString());
									WeaponRank usableRank = equippedRanks.size() > i ? equippedRanks.get(i) : weaponLevelsMap.get(randomUsableType);
									if (usableRank.isHigherThan(weaponLevelsMap.get(randomUsableType))) {
										usableRank = weaponLevelsMap.get(randomUsableType);
									}
									List<FE9Item> replacements = itemData.weaponsOfRankAndType(usableRank, randomUsableType);
									if (replacements.isEmpty()) {
										WeaponRank adjacentRank = usableRank.lowerRank();
										if (adjacentRank == WeaponRank.NONE && usableRank.higherRank().isLowerThan(weaponLevelsMap.get(randomUsableType))) {
											adjacentRank = usableRank.higherRank();
										}
										replacements = itemData.weaponsOfRankAndType(adjacentRank, randomUsableType);
									}
//									List<FE9Item> specialWeapons = itemData.specialWeaponsForJID(targetJID);
//									if (specialWeapons != null) { replacements.addAll(specialWeapons); }
									
									DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Possible replacements: ");
									for (FE9Item weapon : replacements) {
										DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "\t" + itemData.iidOfItem(weapon));
									}
									
									if (!replacements.isEmpty()) {
										FE9Item weapon = replacements.get(rng.nextInt(replacements.size()));
										DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Selected: " + itemData.iidOfItem(weapon));
										weapons.add(weapon);
										if (itemData.isSiegeTome(weapon)) { hasSiegeTome = true; }
										else { hasNonSiegeTome = true; }
										
										if (itemData.isRanged(weapon)) { hasRangedOption = true; }
										if (itemData.isMelee(weapon)) { hasMeleeOption = true; }
									}
								}
								
								if (hasSiegeTome && !hasNonSiegeTome) {
									// Give this character an additional non-siege tome.
									while (types.size() > 0) {
										WeaponType usableType = types.get(rng.nextInt(types.size()));
										WeaponRank usableRank = weaponLevelsMap.get(usableType);
										List<FE9Item> replacements = itemData.weaponsOfRankAndType(usableRank, usableType);
										replacements.removeIf(weapon -> { return itemData.isSiegeTome(weapon); });
										while (replacements.isEmpty()) {
											usableRank = usableRank.lowerRank();
											if (usableRank == WeaponRank.NONE) { break; }
											replacements = itemData.weaponsOfRankAndType(usableRank.lowerRank(), usableType);
											replacements.removeIf(weapon -> { return itemData.isSiegeTome(weapon); });
										}
										if (!replacements.isEmpty()) {
											FE9Item additionalWeapon = replacements.get(rng.nextInt(replacements.size()));
											DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Additional Weapon: " + itemData.iidOfItem(additionalWeapon));
											weapons.add(additionalWeapon);
											break;
										} else {
											types.remove(usableType);
										}
									}
								}
								
								if (hasRangedOption && !hasMeleeOption) {
									// Give this character a melee option if possible.
									while (types.size() > 0) {
										WeaponType usableType = types.get(rng.nextInt(types.size()));
										WeaponRank usableRank = weaponLevelsMap.get(usableType);
										List<FE9Item> replacements = itemData.weaponsOfRankAndType(usableRank, usableType);
										replacements.removeIf(weapon -> { return !itemData.isMelee(weapon); });
										while (replacements.isEmpty()) {
											usableRank = usableRank.lowerRank();
											if (usableRank == WeaponRank.NONE) { break; }
											replacements = itemData.weaponsOfRankAndType(usableRank, usableType);
											replacements.removeIf(weapon -> {return !itemData.isMelee(weapon); });
										}
										if (!replacements.isEmpty()) {
											FE9Item additionalWeapon = replacements.get(rng.nextInt(replacements.size()));
											DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Additional Weapon: " + itemData.iidOfItem(additionalWeapon));
											weapons.add(additionalWeapon);
											break;
										} else {
											types.remove(usableType);
										}
									}
								}
								
								if (hasMeleeOption && !hasRangedOption) {
									// Give this character a ranged option if possible.
									while (types.size() > 0) {
										WeaponType usableType = types.get(rng.nextInt(types.size()));
										WeaponRank usableRank = weaponLevelsMap.get(usableType);
										List<FE9Item> replacements = itemData.weaponsOfRankAndType(usableRank, usableType);
										replacements.removeIf(weapon -> { return !itemData.isRanged(weapon); });
										while (replacements.isEmpty()) {
											usableRank = usableRank.lowerRank();
											if (usableRank == WeaponRank.NONE) { break; }
											replacements = itemData.weaponsOfRankAndType(usableRank, usableType);
											replacements.removeIf(weapon -> {return !itemData.isRanged(weapon); });
										}
										if (!replacements.isEmpty()) {
											FE9Item additionalWeapon = replacements.get(rng.nextInt(replacements.size()));
											DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Additional Weapon: " + itemData.iidOfItem(additionalWeapon));
											weapons.add(additionalWeapon);
											break;
										} else {
											types.remove(usableType);
										}
									}
								}
							}
							
							DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Assigned Weapons: ");
							weapons.forEach(weapon -> {
								DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, itemData.iidOfItem(weapon));
							});
							
							army.setWeapon1ForUnit(unit, weapons.size() > 0 ? itemData.iidOfItem(weapons.get(0)) : null);
							army.setWeapon2ForUnit(unit, weapons.size() > 1 ? itemData.iidOfItem(weapons.get(1)) : null);
							army.setWeapon3ForUnit(unit, weapons.size() > 2 ? itemData.iidOfItem(weapons.get(2)) : null);
							army.setWeapon4ForUnit(unit, weapons.size() > 3 ? itemData.iidOfItem(weapons.get(3)) : null);
							
							List<FE9Item> equipment = new ArrayList<FE9Item>();
							if (!itemData.equipmentListForJID(targetJID).isEmpty()) {
								equipment.addAll(itemData.equipmentListForJID(targetJID));
							}
							
							if (rng.nextInt(5) != 0) {
								List<FE9Item> items = itemData.potentialEquipmentListForJID(targetJID);
								equipment.add(items.get(rng.nextInt(items.size())));
							}
							if (rng.nextInt(100) < 10) {
								List<FE9Item> rareItems = itemData.rareEquipmentForJID(targetJID);
								equipment.add(rareItems.get(rng.nextInt(rareItems.size())));
							}
							if (rng.nextInt(100) == 0) {
								List<FE9Item> veryRare = itemData.veryRareEquipment();
								equipment.add(veryRare.get(rng.nextInt(veryRare.size())));
							}
							
							DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Assigned Equipment: ");
							equipment.forEach(equip -> {
								DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, itemData.iidOfItem(equip));
							});
							
							List<FE9Item> existingDrops = new ArrayList<FE9Item>();
							if (unit.willDropItem1()) { existingDrops.add(itemData.itemWithIID(army.getItem1ForUnit(unit))); }
							if (unit.willDropItem2()) { existingDrops.add(itemData.itemWithIID(army.getItem2ForUnit(unit))); }
							if (unit.willDropItem3()) { existingDrops.add(itemData.itemWithIID(army.getItem3ForUnit(unit))); }
							if (unit.willDropItem4()) { existingDrops.add(itemData.itemWithIID(army.getItem4ForUnit(unit))); }
							existingDrops.retainAll(itemData.getImportantEquipment());
							
							equipment.addAll(0, existingDrops);
							
							unit.setWillDropItem1(false);
							unit.setWillDropItem2(false);
							unit.setWillDropItem3(false);
							unit.setWillDropItem4(false);
							
							army.setItem1ForUnit(unit, equipment.size() > 0 ? itemData.iidOfItem(equipment.get(0)) : null);
							army.setItem2ForUnit(unit, equipment.size() > 1 ? itemData.iidOfItem(equipment.get(1)) : null);
							army.setItem3ForUnit(unit, equipment.size() > 2 ? itemData.iidOfItem(equipment.get(2)) : null);
							army.setItem4ForUnit(unit, equipment.size() > 3 ? itemData.iidOfItem(equipment.get(3)) : null);
							
							if (equipment.size() > 0 && existingDrops.contains(equipment.get(0))) { unit.setWillDropItem1(true); }
							if (equipment.size() > 1 && existingDrops.contains(equipment.get(1))) { unit.setWillDropItem2(true); }
							if (equipment.size() > 2 && existingDrops.contains(equipment.get(2))) { unit.setWillDropItem3(true); }
							if (equipment.size() > 3 && existingDrops.contains(equipment.get(3))) { unit.setWillDropItem4(true); }
						}
					}
				}
			}
		}
		
		chapterData.commitChanges();
		charData.commit();
	}
	
	public static void randomizeMinionCharacters(int chance, boolean forceDifferent, boolean mixRaces, boolean crossGenders, FE9CharacterDataLoader charData,
			FE9ClassDataLoader classData, FE9ChapterDataLoader chapterData, FE9SkillDataLoader skillData, FE9ItemDataLoader itemData, Random rng) {
		
		for (FE9Data.Chapter chapter : FE9Data.Chapter.allChapters()) {
			for (FE9ChapterArmy army : chapterData.armiesForChapter(chapter)) {
				for (String unitID : army.getAllUnitIDs()) {
					if (rng.nextInt(100) >= chance) { continue; }
					FE9ChapterUnit unit = army.getUnitForUnitID(unitID);
					String pid = army.getPIDForUnit(unit);
					if (!charData.isMinionCharacter(charData.characterWithID(pid))) { continue; }
					if (charData.isRestrictedMinionCharacterPID(pid)) { continue; }
					
					String originalJID = army.getJIDForUnit(unit);
					FE9Class originalClass = classData.classWithID(originalJID);
					if (classData.isPacifistClass(originalClass)) { continue; } // Don't change healers.
					
					if (chapter.hasWaterSpawningBandits() && 
							(originalJID.equals(FE9Data.CharacterClass.BANDIT.getJID()) || 
									originalJID.equals(FE9Data.CharacterClass.BERSERKER.getJID()))) { 
						continue;
					}
					
					FE9Character minionCharacter = charData.characterWithID(pid);
					boolean isDaeinMinion = charData.isDaeinCharacter(minionCharacter);
					boolean isPID14Minion = charData.isPID14Character(minionCharacter);
					
					List<FE9Class> replacementClasses = possibleReplacementsForClass(originalClass, false, false, false, 
							forceDifferent, mixRaces, crossGenders, false, classData);
					replacementClasses.removeIf(fe9class -> {
						return classData.isPacifistClass(fe9class);
					});
					if (chapter.useRestrictedClassSetForMinions()) {
						replacementClasses.removeIf(fe9Class -> {
							return classData.isAdvancedClass(fe9Class);
						});
					}
					DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Possible Classes: ");
					for (FE9Class charClass : replacementClasses) {
						DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "\t" + classData.getJIDForClass(charClass));
					}
					if (replacementClasses.isEmpty()) { continue; }
					if (isDaeinMinion) {
						replacementClasses.removeIf(charClass -> {
							return !charData.availableJIDsForDaeinMinions().contains(classData.getJIDForClass(charClass));
						});
					} else if (isPID14Minion) {
						replacementClasses.removeIf(charClass -> {
							return !charData.availableJIDsForPID14Minions().contains(classData.getJIDForClass(charClass));
						});
					}
					if (replacementClasses.isEmpty()) { continue; }
					FE9Class newClass = replacementClasses.get(rng.nextInt(replacementClasses.size()));
					String targetJID = classData.getJIDForClass(newClass);
					if (isDaeinMinion) {
						// Daein minions have classes built into them, so we have to replace PIDs if we change their class too.
						List<FE9Character> replacementCharacters = charData.getDaeinCharactersForJID(targetJID);
						if (replacementCharacters == null || replacementCharacters.isEmpty()) {
							// Try ZAKO
							DebugPrinter.error(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Replacing Daein minion with ZAKO.");
							army.setPIDForUnit(unit, "PID_ZAKO");
						} else {
							FE9Character replacement = replacementCharacters.get(rng.nextInt(replacementCharacters.size()));
							army.setPIDForUnit(unit, charData.getPIDForCharacter(replacement));
						}
					} else if (isPID14Minion) {
						List<FE9Character> replacementCharacters = charData.getPID14CharactersForJID(targetJID);
						if (replacementCharacters == null || replacementCharacters.isEmpty()) {
							// This shouldn't happen with the new logic, but try ZAKO anyway if we get here somehow.
							DebugPrinter.error(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Replacing PID_14 minion with ZAKO.");
							army.setPIDForUnit(unit, "PID_ZAKO");
						} else {
							FE9Character replacement = replacementCharacters.get(rng.nextInt(replacementCharacters.size()));
							army.setPIDForUnit(unit, charData.getPIDForCharacter(replacement));
						}
					}
					
					String classWeaponLevels = classData.getWeaponLevelsForClass(newClass);
					DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Target Class Weapon Levels: " + classWeaponLevels);
					int weaponTypesRemaining = classWeaponLevels.replace("-", "").replace("*", "").length() + (classWeaponLevels.contains("*") ? 1 : 0);
					boolean canUseKnives = (classData.getSID1ForClass(newClass) != null && classData.getSID1ForClass(newClass).equals(FE9Data.Skill.EQUIP_KNIFE.getSID())) ||
							(classData.getSID2ForClass(newClass) != null && classData.getSID2ForClass(newClass).equals(FE9Data.Skill.EQUIP_KNIFE.getSID())) ||
							(classData.getSID3ForClass(newClass) != null && classData.getSID3ForClass(newClass).equals(FE9Data.Skill.EQUIP_KNIFE.getSID()));
					
					FE9Skill addedWeaponDiscipline = null;
					
					StringBuilder levels = new StringBuilder();
					
					String originalLevels = "E";
					
					switch (chapter) {
					case PROLOGUE: case CHAPTER_1: case CHAPTER_2: case CHAPTER_3: originalLevels = "EDC"; break;
					case CHAPTER_4: case CHAPTER_5: case CHAPTER_6: case CHAPTER_7: originalLevels = "EDC"; break;
					case CHAPTER_8: case CHAPTER_9: case CHAPTER_10: case CHAPTER_11: originalLevels = "DCB"; break;
					case CHAPTER_12: case CHAPTER_13: case CHAPTER_14: case CHAPTER_15: originalLevels = "DCB"; break;
					case CHAPTER_16: case CHAPTER_17: case CHAPTER_18: case CHAPTER_19: originalLevels = "CBA"; break;
					case CHAPTER_20: case CHAPTER_21: case CHAPTER_22: case CHAPTER_23: originalLevels = "CBA"; break;
					case CHAPTER_24: case CHAPTER_25: case CHAPTER_26: case CHAPTER_27: originalLevels = "BAS"; break;
					case CHAPTER_27_BK_FIGHT: case CHAPTER_28: case ENDGAME: originalLevels = "BAS"; break;
					default: originalLevels = "E";
					}
					
					boolean starFulfilled = false;
					int numberOfStars = (int)classWeaponLevels.chars().filter(c -> (c == '*')).count();
					for (int i = 0; i < classWeaponLevels.length(); i++) {
						char newClassChar = classWeaponLevels.charAt(i);
						if (newClassChar == '-') { levels.append('-'); }
						else if (newClassChar == '*') {
							if (starFulfilled) { levels.append('-'); }
							else if (numberOfStars > 1 && rng.nextInt(numberOfStars) != 0) {
								levels.append('-');
								numberOfStars--;
							}
							else {
								int index = rng.nextInt(originalLevels.length());
								char rank = originalLevels.charAt(index);
								levels.append(rank);
								if (originalLevels.length() > 1) { originalLevels = originalLevels.replace("" + rank, ""); }
								starFulfilled = true;
								if (i == 0) { addedWeaponDiscipline = skillData.getSkillWithSID(FE9Data.Skill.EQUIP_SWORD.getSID()); }
								if (i == 1) { addedWeaponDiscipline = skillData.getSkillWithSID(FE9Data.Skill.EQUIP_LANCE.getSID()); }
								if (i == 2) { addedWeaponDiscipline = skillData.getSkillWithSID(FE9Data.Skill.EQUIP_AXE.getSID()); }
								if (i == 3) { addedWeaponDiscipline = skillData.getSkillWithSID(FE9Data.Skill.EQUIP_BOW.getSID()); }
								if (i == 7) { addedWeaponDiscipline = skillData.getSkillWithSID(FE9Data.Skill.EQUIP_STAFF.getSID()); }
								if (i == 8) { addedWeaponDiscipline = skillData.getSkillWithSID(FE9Data.Skill.EQUIP_KNIFE.getSID()); }
							}
						} else {
							if (weaponTypesRemaining > 1) {
								int index = rng.nextInt(originalLevels.length());
								char rank = originalLevels.charAt(index);
								levels.append(rank);
								if (originalLevels.length() > 1) { originalLevels = originalLevels.replace("" + rank, ""); }
							} else {
								char rank = originalLevels.charAt(originalLevels.length() - 1);
								levels.append(rank);
							}
							weaponTypesRemaining--;
						}
					}
					
					String finalWeaponLevelString = levels.toString();
					DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Final Weapon Levels: " + finalWeaponLevelString);
					
					army.setJIDForUnit(unit, targetJID);
					if (addedWeaponDiscipline != null) {
						if (army.getSkill1ForUnit(unit) == null) {
							army.setSkill1ForUnit(unit, skillData.getSID(addedWeaponDiscipline));
						} else if (army.getSkill2ForUnit(unit) == null) {
							army.setSkill2ForUnit(unit, skillData.getSID(addedWeaponDiscipline));
						} else if (army.getSkill3ForUnit(unit) == null) {
							army.setSkill3ForUnit(unit, skillData.getSID(addedWeaponDiscipline));
						}
					}
					
					List<FE9Item> weapons = new ArrayList<FE9Item>();
					if (itemData.laguzWeaponForJID(targetJID) != null) {
						weapons.add(itemData.laguzWeaponForJID(targetJID));
					} else {
						int weaponCount = 0;
						String iid1 = army.getWeapon1ForUnit(unit);
						String iid2 = army.getWeapon2ForUnit(unit);
						String iid3 = army.getWeapon3ForUnit(unit);
						String iid4 = army.getWeapon4ForUnit(unit);
						if (iid1 != null) { weaponCount++; }
						if (iid2 != null) { weaponCount++; }
						if (iid3 != null) { weaponCount++; }
						if (iid4 != null) { weaponCount++; }
						
						if (classData.isLaguzClass(originalClass) && !classData.isLaguzClass(newClass)) {
							weaponCount++; // Former Laguz units will not have any weapons, so we should give them one.
						}
						
						List<WeaponRank> equippedRanks = new ArrayList<WeaponRank>(); 
						if (iid1 != null) { equippedRanks.add(itemData.weaponRankForItem(itemData.itemWithIID(iid1))); }
						if (iid2 != null) { equippedRanks.add(itemData.weaponRankForItem(itemData.itemWithIID(iid2))); }
						if (iid3 != null) { equippedRanks.add(itemData.weaponRankForItem(itemData.itemWithIID(iid3))); }
						if (iid4 != null) { equippedRanks.add(itemData.weaponRankForItem(itemData.itemWithIID(iid4))); }
						equippedRanks.removeIf(rank -> (rank == WeaponRank.UNKNOWN || rank == WeaponRank.NONE));
						
						Map<WeaponType, WeaponRank> weaponLevelsMap = itemData.weaponLevelsForWeaponString(finalWeaponLevelString);
						if (!classData.canClassUseLightMagic(newClass)) { // Light magic keys off of staff ranks, but not every staff user uses light magic.
							weaponLevelsMap.remove(WeaponType.LIGHT);
						}
						List<WeaponType> types = weaponLevelsMap.keySet().stream().sorted(WeaponType.getComparator()).collect(Collectors.toList());
						// For whatever reason, Assassin doesn't have any weapon levels defined or the EQUIPKNIFE skill.
						if (canUseKnives || targetJID.equals(FE9Data.CharacterClass.ASSASSIN.getJID())) {
							types.add(WeaponType.KNIFE);
							weaponLevelsMap.put(WeaponType.KNIFE, itemData.highestRankInString(finalWeaponLevelString));
						}
						
						DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, weaponCount + " weapons to assign.");
						for (WeaponType type : types) {
							DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Type: " + type.toString() + " (" + weaponLevelsMap.get(type) + ")");
						}
						for (int i = 0; i < weaponCount; i++) {
							WeaponType randomUsableType = types.get(rng.nextInt(types.size()));
							DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Selected type: " + randomUsableType.toString());
							WeaponRank usableRank = equippedRanks.size() > i ? equippedRanks.get(i) : weaponLevelsMap.get(randomUsableType);
							List<FE9Item> replacements = itemData.weaponsOfRankAndType(usableRank, randomUsableType);
							if (replacements.isEmpty()) {
								WeaponRank adjacentRank = usableRank.lowerRank();
								if (adjacentRank == WeaponRank.NONE && usableRank.higherRank().isLowerThan(weaponLevelsMap.get(randomUsableType))) {
									adjacentRank = usableRank.higherRank();
								}
								replacements = itemData.weaponsOfRankAndType(adjacentRank, randomUsableType);
							}
//							List<FE9Item> specialWeapons = itemData.specialWeaponsForJID(targetJID);
//							if (specialWeapons != null) { replacements.addAll(specialWeapons); }
							
							DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Possible replacements: ");
							for (FE9Item weapon : replacements) {
								DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "\t" + itemData.iidOfItem(weapon));
							}
							
							if (!replacements.isEmpty()) {
								FE9Item weapon = replacements.get(rng.nextInt(replacements.size()));
								DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Selected: " + itemData.iidOfItem(weapon));
								weapons.add(weapon);
							}
						}
					}
					
					DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, "Assigned Weapons: ");
					weapons.forEach(weapon -> {
						DebugPrinter.log(DebugPrinter.Key.FE9_RANDOM_CLASSES, itemData.iidOfItem(weapon));
					});
					
					army.setWeapon1ForUnit(unit, weapons.size() > 0 ? itemData.iidOfItem(weapons.get(0)) : null);
					army.setWeapon2ForUnit(unit, weapons.size() > 1 ? itemData.iidOfItem(weapons.get(1)) : null);
					army.setWeapon3ForUnit(unit, weapons.size() > 2 ? itemData.iidOfItem(weapons.get(2)) : null);
					army.setWeapon4ForUnit(unit, weapons.size() > 3 ? itemData.iidOfItem(weapons.get(3)) : null);
				}
			}
		}
		
		chapterData.commitChanges();
	}
	
	private static List<FE9Class> possibleReplacementsForClass(FE9Class originalClass, boolean includeLords, boolean includeThieves, boolean includeSpecial,
			boolean forceDifferent, boolean mixRaces, boolean crossGenders, boolean forPlayerCharacter, FE9ClassDataLoader classData) {
		Set<FE9Class> classChoices = new HashSet<FE9Class>();
		
		if (!mixRaces) {
			if (classData.isLaguzClass(originalClass)) {
				classChoices.addAll(classData.allLaguzClasses());
			} else if (classData.isPromotedClass(originalClass)) {
				classChoices.addAll(classData.allPromotedClasses());
			} else {
				classChoices.addAll(classData.allUnpromotedClasses());
			}
		} else if (classData.isPromotedClass(originalClass) || classData.isLaguzClass(originalClass)) {
			classChoices.addAll(classData.allPromotedClasses());
			classChoices.addAll(classData.allLaguzClasses());
		} else {
			classChoices.addAll(classData.allUnpromotedClasses());
		}
		
		classChoices.retainAll(classData.allValidClasses());
		
		//don't let flying enemies become grounded
		if (classData.isFlierClass(originalClass) && !forPlayerCharacter) {
			classChoices.retainAll(classData.allFliers());
		}
		
		if (forceDifferent) {
			classChoices.removeIf(classChoice -> {
				return classData.getJIDForClass(classChoice).equals(classData.getJIDForClass(originalClass));
			});
			
			classChoices.removeIf(classChoice -> {
				return classData.areClassesSimilar(classChoice, originalClass);
			});
		}
		
		if (!crossGenders) {
			if (classData.isFemale(originalClass)) {
				classChoices.retainAll(classData.allFemale());
			} else {
				classChoices.removeAll(classData.allFemale());
			}
		}
		
		if (forPlayerCharacter) {
			classChoices.retainAll(classData.allPlayerEligible(includeLords, includeThieves, includeSpecial));
		} else {
			classChoices.retainAll(classData.allEnemyEligible());
		}
		
		if (includeLords && classData.isLordClass(originalClass)) {
			classChoices.removeAll(classData.allPacifistClasses());
		}
		
		return classChoices.stream().sorted(new Comparator<FE9Class>() {
			@Override
			public int compare(FE9Class arg0, FE9Class arg1) {
				return classData.getJIDForClass(arg0).compareTo(classData.getJIDForClass(arg1));
			}
		}).collect(Collectors.toList());
	}
}
