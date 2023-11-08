package fedata.gba.general;

import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeaponRanks {
    public final WeaponRank swordRank;
    public final WeaponRank lanceRank;
    public final WeaponRank axeRank;
    public final WeaponRank bowRank;
    public final WeaponRank animaRank;
    public final WeaponRank lightRank;
    public final WeaponRank darkRank;
    public final WeaponRank staffRank;

    public WeaponRanks() {
        swordRank = WeaponRank.NONE;
        lanceRank = WeaponRank.NONE;
        axeRank = WeaponRank.NONE;
        bowRank = WeaponRank.NONE;
        animaRank = WeaponRank.NONE;
        lightRank = WeaponRank.NONE;
        darkRank = WeaponRank.NONE;
        staffRank = WeaponRank.NONE;
    }

    public WeaponRanks(GBAFECharacterData character, GBAFEClassData characterClass) {
        if (characterClass == null) {
            swordRank = WeaponRank.valueOf(character.getSwordRank());
            lanceRank = WeaponRank.valueOf(character.getLanceRank());
            axeRank = WeaponRank.valueOf(character.getAxeRank());
            bowRank = WeaponRank.valueOf(character.getBowRank());
            animaRank = WeaponRank.valueOf(character.getAnimaRank());
            lightRank = WeaponRank.valueOf(character.getLightRank());
            darkRank = WeaponRank.valueOf(character.getDarkRank());
            staffRank = WeaponRank.valueOf(character.getStaffRank());
        } else {
            swordRank = WeaponRank.valueOf(character.getSwordRank()) == WeaponRank.NONE ? WeaponRank.valueOf(characterClass.getSwordRank()) : WeaponRank.valueOf(character.getSwordRank());
            lanceRank = WeaponRank.valueOf(character.getLanceRank()) == WeaponRank.NONE ? WeaponRank.valueOf(characterClass.getLanceRank()) : WeaponRank.valueOf(character.getLanceRank());
            axeRank = WeaponRank.valueOf(character.getAxeRank()) == WeaponRank.NONE ? WeaponRank.valueOf(characterClass.getAxeRank()) : WeaponRank.valueOf(character.getAxeRank());
            bowRank = WeaponRank.valueOf(character.getBowRank()) == WeaponRank.NONE ? WeaponRank.valueOf(characterClass.getBowRank()) : WeaponRank.valueOf(character.getBowRank());
            animaRank = WeaponRank.valueOf(character.getAnimaRank()) == WeaponRank.NONE ? WeaponRank.valueOf(characterClass.getAnimaRank()) : WeaponRank.valueOf(character.getAnimaRank());
            lightRank = WeaponRank.valueOf(character.getLightRank()) == WeaponRank.NONE ? WeaponRank.valueOf(characterClass.getLightRank()) : WeaponRank.valueOf(character.getLightRank());
            darkRank = WeaponRank.valueOf(character.getDarkRank()) == WeaponRank.NONE ? WeaponRank.valueOf(characterClass.getDarkRank()) : WeaponRank.valueOf(character.getDarkRank());
            staffRank = WeaponRank.valueOf(character.getStaffRank()) == WeaponRank.NONE ? WeaponRank.valueOf(characterClass.getStaffRank()) : WeaponRank.valueOf(character.getStaffRank());
        }
    }

    public WeaponRanks(GBAFEClassData characterClass) {
        swordRank = WeaponRank.valueOf(characterClass.getSwordRank());
        lanceRank = WeaponRank.valueOf(characterClass.getLanceRank());
        axeRank = WeaponRank.valueOf(characterClass.getAxeRank());
        bowRank = WeaponRank.valueOf(characterClass.getBowRank());
        animaRank = WeaponRank.valueOf(characterClass.getAnimaRank());
        lightRank = WeaponRank.valueOf(characterClass.getLightRank());
        darkRank = WeaponRank.valueOf(characterClass.getDarkRank());
        staffRank = WeaponRank.valueOf(characterClass.getStaffRank());
    }
    public WeaponRanks(GBAFECharacterData character) {
        this(character, null);
    }

    public List<WeaponType> getTypes() {
        List<WeaponType> types = new ArrayList<WeaponType>();
        if (swordRank != WeaponRank.NONE) { types.add(WeaponType.SWORD); }
        if (lanceRank != WeaponRank.NONE) { types.add(WeaponType.LANCE); }
        if (axeRank != WeaponRank.NONE) { types.add(WeaponType.AXE); }
        if (bowRank != WeaponRank.NONE) { types.add(WeaponType.BOW); }
        if (lightRank != WeaponRank.NONE) { types.add(WeaponType.LIGHT); }
        if (darkRank != WeaponRank.NONE) { types.add(WeaponType.DARK); }
        if (animaRank != WeaponRank.NONE) { types.add(WeaponType.ANIMA); }
        if (staffRank != WeaponRank.NONE) { types.add(WeaponType.STAFF); }
        return types;
    }

    public WeaponRank rankForType(WeaponType type) {
        switch (type) {
            case SWORD: return swordRank;
            case LANCE: return lanceRank;
            case AXE: return axeRank;
            case BOW: return bowRank;
            case ANIMA: return animaRank;
            case LIGHT: return lightRank;
            case DARK: return darkRank;
            case STAFF: return staffRank;
            default:
                return WeaponRank.NONE;
        }
    }

    public List<WeaponRank> asList() {
        List<WeaponRank> ranks = new ArrayList<>();
        ranks.add(swordRank);
        ranks.add(lanceRank);
        ranks.add(axeRank);
        ranks.add(bowRank);
        ranks.add(animaRank);
        ranks.add(lightRank);
        ranks.add(darkRank);
        ranks.add(staffRank);
        return ranks;
    }
}
