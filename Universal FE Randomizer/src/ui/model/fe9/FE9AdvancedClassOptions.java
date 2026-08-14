package ui.model.fe9;

import java.util.ArrayList;
import java.util.List;

import fedata.gcnwii.fe9.FE9Data;

public class FE9AdvancedClassOptions {

	public boolean randomizePlayerCharacters;
	public List<FE9Data.Character> playerCharactersToRandomize;
	public List<FE9Data.CharacterClass> playerTargetClasses;
	
	public boolean treatSimilarAsSame;
	public boolean evenDistribution;
	public boolean forceChange;
	public boolean allowCrossRace;
	
	public FE9AdvancedClassOptions(boolean randomizePCs, List<FE9Data.Character> pcCharacters, List<FE9Data.CharacterClass> pcClasses, 
			boolean treatSimilarAsSame, boolean evenDistribution, boolean forceChange, boolean allowCrossRace) {
		randomizePlayerCharacters = randomizePCs;
		playerCharactersToRandomize = new ArrayList<FE9Data.Character>(pcCharacters);
		playerTargetClasses = new ArrayList<FE9Data.CharacterClass>(pcClasses);
		this.evenDistribution = evenDistribution;
		this.forceChange = forceChange;
		this.allowCrossRace = allowCrossRace;
		this.treatSimilarAsSame = treatSimilarAsSame;
	}
	
	public FE9AdvancedClassOptions(FE9ClassOptions legacyOptions) {
		randomizePlayerCharacters = legacyOptions.randomizePCs;
		playerCharactersToRandomize = new ArrayList<FE9Data.Character>(FE9Data.Character.allPlayableCharacters).stream().filter(character -> {
			if (character.isModifiable() == false) { return false; }
			if (character.isLord()) { return legacyOptions.includeLords; }
			if (character.isThief()) { return legacyOptions.includeThieves; }
			if (character.isSpecial()) { return legacyOptions.includeSpecial; }
			return true;
		}).toList();
		playerTargetClasses = new ArrayList<FE9Data.CharacterClass>(FE9Data.CharacterClass.playerEligibleClasses);
		evenDistribution = legacyOptions.assignClassesEvenly;
		forceChange = legacyOptions.forceDifferent;
		allowCrossRace = legacyOptions.mixPCRaces;
		treatSimilarAsSame = true;
	}
	
	// For legacy convenience.
	public boolean includeLords() {
		return playerCharactersToRandomize.contains(FE9Data.Character.IKE);
	}
}
