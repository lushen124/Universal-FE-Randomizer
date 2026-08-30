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
import fedata.snes.fe4.FE4ChildCharacter.Influence;
import fedata.snes.fe4.FE4Data.HolyBlood;
import fedata.snes.fe4.FE4Data.HolyBloodSlot1;
import fedata.snes.fe4.FE4Data.HolyBloodSlot2;
import fedata.snes.fe4.FE4Data.HolyBloodSlot3;
import fedata.snes.fe4.FE4Data.Item;
import fedata.snes.fe4.FE4EnemyCharacter;
import random.general.PoolDistributor;
import random.snes.fe4.loader.CharacterDataLoader;
import random.snes.fe4.loader.HolyBloodLoader;
import random.snes.fe4.loader.ItemMapper;
import ui.model.fe4.FE4ClassOptions;
import ui.model.fe4.FE4ClassOptions.BloodOptions;
import ui.model.fe4.FE4ClassOptions.ChildOptions;
import ui.model.fe4.FE4ClassOptions.ItemAssignmentOptions;
import ui.model.fe4.FE4ClassOptions.ShopOptions;

public class FE4ClassRandomizer {
	
	static final int rngSalt = 1248;
	
	public static Map<FE4Data.HolyBlood, FE4Data.HolyBlood> generateBloodMapForBloodShuffle(FE4ClassOptions options, Random rng) {
		Map<FE4Data.HolyBlood, FE4Data.HolyBlood> predeterminedBloodMap = new HashMap<FE4Data.HolyBlood, FE4Data.HolyBlood>();
		PoolDistributor<FE4Data.HolyBlood> bloodDistributor = new PoolDistributor<FE4Data.HolyBlood>();
		List<FE4Data.HolyBlood> workingSet = new ArrayList<FE4Data.HolyBlood>();
		
		for (FE4Data.HolyBlood blood : FE4Data.HolyBlood.values()) {
			if (blood.isRestricted()) {
				predeterminedBloodMap.put(blood, blood);
				continue;
			}
			if (blood == HolyBlood.NONE) { continue; }
			
			if ((options.playerBloodOption == BloodOptions.SHUFFLE && !blood.isEnemyBlood()) ||
					(options.bossBloodOption == BloodOptions.SHUFFLE && blood.isEnemyBlood())) {
				bloodDistributor.addItem(blood);
				workingSet.add(blood);
			}
		}
		
		for (FE4Data.HolyBlood blood : workingSet) {
			if (blood.isRestricted()) { continue; }
			if (blood == HolyBlood.NONE) { continue; }
			FE4Data.Character representative = blood.representative;
			Set<FE4Data.HolyBlood> limitedBloodSet = new HashSet<FE4Data.HolyBlood>(Arrays.asList(representative.limitedHolyBloodSelection()));
			FE4Data.HolyBlood replacementBlood = null;
			do {
				assert !Collections.disjoint(limitedBloodSet, bloodDistributor.possibleResults()) : "Unable to satisfy blood constraints.";
				replacementBlood = bloodDistributor.getRandomItem(rng, false);
				if (!limitedBloodSet.contains(replacementBlood) && !Collections.disjoint(limitedBloodSet, bloodDistributor.possibleResults())) {
					replacementBlood = null;
				}
			} while (replacementBlood == null);
			bloodDistributor.removeItem(replacementBlood, true);
			predeterminedBloodMap.put(blood, replacementBlood);
		}
		
		return predeterminedBloodMap;
	}
	
	public static void randomizePlayableCharacterClasses(FE4ClassOptions options, boolean useFreeInventoryForStaves, CharacterDataLoader charData, HolyBloodLoader bloodData, ItemMapper itemMap, Map<FE4Data.HolyBlood, FE4Data.HolyBlood> predeterminedBloodMap, Random rng) {
		Map<FE4Data.Character, FE4Data.CharacterClass> predeterminedClasses = new HashMap<FE4Data.Character, FE4Data.CharacterClass>();
		Set<FE4Data.Character> blacklistedCharacters = new HashSet<FE4Data.Character>();
		Set<FE4Data.CharacterClass> blacklistedClasses = new HashSet<FE4Data.CharacterClass>();
		Map<FE4Data.Character, FE4Data.Item> requiredItems = new HashMap<FE4Data.Character, FE4Data.Item>();
		
		if (!options.includeLords) {
			blacklistedCharacters.addAll(FE4Data.Character.LordCharacters);
			blacklistedClasses.addAll(FE4Data.CharacterClass.lordClasses);
		}
		
		if (!options.includeThieves) {
			blacklistedCharacters.addAll(FE4Data.Character.ThiefCharacters);
			blacklistedClasses.addAll(FE4Data.CharacterClass.thiefClasses);
		}
		
		if (!options.includeDancers) {
			blacklistedCharacters.addAll(FE4Data.Character.DancerCharacters);
		}
		
		if (!options.includeJulia) {
			blacklistedCharacters.add(FE4Data.Character.JULIA);
		}
		
		PoolDistributor<FE4Data.CharacterClass> classDistributor = new PoolDistributor<FE4Data.CharacterClass>();
		for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.promotedClasses) {
			classDistributor.addItem(charClass);
		}
		for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.unpromotedClasses) {
			classDistributor.addItem(charClass);
		}
		
		// Gen 1
		boolean hasDancer = false;
		if (!options.includeDancers) {
			hasDancer = true;
		}
		
		// We want to avoid anybody else getting Sigurd's major blood
		// since all instances of that weapon will not pass down.
		// This happens to work because getGen1Characters() returns the characters in order of ID, and Sigurd's ID is first.
		// So we can just check Sigurd's blood whenever we look at everybody else.
		List<FE4StaticCharacter> gen1Characters = charData.getGen1Characters();
		for (FE4StaticCharacter staticChar : gen1Characters) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
			
			int blood1Value = staticChar.getHolyBlood1Value();
			int blood2Value = staticChar.getHolyBlood2Value();
			int blood3Value = staticChar.getHolyBlood3Value();
			// Blood 4 is only Loptous, which we're not dealing with player-side.
			
			List<HolyBloodSlot1> slot1Blood = FE4Data.HolyBloodSlot1.slot1HolyBlood(blood1Value);
			List<HolyBloodSlot2> slot2Blood = FE4Data.HolyBloodSlot2.slot2HolyBlood(blood2Value);
			List<HolyBloodSlot3> slot3Blood = FE4Data.HolyBloodSlot3.slot3HolyBlood(blood3Value);
			
			boolean hasMajorBlood = slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent() ||
					slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent() ||
					slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent();
			FE4Data.HolyBlood majorBloodType = null;
			if (hasMajorBlood) {
				if (slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
					majorBloodType = slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType();
				} else if (slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
					majorBloodType = slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType();
				} else if (slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
					majorBloodType = slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType();
				}
			}
			
			boolean hasMinorBlood = slot1Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent() ||
					slot2Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent() ||
					slot3Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent();
			FE4Data.HolyBlood minorBloodType = null;
			if (hasMinorBlood) {
				if (slot1Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent()) {
					minorBloodType = slot1Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().get().bloodType();
				} else if (slot2Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent()) {
					minorBloodType = slot2Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().get().bloodType();
				} else if (slot3Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent()) {
					minorBloodType = slot3Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().get().bloodType();
				}
			}
			
			if (blacklistedCharacters.contains(fe4Char)) {
				predeterminedClasses.put(fe4Char, FE4Data.CharacterClass.valueOf(staticChar.getClassID()));
				// We should still swap blood if necessary.
				if (options.playerBloodOption == BloodOptions.SHUFFLE && predeterminedBloodMap != null) {
					if (hasMajorBlood) {
						FE4Data.HolyBlood newMajor = predeterminedBloodMap.get(majorBloodType);
						final FE4Data.HolyBlood oldMajor = majorBloodType;
						slot1Blood.removeIf(blood -> (blood.bloodType() == oldMajor && blood.isMajor()));
						slot2Blood.removeIf(blood -> (blood.bloodType() == oldMajor && blood.isMajor()));
						slot3Blood.removeIf(blood -> (blood.bloodType() == oldMajor && blood.isMajor()));
						FE4Data.HolyBloodSlot1 slot1 = FE4Data.HolyBloodSlot1.blood(newMajor, true);
						FE4Data.HolyBloodSlot2 slot2 = FE4Data.HolyBloodSlot2.blood(newMajor, true);
						FE4Data.HolyBloodSlot3 slot3 = FE4Data.HolyBloodSlot3.blood(newMajor, true);
						if (slot1 != null) { slot1Blood.add(slot1); }
						else if (slot2 != null) { slot2Blood.add(slot2); }
						else if (slot3 != null) { slot3Blood.add(slot3); }
					}
					
					if (hasMinorBlood) {
						FE4Data.HolyBlood newMinor = predeterminedBloodMap.get(minorBloodType);
						final FE4Data.HolyBlood oldMinor = minorBloodType;
						slot1Blood.removeIf(blood -> (blood.bloodType() == oldMinor && !blood.isMajor()));
						slot2Blood.removeIf(blood -> (blood.bloodType() == oldMinor && !blood.isMajor()));
						slot3Blood.removeIf(blood -> (blood.bloodType() == oldMinor && !blood.isMajor()));
						FE4Data.HolyBloodSlot1 slot1 = FE4Data.HolyBloodSlot1.blood(newMinor, false);
						FE4Data.HolyBloodSlot2 slot2 = FE4Data.HolyBloodSlot2.blood(newMinor, false);
						FE4Data.HolyBloodSlot3 slot3 = FE4Data.HolyBloodSlot3.blood(newMinor, false);
						if (slot1 != null) { slot1Blood.add(slot1); }
						else if (slot2 != null) { slot2Blood.add(slot2); }
						else if (slot3 != null) { slot3Blood.add(slot3); }
					}
				}
				
				staticChar.setHolyBlood1Value(FE4Data.HolyBloodSlot1.valueForSlot1HolyBlood(slot1Blood));
				staticChar.setHolyBlood2Value(FE4Data.HolyBloodSlot2.valueForSlot2HolyBlood(slot2Blood));
				staticChar.setHolyBlood3Value(FE4Data.HolyBloodSlot3.valueForSlot3HolyBlood(slot3Blood));
				
				continue;
			}
			
			FE4Data.CharacterClass targetClass = predeterminedClasses.get(fe4Char);
			if (targetClass != null) {
				setStaticCharacterToClass(options, staticChar, targetClass, !useFreeInventoryForStaves, charData, bloodData, itemMap, predeterminedClasses, predeterminedBloodMap, requiredItems, rng);
				continue;
			}
			
			FE4Data.CharacterClass originalClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID());
			if (originalClass == null) { continue; } // Shouldn't be touching this class. Skip this character.
			
			// In the case that we have a weak requirement on a specific class (where two characters have to be somewhat similar), set that here.
			// Whoever gets randomized first chooses the weapon.
			// The second character should refer to the first character.
			FE4Data.CharacterClass referenceClass = null;
			boolean requiresFlier = originalClass.isFlier();
			if (FE4Data.WeaklyLinkedCharacters.containsKey(fe4Char)) {
				FE4Data.Character referenceCharacter = FE4Data.WeaklyLinkedCharacters.get(fe4Char);
				if (referenceCharacter.isPlayable() && referenceCharacter.isGen1()) {
					// This addresses cases where the current character is linked to a playable character in gen 1 (regardless of gen of the current character).
					referenceClass = predeterminedClasses.get(referenceCharacter);
				} else if ((referenceCharacter.isBoss() && !options.randomizeBosses) || (referenceCharacter.isMinion() && !options.randomizeMinions)) {
					// If it's an enemy and we're not randomizing enemies, then we're restricted to the weapons that the linked enemy can use.
					FE4StaticCharacter holyBoss = charData.getStaticCharacter(referenceCharacter);
					if (holyBoss == null) {
						FE4EnemyCharacter enemy = charData.getEnemyCharacter(referenceCharacter);
						referenceClass = FE4Data.CharacterClass.valueOf(enemy.getClassID()); 
					} else {
						referenceClass = FE4Data.CharacterClass.valueOf(holyBoss.getClassID());
					}
				} else if (referenceCharacter.isMinion() || referenceCharacter.isBoss()) {
					// We just need to make sure that, if it's a flier, it doesn't end up with something it can't use, because we're not
					// going to change enemy fliers.
					FE4StaticCharacter holyBoss = charData.getStaticCharacter(referenceCharacter);
					if (holyBoss == null) {
						FE4EnemyCharacter enemy = charData.getEnemyCharacter(referenceCharacter);
						referenceClass = FE4Data.CharacterClass.valueOf(enemy.getClassID()); 
					} else {
						referenceClass = FE4Data.CharacterClass.valueOf(holyBoss.getClassID());
					}
					// If the enemy isn't a flier, then we don't need to worry about anything.
					if (!referenceClass.isFlier()) {
						referenceClass = null;
					} else {
						requiresFlier = false; // We want to use a reference class, but not necessarily adopt the same flying restriction.
					}
				}
				
				if (referenceClass != null) {
					if (referenceClass.isPromoted() && !originalClass.isPromoted()) {
						List<FE4Data.CharacterClass> demotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(referenceClass.demotedClasses(staticChar.isFemale())));
						Collections.sort(demotedClasses, FE4Data.CharacterClass.defaultComparator);
						if (!demotedClasses.isEmpty()) {
							referenceClass = demotedClasses.get(rng.nextInt(demotedClasses.size()));
						}
					} else if (!referenceClass.isPromoted() && originalClass.isPromoted()) {
						List<FE4Data.CharacterClass> promotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(referenceClass.promotionClasses(staticChar.isFemale())));
						Collections.sort(promotedClasses, FE4Data.CharacterClass.defaultComparator);
						if (!promotedClasses.isEmpty()) {
							referenceClass = promotedClasses.get(rng.nextInt(promotedClasses.size()));
						}
					}
				}
			}
			
			Set<FE4Data.CharacterClass> potentialClasses = new HashSet<FE4Data.CharacterClass>();
			
			if (fe4Char.isHealer() && options.retainHealers) {
				Collections.addAll(potentialClasses, originalClass.getClassPool(true, false, true, staticChar.isFemale(), false, false, options.retainHorses && originalClass.isHorseback(), fe4Char.requiresMelee(), FE4Data.Item.HEAL, null));
			} else {
				FE4Data.Item mustUseItem = null;
				if (options.playerBloodOption == BloodOptions.NO_CHANGE) {
					if (hasMajorBlood) {
						mustUseItem = majorBloodType.holyWeapon.getType().getBasic();
					} else if (hasMinorBlood) {
						mustUseItem = minorBloodType.holyWeapon.getType().getBasic();
					}
				} else if (options.playerBloodOption == BloodOptions.SHUFFLE) {
					if (hasMajorBlood) {
						mustUseItem = predeterminedBloodMap.get(majorBloodType).holyWeapon.getType().getBasic();
					} else if (hasMinorBlood) {
						mustUseItem = predeterminedBloodMap.get(minorBloodType).holyWeapon.getType().getBasic();
					}
				}
				
				if (fe4Char.mustLoseToCharacters().length > 0) {
					// We know this is Ethlyn and Quan, so we could just set requireHorseback to true.
					if (referenceClass != null) {
						Collections.addAll(potentialClasses, referenceClass.getClassPool(true, false, true, staticChar.isFemale(), true, fe4Char.requiresAttack(), true, fe4Char.requiresMelee(), requiresFlier, mustUseItem, Item.HORSESLAYER));
					} else {
						Collections.addAll(potentialClasses, originalClass.getClassPool(false, false, true, staticChar.isFemale(), true, fe4Char.requiresAttack(), true, fe4Char.requiresMelee(), mustUseItem, Item.HORSESLAYER));
					}
				} else {
					if (referenceClass != null) {
						Collections.addAll(potentialClasses, referenceClass.getClassPool(true, false, false, staticChar.isFemale(), false, fe4Char.requiresAttack(), options.retainHorses && originalClass.isHorseback(), fe4Char.requiresMelee(), requiresFlier, mustUseItem, null));
					} else {
						Collections.addAll(potentialClasses, originalClass.getClassPool(false, false, false, staticChar.isFemale(), false, fe4Char.requiresAttack(), options.retainHorses && originalClass.isHorseback(), fe4Char.requiresMelee(), mustUseItem, null));
					}
				}
			}
			
			if (options.assignEvenly) {
				if (Collections.disjoint(classDistributor.possibleResults(), potentialClasses)) {
					// Reload the class pool if we have a completely disjoint set.
					for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.promotedClasses) {
						classDistributor.addItem(charClass);
					}
					for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.unpromotedClasses) {
						classDistributor.addItem(charClass);
					}
				}
				
				potentialClasses.retainAll(classDistributor.possibleResults());
			}
			
			if (options.playerBloodOption == BloodOptions.RANDOMIZE && hasMajorBlood) {
				// If we do randomize blood, make sure nobody is stuck with the same major blood as Sigurd or at least stuck in a class
				// that MUST have the same major blood as Sigurd (blood like Fjalar, Neir, and Ulir have classes
				// that only use one weapon type.) This is because we don't allow Sigurd's holy weapon to pass to Seliph.
				// That means anybody sharing his weapon also won't pass down. This only applies to gen 1 for characters
				// that normally have major blood (i.e. Claud, Brigid, Lewyn).
				Set<FE4Data.HolyBlood> restrictedBlood = new HashSet<FE4Data.HolyBlood>(); 
				
				if (!FE4Data.Character.CharactersRequiringUniqueBlood.contains(fe4Char)) {
					for (FE4Data.Character uniqueChar : FE4Data.Character.CharactersRequiringUniqueBlood) {
						FE4StaticCharacter unique = charData.getStaticCharacter(uniqueChar);
						// If we haven't actually set this character yet, don't include his blood yet.
						// This is actually to make sure Quan doesn't prematurely record his old blood as restricted.
						// TODO: This will break if we ever make somebody later in the roster unique, so we should have a better solution for this at some point.
						// This works for now because Sigurd comes first and Quan is 6th behind 5 characters without major blood.
						if (!predeterminedClasses.containsKey(uniqueChar)) { continue; } 
						if (unique == null) { continue; }
						
						List<HolyBloodSlot1> restrictedSlot1 = FE4Data.HolyBloodSlot1.slot1HolyBlood(unique.getHolyBlood1Value());
						List<HolyBloodSlot2> restrictedSlot2 = FE4Data.HolyBloodSlot2.slot2HolyBlood(unique.getHolyBlood2Value());
						List<HolyBloodSlot3> restrictedSlot3 = FE4Data.HolyBloodSlot3.slot3HolyBlood(unique.getHolyBlood3Value());
						
						if (restrictedSlot1.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) { restrictedBlood.add(restrictedSlot1.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType()); }
						if (restrictedSlot2.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) { restrictedBlood.add(restrictedSlot2.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType()); }
						if (restrictedSlot3.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) { restrictedBlood.add(restrictedSlot3.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType()); }
					}
				}
				
				if (!restrictedBlood.isEmpty()) {
					// Only allow classes that aren't required to have restricted blood.
					potentialClasses = potentialClasses.stream().filter(charClass -> {
						Set<FE4Data.HolyBlood> bloodSet = new HashSet<FE4Data.HolyBlood>(Arrays.asList(charClass.supportedHolyBlood()));
						return bloodSet.size() > 1 || Collections.disjoint(bloodSet, restrictedBlood);
					}).collect(Collectors.toSet());
				}
			}
			
			potentialClasses.removeAll(new HashSet<FE4Data.CharacterClass>(Arrays.asList(fe4Char.blacklistedClasses())));
			potentialClasses.removeAll(blacklistedClasses);
			if (hasDancer) { // Only 1 per generation.
				potentialClasses.remove(FE4Data.CharacterClass.DANCER);
			}
			
			// If we randomize strictly mapped children, then we need to make sure the parent doesn't end up forcing a child into a blacklisted class.
			FE4Data.Character[] children = fe4Char.getChildren();
			if (options.childOption == ChildOptions.MATCH_STRICT && children.length > 0) {
				Set<FE4Data.CharacterClass> childBlacklist = new HashSet<FE4Data.CharacterClass>();
				for (int i = 0; i < children.length; i++) {
					FE4Data.Character child = children[i];
					childBlacklist.addAll(Arrays.asList(child.blacklistedClasses()));
				}
				potentialClasses.removeAll(childBlacklist);
			}
			
			// If we randomize minions, this probably isn't a problem, but due to weapon inheriting, we may have to restrict some characters so that their weapons don't cause issues in gen 2 if they end up on enemies.
			FE4Data.CharacterClass[] whitelistedClasses = fe4Char.whitelistedClasses(options.randomizeMinions);
			if (whitelistedClasses.length > 0) {
				potentialClasses.retainAll(Arrays.asList(whitelistedClasses));
			}
			
			if (potentialClasses.isEmpty()) {
				// The character is going to keep his class.
				classDistributor.removeItem(originalClass, false);
				
				// Update blood if necessary.
				if (options.playerBloodOption == BloodOptions.SHUFFLE && predeterminedBloodMap != null) {
					if (hasMajorBlood) {
						FE4Data.HolyBlood newMajor = predeterminedBloodMap.get(majorBloodType);
						final FE4Data.HolyBlood oldMajor = majorBloodType;
						slot1Blood.removeIf(blood -> (blood.bloodType() == oldMajor && blood.isMajor()));
						slot2Blood.removeIf(blood -> (blood.bloodType() == oldMajor && blood.isMajor()));
						slot3Blood.removeIf(blood -> (blood.bloodType() == oldMajor && blood.isMajor()));
						FE4Data.HolyBloodSlot1 slot1 = FE4Data.HolyBloodSlot1.blood(newMajor, true);
						FE4Data.HolyBloodSlot2 slot2 = FE4Data.HolyBloodSlot2.blood(newMajor, true);
						FE4Data.HolyBloodSlot3 slot3 = FE4Data.HolyBloodSlot3.blood(newMajor, true);
						if (slot1 != null) { slot1Blood.add(slot1); }
						else if (slot2 != null) { slot2Blood.add(slot2); }
						else if (slot3 != null) { slot3Blood.add(slot3); }
					}
					
					if (hasMinorBlood) {
						FE4Data.HolyBlood newMinor = predeterminedBloodMap.get(minorBloodType);
						final FE4Data.HolyBlood oldMinor = minorBloodType;
						slot1Blood.removeIf(blood -> (blood.bloodType() == oldMinor && !blood.isMajor()));
						slot2Blood.removeIf(blood -> (blood.bloodType() == oldMinor && !blood.isMajor()));
						slot3Blood.removeIf(blood -> (blood.bloodType() == oldMinor && !blood.isMajor()));
						FE4Data.HolyBloodSlot1 slot1 = FE4Data.HolyBloodSlot1.blood(newMinor, false);
						FE4Data.HolyBloodSlot2 slot2 = FE4Data.HolyBloodSlot2.blood(newMinor, false);
						FE4Data.HolyBloodSlot3 slot3 = FE4Data.HolyBloodSlot3.blood(newMinor, false);
						if (slot1 != null) { slot1Blood.add(slot1); }
						else if (slot2 != null) { slot2Blood.add(slot2); }
						else if (slot3 != null) { slot3Blood.add(slot3); }
					}
				}
				
				staticChar.setHolyBlood1Value(FE4Data.HolyBloodSlot1.valueForSlot1HolyBlood(slot1Blood));
				staticChar.setHolyBlood2Value(FE4Data.HolyBloodSlot2.valueForSlot2HolyBlood(slot2Blood));
				staticChar.setHolyBlood3Value(FE4Data.HolyBloodSlot3.valueForSlot3HolyBlood(slot3Blood));

				continue;
			}
			
			List<FE4Data.CharacterClass> classList = new ArrayList<FE4Data.CharacterClass>(potentialClasses);
			Collections.sort(classList, FE4Data.CharacterClass.defaultComparator);
			Set<HolyBlood> bloodOptions = new HashSet<HolyBlood>(Arrays.asList(fe4Char.limitedHolyBloodSelection()));
			
			if (options.playerBloodOption == BloodOptions.SHUFFLE) {
				if (hasMajorBlood) {
					assert bloodOptions.contains(predeterminedBloodMap.get(majorBloodType)) : "Attempted to assign blacklisted holy blood to character.";
					if (bloodOptions.contains(predeterminedBloodMap.get(majorBloodType))) {
						bloodOptions = new HashSet<HolyBlood>(Arrays.asList(predeterminedBloodMap.get(majorBloodType)));
					}
					
				} else if (hasMinorBlood) {
					if (bloodOptions.contains(predeterminedBloodMap.get(minorBloodType))) {
						bloodOptions = new HashSet<HolyBlood>(Arrays.asList(predeterminedBloodMap.get(minorBloodType)));
					}
				}
			}
			
			targetClass = classList.get(rng.nextInt(classList.size()));
			if (FE4Data.CharacterClass.reducedChanceClasses.contains(targetClass)) {
				// Reroll once for anybody ending up in these classes.
				targetClass = classList.get(rng.nextInt(classList.size()));
			}
			
			final Set<FE4Data.HolyBlood> finalBlood = bloodOptions;
			List<FE4Data.CharacterClass> fittingClasses = classList.stream().filter(charClass -> {
				Set<HolyBlood> supportedBlood = new HashSet<HolyBlood>(Arrays.asList(charClass.supportedHolyBlood()));
				supportedBlood.retainAll(finalBlood);
				return !supportedBlood.isEmpty();
			}).collect(Collectors.toList());
			
			if (!fittingClasses.isEmpty()) {
				targetClass = fittingClasses.get(rng.nextInt(fittingClasses.size()));
			}
			
			if (targetClass == FE4Data.CharacterClass.DANCER) { hasDancer = true; }
			setStaticCharacterToClass(options, staticChar, targetClass, !useFreeInventoryForStaves, charData, bloodData, itemMap, predeterminedClasses, predeterminedBloodMap, requiredItems, rng);
			if (useFreeInventoryForStaves) { giveStaffIfNecessary(options, staticChar, charData, itemMap, rng); }
			
			for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
				predeterminedClasses.put(linked, targetClass);
			}
			
			// Set ourselves as predetermined, in the odd case that we run across ourself again.
			// Also useful for children in this case.
			predeterminedClasses.put(fe4Char, targetClass);
			
			// Remove this class from the pool so that we avoid assigning it again.
			classDistributor.removeItem(targetClass, false);
		}
		
		// Gen 2 - Common
		// Reset the class distributor.
		classDistributor = new PoolDistributor<FE4Data.CharacterClass>();
		for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.promotedClasses) {
			classDistributor.addItem(charClass);
		}
		for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.unpromotedClasses) {
			classDistributor.addItem(charClass);
		}
		
		List<FE4StaticCharacter> gen2CommonCharacters = charData.getGen2CommonCharacters();
		for  (FE4StaticCharacter staticChar : gen2CommonCharacters) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
			if (fe4Char == null || blacklistedCharacters.contains(fe4Char)) { continue; }
			
			FE4Data.CharacterClass targetClass = predeterminedClasses.get(fe4Char);
			if (targetClass != null) {
				setStaticCharacterToClass(options, staticChar, targetClass, !useFreeInventoryForStaves, charData, bloodData, itemMap, predeterminedClasses, predeterminedBloodMap, requiredItems, rng);
				continue;
			}
			
			FE4Data.CharacterClass originalClass = FE4Data.CharacterClass.valueOf(staticChar.getClassID());
			if (originalClass == null) { continue; } // Shouldn't be touching this class. Skip this character.
			
			Set<FE4Data.CharacterClass> potentialClasses = new HashSet<FE4Data.CharacterClass>(); 
			
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
						Collections.sort(demotedClasses, FE4Data.CharacterClass.defaultComparator);
						if (!demotedClasses.isEmpty()) {
							referenceClass = demotedClasses.get(rng.nextInt(demotedClasses.size()));
						}
					} else if (!referenceClass.isPromoted() && originalClass.isPromoted()) {
						List<FE4Data.CharacterClass> promotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(referenceClass.promotionClasses(staticChar.isFemale())));
						Collections.sort(promotedClasses, FE4Data.CharacterClass.defaultComparator);
						if (!promotedClasses.isEmpty()) {
							referenceClass = promotedClasses.get(rng.nextInt(promotedClasses.size()));
						}
					}
				}
			}
			
			// Limit classes if this option is disabled for those with major holy blood (Sigurd, Quan, Brigid, Lewyn (and technically Claud)).
			int blood1Value = staticChar.getHolyBlood1Value();
			int blood2Value = staticChar.getHolyBlood2Value();
			int blood3Value = staticChar.getHolyBlood3Value();
			// Blood 4 is only Loptous, which we're not dealing with player-side.
			
			List<HolyBloodSlot1> slot1Blood = FE4Data.HolyBloodSlot1.slot1HolyBlood(blood1Value);
			List<HolyBloodSlot2> slot2Blood = FE4Data.HolyBloodSlot2.slot2HolyBlood(blood2Value);
			List<HolyBloodSlot3> slot3Blood = FE4Data.HolyBloodSlot3.slot3HolyBlood(blood3Value);
			
			boolean hasMajorBlood = slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent() ||
					slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent() ||
					slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent();
			FE4Data.HolyBlood majorBloodType = null;
			if (hasMajorBlood) {
				if (slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
					majorBloodType = slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType();
				} else if (slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
					majorBloodType = slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType();
				} else if (slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
					majorBloodType = slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType();
				}
			}
			
			boolean hasMinorBlood = slot1Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent() ||
					slot2Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent() ||
					slot3Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent();
			FE4Data.HolyBlood minorBloodType = null;
			if (hasMinorBlood) {
				if (slot1Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent()) {
					minorBloodType = slot1Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().get().bloodType();
				} else if (slot2Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent()) {
					minorBloodType = slot2Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().get().bloodType();
				} else if (slot3Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent()) {
					minorBloodType = slot3Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().get().bloodType();
				}
			}
			
			if (fe4Char.isHealer() && options.retainHealers) {
				Collections.addAll(potentialClasses, originalClass.getClassPool(true, false, true, staticChar.isFemale(), false, false, options.retainHorses && originalClass.isHorseback(), fe4Char.requiresMelee(), FE4Data.Item.HEAL, null));
			} else {
				FE4Data.Item mustUseItem = fe4Char.requiresWeapon();
				if (options.playerBloodOption == BloodOptions.NO_CHANGE) {
					if (hasMajorBlood) {
						mustUseItem = majorBloodType.holyWeapon.getType().getBasic();
					} else if (hasMinorBlood) {
						mustUseItem = minorBloodType.holyWeapon.getType().getBasic();
					}
				} else if (options.playerBloodOption == BloodOptions.SHUFFLE) {
					if (hasMajorBlood) {
						mustUseItem = predeterminedBloodMap.get(majorBloodType).holyWeapon.getType().getBasic();
					} else if (hasMinorBlood) {
						mustUseItem = predeterminedBloodMap.get(minorBloodType).holyWeapon.getType().getBasic();
					}
				}
				
				if (fe4Char.mustLoseToCharacters().length > 0) {
					// I'm not sure who would fit here, but we'll just force them to horseback. Armored units are male only, so a mix of genders is problematic.
					if (referenceClass != null) {
						Collections.addAll(potentialClasses, referenceClass.getClassPool(true, false, true, staticChar.isFemale(), true, fe4Char.requiresAttack(), true, fe4Char.requiresMelee(), mustUseItem, Item.HORSESLAYER));
					} else {
						Collections.addAll(potentialClasses, originalClass.getClassPool(false, false, true, staticChar.isFemale(), true, fe4Char.requiresAttack(), true, fe4Char.requiresMelee(), mustUseItem, Item.HORSESLAYER));
					}
				} else {
					if (referenceClass != null) {
						Collections.addAll(potentialClasses, referenceClass.getClassPool(true, false, false, staticChar.isFemale(), false, fe4Char.requiresAttack(), options.retainHorses && originalClass.isHorseback(), fe4Char.requiresMelee(), mustUseItem, null));
					} else {
						Collections.addAll(potentialClasses, originalClass.getClassPool(false, false, false, staticChar.isFemale(), false, fe4Char.requiresAttack(), options.retainHorses && originalClass.isHorseback(), fe4Char.requiresMelee(), mustUseItem, null));
					}
				}
			}
			
			if (options.assignEvenly) {
				if (Collections.disjoint(classDistributor.possibleResults(), potentialClasses)) {
					// Reload the class pool.
					for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.promotedClasses) {
						classDistributor.addItem(charClass);
					}
					for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.unpromotedClasses) {
						classDistributor.addItem(charClass);
					}
				}
				
				potentialClasses.retainAll(classDistributor.possibleResults());
			}
			
			potentialClasses.removeAll(new HashSet<FE4Data.CharacterClass>(Arrays.asList(fe4Char.blacklistedClasses())));
			potentialClasses.removeAll(blacklistedClasses);
			// No real candidates for Dancer here, so don't worry about it.
			potentialClasses.remove(FE4Data.CharacterClass.DANCER);
			
			if (potentialClasses.isEmpty()) { 
				classDistributor.removeItem(originalClass, false);
				continue;
			}
			
			List<FE4Data.CharacterClass> classList = new ArrayList<FE4Data.CharacterClass>(potentialClasses);
			Collections.sort(classList, FE4Data.CharacterClass.defaultComparator);
			Set<HolyBlood> bloodOptions = new HashSet<HolyBlood>(Arrays.asList(fe4Char.limitedHolyBloodSelection()));
			
			if (options.playerBloodOption == BloodOptions.SHUFFLE) {
				if (hasMajorBlood) {
					assert bloodOptions.contains(predeterminedBloodMap.get(majorBloodType)) : "Attempted to assign blacklisted holy blood to character.";
					if (bloodOptions.contains(predeterminedBloodMap.get(majorBloodType))) {
						bloodOptions = new HashSet<HolyBlood>(Arrays.asList(predeterminedBloodMap.get(majorBloodType)));
					}
					
				} else if (hasMinorBlood) {
					if (bloodOptions.contains(predeterminedBloodMap.get(minorBloodType))) {
						bloodOptions = new HashSet<HolyBlood>(Arrays.asList(predeterminedBloodMap.get(minorBloodType)));
					}
				}
			}
			
			targetClass = classList.get(rng.nextInt(classList.size()));
			if (FE4Data.CharacterClass.reducedChanceClasses.contains(targetClass)) {
				// Reroll once for anybody ending up in these classes.
				targetClass = classList.get(rng.nextInt(classList.size()));
			}
			
			final Set<FE4Data.HolyBlood> finalBlood = bloodOptions;
			List<FE4Data.CharacterClass> fittingClasses = classList.stream().filter(charClass -> {
				Set<HolyBlood> supportedBlood = new HashSet<HolyBlood>(Arrays.asList(charClass.supportedHolyBlood()));
				supportedBlood.retainAll(finalBlood);
				return !supportedBlood.isEmpty();
			}).collect(Collectors.toList());
			
			if (!fittingClasses.isEmpty()) {
				targetClass = fittingClasses.get(rng.nextInt(fittingClasses.size()));
			}
			
			setStaticCharacterToClass(options, staticChar, targetClass, !useFreeInventoryForStaves, charData, bloodData, itemMap, predeterminedClasses, predeterminedBloodMap, requiredItems, rng);
			if (useFreeInventoryForStaves) { giveStaffIfNecessary(options, staticChar, charData, itemMap, rng); }
			
			for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
				predeterminedClasses.put(linked, targetClass);
			}
			
			predeterminedClasses.put(fe4Char, targetClass);
			
			// Remove the selected class from the pool.
			classDistributor.removeItem(targetClass, false);
		}
		
		
		if (options.includeDancers) { // Allow another dancer for gen 2.
			hasDancer = false;
		}
		// Gen 2 - Children/Substitutes
		// Reset the class Distributor
		classDistributor = new PoolDistributor<FE4Data.CharacterClass>();
		for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.promotedClasses) {
			classDistributor.addItem(charClass);
		}
		for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.unpromotedClasses) {
			classDistributor.addItem(charClass);
		}
		
		// Subs get their own pool.
		PoolDistributor<FE4Data.CharacterClass> subsPool = new PoolDistributor<FE4Data.CharacterClass>();
		for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.promotedClasses) {
			subsPool.addItem(charClass);
		}
		for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.unpromotedClasses) {
			subsPool.addItem(charClass);
		}
		
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
			
			List<FE4Data.HolyBlood> majorBlood = new ArrayList<FE4Data.HolyBlood>();
			
			if ((parent.isFemale() && child.getMajorInfluence() == Influence.MOTHER) || (!parent.isFemale() && child.getMajorInfluence() == Influence.FATHER)) {
				List<FE4Data.HolyBloodSlot1> parentSlot1 = FE4Data.HolyBloodSlot1.slot1HolyBlood(parent.getHolyBlood1Value());
				List<FE4Data.HolyBloodSlot2> parentSlot2 = FE4Data.HolyBloodSlot2.slot2HolyBlood(parent.getHolyBlood2Value());
				List<FE4Data.HolyBloodSlot3> parentSlot3 = FE4Data.HolyBloodSlot3.slot3HolyBlood(parent.getHolyBlood3Value());
			
				majorBlood.addAll(parentSlot1.stream().filter(blood -> (blood.isMajor() == true)).map(slot1 -> (slot1.bloodType())).collect(Collectors.toList()));
				majorBlood.addAll(parentSlot2.stream().filter(blood -> (blood.isMajor() == true)).map(slot2 -> (slot2.bloodType())).collect(Collectors.toList()));
				majorBlood.addAll(parentSlot3.stream().filter(blood -> (blood.isMajor() == true)).map(slot3 -> (slot3.bloodType())).collect(Collectors.toList()));
			}
			
			FE4Data.Character referenceCharacter = fe4Char.getGen1Analogue();
			boolean requiresWeakness = fe4Char.mustLoseToCharacters().length > 0;
			boolean restrictedHealer = fe4Char.isHealer() && options.retainHealers;
			
			final FE4Data.CharacterClass currentClass = FE4Data.CharacterClass.valueOf(child.getClassID());
			
			if (options.childOption == ChildOptions.MATCH_STRICT) {
				targetClass = predeterminedClasses.get(referenceCharacter);
				if (targetClass != null) {
					if (targetClass.isPromoted() && !currentClass.isPromoted()) {
						List<FE4Data.CharacterClass> demotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(targetClass.demotedClasses(child.isFemale())));
						if (options.assignEvenly) {
							if (Collections.disjoint(demotedClasses, classDistributor.possibleResults())) {
								// Reload the class pool. Only unpromoted classes should be necessary here.
								for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.unpromotedClasses) {
									classDistributor.addItem(charClass);
								}
								
								demotedClasses.retainAll(classDistributor.possibleResults());
							}
						}
						Collections.sort(demotedClasses, FE4Data.CharacterClass.defaultComparator);
						if (demotedClasses.size() > 0) {
							targetClass = demotedClasses.get(rng.nextInt(demotedClasses.size()));
						}
					} else if (!targetClass.isPromoted() && currentClass.isPromoted()) {
						List<FE4Data.CharacterClass> promotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(targetClass.promotionClasses(child.isFemale())));
						if (options.assignEvenly) {
							if (Collections.disjoint(promotedClasses, classDistributor.possibleResults())) {
								// Reload the class pool. Only promoted classes should be necessary here.
								for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.promotedClasses) {
									classDistributor.addItem(charClass);
								}
							}
							
							promotedClasses.retainAll(classDistributor.possibleResults());
						}
						Collections.sort(promotedClasses, FE4Data.CharacterClass.defaultComparator);
						if (promotedClasses.size() > 0) {
							targetClass = promotedClasses.get(rng.nextInt(promotedClasses.size()));
						}
					}
				}
				
				if (currentClass.isFlier()) {
					// Override for Altena, who needs to remain flying to not break stuff.
					List<FE4Data.CharacterClass> classPool = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(currentClass.getClassPool(true, false, true, child.isFemale(), false, fe4Char.requiresAttack(), options.retainHorses && currentClass.isHorseback(), fe4Char.requiresMelee(), fe4Char.requiresWeapon(), null)));
					Collections.sort(classPool, FE4Data.CharacterClass.defaultComparator);
					if (classPool.size() > 0) {
						targetClass = classPool.get(rng.nextInt(classPool.size()));
					}
				}
			} else if (options.childOption == ChildOptions.MATCH_LOOSE) {
				FE4Data.CharacterClass referenceClass = predeterminedClasses.get(referenceCharacter);
				FE4Data.HolyBlood majorHolyBlood = null;
				if (!majorBlood.isEmpty()) { majorHolyBlood = majorBlood.get(0); }
				final FE4Data.HolyBlood holyBlood = majorHolyBlood;
				
				if (referenceClass != null) {
					if (referenceClass.isPromoted() && !currentClass.isPromoted()) {
						List<FE4Data.CharacterClass> demotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(referenceClass.demotedClasses(child.isFemale())));
						Collections.sort(demotedClasses, FE4Data.CharacterClass.defaultComparator);
						if (demotedClasses.size() > 0) {
							referenceClass = demotedClasses.get(rng.nextInt(demotedClasses.size()));
						}
					} else if (!referenceClass.isPromoted() && currentClass.isPromoted()) {
						List<FE4Data.CharacterClass> promotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(referenceClass.promotionClasses(child.isFemale())));
						Collections.sort(promotedClasses, FE4Data.CharacterClass.defaultComparator);
						if (promotedClasses.size() > 0) {
							referenceClass = promotedClasses.get(rng.nextInt(promotedClasses.size()));
						}
					}
				}
				
				if (referenceClass == null) { referenceClass = currentClass; }
				Set<FE4Data.CharacterClass> poolSet = new HashSet<FE4Data.CharacterClass>(Arrays.asList(referenceClass.getClassPool(true, false, true, child.isFemale(), requiresWeakness, fe4Char.requiresAttack(), options.retainHorses && currentClass.isHorseback(), fe4Char.requiresMelee(), restrictedHealer ? Item.HEAL : fe4Char.requiresWeapon(), null)));
				if (hasDancer) { poolSet.remove(FE4Data.CharacterClass.DANCER); }
				poolSet.removeAll(Arrays.asList(fe4Char.blacklistedClasses()));
				FE4Data.CharacterClass[] whitelistedClasses = fe4Char.whitelistedClasses(options.randomizeMinions);
				if (whitelistedClasses.length > 0) {
					poolSet.retainAll(Arrays.asList(whitelistedClasses));
				}
				
				if (options.assignEvenly) {
					if (Collections.disjoint(poolSet, classDistributor.possibleResults())) {
						// Reload the class pool.
						for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.promotedClasses) {
							classDistributor.addItem(charClass);
						}
						for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.unpromotedClasses) {
							classDistributor.addItem(charClass);
						}
					}
					
					poolSet.retainAll(classDistributor.possibleResults());
				}
				
				List<FE4Data.CharacterClass> poolList;
				if (majorHolyBlood != null) {			
					poolList = poolSet.stream().filter(charClass -> {
						Set<FE4Data.HolyBlood> supportedSet = new HashSet<FE4Data.HolyBlood>(Arrays.asList(charClass.supportedHolyBlood()));
						return supportedSet.contains(holyBlood);
					}).collect(Collectors.toList());
				} else {
					poolList = new ArrayList<FE4Data.CharacterClass>(poolSet);
				}
				
				if (poolList.isEmpty() && !poolSet.isEmpty()) {
					poolList = poolSet.stream().sorted(FE4Data.CharacterClass.defaultComparator).collect(Collectors.toList());
				}
				
				if (!poolList.isEmpty()) {
					targetClass = poolList.get(rng.nextInt(poolList.size()));
					if (targetClass == FE4Data.CharacterClass.DANCER) { hasDancer = true; }
				}
			} else {
				FE4Data.CharacterClass referenceClass = FE4Data.CharacterClass.valueOf(child.getClassID());
				Set<FE4Data.CharacterClass> poolSet = new HashSet<FE4Data.CharacterClass>(Arrays.asList(referenceClass.getClassPool(false, false, true, child.isFemale(), requiresWeakness, fe4Char.requiresAttack(), options.retainHorses && currentClass.isHorseback(), fe4Char.requiresMelee(), restrictedHealer ? Item.HEAL : fe4Char.requiresWeapon(), null)));
				if (hasDancer) { poolSet.remove(FE4Data.CharacterClass.DANCER); }
				poolSet.removeAll(Arrays.asList(fe4Char.blacklistedClasses()));
				poolSet.removeAll(blacklistedClasses);
				FE4Data.CharacterClass[] whitelistedClasses = fe4Char.whitelistedClasses(options.randomizeMinions);
				if (whitelistedClasses.length > 0) {
					poolSet.retainAll(Arrays.asList(whitelistedClasses));
				}
				if (options.assignEvenly) {
					if (Collections.disjoint(poolSet, classDistributor.possibleResults())) {
						// Reload the class pool.
						for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.promotedClasses) {
							classDistributor.addItem(charClass);
						}
						for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.unpromotedClasses) {
							classDistributor.addItem(charClass);
						}
					}
					
					poolSet.retainAll(classDistributor.possibleResults());
				}
				List<FE4Data.CharacterClass> poolList = new ArrayList<FE4Data.CharacterClass>(poolSet);
				Collections.sort(poolList, FE4Data.CharacterClass.defaultComparator);
				if (!poolList.isEmpty()) {
					targetClass = poolList.get(rng.nextInt(poolList.size()));
					if (targetClass == FE4Data.CharacterClass.DANCER) { hasDancer = true; }
				}
			}
			
			if (targetClass != null) {
				if (child.isFemale()) { targetClass = targetClass.toFemale(); }
				else { targetClass = targetClass.toMale(); }
				setChildCharacterToClass(options, child, parent, targetClass, itemMap, rng);
				
				for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
					predeterminedClasses.put(linked, targetClass);
				}
				predeterminedClasses.put(fe4Char, targetClass);
				
				classDistributor.removeItem(targetClass, false);
				
				FE4Data.CharacterClass referenceClass = targetClass;
				
				FE4Data.Character sub = fe4Char.substituteForChild();
				if (sub != null) {
					FE4StaticCharacter subChar = charData.getStaticCharacter(sub);
					if (subChar != null) {
						Set<FE4Data.CharacterClass> pool  = new HashSet<FE4Data.CharacterClass>(Arrays.asList(referenceClass.getClassPool(true, false, true, subChar.isFemale(), requiresWeakness, fe4Char.requiresAttack(), options.retainHorses && currentClass.isHorseback(), fe4Char.requiresMelee(), restrictedHealer ? Item.HEAL : sub.requiresWeapon(), null)));
						FE4Data.CharacterClass subClass = referenceClass; // Use this as a fallback.
						if (options.assignEvenly) {
							if (Collections.disjoint(pool, subsPool.possibleResults())) {
								// Reload the substitutes class pool.
								for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.promotedClasses) {
									subsPool.addItem(charClass);
								}
								for (FE4Data.CharacterClass charClass : FE4Data.CharacterClass.unpromotedClasses) {
									subsPool.addItem(charClass);
								}
							}
							
							pool.retainAll(subsPool.possibleResults());
						}
						List<FE4Data.CharacterClass> subsList = pool.stream().sorted(FE4Data.CharacterClass.defaultComparator).collect(Collectors.toList());
						if (subsList.size() > 0) {
							 subClass = subsList.get(rng.nextInt(subsList.size()));
							 subsPool.removeItem(subClass, false);
						}
						
						setStaticCharacterToClass(options, subChar, subClass, !useFreeInventoryForStaves, charData, bloodData, itemMap, predeterminedClasses, predeterminedBloodMap, requiredItems, targetClass, rng);
						if (useFreeInventoryForStaves) { giveStaffIfNecessary(options, subChar, charData, itemMap, rng); }
						
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
				if (fe4Char.joinChapter() < 1) { 
					FE4Data.CharacterClass charClass = predeterminedClasses.get(fe4Char);
					possibleWeapons.addAll(Arrays.asList(charClass.usableItems(fullMinor1, fullMinor2, fullMinor3))); 
				}
			}
			possibleWeapons.removeIf(item -> (!item.isWeapon()));
			
			// Do not use super powerful weapons immediately. Stick with basic here. 
			possibleWeapons.retainAll(FE4Data.Item.normalWeapons);
			possibleWeapons.retainAll(FE4Data.Item.cWeapons);
			possibleWeapons.removeAll(FE4Data.Item.siegeTomes);
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons);
				Collections.sort(currentList, FE4Data.Item.defaultComparator);
				for (Integer i : inventoryIndices) {
					FE4Data.Item currentItem = itemMap.getItemAtIndex(i);
					if (currentItem.isRing()) { continue; }
					if (currentList.stream().filter(item -> (FE4Data.Item.healingStaves.contains(item))).findFirst().isPresent()) {
						List<FE4Data.Item> healingStaves = currentList.stream().filter(item -> (FE4Data.Item.healingStaves.contains(item))).collect(Collectors.toList());
						FE4Data.Item randomItem = healingStaves.get(rng.nextInt(healingStaves.size()));
						itemMap.setItemAtIndex(i, randomItem);
						possibleWeapons.remove(randomItem);
					} else {
						FE4Data.Item randomItem = currentList.get(rng.nextInt(currentList.size()));
						itemMap.setItemAtIndex(i, randomItem);
						possibleWeapons.remove(randomItem);
					}
				}
			}
			
			possibleWeapons.clear();
			inventoryIndices = FE4Data.Shops.CHAPTER_2.inventoryIDs;
			for (FE4Data.Character fe4Char : predeterminedClasses.keySet()) {
				if (fe4Char.joinChapter() < 2) {
					FE4Data.CharacterClass charClass = predeterminedClasses.get(fe4Char);
					possibleWeapons.addAll(Arrays.asList(charClass.usableItems(fullMinor1, fullMinor2, fullMinor3)));
				}
			}
			
			possibleWeapons.removeIf(item -> (!item.isWeapon()));
			// Do not use super powerful weapons immediately. Stick with basic here. Allow Bs though. 
			possibleWeapons.retainAll(FE4Data.Item.normalWeapons);
			possibleWeapons.removeAll(FE4Data.Item.aWeapons);
			possibleWeapons.removeAll(FE4Data.Item.siegeTomes);
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons); 
				Collections.sort(currentList, FE4Data.Item.defaultComparator);
				for (Integer i : inventoryIndices) {
					FE4Data.Item currentItem = itemMap.getItemAtIndex(i);
					if (currentItem.isRing()) { continue; }
					if (currentList.stream().filter(item -> (FE4Data.Item.healingStaves.contains(item))).findFirst().isPresent()) {
						List<FE4Data.Item> healingStaves = currentList.stream().filter(item -> (FE4Data.Item.healingStaves.contains(item))).collect(Collectors.toList());
						FE4Data.Item randomItem = healingStaves.get(rng.nextInt(healingStaves.size()));
						itemMap.setItemAtIndex(i, randomItem);
						possibleWeapons.remove(randomItem);
					} else {
						FE4Data.Item randomItem = currentList.get(rng.nextInt(currentList.size()));
						itemMap.setItemAtIndex(i, randomItem);
						possibleWeapons.remove(randomItem);
					}
				}
			}
			
			possibleWeapons.clear();
			inventoryIndices = FE4Data.Shops.CHAPTER_3.inventoryIDs;
			for (FE4Data.Character fe4Char : predeterminedClasses.keySet()) {
				if (fe4Char.joinChapter() < 3) {
					FE4Data.CharacterClass charClass = predeterminedClasses.get(fe4Char);
					possibleWeapons.addAll(Arrays.asList(charClass.usableItems(fullMinor1, fullMinor2, fullMinor3)));
				}
			}
			
			possibleWeapons.removeIf(item -> (!item.isWeapon()));
			// Begin allowing interesting weapons. 
			possibleWeapons.removeAll(FE4Data.Item.powerfulWeapons);
			possibleWeapons.removeAll(FE4Data.Item.siegeTomes);
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons);
				Collections.sort(currentList, FE4Data.Item.defaultComparator);
				for (Integer i : inventoryIndices) {
					FE4Data.Item currentItem = itemMap.getItemAtIndex(i);
					if (currentItem.isRing()) { continue; }
					if (currentList.stream().filter(item -> (FE4Data.Item.healingStaves.contains(item))).findFirst().isPresent()) {
						List<FE4Data.Item> healingStaves = currentList.stream().filter(item -> (FE4Data.Item.healingStaves.contains(item))).collect(Collectors.toList());
						FE4Data.Item randomItem = healingStaves.get(rng.nextInt(healingStaves.size()));
						itemMap.setItemAtIndex(i, randomItem);
						possibleWeapons.remove(randomItem);
					} else {
						FE4Data.Item randomItem = currentList.get(rng.nextInt(currentList.size()));
						itemMap.setItemAtIndex(i, randomItem);
						possibleWeapons.remove(randomItem);
					}
				}
			}
			
			possibleWeapons.clear();
			inventoryIndices = FE4Data.Shops.CHAPTER_4.inventoryIDs;
			for (FE4Data.Character fe4Char : predeterminedClasses.keySet()) {
				if (fe4Char.joinChapter() < 4) {
					FE4Data.CharacterClass charClass = predeterminedClasses.get(fe4Char);
					possibleWeapons.addAll(Arrays.asList(charClass.usableItems(fullMinor1, fullMinor2, fullMinor3)));
				}
			}
			
			possibleWeapons.removeIf(item -> (!item.isWeapon()));
			// Keep powerful weapons out of reach still.
			possibleWeapons.removeAll(FE4Data.Item.powerfulWeapons);
			possibleWeapons.removeAll(FE4Data.Item.siegeTomes);
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons);
				Collections.sort(currentList, FE4Data.Item.defaultComparator);
				for (Integer i : inventoryIndices) {
					FE4Data.Item currentItem = itemMap.getItemAtIndex(i);
					if (currentItem.isRing()) { continue; }
					if (currentList.stream().filter(item -> (FE4Data.Item.healingStaves.contains(item))).findFirst().isPresent()) {
						List<FE4Data.Item> healingStaves = currentList.stream().filter(item -> (FE4Data.Item.healingStaves.contains(item))).collect(Collectors.toList());
						FE4Data.Item randomItem = healingStaves.get(rng.nextInt(healingStaves.size()));
						itemMap.setItemAtIndex(i, randomItem);
						possibleWeapons.remove(randomItem);
					} else {
						FE4Data.Item randomItem = currentList.get(rng.nextInt(currentList.size()));
						itemMap.setItemAtIndex(i, randomItem);
						possibleWeapons.remove(randomItem);
					}
				}
			}
			
			possibleWeapons.clear();
			inventoryIndices = FE4Data.Shops.CHAPTER_5.inventoryIDs;
			for (FE4Data.Character fe4Char : predeterminedClasses.keySet()) {
				if (fe4Char.joinChapter() < 5) {
					FE4Data.CharacterClass charClass = predeterminedClasses.get(fe4Char);
					possibleWeapons.addAll(Arrays.asList(charClass.usableItems(fullMinor1, fullMinor2, fullMinor3)));
				}
			}
			
			possibleWeapons.removeIf(item -> (!item.isWeapon()));
			// Anything goes now.
			possibleWeapons.removeAll(FE4Data.Item.siegeTomes); // Except Siege tomes
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons);
				Collections.sort(currentList, FE4Data.Item.defaultComparator);
				for (Integer i : inventoryIndices) {
					FE4Data.Item currentItem = itemMap.getItemAtIndex(i);
					if (currentItem.isRing()) { continue; }
					if (currentList.stream().filter(item -> (FE4Data.Item.healingStaves.contains(item))).findFirst().isPresent()) {
						List<FE4Data.Item> healingStaves = currentList.stream().filter(item -> (FE4Data.Item.healingStaves.contains(item))).collect(Collectors.toList());
						FE4Data.Item randomItem = healingStaves.get(rng.nextInt(healingStaves.size()));
						itemMap.setItemAtIndex(i, randomItem);
						possibleWeapons.remove(randomItem);
					} else {
						FE4Data.Item randomItem = currentList.get(rng.nextInt(currentList.size()));
						itemMap.setItemAtIndex(i, randomItem);
						possibleWeapons.remove(randomItem);
					}
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
			// Back to basics.
			possibleWeapons.retainAll(FE4Data.Item.normalWeapons);
			possibleWeapons.retainAll(FE4Data.Item.cWeapons);
			possibleWeapons.removeAll(FE4Data.Item.siegeTomes);
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons);
				Collections.sort(currentList, FE4Data.Item.defaultComparator);
				for (Integer i : inventoryIndices) {
					FE4Data.Item currentItem = itemMap.getItemAtIndex(i);
					if (currentItem.isRing()) { continue; }
					FE4Data.Item randomItem = currentList.get(rng.nextInt(currentList.size()));
					itemMap.setItemAtIndex(i, randomItem);
				}
			}
			
			possibleWeapons.clear();
			inventoryIndices = FE4Data.Shops.CHAPTER_7.inventoryIDs;
			for (FE4Data.Character fe4Char : predeterminedClasses.keySet()) {
				if (fe4Char.joinChapter() == 6 || fe4Char.joinChapter() == 7) {
					FE4Data.CharacterClass charClass = predeterminedClasses.get(fe4Char);
					possibleWeapons.addAll(Arrays.asList(charClass.usableItems(fullMinor1, fullMinor2, fullMinor3)));
				}
			}
			
			possibleWeapons.removeIf(item -> (!item.isWeapon()));
			// Allow Bs.
			possibleWeapons.retainAll(FE4Data.Item.normalWeapons);
			possibleWeapons.removeAll(FE4Data.Item.bWeapons);
			possibleWeapons.removeAll(FE4Data.Item.siegeTomes);
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons);
				Collections.sort(currentList, FE4Data.Item.defaultComparator);
				for (Integer i : inventoryIndices) {
					FE4Data.Item currentItem = itemMap.getItemAtIndex(i);
					if (currentItem.isRing()) { continue; }
					FE4Data.Item randomItem = currentList.get(rng.nextInt(currentList.size()));
					itemMap.setItemAtIndex(i, randomItem);
				}
			}
			
			possibleWeapons.clear();
			inventoryIndices = FE4Data.Shops.CHAPTER_8.inventoryIDs;
			for (FE4Data.Character fe4Char : predeterminedClasses.keySet()) {
				if (fe4Char.joinChapter() >= 6 && fe4Char.joinChapter() <= 8) {
					FE4Data.CharacterClass charClass = predeterminedClasses.get(fe4Char);
					possibleWeapons.addAll(Arrays.asList(charClass.usableItems(fullMinor1, fullMinor2, fullMinor3)));
				}
			}
			
			possibleWeapons.removeIf(item -> (!item.isWeapon()));
			// Just allow everything at this point.
			possibleWeapons.removeAll(FE4Data.Item.siegeTomes); // Still no Siege tomes.
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons);
				Collections.sort(currentList, FE4Data.Item.defaultComparator);
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
			possibleWeapons.removeAll(FE4Data.Item.siegeTomes);
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons);
				Collections.sort(currentList, FE4Data.Item.defaultComparator);
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
			possibleWeapons.removeAll(FE4Data.Item.siegeTomes);
			
			if (!possibleWeapons.isEmpty()) {
				List<FE4Data.Item> currentList = new ArrayList<FE4Data.Item>(possibleWeapons);
				Collections.sort(currentList, FE4Data.Item.defaultComparator);
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
			weapons.removeAll(FE4Data.Item.brokenWeapons);
			weapons.removeAll(FE4Data.Item.siegeTomes);
			
			Collections.sort(weapons, FE4Data.Item.defaultComparator);
			
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
	public static void randomizeMinions(FE4ClassOptions options, CharacterDataLoader charData, ItemMapper itemMap, Map<FE4Data.HolyBlood, FE4Data.HolyBlood> predeterminedBloodMap, Random rng) {
		List<FE4EnemyCharacter> enemies = charData.getMinions();
		randomizeEnemies(options, enemies, charData, itemMap, rng);
		
		// Some minions are classified as holy blood bosses because they have some personal skill or holy blood.
		// They should be treated as minions and not bosses.
		List<FE4StaticCharacter> holyBosses = charData.getHolyBossCharacters();
		Map<FE4Data.Character, FE4Data.CharacterClass> predeterminedClasses = new HashMap<FE4Data.Character, FE4Data.CharacterClass>();
		Map<FE4Data.Character, BloodArrays> predeterminedBlood = new HashMap<FE4Data.Character, BloodArrays>();
		
		for (FE4StaticCharacter holyBoss : holyBosses) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(holyBoss.getCharacterID());
			if (!fe4Char.isMinion()) { continue; }
			
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
					// Playable characters are already set at this point.
					// So we can just refer to them directly.
					FE4StaticCharacter playableCharacter = charData.getStaticCharacter(referenceCharacter);
					referenceClass = FE4Data.CharacterClass.valueOf(playableCharacter.getClassID());
				} else if (referenceCharacter.isBoss()) {
					// These will not have been assigned yet, so if we do have this case, the minion decides the weapon.
				}
				
				if (referenceClass != null) {
					if (referenceClass.isPromoted() && !originalClass.isPromoted()) {
						List<FE4Data.CharacterClass> demotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(referenceClass.demotedClasses(holyBoss.isFemale())));
						Collections.sort(demotedClasses, FE4Data.CharacterClass.defaultComparator);
						if (!demotedClasses.isEmpty()) {
							referenceClass = demotedClasses.get(rng.nextInt(demotedClasses.size()));
						}
					} else if (!referenceClass.isPromoted() && originalClass.isPromoted()) {
						List<FE4Data.CharacterClass> promotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(referenceClass.promotionClasses(holyBoss.isFemale())));
						Collections.sort(promotedClasses, FE4Data.CharacterClass.defaultComparator);
						if (!promotedClasses.isEmpty()) {
							referenceClass = promotedClasses.get(rng.nextInt(promotedClasses.size()));
						}
					}
				}
			}
			
			Set<FE4Data.CharacterClass> potentialClasses = new HashSet<FE4Data.CharacterClass>();
			
			FE4Data.Item mustUseItem = null;
			if (options.bossBloodOption == BloodOptions.NO_CHANGE) {
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
			
			if (fe4Char.mustBeatCharacter().length > 0 || fe4Char.mustLoseToCharacters().length > 0) {
				// Don't change these. Even if we leverage effective weapons, it's not guaranteed that the winners will actually win.
				// Just don't touch these at all. (Quan and Ethlyn are locked to horses, so this is fine.)
			} else {
				if (mustUseItem == null && fe4Char.isGen2() && holyBoss.getEquipment3() != FE4Data.Item.NONE.ID) {
					mustUseItem = itemMap.getItemAtIndex(holyBoss.getEquipment3());
				}
				
				if (referenceClass != null) {
					Collections.addAll(potentialClasses, referenceClass.getClassPool(true, false, true, holyBoss.isFemale(), false, fe4Char.requiresAttack(), false, fe4Char.requiresMelee(), mustUseItem, null));
				} else {
					Collections.addAll(potentialClasses, originalClass.getClassPool(false, false, true, holyBoss.isFemale(), false, fe4Char.requiresAttack(), false, fe4Char.requiresMelee(), mustUseItem, null));
				}
			}
			
			potentialClasses.removeAll(Arrays.asList(fe4Char.blacklistedClasses()));
			
			if (potentialClasses.isEmpty()) { continue; }
			
			List<FE4Data.CharacterClass> classList = new ArrayList<FE4Data.CharacterClass>(potentialClasses);
			Collections.sort(classList, FE4Data.CharacterClass.defaultComparator);
			
			targetClass = classList.get(rng.nextInt(potentialClasses.size()));
			randomizeHolyBossBlood(options, holyBoss, targetClass, charData, predeterminedClasses, predeterminedBloodMap, itemMap, rng);
			
			for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
				predeterminedClasses.put(linked, targetClass);
				predeterminedBlood.put(linked, new BloodArrays(holyBoss));
			}
			
			// Set ourselves as predetermined, in the odd case that we run across ourself again.
			// Also useful for children in this case.
			predeterminedClasses.put(fe4Char, targetClass);
		}
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
	public static void randomizeBosses(FE4ClassOptions options, CharacterDataLoader charData, ItemMapper itemMap, Map<FE4Data.HolyBlood, FE4Data.HolyBlood> predeterminedBloodMap, Random rng) {
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
			
			// Some holy bosses are actually just random minions with skills or blood. We should treat them as minions and not as bosses.
			if (fe4Char.isMinion() && !options.randomizeMinions) { continue; }
			
			FE4Data.CharacterClass targetClass = predeterminedClasses.get(fe4Char);
			if (targetClass != null) {
				BloodArrays blood = predeterminedBlood.get(fe4Char);
				if (blood != null) {
					holyBoss.setHolyBlood1Value(FE4Data.HolyBloodSlot1.valueForSlot1HolyBlood(blood.slot1Blood));
					holyBoss.setHolyBlood2Value(FE4Data.HolyBloodSlot2.valueForSlot2HolyBlood(blood.slot2Blood));
					holyBoss.setHolyBlood3Value(FE4Data.HolyBloodSlot3.valueForSlot3HolyBlood(blood.slot3Blood));
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
			for (FE4Data.Character linkedChar : fe4Char.linkedCharacters()) {
				if (FE4Data.WeaklyLinkedCharacters.containsKey(linkedChar)) {
					FE4Data.Character referenceCharacter = FE4Data.WeaklyLinkedCharacters.get(linkedChar);
					if (referenceCharacter.isPlayable() && referenceCharacter.isGen1()) {
						FE4StaticCharacter playableCharacter = charData.getStaticCharacter(referenceCharacter);
						referenceClass = FE4Data.CharacterClass.valueOf(playableCharacter.getClassID());
					} else if (referenceCharacter.isBoss()) {
						// Use the predetermined class (if it was already set)
						referenceClass = predeterminedClasses.get(referenceCharacter);
					}
					
					if (referenceClass != null) {
						if (referenceClass.isPromoted() && !originalClass.isPromoted()) {
							List<FE4Data.CharacterClass> demotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(referenceClass.demotedClasses(holyBoss.isFemale())));
							Collections.sort(demotedClasses, FE4Data.CharacterClass.defaultComparator);
							if (!demotedClasses.isEmpty()) {
								referenceClass = demotedClasses.get(rng.nextInt(demotedClasses.size()));
							}
						} else if (!referenceClass.isPromoted() && originalClass.isPromoted()) {
							List<FE4Data.CharacterClass> promotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(referenceClass.promotionClasses(holyBoss.isFemale())));
							Collections.sort(promotedClasses, FE4Data.CharacterClass.defaultComparator);
							if (!promotedClasses.isEmpty()) {
								referenceClass = promotedClasses.get(rng.nextInt(promotedClasses.size()));
							}
						}
						
						break;
					}
				}
			}
			
			Set<FE4Data.CharacterClass> potentialClasses = new HashSet<FE4Data.CharacterClass>();
			
			// Limit classes if this option is disabled for those with major holy blood (most endgame bosses).
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
				hasMajorBlood = true;
				majorBloodType = slot1Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType();
			} else if (slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
				hasMajorBlood = true;
				majorBloodType = slot2Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType();
			} else if (slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) {
				hasMajorBlood = true;
				majorBloodType = slot3Blood.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType();
			}
			
			boolean hasMinorBlood = false;
			FE4Data.HolyBlood minorBloodType = null;
			
			if (slot1Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent()) {
				hasMinorBlood = true;
				minorBloodType = slot1Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().get().bloodType();
			} else if (slot2Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent()) {
				hasMinorBlood = true;
				minorBloodType = slot2Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().get().bloodType();
			} else if (slot3Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().isPresent()) {
				hasMinorBlood = true;
				minorBloodType = slot3Blood.stream().filter(blood -> (!blood.isMajor())).findFirst().get().bloodType();
			}
			
			FE4Data.Item mustUseItem = null;
			if (options.bossBloodOption != BloodOptions.RANDOMIZE) {
				if (hasMajorBlood) {
					if (options.bossBloodOption == BloodOptions.NO_CHANGE) { mustUseItem = majorBloodType.holyWeapon.getType().getBasic(); }
					else if (options.bossBloodOption == BloodOptions.SHUFFLE) { mustUseItem = predeterminedBloodMap.get(majorBloodType).holyWeapon.getType().getBasic(); }
				} else if (hasMinorBlood) {
					if (options.bossBloodOption == BloodOptions.NO_CHANGE) { mustUseItem = minorBloodType.holyWeapon.getType().getBasic(); }
					else if (options.bossBloodOption == BloodOptions.SHUFFLE) { mustUseItem = predeterminedBloodMap.get(minorBloodType).holyWeapon.getType().getBasic(); }
				}
			}
			
			if (fe4Char.mustBeatCharacter().length > 0 || fe4Char.mustLoseToCharacters().length > 0) {
				// Don't change these. Even if we leverage effective weapons, it's not guaranteed that the winners will actually win.
				// Just don't touch these at all. (Quan and Ethlyn are locked to horses, so this is fine.)
			} else {
				if (mustUseItem == null && fe4Char.isGen2() && holyBoss.getEquipment3() != FE4Data.Item.NONE.ID) {
					mustUseItem = itemMap.getItemAtIndex(holyBoss.getEquipment3());
				}
				
				if (referenceClass != null) {
					Collections.addAll(potentialClasses, referenceClass.getClassPool(true, true, true, holyBoss.isFemale(), false, true, false, fe4Char.requiresMelee(), mustUseItem, null));
				} else {
					Collections.addAll(potentialClasses, originalClass.getClassPool(false, true, true, holyBoss.isFemale(), false, true, false, fe4Char.requiresMelee(), mustUseItem, null));
				}
			}
			
			potentialClasses.removeAll(Arrays.asList(fe4Char.blacklistedClasses()));
			
			if (potentialClasses.isEmpty()) {
				// Still randomize their blood if necessary.
				if (options.bossBloodOption != BloodOptions.NO_CHANGE) { randomizeHolyBossBlood(options, holyBoss, originalClass, charData, predeterminedClasses, predeterminedBloodMap, itemMap, rng); }
				BloodArrays blood = new BloodArrays(holyBoss);
				for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
					predeterminedClasses.put(linked, originalClass);
					predeterminedBlood.put(linked, blood);
				}
				continue;
			}
			
			List<FE4Data.CharacterClass> classList = new ArrayList<FE4Data.CharacterClass>(potentialClasses);
			Collections.sort(classList, FE4Data.CharacterClass.defaultComparator);
			Set<HolyBlood> bloodOptions = new HashSet<HolyBlood>(Arrays.asList(fe4Char.limitedHolyBloodSelection()));
			
			if (options.bossBloodOption == BloodOptions.SHUFFLE) {
				if (hasMajorBlood) {
					assert bloodOptions.contains(predeterminedBloodMap.get(majorBloodType)) : "Attempted to assign blacklisted holy blood to character.";
					if (bloodOptions.contains(predeterminedBloodMap.get(majorBloodType))) {
						bloodOptions = new HashSet<HolyBlood>(Arrays.asList(predeterminedBloodMap.get(majorBloodType)));
					}
					
				} else if (hasMinorBlood) {
					if (bloodOptions.contains(predeterminedBloodMap.get(minorBloodType))) {
						bloodOptions = new HashSet<HolyBlood>(Arrays.asList(predeterminedBloodMap.get(minorBloodType)));
					}
				}
			}
			
			targetClass = classList.get(rng.nextInt(classList.size()));
			
			final Set<FE4Data.HolyBlood> finalBlood = bloodOptions;
			List<FE4Data.CharacterClass> fittingClasses = classList.stream().filter(charClass -> {
				Set<HolyBlood> supportedBlood = new HashSet<HolyBlood>(Arrays.asList(charClass.supportedHolyBlood()));
				supportedBlood.retainAll(finalBlood);
				return !supportedBlood.isEmpty();
			}).collect(Collectors.toList());
			
			if (!fittingClasses.isEmpty()) {
				targetClass = fittingClasses.get(rng.nextInt(fittingClasses.size()));
			}
			
			
			if (options.bossBloodOption != BloodOptions.NO_CHANGE) { randomizeHolyBossBlood(options, holyBoss, targetClass, charData, predeterminedClasses, predeterminedBloodMap, itemMap, rng); }
			BloodArrays blood = new BloodArrays(holyBoss);
			setHolyBossToClass(options, holyBoss, targetClass, blood.slot1Blood, blood.slot2Blood, blood.slot3Blood, charData, predeterminedClasses, itemMap, rng);
			
			for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
				predeterminedClasses.put(linked, targetClass);
				predeterminedBlood.put(linked, blood);
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
			
			Set<FE4Data.CharacterClass> possibleClasses = new HashSet<FE4Data.CharacterClass>(Arrays.asList(currentClass.getClassPool(false, true, true, combatant.isFemale(), false, true, false, requiresMelee, null, null)));
			if (requiresMelee) {
				possibleClasses.removeAll(FE4Data.CharacterClass.rangedOnlyClasses);
			} else if (requiresRange) {
				possibleClasses.retainAll(FE4Data.CharacterClass.rangedOnlyClasses);
			}
			
			// Don't allow any earlier chapters or earlier waves to use advanced classes.
			if (arenaCharacter.arenaChapter() % 6 < 4 || arenaCharacter.arenaLevel() < 6) {
				possibleClasses.removeAll(FE4Data.CharacterClass.advancedClasses);
			}
			
			List<FE4Data.CharacterClass> classList = new ArrayList<FE4Data.CharacterClass>(possibleClasses);
			Collections.sort(classList, FE4Data.CharacterClass.defaultComparator);
			FE4Data.CharacterClass targetClass = classList.get(rng.nextInt(classList.size()));
			FE4Data.Item item1 = null;
			FE4Data.Item item2 = null;
			
			boolean hasMelee = false;
			boolean hasRange = false;
			
			boolean allowInterestingWeapons = arenaCharacter.arenaLevel() > 2;
			boolean allowPowerfulWeapons = arenaCharacter.arenaLevel() > 5;
			
			Set<FE4Data.Item> nonBasicWeapons = new HashSet<FE4Data.Item>();
			nonBasicWeapons.addAll(FE4Data.Item.interestingWeapons);
			nonBasicWeapons.addAll(FE4Data.Item.powerfulWeapons);
			
			if (requiresMelee) {
				Set<FE4Data.Item> possibleWeapons = new HashSet<FE4Data.Item>(Arrays.asList(targetClass.usableItems(null, null, null)));
				possibleWeapons.removeAll(FE4Data.Item.staves);
				possibleWeapons.retainAll(FE4Data.Item.meleeWeapons);
				Set<FE4Data.Item> filteredWeapons = new HashSet<FE4Data.Item>(possibleWeapons); 
				if (allowPowerfulWeapons) {
					filteredWeapons.retainAll(nonBasicWeapons);
				} else if (allowInterestingWeapons) {
					filteredWeapons.retainAll(FE4Data.Item.interestingWeapons);
				} else {
					filteredWeapons.removeAll(nonBasicWeapons);
				}
				
				filteredWeapons.removeAll(FE4Data.Item.statusSet);
				if (combatant.isFemale() == false) { filteredWeapons.removeAll(FE4Data.Item.femaleOnlyWeapons); }
				
				if (filteredWeapons.isEmpty()) {
					filteredWeapons = possibleWeapons;
				}
				List<FE4Data.Item> weaponList = new ArrayList<FE4Data.Item>(filteredWeapons);
				Collections.sort(weaponList, FE4Data.Item.defaultComparator);
				item1 = weaponList.get(rng.nextInt(weaponList.size()));
				hasMelee = true;
				hasRange = FE4Data.Item.rangedWeapons.contains(item1);
			} else { // Requires range.
				Set<FE4Data.Item> possibleWeapons = new HashSet<FE4Data.Item>(Arrays.asList(targetClass.usableItems(null, null, null)));
				possibleWeapons.removeAll(FE4Data.Item.staves);
				possibleWeapons.retainAll(FE4Data.Item.rangedWeapons);
				possibleWeapons.removeAll(FE4Data.Item.meleeWeapons);
				Set<FE4Data.Item> filteredWeapons = new HashSet<FE4Data.Item>(possibleWeapons); 
				if (allowPowerfulWeapons) {
					filteredWeapons.retainAll(nonBasicWeapons);
				} else if (allowInterestingWeapons) {
					filteredWeapons.retainAll(FE4Data.Item.interestingWeapons);
				} else {
					filteredWeapons.removeAll(nonBasicWeapons);
				}
				
				filteredWeapons.removeAll(FE4Data.Item.statusSet);
				if (combatant.isFemale() == false) { filteredWeapons.removeAll(FE4Data.Item.femaleOnlyWeapons); }
				
				if (filteredWeapons.isEmpty()) {
					filteredWeapons = possibleWeapons;
				}
				List<FE4Data.Item> weaponList = new ArrayList<FE4Data.Item>(filteredWeapons);
				Collections.sort(weaponList, FE4Data.Item.defaultComparator);
				item1 = weaponList.get(rng.nextInt(weaponList.size()));
				hasRange = true;
				hasMelee = FE4Data.Item.meleeWeapons.contains(item1);
			}
			
			if (requiresMelee && !hasMelee) {
				Set<FE4Data.Item> possibleWeapons = new HashSet<FE4Data.Item>(Arrays.asList(targetClass.usableItems(null, null, null)));
				possibleWeapons.removeAll(FE4Data.Item.staves);
				possibleWeapons.retainAll(FE4Data.Item.meleeWeapons);
				possibleWeapons.removeAll(FE4Data.Item.rangedWeapons);
				Set<FE4Data.Item> filteredWeapons = new HashSet<FE4Data.Item>(possibleWeapons); 
				if (allowPowerfulWeapons) {
					filteredWeapons.retainAll(nonBasicWeapons);
				} else if (allowInterestingWeapons) {
					filteredWeapons.retainAll(FE4Data.Item.interestingWeapons);
				} else {
					filteredWeapons.removeAll(nonBasicWeapons);
				}
				
				filteredWeapons.removeAll(FE4Data.Item.statusSet);
				if (combatant.isFemale() == false) { filteredWeapons.removeAll(FE4Data.Item.femaleOnlyWeapons); }
				
				if (filteredWeapons.isEmpty()) {
					filteredWeapons = possibleWeapons;
				}
				List<FE4Data.Item> weaponList = new ArrayList<FE4Data.Item>(filteredWeapons);
				Collections.sort(weaponList, FE4Data.Item.defaultComparator);
				item2 = weaponList.get(rng.nextInt(weaponList.size()));
				hasMelee = true;
			}
			else if (requiresRange && !hasRange) {
				Set<FE4Data.Item> possibleWeapons = new HashSet<FE4Data.Item>(Arrays.asList(targetClass.usableItems(null, null, null)));
				possibleWeapons.removeAll(FE4Data.Item.staves);
				possibleWeapons.retainAll(FE4Data.Item.rangedWeapons);
				Set<FE4Data.Item> filteredWeapons = new HashSet<FE4Data.Item>(possibleWeapons); 
				if (allowPowerfulWeapons) {
					filteredWeapons.retainAll(nonBasicWeapons);
				} else if (allowInterestingWeapons) {
					filteredWeapons.retainAll(FE4Data.Item.interestingWeapons);
				} else {
					filteredWeapons.removeAll(nonBasicWeapons);
				}
				
				filteredWeapons.removeAll(FE4Data.Item.statusSet);
				if (combatant.isFemale() == false) { filteredWeapons.removeAll(FE4Data.Item.femaleOnlyWeapons); }
				
				if (filteredWeapons.isEmpty()) {
					filteredWeapons = possibleWeapons;
				}
				List<FE4Data.Item> weaponList = new ArrayList<FE4Data.Item>(filteredWeapons);
				Collections.sort(weaponList, FE4Data.Item.defaultComparator);
				item2 = weaponList.get(rng.nextInt(weaponList.size()));
				hasRange = true;
			} else {
				// Maybe a ring for item2?
				boolean hasRing = rng.nextInt(3) < 2;
				if (hasRing) {
					List<FE4Data.Item> rings = new ArrayList<FE4Data.Item>(FE4Data.Item.statRings);
					Collections.sort(rings, FE4Data.Item.defaultComparator);
					FE4Data.Item ring = rings.get(rng.nextInt(rings.size()));
					if (!targetClass.primaryAttackIsMagic() && ring == FE4Data.Item.MAGIC_RING) {
						ring = FE4Data.Item.POWER_RING;
					} else if (!targetClass.primaryAttackIsStrength() && ring == FE4Data.Item.POWER_RING) {
						ring = FE4Data.Item.MAGIC_RING;
					}
					
					item2 = ring;
				}
			}
			
			setEnemyCharacterToClass(options, combatant, targetClass, item1, item2, null, rng);
		}
	}
	
	private static void randomizeEnemies(FE4ClassOptions options, List<FE4EnemyCharacter> enemies, CharacterDataLoader charData, ItemMapper itemMap, Random rng) {
		Map<FE4Data.Character, FE4Data.CharacterClass> predeterminedClasses = new HashMap<FE4Data.Character, FE4Data.CharacterClass>();

		Map<FE4Data.Character, FE4Data.Item> weaponsBeatingCharacter = new HashMap<FE4Data.Character, FE4Data.Item>();
		
		for (FE4EnemyCharacter enemy : enemies) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(enemy.getCharacterID());
			
			if (FE4Data.Character.DoNotTouchEnemies.contains(fe4Char)) { continue; } // Some enemies have scripted events on the map (Ch. 5 ending scene comes to mind). We shouldn't touch those.
			
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
					setEnemyCharacterToClass(options, enemy, predeterminedClasses.get(fe4Char), mustUseItem, itemMap, rng);
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
			if (fe4Char.mustBeatCharacter().length > 0 || fe4Char.mustLoseToCharacters().length > 0) {
				// Just skip these. There's no guarantee that the battle will play out as expected.
				continue;
			}
			
			FE4Data.CharacterClass currentClass = FE4Data.CharacterClass.valueOf(enemy.getClassID());
			if (currentClass == null) { continue; }
			
			Set<FE4Data.CharacterClass> classPool = new HashSet<FE4Data.CharacterClass>();
			
			// Try to retain siege tome users.
			if (mustUseItem == null) {
				FE4Data.Item item1 = FE4Data.Item.valueOf(enemy.getEquipment1());
				FE4Data.Item item2 = FE4Data.Item.valueOf(enemy.getEquipment2());
				if (item1 != null && item1.isSiegeTome()) { mustUseItem = item1; }
				if (item2 != null && item2.isSiegeTome()) { mustUseItem = item2; }
			}
			
			// Check for weakly linked classes. Some enemies drop equipment that came from gen 1 and need to match.
			FE4Data.CharacterClass referenceClass = null;
			if (FE4Data.WeaklyLinkedCharacters.containsKey(fe4Char)) {
				FE4Data.Character referenceCharacter = FE4Data.WeaklyLinkedCharacters.get(fe4Char);
				if (referenceCharacter.isPlayable() && referenceCharacter.isGen1()) {
					// Playable characters are already set at this point.
					// So we can just refer to them directly.
					FE4StaticCharacter playableCharacter = charData.getStaticCharacter(referenceCharacter);
					referenceClass = FE4Data.CharacterClass.valueOf(playableCharacter.getClassID());
				} else if (referenceCharacter.isBoss()) {
					// Holy bosses are set after minion bosses, so the minion boss gets to decide the class.
				} else {
					// I don't think any minions drop weapons for other minions.
				}
				
				if (referenceClass != null) {
					if (referenceClass.isPromoted() && !currentClass.isPromoted()) {
						List<FE4Data.CharacterClass> demotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(referenceClass.demotedClasses(enemy.isFemale())));
						Collections.sort(demotedClasses, FE4Data.CharacterClass.defaultComparator);
						if (!demotedClasses.isEmpty()) {
							referenceClass = demotedClasses.get(rng.nextInt(demotedClasses.size()));
						}
					} else if (!referenceClass.isPromoted() && currentClass.isPromoted()) {
						List<FE4Data.CharacterClass> promotedClasses = new ArrayList<FE4Data.CharacterClass>(Arrays.asList(referenceClass.promotionClasses(enemy.isFemale())));
						Collections.sort(promotedClasses, FE4Data.CharacterClass.defaultComparator);
						if (!promotedClasses.isEmpty()) {
							referenceClass = promotedClasses.get(rng.nextInt(promotedClasses.size()));
						}
					}	
				}
			}
			
			Collections.addAll(classPool, currentClass.getClassPool(false, true, true, enemy.isFemale(), false, true, false, fe4Char.requiresMelee(), mustUseItem, null));
			
			classPool.removeAll(FE4Data.CharacterClass.advancedClasses);
			if (fe4Char.minionChapter() % 6 < 2) {
				classPool.remove(FE4Data.CharacterClass.DARK_MAGE);
				classPool.remove(FE4Data.CharacterClass.DARK_BISHOP);
			}
			
			classPool.removeAll(Arrays.asList(fe4Char.blacklistedClasses()));
			
			if (classPool.isEmpty()) {
				continue;
			}
			
			List<FE4Data.CharacterClass> classList = new ArrayList<FE4Data.CharacterClass>(classPool);
			Collections.sort(classList, FE4Data.CharacterClass.defaultComparator);
			FE4Data.CharacterClass targetClass = classList.get(rng.nextInt(classPool.size()));
			// Put reduced weight on fliers, since they're maybe a bit too powerful. Re-randomize once if they get fliers the first time
			if (targetClass.isFlier()) { 
				targetClass = classList.get(rng.nextInt(classPool.size()));
			}
			
			if (mustUseItem != null) {
				setEnemyCharacterToClass(options, enemy, targetClass, mustUseItem, itemMap, rng);
			} else {
				setEnemyCharacterToClass(options, enemy, fe4Char, targetClass, itemMap, rng);
			}
			
			for (FE4Data.Character linked : fe4Char.linkedCharacters()) {
				predeterminedClasses.put(linked, targetClass);
			}
		}
	}
	
	private static void randomizeHolyBossBlood(FE4ClassOptions options, FE4StaticCharacter holyBoss, FE4Data.CharacterClass targetClass, CharacterDataLoader charData, 
			Map<FE4Data.Character, FE4Data.CharacterClass> predeterminedClasses, Map<FE4Data.HolyBlood, FE4Data.HolyBlood> predeterminedBloodMap, ItemMapper itemMap, Random rng) {
		
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
		
		FE4Data.Character fe4Char = FE4Data.Character.valueOf(holyBoss.getCharacterID());
		
		// Anybody with major blood is keeping major blood, but that blood might have to be adjusted.
		if (hasMajorBlood && majorBloodType != null) {
			slot1Blood.removeIf(blood -> (blood.isMajor()));
			slot2Blood.removeIf(blood -> (blood.isMajor()));
			slot3Blood.removeIf(blood -> (blood.isMajor()));
			
			Set<FE4Data.HolyBlood> bloodSet = new HashSet<FE4Data.HolyBlood>(Arrays.asList(targetClass.supportedHolyBlood()));
			bloodSet.retainAll(new HashSet<FE4Data.HolyBlood>(Arrays.asList(fe4Char.limitedHolyBloodSelection())));
			List<FE4Data.HolyBlood> bloodOptions = bloodSet.stream().sorted(FE4Data.HolyBlood.defaultComparator).collect(Collectors.toList());
			// Bosses should never get Bragi Blood. That's a staff as a weapon if that's the case.
			bloodOptions.remove(FE4Data.HolyBlood.BRAGI);
			bloodOptions.remove(FE4Data.HolyBlood.NAGA); // Probably shouldn't get Naga either, in case they end up with the Naga tome (and wipe the floor with everybody).
			if (!bloodOptions.isEmpty()) {
				FE4Data.HolyBlood newMajorBlood = bloodOptions.get(rng.nextInt(bloodOptions.size()));
				if (options.bossBloodOption == BloodOptions.SHUFFLE && predeterminedBloodMap != null) {
					newMajorBlood = predeterminedBloodMap.get(majorBloodType);
				}
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
		List<FE4Data.HolyBlood> existingMinorBlood = new ArrayList<FE4Data.HolyBlood>();
		existingMinorBlood.addAll(slot1Blood.stream().filter(blood -> (blood.isMajor() == false)).map(blood -> (blood.bloodType())).collect(Collectors.toList()));
		existingMinorBlood.addAll(slot2Blood.stream().filter(blood -> (blood.isMajor() == false)).map(blood -> (blood.bloodType())).collect(Collectors.toList()));
		existingMinorBlood.addAll(slot3Blood.stream().filter(blood -> (blood.isMajor() == false)).map(blood -> (blood.bloodType())).collect(Collectors.toList()));
		
		slot1Blood.removeIf(blood -> (blood.isMajor() == false));
		slot2Blood.removeIf(blood -> (blood.isMajor() == false));
		slot3Blood.removeIf(blood -> (blood.isMajor() == false));
		
		if (options.bossBloodOption == BloodOptions.SHUFFLE && predeterminedBloodMap != null) {
			List<FE4Data.HolyBlood> replacementBlood = existingMinorBlood.stream().map(blood -> (predeterminedBloodMap.get(blood))).collect(Collectors.toList());
			for (FE4Data.HolyBlood blood : replacementBlood) {
				FE4Data.HolyBloodSlot1 slot1 = FE4Data.HolyBloodSlot1.blood(blood, false);
				FE4Data.HolyBloodSlot2 slot2 = FE4Data.HolyBloodSlot2.blood(blood, false);
				FE4Data.HolyBloodSlot3 slot3 = FE4Data.HolyBloodSlot3.blood(blood, false);
				
				if (slot1 != null) { slot1Blood.add(slot1); }
				if (slot2 != null) { slot2Blood.add(slot2); }
				if (slot3 != null) { slot3Blood.add(slot3); }
			}
		} else if (options.bossBloodOption == BloodOptions.RANDOMIZE) {
			List<HolyBlood> bloodOptions = new ArrayList<HolyBlood>(Arrays.asList(targetClass.supportedHolyBlood()));
			Collections.sort(bloodOptions, FE4Data.HolyBlood.defaultComparator);
			if (majorBloodType != null) {
				bloodOptions.remove(majorBloodType);
			}
			// Once again, no Bragi or Naga.
			bloodOptions.remove(FE4Data.HolyBlood.BRAGI);
			bloodOptions.remove(FE4Data.HolyBlood.NAGA);
			for (int i = 0; i < existingMinorBlood.size(); i++) {
				FE4Data.HolyBlood blood = bloodOptions.get(rng.nextInt(bloodOptions.size()));
				
				if (blood == null) { break; }
				
				FE4Data.HolyBloodSlot1 slot1 = FE4Data.HolyBloodSlot1.blood(blood, false);
				FE4Data.HolyBloodSlot2 slot2 = FE4Data.HolyBloodSlot2.blood(blood, false);
				FE4Data.HolyBloodSlot3 slot3 = FE4Data.HolyBloodSlot3.blood(blood, false);
				
				if (slot1 != null) { slot1Blood.add(slot1); }
				if (slot2 != null) { slot2Blood.add(slot2); }
				if (slot3 != null) { slot3Blood.add(slot3); }
				
				bloodOptions.remove(blood);
			}
		}
		
		holyBoss.setHolyBlood1Value(FE4Data.HolyBloodSlot1.valueForSlot1HolyBlood(slot1Blood));
		holyBoss.setHolyBlood2Value(FE4Data.HolyBloodSlot2.valueForSlot2HolyBlood(slot2Blood));
		holyBoss.setHolyBlood3Value(FE4Data.HolyBloodSlot3.valueForSlot3HolyBlood(slot3Blood));
	}
	
	private static void setHolyBossToClass(FE4ClassOptions options, FE4StaticCharacter holyBoss, FE4Data.CharacterClass targetClass, List<FE4Data.HolyBloodSlot1> slot1Blood, List<FE4Data.HolyBloodSlot2> slot2Blood, List<FE4Data.HolyBloodSlot3> slot3Blood, CharacterDataLoader charData, Map<FE4Data.Character, FE4Data.CharacterClass> predeterminedClasses, ItemMapper itemMap, Random rng) {
		
		FE4Data.Character fe4Char = FE4Data.Character.valueOf(holyBoss.getCharacterID());
		
		holyBoss.setClassID(targetClass.ID);
		
		if (slot1Blood == null) { slot1Blood = new ArrayList<FE4Data.HolyBloodSlot1>(); }
		if (slot2Blood == null) { slot2Blood = new ArrayList<FE4Data.HolyBloodSlot2>(); }
		if (slot3Blood == null) { slot3Blood = new ArrayList<FE4Data.HolyBloodSlot3>(); }
		
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
		List<FE4Data.Item> usableItems = new ArrayList<Item>(Arrays.asList(targetClass.usableItems(slot1Blood, slot2Blood, slot3Blood, true)));
		// Bosses shouldn't be using anything low ranked, if they can help it.
		Collections.sort(usableItems, new Comparator<FE4Data.Item>() {
			@Override
			public int compare(Item arg0, Item arg1) {
				if (arg0.getRank() == arg1.getRank()) { return 0; }
				return arg0.getRank().isHigher(arg1.getRank()) ? -1 : 1;
			}
		});
		usableItems.removeIf(item -> (item.getRank() == FE4Data.Item.WeaponRank.PRF));
		usableItems.removeIf(item -> (FE4Data.Item.playerOnlySet.contains(item)));
		if (!holyBoss.isFemale()) {
			usableItems.removeIf(item -> (FE4Data.Item.femaleOnlyWeapons.contains(item)));
		}
		
		List<FE4Data.Item> stafflessList = new ArrayList<Item>(usableItems);
		stafflessList.removeIf(item -> (item.getType() == FE4Data.Item.ItemType.STAFF));
		
		int equip1 = holyBoss.getEquipment1();
		FE4Data.Item item1 = FE4Data.Item.valueOf(equip1);
		if (item1 != FE4Data.Item.NONE && targetClass.canUseWeapon(item1, slot1Blood, slot2Blood, slot3Blood) == false) {
			boolean isHolyWeapon = item1.getRank() == FE4Data.Item.WeaponRank.PRF;
			boolean isBroken = item1.isBroken();
			FE4Data.Item replacement = null;
			if (isHolyWeapon && majorBloodType != null) {
				replacement = majorBloodType.holyWeapon;
			} else if (!stafflessList.isEmpty()) {
				replacement = stafflessList.get(rng.nextInt(stafflessList.size()));
			} else {
				replacement = usableItems.get(rng.nextInt(Math.max(1, usableItems.size() / 2)));
			}
			
			if (isBroken) {
				replacement = FE4Data.Item.getBrokenWeapon(replacement.getType(), replacement.getRank());
			}
			holyBoss.setEquipment1(replacement.ID);
			if (usableItems.size() > 1) { usableItems.remove(replacement); }
			stafflessList.remove(replacement);
			
			item1 = replacement;
		}
		
		int equip2 = holyBoss.getEquipment2();
		FE4Data.Item item2 = FE4Data.Item.valueOf(equip2);
		if (item2 != FE4Data.Item.NONE && (targetClass.canUseWeapon(item2, slot1Blood, slot2Blood, slot3Blood) == false || (item1 != null && item2.ID == equip1))) {
			boolean isHolyWeapon = item2.getRank() == FE4Data.Item.WeaponRank.PRF;
			boolean isBroken = item2.isBroken();
			FE4Data.Item replacement = null;
			if (isHolyWeapon) {
				replacement = majorBloodType.holyWeapon;
			} else {
				replacement = usableItems.get(rng.nextInt(Math.max(1, usableItems.size() / 2)));
			}

			if (isBroken) {
				replacement = FE4Data.Item.getBrokenWeapon(replacement.getType(), replacement.getRank());
			}
			holyBoss.setEquipment2(replacement.ID);
			if (usableItems.size() > 1) { usableItems.remove(replacement); }
			
			item2 = replacement;
		}
		
		// Process drops if necessary.
		int equip3 = holyBoss.getEquipment3();
		if (equip3 != FE4Data.Item.NONE.ID && itemMap != null) {
			FE4Data.Item droppedItem = itemMap.getItemAtIndex(equip3);
			// If their drop is the same as one of their items, it will collapse into the same item.
			// We should make sure this stays consistent if we change their equipment.
			if (droppedItem.ID == equip1) { itemMap.setItemAtIndex(equip3, item1); droppedItem = item1; }
			else if (droppedItem.ID == equip2) { itemMap.setItemAtIndex(equip3, item2); droppedItem = item2; }
			
			// If they drop something and are in Gen 1, we should make sure they can drop something they can use.
			if (fe4Char.isGen1()) {
				if (!targetClass.canUseWeapon(droppedItem, slot1Blood, slot2Blood, slot3Blood)) {
					FE4Data.Item replacement = usableItems.get(rng.nextInt(Math.max(1, usableItems.size() / 2)));
					itemMap.setItemAtIndex(equip3, replacement);
				}
			}
		}
	}
	
	private static void giveStaffIfNecessary(FE4ClassOptions options, FE4StaticCharacter character, CharacterDataLoader charData, ItemMapper itemMap, Random rng) {
		FE4Data.CharacterClass charClass = FE4Data.CharacterClass.valueOf(character.getClassID());
		FE4Data.Character fe4Char = FE4Data.Character.valueOf(character.getCharacterID());
		if (charClass.isHealer() || (fe4Char.isHealer() && options.retainHealers)) {
			int equip1 = character.getEquipment1();
			int equip2 = character.getEquipment2();
			int equip3 = character.getEquipment3();
			
			FE4Data.Item item1 = itemMap.getItemAtIndex(equip1);
			FE4Data.Item item2 = itemMap.getItemAtIndex(equip2);
			FE4Data.Item item3 = itemMap.getItemAtIndex(equip3);
			
			if ((item1 != null && FE4Data.Item.healingStaves.contains(item1)) ||
					(item2 != null && FE4Data.Item.healingStaves.contains(item2)) ||
					(item3 != null && FE4Data.Item.healingStaves.contains(item3))) {
				return; // already has a healing staff.
			}
			
			List<FE4Data.HolyBloodSlot1> slot1Blood = FE4Data.HolyBloodSlot1.slot1HolyBlood(character.getHolyBlood1Value());
			List<FE4Data.HolyBloodSlot2> slot2Blood = FE4Data.HolyBloodSlot2.slot2HolyBlood(character.getHolyBlood2Value());
			List<FE4Data.HolyBloodSlot3> slot3Blood = FE4Data.HolyBloodSlot3.slot3HolyBlood(character.getHolyBlood3Value());
			
			Set<FE4Data.Item> usable = new HashSet<FE4Data.Item>(Arrays.asList(charClass.usableItems(slot1Blood, slot2Blood, slot3Blood, false)));
			if (usable.isEmpty()) { return; }
			List<FE4Data.Item> list = usable.stream().filter(item -> (FE4Data.Item.healingStaves.contains(item))).sorted(FE4Data.Item.defaultComparator).collect(Collectors.toList());
			if (list.isEmpty()) { list = Arrays.asList(FE4Data.Item.HEAL); }
			
			FE4Data.Item randomStaff = list.get(rng.nextInt(list.size()));
			Integer inventoryID = itemMap.obtainFreeInventoryID(randomStaff);
			if (inventoryID == null) { return; }
			
			if (item1 == null) { equip1 = inventoryID; }
			else if (item2 == null) { equip2 = inventoryID; }
			else if (item3 == null) { equip3 = inventoryID; }
			
			character.setEquipment1(equip1);
			character.setEquipment2(equip2);
			character.setEquipment3(equip3);
		}
	}
	
	private static void setStaticCharacterToClass(FE4ClassOptions options, FE4StaticCharacter character, FE4Data.CharacterClass targetClass, boolean prioritizeHealingStavesForHealers, CharacterDataLoader charData, HolyBloodLoader bloodData, ItemMapper itemMap, 
			Map<FE4Data.Character, FE4Data.CharacterClass> predeterminedClasses, Map<FE4Data.HolyBlood, FE4Data.HolyBlood> predeterminedBloodMap, Map<FE4Data.Character, FE4Data.Item> requiredItems, Random rng) {
		setStaticCharacterToClass(options, character, targetClass, prioritizeHealingStavesForHealers, charData, bloodData, itemMap, predeterminedClasses, predeterminedBloodMap, requiredItems, null, rng);
	}
	
	private static void setStaticCharacterToClass(FE4ClassOptions options, FE4StaticCharacter character, FE4Data.CharacterClass targetClass, boolean prioritizeHealingStavesForHealers, CharacterDataLoader charData, HolyBloodLoader bloodData, ItemMapper itemMap, 
			Map<FE4Data.Character, FE4Data.CharacterClass> predeterminedClasses, Map<FE4Data.HolyBlood, FE4Data.HolyBlood> predeterminedBloodMap, Map<FE4Data.Character, FE4Data.Item> requiredItems, FE4Data.CharacterClass relatedClass, Random rng) {
		FE4Data.CharacterClass oldClass = FE4Data.CharacterClass.valueOf(character.getClassID());
		
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
		
		FE4Data.Character fe4Char = FE4Data.Character.valueOf(character.getCharacterID());
		
		if (options.playerBloodOption != BloodOptions.NO_CHANGE) {
			
			Set<FE4Data.HolyBlood> restrictedBlood = new HashSet<FE4Data.HolyBlood>(); 
			
			if (!FE4Data.Character.CharactersRequiringUniqueBlood.contains(fe4Char)) {
				for (FE4Data.Character uniqueChar : FE4Data.Character.CharactersRequiringUniqueBlood) {
					FE4StaticCharacter unique = charData.getStaticCharacter(uniqueChar);
					if (!predeterminedClasses.containsKey(uniqueChar)) { continue; } 
					if (unique == null) { continue; }
					
					List<HolyBloodSlot1> restrictedSlot1 = FE4Data.HolyBloodSlot1.slot1HolyBlood(unique.getHolyBlood1Value());
					List<HolyBloodSlot2> restrictedSlot2 = FE4Data.HolyBloodSlot2.slot2HolyBlood(unique.getHolyBlood2Value());
					List<HolyBloodSlot3> restrictedSlot3 = FE4Data.HolyBloodSlot3.slot3HolyBlood(unique.getHolyBlood3Value());
					
					if (restrictedSlot1.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) { restrictedBlood.add(restrictedSlot1.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType()); }
					if (restrictedSlot2.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) { restrictedBlood.add(restrictedSlot2.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType()); }
					if (restrictedSlot3.stream().filter(blood -> (blood.isMajor())).findFirst().isPresent()) { restrictedBlood.add(restrictedSlot3.stream().filter(blood -> (blood.isMajor())).findFirst().get().bloodType()); }
				}
			}
			
			// Anybody with major blood is keeping major blood, but that blood might have to be adjusted.
			if (hasMajorBlood && majorBloodType != null) {
				
				// Adjust growths (we add back the holy blood growths and remove them again once the blood is determined).
				character.setHPGrowth(character.getHPGrowth() + bloodData.holyBloodByType(majorBloodType).getHPGrowthBonus() * 2);
				character.setSTRGrowth(character.getSTRGrowth() + bloodData.holyBloodByType(majorBloodType).getSTRGrowthBonus() * 2);
				character.setMAGGrowth(character.getMAGGrowth() + bloodData.holyBloodByType(majorBloodType).getMAGGrowthBonus() * 2);
				character.setSKLGrowth(character.getSKLGrowth() + bloodData.holyBloodByType(majorBloodType).getSKLGrowthBonus() * 2);
				character.setSPDGrowth(character.getSPDGrowth() + bloodData.holyBloodByType(majorBloodType).getSPDGrowthBonus() * 2);
				character.setDEFGrowth(character.getDEFGrowth() + bloodData.holyBloodByType(majorBloodType).getDEFGrowthBonus() * 2);
				character.setRESGrowth(character.getRESGrowth() + bloodData.holyBloodByType(majorBloodType).getRESGrowthBonus() * 2);
				character.setLCKGrowth(character.getLCKGrowth() + bloodData.holyBloodByType(majorBloodType).getLCKGrowthBonus() * 2);
				
				FE4Data.Item holyWeaponToUpdate = majorBloodType.holyWeapon;
				
				slot1Blood.removeIf(blood -> (blood.isMajor()));
				slot2Blood.removeIf(blood -> (blood.isMajor()));
				slot3Blood.removeIf(blood -> (blood.isMajor()));
				
				Integer inventoryID = FE4Data.HolyWeaponInventoryIDs.get(holyWeaponToUpdate);
				
				// Julia is the only character allowed to change Naga. (i.e. not Deirdre)
				if (holyWeaponToUpdate == FE4Data.Item.NAGA && fe4Char != FE4Data.Character.JULIA) { inventoryID = null; }
				
				Set<FE4Data.HolyBlood> bloodOptions = new HashSet<FE4Data.HolyBlood>(Arrays.asList(targetClass.supportedHolyBlood()));
				Set<HolyBlood> limitedOptions = new HashSet<HolyBlood>(Arrays.asList(fe4Char.limitedHolyBloodSelection()));
				bloodOptions.retainAll(limitedOptions);
				if (restrictedBlood != null) {
					bloodOptions.removeAll(restrictedBlood);
				}
				
				List<FE4Data.HolyBlood> bloodList = new ArrayList<FE4Data.HolyBlood>(bloodOptions);
				Collections.sort(bloodList, FE4Data.HolyBlood.defaultComparator);
				//assert !bloodList.isEmpty() : "No valid holy blood available.";
				if (!bloodList.isEmpty()) {
					FE4Data.HolyBlood newMajorBlood = bloodList.get(rng.nextInt(bloodList.size()));
					if (options.playerBloodOption == BloodOptions.SHUFFLE && predeterminedBloodMap != null) {
						newMajorBlood = predeterminedBloodMap.get(majorBloodType);
					}
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
			List<HolyBlood> existingMinorBlood = new ArrayList<HolyBlood>();
			existingMinorBlood.addAll(slot1Blood.stream().filter(blood -> (blood.isMajor() == false)).map(blood -> (blood.bloodType())).collect(Collectors.toList()));
			existingMinorBlood.addAll(slot2Blood.stream().filter(blood -> (blood.isMajor() == false)).map(blood -> (blood.bloodType())).collect(Collectors.toList()));
			existingMinorBlood.addAll(slot3Blood.stream().filter(blood -> (blood.isMajor() == false)).map(blood -> (blood.bloodType())).collect(Collectors.toList()));
			
			int minorBloodCount = existingMinorBlood.size();
			
			slot1Blood.removeIf(blood -> (blood.isMajor() == false));
			slot2Blood.removeIf(blood -> (blood.isMajor() == false));
			slot3Blood.removeIf(blood -> (blood.isMajor() == false));
			
			// Adjust growths up temporarily, they'll be docked later.
			for (HolyBlood oldBlood : existingMinorBlood) {
				character.setHPGrowth(character.getHPGrowth() + bloodData.holyBloodByType(oldBlood).getHPGrowthBonus());
				character.setSTRGrowth(character.getSTRGrowth() + bloodData.holyBloodByType(oldBlood).getSTRGrowthBonus());
				character.setMAGGrowth(character.getMAGGrowth() + bloodData.holyBloodByType(oldBlood).getMAGGrowthBonus());
				character.setSKLGrowth(character.getSKLGrowth() + bloodData.holyBloodByType(oldBlood).getSKLGrowthBonus());
				character.setSPDGrowth(character.getSPDGrowth() + bloodData.holyBloodByType(oldBlood).getSPDGrowthBonus());
				character.setDEFGrowth(character.getDEFGrowth() + bloodData.holyBloodByType(oldBlood).getDEFGrowthBonus());
				character.setRESGrowth(character.getRESGrowth() + bloodData.holyBloodByType(oldBlood).getRESGrowthBonus());
				character.setLCKGrowth(character.getLCKGrowth() + bloodData.holyBloodByType(oldBlood).getLCKGrowthBonus());
			}
			List<HolyBlood> newMinorBlood = new ArrayList<HolyBlood>();
			
			if (options.playerBloodOption == BloodOptions.SHUFFLE && predeterminedBloodMap != null) {
				newMinorBlood = existingMinorBlood.stream().map(existingBlood -> {
					return predeterminedBloodMap.get(existingBlood);
				}).collect(Collectors.toList());
				
				for (FE4Data.HolyBlood blood : newMinorBlood) {
					FE4Data.HolyBloodSlot1 slot1 = FE4Data.HolyBloodSlot1.blood(blood, false);
					FE4Data.HolyBloodSlot2 slot2 = FE4Data.HolyBloodSlot2.blood(blood, false);
					FE4Data.HolyBloodSlot3 slot3 = FE4Data.HolyBloodSlot3.blood(blood, false);
					
					if (slot1 != null) { slot1Blood.add(slot1); }
					if (slot2 != null) { slot2Blood.add(slot2); }
					if (slot3 != null) { slot3Blood.add(slot3); }
				}
			} else {
				List<HolyBlood> bloodOptions = new ArrayList<HolyBlood>(Arrays.asList(targetClass.supportedHolyBlood()));
				Collections.sort(bloodOptions, FE4Data.HolyBlood.defaultComparator);
				if (majorBloodType != null) { bloodOptions.remove(majorBloodType); }
				if (bloodOptions.isEmpty()) { 
					// Some classes only have one choice for blood type, and it might have already been taken by the major blood.
					// In that case, use anything else.
					bloodOptions = new ArrayList<HolyBlood>(Arrays.asList(HolyBlood.values())); 
					bloodOptions.remove(HolyBlood.NONE); 
					if (majorBloodType != null) { bloodOptions.remove(majorBloodType); } 
				}
				
				for (int i = 0; i < minorBloodCount; i++) {
					HolyBlood blood = bloodOptions.get(rng.nextInt(bloodOptions.size()));
					
					if (blood == null) { break; }
					
					newMinorBlood.add(blood);
					
					FE4Data.HolyBloodSlot1 slot1 = FE4Data.HolyBloodSlot1.blood(blood, false);
					FE4Data.HolyBloodSlot2 slot2 = FE4Data.HolyBloodSlot2.blood(blood, false);
					FE4Data.HolyBloodSlot3 slot3 = FE4Data.HolyBloodSlot3.blood(blood, false);
					
					if (slot1 != null) { slot1Blood.add(slot1); }
					if (slot2 != null) { slot2Blood.add(slot2); }
					if (slot3 != null) { slot3Blood.add(slot3); }
					
					bloodOptions.remove(blood);
				}
			}
			
			// Swap STR/MAG if necessary before we start subtracting growths.
			boolean wasSTRBased = oldClass.primaryAttackIsStrength();
			boolean wasMAGBased = oldClass.primaryAttackIsMagic();
			
			boolean isSTRBased = targetClass.primaryAttackIsStrength();
			boolean isMAGBased = targetClass.primaryAttackIsMagic();
			
			if ((wasSTRBased && !wasMAGBased && isMAGBased && !isSTRBased) || (wasMAGBased && !wasSTRBased && isSTRBased && !isMAGBased)) {
				// Swap in the case that we've randomized across the STR/MAG split.
				int oldSTR = character.getSTRGrowth();
				character.setSTRGrowth(character.getMAGGrowth());
				character.setMAGGrowth(oldSTR);
				
				oldSTR = character.getBaseSTR();
				character.setBaseSTR(character.getBaseMAG());
				character.setBaseMAG(oldSTR);
			}
			
			// Adjust growths back down based on blood selected.
			if (majorBloodType != null) {
				character.setHPGrowth(character.getHPGrowth() - bloodData.holyBloodByType(majorBloodType).getHPGrowthBonus() * 2);
				character.setSTRGrowth(character.getSTRGrowth() - bloodData.holyBloodByType(majorBloodType).getSTRGrowthBonus() * 2);
				character.setMAGGrowth(character.getMAGGrowth() - bloodData.holyBloodByType(majorBloodType).getMAGGrowthBonus() * 2);
				character.setSKLGrowth(character.getSKLGrowth() - bloodData.holyBloodByType(majorBloodType).getSKLGrowthBonus() * 2);
				character.setSPDGrowth(character.getSPDGrowth() - bloodData.holyBloodByType(majorBloodType).getSPDGrowthBonus() * 2);
				character.setDEFGrowth(character.getDEFGrowth() - bloodData.holyBloodByType(majorBloodType).getDEFGrowthBonus() * 2);
				character.setRESGrowth(character.getRESGrowth() - bloodData.holyBloodByType(majorBloodType).getRESGrowthBonus() * 2);
				character.setLCKGrowth(character.getLCKGrowth() - bloodData.holyBloodByType(majorBloodType).getLCKGrowthBonus() * 2);
			}
			
			for (HolyBlood newBlood : newMinorBlood) {
				character.setHPGrowth(character.getHPGrowth() - bloodData.holyBloodByType(newBlood).getHPGrowthBonus());
				character.setSTRGrowth(character.getSTRGrowth() - bloodData.holyBloodByType(newBlood).getSTRGrowthBonus());
				character.setMAGGrowth(character.getMAGGrowth() - bloodData.holyBloodByType(newBlood).getMAGGrowthBonus());
				character.setSKLGrowth(character.getSKLGrowth() - bloodData.holyBloodByType(newBlood).getSKLGrowthBonus());
				character.setSPDGrowth(character.getSPDGrowth() - bloodData.holyBloodByType(newBlood).getSPDGrowthBonus());
				character.setDEFGrowth(character.getDEFGrowth() - bloodData.holyBloodByType(newBlood).getDEFGrowthBonus());
				character.setRESGrowth(character.getRESGrowth() - bloodData.holyBloodByType(newBlood).getRESGrowthBonus());
				character.setLCKGrowth(character.getLCKGrowth() - bloodData.holyBloodByType(newBlood).getLCKGrowthBonus());
			}
			
			character.setHolyBlood1Value(FE4Data.HolyBloodSlot1.valueForSlot1HolyBlood(slot1Blood));
			character.setHolyBlood2Value(FE4Data.HolyBloodSlot2.valueForSlot2HolyBlood(slot2Blood));
			character.setHolyBlood3Value(FE4Data.HolyBloodSlot3.valueForSlot3HolyBlood(slot3Blood));
		}
		
		// Verify equipment.
		
		// If this is Deirdre, substitute her aura (0x60) with chapter 8 steel lance (0x34) to prevent duplicate items in Gen 2.
		if (character.getCharacterID() == FE4Data.Character.DEIRDRE.ID) {
			character.setEquipment2(FE4Data.Chapter8ShopSteelLanceInventoryID);
			itemMap.setItemAtIndex(FE4Data.Chapter8ShopSteelLanceInventoryID, FE4Data.Item.AURA);
		}
		
		Set<FE4Data.Item> usableSet = new HashSet<Item>(Arrays.asList(targetClass.usableItems(slot1Blood, slot2Blood, slot3Blood)));
		
		if (relatedClass != null) {
			usableSet.retainAll(new HashSet<Item>(Arrays.asList(relatedClass.usableItems(null, null, null))));
		}
		
		usableSet.removeIf(item -> (item.getRank() == FE4Data.Item.WeaponRank.PRF));
		if (!character.isFemale()) {
			usableSet.removeIf(item -> (FE4Data.Item.femaleOnlyWeapons.contains(item)));
		}
		
		// Special case for Chulainn, since he's an arena combatant. Shouldn't have a status sword.
		if (character.getCharacterID() == FE4Data.Character.CHULAINN.ID) {
			usableSet.removeAll(FE4Data.Item.statusSet);
		}
		
		// Remove any player only weapons for characters that can start as enemies (i.e. Berserk Sword, Berserk Staff)
		if (FE4Data.Character.RecruitableEnemyCharacters.contains(fe4Char)) {
			usableSet.removeAll(FE4Data.Item.playerOnlySet);
		}
		
		List<FE4Data.Item> usableItems = usableSet.stream().sorted(FE4Data.Item.defaultComparator).collect(Collectors.toList());
		
		// Remove Hel, if it's in here.
		usableItems.remove(FE4Data.Item.HEL);
		
		boolean canAttack = usableItems.stream().anyMatch(item -> (item.isWeapon()));
		boolean hasWeapon = false;
		boolean isHealer = targetClass.isHealer();
		if (!isHealer) {
			isHealer = fe4Char.isHealer() && options.retainHealers;
		}
		// If we're not prioritizing healing staves, we don't need any special logic that relies on this being a healing class.
		if (!prioritizeHealingStavesForHealers) { isHealer = false; }
		
		Set<FE4Data.Item> healingStaves = new HashSet<Item>(FE4Data.Item.healingStaves);
		healingStaves.retainAll(usableItems);
		
		int equip1 = character.getEquipment1();
		FE4Data.Item item1 = itemMap.getItemAtIndex(equip1);
		if (item1 != null && targetClass.canUseWeapon(item1, slot1Blood, slot2Blood, slot3Blood) == false) {
			boolean isHolyWeapon = item1.getRank() == FE4Data.Item.WeaponRank.PRF;
			boolean isBroken = item1.isBroken();
			FE4Data.Item replacement = null;
			if (isHolyWeapon) {
				replacement = majorBloodType.holyWeapon;
			} else {
				if (isHealer && !healingStaves.isEmpty()) {
					List<FE4Data.Item> healStaffList = new ArrayList<FE4Data.Item>(healingStaves); 
					Collections.sort(healStaffList, FE4Data.Item.defaultComparator);
					replacement = healStaffList.get(rng.nextInt(healStaffList.size()));
				} else {
					if (options.itemOptions == ItemAssignmentOptions.SIDEGRADE_STRICT) {
						replacement = strictReplacementForItem(item1, fe4Char.joinChapter(), new HashSet<FE4Data.Item>(usableItems), rng);
					} else if (options.itemOptions != ItemAssignmentOptions.RANDOMIZE) {
						replacement = looseReplacementForItem(item1, fe4Char.joinChapter(), new HashSet<FE4Data.Item>(usableItems), rng);
					} else {
						replacement = usableItems.get(rng.nextInt(usableItems.size()));
					}
				}
			}
			
			if (isBroken) {
				replacement = FE4Data.Item.getBrokenWeapon(replacement.getType(), replacement.getRank());
			}
			
			itemMap.setItemAtIndex(equip1, replacement);
			usableItems.remove(replacement);
			if (usableItems.isEmpty()) { // In case there's only one choice for us, and we need more than one weapon, add it back it in if it's the last one.
				usableItems.add(replacement);
			}
			
			hasWeapon = replacement.isWeapon();
		} else if (item1 != null) {
			hasWeapon = item1.isWeapon();
		}
		
		int equip2 = character.getEquipment2();
		FE4Data.Item item2 = itemMap.getItemAtIndex(equip2);
		if (item2 != null && (targetClass.canUseWeapon(item2, slot1Blood, slot2Blood, slot3Blood) == false || (item1 != null && item2.ID == equip1))) {
			boolean isHolyWeapon = item2.getRank() == FE4Data.Item.WeaponRank.PRF;
			boolean isBroken = item2.isBroken();
			FE4Data.Item replacement = null;
			if (isHolyWeapon) {
				replacement = majorBloodType.holyWeapon;
			} else {
				if (isHealer && !healingStaves.isEmpty()) {
					List<FE4Data.Item> healStaffList = new ArrayList<FE4Data.Item>(healingStaves);
					Collections.sort(healStaffList, FE4Data.Item.defaultComparator);
					replacement = healStaffList.get(rng.nextInt(healStaffList.size()));
				} else {
					if (options.itemOptions == ItemAssignmentOptions.SIDEGRADE_STRICT) {
						replacement = strictReplacementForItem(item2, fe4Char.joinChapter(), new HashSet<FE4Data.Item>(usableItems), rng);
					} else if (options.itemOptions != ItemAssignmentOptions.RANDOMIZE) {
						replacement = looseReplacementForItem(item2, fe4Char.joinChapter(), new HashSet<FE4Data.Item>(usableItems), rng);
					} else {
						replacement = usableItems.get(rng.nextInt(usableItems.size()));
					}
				}
			}
			
			if (isBroken) {
				replacement = FE4Data.Item.getBrokenWeapon(replacement.getType(), replacement.getRank());
			}
			
			itemMap.setItemAtIndex(equip2, replacement);
			usableItems.remove(replacement);
			if (usableItems.isEmpty()) { // In case there's only one choice for us, and we need more than one weapon, add it back it in if it's the last one.
				usableItems.add(replacement);
			}
			if (!hasWeapon) { hasWeapon = replacement.isWeapon(); }
		} else if (!hasWeapon && item2 != null) {
			hasWeapon = item2.isWeapon(); 
		}
		
		int equip3 = character.getEquipment3();
		FE4Data.Item item3 = itemMap.getItemAtIndex(equip3);
		if (item3 != null && (targetClass.canUseWeapon(item3, slot1Blood, slot2Blood, slot3Blood) == false || (item2 != null && item3.ID == item2.ID) || (item1 != null && item3.ID == item1.ID))) {
			boolean isHolyWeapon = item3.getRank() == FE4Data.Item.WeaponRank.PRF;
			boolean isBroken = item3.isBroken();
			FE4Data.Item replacement = null;
			if (isHolyWeapon) {
				replacement = majorBloodType.holyWeapon;
			} else {
				if (options.itemOptions == ItemAssignmentOptions.SIDEGRADE_STRICT) {
					replacement = strictReplacementForItem(item3, fe4Char.joinChapter(), new HashSet<FE4Data.Item>(usableItems), rng);
				} else if (options.itemOptions != ItemAssignmentOptions.RANDOMIZE) {
					replacement = looseReplacementForItem(item3, fe4Char.joinChapter(), new HashSet<FE4Data.Item>(usableItems), rng);
				} else {
					replacement = usableItems.get(rng.nextInt(usableItems.size()));
				}
			}
			
			if (isBroken) {
				replacement = FE4Data.Item.getBrokenWeapon(replacement.getType(), replacement.getRank());
			}
			
			itemMap.setItemAtIndex(equip3, replacement);
			usableItems.remove(replacement);
			if (usableItems.isEmpty()) { // In case there's only one choice for us, and we need more than one weapon, add it back it in if it's the last one.
				usableItems.add(replacement);
			}
			if (!hasWeapon) { hasWeapon = replacement.isWeapon(); }
		} else if (!hasWeapon && item3 != null) {
			hasWeapon = item3.isWeapon();
		}
		
		if (canAttack && !hasWeapon && !isHealer) {
			item1 = itemMap.getItemAtIndex(equip1);
			if (item1 != null) {
				List<FE4Data.Item> usableWeapons = new ArrayList<Item>(usableItems);
				usableWeapons = usableWeapons.stream().filter(item -> (item.isWeapon())).collect(Collectors.toList());
				if (!usableWeapons.isEmpty()) {
					FE4Data.Item weapon = null;
					
					if (options.itemOptions == ItemAssignmentOptions.SIDEGRADE_STRICT) {
						weapon = strictReplacementForItem(item1, fe4Char.joinChapter(), new HashSet<FE4Data.Item>(usableWeapons), rng);
					} else if (options.itemOptions != ItemAssignmentOptions.RANDOMIZE) {
						weapon = looseReplacementForItem(item1, fe4Char.joinChapter(), new HashSet<FE4Data.Item>(usableWeapons), rng);
					} else {
						weapon = usableItems.get(rng.nextInt(usableItems.size()));
					}
					itemMap.setItemAtIndex(equip1, weapon);
					usableItems.remove(weapon);
					hasWeapon = true;
				}
			}
		}
		
		Set<Item> potentialGifts = new HashSet<Item>(Arrays.asList(targetClass.usableItems(slot1Blood, slot2Blood, slot3Blood)));
		Set<Item> rewardSet = new HashSet<FE4Data.Item>(FE4Data.Item.interestingWeapons);
		rewardSet.addAll(FE4Data.Item.powerfulWeapons);
		
		// Characters that already have major blood will receive their weapon naturally.
		// If somebody randomizes into major blood, we'll deal with that later.
		rewardSet.removeAll(FE4Data.Item.holyWeapons); 
		
		potentialGifts.retainAll(rewardSet);
		if (!character.isFemale()) {
			potentialGifts.removeAll(FE4Data.Item.femaleOnlyWeapons);
		}
		
		if (!potentialGifts.isEmpty()) {
			usableItems = new ArrayList<Item>(potentialGifts);
		} else {
			usableItems = new ArrayList<Item>(Arrays.asList(targetClass.usableItems(slot1Blood, slot2Blood, slot3Blood)));
		}
		
		
		// Fix conversation items if necessary.
		if (options.adjustConversationWeapons) {
			for (FE4Data.Character recipient : FE4Data.EventItemInventoryIDsByRecipient.keySet()) {
				if (recipient.ID == fe4Char.ID) {
					List<Integer> inventoryIDs = FE4Data.EventItemInventoryIDsByRecipient.get(recipient);
					for (int inventoryID : inventoryIDs) {
						FE4Data.Item item = itemMap.getItemAtIndex(inventoryID);
						if (targetClass.canUseWeapon(item, slot1Blood, slot2Blood, slot3Blood) == false) {
							FE4Data.Item replacement = usableItems.get(rng.nextInt(usableItems.size()));
							itemMap.setItemAtIndex(inventoryID, replacement);
							if (usableItems.size() > 1) {
								usableItems.remove(replacement);
							}
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
	}
	
	public static void setChildCharacterToClass(FE4ClassOptions options, FE4ChildCharacter child, FE4StaticCharacter parent, FE4Data.CharacterClass targetClass, ItemMapper itemMap, Random rng) {
		child.setClassID(targetClass.ID);
		
		FE4Data.Character fe4Char = FE4Data.Character.valueOf(child.getCharacterID());
		
		List<FE4Data.Item> usableItems = new ArrayList<Item>(Arrays.asList(targetClass.usableItems(new ArrayList<FE4Data.HolyBloodSlot1>(), new ArrayList<FE4Data.HolyBloodSlot2>(), new ArrayList<FE4Data.HolyBloodSlot3>())));
		Collections.sort(usableItems, FE4Data.Item.defaultComparator);
		if (!child.isFemale()) {
			usableItems.removeIf(item -> (FE4Data.Item.femaleOnlyWeapons.contains(item)));
		}
		
		boolean canAttack = usableItems.stream().anyMatch(item -> (item.isWeapon()));
		boolean hasWeapon = false;
		boolean isHealer = targetClass.isHealer() || (fe4Char.isHealer() && options.retainHealers);
		
		Set<FE4Data.Item> healingStaves = new HashSet<Item>(FE4Data.Item.healingStaves);
		healingStaves.retainAll(usableItems);
		
		List<FE4Data.HolyBloodSlot1> slot1Blood = parent != null ? FE4Data.HolyBloodSlot1.slot1HolyBlood(parent.getHolyBlood1Value()) : null;
		List<FE4Data.HolyBloodSlot2> slot2Blood = parent != null ? FE4Data.HolyBloodSlot2.slot2HolyBlood(parent.getHolyBlood2Value()) : null;
		List<FE4Data.HolyBloodSlot3> slot3Blood = parent != null ? FE4Data.HolyBloodSlot3.slot3HolyBlood(parent.getHolyBlood3Value()) : null;
		
		int equip1 = child.getEquipment1();
		FE4Data.Item item1 = itemMap.getItemAtIndex(equip1);
		if (item1 != null && targetClass.canUseWeapon(item1, slot1Blood, slot2Blood, slot3Blood) == false) {
			FE4Data.Item replacement = null;
			boolean isBroken = item1.isBroken();
			if (isHealer && !healingStaves.isEmpty()) {
				List<FE4Data.Item> healStaffList = new ArrayList<FE4Data.Item>(healingStaves); 
				replacement = healStaffList.get(rng.nextInt(healStaffList.size()));
			} else {
				if (options.itemOptions == ItemAssignmentOptions.SIDEGRADE_STRICT) {
					replacement = strictReplacementForItem(item1, fe4Char.joinChapter(), new HashSet<FE4Data.Item>(usableItems), rng);
				} else if (options.itemOptions != ItemAssignmentOptions.RANDOMIZE) {
					replacement = looseReplacementForItem(item1, fe4Char.joinChapter(), new HashSet<FE4Data.Item>(usableItems), rng);
				} else {
					replacement = usableItems.get(rng.nextInt(usableItems.size()));
				}
			}
			
			if (isBroken) {
				replacement = FE4Data.Item.getBrokenWeapon(replacement.getType(), replacement.getRank());
			}
			
			itemMap.setItemAtIndex(equip1, replacement);
			usableItems.remove(replacement);
			
			hasWeapon = replacement.isWeapon();
		} else if (item1 != null) {
			hasWeapon = item1.isWeapon();
		}
		
		int equip2 = child.getEquipment2();
		FE4Data.Item item2 = itemMap.getItemAtIndex(equip2);
		if (item2 != null && targetClass.canUseWeapon(item2, slot1Blood, slot2Blood, slot3Blood) == false) {
			FE4Data.Item replacement = null;
			boolean isBroken = item2.isBroken();
			if (options.itemOptions == ItemAssignmentOptions.SIDEGRADE_STRICT) {
				replacement = strictReplacementForItem(item2, fe4Char.joinChapter(), new HashSet<FE4Data.Item>(usableItems), rng);
			} else if (options.itemOptions != ItemAssignmentOptions.RANDOMIZE) {
				replacement = looseReplacementForItem(item2, fe4Char.joinChapter(), new HashSet<FE4Data.Item>(usableItems), rng);
			} else {
				replacement = usableItems.get(rng.nextInt(usableItems.size()));
			}
			
			if (isBroken) {
				replacement = FE4Data.Item.getBrokenWeapon(replacement.getType(), replacement.getRank());
			}
			itemMap.setItemAtIndex(equip2, replacement);
			usableItems.remove(replacement);
			if (!hasWeapon) { hasWeapon = replacement.isWeapon(); }
		} else if (!hasWeapon && item2 != null) {
			hasWeapon = item2.isWeapon(); 
		}
		
		if (canAttack && !hasWeapon && !isHealer) {
			item1 = itemMap.getItemAtIndex(equip1);
			if (item1 != null) {
				List<FE4Data.Item> usableWeapons = new ArrayList<Item>(usableItems);
				usableWeapons = usableWeapons.stream().filter(item -> (item.isWeapon())).collect(Collectors.toList());
				if (!usableWeapons.isEmpty()) {
					FE4Data.Item weapon = null;
					
					if (options.itemOptions == ItemAssignmentOptions.SIDEGRADE_STRICT) {
						weapon = strictReplacementForItem(item1, fe4Char.joinChapter(), new HashSet<FE4Data.Item>(usableWeapons), rng);
					} else if (options.itemOptions != ItemAssignmentOptions.RANDOMIZE) {
						weapon = looseReplacementForItem(item1, fe4Char.joinChapter(), new HashSet<FE4Data.Item>(usableWeapons), rng);
					} else {
						weapon = usableItems.get(rng.nextInt(usableItems.size()));
					}
					itemMap.setItemAtIndex(equip1, weapon);
					usableItems.remove(weapon);
					hasWeapon = true;
				}
			}
		}
		
		Set<Item> potentialGifts = new HashSet<Item>(Arrays.asList(targetClass.usableItems(slot1Blood, slot2Blood, slot3Blood)));
		Set<Item> rewardSet = new HashSet<FE4Data.Item>(FE4Data.Item.interestingWeapons);
		rewardSet.addAll(FE4Data.Item.powerfulWeapons);
		potentialGifts.retainAll(rewardSet);
		if (!child.isFemale()) {
			potentialGifts.removeAll(FE4Data.Item.femaleOnlyWeapons);
		}
		if (!potentialGifts.isEmpty()) {
			usableItems = new ArrayList<Item>(potentialGifts);
		} else {
			usableItems = new ArrayList<Item>(Arrays.asList(targetClass.usableItems(slot1Blood, slot2Blood, slot3Blood)));
		}
		
		// Fix conversation items if necessary.
		if (options.adjustConversationWeapons) {
			for (FE4Data.Character recipient : FE4Data.EventItemInventoryIDsByRecipient.keySet()) {
				if (recipient.ID == fe4Char.ID) {
					List<Integer> inventoryIDs = FE4Data.EventItemInventoryIDsByRecipient.get(recipient);
					for (int inventoryID : inventoryIDs) {
						FE4Data.Item item = itemMap.getItemAtIndex(inventoryID);
						if (targetClass.canUseWeapon(item, slot1Blood, slot2Blood, slot3Blood) == false) {
							FE4Data.Item replacement = usableItems.get(rng.nextInt(usableItems.size()));
							itemMap.setItemAtIndex(inventoryID, replacement);
							if (usableItems.size() > 1) {
								usableItems.remove(replacement);
							}
						}
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
		
		boolean isStationary = FE4Data.Character.CastleGuards.contains(enemyChar); // This isn't technically true, but for our purposes, castle guards are definitely stationary.
		boolean isCastleGuard = FE4Data.Character.CastleGuards.contains(enemyChar);
		
		Set<FE4Data.Item> itemSet = new HashSet<FE4Data.Item>(Arrays.asList(targetClass.usableItems(null, null, null, isStationary)));
		if (isCastleGuard) { itemSet.removeAll(FE4Data.Item.meleeStaves); }
		if (enemyChar.ID == FE4Data.Character.KUTUZOV_TURN_12.ID) {
			// Special Case: Kutuzov is a boss in Chapter 7 that spawns initially missing a weapon. After 12 turns, he finds his poweful weapon in his castle and re-emerges as a new character.
			// If this character is the version after turn 12, then we're going to give him only powerful weapons.
			itemSet.retainAll(FE4Data.Item.powerfulWeapons);
		} else {
			itemSet.removeAll(blacklistedItems);
		}
		itemSet.removeAll(FE4Data.Item.femaleOnlyWeapons);
		itemSet.removeIf(item -> (FE4Data.Item.playerOnlySet.contains(item)));
		List<FE4Data.Item> usableItems = new ArrayList<FE4Data.Item>(itemSet);
		if (enemyChar.isBoss()) {
			Collections.sort(usableItems, new Comparator<FE4Data.Item>() {
				@Override
				public int compare(Item o1, Item o2) {
					if (o1.getRank() == o2.getRank()) { return 0; }
					return o1.getRank().isHigher(o2.getRank()) ? -1 : 1;
				}
			});
		} else {
			Collections.sort(usableItems, FE4Data.Item.defaultComparator);
		}
		int item1ID = enemy.getEquipment1();
		FE4Data.Item item1 = FE4Data.Item.valueOf(item1ID);
		if (item1 != Item.NONE) {
			if (!targetClass.canUseWeapon(item1, null, null, null)) {
				List<FE4Data.Item> usableWeapons = new ArrayList<Item>(usableItems);
				usableWeapons = usableWeapons.stream().filter(item -> (item.isWeapon())).collect(Collectors.toList());
				if (!usableWeapons.isEmpty()) {
					FE4Data.Item weapon = usableWeapons.get(rng.nextInt(enemyChar.isBoss() ? Math.max(1, usableWeapons.size() / 2) : usableWeapons.size()));
					enemy.setEquipment1(weapon.ID);
					if (usableItems.size() > 1) { usableItems.remove(weapon); }
					
					item1 = weapon;
				}
			}
		}
		
		int item2ID = enemy.getEquipment2();
		FE4Data.Item item2 = FE4Data.Item.valueOf(item2ID);
		if (item2 != Item.NONE) {
			if (!targetClass.canUseWeapon(item2, null, null, null) || (item1 != null && item2.ID == item1ID)) {
				if (!usableItems.isEmpty()) {
					FE4Data.Item item = usableItems.get(rng.nextInt(enemyChar.isBoss() ? Math.max(1, usableItems.size() / 2) : usableItems.size()));
					enemy.setEquipment2(item.ID);
					item2 = item;
				} else {
					enemy.setEquipment2(Item.NONE.ID);
				}
			}
		}
		
		// Gen 1 characters can alter their drops to a weapon they can use.
		int droppedItemInventoryID = enemy.getDropableEquipment();
		if (droppedItemInventoryID != FE4Data.Item.NONE.ID && itemMap != null) {
			FE4Data.Item droppedItem = itemMap.getItemAtIndex(droppedItemInventoryID);
			
			if (droppedItem.ID == item1ID) { itemMap.setItemAtIndex(droppedItemInventoryID, item1); droppedItem = item1; }
			if (droppedItem.ID == item2ID) { itemMap.setItemAtIndex(droppedItemInventoryID, item2); droppedItem = item2; }
			
			if (enemyChar.isGen1() && !targetClass.canUseWeapon(droppedItem, null, null, null) && !itemSet.isEmpty()) {
				usableItems = new ArrayList<FE4Data.Item>(itemSet);
				FE4Data.Item replacement = usableItems.get(rng.nextInt(enemyChar.isBoss() ? Math.max(1, usableItems.size() / 2) : usableItems.size()));
				itemMap.setItemAtIndex(droppedItemInventoryID, replacement);
			}
		}
	}
	
	private static void setEnemyCharacterToClass(FE4ClassOptions options, FE4EnemyCharacter enemy, FE4Data.CharacterClass targetClass, FE4Data.Item weapon, ItemMapper itemMap, Random rng) {
		setEnemyCharacterToClass(options, enemy, targetClass, weapon, null, itemMap, rng);
	}
	
	private static void setEnemyCharacterToClass(FE4ClassOptions options, FE4EnemyCharacter enemy, FE4Data.CharacterClass targetClass, FE4Data.Item item1, FE4Data.Item item2, ItemMapper itemMap, Random rng) {
		enemy.setClassID(targetClass.ID);
		enemy.setEquipment1(item1 != null ? item1.ID : FE4Data.Item.NONE.ID);
		if (item2 != null) {
			enemy.setEquipment2(item2 != null ? item2.ID : FE4Data.Item.NONE.ID);
		} else if (enemy.getEquipment2() != FE4Data.Item.NONE.ID) {
			Set<FE4Data.Item> itemSet = new HashSet<FE4Data.Item>(Arrays.asList(targetClass.usableItems(null, null, null, false)));
			itemSet.removeAll(FE4Data.Item.femaleOnlyWeapons);
			itemSet.removeIf(item -> (FE4Data.Item.playerOnlySet.contains(item)));
			List<FE4Data.Item> usableItems = new ArrayList<FE4Data.Item>(itemSet);
			Collections.sort(usableItems, FE4Data.Item.defaultComparator);
			
			int item2ID = enemy.getEquipment2();
			FE4Data.Item existingItem = FE4Data.Item.valueOf(item2ID);
			if (item2 != Item.NONE) {
				if (!targetClass.canUseWeapon(existingItem, null, null, null) || (item1 != null && existingItem.ID == item1.ID)) {
					if (!usableItems.isEmpty()) {
						FE4Data.Item item = usableItems.get(rng.nextInt(usableItems.size()));
						enemy.setEquipment2(item.ID);
					} else {
						enemy.setEquipment2(Item.NONE.ID);
					}
				}
			}
		}
		
		// If we must set the item on this enemy and they also drop a weapon, force that drop to be the item given (unless it's a ring), just to ensure they can use the dropped item.
		if (itemMap != null && enemy.getDropableEquipment() != FE4Data.Item.NONE.ID && !itemMap.getItemAtIndex(enemy.getDropableEquipment()).isRing()) {
			itemMap.setItemAtIndex(enemy.getDropableEquipment(), item1);
		}
	}
	
	private static FE4Data.Item strictReplacementForItem(Item referenceItem, int joinChapter, Set<FE4Data.Item> allUsableItems, Random rng) {
		Set<FE4Data.Item> workingSet = new HashSet<FE4Data.Item>(allUsableItems);
		boolean filtered = false;
		
		if (joinChapter % 6 <= 1) { // Check Iron > Steel > Silver
			if (FE4Data.Item.ironSet.contains(referenceItem)) {
				workingSet.retainAll(FE4Data.Item.ironSet);
				filtered = true;
			} else if (FE4Data.Item.steelSet.contains(referenceItem)) { 
				workingSet.retainAll(FE4Data.Item.steelSet);
				filtered = true;
			} else if (FE4Data.Item.silverSet.contains(referenceItem)) { 
				workingSet.retainAll(FE4Data.Item.silverSet);
				filtered = true;
			}
		} else if (joinChapter % 6 <= 3) { // Check Steel > Iron > Silver
			if (FE4Data.Item.steelSet.contains(referenceItem)) {
				workingSet.retainAll(FE4Data.Item.steelSet);
				filtered = true;
			} else if (FE4Data.Item.ironSet.contains(referenceItem)) { 
				workingSet.retainAll(FE4Data.Item.ironSet);
				filtered = true;
			} else if (FE4Data.Item.silverSet.contains(referenceItem)) { 
				workingSet.retainAll(FE4Data.Item.silverSet);
				filtered = true;
			}
		} else { // Check Silver > Steel > Iron
			if (FE4Data.Item.silverSet.contains(referenceItem)) { 
				workingSet.retainAll(FE4Data.Item.silverSet);
				filtered = true;
			} else if (FE4Data.Item.steelSet.contains(referenceItem)) { 
				workingSet.retainAll(FE4Data.Item.steelSet);
				filtered = true;
			} else if (FE4Data.Item.ironSet.contains(referenceItem)) {
				workingSet.retainAll(FE4Data.Item.ironSet);
				filtered = true;
			} 		
		}
		
		if (filtered) { // Some filter happened. We should use that filter and return something from that list.
			if (!workingSet.isEmpty()) {
				List<FE4Data.Item> weaponList = new ArrayList<FE4Data.Item>(workingSet);
				Collections.sort(weaponList, FE4Data.Item.defaultComparator);
				return weaponList.get(rng.nextInt(weaponList.size()));
			}
		}
		// Check the more exotic weapons if we get this far.
		if (FE4Data.Item.rangedSet.contains(referenceItem)) {
			workingSet.retainAll(FE4Data.Item.rangedSet);
			filtered = true;
		} else if (FE4Data.Item.effectiveSet.contains(referenceItem)) {
			workingSet.retainAll(FE4Data.Item.effectiveSet);
			filtered = true;
		} else if (FE4Data.Item.braveSet.contains(referenceItem)) {
			workingSet.retainAll(FE4Data.Item.braveSet);
			filtered = true;
		}
		
		if (filtered) {
			if (!workingSet.isEmpty()) {
				List<FE4Data.Item> weaponList = new ArrayList<FE4Data.Item>(workingSet);
				Collections.sort(weaponList, FE4Data.Item.defaultComparator);
				return weaponList.get(rng.nextInt(weaponList.size()));
			}
		}
		
		return looseReplacementForItem(referenceItem, joinChapter, allUsableItems, rng);
	}
	
	private static FE4Data.Item looseReplacementForItem(Item referenceItem, int joinChapter, Set<FE4Data.Item> allUsableItems, Random rng) {
		Set<FE4Data.Item> workingSet = new HashSet<FE4Data.Item>(allUsableItems);
		
		if (FE4Data.Item.normalWeapons.contains(referenceItem)) {
			workingSet.retainAll(FE4Data.Item.normalWeapons);
		} else {
			workingSet.retainAll(FE4Data.Item.interestingWeapons);
		}
		
		if (referenceItem.getRank() == FE4Data.Item.WeaponRank.C) {
			workingSet.retainAll(FE4Data.Item.cWeapons);
		} else if (referenceItem.getRank() == FE4Data.Item.WeaponRank.B) {
			workingSet.retainAll(FE4Data.Item.bWeapons);
		} else if (referenceItem.getRank() == FE4Data.Item.WeaponRank.A) {
			workingSet.retainAll(FE4Data.Item.aWeapons);
		}
		
		if (!workingSet.isEmpty()) {
			List<FE4Data.Item> replacementList = new ArrayList<FE4Data.Item>(workingSet);
			Collections.sort(replacementList, FE4Data.Item.defaultComparator);
			return replacementList.get(rng.nextInt(replacementList.size()));
		} else if (!allUsableItems.isEmpty()) {
			List<FE4Data.Item> replacementList = new ArrayList<FE4Data.Item>(allUsableItems);
			Collections.sort(replacementList, FE4Data.Item.defaultComparator);
			return replacementList.get(rng.nextInt(replacementList.size()));
		} else {
			// This shouldn't ever happen, but we have no choice. Just return the reference item.
			return referenceItem;
		}
	}
}
