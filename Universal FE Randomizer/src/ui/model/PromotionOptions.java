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
	public final Boolean keepSameDamageType;

	// FE6 Only
	public final Boolean allowThiefPromotion;
	public final Boolean keepThiefAbilities;
	public final Boolean universal;

	public PromotionOptions(Mode mode, Boolean allowMountChange, Boolean allowEnemyClass, Boolean commonWeapon,
							Boolean allowMonsterClasses, Boolean keepSameDamageType, Boolean allowThiefPromotion, Boolean keepThiefAbilities, Boolean universal) {
		this.promotionMode = mode;

		this.allowMountChanges = allowMountChange;
		this.allowEnemyOnlyPromotedClasses = allowEnemyClass;

		this.requireCommonWeapon = commonWeapon;
		this.allowMonsterClasses = allowMonsterClasses;
		this.keepSameDamageType = keepSameDamageType;
		this.allowThiefPromotion = allowThiefPromotion;
		this.keepThiefAbilities = keepThiefAbilities;
		this.universal = universal;
	}

}
