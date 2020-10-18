package fedata.gba;

import util.DiffCompiler;
import util.FreeSpaceManager;

public interface GBAFESpellAnimationCollection {
	
	// TODO: Figure out what can be shared between all games.
	public int getAnimationValueForID(int itemID);
	public void setAnimationValueForID(int itemID, int animationValue);
	
	public void addAnimation(int itemID, int numberOfCharacters, int animationValue, int colorValue);
	
	public void commit();
	public void compileDiffs(DiffCompiler compiler, FreeSpaceManager freeSpace);

}
