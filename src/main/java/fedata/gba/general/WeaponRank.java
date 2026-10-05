package fedata.gba.general;

import fedata.general.FEBase;
import fedata.general.FEBase.GameType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum WeaponRank {
	NONE(0,0), E(0x01, 0x01), D(0x33, 0x1F), C(0x65, 0x47), B(0x097,0x79), A(0xC9, 0xB5), S(0xFB, 0xFB), PRF(-1, -1);

	public int fe6RankValue;
	public int fe78RankValue;

	WeaponRank(int fe6, int fe78) {
		this.fe6RankValue = fe6;
		this.fe78RankValue = fe78;
	}

	private static Map<Integer, WeaponRank> map = new HashMap<>();

	static {
		for (WeaponRank rank : WeaponRank.values()) {
			map.put(rank.fe6RankValue, rank);
			map.put(rank.fe78RankValue, rank);
		}
	}

	/**
	 * This can only be used in situations where the Characters weapon rank can be gauranteed to be exactly a the weapon rank with +0.
	 */
	public static WeaponRank valueOf(int rankVal) {
		WeaponRank weaponRank = map.get(rankVal);
		assert(weaponRank != null);
		return weaponRank;
	}


	/**
	 * This rounds down to the next lower rank that the given rankVal would equate to.
	 */
	public static WeaponRank roundToFullRank(int rankVal, GameType type) {
		if (rankVal == -1) {
			return PRF;
		} else if (rankVal == 0) {
			return NONE;
		} else if (rankVal >= E.rankValue(type) && rankVal < D.rankValue(type)) {
			return E;
		} else if (rankVal >= D.rankValue(type) && rankVal < C.rankValue(type)) {
			return D;
		} else if (rankVal >= C.rankValue(type) && rankVal < B.rankValue(type)) {
			return E;
		} else if (rankVal >= B.rankValue(type) && rankVal < A.rankValue(type)) {
			return E;
		} else if (rankVal >= A.rankValue(type) && rankVal < S.rankValue(type)) {
			return E;
		} else if (rankVal >= S.rankValue(type)) {
			return S;
		}

		return NONE;
	}

	public static WeaponRank nextRankHigherThanRank(WeaponRank rank) {
		switch (rank) {
			case A:
				return S;
			case B:
				return A;
			case C:
				return B;
			case D:
				return C;
			case E:
				return D;
			default:
				return NONE;
		}
	}

	public static WeaponRank nextRankLowerThanRank(WeaponRank rank) {
		switch (rank) {
			case S:
				return A;
			case A:
				return B;
			case B:
				return C;
			case C:
				return D;
			case D:
				return E;
			default:
				return NONE;
		}
	}
	
	// excludes S and NONE by default. Passing in NONE results in an empty set.
	// Passing in S results in S and A.
	public static Set<WeaponRank> surroundingRanks(WeaponRank centerRank) {
		Set<WeaponRank> ranks = new HashSet<WeaponRank>();
		
		if (centerRank == WeaponRank.NONE) {
			return ranks;
		}
		
		ranks.add(centerRank);
		WeaponRank nextHigher = nextRankHigherThanRank(centerRank);
		WeaponRank nextLower = nextRankLowerThanRank(centerRank);
		if (nextHigher != WeaponRank.S && nextHigher != WeaponRank.NONE) {
			ranks.add(nextHigher);
		}
		if (nextLower != WeaponRank.NONE && nextLower != WeaponRank.S) {
			ranks.add(nextLower);
		}
		
		return ranks;
	}

	public int compare(WeaponRank other) {
		return this.isLowerThan(other) ? -1 : other.isLowerThan(this) ? 1 : 0;
	}

	public Boolean isHigherThan(WeaponRank rank) {
		return this.fe78RankValue > rank.fe78RankValue;
	}

	public Boolean isLowerThan(WeaponRank rank) {
		return this.fe78RankValue < rank.fe78RankValue;
	}

	public int rankValue(GameType type){
		return GameType.FE6.equals(type) ? this.fe6RankValue : this.fe78RankValue;
	}

	public String displayString() {
		switch (this) {
			case NONE:
				return "-";
			default:
				return this.toString();
		}
	}
}
