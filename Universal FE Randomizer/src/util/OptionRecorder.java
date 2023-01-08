package util;

import java.util.prefs.Preferences;

import com.google.gson.Gson;

import fedata.general.FEBase;
import ui.fe4.FE4ClassOptions;
import ui.fe4.FE4EnemyBuffOptions;
import ui.fe4.FE4PromotionOptions;
import ui.fe4.HolyBloodOptions;
import ui.fe4.SkillsOptions;
import ui.fe9.FE9ClassOptions;
import ui.fe9.FE9SkillsOptions;
import ui.model.BaseOptions;
import ui.model.CharacterShufflingOptions;
import ui.model.ClassOptions;
import ui.model.EnemyOptions;
import ui.model.FE9EnemyBuffOptions;
import ui.model.FE9OtherCharacterOptions;
import ui.model.GrowthOptions;
import ui.model.ItemAssignmentOptions;
import ui.model.MiscellaneousOptions;
import ui.model.OtherCharacterOptions;
import ui.model.RecruitmentOptions;
import ui.model.WeaponOptions;

public class OptionRecorder {
	private static final Integer FE4OptionBundleVersion = 5;
	private static final Integer GBAOptionBundleVersion = 13;
	private static final Integer FE9OptionBundleVersion = 12;
	
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
		public CharacterShufflingOptions characterShufflingOptions;
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
		public FE9OtherCharacterOptions otherOptions;
		public FE9EnemyBuffOptions enemyBuff;
		public FE9ClassOptions classes;
		public WeaponOptions weapons;
		public MiscellaneousOptions misc;
		public String seed;
		public Integer version;
	}
	
	public static AllOptions options = loadOptions();
	
	private static final String SettingsKey = "saved_settings";
	
	private static final String FE4Suffix = "_fe4";
	private static final String FE6Suffix = "_fe6";
	private static final String FE7Suffix = "_fe7";
	private static final String FE8Suffix = "_fe8";
	private static final String FE9Suffix = "_fe9";
	
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
			
			// Migrate to pieced JSON.
			prefs.remove(SettingsKey);
			saveOptions(loadedOptions);
			
			return loadedOptions;
		} else {
			AllOptions options = new AllOptions();
			options.fe4 = loadFE4Options();
			options.fe6 = loadFE6Options();
			options.fe7 = loadFE7Options();
			options.fe8 = loadFE8Options();
			options.fe9 = loadFE9Options();
			return options;
		}
	}
	
	private static FE4OptionBundle loadFE4Options() {
		Preferences prefs = Preferences.userRoot().node(OptionRecorder.class.getName());
		String jsonString = prefs.get(SettingsKey + FE4Suffix, null);
		if (jsonString != null) {
			Gson gson = new Gson();

			FE4OptionBundle loadedOptions = null;
			try {
				loadedOptions = gson.fromJson(jsonString, FE4OptionBundle.class);
			} catch (Exception e) {
				return null;
			}
			return FE4OptionBundleVersion != loadedOptions.version ? null : loadedOptions;
		}
		
		return null;
	}
	
	private static GBAOptionBundle loadFE6Options() {
		Preferences prefs = Preferences.userRoot().node(OptionRecorder.class.getName());
		String jsonString = prefs.get(SettingsKey + FE6Suffix, null);
		if (jsonString != null) {
			Gson gson = new Gson();
			
			GBAOptionBundle loadedOptions;
			try {
				loadedOptions = gson.fromJson(jsonString, GBAOptionBundle.class);
			} catch (Exception e) {
				return null;
			}
			return GBAOptionBundleVersion != loadedOptions.version ? null : loadedOptions;
		}
		
		return null;
	}
	
	private static GBAOptionBundle loadFE7Options() {
		Preferences prefs = Preferences.userRoot().node(OptionRecorder.class.getName());
		String jsonString = prefs.get(SettingsKey + FE7Suffix, null);
		if (jsonString != null) {
			Gson gson = new Gson();
			GBAOptionBundle loadedOptions;
			try {
				loadedOptions = gson.fromJson(jsonString, GBAOptionBundle.class);
			} catch (Exception e) {
				return null;
			}
			return GBAOptionBundleVersion != loadedOptions.version ? null : loadedOptions;
		}
		
		return null;
	}
	
	private static GBAOptionBundle loadFE8Options() {
		Preferences prefs = Preferences.userRoot().node(OptionRecorder.class.getName());
		String jsonString = prefs.get(SettingsKey + FE8Suffix, null);
		if (jsonString != null) {
			Gson gson = new Gson();
			GBAOptionBundle loadedOptions;
			try {
				loadedOptions = gson.fromJson(jsonString, GBAOptionBundle.class);
			} catch (Exception e) {
				return null;
			}
			return GBAOptionBundleVersion != loadedOptions.version ? null : loadedOptions;
		}
		
		return null;
	}
	
	private static FE9OptionBundle loadFE9Options() {
		Preferences prefs = Preferences.userRoot().node(OptionRecorder.class.getName());
		String jsonString = prefs.get(SettingsKey + FE9Suffix, null);
		if (jsonString != null) {
			Gson gson = new Gson();
			FE9OptionBundle loadedOptions;
			try {
				loadedOptions = gson.fromJson(jsonString, FE9OptionBundle.class);
			} catch (Exception e) {
				return null;
			}
			return FE9OptionBundleVersion != loadedOptions.version ? null : loadedOptions;
		}
		
		return null;
	}
	
	private static void saveOptions(AllOptions options) {
		Gson gson = new Gson();
		Preferences prefs = Preferences.userRoot().node(OptionRecorder.class.getName());
		
		if (options.fe4 != null) {
			String fe4String = gson.toJson(options.fe4);
			prefs.put(SettingsKey + FE4Suffix, fe4String);
		}
		if (options.fe6 != null) {
			String fe6String = gson.toJson(options.fe6);
			prefs.put(SettingsKey + FE6Suffix, fe6String);
		}
		if (options.fe7 != null) {
			String fe7String = gson.toJson(options.fe7);
			prefs.put(SettingsKey + FE7Suffix, fe7String);
		}
		if (options.fe8 != null) {
			String fe8String = gson.toJson(options.fe8);
			prefs.put(SettingsKey + FE8Suffix, fe8String);
		}
		if (options.fe9 != null) {
			String fe9String = gson.toJson(options.fe9);
			prefs.put(SettingsKey + FE9Suffix, fe9String);
		}
	}
	
	public static void recordFE9Options(GrowthOptions growthOptions, BaseOptions baseOptions, FE9SkillsOptions skillOptions,
			FE9OtherCharacterOptions otherOptions, FE9EnemyBuffOptions buffOptions, FE9ClassOptions classOptions, WeaponOptions weaponOptions,
			MiscellaneousOptions miscOptions, String seed) {
		FE9OptionBundle bundle = new FE9OptionBundle();
		bundle.growths = growthOptions;
		bundle.bases = baseOptions;
		bundle.skills = skillOptions;
		bundle.otherOptions = otherOptions;
		bundle.enemyBuff = buffOptions;
		bundle.classes = classOptions;
		bundle.weapons = weaponOptions;
		bundle.misc = miscOptions;
		bundle.seed = seed;
		bundle.version = FE9OptionBundleVersion;
		
		options.fe9 = bundle;
		
		saveOptions(options);
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
		
		saveOptions(options);
	}
	
	public static void recordGBAFEOptions(FEBase.GameType gameType, GrowthOptions growths, BaseOptions bases, ClassOptions classes, WeaponOptions weapons,
			OtherCharacterOptions other, EnemyOptions enemies, MiscellaneousOptions otherOptions, RecruitmentOptions recruitment, ItemAssignmentOptions itemAssignment, CharacterShufflingOptions shufflingOptions, String seed) {
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
		bundle.characterShufflingOptions = shufflingOptions;
		
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
		
		saveOptions(options);
	}

}
