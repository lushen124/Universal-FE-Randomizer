package fedata.gba.general;

public class ChapterUnitClassOverride {
	
	public enum OverrideCondition {
		ALWAYS,
		ONLY_IF_NOT_FLIER
	}
	
	public OverrideCondition condition;
	
	public int characterNumber;
	public Integer oldClassID; // In case a character shows up as multiple classes within a chapter. Leave as null to target instances without regard to the character's original class.
	public int newClassID;
	
	// Useful for narrowing down a particular instance of a character.
	// Leave as null to indicate all instances of a character.
	public Integer startX;
	public Integer startY;
	public Integer endX;
	public Integer endY;
	
	public ChapterUnitClassOverride(int characterID, int targetClassID, OverrideCondition condition) {
		characterNumber = characterID;
		newClassID = targetClassID;
		this.condition = condition;
	}
	
	public ChapterUnitClassOverride(int characterID, int targetClassID, OverrideCondition condition, int sourceClassID) {
		this(characterID, targetClassID, condition);
		oldClassID = sourceClassID;
	}
	
	public ChapterUnitClassOverride(int characterID, int targetClassID, OverrideCondition condition, Integer startX, Integer startY, Integer endX, Integer endY) {
		this(characterID, targetClassID, condition);
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}

	public ChapterUnitClassOverride(int characterID, int targetClassID, OverrideCondition condition, int sourceClassID, Integer startX, Integer startY, Integer endX, Integer endY) {
		this(characterID, targetClassID, condition, startX, startY, endX, endY);
		oldClassID = sourceClassID;
	}
}
