package fedata.gba.general;

public class CharacterNudge {
	int characterID;
	
	int oldX;
	int oldY;
	
	int newX;
	int newY;
	
	int movementId;
	
	public CharacterNudge(int charID, int oldX, int oldY, int newX, int newY) {
		characterID = charID;
		this.oldX = oldX;
		this.oldY = oldY;
		this.newX = newX;
		this.newY = newY;
		this.movementId = -1;
	}
	public CharacterNudge(int charID, int oldX, int oldY, int newX, int newY, int movementId) {
		characterID = charID;
		this.oldX = oldX;
		this.oldY = oldY;
		this.newX = newX;
		this.newY = newY;
		this.movementId = movementId;
	}
	
	public int getCharacterID() { return characterID; }
	public int getOldX() { return oldX; }
	public int getOldY() { return oldY; }
	public int getNewX() { return newX; }
	public int getNewY() { return newY; }
	public int getMovementId() { return movementId; }
}
