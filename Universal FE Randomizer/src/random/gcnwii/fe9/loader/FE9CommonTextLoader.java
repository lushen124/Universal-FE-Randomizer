package random.gcnwii.fe9.loader;

import java.util.List;

import fedata.gcnwii.fe9.FE9Data;
import io.gcn.GCNFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import io.gcn.GCNMessageFileHandler;
import util.DebugPrinter;

public class FE9CommonTextLoader {
	
	GCNMessageFileHandler messageFile;
	
	public FE9CommonTextLoader(GCNISOHandler isoHandler) throws GCNISOException {
		
		GCNFileHandler handler = isoHandler.handlerForFileWithName(FE9Data.CommonTextFilename);
		assert(handler instanceof GCNMessageFileHandler);
		messageFile = (GCNMessageFileHandler)handler;
		
		List<String> identifiers = messageFile.allIdentifiers();
		
		for (String id : identifiers) {
			DebugPrinter.log(DebugPrinter.Key.FE9_TEXT_LOADER, "Loaded text entry: " + id);
		}
	}
	
	public String textStringForIdentifier(String identifier) {
		return messageFile.getStringWithIdentifier(identifier);
	}
	
	public void setStringForIdentifier(String identifier, String string) {
		messageFile.addStringWithIdentifier(identifier, string);
	}

}
