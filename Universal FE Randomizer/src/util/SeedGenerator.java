package util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import fedata.general.FEBase.GameType;

public class SeedGenerator {
	
	static String[] fe4Quotes = new String[] {
			"Those Pegasus Knights won't pose a problem in the face of the Beige Ritter. Now watch 'em fall!",
			"Hrmph. I can live with the strong and reliable part, but leave the slow bit out of it, ok?",
			"My kindness toward you has run out. I hate to say it, but you're going to die. ",
			"I've waited for this day! You're going to pay for taking my mother and little sister!",
			"I will use my power to create a world which is free of prejudice.",
			"Cross me, and I will pursue you to the very depths of the inferno itself, till I take your head.",
			"Don't hold this against me. ...it's just how I make a living.",
			"You drove my mother to her death, remember? Or do I have to remind you!?",
			"You're a disgrace to the House of Jungby, AND you've tarnished the reputation of the Crusader Ulir!!",
			"Thracia is not like it used to be... I want to fight for the people!",
			"It's the powerless citizens who suffer, and I'm not about to let it happen!",
			"You must keep his dream alive, for you are the only one who can pull it off now.",
			"Siding with the empire was an absurd idea. And the populace is now in jeopardy as a result!",
			"Prince Shannan is more handsome than you could ever hope to be!",
			"What can a little girl like you do to me? I'll send you to hell, just as I did her!",
			"Still your tongue... And feel the wrath of the goddess of lightning!",
			"My unremitting love for you is strong enough to cross the bounds of insanity!",
			"From this point forward we are an army of liberators fighting for love and justice!!",
			"For the first time in my life I know what I'm here for. And that's to fight...",
			"I am the heir of Loptous's kin, and the inheritor of his power. This world belongs rightfully to me.",
			"I need a man the calibre of my dear brother. That's why I'll never marry.",
			"I've dreamed of killing you with my bare hands, and I've lived to carry it out!!",
			"We do what we do to survive! So don't hold a grudge over someone else's livelihood.",
			"Never waste your anger on individuals. Always focus it on the evil within all of us.",
			"Wherever you go, I shall follow... even to the ends of the continent.",
			"We were born into the knighthood and are prepared to die fighting!",
			"It's not something I'm proud of. But the mission awaiting us was a fate worse than death.",
			"I have the Gae Bolg with me. I'm not going to fall with this at my side.",
			"Die! You hyenas! As long as I have the Gae Bolg, I will not lose to your kind.",
			"I'll be strong enough to protect everybody here someday!"
	};
	
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
	
	static String[] fe8Quotes = new String[] {
			"We charge down these cliffs, and the only thing we'll be rushing to is death.",
			"If you have lied to me today, I will hound you to the very grave itself.",
			"Rise and rage, my precious children of darkness.",
			"Trust me. I don't pick fights I cannot win.",
			"To think I'd lose a fight not to a man's sword but to a woman's words.",
			"You will move from that place. That is my father's seat. It is his throne... You've no right to sit there.",
			"I am here on a mission. One that I swore to my brother I would fulfill.",
			"You're just a corpse who does not know he is dead.",
			"Do you truly think you can take us with those numbers? Imbecile! You'll learn the error of your ways.",
			"I thought to catch a little bird in my net, and it seems instead I've snared a hawk.",
			"You should be happy to fight, kill, and die in my service!",
			"It's not over yet. Victory still hands in the balance.",
			"I know the path I'm given is foolish. Yet I am a knight, and I have no other.",
			"I told myself I could be happy simply serving you as your most loyal knight.",
			"You're a stepping-stone... And I'm moving up. Don't take it personally.",
			"I'm stronger than I used to be. No offence, but you're not in my league anymore.",
			"For the one I love... I betrayed everything. My country, my lord and master... Everything...",
			"You humans are so inconstant. You've forgotten what it is to fear me.",
			"Merely serving me must be the greatest pleasure man can know.",
			"If I were not a holy woman, I would beat you senseless.",
			"Trying to trap me is a mistake. Failing, an expensive one. I think it's time for you to learn how expensive.",
			"Gambling's what I live for. Even when I lose, I never want to stop."
	};
	
	static String[] fe9Quotes = new String[] {
			"For all those born with nothing, there are those born with everything. Perhaps the ones who don't know the difference are the ones we should envy.",
			"Great, then how are we going to hurt him? Angry looks?",
			"Okay, so what do I do? Put on a funny hat or something?",
			"What say I run you through with my lance and call it a day?!",
			"You kill for sport, and hide your fear behind a wall of bravado. Now let me show you the meaning of true fear!",
			"You disgust me beyond words!",
			"The seeds of war have indeed been sewn across the continent.",
			"In the middle of that giant mess, you didn't give up, Boss. And I knew. That is how a true man lives!",
			"You think you can defeat me? The man who taught you how to fight? What a fool.",
			"In truth, there is nothing in this world more humorous than a knight.",
			"I’m not a child. Those days ended the instant I took my first life.",
			"Your very existence is an insult to all that is beautiful!",
			"Sure, I’ll take you on. But it’s going to hurt!",
			"Folks always judge, and they’re usually wrong.",
			"There's nothing more awkward than having to kill a friend because of his poor taste in employers, I tell you.",
			"Lament your fortune, dear children, for all hope is lost.",
			"Now we've got you! Eat rock!",
			"That was my name... once. But I... threw it away.",
			"Why are you always scowling? Small children will start to cry!",
			"You can learn the dance of blades from me without knowing my history, can you not?",
			"Humans are shameless creatures that carelessly ignore any misfortune which does not befall them directly."
	};
	
	public static String generateRandomSeed(GameType type) {
		switch (type) {
		case FE4: {
			int quoteCount = fe4Quotes.length;
			return fe4Quotes[ThreadLocalRandom.current().nextInt(quoteCount)];
		}
		case FE6: {
			int quoteCount = fe6Quotes.length;
			return fe6Quotes[ThreadLocalRandom.current().nextInt(quoteCount)];
		}
		case FE7: {
			int quoteCount = fe7Quotes.length;
			return fe7Quotes[ThreadLocalRandom.current().nextInt(quoteCount)];
		}
		case FE8: {
			int quoteCount = fe8Quotes.length;
			return fe8Quotes[ThreadLocalRandom.current().nextInt(quoteCount)];
		}
		case FE9: {
			int quoteCount = fe9Quotes.length;
			return fe9Quotes[ThreadLocalRandom.current().nextInt(quoteCount)];
		}
		default:
			return generateRandomSeed();
		}
	}
	public static String generateRandomSeed() {
		int gameSelect = ThreadLocalRandom.current().nextInt(5);
		if (gameSelect == 0) {
			return generateRandomSeed(GameType.FE6);
		} else if (gameSelect == 1) {
			return generateRandomSeed(GameType.FE7);
		} else if (gameSelect == 2) {
			return generateRandomSeed(GameType.FE8);
		} else if (gameSelect == 3) {
			return generateRandomSeed(GameType.FE4);
		} else if (gameSelect == 4) {
			return generateRandomSeed(GameType.FE9);
		}
		
		return "Type something in!";
	}
	
	public static long generateSeedValue(String seedString, int seedSalt) {
		StringBuilder sb = new StringBuilder(seedString);
		while (sb.length() < 2) {
			sb.append(seedString);
		}
		
		long counter = sb.toString().hashCode() + seedSalt;
		
		DebugPrinter.log(DebugPrinter.Key.RANDOM, "Previewing Seed \"" + seedString + "\" with salt " + seedSalt);
		Random rng = new Random(counter);
		DebugPrinter.log(DebugPrinter.Key.RANDOM, "" + rng.nextInt(100) + ", " + rng.nextInt(100) + ", " + rng.nextInt(100) + ", " + rng.nextInt(100) + ", " + rng.nextInt(100) + ", " + rng.nextInt(100) + ", " + rng.nextInt(100) + ", " + rng.nextInt(100) + ", " + rng.nextInt(100) + ", " + rng.nextInt(100) + ", " + rng.nextInt(100) + ", " + rng.nextInt(100) + "" );
		
		return counter;
	}
}
