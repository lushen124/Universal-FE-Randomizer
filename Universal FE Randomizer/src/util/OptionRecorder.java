package util;

import java.util.prefs.Preferences;

import com.google.gson.Gson;

import fedata.general.FEBase;
import ui.fe4.FE4ClassOptions;
import ui.fe4.FE4EnemyBuffOptions;
import ui.fe4.FE4PromotionOptions;
import ui.fe4.HolyBloodOptions;
import ui.fe4.SkillsOptions;
import ui.fe9.FE9SkillsOptions;
import ui.model.BaseOptions;
import ui.model.ClassOptions;
import ui.model.EnemyOptions;
import ui.model.GrowthOptions;
import ui.model.ItemAssignmentOptions;
import ui.model.MiscellaneousOptions;
import ui.model.OtherCharacterOptions;
import ui.model.RecruitmentOptions;
import ui.model.WeaponOptions;

public class OptionRecorder {
	
	private static final Integer FE4OptionBundleVersion = 2;
	private static final Integer GBAOptionBundleVersion = 2;
	private static final Integer FE9OptionBundleVersion = 1;
	
	public static class AllOptions {
		public FE4OptionBundle fe4;
		public GBAOptionBundle fe6;
		public GBAOptionBundle fe7;
		public GBAOptionBundle fe8;
		public FE9OptionBundle fe9;
	}
	
	public static class GBAOptionBundle {
		public GrowthOptions growths;
		public BaseOptions bases;
		public ClassOptions classes;
		public WeaponOptions weapons;
		public OtherCharacterOptions other;
		public EnemyOptions enemies;
		public MiscellaneousOptions otherOptions;
		public RecruitmentOptions recruitmentOptions;
		public ItemAssignmentOptions itemAssignmentOptions;
		public String seed;
		public Integer version;
	}
	
	public static class FE4OptionBundle {
		public GrowthOptions growths;
		public BaseOptions bases;
		public HolyBloodOptions holyBlood;
		public SkillsOptions skills;
		public FE4ClassOptions classes;
		public FE4PromotionOptions promo;
		public FE4EnemyBuffOptions enemyBuff;
		public MiscellaneousOptions misc;
		public String seed;
		public Integer version;
	}
	
	public static class FE9OptionBundle {
		public GrowthOptions growths;
		public BaseOptions bases;
		public FE9SkillsOptions skills;
		public Integer version;
	}
	
	public static AllOptions options = loadOptions();
	
	private static final String SettingsKey = "saved_settings";
	
	private static AllOptions loadOptions() {
		Preferences prefs = Preferences.userRoot().node(OptionRecorder.class.getName());
		String jsonString = prefs.get(SettingsKey, null);
		if (jsonString != null) {
			Gson gson = new Gson();
			AllOptions loadedOptions = gson.fromJson(jsonString, AllOptions.class);
			// Version check.
			if (loadedOptions.fe4 != null && FE4OptionBundleVersion != loadedOptions.fe4.version) { loadedOptions.fe4 = null; }
			if (loadedOptions.fe6 != null && GBAOptionBundleVersion != loadedOptions.fe6.version) { loadedOptions.fe6 = null; }
			if (loadedOptions.fe7 != null && GBAOptionBundleVersion != loadedOptions.fe7.version) { loadedOptions.fe7 = null; }
			if (loadedOptions.fe8 != null && GBAOptionBundleVersion != loadedOptions.fe8.version) { loadedOptions.fe8 = null; }
			if (loadedOptions.fe9 != null && FE9OptionBundleVersion != loadedOptions.fe9.version) { loadedOptions.fe9 = null; }
			return loadedOptions;
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
			FE4ClassOptions classOptions, FE4PromotionOptions promoOptions, FE4EnemyBuffOptions buffOptions, MiscellaneousOptions miscOptions, String seed) {
		FE4OptionBundle bundle = new FE4OptionBundle();
		bundle.growths = growthOptions;
		bundle.bases = basesOptions;
		bundle.holyBlood = bloodOptions;
		bundle.skills = skillOptions;
		bundle.classes = classOptions;
		bundle.promo = promoOptions;
		bundle.enemyBuff = buffOptions;
		bundle.misc = miscOptions;
		bundle.seed = seed;
		bundle.version = FE4OptionBundleVersion;
		
		options.fe4 = bundle;
		
		saveOptions();
	}
	
	public static void recordGBAFEOptions(FEBase.GameType gameType, GrowthOptions growths, BaseOptions bases, ClassOptions classes, WeaponOptions weapons,
			OtherCharacterOptions other, EnemyOptions enemies, MiscellaneousOptions otherOptions, RecruitmentOptions recruitment, ItemAssignmentOptions itemAssignment, String seed) {
		GBAOptionBundle bundle = new GBAOptionBundle();
		bundle.growths = growths;
		bundle.bases = bases;
		bundle.classes = classes;
		bundle.weapons = weapons;
		bundle.other = other;
		bundle.enemies = enemies;
		bundle.otherOptions = otherOptions;
		bundle.recruitmentOptions = recruitment;
		bundle.itemAssignmentOptions = itemAssignment;
		bundle.seed = seed;
		bundle.version = GBAOptionBundleVersion;
		
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
