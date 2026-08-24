package fedata.gba.general;

import java.util.Comparator;

import ui.views.components.ListDisplayable;

public interface GBAFEClass extends ListDisplayable {
	
	public static Comparator<GBAFEClass> idComparator = new Comparator<GBAFEClass>() {
		@Override
		public int compare(GBAFEClass o1, GBAFEClass o2) {
			return Integer.compare(o1.getID(), o2.getID());
		}
	};
	
	public int getID();
	
	public Boolean isLord();
	public Boolean isThief();
	public Boolean canDestroyVillages();
	public Boolean isFemale();
	public Boolean isPromoted();
	public Boolean canAttack();
	public String name();
	
	public String displayName();
	
	default String displayString() {
		return displayName();
	}
}
