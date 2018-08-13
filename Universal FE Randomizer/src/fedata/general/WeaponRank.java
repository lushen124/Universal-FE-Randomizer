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
	
	public Boolean isLowerThan(WeaponRank rank) {
		switch(rank) {
		case NONE:
		case PRF:
		case E:
			return false;
		case S:
			return this != S;
		case A:
			return this != S && this != A;
		case B:
			return this != S && this != A && this != B;
		case C:
			return this == D || this == E;
		case D:
			return this == E;
		default:
			return false;
		}
	}
}
