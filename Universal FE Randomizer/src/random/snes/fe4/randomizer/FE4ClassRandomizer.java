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

import fedata.snes.fe4.FE4ChildCharacter;
import fedata.snes.fe4.FE4Data;
import fedata.snes.fe4.FE4StaticCharacter;
import fedata.snes.fe4.FE4Data.HolyBlood;
import fedata.snes.fe4.FE4Data.HolyBloodSlot1;
import fedata.snes.fe4.FE4Data.HolyBloodSlot2;
import fedata.snes.fe4.FE4Data.HolyBloodSlot3;
import fedata.snes.fe4.FE4Data.Item;
import fedata.snes.fe4.FE4EnemyCharacter;
import random.snes.fe4.loader.CharacterDataLoader;
import random.snes.fe4.loader.ItemMapper;
import ui.fe4.FE4ClassOptions;
import ui.fe4.FE4ClassOptions.ChildOptions;
import ui.fe4.FE4ClassOptions.ShopOptions;

public class FE4ClassRandomizer {
	
	static final int rngSalt = 1248;
	
	public static void randomizePlayableCharacterClasses(FE4ClassOptions options, CharacterDataLoader charData, ItemMapper itemMap, Random rng) {
		Map<FE4Data.Character, FE4Data.CharacterClass> predeterminedClasses = new HashMap<FE4Data.Character, FE4Data.CharacterClass>();
		Set<FE4Data.Character> blacklistedCharacters = new HashSet<FE4Data.Character>();
		Map<FE4Data.Character, FE4Data.Item> requiredItems = new HashMap<FE4Data.Character, FE4Data.Item>();
		
		if (!options.includeLords) {
			blacklistedCharacters.addAll(FE4Data.Character.LordCharacters);
		}
		
		if (!options.includeThieves) {
			blacklistedCharacters.addAll(FE4Data.Character.ThiefCharacters);
		}
		
		if (!options.includeDancers) {
			blacklistedCharacters.addAll(FE4Data.Character.DancerCharacters);
		}
		
		// Gen 1
		List<FE4StaticCharacter> gen1Characters = charData.getGen1Characters();
		for  (FE4StaticCharacter staticChar : gen1Characters) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
			if (blacklistedCharacters.contains(fe4Char)) { continue; }
			
			FE4Data.CharacterClass targetClass = predeterminedClasses.get(fe4Char);
			if (targetClass != null) {
				setStaticCharacterToClass(options, staticChar, targetClass, charData, itemMap, predeterminedClasses, requiredItems, rng);
				continue;
			}
			
			FE4Data.CharacterClass originalClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID());
			if (originalClass == null) { continue; } // Shouldn't be touching this class. Skip this character.
			
			// In the case that we have a weak requirement on a specific class (where two characters have to be somewhat similar), set that here.
			// Whoever gets randomized first chooses the weapon.
			// The second character should refer to the first character.
			FE4Data.CharacterClass referenceClass = null;
			if (FE4Data.WeaklyLinkedCharacters.containsKey(fe4Char)) {
				FE4Data.Character referenceCharacter = FE4Data.WeaklyLinkedCharacters.get(fe4Char);
				if (referenceCharacter.isPlayable() && referenceCharacter.isGen1()) {
					referenceClass = predeterminedClasses.get(referenceCharacter);
					if (referenceClass != null) {
						if (referenceClass.isPromoted() && !originalClass.isPromoted()) {
							List<FE4Data.CharacterClass> demotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(referenceClass.demotedClasses(staticChar.isFemale())));
							if (!demotedClasses.isEmpty()) {
								referenceClass = demotedClasses.get(rng.nextInt(demotedClasses.size()));
							}
						} else if (!referenceClass.isPromoted() && originalClass.isPromoted()) {
							List<FE4Data.CharacterClass> promotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(referenceClass.promotionClasses(staticChar.isFemale())));
							if (!promotedClasses.isEmpty()) {
								referenceClass = promotedClasses.get(rng.nextInt(promotedClasses.size()));
							}
						}
					}
				}
			}
			
			List<FE4Data.CharacterClass> potentialClasses = new ArrayList<FE4Data.CharacterClass>();
			
			if (fe4Char.isHealer() && options.retainHealers) {
				Collections.addAll(potentialClasses, originalClass.getClassPool(true, false, true, staticChar.isFemale(), false, false, false, FE4Data.Item.HEAL, null));
			} else {
				FE4Data.Item mustUseItem = null;
				if (!options.randomizeBlood) {
					// Limit classes if this option is disabled for those with major holy blood (Sigurd, Quan, Brigid, Lewyn (and technically Claud)).
					int blood1Value = staticChar.getHolyBlood1Value();
					int blood2Value = staticChar.getHolyBlood2Value();
					int blood3Value = staticChar.getHolyBlood3Value();
					// Blood 4 is only Loptous, which we're not dealing with player-side.
					
					List<HolyBloodSlot1> slot1Blood = FE4Data.HolyBloodSlot1.slot1HolyBlood(blood1Value);
					List<HolyBloodSlot2> slot2Blood = FE4Data.HolyBloodSlot2.slot2HolyBlood(blood2Value);
					List<HolyBloodSlot3> slot3Blood = FE4Data.HolyBloodSlot3.slot3HolyBlood(blood3Value);
					
					if (slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
						mustUseItem = slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType().weaponType.getBasic();
					} else if (slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
						mustUseItem = slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType().weaponType.getBasic();
					} else if (slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
						mustUseItem = slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType().weaponType.getBasic();
					}
				}
				
				if (fe4Char.mustLoseToCharacters().length > 0) {
					// We know this is Ethlyn and Quan, so we could just set requireHorseback to true.
					if (referenceClass != null) {
						Collections.addAll(potentialClasses, referenceClass.getClassPool(true, false, true, staticChar.isFemale(), true, fe4Char.requiresAttack(), true, mustUseItem, Item.HORSESLAYER));
					} else {
						Collections.addAll(potentialClasses, originalClass.getClassPool(false, false, true, staticChar.isFemale(), true, fe4Char.requiresAttack(), true, mustUseItem, Item.HORSESLAYER));
					}
				} else {
					if (referenceClass != null) {
						Collections.addAll(potentialClasses, referenceClass.getClassPool(true, false, false, staticChar.isFemale(), false, fe4Char.requiresAttack(), false, mustUseItem, null));
					} else {
						Collections.addAll(potentialClasses, originalClass.getClassPool(false, false, false, staticChar.isFemale(), false, fe4Char.requiresAttack(), false, mustUseItem, null));
					}
				}
			}
			
			if (potentialClasses.isEmpty()) { continue; }
			
			Set<HolyBlood> bloodOptions = new HashSet<HolyBlood>(Arrays.asList(fe4Char.limitedHolyBloodSelection()));
			targetClass = potentialClasses.get(rng.nextInt(potentialClasses.size()));
			Set<HolyBlood> supportedBlood = new HashSet<HolyBlood>(Arrays.asList(targetClass.supportedHolyBlood()));
			supportedBlood.retainAll(bloodOptions);
			while (supportedBlood.isEmpty()) {
				targetClass = potentialClasses.get(rng.nextInt(potentialClasses.size()));
				supportedBlood = new HashSet<HolyBlood>(Arrays.asList(targetClass.supportedHolyBlood()));
				supportedBlood.retainAll(bloodOptions);	
			}
			
			
			setStaticCharacterToClass(options, staticChar, targetClass, charData, itemMap, predeterminedClasses, requiredItems, rng);
			
			for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
				predeterminedClasses.put(linked, targetClass);
			}
			
			// Set ourselves as predetermined, in the odd case that we run across ourself again.
			// Also useful for children in this case.
			predeterminedClasses.put(fe4Char, targetClass);
		}
		
		// Gen 2 - Common
		List<FE4StaticCharacter> gen2CommonCharacters = charData.getGen2CommonCharacters();
		for  (FE4StaticCharacter staticChar : gen2CommonCharacters) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
			if (fe4Char == null || blacklistedCharacters.contains(fe4Char)) { continue; }
			
			FE4Data.CharacterClass targetClass = predeterminedClasses.get(fe4Char);
			if (targetClass != null) {
				setStaticCharacterToClass(options, staticChar, targetClass, charData, itemMap, predeterminedClasses, requiredItems, rng);
				continue;
			}
			
			FE4Data.CharacterClass originalClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID());
			if (originalClass == null) { continue; } // Shouldn't be touching this class. Skip this character.
			
			List<FE4Data.CharacterClass> potentialClasses = new ArrayList<FE4Data.CharacterClass>(); 
			
			// In the case that we have a weak requirement on a specific class (where two characters have to be somewhat similar), set that here.
			// Whoever gets randomized first chooses the weapon.
			// The second character should refer to the first character.
			FE4Data.CharacterClass referenceClass = null;
			if (FE4Data.WeaklyLinkedCharacters.containsKey(fe4Char)) {
				FE4Data.Character referenceCharacter = FE4Data.WeaklyLinkedCharacters.get(fe4Char);
				if (referenceCharacter.isPlayable() && referenceCharacter.isGen1()) {
					referenceClass = predeterminedClasses.get(referenceCharacter);
					if (referenceClass.isPromoted() && !originalClass.isPromoted()) {
						List<FE4Data.CharacterClass> demotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(referenceClass.demotedClasses(staticChar.isFemale())));
						if (!demotedClasses.isEmpty()) {
							referenceClass = demotedClasses.get(rng.nextInt(demotedClasses.size()));
						}
					} else if (!referenceClass.isPromoted() && originalClass.isPromoted()) {
						List<FE4Data.CharacterClass> promotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(referenceClass.promotionClasses(staticChar.isFemale())));
						if (!promotedClasses.isEmpty()) {
							referenceClass = promotedClasses.get(rng.nextInt(promotedClasses.size()));
						}
					}
				}
			}
			
			if (fe4Char.isHealer() && options.retainHealers) {
				Collections.addAll(potentialClasses, originalClass.getClassPool(true, false, true, staticChar.isFemale(), false, false, false, FE4Data.Item.HEAL, null));
			} else {
				FE4Data.Item mustUseItem = fe4Char.requiresWeapon();
				if (!options.randomizeBlood) {
					// Limit classes if this option is disabled for those with major holy blood (Sigurd, Quan, Brigid, Lewyn (and technically Claud)).
					int blood1Value = staticChar.getHolyBlood1Value();
					int blood2Value = staticChar.getHolyBlood2Value();
					int blood3Value = staticChar.getHolyBlood3Value();
					// Blood 4 is only Loptous, which we're not dealing with player-side.
					
					List<HolyBloodSlot1> slot1Blood = FE4Data.HolyBloodSlot1.slot1HolyBlood(blood1Value);
					List<HolyBloodSlot2> slot2Blood = FE4Data.HolyBloodSlot2.slot2HolyBlood(blood2Value);
					List<HolyBloodSlot3> slot3Blood = FE4Data.HolyBloodSlot3.slot3HolyBlood(blood3Value);
					if (slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
						mustUseItem = slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType().weaponType.getBasic();
					} else if (slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
						mustUseItem = slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType().weaponType.getBasic();
					} else if (slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
						mustUseItem = slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType().weaponType.getBasic();
					}
				}
				if (fe4Char.mustLoseToCharacters().length > 0) {
					// I'm not sure who would fit here, but we'll just force them to horseback. Armored units are male only, so a mix of genders is problematic.
					if (referenceClass != null) {
						Collections.addAll(potentialClasses, referenceClass.getClassPool(true, false, true, staticChar.isFemale(), true, fe4Char.requiresAttack(), true, mustUseItem, Item.HORSESLAYER));
					} else {
						Collections.addAll(potentialClasses, originalClass.getClassPool(false, false, true, staticChar.isFemale(), true, fe4Char.requiresAttack(), true, mustUseItem, Item.HORSESLAYER));
					}
				} else {
					if (referenceClass != null) {
						Collections.addAll(potentialClasses, referenceClass.getClassPool(true, false, false, staticChar.isFemale(), false, fe4Char.requiresAttack(), false, mustUseItem, null));
					} else {
						Collections.addAll(potentialClasses, originalClass.getClassPool(false, false, false, staticChar.isFemale(), false, fe4Char.requiresAttack(), false, mustUseItem, null));
					}
				}
			}
			
			if (potentialClasses.isEmpty()) { continue; }
			
			targetClass = potentialClasses.get(rng.nextInt(potentialClasses.size()));
			setStaticCharacterToClass(options, staticChar, targetClass, charData, itemMap, predeterminedClasses, requiredItems, rng);
			
			for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
				predeterminedClasses.put(linked, targetClass);
			}
			
			predeterminedClasses.put(fe4Char, targetClass);
		}
		
		// Gen 2 - Children/Substitutes
		List<FE4ChildCharacter> gen2Children = charData.getAllChildren();
		for (FE4ChildCharacter child : gen2Children) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(child.getCharacterID());
			if (fe4Char == null || blacklistedCharacters.contains(fe4Char)) { continue; }
			FE4Data.Character parentChar = fe4Char.primaryParent();
			FE4StaticCharacter parent = charData.getStaticCharacter(parentChar);
			
			FE4Data.CharacterClass targetClass = predeterminedClasses.get(fe4Char);
			if (targetClass != null) {
				setChildCharacterToClass(options, child, parent, targetClass, itemMap, rng);
				continue;
			}
			
			FE4Data.Character referenceCharacter = fe4Char.getGen1Analogue();
			boolean requiresWeakness = fe4Char.mustLoseToCharacters().length > 0;
			boolean restrictedHealer = fe4Char.isHealer() && options.retainHealers;
			
			FE4Data.CharacterClass currentClass = FE4Data.CharacterClass.valueOf(child.getClassID());
			
			if (options.childOption == ChildOptions.MATCH_STRICT) {
				targetClass = predeterminedClasses.get(referenceCharacter);
				if (targetClass != null) {
					if (targetClass.isPromoted() && !currentClass.isPromoted()) {
						FE4Data.CharacterClass[] demotedClasses = targetClass.demotedClasses(child.isFemale());
						if (demotedClasses.length > 0) {
							targetClass = demotedClasses[rng.nextInt(demotedClasses.length)];
						}
					} else if (!targetClass.isPromoted() && currentClass.isPromoted()) {
						FE4Data.CharacterClass[] promotedClasses = targetClass.promotionClasses(child.isFemale());
						if (promotedClasses.length > 0) {
							targetClass = promotedClasses[rng.nextInt(promotedClasses.length)];
						}
					}
				}
				
				if (currentClass.isFlier()) {
					// Override for Altena, who needs to remain flying to not break stuff.
					FE4Data.CharacterClass[] classPool = currentClass.getClassPool(true, false, true, child.isFemale(), false, fe4Char.requiresAttack(), false, fe4Char.requiresWeapon(), null);
					if (classPool.length > 0) {
						targetClass = classPool[rng.nextInt(classPool.length)];
					}
				}
			} else if (options.childOption == ChildOptions.MATCH_LOOSE) {
				FE4Data.CharacterClass referenceClass = predeterminedClasses.get(referenceCharacter);
				if (referenceClass != null) {
					if (referenceClass.isPromoted() && !currentClass.isPromoted()) {
						FE4Data.CharacterClass[] demotedClasses = referenceClass.demotedClasses(child.isFemale());
						if (demotedClasses.length > 0) {
							referenceClass = demotedClasses[rng.nextInt(demotedClasses.length)];
						}
					} else if (!referenceClass.isPromoted() && currentClass.isPromoted()) {
						FE4Data.CharacterClass[] promotedClasses = referenceClass.promotionClasses(child.isFemale());
						if (promotedClasses.length > 0) {
							referenceClass = promotedClasses[rng.nextInt(promotedClasses.length)];
						}
					}
				}
				FE4Data.CharacterClass[] pool = referenceClass.getClassPool(true, false, true, child.isFemale(), requiresWeakness, fe4Char.requiresAttack(), false, restrictedHealer ? Item.HEAL : fe4Char.requiresWeapon(), null);
				if (pool.length > 0) {
					targetClass = pool[rng.nextInt(pool.length)];
				}
			} else {
				FE4Data.CharacterClass referenceClass = FE4Data.CharacterClass.valueOf(child.getClassID());
				FE4Data.CharacterClass[] pool = referenceClass.getClassPool(false, false, false, child.isFemale(), requiresWeakness, fe4Char.requiresAttack(), false, restrictedHealer ? Item.HEAL : fe4Char.requiresWeapon(), null);
				if (pool.length > 0) {
					targetClass = pool[rng.nextInt(pool.length)];
				}
			}
			
			if (targetClass != null) {
				setChildCharacterToClass(options, child, parent, targetClass, itemMap, rng);
				
				for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
					predeterminedClasses.put(linked, targetClass);
				}
				predeterminedClasses.put(fe4Char, targetClass);
				
				FE4Data.CharacterClass referenceClass = targetClass;
				
				FE4Data.Character sub = fe4Char.substituteForChild();
				if (sub != null) {
					FE4StaticCharacter subChar = charData.getStaticCharacter(sub);
					if (subChar != null) {
						FE4Data.CharacterClass[] pool = referenceClass.getClassPool(true, false, true, subChar.isFemale(), requiresWeakness, fe4Char.requiresAttack(), false, restrictedHealer ? Item.HEAL : sub.requiresWeapon(), null);
						FE4Data.CharacterClass subClass = referenceClass; // Use this as a fallback.
						if (pool.length > 0) {
							 subClass = pool[rng.nextInt(pool.length)];
						}
						
						setStaticCharacterToClass(options, subChar, subClass, charData, itemMap, predeterminedClasses, requiredItems, rng);
						
						predeterminedClasses.put(sub, subClass);
					}
				}
				
			} else {
				System.err.println("No classes in the class pool for Child " + fe4Char.toString());
			}
		}
		
		List<FE4Data.HolyBloodSlot1> fullMinor1 = new ArrayList<FE4Data.HolyBloodSlot1>(Arrays.asList(FE4Data.HolyBloodSlot1.MINOR_BALDR, FE4Data.HolyBloodSlot1.MINOR_DAIN, FE4Data.HolyBloodSlot1.MINOR_NAGA, FE4Data.HolyBloodSlot1.MINOR_NJORUN));
		List<FE4Data.HolyBloodSlot2> fullMinor2 = new ArrayList<FE4Data.HolyBloodSlot2>(Arrays.asList(FE4Data.HolyBloodSlot2.MINOR_FJALAR, FE4Data.HolyBloodSlot2.MINOR_NEIR, FE4Data.HolyBloodSlot2.MINOR_OD, FE4Data.HolyBloodSlot2.MINOR_ULIR));
		List<FE4Data.HolyBloodSlot3> fullMinor3 = new ArrayList<FE4Data.HolyBloodSlot3>(Arrays.asList(FE4Data.HolyBloodSlot3.MINOR_BRAGI, FE4Data.HolyBloodSlot3.MINOR_FORSETI, FE4Data.HolyBloodSlot3.MINOR_HEZUL, FE4Data.HolyBloodSlot3.MINOR_THRUD));
		if (options.shopOption == ShopOptions.ADJUST_TO_MATCH) {
			Set<Integer> inventoryIndices = FE4Data.Shops.CHAPTER_1.inventoryIDs;
			Set<FE4Data.Item> possibleWeapons =  new HashSet<FE4Data.Item>();
			
			for (FE4Data.Character fe4Char : predeterminedClasses.keySet()) {
				if (fe4Char.joinChapter() == 0) { 
					FE4Data.CharacterClass charClass = predeterminedClasses.get(fe4Char);
					possibleWeapons.addAll(Arrays.asList(charClass.usableItems(fullMinor1, fullMinor2, fullMinor3))); 
				}
			}
			possibleWeapons.removeIf(item -> (!item.isWeapon()));
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons); 
				for (Integer i : inventoryIndices) {
					FE4Data.Item currentItem = itemMap.getItemAtIndex(i);
					if (currentItem.isRing()) { continue; }
					FE4Data.Item randomItem = currentList.get(rng.nextInt(currentList.size()));
					itemMap.setItemAtIndex(i, randomItem);
				}
			}
			
			inventoryIndices = FE4Data.Shops.CHAPTER_2.inventoryIDs;
			for (FE4Data.Character fe4Char : predeterminedClasses.keySet()) {
				if (fe4Char.joinChapter() == 1) {
					FE4Data.CharacterClass charClass = predeterminedClasses.get(fe4Char);
					possibleWeapons.addAll(Arrays.asList(charClass.usableItems(fullMinor1, fullMinor2, fullMinor3)));
				}
			}
			
			possibleWeapons.removeIf(item -> (!item.isWeapon()));
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons); 
				for (Integer i : inventoryIndices) {
					FE4Data.Item currentItem = itemMap.getItemAtIndex(i);
					if (currentItem.isRing()) { continue; }
					FE4Data.Item randomItem = currentList.get(rng.nextInt(currentList.size()));
					itemMap.setItemAtIndex(i, randomItem);
				}
			}
			
			inventoryIndices = FE4Data.Shops.CHAPTER_3.inventoryIDs;
			for (FE4Data.Character fe4Char : predeterminedClasses.keySet()) {
				if (fe4Char.joinChapter() == 2) {
					FE4Data.CharacterClass charClass = predeterminedClasses.get(fe4Char);
					possibleWeapons.addAll(Arrays.asList(charClass.usableItems(fullMinor1, fullMinor2, fullMinor3)));
				}
			}
			
			possibleWeapons.removeIf(item -> (!item.isWeapon()));
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons); 
				for (Integer i : inventoryIndices) {
					FE4Data.Item currentItem = itemMap.getItemAtIndex(i);
					if (currentItem.isRing()) { continue; }
					FE4Data.Item randomItem = currentList.get(rng.nextInt(currentList.size()));
					itemMap.setItemAtIndex(i, randomItem);
				}
			}
			
			inventoryIndices = FE4Data.Shops.CHAPTER_4.inventoryIDs;
			for (FE4Data.Character fe4Char : predeterminedClasses.keySet()) {
				if (fe4Char.joinChapter() == 3) {
					FE4Data.CharacterClass charClass = predeterminedClasses.get(fe4Char);
					possibleWeapons.addAll(Arrays.asList(charClass.usableItems(fullMinor1, fullMinor2, fullMinor3)));
				}
			}
			
			possibleWeapons.removeIf(item -> (!item.isWeapon()));
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons); 
				for (Integer i : inventoryIndices) {
					FE4Data.Item currentItem = itemMap.getItemAtIndex(i);
					if (currentItem.isRing()) { continue; }
					FE4Data.Item randomItem = currentList.get(rng.nextInt(currentList.size()));
					itemMap.setItemAtIndex(i, randomItem);
				}
			}
			
			inventoryIndices = FE4Data.Shops.CHAPTER_5.inventoryIDs;
			for (FE4Data.Character fe4Char : predeterminedClasses.keySet()) {
				if (fe4Char.joinChapter() == 4) {
					FE4Data.CharacterClass charClass = predeterminedClasses.get(fe4Char);
					possibleWeapons.addAll(Arrays.asList(charClass.usableItems(fullMinor1, fullMinor2, fullMinor3)));
				}
			}
			
			possibleWeapons.removeIf(item -> (!item.isWeapon()));
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons); 
				for (Integer i : inventoryIndices) {
					FE4Data.Item currentItem = itemMap.getItemAtIndex(i);
					if (currentItem.isRing()) { continue; }
					FE4Data.Item randomItem = currentList.get(rng.nextInt(currentList.size()));
					itemMap.setItemAtIndex(i, randomItem);
				}
			}
			
			inventoryIndices = FE4Data.Shops.CHAPTER_6.inventoryIDs;
			possibleWeapons.clear();
			for (FE4Data.Character fe4Char : predeterminedClasses.keySet()) {
				if (fe4Char.joinChapter() == 6) {
					FE4Data.CharacterClass charClass = predeterminedClasses.get(fe4Char);
					possibleWeapons.addAll(Arrays.asList(charClass.usableItems(fullMinor1, fullMinor2, fullMinor3)));
				}
			}
			
			possibleWeapons.removeIf(item -> (!item.isWeapon()));
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons); 
				for (Integer i : inventoryIndices) {
					FE4Data.Item currentItem = itemMap.getItemAtIndex(i);
					if (currentItem.isRing()) { continue; }
					FE4Data.Item randomItem = currentList.get(rng.nextInt(currentList.size()));
					itemMap.setItemAtIndex(i, randomItem);
				}
			}
			
			inventoryIndices = FE4Data.Shops.CHAPTER_7.inventoryIDs;
			for (FE4Data.Character fe4Char : predeterminedClasses.keySet()) {
				if (fe4Char.joinChapter() == 7) {
					FE4Data.CharacterClass charClass = predeterminedClasses.get(fe4Char);
					possibleWeapons.addAll(Arrays.asList(charClass.usableItems(fullMinor1, fullMinor2, fullMinor3)));
				}
			}
			
			possibleWeapons.removeIf(item -> (!item.isWeapon()));
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons); 
				for (Integer i : inventoryIndices) {
					FE4Data.Item currentItem = itemMap.getItemAtIndex(i);
					if (currentItem.isRing()) { continue; }
					FE4Data.Item randomItem = currentList.get(rng.nextInt(currentList.size()));
					itemMap.setItemAtIndex(i, randomItem);
				}
			}
			
			inventoryIndices = FE4Data.Shops.CHAPTER_8.inventoryIDs;
			for (FE4Data.Character fe4Char : predeterminedClasses.keySet()) {
				if (fe4Char.joinChapter() == 8) {
					FE4Data.CharacterClass charClass = predeterminedClasses.get(fe4Char);
					possibleWeapons.addAll(Arrays.asList(charClass.usableItems(fullMinor1, fullMinor2, fullMinor3)));
				}
			}
			
			possibleWeapons.removeIf(item -> (!item.isWeapon()));
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons); 
				for (Integer i : inventoryIndices) {
					FE4Data.Item currentItem = itemMap.getItemAtIndex(i);
					if (currentItem.isRing()) { continue; }
					FE4Data.Item randomItem = currentList.get(rng.nextInt(currentList.size()));
					itemMap.setItemAtIndex(i, randomItem);
				}
			}
			
			inventoryIndices = FE4Data.Shops.CHAPTER_9.inventoryIDs;
			for (FE4Data.Character fe4Char : predeterminedClasses.keySet()) {
				if (fe4Char.joinChapter() == 9) {
					FE4Data.CharacterClass charClass = predeterminedClasses.get(fe4Char);
					possibleWeapons.addAll(Arrays.asList(charClass.usableItems(fullMinor1, fullMinor2, fullMinor3)));
				}
			}
			
			possibleWeapons.removeIf(item -> (!item.isWeapon()));
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons); 
				for (Integer i : inventoryIndices) {
					FE4Data.Item currentItem = itemMap.getItemAtIndex(i);
					if (currentItem.isRing()) { continue; }
					FE4Data.Item randomItem = currentList.get(rng.nextInt(currentList.size()));
					itemMap.setItemAtIndex(i, randomItem);
				}
			}
			
			inventoryIndices = FE4Data.Shops.CHAPTER_10.inventoryIDs;
			for (FE4Data.Character fe4Char : predeterminedClasses.keySet()) {
				if (fe4Char.joinChapter() == 10) {
					FE4Data.CharacterClass charClass = predeterminedClasses.get(fe4Char);
					possibleWeapons.addAll(Arrays.asList(charClass.usableItems(fullMinor1, fullMinor2, fullMinor3)));
				}
			}
			
			possibleWeapons.removeIf(item -> (!item.isWeapon()));
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons); 
				for (Integer i : inventoryIndices) {
					FE4Data.Item currentItem = itemMap.getItemAtIndex(i);
					if (currentItem.isRing()) { continue; }
					FE4Data.Item randomItem = currentList.get(rng.nextInt(currentList.size()));
					itemMap.setItemAtIndex(i, randomItem);
				}
			}
		} else if (options.shopOption == ShopOptions.RANDOMIZE) {
			List<Integer> inventoryIndices = new ArrayList<Integer>();
			for (FE4Data.Shops shop : FE4Data.Shops.values()) {
				inventoryIndices.addAll(shop.inventoryIDs);
			}
			
			List<FE4Data.Item> weapons = new ArrayList<FE4Data.Item>();
			weapons.addAll(FE4Data.Item.cWeapons);
			weapons.addAll(FE4Data.Item.bWeapons);
			weapons.addAll(FE4Data.Item.aWeapons);
			
			for (int index : inventoryIndices) {
				FE4Data.Item currentItem = itemMap.getItemAtIndex(index);
				if (currentItem.isRing()) { continue; }
				int randomIndex = rng.nextInt(weapons.size());
				FE4Data.Item randomWeapon = weapons.get(randomIndex);
				itemMap.setItemAtIndex(index, randomWeapon);
			}
		}
	}
	
	// Should be run after randomizing playable characters.
	public static void randomizeMinions(FE4ClassOptions options, CharacterDataLoader charData, ItemMapper itemMap, Random rng) {
		List<FE4EnemyCharacter> enemies = charData.getMinions();
		randomizeEnemies(options, enemies, charData, itemMap, rng);
	}
	
	private static class BloodArrays {
		public final List<FE4Data.HolyBloodSlot1> slot1Blood;
		public final List<FE4Data.HolyBloodSlot2> slot2Blood;
		public final List<FE4Data.HolyBloodSlot3> slot3Blood;
		
		public BloodArrays(FE4StaticCharacter character) {
			super();
			slot1Blood = FE4Data.HolyBloodSlot1.slot1HolyBlood(character.getHolyBlood1Value());
			slot2Blood = FE4Data.HolyBloodSlot2.slot2HolyBlood(character.getHolyBlood2Value());
			slot3Blood = FE4Data.HolyBloodSlot3.slot3HolyBlood(character.getHolyBlood3Value());
		}
	}
	
	// Should be run after randomizing enemies.
	public static void randomizeBosses(FE4ClassOptions options, CharacterDataLoader charData, ItemMapper itemMap, Random rng) {
		List<FE4EnemyCharacter> bosses = charData.getPlainBossCharacters();
		randomizeEnemies(options, bosses, charData, itemMap, rng);
		
		List<FE4StaticCharacter> holyBosses = charData.getHolyBossCharacters();
		Map<FE4Data.Character, FE4Data.CharacterClass> predeterminedClasses = new HashMap<FE4Data.Character, FE4Data.CharacterClass>();
		Map<FE4Data.Character, BloodArrays> predeterminedBlood = new HashMap<FE4Data.Character, BloodArrays>();
		
		for (FE4EnemyCharacter boss : bosses) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(boss.getCharacterID());
			FE4Data.CharacterClass charClass = FE4Data.CharacterClass.valueOf(boss.getClassID());
			predeterminedClasses.put(fe4Char, charClass);
		}
		
		for (FE4StaticCharacter holyBoss : holyBosses) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(holyBoss.getCharacterID());
			
			FE4Data.CharacterClass targetClass = predeterminedClasses.get(fe4Char);
			if (targetClass != null) {
				BloodArrays blood = predeterminedBlood.get(fe4Char);
				if (blood != null) {
					setHolyBossToClass(options, holyBoss, targetClass, blood.slot1Blood, blood.slot2Blood, blood.slot3Blood, charData, predeterminedClasses, itemMap, rng);
				} else {
					setHolyBossToClass(options, holyBoss, targetClass, null, null, null, charData, predeterminedClasses, itemMap, rng);
				}
				continue;
			}
			
			FE4Data.CharacterClass originalClass = FE4Data.CharacterClass.valueOf(holyBoss.getClassID());
			if (originalClass == null) { continue; } // Shouldn't be touching this class. Skip this character.
			
			// In the case that we have a weak requirement on a specific class (where two characters have to be somewhat similar), set that here.
			// Whoever gets randomized first chooses the weapon.
			// The second character should refer to the first character.
			FE4Data.CharacterClass referenceClass = null;
			if (FE4Data.WeaklyLinkedCharacters.containsKey(fe4Char)) {
				FE4Data.Character referenceCharacter = FE4Data.WeaklyLinkedCharacters.get(fe4Char);
				if (referenceCharacter.isPlayable() && referenceCharacter.isGen1()) {
					FE4StaticCharacter playableCharacter = charData.getStaticCharacter(referenceCharacter);
					referenceClass = FE4Data.CharacterClass.valueOf(playableCharacter.getClassID());
					if (referenceClass.isPromoted() && !originalClass.isPromoted()) {
						List<FE4Data.CharacterClass> demotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(referenceClass.demotedClasses(holyBoss.isFemale())));
						if (!demotedClasses.isEmpty()) {
							referenceClass = demotedClasses.get(rng.nextInt(demotedClasses.size()));
						}
					} else if (!referenceClass.isPromoted() && originalClass.isPromoted()) {
						List<FE4Data.CharacterClass> promotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(referenceClass.promotionClasses(holyBoss.isFemale())));
						if (!promotedClasses.isEmpty()) {
							referenceClass = promotedClasses.get(rng.nextInt(promotedClasses.size()));
						}
					}
				}
			}
			
			List<FE4Data.CharacterClass> potentialClasses = new ArrayList<FE4Data.CharacterClass>();
			
			FE4Data.Item mustUseItem = null;
			if (!options.randomizeBossBlood) {
				// Limit classes if this option is disabled for those with major holy blood (most endgame bosses).
				int blood1Value = holyBoss.getHolyBlood1Value();
				int blood2Value = holyBoss.getHolyBlood2Value();
				int blood3Value = holyBoss.getHolyBlood3Value();
				// Blood 4 is only Loptous, which we're not dealing with player-side.
				
				List<HolyBloodSlot1> slot1Blood = FE4Data.HolyBloodSlot1.slot1HolyBlood(blood1Value);
				List<HolyBloodSlot2> slot2Blood = FE4Data.HolyBloodSlot2.slot2HolyBlood(blood2Value);
				List<HolyBloodSlot3> slot3Blood = FE4Data.HolyBloodSlot3.slot3HolyBlood(blood3Value);
				
				if (slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
					mustUseItem = slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType().weaponType.getBasic();
				} else if (slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
					mustUseItem = slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType().weaponType.getBasic();
				} else if (slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
					mustUseItem = slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType().weaponType.getBasic();
				}
			}
			
			if (fe4Char.mustBeatCharacter().length > 0) {
				FE4Data.Character loser = fe4Char.mustBeatCharacter()[0];
				if (loser.isPlayable() && mustUseItem == null) {
					FE4StaticCharacter playableCharacter = charData.getStaticCharacter(loser);
					FE4Data.CharacterClass losingClass = FE4Data.CharacterClass.valueOf(playableCharacter.getClassID());
					List<FE4Data.Item> weapons = new ArrayList<FE4Data.Item>(Arrays.asList(losingClass.criticalWeaknessWeapons()));
					if (!weapons.isEmpty()) {
						mustUseItem = weapons.get(rng.nextInt(weapons.size()));
					}
				} else if (predeterminedClasses.containsKey(loser) && mustUseItem == null) {
					FE4Data.CharacterClass losingClass = predeterminedClasses.get(loser);
					List<FE4Data.Item> weapons = new ArrayList<FE4Data.Item>(Arrays.asList(losingClass.criticalWeaknessWeapons()));
					if (!weapons.isEmpty()) {
						mustUseItem = weapons.get(rng.nextInt(weapons.size()));
					}
				} else {
					// Defer to later?
				}
				
				if (mustUseItem == null && fe4Char.isGen2() && holyBoss.getEquipment3() != FE4Data.Item.NONE.ID) {
					mustUseItem = itemMap.getItemAtIndex(holyBoss.getEquipment3());
				}
				
				if (referenceClass != null) {
					Collections.addAll(potentialClasses, referenceClass.getClassPool(true, false, true, holyBoss.isFemale(), false, fe4Char.requiresAttack(), true, mustUseItem, null));
				} else {
					Collections.addAll(potentialClasses, originalClass.getClassPool(false, false, true, holyBoss.isFemale(), false, fe4Char.requiresAttack(), true, mustUseItem, null));
				}
			} else if (fe4Char.mustLoseToCharacters().length > 0) {
				FE4Data.Item loseToWeapon = null;
				for (FE4Data.Character otherWeaknesses : fe4Char.sharedWeaknesses()) {
					if (otherWeaknesses == fe4Char) { continue; }
					if (otherWeaknesses.isMinion()) {
						FE4EnemyCharacter minion = charData.getEnemyCharacter(otherWeaknesses);
						if (minion != null) {
							FE4Data.CharacterClass weakClass = FE4Data.CharacterClass.valueOf(minion.getClassID());
							List<FE4Data.Item> weakWeapons = new ArrayList<FE4Data.Item>(Arrays.asList(weakClass.criticalWeaknessWeapons()));
							if (!weakWeapons.isEmpty()) {
								loseToWeapon = weakWeapons.get(rng.nextInt(weakWeapons.size()));
								break;
							}
						}
					} else if (otherWeaknesses.isBoss() && predeterminedClasses.containsKey(otherWeaknesses)) {
						FE4Data.CharacterClass weakClass = predeterminedClasses.get(otherWeaknesses);
						List<FE4Data.Item> weakWeapons = new ArrayList<FE4Data.Item>(Arrays.asList(weakClass.criticalWeaknessWeapons()));
						if (!weakWeapons.isEmpty()) {
							loseToWeapon = weakWeapons.get(rng.nextInt(weakWeapons.size()));
							break;
						}
					}
				}
				
				if (mustUseItem == null && fe4Char.isGen2() && holyBoss.getEquipment3() != FE4Data.Item.NONE.ID) {
					mustUseItem = itemMap.getItemAtIndex(holyBoss.getEquipment3());
				}
				
				if (referenceClass != null) {
					Collections.addAll(potentialClasses, referenceClass.getClassPool(true, false, true, holyBoss.isFemale(), true, fe4Char.requiresAttack(), true, mustUseItem, loseToWeapon));
				} else {
					Collections.addAll(potentialClasses, originalClass.getClassPool(false, false, true, holyBoss.isFemale(), true, fe4Char.requiresAttack(), true, mustUseItem, loseToWeapon));
				}
			} else {
				if (mustUseItem == null && fe4Char.isGen2() && holyBoss.getEquipment3() != FE4Data.Item.NONE.ID) {
					mustUseItem = itemMap.getItemAtIndex(holyBoss.getEquipment3());
				}
				
				if (referenceClass != null) {
					Collections.addAll(potentialClasses, referenceClass.getClassPool(true, false, false, holyBoss.isFemale(), false, fe4Char.requiresAttack(), false, mustUseItem, null));
				} else {
					Collections.addAll(potentialClasses, originalClass.getClassPool(false, false, false, holyBoss.isFemale(), false, fe4Char.requiresAttack(), false, mustUseItem, null));
				}
			}
			
			if (potentialClasses.isEmpty()) { continue; }
			
			targetClass = potentialClasses.get(rng.nextInt(potentialClasses.size()));
			setHolyBossToClass(options, holyBoss, targetClass, charData, predeterminedClasses, itemMap, rng);
			
			for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
				predeterminedClasses.put(linked, targetClass);
				predeterminedBlood.put(linked, new BloodArrays(holyBoss));
			}
			
			// Set ourselves as predetermined, in the odd case that we run across ourself again.
			// Also useful for children in this case.
			predeterminedClasses.put(fe4Char, targetClass);
		}
	}
	
	// Can be run at any time.
	public static void randomizeArena(FE4ClassOptions options, CharacterDataLoader charData, Random rng) {
		List<FE4EnemyCharacter> arenaEnemies = charData.getArenaCombatants();
		for (FE4EnemyCharacter combatant : arenaEnemies) {
			FE4Data.Character arenaCharacter = FE4Data.Character.valueOf(combatant.getCharacterID());
			FE4Data.CharacterClass currentClass = FE4Data.CharacterClass.valueOf(combatant.getClassID());
			
			boolean requiresMelee = arenaCharacter.requiresMelee();
			boolean requiresRange = arenaCharacter.requiresRange();
			
			Set<FE4Data.CharacterClass> possibleClasses = new HashSet<FE4Data.CharacterClass>(Arrays.asList(currentClass.getClassPool(false, true, false, rng.nextInt(2) == 0, false, true, false, null, null)));
			if (requiresMelee) {
				possibleClasses.removeAll(FE4Data.CharacterClass.rangedOnlyClasses);
			} else if (requiresRange) {
				possibleClasses.retainAll(FE4Data.CharacterClass.rangedOnlyClasses);
			}
			
			List<FE4Data.CharacterClass> classList = new ArrayList<FE4Data.CharacterClass>(possibleClasses);
			
			FE4Data.CharacterClass targetClass = classList.get(rng.nextInt(classList.size()));
			FE4Data.Item item1 = null;
			FE4Data.Item item2 = null;
			
			boolean hasMelee = false;
			boolean hasRange = false;
			
			if (requiresMelee) {
				Set<FE4Data.Item> possibleWeapons = new HashSet<FE4Data.Item>(Arrays.asList(targetClass.usableItems(null, null, null)));
				possibleWeapons.removeAll(FE4Data.Item.staves);
				possibleWeapons.retainAll(FE4Data.Item.meleeWeapons);
				List<FE4Data.Item> weaponList = new ArrayList<FE4Data.Item>(possibleWeapons);
				item1 = weaponList.get(rng.nextInt(weaponList.size()));
				hasMelee = true;
				hasRange = FE4Data.Item.rangedWeapons.contains(item1);
			} else { // Requires range.
				Set<FE4Data.Item> possibleWeapons = new HashSet<FE4Data.Item>(Arrays.asList(targetClass.usableItems(null, null, null)));
				possibleWeapons.removeAll(FE4Data.Item.staves);
				possibleWeapons.retainAll(FE4Data.Item.rangedWeapons);
				possibleWeapons.removeAll(FE4Data.Item.meleeWeapons);
				List<FE4Data.Item> weaponList = new ArrayList<FE4Data.Item>(possibleWeapons);
				item1 = weaponList.get(rng.nextInt(weaponList.size()));
				hasRange = true;
				hasMelee = FE4Data.Item.meleeWeapons.contains(item1);
			}
			
			if (requiresMelee && !hasMelee) {
				Set<FE4Data.Item> possibleWeapons = new HashSet<FE4Data.Item>(Arrays.asList(targetClass.usableItems(null, null, null)));
				possibleWeapons.removeAll(FE4Data.Item.staves);
				possibleWeapons.retainAll(FE4Data.Item.meleeWeapons);
				possibleWeapons.removeAll(FE4Data.Item.rangedWeapons);
				List<FE4Data.Item> weaponList = new ArrayList<FE4Data.Item>(possibleWeapons);
				item2 = weaponList.get(rng.nextInt(weaponList.size()));
				hasMelee = true;
			}
			else if (requiresRange && !hasRange) {
				Set<FE4Data.Item> possibleWeapons = new HashSet<FE4Data.Item>(Arrays.asList(targetClass.usableItems(null, null, null)));
				possibleWeapons.removeAll(FE4Data.Item.staves);
				possibleWeapons.retainAll(FE4Data.Item.rangedWeapons);
				List<FE4Data.Item> weaponList = new ArrayList<FE4Data.Item>(possibleWeapons);
				item2 = weaponList.get(rng.nextInt(weaponList.size()));
				hasRange = true;
			} else {
				// Maybe a ring for item2?
				boolean hasRing = rng.nextInt(3) < 2;
				if (hasRing) {
					List<FE4Data.Item> rings = new ArrayList<FE4Data.Item>(FE4Data.Item.statRings);
					FE4Data.Item ring = rings.get(rng.nextInt(rings.size()));
					if (!targetClass.primaryAttackIsMagic() && ring == FE4Data.Item.MAGIC_RING) {
						ring = FE4Data.Item.POWER_RING;
					} else if (!targetClass.primaryAttackIsStrength() && ring == FE4Data.Item.POWER_RING) {
						ring = FE4Data.Item.MAGIC_RING;
					}
					
					item2 = ring;
				}
			}
			
			setEnemyCharacterToClass(options, combatant, targetClass, item1, item2, null);
		}
	}
	
	private static void randomizeEnemies(FE4ClassOptions options, List<FE4EnemyCharacter> enemies, CharacterDataLoader charData, ItemMapper itemMap, Random rng) {
		Map<FE4Data.Character, FE4Data.CharacterClass> predeterminedClasses = new HashMap<FE4Data.Character, FE4Data.CharacterClass>();
		
		List<FE4EnemyCharacter> deferredEnemies = new ArrayList<FE4EnemyCharacter>();
		Map<FE4Data.Character, FE4Data.Item> weaponsBeatingCharacter = new HashMap<FE4Data.Character, FE4Data.Item>();
		
		for (FE4EnemyCharacter enemy : enemies) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(enemy.getCharacterID());
			
			if (predeterminedClasses.containsKey(fe4Char)) {
				FE4Data.Item mustUseItem = null;
				if (fe4Char.mustBeatCharacter().length > 0) {
					for (FE4Data.Character loser : fe4Char.mustBeatCharacter()) {
						if (weaponsBeatingCharacter.containsKey(loser)) {
							mustUseItem = weaponsBeatingCharacter.get(loser);
							break;
						}
					}
				}
				if (mustUseItem != null) {
					setEnemyCharacterToClass(options, enemy, predeterminedClasses.get(fe4Char), mustUseItem, itemMap);
				} else { 
					setEnemyCharacterToClass(options, enemy, fe4Char, predeterminedClasses.get(fe4Char), itemMap, rng);
				}
				continue;
			}
			
			FE4Data.Item mustUseItem = fe4Char.requiresWeapon();
			// Gen 2 enemies should adjust their class to match any dropped item they might have. (Gen 1 is allowed to be the setter of the item.)
			if (fe4Char.isGen2() && mustUseItem == null && enemy.getDropableEquipment() != FE4Data.Item.NONE.ID) {
				FE4Data.Item droppedItem = itemMap.getItemAtIndex(enemy.getDropableEquipment());
				if (droppedItem.isWeapon()) {
					mustUseItem = droppedItem; 
				}
			}
			if (fe4Char.mustBeatCharacter().length > 0) {
				FE4Data.Character loser = fe4Char.mustBeatCharacter()[0];
				FE4Data.CharacterClass loserClass = null;
				if (loser.isPlayable()) {
					if (loser.isChild()) {
						FE4ChildCharacter child = charData.getChildCharacter(loser);
						if (child != null) { loserClass = FE4Data.CharacterClass.valueOf(child.getClassID()); }
					} else {
						FE4StaticCharacter staticChar = charData.getStaticCharacter(loser);
						if (staticChar != null) { loserClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID()); }
					}
				} else {
					if (predeterminedClasses.containsKey(loser)) {
						loserClass = predeterminedClasses.get(loser);
					} else {
						FE4EnemyCharacter loserEnemy = charData.getEnemyCharacter(loser);
						if (loserEnemy != null && loserEnemy.hasCommittedChanges()) {
							loserClass = FE4Data.CharacterClass.valueOf(loserEnemy.getClassID());
						}
					}
				}
				if (loserClass == null) {
					// Wait until we've done all the other enemies before doing this one.
					deferredEnemies.add(enemy);
					continue;
				} else {
					FE4Data.Item[] weaponPool = loserClass.criticalWeaknessWeapons();
					if (weaponPool.length > 0) {
						mustUseItem = weaponPool[rng.nextInt(weaponPool.length)];
					}
				}
			}
			
			FE4Data.CharacterClass currentClass = FE4Data.CharacterClass.valueOf(enemy.getClassID());
			if (currentClass == null) { continue; }
			
			List<FE4Data.CharacterClass> classPool = new ArrayList<FE4Data.CharacterClass>();
			FE4Data.Item mustLoseToWeapon = null;
			if (fe4Char.sharedWeaknesses().length > 0) {
				for (FE4Data.Character weakChar : fe4Char.sharedWeaknesses()) {
					if (weakChar == fe4Char) { continue; }
					if (weaponsBeatingCharacter.containsKey(weakChar)) {
						mustLoseToWeapon = weaponsBeatingCharacter.get(weakChar);
						break;
					}
				}
			}
			Collections.addAll(classPool, currentClass.getClassPool(false, true, true, rng.nextInt(4) == 0, fe4Char.mustLoseToCharacters().length > 0, true, false, mustUseItem, mustLoseToWeapon));
			
			classPool.removeAll(FE4Data.CharacterClass.advancedClasses);
			
			if (classPool.isEmpty()) {
				continue;
			}
			
			FE4Data.CharacterClass targetClass = classPool.get(rng.nextInt(classPool.size()));
			// Put reduced weight on fliers, since they're maybe a bit too powerful. Re-randomize once if they get fliers the first time
			if (targetClass.isFlier()) { 
				targetClass = classPool.get(rng.nextInt(classPool.size()));
			}
			
			if (fe4Char.mustLoseToCharacters().length > 0) {
				FE4Data.Item[] winningWeapons = targetClass.criticalWeaknessWeapons();
				if (winningWeapons.length > 0) {
					weaponsBeatingCharacter.put(fe4Char, weaponsBeatingCharacter.put(fe4Char, winningWeapons[rng.nextInt(winningWeapons.length)]));
				}
			}
			
			if (mustUseItem != null) {
				setEnemyCharacterToClass(options, enemy, targetClass, mustUseItem, itemMap);
			} else {
				setEnemyCharacterToClass(options, enemy, fe4Char, targetClass, itemMap, rng);
			}
			
			for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
				predeterminedClasses.put(linked, targetClass);
			}
		}
		
		for (FE4EnemyCharacter deferred : deferredEnemies) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(deferred.getCharacterID());
			
			if (predeterminedClasses.containsKey(fe4Char)) {
				FE4Data.Item mustUseItem = null;
				if (fe4Char.mustBeatCharacter().length > 0) {
					for (FE4Data.Character loser : fe4Char.mustBeatCharacter()) {
						if (weaponsBeatingCharacter.containsKey(loser)) {
							mustUseItem = weaponsBeatingCharacter.get(loser);
							break;
						}
					}
				}
				if (mustUseItem != null) {
					setEnemyCharacterToClass(options, deferred, predeterminedClasses.get(fe4Char), mustUseItem, itemMap);
				} else {
					setEnemyCharacterToClass(options, deferred, fe4Char, predeterminedClasses.get(fe4Char), itemMap, rng);
				}
				continue;
			}
			
			FE4Data.Item mustUseItem = fe4Char.requiresWeapon();
			if (fe4Char.mustBeatCharacter().length > 0) {
				FE4Data.Character loser = fe4Char.mustBeatCharacter()[0];
				FE4Data.CharacterClass loserClass = null;
				if (loser.isPlayable()) {
					if (loser.isChild()) {
						FE4ChildCharacter child = charData.getChildCharacter(loser);
						if (child != null) { loserClass = FE4Data.CharacterClass.valueOf(child.getClassID()); }
					} else {
						FE4StaticCharacter staticChar = charData.getStaticCharacter(loser);
						if (staticChar != null) { loserClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID()); }
					}
				} else {
					if (predeterminedClasses.containsKey(loser)) {
						loserClass = predeterminedClasses.get(loser);
					} else {
						FE4EnemyCharacter loserEnemy = charData.getEnemyCharacter(loser);
						if (loserEnemy != null && loserEnemy.hasCommittedChanges()) {
							loserClass = FE4Data.CharacterClass.valueOf(loserEnemy.getClassID());
						}
					}
					
				}
				if (loserClass == null) {
					// This shouldn't happen again...
					// But if it does, just go ahead and randomize it.
				} else {
					FE4Data.Item[] weaponPool = loserClass.criticalWeaknessWeapons();
					if (weaponPool.length > 0) {
						mustUseItem = weaponPool[rng.nextInt(weaponPool.length)];
					}
				}
			}
			
			FE4Data.CharacterClass currentClass = FE4Data.CharacterClass.valueOf(deferred.getClassID());
			if (currentClass == null) { continue; }
			
			List<FE4Data.CharacterClass> classPool = new ArrayList<FE4Data.CharacterClass>();
			FE4Data.Item mustLoseToWeapon = null;
			if (fe4Char.sharedWeaknesses().length > 0) {
				for (FE4Data.Character weakChar : fe4Char.sharedWeaknesses()) {
					if (weakChar == fe4Char) { continue; }
					if (weaponsBeatingCharacter.containsKey(weakChar)) {
						mustLoseToWeapon = weaponsBeatingCharacter.get(weakChar);
						break;
					}
				}
			}
			Collections.addAll(classPool, currentClass.getClassPool(false, true, true, rng.nextInt(4) == 0, fe4Char.mustLoseToCharacters().length > 0, true, false, mustUseItem, mustLoseToWeapon));
			
			classPool.removeAll(FE4Data.CharacterClass.advancedClasses);
			if (classPool.isEmpty()) {
				continue;
			}
			
			FE4Data.CharacterClass targetClass = classPool.get(rng.nextInt(classPool.size()));
			// Put reduced weight on fliers, since they're maybe a bit too powerful. Re-randomize once if they get fliers the first time
			if (targetClass.isFlier()) { 
				targetClass = classPool.get(rng.nextInt(classPool.size()));
			}
			if (fe4Char.mustLoseToCharacters().length > 0) {
				FE4Data.Item[] winningWeapons = targetClass.criticalWeaknessWeapons();
				if (winningWeapons.length > 0) {
					weaponsBeatingCharacter.put(fe4Char, weaponsBeatingCharacter.put(fe4Char, winningWeapons[rng.nextInt(winningWeapons.length)]));
				}
			}
			
			if (mustUseItem != null) {
				setEnemyCharacterToClass(options, deferred, targetClass, mustUseItem, itemMap);
			} else {
				setEnemyCharacterToClass(options, deferred, fe4Char, targetClass, itemMap, rng);
			}
			
			for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
				predeterminedClasses.put(linked, targetClass);
			}
		}
	}
	
	private static void setHolyBossToClass(FE4ClassOptions options, FE4StaticCharacter holyBoss, FE4Data.CharacterClass targetClass, CharacterDataLoader charData, Map<FE4Data.Character, FE4Data.CharacterClass> predeterminedClasses, ItemMapper itemMap, Random rng) {
		FE4Data.CharacterClass oldClass = FE4Data.CharacterClass.valueOf(holyBoss.getClassID());
		boolean wasSTRBased = oldClass.primaryAttackIsStrength();
		boolean wasMAGBased = oldClass.primaryAttackIsMagic();
		
		boolean isSTRBased = targetClass.primaryAttackIsStrength();
		boolean isMAGBased = targetClass.primaryAttackIsMagic();
		
		if ((wasSTRBased && !wasMAGBased && isMAGBased && !isSTRBased) || (wasMAGBased && !wasMAGBased && isSTRBased && !isMAGBased)) {
			// Swap in the case that we've randomized across the STR/MAG split.
			int oldSTR = holyBoss.getSTRGrowth();
			holyBoss.setSTRGrowth(holyBoss.getMAGGrowth());
			holyBoss.setMAGGrowth(oldSTR);
			
			oldSTR = holyBoss.getBaseSTR();
			holyBoss.setBaseSTR(holyBoss.getBaseMAG());
			holyBoss.setBaseMAG(oldSTR);
		}
		
		holyBoss.setClassID(targetClass.ID);
		
		int blood1Value = holyBoss.getHolyBlood1Value();
		int blood2Value = holyBoss.getHolyBlood2Value();
		int blood3Value = holyBoss.getHolyBlood3Value();
		// Blood 4 is only Loptous, which we're not dealing with player-side.
		
		List<HolyBloodSlot1> slot1Blood = FE4Data.HolyBloodSlot1.slot1HolyBlood(blood1Value);
		List<HolyBloodSlot2> slot2Blood = FE4Data.HolyBloodSlot2.slot2HolyBlood(blood2Value);
		List<HolyBloodSlot3> slot3Blood = FE4Data.HolyBloodSlot3.slot3HolyBlood(blood3Value);
		
		boolean hasMajorBlood = false;
		FE4Data.HolyBlood majorBloodType = null;
		if (slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
			majorBloodType = slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType();
			hasMajorBlood = true;
		}
		if (!hasMajorBlood && slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
			majorBloodType = slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType();
			hasMajorBlood = true;
		}
		if (!hasMajorBlood && slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
			majorBloodType = slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType();
			hasMajorBlood = true;
		}
		
		if (options.randomizeBossBlood) {
			// Anybody with major blood is keeping major blood, but that blood might have to be adjusted.
			if (hasMajorBlood && majorBloodType != null) {
				slot1Blood.removeIf(blood -> (blood.isMajor()));
				slot2Blood.removeIf(blood -> (blood.isMajor()));
				slot3Blood.removeIf(blood -> (blood.isMajor()));
				
				List<FE4Data.HolyBlood> bloodOptions = Arrays.asList(targetClass.supportedHolyBlood());
				if (!bloodOptions.isEmpty()) {
					FE4Data.HolyBlood newMajorBlood = bloodOptions.get(rng.nextInt(bloodOptions.size()));
					FE4Data.HolyBloodSlot1 slot1 = FE4Data.HolyBloodSlot1.blood(newMajorBlood, true);
					FE4Data.HolyBloodSlot2 slot2 = FE4Data.HolyBloodSlot2.blood(newMajorBlood, true);
					FE4Data.HolyBloodSlot3 slot3 = FE4Data.HolyBloodSlot3.blood(newMajorBlood, true);
					
					if (slot1 != null) { 
						slot1Blood.add(slot1);
					} else if (slot2 != null) { 
						slot2Blood.add(slot2);
					} else if (slot3 != null) {
						slot3Blood.add(slot3);
					}
					
					majorBloodType = newMajorBlood;
				}
			}
			
			// Look for Minor blood now.
			int minorBloodCount = 0;
			minorBloodCount += slot1Blood.stream().filter(blood -> (blood.isMajor() == false)).count();
			minorBloodCount += slot2Blood.stream().filter(blood -> (blood.isMajor() == false)).count();
			minorBloodCount += slot3Blood.stream().filter(blood -> (blood.isMajor() == false)).count();
			
			slot1Blood.removeIf(blood -> (blood.isMajor() == false));
			slot2Blood.removeIf(blood -> (blood.isMajor() == false));
			slot3Blood.removeIf(blood -> (blood.isMajor() == false));
			
			List<HolyBlood> bloodOptions = new ArrayList<HolyBlood>(Arrays.asList(HolyBlood.values()));
			if (majorBloodType != null) {
				bloodOptions.remove(majorBloodType);
			}
			boolean hasFavoredBlood = false;
			for (int i = 0; i < minorBloodCount; i++) {
				FE4Data.HolyBlood blood = null;
				FE4Data.HolyBlood[] favoredBlood = targetClass.supportedHolyBlood();
				if (!hasFavoredBlood && favoredBlood.length > 0 && rng.nextInt(2) == 0) {
					// Pull from the blood options based on class. (Can only happen once)
					blood = favoredBlood[rng.nextInt(favoredBlood.length)];
					hasFavoredBlood = true;
				} else {
					// Pull from the remaining list.
					blood = bloodOptions.get(rng.nextInt(bloodOptions.size()));
				}
				
				if (blood == null) { break; }
				
				FE4Data.HolyBloodSlot1 slot1 = FE4Data.HolyBloodSlot1.blood(blood, false);
				FE4Data.HolyBloodSlot2 slot2 = FE4Data.HolyBloodSlot2.blood(blood, false);
				FE4Data.HolyBloodSlot3 slot3 = FE4Data.HolyBloodSlot3.blood(blood, false);
				
				if (slot1 != null) { slot1Blood.add(slot1); }
				if (slot2 != null) { slot2Blood.add(slot2); }
				if (slot3 != null) { slot3Blood.add(slot3); }
				
				bloodOptions.remove(blood);
			}
			
			setHolyBossToClass(options, holyBoss, targetClass, slot1Blood, slot2Blood, slot3Blood, charData, predeterminedClasses, itemMap, rng);
		}
	}
	
	private static void setHolyBossToClass(FE4ClassOptions options, FE4StaticCharacter holyBoss, FE4Data.CharacterClass targetClass, List<FE4Data.HolyBloodSlot1> slot1Blood, List<FE4Data.HolyBloodSlot2> slot2Blood, List<FE4Data.HolyBloodSlot3> slot3Blood, CharacterDataLoader charData, Map<FE4Data.Character, FE4Data.CharacterClass> predeterminedClasses, ItemMapper itemMap, Random rng) {
		
		FE4Data.Character fe4Char = FE4Data.Character.valueOf(holyBoss.getCharacterID());
		
		holyBoss.setClassID(targetClass.ID);
		
		if (slot1Blood == null) { slot1Blood = new ArrayList<FE4Data.HolyBloodSlot1>(); }
		if (slot2Blood == null) { slot2Blood = new ArrayList<FE4Data.HolyBloodSlot2>(); }
		if (slot3Blood == null) { slot3Blood = new ArrayList<FE4Data.HolyBloodSlot3>(); }
		
		holyBoss.setHolyBlood1Value(FE4Data.HolyBloodSlot1.valueForSlot1HolyBlood(slot1Blood));
		holyBoss.setHolyBlood2Value(FE4Data.HolyBloodSlot2.valueForSlot2HolyBlood(slot2Blood));
		holyBoss.setHolyBlood3Value(FE4Data.HolyBloodSlot3.valueForSlot3HolyBlood(slot3Blood));
		
		boolean hasMajorBlood = false;
		FE4Data.HolyBlood majorBloodType = null;
		if (slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
			majorBloodType = slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType();
			hasMajorBlood = true;
		}
		if (!hasMajorBlood && slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
			majorBloodType = slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType();
			hasMajorBlood = true;
		}
		if (!hasMajorBlood && slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
			majorBloodType = slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType();
			hasMajorBlood = true;
		}
		
		// Verify equipment.
		List<FE4Data.Item> usableItems = new ArrayList<Item>(Arrays.asList(targetClass.usableItems(slot1Blood, slot2Blood, slot3Blood)));
		usableItems.removeIf(item -> (item.getRank() == FE4Data.Item.WeaponRank.PRF));
		
		List<FE4Data.Item> stafflessList = new ArrayList<Item>(usableItems);
		stafflessList.removeIf(item -> (item.getType() == FE4Data.Item.ItemType.STAFF));
		
		int equip1 = holyBoss.getEquipment1();
		FE4Data.Item item1 = FE4Data.Item.valueOf(equip1);
		if (item1 != FE4Data.Item.NONE && targetClass.canUseWeapon(item1, slot1Blood, slot2Blood, slot3Blood) == false) {
			boolean isHolyWeapon = item1.getRank() == FE4Data.Item.WeaponRank.PRF;
			FE4Data.Item replacement = null;
			if (isHolyWeapon) {
				replacement = majorBloodType.holyWeapon;
			} else if (!stafflessList.isEmpty()) {
				replacement = stafflessList.get(rng.nextInt(stafflessList.size()));
			} else {
				replacement = usableItems.get(rng.nextInt(usableItems.size()));
			}
			holyBoss.setEquipment1(replacement.ID);
			usableItems.remove(replacement);
			stafflessList.remove(replacement);
		}
		
		int equip2 = holyBoss.getEquipment2();
		FE4Data.Item item2 = FE4Data.Item.valueOf(equip2);
		if (item2 != FE4Data.Item.NONE && (targetClass.canUseWeapon(item2, slot1Blood, slot2Blood, slot3Blood) == false || item2 == item1)) {
			boolean isHolyWeapon = item2.getRank() == FE4Data.Item.WeaponRank.PRF;
			FE4Data.Item replacement = null;
			if (isHolyWeapon) {
				replacement = majorBloodType.holyWeapon;
			} else {
				replacement = usableItems.get(rng.nextInt(usableItems.size()));
			}
			
			holyBoss.setEquipment2(replacement.ID);
			usableItems.remove(replacement);
		}
		
		// If they drop something and are in Gen 1, we should make sure they can drop something they can use.
		int equip3 = holyBoss.getEquipment3();
		if (equip3 != FE4Data.Item.NONE.ID && fe4Char.isGen1() && itemMap != null) {
			FE4Data.Item droppedItem = itemMap.getItemAtIndex(equip3);
			if (!targetClass.canUseWeapon(droppedItem, slot1Blood, slot2Blood, slot3Blood)) {
				FE4Data.Item replacement = usableItems.get(rng.nextInt(usableItems.size()));
				itemMap.setItemAtIndex(equip3, replacement);
			}
		}
	}
	
	private static void setStaticCharacterToClass(FE4ClassOptions options, FE4StaticCharacter character, FE4Data.CharacterClass targetClass, CharacterDataLoader charData, ItemMapper itemMap, Map<FE4Data.Character, FE4Data.CharacterClass> predeterminedClasses, Map<FE4Data.Character, FE4Data.Item> requiredItems, Random rng) {
		FE4Data.CharacterClass oldClass = FE4Data.CharacterClass.valueOf(character.getClassID());
		boolean wasSTRBased = oldClass.primaryAttackIsStrength();
		boolean wasMAGBased = oldClass.primaryAttackIsMagic();
		
		boolean isSTRBased = targetClass.primaryAttackIsStrength();
		boolean isMAGBased = targetClass.primaryAttackIsMagic();
		
		if ((wasSTRBased && !wasMAGBased && isMAGBased && !isSTRBased) || (wasMAGBased && !wasMAGBased && isSTRBased && !isMAGBased)) {
			// Swap in the case that we've randomized across the STR/MAG split.
			int oldSTR = character.getSTRGrowth();
			character.setSTRGrowth(character.getMAGGrowth());
			character.setMAGGrowth(oldSTR);
			
			oldSTR = character.getBaseSTR();
			character.setBaseSTR(character.getBaseMAG());
			character.setBaseMAG(oldSTR);
		}
		
		character.setClassID(targetClass.ID);
		
		int blood1Value = character.getHolyBlood1Value();
		int blood2Value = character.getHolyBlood2Value();
		int blood3Value = character.getHolyBlood3Value();
		// Blood 4 is only Loptous, which we're not dealing with player-side.
		
		List<HolyBloodSlot1> slot1Blood = FE4Data.HolyBloodSlot1.slot1HolyBlood(blood1Value);
		List<HolyBloodSlot2> slot2Blood = FE4Data.HolyBloodSlot2.slot2HolyBlood(blood2Value);
		List<HolyBloodSlot3> slot3Blood = FE4Data.HolyBloodSlot3.slot3HolyBlood(blood3Value);
		
		boolean hasMajorBlood = false;
		FE4Data.HolyBlood majorBloodType = null;
		if (slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
			majorBloodType = slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType();
			hasMajorBlood = true;
		}
		if (!hasMajorBlood && slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
			majorBloodType = slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType();
			hasMajorBlood = true;
		}
		if (!hasMajorBlood && slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
			majorBloodType = slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType();
			hasMajorBlood = true;
		}
		
		if (options.randomizeBlood) {
			// Anybody with major blood is keeping major blood, but that blood might have to be adjusted.
			if (hasMajorBlood && majorBloodType != null) {
				FE4Data.Item holyWeaponToUpdate = majorBloodType.holyWeapon;
				
				slot1Blood.removeIf(blood -> (blood.isMajor()));
				slot2Blood.removeIf(blood -> (blood.isMajor()));
				slot3Blood.removeIf(blood -> (blood.isMajor()));
				
				Integer inventoryID = FE4Data.HolyWeaponInventoryIDs.get(holyWeaponToUpdate);
				
				FE4Data.Character fe4Char = FE4Data.Character.valueOf(character.getCharacterID());
				Set<FE4Data.HolyBlood> bloodOptions = new HashSet<FE4Data.HolyBlood>(Arrays.asList(targetClass.supportedHolyBlood()));
				Set<HolyBlood> limitedOptions = new HashSet<HolyBlood>(Arrays.asList(fe4Char.limitedHolyBloodSelection()));
				bloodOptions.retainAll(limitedOptions);
				
				List<FE4Data.HolyBlood> bloodList = new ArrayList<FE4Data.HolyBlood>(bloodOptions);
				if (!bloodList.isEmpty()) {
					FE4Data.HolyBlood newMajorBlood = bloodList.get(rng.nextInt(bloodOptions.size()));
					FE4Data.HolyBloodSlot1 slot1 = FE4Data.HolyBloodSlot1.blood(newMajorBlood, true);
					FE4Data.HolyBloodSlot2 slot2 = FE4Data.HolyBloodSlot2.blood(newMajorBlood, true);
					FE4Data.HolyBloodSlot3 slot3 = FE4Data.HolyBloodSlot3.blood(newMajorBlood, true);
					
					if (slot1 != null) { 
						slot1Blood.add(slot1);
					} else if (slot2 != null) { 
						slot2Blood.add(slot2);
					} else if (slot3 != null) {
						slot3Blood.add(slot3);
					}
					
					if (holyWeaponToUpdate != null && inventoryID != null) { itemMap.setItemAtIndex(inventoryID, newMajorBlood.holyWeapon); }
					
					majorBloodType = newMajorBlood;
				}
			}
			
			// Look for Minor blood now.
			int minorBloodCount = 0;
			minorBloodCount += slot1Blood.stream().filter(blood -> (blood.isMajor() == false)).count();
			minorBloodCount += slot2Blood.stream().filter(blood -> (blood.isMajor() == false)).count();
			minorBloodCount += slot3Blood.stream().filter(blood -> (blood.isMajor() == false)).count();
			
			slot1Blood.removeIf(blood -> (blood.isMajor() == false));
			slot2Blood.removeIf(blood -> (blood.isMajor() == false));
			slot3Blood.removeIf(blood -> (blood.isMajor() == false));
			
			List<HolyBlood> bloodOptions = new ArrayList<HolyBlood>(Arrays.asList(HolyBlood.values()));
			if (majorBloodType != null) { bloodOptions.remove(majorBloodType); }
			boolean hasFavoredBlood = false;
			for (int i = 0; i < minorBloodCount; i++) {
				FE4Data.HolyBlood blood = null;
				FE4Data.HolyBlood[] favoredBlood = targetClass.supportedHolyBlood();
				if (!hasFavoredBlood && favoredBlood.length > 0 && rng.nextInt(2) == 0) {
					// Pull from the blood options based on class. (Can only happen once)
					blood = favoredBlood[rng.nextInt(favoredBlood.length)];
					hasFavoredBlood = true;
				} else {
					// Pull from the remaining list.
					blood = bloodOptions.get(rng.nextInt(bloodOptions.size()));
				}
				
				if (blood == null) { break; }
				
				FE4Data.HolyBloodSlot1 slot1 = FE4Data.HolyBloodSlot1.blood(blood, false);
				FE4Data.HolyBloodSlot2 slot2 = FE4Data.HolyBloodSlot2.blood(blood, false);
				FE4Data.HolyBloodSlot3 slot3 = FE4Data.HolyBloodSlot3.blood(blood, false);
				
				if (slot1 != null) { slot1Blood.add(slot1); }
				if (slot2 != null) { slot2Blood.add(slot2); }
				if (slot3 != null) { slot3Blood.add(slot3); }
				
				bloodOptions.remove(blood);
			}
			
			character.setHolyBlood1Value(FE4Data.HolyBloodSlot1.valueForSlot1HolyBlood(slot1Blood));
			character.setHolyBlood2Value(FE4Data.HolyBloodSlot2.valueForSlot2HolyBlood(slot2Blood));
			character.setHolyBlood3Value(FE4Data.HolyBloodSlot3.valueForSlot3HolyBlood(slot3Blood));
		}
		
		// Verify equipment.
		List<FE4Data.Item> usableItems = new ArrayList<Item>(Arrays.asList(targetClass.usableItems(slot1Blood, slot2Blood, slot3Blood)));
		usableItems.removeIf(item -> (item.getRank() == FE4Data.Item.WeaponRank.PRF));
		
		boolean canAttack = usableItems.stream().anyMatch(item -> (item.isWeapon()));
		boolean hasWeapon = false;
		boolean isHealer = targetClass.isHealer();
		
		Set<FE4Data.Item> healingStaves = new HashSet<Item>(FE4Data.Item.healingStaves);
		healingStaves.retainAll(usableItems);
		
		int equip1 = character.getEquipment1();
		FE4Data.Item item1 = itemMap.getItemAtIndex(equip1);
		if (item1 != null && targetClass.canUseWeapon(item1, slot1Blood, slot2Blood, slot3Blood) == false) {
			boolean isHolyWeapon = item1.getRank() == FE4Data.Item.WeaponRank.PRF;
			FE4Data.Item replacement = null;
			if (isHolyWeapon) {
				replacement = majorBloodType.holyWeapon;
			} else {
				if (isHealer && !healingStaves.isEmpty()) {
					List<FE4Data.Item> healStaffList = new ArrayList<FE4Data.Item>(healingStaves); 
					replacement = healStaffList.get(rng.nextInt(healStaffList.size()));
				} else {
					replacement = usableItems.get(rng.nextInt(usableItems.size()));
				}
			}
			
			itemMap.setItemAtIndex(equip1, replacement);
			usableItems.remove(replacement);
			
			hasWeapon = replacement.isWeapon();
		} else if (item1 != null) {
			hasWeapon = item1.isWeapon();
		}
		
		int equip2 = character.getEquipment2();
		FE4Data.Item item2 = itemMap.getItemAtIndex(equip2);
		if (item2 != null && (targetClass.canUseWeapon(item2, slot1Blood, slot2Blood, slot3Blood) == false || item2 == item1)) {
			boolean isHolyWeapon = item2.getRank() == FE4Data.Item.WeaponRank.PRF;
			FE4Data.Item replacement = null;
			if (isHolyWeapon) {
				replacement = majorBloodType.holyWeapon;
			} else {
				replacement = usableItems.get(rng.nextInt(usableItems.size()));
			}
			
			itemMap.setItemAtIndex(equip2, replacement);
			usableItems.remove(replacement);
			if (!hasWeapon) { hasWeapon = replacement.isWeapon(); }
		} else if (!hasWeapon && item2 != null) {
			hasWeapon = item2.isWeapon(); 
		}
		
		int equip3 = character.getEquipment3();
		FE4Data.Item item3 = itemMap.getItemAtIndex(equip3);
		if (item3 != null && (targetClass.canUseWeapon(item3, slot1Blood, slot2Blood, slot3Blood) == false || item3 == item2 || item3 == item1)) {
			boolean isHolyWeapon = item3.getRank() == FE4Data.Item.WeaponRank.PRF;
			FE4Data.Item replacement = null;
			if (isHolyWeapon) {
				replacement = majorBloodType.holyWeapon;
			} else {
				replacement = usableItems.get(rng.nextInt(usableItems.size()));
			}
			
			itemMap.setItemAtIndex(equip3, replacement);
			usableItems.remove(replacement);
			if (!hasWeapon) { hasWeapon = replacement.isWeapon(); }
		} else if (!hasWeapon && item3 != null) {
			hasWeapon = item3.isWeapon();
		}
		
		if (canAttack && !hasWeapon) {
			item1 = itemMap.getItemAtIndex(equip1);
			if (item1 != null) {
				List<FE4Data.Item> usableWeapons = new ArrayList<Item>(usableItems);
				usableWeapons = usableWeapons.stream().filter(item -> (item.isWeapon())).collect(Collectors.toList());
				if (!usableWeapons.isEmpty()) {
					FE4Data.Item weapon = usableWeapons.get(rng.nextInt(usableWeapons.size()));
					itemMap.setItemAtIndex(equip1, weapon);
					usableItems.remove(weapon);
					hasWeapon = true;
				}
			}
		}
		
		usableItems = new ArrayList<Item>(Arrays.asList(targetClass.usableItems(slot1Blood, slot2Blood, slot3Blood)));
		
		// Fix conversation items if necessary.
		if (options.adjustConversationWeapons) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(character.getCharacterID());
			for (FE4Data.Character recipient : FE4Data.EventItemInventoryIDsByRecipient.keySet()) {
				if (recipient == fe4Char) {
					int inventoryID = FE4Data.EventItemInventoryIDsByRecipient.get(recipient);
					FE4Data.Item item = itemMap.getItemAtIndex(inventoryID);
					if (targetClass.canUseWeapon(item, slot1Blood, slot2Blood, slot3Blood) == false) {
						FE4Data.Item replacement = usableItems.get(rng.nextInt(usableItems.size()));
						itemMap.setItemAtIndex(inventoryID, replacement);
						usableItems.remove(replacement);
					}
					
					if (FE4Data.WeaklyLinkedCharacters.containsKey(recipient)) {
						FE4Data.Character otherRecipient = FE4Data.WeaklyLinkedCharacters.get(recipient);
						FE4Data.Item receivedItem = itemMap.getItemAtIndex(inventoryID);
						if (predeterminedClasses.containsKey(otherRecipient)) {
							FE4Data.CharacterClass otherClass = predeterminedClasses.get(otherRecipient);
							if (otherClass.canUseWeapon(receivedItem, slot1Blood, slot2Blood, slot3Blood) == false) {
								Set<FE4Data.Item> myUsableItems = new HashSet<Item>(Arrays.asList(targetClass.usableItems(slot1Blood, slot2Blood, slot3Blood)));
								FE4StaticCharacter theirCharacter = charData.getStaticCharacter(otherRecipient);
								List<FE4Data.HolyBloodSlot1> theirSlot1 = theirCharacter != null ? FE4Data.HolyBloodSlot1.slot1HolyBlood(theirCharacter.getHolyBlood1Value()) : new ArrayList<FE4Data.HolyBloodSlot1>();
								List<FE4Data.HolyBloodSlot2> theirSlot2 = theirCharacter != null ? FE4Data.HolyBloodSlot2.slot2HolyBlood(theirCharacter.getHolyBlood2Value()) : new ArrayList<FE4Data.HolyBloodSlot2>();
								List<FE4Data.HolyBloodSlot3> theirSlot3 = theirCharacter != null ? FE4Data.HolyBloodSlot3.slot3HolyBlood(theirCharacter.getHolyBlood3Value()) : new ArrayList<FE4Data.HolyBloodSlot3>();
								Set<FE4Data.Item> theirUsableItems = new HashSet<Item>(Arrays.asList(otherClass.usableItems(theirSlot1, theirSlot2, theirSlot3)));
								myUsableItems.retainAll(theirUsableItems);
								if (!myUsableItems.isEmpty()) {
									FE4Data.Item replacement = myUsableItems.stream().max(new Comparator<FE4Data.Item>() {
										@Override
										public int compare(Item arg0, Item arg1) {
											if (arg0 == arg1) { return 0; }
											return arg0.ID < arg1.ID ? -1 : 1;
										}
									}).get();
									
									itemMap.setItemAtIndex(inventoryID, replacement);
								}
							}
						} else {
							requiredItems.put(otherRecipient, receivedItem);
						}
					}
				}
			}
		}
	}
	
	private static void setChildCharacterToClass(FE4ClassOptions options, FE4ChildCharacter child, FE4StaticCharacter parent, FE4Data.CharacterClass targetClass, ItemMapper itemMap, Random rng) {
		child.setClassID(targetClass.ID);
		
		List<FE4Data.Item> usableItems = new ArrayList<Item>(Arrays.asList(targetClass.usableItems(new ArrayList<FE4Data.HolyBloodSlot1>(), new ArrayList<FE4Data.HolyBloodSlot2>(), new ArrayList<FE4Data.HolyBloodSlot3>())));
		
		boolean canAttack = usableItems.stream().anyMatch(item -> (item.isWeapon()));
		boolean hasWeapon = false;
		
		List<FE4Data.HolyBloodSlot1> slot1Blood = parent != null ? FE4Data.HolyBloodSlot1.slot1HolyBlood(parent.getHolyBlood1Value()) : null;
		List<FE4Data.HolyBloodSlot2> slot2Blood = parent != null ? FE4Data.HolyBloodSlot2.slot2HolyBlood(parent.getHolyBlood2Value()) : null;
		List<FE4Data.HolyBloodSlot3> slot3Blood = parent != null ? FE4Data.HolyBloodSlot3.slot3HolyBlood(parent.getHolyBlood3Value()) : null;
		
		int equip1 = child.getEquipment1();
		FE4Data.Item item1 = itemMap.getItemAtIndex(equip1);
		if (item1 != null && targetClass.canUseWeapon(item1, slot1Blood, slot2Blood, slot3Blood) == false) {
			FE4Data.Item replacement = usableItems.get(rng.nextInt(usableItems.size()));
			itemMap.setItemAtIndex(equip1, replacement);
			usableItems.remove(replacement);
			
			hasWeapon = replacement.isWeapon();
		} else if (item1 != null) {
			hasWeapon = item1.isWeapon();
		}
		
		int equip2 = child.getEquipment2();
		FE4Data.Item item2 = itemMap.getItemAtIndex(equip2);
		if (item2 != null && targetClass.canUseWeapon(item2, slot1Blood, slot2Blood, slot3Blood) == false) {
			FE4Data.Item replacement = usableItems.get(rng.nextInt(usableItems.size()));
			
			itemMap.setItemAtIndex(equip2, replacement);
			usableItems.remove(replacement);
			if (!hasWeapon) { hasWeapon = replacement.isWeapon(); }
		} else if (!hasWeapon && item2 != null) {
			hasWeapon = item2.isWeapon(); 
		}
		
		if (canAttack && !hasWeapon) {
			item1 = itemMap.getItemAtIndex(equip1);
			if (item1 != null) {
				List<FE4Data.Item> usableWeapons = new ArrayList<Item>(usableItems);
				usableWeapons = usableWeapons.stream().filter(item -> (item.isWeapon())).collect(Collectors.toList());
				if (!usableWeapons.isEmpty()) {
					FE4Data.Item weapon = usableWeapons.get(rng.nextInt(usableWeapons.size()));
					itemMap.setItemAtIndex(equip1, weapon);
					usableItems.remove(weapon);
					hasWeapon = true;
				}
			}
		}
		
		// Fix conversation items if necessary.
		if (options.adjustConversationWeapons) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(child.getCharacterID());
			for (FE4Data.Character recipient : FE4Data.EventItemInventoryIDsByRecipient.keySet()) {
				if (recipient == fe4Char) {
					int inventoryID = FE4Data.EventItemInventoryIDsByRecipient.get(recipient);
					FE4Data.Item item = itemMap.getItemAtIndex(inventoryID);
					if (targetClass.canUseWeapon(item, slot1Blood, slot2Blood, slot3Blood) == false) {
						FE4Data.Item replacement = usableItems.get(rng.nextInt(usableItems.size()));
						itemMap.setItemAtIndex(inventoryID, replacement);
						usableItems.remove(replacement);
					}
				}
			}
		}
	}

	private static void setEnemyCharacterToClass(FE4ClassOptions options, FE4EnemyCharacter enemy, FE4Data.Character enemyChar, FE4Data.CharacterClass targetClass, ItemMapper itemMap, Random rng) {
		enemy.setClassID(targetClass.ID);
		
		Set<FE4Data.Item> blacklistedItems = new HashSet<FE4Data.Item>();
		int chapter = enemyChar.minionChapter();
		int effectiveChapter = chapter % 6; // Matches up Prologue with Ch 6, 1 with 7, 2-8, 3-9, 4-10, 5-Endgame
		// Prologue/Ch6 - 100%
		// Ch1/Ch7 - 80%
		// Ch2/Ch8 - 60%
		// Ch3/Ch9 - 40%
		// Ch4/Ch10 - 20%
		// Ch5/Endgame - 0%
		boolean shouldBlacklist = rng.nextInt(6) >= effectiveChapter; 
		
		if (shouldBlacklist) {
			// Blacklist anything too powerful in the prologue.
			blacklistedItems.addAll(FE4Data.Item.interestingWeapons);
			blacklistedItems.addAll(FE4Data.Item.powerfulWeapons);
		}
		
		Set<FE4Data.Item> itemSet = new HashSet<FE4Data.Item>(Arrays.asList(targetClass.usableItems(null, null, null)));
		itemSet.removeAll(blacklistedItems);
		List<FE4Data.Item> usableItems = new ArrayList<FE4Data.Item>(itemSet); 
		
		int item1ID = enemy.getEquipment1();
		FE4Data.Item item1 = FE4Data.Item.valueOf(item1ID);
		if (item1 != Item.NONE) {
			if (!targetClass.canUseWeapon(item1, null, null, null)) {
				List<FE4Data.Item> usableWeapons = new ArrayList<Item>(usableItems);
				usableWeapons = usableWeapons.stream().filter(item -> (item.isWeapon())).collect(Collectors.toList());
				if (!usableWeapons.isEmpty()) {
					FE4Data.Item weapon = usableWeapons.get(rng.nextInt(usableWeapons.size()));
					enemy.setEquipment1(weapon.ID);
					usableItems.remove(weapon);
				}
			}
		}
		
		int item2ID = enemy.getEquipment2();
		FE4Data.Item item2 = FE4Data.Item.valueOf(item2ID);
		if (item2 != Item.NONE) {
			if (!targetClass.canUseWeapon(item2, null, null, null) || item2 == item1) {
				if (!usableItems.isEmpty()) {
					FE4Data.Item item = usableItems.get(rng.nextInt(usableItems.size()));
					enemy.setEquipment2(item.ID);
				} else {
					enemy.setEquipment2(Item.NONE.ID);
				}
			}
		}
		
		// Gen 1 characters can alter their drops to a weapon they can use.
		int droppedItemInventoryID = enemy.getDropableEquipment();
		if (droppedItemInventoryID != FE4Data.Item.NONE.ID && enemyChar.isGen1()) {
			FE4Data.Item droppedItem = itemMap.getItemAtIndex(droppedItemInventoryID);
			if (!targetClass.canUseWeapon(droppedItem, null, null, null) && !itemSet.isEmpty()) {
				usableItems = new ArrayList<FE4Data.Item>(itemSet);
				FE4Data.Item replacement = usableItems.get(rng.nextInt(usableItems.size()));
				itemMap.setItemAtIndex(droppedItemInventoryID, replacement);
			}
		}
	}
	
	private static void setEnemyCharacterToClass(FE4ClassOptions options, FE4EnemyCharacter enemy, FE4Data.CharacterClass targetClass, FE4Data.Item weapon, ItemMapper itemMap) {
		setEnemyCharacterToClass(options, enemy, targetClass, weapon, null, itemMap);
	}
	
	private static void setEnemyCharacterToClass(FE4ClassOptions options, FE4EnemyCharacter enemy, FE4Data.CharacterClass targetClass, FE4Data.Item item1, FE4Data.Item item2, ItemMapper itemMap) {
		enemy.setClassID(targetClass.ID);
		enemy.setEquipment1(item1 != null ? item1.ID : FE4Data.Item.NONE.ID);
		enemy.setEquipment2(item2 != null ? item2.ID : FE4Data.Item.NONE.ID);
		
		// If we must set the item on this enemy and they also drop a weapon, force that drop to be the item given (unless it's a ring), just to ensure they can use the dropped item.
		if (itemMap != null && enemy.getDropableEquipment() != FE4Data.Item.NONE.ID && !itemMap.getItemAtIndex(enemy.getDropableEquipment()).isRing()) {
			itemMap.setItemAtIndex(enemy.getDropableEquipment(), item1);
		}
	}
}
