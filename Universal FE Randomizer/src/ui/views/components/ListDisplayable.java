package ui.views.components;

import java.util.Comparator;

public interface ListDisplayable {
	public String displayString();
	
	public static Comparator<ListDisplayable> displayableComparator = new Comparator<ListDisplayable>() {
		@Override
		public int compare(ListDisplayable o1, ListDisplayable o2) {
			return o1.displayString().compareTo(o2.displayString());
		}
	};
}
