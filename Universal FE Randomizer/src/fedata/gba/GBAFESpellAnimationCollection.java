package fedata.gba;

import util.DiffCompiler;

public interface GBAFESpellAnimationCollection {
	
	// TODO: Figure out what can be shared between all games.
	public int getAnimationValueForID(int itemID);
	public void setAnimationValueForID(int itemID, int animationValue);
	
	public void commit();
	public void compileDiffs(DiffCompiler compiler);

}
