# Yune: A Universal Fire Emblem Randomizer

# Latest Version: [0.9.3](https://github.com/lushen124/Universal-FE-Randomizer/releases/tag/0.9.3)

## Introduction

Properly universal this time. Those of you that used the original may recall that it supported all the GBA FEs, but was locked to Windows machines, mostly because I wrote it in Visual Basic, which has zero cross compatability. This time around, we're taking the opposite approach and starting with a single game first, but working on the three major desktop platforms.

And hey, name change! It's actually perfect in a lot of ways because it encapsulates several of my favorites. Alongside Fire Emblem, I'm also a fan of Love Live! with my favorite girls being Minami Kotori in μ's and Takami Chika in Aqours. Kotori's symbol reflects her name: Little Bird. Chika's associated color is Orange, alongside her symbol, a Mikan (tangerine). Combine these two to get an Orange Little Bird, which leads me to my favorite Fire Emblem character, which, if you couldn't tell already, was Micaiah from Radiant Dawn. Yune is the name of the orange little bird she has with her in most of her art. And without wishing to spoil those who haven't played Radiant Dawn, there is another way Yune is fitting for the name of a randomizer, but I'll leave that to you, because if you haven't played Radiant Dawn, you really should.

<p align="center">
  <img src="https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Universal%20FE%20Randomizer/res/YuneIcon.png">
</p>
<p align="center">
  <i>That's Yune!</i>
</p>

## Compatability
* <a href="#fe4">Fire Emblem: Genealogy of the Holy War (aka FE4 or ファイアーエムブレム　聖戦の系譜)</a>
* <a href="#gbafe">Fire Emblem: Binding Blade (aka FE6 or ファイアーエムブレム　封印の剣)</a>
* <a href="#gbafe">Fire Emblem: Blazing Sword (aka FE7 or ファイアーエムブレム 烈火の剣)</a>
* <a href="#gbafe">Fire Emblem: The Sacred Stones (aka FE8 or ファイアーエムブレム 聖魔の光石)</a>
* <a href="#fe9">Fire Emblem: Path of Radiance (aka FE9 or ファイアーエムブレム 蒼炎の軌)</a>

Note that FE4 and FE6 require clean JP versions of those games. The randomizer will do a cheksum comparison to make sure of this. A Checksum failure error indicates an altered or otherwise invalid file. Additionally, FE4 is ok with either a Headered version of the ROM or an Unheadered version.

FE7 and FE8 currently require US versions of those games. Like above, a cheksum comparison will be performed to make sure the game is valid for randomization.

FE9 requires the US version of the ISO. You do not need to extract any part of the ISO as the randomizer is capable of reading the file system directly and modifying and rebuilding the ISO. Small caveat: I've run into out of memory issues when randomizing FE9 with a 32-bit JRE. If you are on a 32-bit JRE, you will receive a warning when loading FE9 that recommends you update to a 64-bit JRE. Even if I optimize FE9 to work on a 32-bit JRE, FE10 will almost certainly bust the memory limit.

## Installation
The list of releases can always be found here: https://github.com/lushen124/Universal-FE-Randomizer/releases.

### Windows

**Requirements**: JRE >= 1.8.0. I was thinking about bundling this into the executable, but that's just a massive waste of space, and most of you probably can get JRE relatively easily. And if you can't, here you to: http://www.oracle.com/technetwork/java/javase/downloads/index.html. I've only tested this with Windows 10, but I don't see why older versions of Windows would be excluded so long as you have JRE.

Important note: There are two versions of the JAR and binary. **The one you need depends on the version of JRE you have installed and NOT the version of Windows you have installed.** That is to say, you can have a 64-bit version of Windows but still be running an x86 (32-bit) version of JRE. I recommend you update your JRE to match the architecture for your Windows installation, but if you want to retain your x86 JRE, use the x86 version. Chances are, if you see a splash screen and then nothing happens, you have the wrong version. Also, if you're trying to randomize FE9, a 64-bit JRE is required, as there isn't enough memory to randomize FE9 with 32-bit JREs.

### MacOS

The ZIP file will extract an actual bundle that can be double clicked and run like usual (after any security settings that may try to stop you). If you don't know, MacOS apps are actually a folder in disguise, which is why Github shows it like one. If, for some reason, you want to run the raw JAR, you need to specify the `-XstartOnFirstThread` flag. For example:

```
java -jar -XstartOnFirstThread Yune\ -\ MacOS.jar
```

**Requirements**: Like Windows, you need a JRE to run it, though most versions of MacOS have one included that should be sufficient. If it doesn't work, go ahead and grab the newest one at http://www.oracle.com/technetwork/java/javase/downloads/index.html. Tested with OS X 10.13 (High Sierra). Unlike Windows, if you have any Mac from the last 8 years, you should have a 64-bit machine (Mac OS X 10.7 Lion and above).

### GTK

You will need GTK which is in most linux distros, as well as a way of running a java file. This is why you install the jre. 

```
sudo apt-get update
```

```
sudo apt-get install default-jre
```

Go to the file location of the GTK(x86_64).jar jar file and then run it with

``` 
java -jar 'Yune - GTK(x86_64).jar'

```

...or something like that. Tested with Ubuntu 16.04.4.

Also, you may need to `chmod` it so that it's runnable. That can be done with

```
chmod 777 'Yune\ -\ GTK(x86_64).jar'
```

# Usage

Usage is pretty straightforward. Once you've downloaded the app for your platform and you launch it, you should be greeted with a relatively unassuming box with a single button.

![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Startup.png)

Once you click on "Browse", you'll be taken to a File Open dialog that allows you to select a file. Yune looks for files with the common extensions for the supported games. That is, SFC and SMC for FE4, GBA for FE6, 7, 8, and ISO for FE9. If you have a different extension, you can disable the filter to show all files in the bottom right corner of the dialog window.

![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/GameSelect.png)

Once you select your file, if the file is supported (which Yune determines by checking the file's CRC-32 hash), then the main window will populate itself with the info of the ROM/ISO as well as the appropriate options for the game that was detected. Simply choose how you wish to randomize the game and click Randomize in the bottom right corner once you're ready.

![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Options.png)

Once you click on Randomize, Yune will prompt for a new file name to save as. If you choose the same filename as the original file, Yune will create a file with the same name with "(Randomized)" appended to it to avoid replacing your original file. Once you choose the name, Yune will start randomizing and show you a status bar to let you know things are working.

![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Progress.png)

Once it finishes, you'll be notified and then asked if you would like to save a changelog to let you know all of the changes made to the game. 

![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Finish.png)

The changelog is in an HTML format and contains data about the settings you used and character, class, item, and chapter data for the game (sections depending on the game), alongside their original values and their new values. But beyond that, that's it! Launch your favorite emulator for your game and give it a spin! :)

# Randomization Options

## <div id="fe4">Fire Emblem: Genealogy of the Holy War</div>

### Growths
Randomizes the growths of each character. This only involves a character's personal growths. For characters that have holy blood of some kind, their growths are boosted from the holy blood, but that boost is not accounted for here. There are three modes of randomization:

* **Redistribute** - Sums up the character's total growths and randomly redistributes it across all stat areas. Optionally applies variance to the sum.
* **Delta** - Applies a random delta to each stat area's growth, adding or subtracting an amount up to the variance specified.
* **Full** - Discards the character's usual growth rate and generates completely new ones between the specified range. 

On top of that, there are two additional options that can be applied.

* **Adjust STR/MAG by Class** - Since this game separates Strength and Magic into different stats, this option will swap a character's STR/MAG stats to ensure that the stat with the higher growth is on the character's primary attack stat. In other words, physical attackers are guaranteed to have a greater or equal growth rate for Strength than Magic, and vice versa for magic users.
* **Adjust HP Growths** - This option gives HP growths additional weight when redistributing growths and ensures HP growths are in the upper half of the range when fully randomizing growths. This option does nothing when the randomization mode is Delta.

*Note: In order to prevent issues with overflow during inheritence, the maximum growth rate for any character in any area is 85%.*

### Bases
Randomizes the base stats for each character. A character's effective bases are determined by the sum of their personal bases and the bases for their class. This randomization option only involves a character's personal bases. There are two modes of randomization:

* **Redistribute** - Sums up the character's total personal bases and randomly redistributes it across all stat areas. HP is **not** included in this, as there is no contribution from the class when determining a character's effective base HP. Optionally applies a variance to the sum of growths for a character.
* **Delta** - Applies a random delta to each stat area, adding or subtracting a random amount up to the variance specified.

On top of that, one more option is available in both cases.

* **Adjust STR/MAG by Class** - Much like with growths, this option will ensure that the character's primary attacking stat will be higher than or equal to the secondary attacking stat.

### Holy Blood

* **Randomize Growth Bonuses** - Holy blood grants its carrier bonuses in their growth rates, independent from the character's personal growths. This option randomly redistributes the bonus growth areas for each holy blood, redistributing up to the growth amount specified between all growth areas. The bonuses are doled out in chunks, and **Chunk Size** can be adjusted. Larger chunks result in more concentrated bonuses, while smaller chunks are more likely to give a spread (most of the time). Additionally, an optional **HP Baseline** can be given so that HP bonuses are more consistent (though some bloods may also get additional HP bonuses on top of that.
  * **Generate Unique Bonuses** - This option attempts to re-roll bloods that are deemed too similar to each other so that all bloods have a more unique set of bonuses. It does this by ordering the bonus stat areas from highest to lowest and considers any bloods that have the same ordering as similar enough to be re-rolled.
  * **STR/MAG Options** - By default, Yune does avoid giving STR to magical bloods and MAG to physical bloods, but this can now be toggled.
* **Randomize Holy Weapon Bonuses** - Characters with Major holy blood gain access to holy weapons, depending on the blood that they have. These weapons generally give large stat bonuses when equipped. This option redistributes the bonuses conferred to the user of each type of holy weapon.
* **Assign Holy Blood to Playable Characters** - This option randomly assigns holy blood to playable characters. The type of blood can be limited to a set that matches their class's weapon usage if the **Match Blood to Weapon Usage** option is checked. The chance of receiving Major, Minor, or no Holy Blood can also be customized. 

### Skills
Modifies the skills each character has. There are two modes of assigning skills.

* **Shuffle** - Uses the pool of skills normally available between the characters and simply shuffles them around.
  * **Separate Pools by Generation** - Shuffles skills only between characters of the same generation. 
* **Randomize** - Assigns skills at random using the specified weights for skills. Specific skills can be enabled, disabled, or weighted higher or lower than other skills. Note that these are specifying weights of a total, so it's the relation to other skills that counts. Pursuit is also treated separately from other skills, due to the important nature of it. It has its own independent chance of showing up on playable characters. A chance of 100% means all playable characters will receive Pursuit as a skill (as long as they are eligible to receive a skill).

Regardless of the option above, the number of skills a character receives can also be set to either match their normal amount of skills (e.g. Alec will always have two skills, Sigurd will always have one skill, Claud will always have no skills, etc), or the be randomly determined in the case of randomizing skills, or not be limited in the case of shuffling skills, as dictated by the **Retain Number of Skills** option. Much like the skill weights for randomizing skills, skill counts can be enabled, disabled, or any weight in between when randomizing skills. Much like skill weights, these weights are also only in relation to the other options.

*Note: Child units do not have any skill information defined. Their skills are entirely determined by their parents. For the purposes of the randomizer, Julia is **not** considered a child character.*

### Classes
Randomly assigns classes to units. Some universal restrictions are in place for this option. Namely, any unit that is normally a flier class will remain a flier class to maintain compatibility. Other restrictions are defined below.

#### Playable Characters
Randomizes character classes for all playable characters. By default, Lords (Sigurd and Seliph), Thieves (Dew, Patty, and Daisy), Dancers (Silvia, Lene, Laylea), and Julia are not randomized to avoid compatibility issues. Additionally, in order to make sure scripted events play out properly, Quan and Ethlyn will always be on horseback, and Quan will be restricted from any class that prevents Altena from flying (i.e. only able to use Lances and Swords).

**Include Lords** - Allows Sigurd and Seliph to be randomized. This option also adds Lord Knight and Junior Lord to the class pool.

**Include Thieves** - Allows Dew, Patty, and Daisy to be randomized. This option also adds Thief and Thief Fighter to the class pool.

**Include Dancers** - Allows Silvia, Lene, and Laylea to be randomized. This option also adds Dancer to the class pool, albeit limited to one dancer per generation. Note that this option will break Silvia's village event in Chapter 4, as it requires her to be a dancer.

**Include Julia** - Julia is a special case, as the endgame heavily relies on her with her holy weapon to reliably complete the game. Enabling this option removes the safeguard from the endgame.

**Retain Healers** - This option ensures Edain, Claud, Lana, Muirne, Coirpre, and Charlot can all still use staves. This provides a baseline of healers that you will have at least a couple of healers on the field in each generation.

**Retain Horseback Units** - By default, there is no weight on the classes selected. Since the maps in this game are quite large, this option restricts units that are normally on horseback to horseback classes in order to keep chapters moving at their normal speed.

**Children Options** - There are three options to dictate how children units are assigned their classes. Note that in all cases, substitute characters will have the same class as their child counterparts.

* **Match Parents (Strict)** - This option attempts to match children as closely as possible to their First Generation analogues. For example, in the base game, Edain's children are Lana and Lester, with Lana sharing a class with Edain herself and Lester sharing a class with Midir. Using this option will set Lana's class to match Edain's exactly and Lester's class to match Midir's exactly. 
* **Match Parents (Loose)** - Similar to above, except instead of matching the class exactly, it ensures that the child shares at least one weapon with his/her First Generation analogue. Using the same example above, if Edain were a Myrmidon and Midir were a Cavalier, then Lana would be any class that can use a sword and Lester would be any class that can use either a Sword or a Lance.
* **Randomize** - Child characters have entirely random classes.

**Holy Blood Options** - There are three options to dictate how holy blood is handled.

* **No Change** - Do not change anybody's holy blood. A character's class pool is restricted to classes that support that character's natural holy blood.

* **Shuffle** - This option is only available if bosses are allowed to be randomize, and setting this option will force the equivalent boss option to Shuffle as well. This option will shuffle holy blood assignment so that all instances of one blood will now be a different blood. For example, Sigurd has Major Baldr blood and Ethlyn has Minor Baldr blood. With this option, blood can be changed, but in all cases, Sigurd will always have the Major version of that blood and Ethlyn will always have the minor version of the same blood.

* **Randomize** - Allows characters to change their holy blood. The character's class is not restricted and their holy blood will be adjusted to match their new class.

**Shop Options** - Determines how weapon shop items are set. Three options are available. Note that this primarily affects the first generation. The second generation shops largely are determined by your inventory from generation 1.

* **No Change** - Do not change the shop items.
* **Adjust to Party** - Adjusts shop items based on the classes of the characters you would normally have in each chapter. Shop items are generally scaled based on the chapter they show up, with more powerful weapons only showing up in later shops.
* **Randomize** - Exactly as it says: randomly assigns weapons to the shops. No logic is applied to scale shops based on chapter.

**Adjust Conversation Gifts** - If this option is enabled, updates what items characters give to other characters when talking to them to match their new class. Only affects conversations in which characters normally gain items.

**Adjust STR/MAG Growths and Bases** - If a character changes from a physical-only attacker to a magic-only attacker, this option will swap their Strength and Magic stats so that their primary attacking stat is the higher stat. Note that hybrid classes that use at least one physical weapon and one magical weapon (including staves) will not swap their stats.

**Weapon Assignment** - Determines how weapons are assigned after a character changes his/her class. There are three options.

* **Sidegrade (Strict)** - Uses the closest counterpart to the original weapon they had for their new class, where possible. When not possible, falls back to the loose sidegrade.
* **Sidegrade (Loose)** - Uses any weapon that matches the rank of their original weapon.
* **Randomize** - Uses any weapon that the character can normally use, based on their class and holy blood.

#### Regular Enemies
Randomizes the minor generic enemies throughout the game. Due to the way the game is designed, these enemies are randomized in blocks, as whenever the game spawns a platoon of enemies, they are all identical.

#### Arena Enemies
Randomizes the enemies that show up in the arena for each chapter. Whether each character is a melee character or a ranged character remains unchanged. Additional logic is applied to avoid having more advanced classes in earlier chapters and in earlier levels of the arena, though this is by no means guaranteed. Arena enemies are assigned random weapons, though there is also logic to avoid stronger weapons in the earlier levels.

#### Bosses
Randomizes the bosses that show up throughout the game, where a boss is defined as any non-recruitable unit with a portrait. Additionally regular bosses are defined as those that have no special holy blood or skills. Those bosses that do have holy blood or skills beyond their class skills are defined as Holy Bosses. 

**Holy Blood Options** - The sub-option here is similar to the same option in playable characters randomization. **No Change** will restrict classes to those that support the boss's natural holy blood. **Randomize** unlocks the restriction and picks a holy blood that matches the class. **Shuffle** is only available if playable characters are randomized and selecting this option will force the playable character holy blood option to use the Shuffle option as well. It does the same as mentioned above, shuffling all instances of each holy blood to a different, consistent blood.

### Promotions
There are three options for determining promotions.

* **Default Promotions** - Promotions are set based on the normal promotion path the class has.
* **Similar Promotions** - Promotions are set to any class nothing is lost on promotion. This includes weapon ranks/usage, class skills, and movement types/ranges.
  * **Allow Mount Change** - This option allows horseback units to promote into fliers and vice versa.
  * **Allow Enemy-only Promotions** - This option allows units to promote into high-level enemy-only classes, including Baron, Emperor, and Queen.
* **Random Promotions** - Promotions are set randomly, with no regard to the unpromoted class.
  * **Requires Common Weapon** - Restrict the promotion options to those that share at least one weapon type with the unpromoted class.

### Buff Enemies
These options all make the game harder by increasing enemy stats, improving their weapons, or granting holy blood and weapons.

**Improve Enemy Stats** - Increases the class growth rate, which increases the effective stats generic enemies have throughout the game. This also directly increases the base stats for boss characters. There are two methods to increase stats.

* **Flat** - For generic enemies, this option adds a flat amount to growth rates. For bosses, this option increases their base stats in all areas by 1 for every 10% (increases HP by 2 instead of 1 for every 10%).
* **Scaling** - For generic enemies, this option multiplies their growth in each stat area by (100 + value)%. For bosses, this option multiplies their base stat by (100 + value)% for each area.

**Improve Enemy Equipment** - Gives generic enemies a chance to have more interesting or powerful weapons than they normally have. This option has no effect on boss equipment.

**Force Major Blood and Holy Weapon** - This option only has an effect on holy bosses (bosses that normally have either holy blood or personal skills). For bosses without holy blood, this option assigns them Major Blood based on their class. For bosses with minor holy blood, this option promotes their minor blood to major holy blood. For all holy bosses, gives them the holy weapon corresponding to their major holy blood.

### Miscellaneous
**Apply English Patch** - Applies the English translation patch found [here](https://serenesforest.net/forums/index.php?/topic/63676-fe4-translation-patch-open-beta-v7/).

**Randomize Rings** - Replaces all of the rings found in the game with a random ring.

## <div id="gbafe">Fire Emblem: Binding Blade, Fire Emblem: Blazing Sword, Fire Emblem: The Sacred Stones</div>

### Growths
I made some illustrations to show what's going on here.

**Redistribution** - This method simply sums a character's growth percentages and doles them back out randomly, which attempts to keep high growth characters with high growths, just in random areas. A variance is allowed to add or subtract a random amount from their totals before redistributing, offering some differences.

<p align="center">
  <img src="https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Readme/GrowthRedistribution.png">
</p>
<p align="center">
  <i>This example assumes variance of 0, so it's pure redistribution of growths.</i>
</p>

**Variance** - This method attempts to preserve character's original growths, but adds minor tweaks to each in either direction. For example, somebody with high STR growth will have either slightly more or slightly less, depending on the variance given and the random number generator.

<p align="center">
  <img src="https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Readme/GrowthDelta.png">
</p>
<p align="center">
  <i>This example assumes variance of 20, so each growth area can change by up to 20% in either direction.</i>
</p>

**Full** - This method has no attachment to the character's original growths and will simply assign growth values randomly between the specified minimum and maximum for all stat areas.

<p align="center">
  <img src="https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Readme/GrowthFull.png">
</p>
<p align="center">
  <i>It should go without saying that this is assuming a minimum of 5% and a maximum of 100%.</i>
</p>

### Bases
As you may know, a character's base stats are the sum of their personal bases and the bases afforded to them by their class.

<p align="center">
  <img src="https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Readme/Bases.png">
</p>
<p align="center">
  <i>The randomizer only touches the personal bases.</i>
</p>

**Redistribution** - Sums up the character's bases and randomly assigns them back out. Note that negative personal bases are not allowed here, so while the sum can be lowered from a character normally having a negative personal base, the personal bases that we end up with are strictly positive.

**Variance** - Adds (or subtracts) a random number up to the variance provided to all of a character's bases. Unlike redistribution, there is a chance of receiving negative personal bases here, though no character is allowed to have a negative base after applying their class bases in any area.

### Other Character Options

**Randomize CON** - CON (short for Constitution) is a stat that has a base value, but no growth. Each character has a personal CON that's added to the class's base CON to derive their final value. This option randomizes the character's personal CON using a variance. A random number is added or subtracted from their original personal CON and clamped at the minimum CON specified.

**Randomize MOV** - Movement range is set to a single class, and therefore, unlike the previous options, changes in MOV affect every unit in the class, friend and foe alike. For the purpose of this, Male versions are distinct from their Female counterparts when determining distinct classes. For example, a Male Paladin is considered a different class from a Female Paladin. With this option, it's possible that Male Paladins have only 5 MOV while Female Paladins may have 8 MOV.

**Randomize Affinity** - A relatively minor change. Affinity affects support bonuses that a character receives from supporting another unit. This option simply assigns random affinities to your characters, randomizing the support bonuses they receive from each unit.

### Weapons

**Randomize MT/Hit/WT/Durability** - This randomizes the main stats of every weapon in the game. All of these stats are randomized using a Minimum value, a Maximum value, and a Variance value. This basically adds or subtracts a random number up to the variance value to the stat affected, and then clamps the value between the specified minimum and maximum.

**Apply Random Effects** - This is a returning feature from the old randomizer, but with more granular controls. Simply put, for every weapon in the game, the randomizer will add one of the enabled options to it. If a weapon already had a special effect, it gains another one, if available. Unwanted weapon effects can be disabled as desired. Mouse-over each option when enabled to see a description of what happens for each option.

There's a new option under this to have safe basic weapons. This ensures that basic weapons (Iron weapons, Fire, Lightning, and Flux) are not given random effects. This gives you a safe zone to play without early game immediately becoming insane (unless you also turn on the ability to improve enemy weapons or randomize minion classes).

Additionally, an option is available to limit how many items can randomly receive effects. At 100%, all weapons (except possibly Iron weapons if the previous setting is checked) will receive an effect.

*Note: A few limitations are in place currently for this feature. Due to how ranged axes work, non-ranged axes are ineligible for gaining range or becoming magical weapons. Ranges are also limited to normally valid ranges in the game.*

### Classes

**Randomize Playable Characters** - The obvious randomization feature, changes the class of every playable character to a random class. Starting equipment is modified accordingly. Options to **Include Lords** and **Include Thieves** do as they sound. **Include Special Classes** generally prevents randomization of dancers/bards as well as Manaketes in the games that feature them. They add the classes to the randomization pool and they add the characters originally posessing those classes to the character pool to randomize. Disabling them will ensure Lord characters and Thief characters remain untouched (usually safer).

An option is also available: **Assign Classes Evenly** which will attempt to even out the class distribution so that no one class is overly represented in the result.

*Note: One new limitation that's in effect for now is limiting the pool for some characters. To keep things working with minimal issues, characters that have flying mounts can only randomize to another class with a flying mount. Characters that are a class that can cross water are limited to classes that can cross water normally (including flying classes). Same with characters that can cross mountains. These characters will be whitelisted over time to be randomizable.*

**Randomize Boss Characters** - Same as above, except boss characters. On the note of limitations, even though they're usually sitting on thrones and are therefore not impacted by movement, scripted scenes might break if flying bosses are told to cross mountains or bodies of water, so for now, they are subject to the same restriction as playable characters.

**Randomize Enemy Minions** - A new option this time around is to randomize the classes of the random enemies that you fight. The same restrictions apply for enemies with unique movement costs across different terrains, so once again, fliers will remain fliers, etc. Note that Thieves are unaffected, since they will remain thieves to apply pressure for chests. Like playable characters, chapters are whitelisted as necessary if they are deemed safe for full randomization. (I think the only chapter that's weird is Chapter 25, which features pirates and fliers over water.)

<p align="center">
  <img src="https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Readme/RandomClasses.png">
</p>
<p align="center">
  <i>You can turn on all three!</i>
</p>

**Bases Options** - This determines how a unit's base stats are determined when changing classes. By default, Yune **Retains Final Bases** so that the unit's effective base stats in-game does not change. The other options will cause effective bases to change by either **Retaining Personal Bases** or by **Adjusting Bases to Class** where a character's final bases are shuffled around to emphasize their new class's stronger points. This only applies to playable characters and bosses.

**Force Class Change** - This option will try to force characters to change their class when possible. If a character has no valid options to change to (due to limitations), then they will remain as they were as a fallback. But otherwise, if a class can change when this option is enabled, that character will change class.

**[FE8] Mix Monster Classes** - By default, without this option, class randomization will stay divided between monsters and humans. Any human characters and minions will remain as a human class and any monster bosses and minions will remain as a monster class. If this box is checked, then it allows humans to randomize into monster classes and monsters to randomize into human classes.

### Enemy Buffs
Thought the game was too easy?

**Buff Enemy Growths** - Random Enemies and their stats are dictated by class growth rates. Normally these growths would be overridden by a character's personal growths, but enemies don't have that quality, so they will scale based on these rates. Normally, they're pretty low, but this option will increase the growth rates so that, as the game progresses, the enemies will become tougher, due to the autoleveling routine. There are two options: **Flat** and **Scaling**. Flat buffs apply a constant to all of the growth rates. This generally makes up for weak points of enemies (tankier myrmidons and faster knights, etc.) A little boost will go a long way, especially for hard mode, so don't go too crazy with Flat buffs (10 - 20 is a pretty noticeable buff, especially later in the game). Scaling buffs apply a fraction based off of their original class growths. This generally emphasizes a class's strong points (myrmidons that are actually fast and knights that are actually tanky). As it's a fraction, the values is a percentage, where a value of 100% is a doubling of growth rates.

<p align="center">
  <img src="https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Readme/EnemyBuff.png">
</p>
<p align="center">
  <i>An example using enemy Knights. Also, hard mode adds phantom levels. :)</i>
</p>

**Improve Enemy Weapons** - This option selects some enemies and upgrades their weapons to the next level. For example, if an enemy was using an Iron Sword (an E rank weapon), he/she might now be using a Steel Sword (a D rank weapon) or a Longsword (a D rank weapon effective against cavalry), or if he's a myrmidon, a Wo Dao is a possibility (D rank weapon locked to myrmidons and has a high critical rate). You can set how often this happens, anywhere between 1% and 100% of minions. Note that this does not affect boss weaponry.

**Buff Boss Stats** - This option applies a bonus to all boss characters' base stats. There are two modes to determine how large of a bonus is applied, using a multiplier determined by how late in the game the boss appears. The Max Boost allows you to set the maximum stat gain for bosses. This value is applied to the final modified boss of the game. All prior bosses will receive a fraction of that boost.

* **Scale Linearly** - This gradually adds a constant amount from the beginning of the game to each boss. Starting bosses receive 1 stat point boost at a minimum, and the rate of increase is evenly distributed throughout the game.

* **Ease In/Ease Out** - This curve starts more slowly and ends more slowly, with the biggest deltas in increases reserved for the mid-game. The result is a slower ramp up time and a gradual ease into the max stat boost specified. This is also sometimes referred to as an S-curve.

<p align="center">
  <img src="https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Readme/EaseInEaseOutVSLinear.png">
</p>
<p align="center">
  <i>Quick math refresher if you need one. The far left is the prologue, the far right is the endgame.</i>
</p>

### Miscellaneous Options
Random stuff that may be funny.

**[FE6] Apply English Patch** - This option applies the English patch from Serenes Forest (https://serenesforest.net/forums/index.php?/topic/41095-fe6-localization-patch-v10-seriously-we-did-something/) onto the Japanese FE6 ROM.

**Randomize Rewards** - Rewards include chests and village items. This encapsultes any item you can get from villages and chests, but does not include villages that grant you gold. Also does not affect any items that are given specifically to a character (e.g. Mani Katti). 50% of the time, the item will be replaced with a related item, and the other 50% of the time will be a completely random item. Possible items include weapons, stat boosters, promotion items, and any other consumable items. Related items for non-weapons are those that fit into the same category (i.e. stat boosters or promotion item). Related items for weapons are those weapons with either the same rank or the same type.

### Recruitment
This option swaps around characters so that they join at different parts of the game than they normally do. The way this feature works is that the characters in the game are treated as slots to be filled by an arbitrary character from the playable character pool. The character that fills the slot is, for all intents and purposes, treated as if they were the slot character they replaced. This is important for determining supports. The options available determine how the stats for characters are calculated when they are leveled or de-leveled to match the slot they fill.

#### Growth Options

* **Use Fill Growths** - Uses the character's original growths. That means, for example, Karel replacing Roy will use Karel's growths.
* **Use Slot Growths** - Characters use the growths of the character they replace. That means, for example, Nino replacing Rebecca will use Rebecca's growths.
* **Slot Relative Growths** - Characters' growths are determined by mapping the slot character's growth values to the fill character's growth spread. 

<p align="center">
  <img src="https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Readme/RR_StatRelative.png">
</p>
<p align="center">
  <i>An example of Slot Relative Growths with Gerik replacing Knoll.</i>
</p>

* **Autolevel Base Stats** - This option uses the character's growth rates to simulate level gain and level loss to determine their base stats.
  * **Use Original Growths** - Uses the character's original growths to do the autoleveling. Has no effect if the original growth is identical to the new growth (i.e. if "Use Fill Growths" was selected above).
  * **Use New Growths** - Uses the character's new growths to do the autoleveling. It's only different from original growths if the latter two growth options above are used.
* **Match Base Stats** - This option sets the character's base stats to be equal to the base stats of the slot they fill.
* **Relative Base Stats** This option sets the character's base stats such that their highest stat matches the highest stat of the slot they fill and the remainder of the stats are based on the character's normal stat spread. See the growth example above, except with bases.

* **Use Fill Class** - This is the default option. Characters that randomize into another character's slot will retain their normal classes.
* **Use Slot Class** - This is an alternative where characters that randomize into another character's slot will become the class of the character they replace.

**Include Lords, Thieves, Special Characters** - Does exactly as it says: allows characters in these respective groups to change their recruitment time. If disabled, that group of characters will always join the same as they usually do in the vanilla game.
**Allow Cross-Gender Assignments** - This option removes the restriction on gender so that males can fill female slots and vice versa.

**[FE8] Include Creature Campaign NPCs** - This option adds Fado, Hayden, Glen, and Ismaire to the pool for randomzied recruitment.

### Weapon Assignment
These determine how items are assigned when they need to be assigned (affects randomized classes and randomized recruitment).

* **Strict Matching** - Replaces weapons using the closest counterpart to the original weapon.
* **Match Rank** - Replaces weapons using any weapon that shares the same weapon rank as the original weapon.
* **Random** - Uses any weapon the character can use based on his/her weapon ranks.

## <div id="fe9">Fire Emblem: Path of Radiance</div>

### Growths
This isn't anything too novel if you've read the above randomization options. I even reused the same data model when reading these options for FE9, so the options do exactly as they do above. The only difference is that, like FE4, there is an option to ensure that the growth rate for STR is higher than the growth rate for MAG for physical units and vice versa for magical units. Hybrid units (like Cleric/Valkyrie and Elincia Falcon Knight) have no preference for either.

### Bases
Also similar to all of the other titles. And again, STR/MAG options are available to try to make sure a physical unit doesn't end up with MAG bases at the expense of their STR base (and vice versa for magical units).

### CON and Affinity
CON randomization does the same thing, but CON has a very different role in FE9 than in GBAFE. CON (or Build if you prefer) only really affects shoving and rescuing (and I think maybe the Colossus skill?), so it has much less of an effect. Affinity also works the same way, though support building is much easier in FE9 than in GBAFE, so maybe it's a bit more interesting here.

### Rewards
There's two options this time around (and I might backport this to the GBAFE randomizers).

* **Similar Replacements** - Tries to find similar items for replacements. Similar in the sense that a stat booster would be replaced with another stat booster, a skill scroll replaced by another skill scroll, a consumable being replaced with another consumable, and a weapon replaced with one of the same type or of the same rank.
* **Random Replacements** - Replaces chests, villages, and desert items with completely random rewards (the old logic) 

### Skills
This, in my opinion, is the most interesting. Two high level options here:

* **Randomize Existing Skills** - This option simply randomizes skills that already exist into different skills. It does not give any skills to characters that didn't have them normally.
* **Fully Randomize Skills** - This options gives each character a chance of having a skill. This means some characters that previously didn't have skills may gain them, and some characters may lose them. The chance for a skill can be customized, and 100% means all characters will get 1 skill.

You'll also realize that, if you're familiar with FE4, that there is no option for the number of skills. This is primarily due to the capacity system. The cheapest skill costs 5 and the most expensive, non-occult skill, costs 15. This lines up well with mounted units having a max capacity of 15 (unpromoted), so each character can only have one skill assigned to them. I haven't decided on how to manage occult scrolls yet, so those are not included in the randomization yet.

### Classes
This is probably the buggiest option. I think I've managed to get most battle animations to work appropriately, but there's always the chance that a character later in the game causes issues, because I haven't been able to test that far. The options should look familiar, but I'll go over them briefly. **Include Lords** will randomizes Ike's class and add Ranger to the class pool. I'm not sure about how this breaks endgame, so I may need more modifications to make things possible with a non-Lord Ike. **Include Thieves** basically affect Volke and Sothe and allow them to change class and adds thieves to the randomization pool (I'm not sure if this works properly either). **Include Special Classes** basically just means Reyson, and there should be logic to make sure you only get at most one heron if this is included (as two herons is basically a free win). **Allow Crossgender Assignments** does as you expect, attempting to keep gender lines consistent. **Allow Cross-race Assignments** is new, but you can guess that this means crossing Beorc and Laguz classes. This applies for bosses and minions as well. 

Bosses work as you expect, though there might be additional logic for bosses that I haven't added yet, and I haven't tested cross-race assignments yet. Minions are also in the same boat, but in addition to not knowing if cross-race assignments work, some minions are directly referenced by their ID in chapter scripts, and those IDs need to be a specific class, so even if you force 100% of minions to change class, some will be immune to change to make sure the game is still functional.

Finally, **Force Class Change** does exactly as you expect and ensures that no character that is randomized ends up with the same class.

### Buff Enemies
Buffing minions work the same way as you are used to from GBAFE. Their class growths determine how they scale later into the game. Improving their weapons gives a chance of giving them a higher rank weapon. But more interestingly, it's possible to give minions random skills. This includes all normal skills, but excludes skills that don't make sense (like Provoke, Shade, Paragon, or Blossom). Like the weapons, a chance can be assigned for minions to receive skills.

Buffing bosses, on the other hand, looks to be borked, currently, because for whatever reason, FE9 doesn't load boss character stats from the character data. I suspect this is in the chapter script, but I haven't verified it yet. That said, boss weapons and boss skills are functional, so they should work.

## Sample Screenshots
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Windows/FE4Classes.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Windows/FE4Enemies.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Windows/FE4HolyBlood.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Windows/FE4Skills.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Linux/RandomClasses.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Linux/CustomPalettes.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Linux/WeaponEffects.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/MacOS/Minions.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/MacOS/CustomPalettes.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/MacOS/WeaponDescription.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Windows/Classes.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Windows/CustomPalettes.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Windows/WeaponDescriptions.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Windows/FE9Classes.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Windows/FE9Minions.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Windows/FE9Skills.png)

## Nice-to-have Features
* ~~Fixing battle animation palettes. I think I can programmatically assign proper looking colors based on classes, but I'm not sure yet.~~ This is actually done now. It mostly works, though it occasionally gives an odd color.
* ~~Fixing text to match items. Makes random weapon effects less infuriating to play.~~ Done!
* ~~Fixing world map sequences to be somewhat more accurate to a unit's class.~~ Done!
* Allowing melee axes to gain range.
* ~~Remove limitations on class randomization.~~ This is mostly resolved using a blacklist of characters and chapters which continue to limit class choices. All other characters and chapters are allowed to fully randomize classes.
* Add random fun ASM changes (Ranging from streamlining changes like removing the need to stand adjacent to a unit for support, to infurating, ~~like 1RN~~, or Thracia-style 1-99 HIT.)

## Next Steps (Kind of in order)
* ~~Add in FE6 support.~~ Done!
* Add in regional support outside of North America. (Should also be straightforward)
* ~~Add in FE8 support.~~ Done!
* ~~Add in FE4 support. (For the lulz, and mostly because it's possible and I have some fun ideas)~~
* Add in ~~FE9~~/10 support - There was a proof of concept for this randomizer made, so it might be time to start looking into seriously supporting this. (I mean, I can't call it Yune and then not support FE10.)

## Wishful thinking (probably not happening any time soon)
* Research FE3/5 support. (Need to actually finish these games to understand them)
* Research 3DSFE support. (Similar to extracting data from ISO and recompiling)
