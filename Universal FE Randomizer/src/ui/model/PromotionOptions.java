package ui.model;

import fedata.general.FEBase.GameType;
import util.recordkeeper.RecordKeeper;

public class PromotionOptions {

	public enum Mode {
		STRICT, LOOSE, RANDOM
	}

	public final Mode promotionMode;

	public final Boolean allowMountChanges;
	public final Boolean allowEnemyOnlyPromotedClasses;

	public final Boolean requireCommonWeapon;
	public final Boolean allowMonsterClasses;
	public Boolean keepSameDamageType;

	public PromotionOptions(Mode mode, Boolean allowMountChange, Boolean allowEnemyClass, Boolean commonWeapon,
							Boolean allowMonsterClasses, Boolean keepSameDamageType) {
		super();
		this.promotionMode = mode;

		this.allowMountChanges = allowMountChange;
		this.allowEnemyOnlyPromotedClasses = allowEnemyClass;

		this.requireCommonWeapon = commonWeapon;
		this.allowMonsterClasses = allowMonsterClasses;
		this.keepSameDamageType = keepSameDamageType;
	}

}
