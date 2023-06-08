package ui.model.fe4;

public class FE4PromotionOptions {
	
	public enum Mode {
		STRICT,
		LOOSE,
		RANDOM
	}
	
	public final Mode promotionMode;
	
	public final boolean allowMountChanges;
	public final boolean allowEnemyOnlyPromotedClasses;
	
	public final boolean requireCommonWeapon;
	
	public FE4PromotionOptions(Mode mode, boolean allowMountChange, boolean allowEnemyClass, boolean commonWeapon) {
		super();
		this.promotionMode = mode;
		
		this.allowMountChanges = allowMountChange;
		this.allowEnemyOnlyPromotedClasses = allowEnemyClass;
		
		this.requireCommonWeapon = commonWeapon;
	}

}
