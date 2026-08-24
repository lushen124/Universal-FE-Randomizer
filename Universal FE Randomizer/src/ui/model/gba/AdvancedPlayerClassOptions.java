package ui.model.gba;

import java.util.List;

public class AdvancedPlayerClassOptions {
	public enum BaseTransferOption {
		NO_CHANGE, ADJUST_TO_MATCH, ADJUST_TO_CLASS
	}
	
	public enum GenderRestrictionOption {
		STRICT, LOOSE, NONE
	}
	
	public enum GrowthAdjustmentOption {
		NO_CHANGE, TRANSFER_PERSONAL_GROWTHS, CLASS_RELATIVE_GROWTHS
	}
	
	public boolean randomizePlayableCharacters;
	
	public List<Integer> randomizedCharacterIDs;
	public List<Integer> allowedClassIDs;
	
	public boolean assignEvenly;
	public boolean forceChange;
	public boolean treatSimilarAsSame;
	
	public BaseTransferOption transferBases;
	public GenderRestrictionOption restrictGender;
	public GrowthAdjustmentOption growthAdjustments;
	
	public AdvancedPlayerClassOptions(boolean randomizeClasses, List<Integer> characterIDs, List<Integer> classIDs, boolean assignEvenly, boolean forceChange, boolean treatSimilarAsSame,
			BaseTransferOption basesTransfer, GenderRestrictionOption genderOption, GrowthAdjustmentOption growthAdjustment) {
		this.randomizePlayableCharacters = randomizeClasses;
		
		randomizedCharacterIDs = characterIDs;
		allowedClassIDs = classIDs;
		
		this.assignEvenly = assignEvenly;
		this.forceChange = forceChange;
		this.treatSimilarAsSame = treatSimilarAsSame;
		
		this.transferBases = basesTransfer;
		this.restrictGender = genderOption;
		this.growthAdjustments = growthAdjustment;
	}
	
	public AdvancedPlayerClassOptions optionsDisablingGenderRequirement() {
		return new AdvancedPlayerClassOptions(this.randomizePlayableCharacters,
				this.randomizedCharacterIDs, this.allowedClassIDs,
				this.assignEvenly, this.forceChange, this.treatSimilarAsSame,
				this.transferBases, GenderRestrictionOption.NONE, this.growthAdjustments);
	}
	
	public AdvancedPlayerClassOptions optionsDisablingTreatSimilarAsSame() {
		return new AdvancedPlayerClassOptions(this.randomizePlayableCharacters,
				this.randomizedCharacterIDs, this.allowedClassIDs,
				this.assignEvenly, this.forceChange, false,
				this.transferBases, this.restrictGender, this.growthAdjustments);
	}
	
	public AdvancedPlayerClassOptions optionsDisablingForceChange() {
		return new AdvancedPlayerClassOptions(this.randomizePlayableCharacters,
				this.randomizedCharacterIDs, this.allowedClassIDs,
				this.assignEvenly, false, this.treatSimilarAsSame,
				this.transferBases, this.restrictGender, this.growthAdjustments);
	}
}
