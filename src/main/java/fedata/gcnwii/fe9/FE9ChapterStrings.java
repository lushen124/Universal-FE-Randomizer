package fedata.gcnwii.fe9;

import io.gcn.GCNMessageFileHandler;
import util.DebugPrinter;

public class FE9ChapterStrings {
	
	private GCNMessageFileHandler messageHandler;
	
	public FE9ChapterStrings(GCNMessageFileHandler handler) {
		messageHandler = handler;
	}

	public String textStringForIdentifier(String identifier) {
		return messageHandler.getStringWithIdentifier(identifier);
	}
	
	public void setStringForIdentifier(String identifier, String string) {
		messageHandler.addStringWithIdentifier(identifier, string);
	}
	
	public void debugPrintStrings() {
		for (String identifier : messageHandler.allIdentifiers()) {
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_STRINGS, identifier);
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_STRINGS, "-----------------------");
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_STRINGS, textStringForIdentifier(identifier));
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_STRINGS, "");
		}
	}
}
