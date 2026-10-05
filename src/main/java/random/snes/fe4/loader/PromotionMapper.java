package random.snes.fe4.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fedata.snes.fe4.FE4ChildCharacter;
import fedata.snes.fe4.FE4Data;
import fedata.snes.fe4.FE4StaticCharacter;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;
import util.recordkeeper.RecordKeeper;

public class PromotionMapper {
	
	private boolean isHeadered;
	
	private Map<FE4Data.Character, FE4Data.CharacterClass> promotions;
	
	public PromotionMapper(FileHandler handler, CharacterDataLoader charData, boolean isHeadered) {
		super();
		this.isHeadered = isHeadered;
		
		initializeMap(handler, charData);
	}
	
	public FE4Data.CharacterClass getPromotionForCharacter(FE4Data.Character fe4Char) {
		return promotions.get(fe4Char);
	}
	
	public void setPromotionForCharacter(FE4Data.Character fe4Char, FE4Data.CharacterClass promotion) {
		if (fe4Char == null || promotion == null || !promotions.containsKey(fe4Char)) { return; }
		promotions.put(fe4Char, promotion);
	}
	
	public Set<FE4Data.Character> allPromotableCharacters() {
		return promotions.keySet();
	}

	private void initializeMap(FileHandler handler, CharacterDataLoader charData) {
		promotions = new HashMap<FE4Data.Character, FE4Data.CharacterClass>();
		
		List<FE4StaticCharacter> staticCharacters = new ArrayList<FE4StaticCharacter>();
		staticCharacters.addAll(charData.getGen1Characters());
		staticCharacters.addAll(charData.getGen2CommonCharacters());
		staticCharacters.addAll(charData.getGen2SubstituteCharacters());
		for (FE4StaticCharacter staticChar : staticCharacters) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(staticChar.getCharacterID());
			int index = fe4Char.ID - 1; // Sigurd is in slot 0, even if his ID is 1, so we adjust everything down by 1.
			if (index >= FE4Data.PromotionTableCount) { System.err.println("Attempted to set promotion data for invalid index."); continue; }
			long address = FE4Data.PromotionTableOffset + index * (FE4Data.PromotionTableEntrySize);
			if (!isHeadered) { address -= 0x200; }
			
			byte[] data = handler.readBytesAtOffset(address, 1);
			FE4Data.CharacterClass promotionClass = FE4Data.CharacterClass.valueOf(data[0]);
			if (promotionClass != null) {
				promotions.put(fe4Char, promotionClass);
			} else {
				promotions.put(fe4Char, FE4Data.CharacterClass.NONE);
			}
		}
		
		for (FE4ChildCharacter childChar : charData.getAllChildren()) {
			FE4Data.Character fe4Char = FE4Data.Character.valueOf(childChar.getCharacterID());
			int index = fe4Char.ID - 1; // Sigurd is in slot 0, even if his ID is 1, so we adjust everything down by 1.
			if (index >= FE4Data.PromotionTableCount) { System.err.println("Attempted to set promotion data for invalid index."); continue; }
			long address = FE4Data.PromotionTableOffset + index * (FE4Data.PromotionTableEntrySize);
			if (!isHeadered) { address -= 0x200; }
			
			byte[] data = handler.readBytesAtOffset(address, 1);
			FE4Data.CharacterClass promotionClass = FE4Data.CharacterClass.valueOf(data[0]);
			if (promotionClass != null) {
				promotions.put(fe4Char, promotionClass);
			} else {
				promotions.put(fe4Char, FE4Data.CharacterClass.NONE);
			}
		}
	}
	
	public void compileDiff(DiffCompiler compiler) {
		for (FE4Data.Character fe4Char : promotions.keySet()) {
			FE4Data.CharacterClass promotion = promotions.get(fe4Char);
			
			int index = fe4Char.ID - 1; // Sigurd is in slot 0, even if his ID is 1, so we adjust everything down by 1.
			if (index >= FE4Data.PromotionTableCount) { System.err.println("Attempted to set promotion data for invalid index."); continue; }
			long address = FE4Data.PromotionTableOffset + index * (FE4Data.PromotionTableEntrySize);
			if (!isHeadered) { address -= 0x200; }
			compiler.addDiff(new Diff(address, 1, new byte[] {(byte)promotion.ID}, null));
		}
	}
	
	public void recordPromotions(RecordKeeper rk, Boolean isInitial) {
		for (FE4Data.Character fe4Char : promotions.keySet()) {
			FE4Data.CharacterClass promotion = promotions.get(fe4Char);
			
			String category = null;
			if (!fe4Char.isPlayable()) { continue; }
			
			if (fe4Char.isGen1()) {
				category = CharacterDataLoader.RecordKeeperCategoryKey + " - " + CharacterDataLoader.RecordKeeperSubcategoryGen1;
			} else if (fe4Char.isGen2()) {
				if (fe4Char.isSubstitute()) {
					category = CharacterDataLoader.RecordKeeperCategoryKey + " - " + CharacterDataLoader.RecordKeeperSubcategoryGen2Subs;
				} else if (fe4Char.isChild()) {
					category = CharacterDataLoader.RecordKeeperCategoryKey + " - " + CharacterDataLoader.RecordKeeperSubcategoryGen2Child;
				} else {
					category = CharacterDataLoader.RecordKeeperCategoryKey + " - " + CharacterDataLoader.RecordKeeperSubcategoryGen2Static;
				}
			}
			if (category != null) {
				recordData(rk, isInitial, category, fe4Char.toString(), "Promotion", promotion.toString());
			}
		}
	}
	
	private void recordData(RecordKeeper rk, boolean isInitial, String category, String entryKey, String key, String value) {
		if (isInitial) {
			rk.recordOriginalEntry(category, entryKey, key, value);
		} else {
			rk.recordUpdatedEntry(category, entryKey, key, value);
		}
	}
}
