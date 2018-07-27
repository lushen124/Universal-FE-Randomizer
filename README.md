# Yune: A Universal Fire Emblem Randomizer
Properly universal this time. And hey, name change!

## Installation
Executables are found in the above executables folder and are separated by platform. The raw JAR file is available if you want to run it directly, but MacOS and Windows users also have the option of using an executable that generally makes things easier to launch.

### Windows
Raw JAR:
https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Executables/JAR/Yune-Windows.jar

Executable:
https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Executables/Windows/Yune.exe

**Requirements**: JRE >= 1.8.0. I was thinking about bundling this into the executable, but that's just a massive waste of space, and most of you probably can get JRE relatively easily. And if you can't, here you to: http://www.oracle.com/technetwork/java/javase/downloads/index.html. I've only tested this with Windows 10, but I don't see why older versions of Windows would be excluded so long as you have JRE.

### MacOS
Raw JAR:
https://github.com/lushen124/Universal-FE-Randomizer/blob/master/Executables/JAR/Yune-MacOS.jar

App Bundle:
https://github.com/lushen124/Universal-FE-Randomizer/tree/master/Executables/MacOS - The ZIP file will extract an actual bundle that can be double clicked and run like usual (after any security settings that may try to stop you). If you don't know, MacOS apps are actually a folder in disguise, which is why Github shows it like one. 

**Requirements**: Like Windows, you need a JRE to run it, though most versions of MacOS have one included that should be sufficient. If it doesn't work, go ahead and grab the newest one at http://www.oracle.com/technetwork/java/javase/downloads/index.html. Tested with OS X 10.12 (Sierra).

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
