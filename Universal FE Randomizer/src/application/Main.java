package application;

import org.eclipse.swt.widgets.*;

import ui.MainView;
import util.GBAImageCodec;
import util.WhyDoesJavaNotHaveThese;

public class Main {
	
	static Display mainDisplay;
	static MainView mainView;

	public static void main(String[] args) {
		
		System.out.println("Earth Splitter: " + WhyDoesJavaNotHaveThese.displayStringForBytes(GBAImageCodec.getGBAGraphicsDataForImage("weaponIcons/EarthSplitter.png", GBAImageCodec.gbaWeaponColorPalette)));
		System.out.println("Gust Shot: " + WhyDoesJavaNotHaveThese.displayStringForBytes(GBAImageCodec.getGBAGraphicsDataForImage("weaponIcons/GustShot.png", GBAImageCodec.gbaWeaponColorPalette)));
		System.out.println("Fierce Flame: " + WhyDoesJavaNotHaveThese.displayStringForBytes(GBAImageCodec.getGBAGraphicsDataForImage("weaponIcons/FierceFlame.png", GBAImageCodec.gbaWeaponColorPalette)));
		System.out.println("Dark Miasma: " + WhyDoesJavaNotHaveThese.displayStringForBytes(GBAImageCodec.getGBAGraphicsDataForImage("weaponIcons/DarkMiasma.png", GBAImageCodec.gbaWeaponColorPalette)));
		System.out.println("Holy Light: " + WhyDoesJavaNotHaveThese.displayStringForBytes(GBAImageCodec.getGBAGraphicsDataForImage("weaponIcons/HolyLight.png", GBAImageCodec.gbaWeaponColorPalette)));
		
		 /* Instantiate Display object, it represents SWT session */
		  mainDisplay = new Display();

		  mainView = new MainView(mainDisplay);
		  

		  while (!mainView.mainShell.isDisposed()) {
		   if (!mainDisplay.readAndDispatch())
			   mainDisplay.sleep();
		  }

		  /* Dispose the display */
		  mainDisplay.dispose();
	}

}
