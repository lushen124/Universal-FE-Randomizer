package util;

import java.util.prefs.Preferences;

import com.google.gson.Gson;

import fedata.general.FEBase;
import ui.fe4.FE4ClassOptions;
import ui.fe4.HolyBloodOptions;
import ui.fe4.SkillsOptions;
import ui.model.BaseOptions;
import ui.model.ClassOptions;
import ui.model.EnemyOptions;
import ui.model.GrowthOptions;
import ui.model.MiscellaneousOptions;
import ui.model.OtherCharacterOptions;
import ui.model.WeaponOptions;

public class OptionRecorder {
	
	public static class AllOptions {
		public FE4OptionBundle fe4;
		public GBAOptionBundle fe6;
		public GBAOptionBundle fe7;
		public GBAOptionBundle fe8;
	}
	
	public static class GBAOptionBundle {
		public GrowthOptions growths;
		public BaseOptions bases;
		public ClassOptions classes;
		public WeaponOptions weapons;
		public OtherCharacterOptions other;
		public EnemyOptions enemies;
		public MiscellaneousOptions otherOptions;
		public String seed;
	}
	
	public static class FE4OptionBundle {
		public GrowthOptions growths;
		public BaseOptions bases;
		public HolyBloodOptions holyBlood;
		public SkillsOptions skills;
		public FE4ClassOptions classes;
		public MiscellaneousOptions misc;
		public String seed;
	}
	
	public static AllOptions options = loadOptions();
	
	private static final String SettingsKey = "saved_settings";
	
	private static AllOptions loadOptions() {
		Preferences prefs = Preferences.userRoot().node(OptionRecorder.class.getName());
		String jsonString = prefs.get(SettingsKey, null);
		if (jsonString != null) {
			Gson gson = new Gson();
			return gson.fromJson(jsonString, AllOptions.class);
		} else {
			return new AllOptions();
		}
	}
	
	private static void saveOptions() {
		Gson gson = new Gson();
		String jsonString = gson.toJson(options);
		Preferences prefs = Preferences.userRoot().node(OptionRecorder.class.getName());
		prefs.put(SettingsKey, jsonString);
	}
	
	public static void recordFE4Options(GrowthOptions growthOptions, BaseOptions basesOptions, HolyBloodOptions bloodOptions, SkillsOptions skillOptions, 
			FE4ClassOptions classOptions, MiscellaneousOptions miscOptions, String seed) {
		FE4OptionBundle bundle = new FE4OptionBundle();
		bundle.growths = growthOptions;
		bundle.bases = basesOptions;
		bundle.holyBlood = bloodOptions;
		bundle.skills = skillOptions;
		bundle.classes = classOptions;
		bundle.misc = miscOptions;
		bundle.seed = seed;
		
		options.fe4 = bundle;
		
		saveOptions();
	}
	
	public static void recordGBAFEOptions(FEBase.GameType gameType, GrowthOptions growths, BaseOptions bases, ClassOptions classes, WeaponOptions weapons,
			OtherCharacterOptions other, EnemyOptions enemies, MiscellaneousOptions otherOptions, String seed) {
		GBAOptionBundle bundle = new GBAOptionBundle();
		bundle.growths = growths;
		bundle.bases = bases;
		bundle.classes = classes;
		bundle.weapons = weapons;
		bundle.other = other;
		bundle.enemies = enemies;
		bundle.otherOptions = otherOptions;
		bundle.seed = seed;
		
		switch (gameType) {
		case FE6:
			options.fe6 = bundle;
			break;
		case FE7:
			options.fe7 = bundle;
			break;
		case FE8:
			options.fe8 = bundle;
			break;
		default:
			return;
		}
		
		saveOptions();
	}

}
