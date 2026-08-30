package fedata.gba.general;

import java.util.Set;

public interface GBAFEShop {
	
	public enum GameStage {
		EARLY, MID, LATE
	}
	
	public long getPointerOffset();
	public long getOriginalShopAddress();
	public Set<GBAFEShop> groupedShops();

	public GameStage getGameStage();
}
