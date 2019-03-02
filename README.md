# Yune: A Universal Fire Emblem Randomizer

## Latest Version: 0.8.0

### Compatability
* Fire Emblem: Genealogy of the Holy War (aka FE4 or ファイアーエムブレム　聖戦の系譜)
* Fire Emblem: Binding Blade (aka FE6 or ファイアーエムブレム　封印の剣)
* Fire Emblem: Blazing Sword (aka FE7 or ファイアーエムブレム 烈火の剣)
* Fire Emblem: The Sacred Stones (aka FE8 or ファイアーエムブレム 聖魔の光石)

Note that FE4 and FE6 require clean JP versions of those games. The randomizer will do a cheksum comparison to make sure of this. A Checksum failure error indicates an altered or otherwise invalid file. Additionally, FE4 is ok with either a Headered version of the ROM or an Unheadered version.

FE7 and FE8 currently require US versions of those games. Like above, a cheksum comparison will be performed to make sure the game is valid for randomization.

* * *

Properly universal this time. Those of you that used the original may recall that it supported all the GBA FEs, but was locked to Windows machines, mostly because I wrote it in Visual Basic, which has zero cross compatability. This time around, we're taking the opposite approach and starting with a single game first, but working on the three major desktop platforms.

And hey, name change! It's actually perfect in a lot of ways because it encapsulates several of my favorites. Alongside Fire Emblem, I'm also a fan of Love Live! with my favorite girls being Minami Kotori in μ's and Takami Chika in Aqours. Kotori's symbol reflects her name: Little Bird. Chika's associated color is Orange, alongside her symbol, a Mikan (tangerine). Combine these two to get an Orange Little Bird, which leads me to my favorite Fire Emblem character, which, if you couldn't tell already, was Micaiah from Radiant Dawn. Yune is the name of the orange little bird she has with her in most of her art. And without wishing to spoil those who haven't played Radiant Dawn, there is another way Yune is fitting for the name of a randomizer, but I'll leave that to you, because if you haven't played Radiant Dawn, you really should.

<p align="center">
  <img src="https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Universal%20FE%20Randomizer/res/YuneIcon.png">
</p>
<p align="center">
  <i>That's Yune!</i>
</p>

## Installation
Executables are found in the above executables folder and are separated by platform. The raw JAR file is available if you want to run it directly, but MacOS and Windows users also have the option of using an executable that generally makes things easier to launch.

### Windows
Raw JAR:
https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Executables/JAR/Yune%20-%20Windows.jar
https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Executables/JAR/Yune%20-%20Windows%20(x86).jar

Executable:
https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Executables/Windows/Yune.exe
https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Executables/Windows/Yune%20(x86).exe

**Requirements**: JRE >= 1.8.0. I was thinking about bundling this into the executable, but that's just a massive waste of space, and most of you probably can get JRE relatively easily. And if you can't, here you to: http://www.oracle.com/technetwork/java/javase/downloads/index.html. I've only tested this with Windows 10, but I don't see why older versions of Windows would be excluded so long as you have JRE.

Important note: There are two versions of the JAR and binary. **The one you need depends on the version of JRE you have installed and NOT the version of Windows you have installed.** That is to say, you can have a 64-bit version of Windows but still be running an x86 (32-bit) version of JRE. I recommend you update your JRE to match the architecture for your Windows installation, but if you want to retain your x86 JRE, use the x86 version. Chances are, if you see a splash screen and then nothing happens, you have the wrong version.

### MacOS
Raw JAR:
https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Executables/JAR/Yune%20-%20MacOS.jar

App Bundle:
https://github.com/lushen124/Universal-FE-Randomizer/tree/master/Executables/MacOS - The ZIP file will extract an actual bundle that can be double clicked and run like usual (after any security settings that may try to stop you). If you don't know, MacOS apps are actually a folder in disguise, which is why Github shows it like one. If, for some reason, you want to run the raw JAR, you need to specify the `-XstartOnFirstThread` flag. For example:

```
java -jar -XstartOnFirstThread Yune\ -\ MacOS.jar
```

**Requirements**: Like Windows, you need a JRE to run it, though most versions of MacOS have one included that should be sufficient. If it doesn't work, go ahead and grab the newest one at http://www.oracle.com/technetwork/java/javase/downloads/index.html. Tested with OS X 10.13 (High Sierra). Unlike Windows, if you have any Mac from the last 8 years, you should have a 64-bit machine (Mac OS X 10.7 Lion and above).

### GTK
Raw JAR:
https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Executables/JAR/Yune%20-%20GTK(x86_64).jar

I don't actually know that much about Linux, but this should work on any distro that supports GTK, which I believe is most of them. I also assume that if you are running Linux, you also know your way around things and you don't need a wrapper for a JAR file since most of you are probably using command line, so you can figure it out. For those that are running Linux but don't know how to run this, I can only say how I run it for testing, which is to (like the other OSes) install JRE with:

```
$ sudo apt-get update
```

```
$ sudo apt-get install default-jre
```

And then run it with

``` 
$ java -jar Yune\ -\ GTK(x86_64).jar
```

...or something like that. Tested with Ubuntu 16.04.4.

Also, you may need to `chmod` it so that it's runnable. That can be done with

```
chmod 777 Yune\ -\ GTK(x86_64).jar
```

## Randomization Options

### Fire Emblem: Genealogy of the Holy War

#### Growths
Randomizes the growths of each character. This only involves a character's personal growths. For characters that have holy blood of some kind, their growths are boosted from the holy blood, but that boost is not accounted for here. There are three modes of randomization:

* **Redistribute** - Sums up the character's total growths and randomly redistributes it across all stat areas. Optionally applies variance to the sum.
* **Delta** - Applies a random delta to each stat area's growth, adding or subtracting an amount up to the variance specified.
* **Full** - Discards the character's usual growth rate and generates completely new ones between the specified range. 

On top of that, there are two additional options that can be applied.

* **Adjust STR/MAG by Class** - Since this game separates Strength and Magic into different stats, this option will swap a character's STR/MAG stats to ensure that the stat with the higher growth is on the character's primary attack stat. In other words, physical attackers are guaranteed to have a greater or equal growth rate for Strength than Magic, and vice versa for magic users.
* **Adjust HP Growths** - This option gives HP growths additional weight when redistributing growths and ensures HP growths are in the upper half of the range when fully randomizing growths. This option does nothing when the randomization mode is Delta.

*Note: In order to prevent issues with overflow during inheritence, the maximum growth rate for any character in any area is 85%.*

#### Bases
Randomizes the base stats for each character. A character's effective bases are determined by the sum of their personal bases and the bases for their class. This randomization option only involves a character's personal bases. There are two modes of randomization:

* **Redistribute** - Sums up the character's total personal bases and randomly redistributes it across all stat areas. HP is **not** included in this, as there is no contribution from the class when determining a character's effective base HP. Optionally applies a variance to the sum of growths for a character.
* **Delta** - Applies a random delta to each stat area, adding or subtracting a random amount up to the variance specified.

On top of that, one more option is available in both cases.

* **Adjust STR/MAG by Class** - Much like with growths, this option will ensure that the character's primary attacking stat will be higher than or equal to the secondary attacking stat.

#### Holy Blood

* **Randomize Growth Bonuses** - Holy blood grants its carrier bonuses in their growth rates, independent from the character's personal growths. This option randomly redistributes the bonus growth areas for each holy blood, redistributing up to the growth amount specified between all growth areas.
* **Randomize Holy Weapon Bonuses** - Characters with Major holy blood gain access to holy weapons, depending on the blood that they have. These weapons generally give large stat bonuses when equipped. This option redistributes the bonuses conferred to the user of each type of holy weapon.
* **Assign Holy Blood to Playable Characters** - This option randomly assigns holy blood to playable characters. The type of blood can be limited to a set that matches their class's weapon usage if the **Match Blood to Weapon Usage** option is checked. The chance of receiving Major, Minor, or no Holy Blood can also be customized. 

#### Skills

##### Shuffle
This option takes all of the skills from every character and then randomly doles them back out again. If the option to retain the number of skills is enabled, this makes sure a character that contributed 2 skills to the pool, for example, will be assigned 2 skills back. Disabling the option will not limit a character to the number of skills they started with, though each character is capped at a max of 4 skills. The other option for this mode is to separate the pool of skills by generation, which should be self-explanatory what it does.

##### Randomize
This option takes up most of the space and will randomly assign skills based on a weighted distribution set using the controls for each skill. A skill can be disabled entirely to remove it from the pool, or it can choose from 5 levels of likelyhood. Note that the likelyhood is all relative, as they must add up to 100%, so putting everything on Least Likely is equivalent to putting everything on Most Likely. If the option to retain the number of skills is enabled, a character's original number of skills is used to determine how many skills they are randomized and the controls for distributing number of skills is disabled. If the option to retain the number of skills is disabled, the skill count distribution controls are enabled and, in a similar way, the number of skills to give each character can be customized (in more or less the same way).

<p align="center">
  <img src="https://raw.githubusercontent.com/lushen124/Universal-FE-Randomizer/master/Screenshots/Windows/FE4Skills.png">
</p>

#### Classes
This section grew drastically, mostly due to how many safeguards you might need to make sure the game is still enjoyable. As usaul, the randomize playable characters, enemies, and bosses are back, and for enemies, not much has changed, though an option to randomize arena fights has also been added since it's possible and slightly entertaining. Bosses also have another option to randomize holy blood for the bosses that have it, should you want to expand the pool of classes for bosses later in the game. This option is also available for playable classes, and for the same reason. If this option is disabled, the class pool for those characters that have major holy blood is reduced to make sure they can still use their holy weapon. If this option is enabled, then characters will have their major blood be determined by what kind of weapons their class can actually use. For example, if Sigurd randomizes into a Bow Knight, then he will also have Major Ulir blood. Byron in Chapter 5 will also deliver him Yewfelle instead of Tyrfing.

<p align="center" float="left">
  <img width="45%" src="https://raw.githubusercontent.com/lushen124/Universal-FE-Randomizer/master/Screenshots/Windows/FE4Enemies.png">
  <img width="45%" src="https://raw.githubusercontent.com/lushen124/Universal-FE-Randomizer/master/Screenshots/Windows/FE4Classes.png">
</p>

For children, there are three options on how they are assigned their class. When matching strictly, children derive their class from their closest Gen 1 analogue, determined by their class equivalency in the original game (so since Lester is an Arch Knight, and Midir is an Arch Knight, Lester will match Midir's class). When matching loosely, children get a randomized class, but are limited to classes that share weapon usage for at least one weapon with their Gen 1 analogue. Using the above example, if Midir randomized into an Axe Fighter, then Lester will randomize into a class that can use axes (i.e. Axe Fighter, Barbarian, Pirate, Axe Armor, or Axe Knight).

The option to adjust conversation rewards updates all of the items received from conversations (and most events) into items the recipient can actually use. If disabled, all of the original weapons/items will be given instead. For example, in the Prologue, Arvis delivers Sigurd a Silver Sword normally. Disabling the option retains the Silver Sword, regardless of Sigurd's class. Enabling the option will change the Silver Sword into another weapon in the case that Sigurd is a class that can't use swords.

Another set of options involves shop items, which have the option of not being touched, randomized to weapons usable by your party at the time, or completely randomized. Should be pretty self-explanatory what each of those means.

Finally, there's an option to swap a character's growths and bases for STR and MAG if their target class uses the other stat for attacking. Similar to the option for Growths and Bases randomization, except this one just swaps instead of randomizes. Like the other options, a character that ends up in a class that can use both will not swap their growths/bases.

#### Miscellaneous
The option to apply an English patch will apply the translation patch found [here](https://serenesforest.net/forums/index.php?/topic/63676-fe4-translation-patch-open-beta-v7/).

The other option that I wasn't sure where to place it was an option to randomize all of the player-obtainable rings in this game. FE4's invenotry system is unique in that any item the player can obtain is tracked separately from generic weapons. This means it becomes challenging to freely give out weapons or items for classes. In most cases, I simply change the item in the inventory "slot", but I can't create new slots. This means I can't, say, give Naoise a second weapon if he randomizes into a class that could use one. However, this does mean it's easy to scan through the list for rings and select a random ring for each occurrence, which is what this option does.

### GBA FE Options

#### Growths
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

#### Bases
As you may know, a character's base stats are the sum of their personal bases and the bases afforded to them by their class.

<p align="center">
  <img src="https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Readme/Bases.png">
</p>
<p align="center">
  <i>The randomizer only touches the personal bases.</i>
</p>

**Redistribution** - Sums up the character's bases and randomly assigns them back out. Note that negative personal bases are not allowed here, so while the sum can be lowered from a character normally having a negative personal base, the personal bases that we end up with are strictly positive.

**Variance** - Adds (or subtracts) a random number up to the variance provided to all of a character's bases. Unlike redistribution, there is a chance of receiving negative personal bases here, though no character is allowed to have a negative base after applying their class bases in any area.

#### Miscellaneous Character Options

**Randomize CON** - CON (short for Constitution) is a stat that has a base value, but no growth. Each character has a personal CON that's added to the class's base CON to derive their final value. This option randomizes the character's personal CON using a variance. A random number is added or subtracted from their original personal CON and clamped at the minimum CON specified.

**Randomize MOV** - Movement range is set to a single class, and therefore, unlike the previous options, changes in MOV affect every unit in the class, friend and foe alike. For the purpose of this, Male versions are distinct from their Female counterparts when determining distinct classes. For example, a Male Paladin is considered a different class from a Female Paladin. With this option, it's possible that Male Paladins have only 5 MOV while Female Paladins may have 8 MOV.

**Randomize Affinity** - A relatively minor change. Affinity affects support bonuses that a character receives from supporting another unit. This option simply assigns random affinities to your characters, randomizing the support bonuses they receive from each unit.

#### Weapons

**Randomize MT/Hit/WT/Durability** - This randomizes the main stats of every weapon in the game. All of these stats are randomized using a Minimum value, a Maximum value, and a Variance value. This basically adds or subtracts a random number up to the variance value to the stat affected, and then clamps the value between the specified minimum and maximum.

**Apply Random Effects** - This is a returning feature from the old randomizer, but with more granular controls. Simply put, for every weapon in the game, the randomizer will add one of the enabled options to it. If a weapon already had a special effect, it gains another one, if available. Unwanted weapon effects can be disabled as desired. Mouse-over each option when enabled to see a description of what happens for each option.

There's a new option under this to have safe basic weapons. This ensures that basic weapons (Iron weapons, Fire, Lightning, and Flux) are not given random effects. This gives you a safe zone to play without early game immediately becoming insane (unless you also turn on the ability to improve enemy weapons or randomize minion classes).

*Note: A few limitations are in place currently for this feature. Due to how ranged axes work, non-ranged axes are ineligible for gaining range or becoming magical weapons. Ranges are also limited to normally valid ranges in the game.*

#### Classes

**Randomize Playable Characters** - The obvious randomization feature, changes the class of every playable character to a random class. Starting equipment is modified accordingly. Options to **Include Lords** and **Include Thieves** do as they sound. They add the classes to the randomization pool and they add the characters originally posessing those classes to the character pool to randomize. Disabling them will ensure Lord characters and Thief characters remain untouched (usually safer).

*Note: One new limitation that's in effect for now is limiting the pool for some characters. To keep things working with minimal issues, characters that have flying mounts can only randomize to another class with a flying mount. Characters that are a class that can cross water are limited to classes that can cross water normally (including flying classes). Same with characters that can cross mountains. These characters will be whitelisted over time to be randomizable. For now, Florina is in the whitelist, but Fiora, Farina, and Vaida remain locked to flying classes.*

**Randomize Boss Characters** - Same as above, except boss characters. On the note of limitations, even though they're usually sitting on thrones and are therefore not impacted by movement, scripted scenes might break if flying bosses are told to cross mountains or bodies of water, so for now, they are subject to the same restriction as playable characters.

**Randomize Enemy Minions** - A new option this time around is to randomize the classes of the random enemies that you fight. The same restrictions apply for enemies with unique movement costs across different terrains, so once again, fliers will remain fliers, etc. Note that Thieves are unaffected, since they will remain thieves to apply pressure for chests. Like playable characters, chapters are whitelisted as necessary if they are deemed safe for full randomization. (I think the only chapter that's weird is Chapter 25, which features pirates and fliers over water.)

<p align="center">
  <img src="https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Readme/RandomClasses.png">
</p>
<p align="center">
  <i>You can turn on all three!</i>
</p>

**Mix Monster Classes (FE8 only)** - By default, without this option, class randomization will stay divided between monsters and humans. Any human characters and minions will remain as a human class and any monster bosses and minions will remain as a monster class. If this box is checked, then it allows humans to randomize into monster classes and monsters to randomize into human classes.

#### Enemy Buffs
Thought the game was too easy?

**Buff Enemy Growths** - Random Enemies and their stats are dictated by class growth rates. Normally these growths would be overridden by a character's personal growths, but enemies don't have that quality, so they will scale based on these rates. Normally, they're pretty low, but this option will increase the growth rates so that, as the game progresses, the enemies will become tougher, due to the autoleveling routine. There are two options: **Flat** and **Scaling**. Flat buffs apply a constant to all of the growth rates. This generally makes up for weak points of enemies (tankier myrmidons and faster knights, etc.) A little boost will go a long way, especially for hard mode, so don't go too crazy with Flat buffs (10 - 20 is a pretty noticeable buff, especially later in the game). Scaling buffs apply a fraction based off of their original class growths. This generally emphasizes a class's strong points (myrmidons that are actually fast and knights that are actually tanky). As it's a fraction, the values is a percentage, where a value of 100% is a doubling of growth rates.

<p align="center">
  <img src="https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Readme/EnemyBuff.png">
</p>
<p align="center">
  <i>An example using enemy Knights. Also, hard mode adds phantom levels. :)</i>
</p>

**Improve Enemy Weapons** - This option selects some enemies and upgrades their weapons to the next level. For example, if an enemy was using an Iron Sword (an E rank weapon), he/she might now be using a Steel Sword (a D rank weapon) or a Longsword (a D rank weapon effective against cavalry), or if he's a myrmidon, a Wo Dao is a possibility (D rank weapon locked to myrmidons and has a high critical rate). You can set how often this happens, anywhere between 1% and 100% of minions. Note that this does not affect boss weaponry.

#### Miscellaneous Options
Random stuff that may be funny.

**Apply English Patch (FE6 Only)** - This option applies the English patch from Serenes Forest (https://serenesforest.net/forums/index.php?/topic/41095-fe6-localization-patch-v10-seriously-we-did-something/) onto the Japanese FE6 ROM.

**Randomize Rewards** - Rewards include chests and village items. This encapsultes any item you can get from villages and chests, but does not include villages that grant you gold. Also does not affect any items that are given specifically to a character (e.g. Mani Katti). 50% of the time, the item will be replaced with a related item, and the other 50% of the time will be a completely random item. Possible items include weapons, stat boosters, promotion items, and any other consumable items. Related items for non-weapons are those that fit into the same category (i.e. stat boosters or promotion item). Related items for weapons are those weapons with either the same rank or the same type.

**Randomize Recruitment Order (WIP)** - A weird option from the original randomizer which more or less replaces character data completely with another, simulating a randomized recruitment order. Not sure if I'll make any changes to this or not.

## Sample Screenshots
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Linux/RandomClasses.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Linux/CustomPalettes.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Linux/WeaponEffects.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/MacOS/Minions.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/MacOS/CustomPalettes.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/MacOS/WeaponDescription.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Windows/Classes.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Windows/CustomPalettes.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Windows/WeaponDescriptions.png)

## Nice-to-have Features
* ~~Fixing battle animation palettes. I think I can programmatically assign proper looking colors based on classes, but I'm not sure yet.~~ This is actually done now. It mostly works, though it occasionally gives an odd color.
* ~~Fixing text to match items. Makes random weapon effects less infuriating to play.~~ Done!
* Fixing world map sequences to be somewhat more accurate to a unit's class.
* Allowing melee axes to gain range.
* ~~Remove limitations on class randomization.~~ This is mostly resolved using a blacklist of characters and chapters which continue to limit class choices. All other characters and chapters are allowed to fully randomize classes.
* Add random fun ASM changes (Ranging from streamlining changes like removing the need to stand adjacent to a unit for support, to infurating, like 1RN, or Thracia-style 1-99 HIT.)

## Next Steps (Kind of in order)
* ~~Add in FE6 support.~~ Done!
* Add in regional support outside of North America. (Should also be straightforward)
* ~~Add in FE8 support.~~ Done!
* ~~Add in FE4 support. (For the lulz, and mostly because it's possible and I have some fun ideas)~~

## Wishful thinking (probably not happening any time soon)
* Research FE9/10 support. (How do I ISO)
* Research FE3/5 support. (Need to actually finish these games to understand them)
* Research 3DSFE support. (Similar to extracting data from ISO and recompiling)
