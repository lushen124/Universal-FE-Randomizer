package util;

import java.util.concurrent.ThreadLocalRandom;

import fedata.FEBase.GameType;

public class SeedGenerator {
	
	static String[] fe6Quotes = new String[] {
			"Finally, a foe worthy of my axe! Let's show 'em what we're made of!",
			"I am sorry, Your Highness, but I couldn't leave you in that dank cellar.",
			"If there is any possibility to avoid further bloodshed, we must try.",
			"Black-hearted fiend!",
			"The strongest of a pile of worms is hardly a match for my boot, however.",
			"Your duty is not to think. Your duty is to follow orders.",
			"Do you doubt my judgment?",
			"I'm sorry for lying... But I've chosen not to run away anymore.",
			"She...may act strong... But she is still a child...",
			"Your garb, your hair--even your speech... All a painfully obvious guise to hide your true lowborn face.",
			"A proper gentleman would escort a lady to her mansion!",
			"That expression on your face scared her away.",
			"The nobility steals from the poor so you can live in luxury at our expense!",
			"Ilian mercenaries are said to never betray their employers, no matter what.",
			"As a knight of Ostia, you must always remain calm.",
			"Dogs of the Lycian Army... Come. I shall cut you down where you stand!",
			"Now, leave Ostia to me and we can all go home with our limbs attached.",
			"He's grown so much in the short time since I met him last.",
			"Opposing me...is the equivalent of opposing the gods!",
			"All the way out here in the Isles West, folks work to death for nobles in their nest!"
	};
	
	static String[] fe7Quotes = new String[] {
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
	
	public static String generateRandomSeed(GameType type) {
		switch (type) {
		case FE6: {
			int quoteCount = fe6Quotes.length;
			return fe6Quotes[ThreadLocalRandom.current().nextInt(quoteCount)];
		}
		case FE7: {
			int quoteCount = fe7Quotes.length;
			return fe7Quotes[ThreadLocalRandom.current().nextInt(quoteCount)];
		}
		default:
			return generateRandomSeed();
		}
	}
	public static String generateRandomSeed() {
		int gameSelect = ThreadLocalRandom.current().nextInt(2);
		if (gameSelect == 0) {
			return generateRandomSeed(GameType.FE6);
		} else if (gameSelect == 1) {
			return generateRandomSeed(GameType.FE7);
		}
		
		return "Type something in!";
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
