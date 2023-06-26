package random.gba.loader;

import java.util.LinkedHashMap;
import java.util.Map;

import fedata.gba.fe8.FE8Data;
import fedata.gba.fe8.PromotionBranch;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;

/**
 * The Dataloader for the Promotion Branch table, note that this is only used for FE8,
 * as in the other games (FE6, FE7) this is already part of the Class Data
 */
public class PromotionDataLoader {

    public static final String RecordKeeperCategoryKey = "Promotions";

    private Map<FE8Data.CharacterClass, PromotionBranch> promotionBranches;

    public PromotionDataLoader(FileHandler handler) {
        promotionBranches = new LinkedHashMap<>();
        long baseAddress = FileReadHelper.readAddress(handler, FE8Data.PromotionBranchTablePointer);

        for (FE8Data.CharacterClass currentClass : FE8Data.CharacterClass.values()) { // These are conveniently labeled
            // in order of class ID.
            int index = currentClass.ID;
            promotionBranches.put(currentClass,
                    new PromotionBranch(handler, baseAddress + (index * FE8Data.BytesPerPromotionBranchEntry)));
        }
    }

    public void compileDiffs(DiffCompiler compiler, FileHandler handler) {
        for (PromotionBranch branch : promotionBranches.values()) {
            branch.commitChanges();
        }

        // Write the classes in order, including ones we didn't modify. Those will have
        // to be copied from the handler, since we didn't have objects made for them.
        for (FE8Data.CharacterClass characterClass : FE8Data.CharacterClass.values()) {
            PromotionBranch branch = promotionBranches.get(characterClass);
            compiler.addDiff(new Diff(branch.getAddressOffset(), branch.getData().length, branch.getData(), null));
        }
    }


    public Boolean hasPromotions(int baseClassID) {
        return getFirstPromotionOptionClassID(baseClassID) != 0 || getSecondPromotionOptionClassID(baseClassID) != 0;
    }

    public int getFirstPromotionOptionClassID(int baseClassID) {
        FE8Data.CharacterClass baseClass = FE8Data.CharacterClass.valueOf(baseClassID);
        if (baseClass == null) { return 0; }
        PromotionBranch branch = promotionBranches.get(baseClass);
        if (branch == null) { return 0; }
        return branch.getFirstPromotion();
    }

    public int getSecondPromotionOptionClassID(int baseClassID) {
        FE8Data.CharacterClass baseClass = FE8Data.CharacterClass.valueOf(baseClassID);
        if (baseClass == null) { return 0; }
        PromotionBranch branch = promotionBranches.get(baseClass);
        if (branch == null) { return 0; }
        return branch.getSecondPromotion();
    }

    public void setFirstPromotionOptionForClass(int baseClassID, int firstPromotionClassID) {
        FE8Data.CharacterClass baseClass = FE8Data.CharacterClass.valueOf(baseClassID);
        if (baseClass == null) { return; }
        FE8Data.CharacterClass promotedClass = FE8Data.CharacterClass.valueOf(firstPromotionClassID);
        if (promotedClass == null) { return; }

        PromotionBranch branch = promotionBranches.get(baseClass);
        if (branch == null) { return; }
        branch.setFirstPromotion(promotedClass.ID);
    }

    public void setSecondPromotionOptionForClass(int baseClassID, int secondPromotionClassID) {
        FE8Data.CharacterClass baseClass = FE8Data.CharacterClass.valueOf(baseClassID);
        if (baseClass == null) { return; }
        FE8Data.CharacterClass promotedClass = FE8Data.CharacterClass.valueOf(secondPromotionClassID);
        if (promotedClass == null) { return; }

        PromotionBranch branch = promotionBranches.get(baseClass);
        if (branch == null) { return; }
        branch.setSecondPromotion(promotedClass.ID);
    }

    public Map<FE8Data.CharacterClass, PromotionBranch> getAllPromotionBranches() {
        return promotionBranches;
    }

}