package fedata.gba.general;

public class CharacterNudge {
	public enum Condition {
		ALWAYS, ONLY_IF_NOT_FLIER
	}
	
	public final int characterID;
	public final Condition nudgeCondition;
	
	// Optional (null if lazy), but serves as a safeguard to make sure we're changing the right values.
	// Also useful if you're targeting specific enemies that have the same character ID. Their start/end positions
	// would be different.
	public final Integer originalStartingX;
	public final Integer originalStartingY;
	public final Integer originalEndingX;
	public final Integer originalEndingY;
	
	// Null here means don't change that particular value from the original. All nulls is a no-op.
	public final Integer newStartingX;
	public final Integer newStartingY;
	public final Integer newEndingX;
	public final Integer newEndingY;
	
	public CharacterNudge(int charID, Condition nudgeCondition,
			Integer originalStartX, Integer originalStartY, Integer originalEndX, Integer originalEndY, 
			Integer newStartX, Integer newStartY, Integer newEndX, Integer newEndY) {
		characterID = charID;
		this.nudgeCondition = nudgeCondition;
		
		originalStartingX = originalStartX;
		originalStartingY = originalStartY;
		originalEndingX = originalEndX;
		originalEndingY = originalEndY;
		
		newStartingX = newStartX;
		newStartingY = newStartY;
		newEndingX = newEndX;
		newEndingY = newEndY;
	}
}
