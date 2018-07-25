package fedata.general;

public enum WeaponRank {
	NONE, E, D, C, B, A, S, PRF;
	
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
}
