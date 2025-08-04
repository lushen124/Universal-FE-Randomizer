package random.gba.randomizer.service;

import java.util.Random;

import fedata.gba.GBAFEChapterData;
import fedata.gba.GBAFEChapterItemData;
import fedata.gba.GBAFEChapterUnitData;
import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.gba.GBAFEItemData;
import fedata.general.FEBase.GameType;
import random.gba.loader.ChapterLoader;
import random.gba.loader.CharacterDataLoader;
import random.gba.loader.ClassDataLoader;
import random.gba.loader.ItemDataLoader;
import random.gba.loader.TextLoader;
import random.gba.randomizer.ClassRandomizer;
import ui.model.ItemAssignmentOptions;
/**
 * Service responsible for giving new usable items to a character who had their class changed
 */
public class ItemAssignmentService {
	
	public static void assignNewItems(CharacterDataLoader characterData, GBAFECharacterData slot,
			GBAFEClassData targetClass, ChapterLoader chapterData, ItemAssignmentOptions inventoryOptions, GameType type, Random rng,
			TextLoader textData, ClassDataLoader classData, ItemDataLoader itemData) {
		for (GBAFEChapterData chapter : chapterData.allChapters()) {
			GBAFEChapterItemData reward = chapter.chapterItemGivenToCharacter(slot.getID());
			if (reward != null) {
				GBAFEItemData item = null;
				GBAFEItemData[] prfWeapons = itemData.prfWeaponsForClass(targetClass.getID());
				if (prfWeapons.length > 0) {
					item = prfWeapons[rng.nextInt(prfWeapons.length)];
				} else {
					item = itemData.getRandomWeaponForCharacter(slot, false, false,
							characterData.isEnemyAtAnyPoint(slot.getID()), inventoryOptions.assignPromoWeapons,
							inventoryOptions.assignPoisonWeapons, false, false, rng);
				}

				if (item != null) {
					reward.setItemID(item.getID());
				}
			}

			for (GBAFEChapterUnitData unit : chapter.allUnits()) {
				if (unit.getCharacterNumber() == slot.getID()) {
					unit.setStartingClass(targetClass.getID());

					// Set Inventory.
					ClassRandomizer.validateCharacterInventory(inventoryOptions, slot, targetClass, unit, chapter,
							characterData.characterIDRequiresRange(slot.getID()),
							characterData.characterIDRequiresMelee(slot.getID()), characterData, classData, itemData,
							textData, false, false, false, false, type, rng);
					if (characterData.isThiefCharacterID(slot.getID())) {
						ClassRandomizer.validateFormerThiefInventory(unit, itemData);
					}
					ClassRandomizer.validateSpecialClassInventory(unit, itemData, rng);
				}
			}
		}
	}
}
