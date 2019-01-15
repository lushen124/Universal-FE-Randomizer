package random.snes.fe4.randomizer;

import java.util.List;
import java.util.Random;

import fedata.snes.fe4.FE4Data;
import fedata.snes.fe4.FE4HolyBlood;
import random.snes.fe4.loader.HolyBloodLoader;

public class FE4BloodRandomizer {
	
	static final int rngSalt = 12321;
	
	public static void randomizeHolyBloodGrowthBonuses(int growthBonusTotal, HolyBloodLoader bloodData, Random rng) {
		List<FE4HolyBlood> bloodList = bloodData.allHolyBlood();
		for (FE4HolyBlood blood : bloodList) {
			int growthRemaining = growthBonusTotal;
			
			int hpBonus = 0;
			int strBonus = 0;
			int magBonus = 0;
			int sklBonus = 0;
			int spdBonus = 0;
			int lckBonus = 0;
			int defBonus = 0;
			int resBonus = 0;
			
			while (growthRemaining > 0) {
				int amount = 5;
				growthRemaining -= 5;
				int field = rng.nextInt(7);
				switch (field) {
				case 0:
					hpBonus += amount;
					break;
				case 1:
					if (blood.getWeaponType().isPhysical()) { strBonus += amount; }
					else { magBonus += amount; }
					break;
				case 2:
					sklBonus += amount;
					break;
				case 3:
					spdBonus += amount;
					break;
				case 4:
					lckBonus += amount;
					break;
				case 5:
					defBonus += amount;
					break;
				case 6:
					resBonus += amount;
					break;
				}
			}
			
			blood.setHPGrowthBonus(hpBonus);
			blood.setSTRGrowthBonus(strBonus);
			blood.setMAGGrowthBonus(magBonus);
			blood.setSKLGrowthBonus(sklBonus);
			blood.setSPDGrowthBonus(spdBonus);
			blood.setLCKGrowthBonus(lckBonus);
			blood.setDEFGrowthBonus(defBonus);
			blood.setRESGrowthBonus(resBonus);
		}
	}

	public static void randomizeHolyWeaponBonuses(HolyBloodLoader bloodData, Random rng) {
		List<FE4HolyBlood> bloodList = bloodData.allHolyBlood();
		for (FE4HolyBlood blood : bloodList) {
			
			FE4Data.Item holyWeapon = FE4Data.Item.valueOf(blood.getHolyWeaponID());
			if (holyWeapon.holyBloodForHolyWeapon() == FE4Data.HolyBlood.NAGA ||
					holyWeapon.holyBloodForHolyWeapon() == FE4Data.HolyBlood.LOPTOUS) {
				continue;
			}
			
			int holyWeaponBonuses = blood.getHolyWeaponSKLBonus() + blood.getHolyWeaponSPDBonus() + blood.getHolyWeaponDEFBonus() + blood.getHolyWeaponRESBonus() + blood.getHolyWeaponSTRBonus() + blood.getHolyWeaponMAGBonus();
			
			int strBonus = 0;
			int magBonus = 0;
			int sklBonus = 0;
			int spdBonus = 0;
			int defBonus = 0;
			int resBonus = 0;
			
			while (holyWeaponBonuses > 0) {
				int amount = 5;
				holyWeaponBonuses -= 5;
				int field = rng.nextInt(5);
				switch (field) {
				case 0:
					if (blood.getWeaponType().isPhysical()) { strBonus += amount; }
					else { magBonus += amount; }
					break;
				case 1:
					sklBonus += amount;
					break;
				case 2:
					spdBonus += amount;
					break;
				case 3:
					defBonus += amount;
					break;
				case 4:
					resBonus += amount;
					break;
				}
			}
			
			blood.setHolyWeaponSTRBonus(strBonus);
			blood.setHolyWeaponMAGBonus(magBonus);
			blood.setHolyWeaponSKLBonus(sklBonus);
			blood.setHolyWeaponSPDBonus(spdBonus);
			blood.setHolyWeaponDEFBonus(defBonus);
			blood.setHolyWeaponRESBonus(resBonus);
		}
	}
}
