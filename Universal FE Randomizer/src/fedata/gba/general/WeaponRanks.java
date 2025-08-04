package fedata.gba.general;

import fedata.gba.GBAFECharacterData;
import fedata.gba.GBAFEClassData;
import fedata.general.FEBase.GameType;

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
    
    public WeaponRanks(GBAFEClassData characterClass, WeaponRank upRank, GameType type) {
    	WeaponRank sword = WeaponRank.roundToFullRank(characterClass.getSwordRank(), type);
        WeaponRank lance = WeaponRank.roundToFullRank(characterClass.getLanceRank(), type);
        WeaponRank axe = WeaponRank.roundToFullRank(characterClass.getAxeRank(), type);
        WeaponRank bow = WeaponRank.roundToFullRank(characterClass.getBowRank(), type);
        WeaponRank anima = WeaponRank.roundToFullRank(characterClass.getAnimaRank(), type);
        WeaponRank light = WeaponRank.roundToFullRank(characterClass.getLightRank(), type);
        WeaponRank dark = WeaponRank.roundToFullRank(characterClass.getDarkRank(), type);
        WeaponRank staff = WeaponRank.roundToFullRank(characterClass.getStaffRank(), type);
        
        if (sword != WeaponRank.NONE) { sword = upRank; }
        if (lance != WeaponRank.NONE) { lance = upRank; }
        if (axe != WeaponRank.NONE) { axe = upRank; }
        if (bow != WeaponRank.NONE) { bow = upRank; }
        if (anima != WeaponRank.NONE) { anima = upRank; }
        if (light != WeaponRank.NONE) { light = upRank; }
        if (dark != WeaponRank.NONE) { dark = upRank; }
        if (staff != WeaponRank.NONE) { staff = upRank; }
        
        swordRank = sword;
        lanceRank = lance;
        axeRank = axe;
        bowRank = bow;
        animaRank = anima;
        lightRank = light;
        darkRank = dark;
        staffRank = staff;
    }

    public WeaponRanks(GBAFEClassData characterClass, boolean roundToNearest, GameType type) {
        WeaponRank sword = WeaponRank.valueOf(characterClass.getSwordRank());
        WeaponRank lance = WeaponRank.valueOf(characterClass.getLanceRank());
        WeaponRank axe = WeaponRank.valueOf(characterClass.getAxeRank());
        WeaponRank bow = WeaponRank.valueOf(characterClass.getBowRank());
        WeaponRank anima = WeaponRank.valueOf(characterClass.getAnimaRank());
        WeaponRank light = WeaponRank.valueOf(characterClass.getLightRank());
        WeaponRank dark = WeaponRank.valueOf(characterClass.getDarkRank());
        WeaponRank staff = WeaponRank.valueOf(characterClass.getStaffRank());
        
        if (roundToNearest) {
        	if (sword == null) { sword = WeaponRank.roundToFullRank(characterClass.getSwordRank(), type); }
        	if (lance == null) { lance = WeaponRank.roundToFullRank(characterClass.getLanceRank(), type); }
        	if (axe == null) { axe = WeaponRank.roundToFullRank(characterClass.getAxeRank(), type); }
        	if (bow == null) { bow = WeaponRank.roundToFullRank(characterClass.getBowRank(), type); }
        	if (anima == null) { anima = WeaponRank.roundToFullRank(characterClass.getAnimaRank(), type); }
        	if (light == null) { light = WeaponRank.roundToFullRank(characterClass.getLightRank(), type); }
        	if (dark == null) { dark = WeaponRank.roundToFullRank(characterClass.getDarkRank(), type); }
        	if (staff == null) { staff = WeaponRank.roundToFullRank(characterClass.getStaffRank(), type); }
        }
        
        swordRank = sword;
        lanceRank = lance;
        axeRank = axe;
        bowRank = bow;
        animaRank = anima;
        lightRank = light;
        darkRank = dark;
        staffRank = staff;
    }
    
    public WeaponRanks(GBAFECharacterData character) {
        this(character, null);
    }
    
    public WeaponRanks(GBAFECharacterData character, GBAFEClassData charClass, boolean roundToNearest, GameType type) {
    	WeaponRank sword = WeaponRank.valueOf(character.getSwordRank());
        WeaponRank lance = WeaponRank.valueOf(character.getLanceRank());
        WeaponRank axe = WeaponRank.valueOf(character.getAxeRank());
        WeaponRank bow = WeaponRank.valueOf(character.getBowRank());
        WeaponRank anima = WeaponRank.valueOf(character.getAnimaRank());
        WeaponRank light = WeaponRank.valueOf(character.getLightRank());
        WeaponRank dark = WeaponRank.valueOf(character.getDarkRank());
        WeaponRank staff = WeaponRank.valueOf(character.getStaffRank());
        
        if (roundToNearest) {
        	if (sword == null) { sword = WeaponRank.roundToFullRank(character.getSwordRank(), type); }
        	if (lance == null) { lance = WeaponRank.roundToFullRank(character.getLanceRank(), type); }
        	if (axe == null) { axe = WeaponRank.roundToFullRank(character.getAxeRank(), type); }
        	if (bow == null) { bow = WeaponRank.roundToFullRank(character.getBowRank(), type); }
        	if (anima == null) { anima = WeaponRank.roundToFullRank(character.getAnimaRank(), type); }
        	if (light == null) { light = WeaponRank.roundToFullRank(character.getLightRank(), type); }
        	if (dark == null) { dark = WeaponRank.roundToFullRank(character.getDarkRank(), type); }
        	if (staff == null) { staff = WeaponRank.roundToFullRank(character.getStaffRank(), type); }
        }
        
        if (charClass != null) {
        	if (sword == null) { sword = WeaponRank.valueOf(charClass.getSwordRank()); }
        	if (lance == null) { lance = WeaponRank.valueOf(charClass.getLanceRank()); }
        	if (axe == null) { axe = WeaponRank.valueOf(charClass.getAxeRank()); }
        	if (bow == null) { bow = WeaponRank.valueOf(charClass.getBowRank()); }
        	if (anima == null) { anima = WeaponRank.valueOf(charClass.getAnimaRank()); }
        	if (light == null) { light = WeaponRank.valueOf(charClass.getLightRank()); }
        	if (dark == null) { dark = WeaponRank.valueOf(charClass.getDarkRank()); }
        	if (staff == null) { staff = WeaponRank.valueOf(charClass.getStaffRank()); }
        	
        	if (roundToNearest) {
            	if (sword == null) { sword = WeaponRank.roundToFullRank(charClass.getSwordRank(), type); }
            	if (lance == null) { lance = WeaponRank.roundToFullRank(charClass.getLanceRank(), type); }
            	if (axe == null) { axe = WeaponRank.roundToFullRank(charClass.getAxeRank(), type); }
            	if (bow == null) { bow = WeaponRank.roundToFullRank(charClass.getBowRank(), type); }
            	if (anima == null) { anima = WeaponRank.roundToFullRank(charClass.getAnimaRank(), type); }
            	if (light == null) { light = WeaponRank.roundToFullRank(charClass.getLightRank(), type); }
            	if (dark == null) { dark = WeaponRank.roundToFullRank(charClass.getDarkRank(), type); }
            	if (staff == null) { staff = WeaponRank.roundToFullRank(charClass.getStaffRank(), type); }
            }
        }
        
        swordRank = sword;
        lanceRank = lance;
        axeRank = axe;
        bowRank = bow;
        animaRank = anima;
        lightRank = light;
        darkRank = dark;
        staffRank = staff;
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
    
    public WeaponType getHighestRank() {
    	WeaponType type = null;
    	WeaponRank highestRank = null;
    	if (swordRank != WeaponRank.NONE && (highestRank == null || swordRank.isHigherThan(highestRank))) { highestRank = swordRank; type = WeaponType.SWORD; }
    	if (lanceRank != WeaponRank.NONE && (highestRank == null || lanceRank.isHigherThan(highestRank))) { highestRank = lanceRank; type = WeaponType.LANCE; }
    	if (axeRank != WeaponRank.NONE && (highestRank == null || axeRank.isHigherThan(highestRank))) { highestRank = axeRank; type = WeaponType.AXE; }
    	if (bowRank != WeaponRank.NONE && (highestRank == null || bowRank.isHigherThan(highestRank))) { highestRank = bowRank; type = WeaponType.BOW; }
    	if (lightRank != WeaponRank.NONE && (highestRank == null || lightRank.isHigherThan(highestRank))) { highestRank = lightRank; type = WeaponType.LIGHT; }
    	if (animaRank != WeaponRank.NONE && (highestRank == null || animaRank.isHigherThan(highestRank))) { highestRank = animaRank; type = WeaponType.ANIMA; }
    	if (darkRank != WeaponRank.NONE && (highestRank == null || darkRank.isHigherThan(highestRank))) { highestRank = darkRank; type = WeaponType.DARK; }
    	if (staffRank != WeaponRank.NONE && (highestRank == null || staffRank.isHigherThan(highestRank))) { highestRank = staffRank; type = WeaponType.STAFF; }
    	
    	return type;
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
    
    public String debugString() {
    	List<String> stringComponents = new ArrayList<String>();
    	if (swordRank != WeaponRank.NONE) {
    		stringComponents.add("Sword: " + swordRank.displayString());
    	}
    	if (lanceRank != WeaponRank.NONE) {
    		stringComponents.add("Lance: " + lanceRank.displayString());
    	}
    	if (axeRank != WeaponRank.NONE) {
    		stringComponents.add("Axe: " + axeRank.displayString());
    	}
    	if (bowRank != WeaponRank.NONE) {
    		stringComponents.add("Bow: " + bowRank.displayString());
    	}
    	if (animaRank != WeaponRank.NONE) {
    		stringComponents.add("Anima: " + animaRank.displayString());
    	}
    	if (lightRank != WeaponRank.NONE) {
    		stringComponents.add("Light: " + lightRank.displayString());
    	}
    	if (darkRank != WeaponRank.NONE) {
    		stringComponents.add("Dark: " + darkRank.displayString());
    	}
    	
    	return "[" + String.join(",", stringComponents) + "]";
    }
}
