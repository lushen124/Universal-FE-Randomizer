package fedata.general;

public class FEBase {

	public enum GameType {
		UNKNOWN, FE4, FE6, FE7, FE8;
		
		public boolean isGBA() {
			switch (this) {
			case FE6:
			case FE7:
			case FE8:
				return true;
			default:
				return false;
			}
		}
		
		public boolean isSFC() {
			switch(this) {
			case FE4:
				return true;
			default:
				return false;
			}
		}
	}
}
