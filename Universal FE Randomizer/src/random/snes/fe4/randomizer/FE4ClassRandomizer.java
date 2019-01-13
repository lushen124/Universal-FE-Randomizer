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

import fedata.snes.fe4.FE4ChildCharacter;
import fedata.snes.fe4.FE4Data;
import fedata.snes.fe4.FE4StaticCharacter;
import fedata.snes.fe4.FE4Data.HolyBlood;
import fedata.snes.fe4.FE4Data.HolyBloodSlot1;
import fedata.snes.fe4.FE4Data.HolyBloodSlot2;
import fedata.snes.fe4.FE4Data.HolyBloodSlot3;
import fedata.snes.fe4.FE4Data.Item;
import random.snes.fe4.loader.CharacterDataLoader;
import random.snes.fe4.loader.ItemMapper;
import ui.fe4.FE4ClassOptions;
import ui.fe4.FE4ClassOptions.ChildOptions;

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
			
			targetClass = potentialClasses.get(rng.nextInt(potentialClasses.size()));
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
				}
			}
			
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
			
			FE4Data.CharacterClass targetClass = predeterminedClasses.get(fe4Char);
			if (targetClass != null) {
				setChildCharacterToClass(options, child, targetClass, itemMap, rng);
				continue;
			}
			
			FE4Data.Character referenceCharacter = fe4Char.getGen1Analogue();
			boolean requiresWeakness = fe4Char.mustLoseToCharacters().length > 0;
			boolean restrictedHealer = fe4Char.isHealer() && options.retainHealers;
			
			FE4Data.CharacterClass currentClass = FE4Data.CharacterClass.valueOf(child.getClassID());
			
			if (options.childOption == ChildOptions.MATCH_STRICT) {
				targetClass = predeterminedClasses.get(referenceCharacter);
				if (targetClass != null && targetClass.isPromoted() && !currentClass.isPromoted()) {
					FE4Data.CharacterClass[] demotedClasses = targetClass.demotedClasses(child.isFemale());
					if (demotedClasses.length > 0) {
						targetClass = demotedClasses[rng.nextInt(demotedClasses.length)];
					}
				}
				
				if (currentClass.isFlier()) {
					// Override for Altena, who needs to remain flying to not break stuff.
					FE4Data.CharacterClass[] classPool = currentClass.getClassPool(true, false, true, child.isFemale(), false, fe4Char.requiresAttack(), false, null, null);
					if (classPool.length > 0) {
						targetClass = classPool[rng.nextInt(classPool.length)];
					}
				}
			} else if (options.childOption == ChildOptions.MATCH_LOOSE) {
				FE4Data.CharacterClass referenceClass = predeterminedClasses.get(referenceCharacter);
				FE4Data.CharacterClass[] pool = referenceClass.getClassPool(true, false, true, child.isFemale(), requiresWeakness, fe4Char.requiresAttack(), false, restrictedHealer ? Item.HEAL : null, null);
				if (pool.length > 0) {
					targetClass = pool[rng.nextInt(pool.length)];
				}
			} else {
				FE4Data.CharacterClass referenceClass = FE4Data.CharacterClass.valueOf(child.getClassID());
				FE4Data.CharacterClass[] pool = referenceClass.getClassPool(false, false, false, child.isFemale(), requiresWeakness, fe4Char.requiresAttack(), false, restrictedHealer ? Item.HEAL : null, null);
				if (pool.length > 0) {
					targetClass = pool[rng.nextInt(pool.length)];
				}
			}
			
			if (targetClass != null) {
				setChildCharacterToClass(options, child, targetClass, itemMap, rng);
				
				for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
					predeterminedClasses.put(linked, targetClass);
				}
				predeterminedClasses.put(fe4Char, targetClass);
				
				FE4Data.CharacterClass referenceClass = targetClass;
				
				FE4Data.Character sub = fe4Char.substituteForChild();
				if (sub != null) {
					FE4StaticCharacter subChar = charData.getStaticCharacter(sub);
					if (subChar != null) {
						FE4Data.CharacterClass[] pool = referenceClass.getClassPool(true, false, true, subChar.isFemale(), requiresWeakness, fe4Char.requiresAttack(), false, restrictedHealer ? Item.HEAL : null, null);
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
	}
	
	private static void setStaticCharacterToClass(FE4ClassOptions options, FE4StaticCharacter character, FE4Data.CharacterClass targetClass, CharacterDataLoader charData, ItemMapper itemMap, Map<FE4Data.Character, FE4Data.CharacterClass> predeterminedClasses, Map<FE4Data.Character, FE4Data.Item> requiredItems, Random rng) {
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
				
				List<FE4Data.HolyBlood> bloodOptions = Arrays.asList(targetClass.majorBloodOptions());
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
			boolean hasFavoredBlood = false;
			for (int i = 0; i < minorBloodCount; i++) {
				FE4Data.HolyBlood blood = null;
				FE4Data.HolyBlood[] favoredBlood = targetClass.majorBloodOptions();
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
		
		int equip1 = character.getEquipment1();
		FE4Data.Item item1 = itemMap.getItemAtIndex(equip1);
		if (item1 != null && targetClass.canUseWeapon(item1) == false) {
			boolean isHolyWeapon = item1.getRank() == FE4Data.Item.WeaponRank.PRF;
			FE4Data.Item replacement = null;
			if (isHolyWeapon) {
				replacement = majorBloodType.holyWeapon;
			} else {
				replacement = usableItems.get(rng.nextInt(usableItems.size()));
			}
			
			itemMap.setItemAtIndex(equip1, replacement);
			usableItems.remove(replacement);
			
			hasWeapon = replacement.isWeapon();
		} else if (item1 != null) {
			hasWeapon = item1.isWeapon();
		}
		
		int equip2 = character.getEquipment2();
		FE4Data.Item item2 = itemMap.getItemAtIndex(equip2);
		if (item2 != null && targetClass.canUseWeapon(item2) == false) {
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
		if (item3 != null && targetClass.canUseWeapon(item3) == false) {
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
				usableWeapons.stream().filter(item -> (item.isWeapon()));
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
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(character.getCharacterID());
			for (FE4Data.Character recipient : FE4Data.EventItemInventoryIDsByRecipient.keySet()) {
				if (recipient == fe4Char) {
					int inventoryID = FE4Data.EventItemInventoryIDsByRecipient.get(recipient);
					FE4Data.Item item = itemMap.getItemAtIndex(inventoryID);
					if (targetClass.canUseWeapon(item) == false) {
						FE4Data.Item replacement = usableItems.get(rng.nextInt(usableItems.size()));
						itemMap.setItemAtIndex(inventoryID, replacement);
						usableItems.remove(replacement);
					}
					
					if (FE4Data.WeaklyLinkedCharacters.containsKey(recipient)) {
						FE4Data.Character otherRecipient = FE4Data.WeaklyLinkedCharacters.get(recipient);
						FE4Data.Item receivedItem = itemMap.getItemAtIndex(inventoryID);
						if (predeterminedClasses.containsKey(otherRecipient)) {
							FE4Data.CharacterClass otherClass = predeterminedClasses.get(otherRecipient);
							if (otherClass.canUseWeapon(receivedItem) == false) {
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
	
	private static void setChildCharacterToClass(FE4ClassOptions options, FE4ChildCharacter child, FE4Data.CharacterClass targetClass, ItemMapper itemMap, Random rng) {
		child.setClassID(targetClass.ID);
		
		List<FE4Data.Item> usableItems = new ArrayList<Item>(Arrays.asList(targetClass.usableItems(new ArrayList<FE4Data.HolyBloodSlot1>(), new ArrayList<FE4Data.HolyBloodSlot2>(), new ArrayList<FE4Data.HolyBloodSlot3>())));
		
		boolean canAttack = usableItems.stream().anyMatch(item -> (item.isWeapon()));
		boolean hasWeapon = false;
		
		int equip1 = child.getEquipment1();
		FE4Data.Item item1 = itemMap.getItemAtIndex(equip1);
		if (item1 != null && targetClass.canUseWeapon(item1) == false) {
			FE4Data.Item replacement = usableItems.get(rng.nextInt(usableItems.size()));
			itemMap.setItemAtIndex(equip1, replacement);
			usableItems.remove(replacement);
			
			hasWeapon = replacement.isWeapon();
		} else if (item1 != null) {
			hasWeapon = item1.isWeapon();
		}
		
		int equip2 = child.getEquipment2();
		FE4Data.Item item2 = itemMap.getItemAtIndex(equip2);
		if (item2 != null && targetClass.canUseWeapon(item2) == false) {
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
				usableWeapons.stream().filter(item -> (item.isWeapon()));
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
					if (targetClass.canUseWeapon(item) == false) {
						FE4Data.Item replacement = usableItems.get(rng.nextInt(usableItems.size()));
						itemMap.setItemAtIndex(inventoryID, replacement);
						usableItems.remove(replacement);
					}
				}
			}
		}
	}

}
