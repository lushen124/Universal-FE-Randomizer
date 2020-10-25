package fedata.gba.general;

import java.util.HashMap;
import java.util.Map;

public enum WeaponEffects {
	
	NONE, STAT_BOOSTS, EFFECTIVENESS, UNBREAKABLE, BRAVE, REVERSE_TRIANGLE, EXTEND_RANGE, HIGH_CRITICAL, MAGIC_DAMAGE, POISON, HALF_HP, DEVIL;
	
	public enum InfoKeys {
		CRITICAL_RANGE
	}
	
	public Map<InfoKeys, Object> additionalInfo;
	
	private WeaponEffects() {
		additionalInfo = new HashMap<InfoKeys, Object>();
	}
}
