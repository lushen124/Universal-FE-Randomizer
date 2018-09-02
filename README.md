# Yune: A Universal Fire Emblem Randomizer
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
![Randomizer Image](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Windows.png)

Raw JAR:
https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Executables/JAR/Yune%20-%20Windows.jar

Executable:
https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Executables/Windows/Yune.exe

**Requirements**: JRE >= 1.8.0. I was thinking about bundling this into the executable, but that's just a massive waste of space, and most of you probably can get JRE relatively easily. And if you can't, here you to: http://www.oracle.com/technetwork/java/javase/downloads/index.html. I've only tested this with Windows 10, but I don't see why older versions of Windows would be excluded so long as you have JRE.

### MacOS
![Randomizer Image](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/MacOS.png)

Raw JAR:
https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Executables/JAR/Yune%20-%20MacOS.jar

App Bundle:
https://github.com/lushen124/Universal-FE-Randomizer/tree/master/Executables/MacOS - The ZIP file will extract an actual bundle that can be double clicked and run like usual (after any security settings that may try to stop you). If you don't know, MacOS apps are actually a folder in disguise, which is why Github shows it like one. 

**Requirements**: Like Windows, you need a JRE to run it, though most versions of MacOS have one included that should be sufficient. If it doesn't work, go ahead and grab the newest one at http://www.oracle.com/technetwork/java/javase/downloads/index.html. Tested with OS X 10.13 (High Sierra).

### GTK
![Randomizer Image](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Linux.png)

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

## Compatability
At time of writing, the randomizer supports two games: Fire Emblem: Binding Blade (aka FE6 or ファイアーエムブレム　封印の剣) and Fire Emblem: Blazing Sword (aka FE7 or ファイアーエムブレム 烈火の剣). In the case of the former, the randomizer expects a clean JP ROM and will give the option to apply the english patch on top of it. In the case of the latter, only the US version of the game is supported. I plan to add support for FE7JP and FE8 soon. Afterwards, it'll take a while, but I'll look into either the SFC games (FE3 ~ FE5) or the Tellius games (FE9 ~ FE10) depending on whether I want to learn more about how FE3 ~ FE5 work or if I want to learn how to decode ISO files.

## Randomization Options

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

### Miscellaneous Character Options

**Randomize CON** - CON (short for Constitution) is a stat that has a base value, but no growth. Each character has a personal CON that's added to the class's base CON to derive their final value. This option randomizes the character's personal CON using a variance. A random number is added or subtracted from their original personal CON and clamped at the minimum CON specified.

**Randomize MOV** - Movement range is set to a single class, and therefore, unlike the previous options, changes in MOV affect every unit in the class, friend and foe alike. For the purpose of this, Male versions are distinct from their Female counterparts when determining distinct classes. For example, a Male Paladin is considered a different class from a Female Paladin. With this option, it's possible that Male Paladins have only 5 MOV while Female Paladins may have 8 MOV.

**Randomize Affinity** - A relatively minor change. Affinity affects support bonuses that a character receives from supporting another unit. This option simply assigns random affinities to your characters, randomizing the support bonuses they receive from each unit.

### Weapons

**Randomize MT/Hit/WT/Durability** - This randomizes the main stats of every weapon in the game. All of these stats are randomized using a Minimum value, a Maximum value, and a Variance value. This basically adds or subtracts a random number up to the variance value to the stat affected, and then clamps the value between the specified minimum and maximum.

**Apply Random Effects** - This is a returning feature from the old randomizer, but with more granular controls. Simply put, for every weapon in the game, the randomizer will add one of the enabled options to it. If a weapon already had a special effect, it gains another one, if available. Unwanted weapon effects can be disabled as desired. Mouse-over each option when enabled to see a description of what happens for each option.

There's a new option under this to have safe basic weapons. This ensures that basic weapons (Iron weapons, Fire, Lightning, and Flux) are not given random effects. This gives you a safe zone to play without early game immediately becoming insane (unless you also turn on the ability to improve enemy weapons or randomize minion classes).

*Note: A few limitations are in place currently for this feature. Due to how ranged axes work, non-ranged axes are ineligible for gaining range or becoming magical weapons. Ranges are also limited to normally valid ranges in the game.*

### Classes

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

### Miscellaneous Options
Random stuff that may be funny.

**Randomize Rewards** - Rewards include chests and village items. This encapsultes any item you can get from villages and chests, but does not include villages that grant you gold. Also does not affect any items that are given specifically to a character (e.g. Mani Katti). 50% of the time, the item will be replaced with a related item, and the other 50% of the time will be a completely random item. Possible items include weapons, stat boosters, promotion items, and any other consumable items. Related items for non-weapons are those that fit into the same category (i.e. stat boosters or promotion item). Related items for weapons are those weapons with either the same rank or the same type.

**Randomize Recruitment Order (WIP)** - A weird option from the original randomizer which more or less replaces character data completely with another, simulating a randomized recruitment order. Not sure if I'll make any changes to this or not.

## Sample Screenshots
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Linux/FixedPalettes.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Linux/RandomMinions.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/MacOS/MorePalettes.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/MacOS/MoreText.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Windows/FE6.png)
![](https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Screenshots/Windows/FE6_2.png)

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
* Add in FE8 support. (Lower on the priority list since FE8 Auto Randomizer exists)
* Add in FE4 support. (For the lulz, and mostly because it's possible and I have some fun ideas)

## Wishful thinking (probably not happening any time soon)
* Research FE9/10 support. (How do I ISO)
* Research FE3/5 support. (Need to actually finish these games to understand them)
* Research 3DSFE support. (Similar to extracting data from ISO and recompiling)
