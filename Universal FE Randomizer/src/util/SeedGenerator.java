package util;

import java.util.concurrent.ThreadLocalRandom;

public class SeedGenerator {
	
	static String[] quotes = new String[] {
		"So this is the Mani Katti. A blade with no equal.",
		"But you should really fear the enemy's archers, not your own.",
		"Hey, you! Yes, you, the grim-faced one!",
		"Craven cur!",
		"Big words make my head hurt!",
		"Beware the Black Fang!",
		"Tell me...are you afraid to die?",
		"Oho!! I...I've been saved!",
		"But be of good cheer! In dying here, you will be spared the cataclysm to come!",
		"The punishment for traitors is death.",
		"You're inhuman. Your soul is black and devoid of warmth.",
		"For those who's reasoning is bent, I shall straighten it with my bow",
		"In the name of the Fang, I sentence you to death.",
		"We do not question our orders. We are the jaw that bites.",
		"All the evil that you've done up to now... Repent it, and sleep.",
		"Behold! A giant walks among you! My defense is impenetrable!",
		"You're about to die. Scream if you must.",
		"This is a message from Lord Nergal. 'I await you on the Dread Isle.'",
		"Take me into your bosoms and keep me safe forever!",
		"Thunder! Thunder, hear my cry!",
		"That little bird has escaped this island cage twice.",
		"Humans... they are so very fragile.",
		"Uh-oh! That bandit's spotted me! He's coming this way! Let's close in and attack!"
	};
	
	public static String generateRandomSeed() {
		int quoteCount = quotes.length;
		return quotes[ThreadLocalRandom.current().nextInt(quoteCount)];
	}
	
	public static long generateSeedValue(String seedString, int seedSalt) {
		Boolean isOdd = seedSalt % 2 == 1;
		StringBuilder sb = new StringBuilder(seedString);
		while (sb.length() < 2) {
			sb.append(seedString);
		}
		
		String finalSeed = sb.toString();
		long counter = 0;
		for (int i = (isOdd ? 0 : 1); i < finalSeed.length(); i += 2) {
			counter += (seedSalt * (int)finalSeed.charAt(i));
		}
		
		return counter;
	}

}
