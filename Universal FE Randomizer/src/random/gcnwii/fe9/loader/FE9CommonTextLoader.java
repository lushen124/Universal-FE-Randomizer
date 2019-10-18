package random.gcnwii.fe9.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9TextEntry;
import io.gcn.GCNFileHandler;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import util.DebugPrinter;
import util.WhyDoesJavaNotHaveThese;

public class FE9CommonTextLoader {
	
	List<FE9TextEntry> allTextEntries;
	Map<String, FE9TextEntry> idToTextEntry;
	
	GCNFileHandler handler;
	
	public FE9CommonTextLoader(GCNISOHandler isoHandler) throws GCNISOException {
		allTextEntries = new ArrayList<FE9TextEntry>();
		idToTextEntry = new HashMap<String, FE9TextEntry>();
		
		handler = isoHandler.handlerForFileWithName(FE9Data.CommonTextFilename);
		long offset = FE9Data.CommonTextDataStartOffset;
		for (int i = 0; i < FE9Data.CommonTextCount; i++) {
			long dataOffset = offset + i * FE9Data.CommonTextEntrySize;
			byte[] data = handler.readBytesAtOffset(dataOffset, FE9Data.CommonTextEntrySize);
			FE9TextEntry textEntry = new FE9TextEntry(data, dataOffset);
			allTextEntries.add(textEntry);
			
			long idOffset = textEntry.getIDOffset() + FE9Data.CommonTextIDStartOffset;
			handler.setNextReadOffset(idOffset);
			byte[] idBytes = handler.continueReadingBytesUpToNextTerminator(idOffset + 0xFF);
			String identifier = WhyDoesJavaNotHaveThese.stringFromAsciiBytes(idBytes);
			idToTextEntry.put(identifier, textEntry);
			
			DebugPrinter.log(DebugPrinter.Key.FE9_TEXT_LOADER, "Loaded text entry: " + identifier);
		}
	}
	
	public String textStringForIdentifier(String identifier) {
		FE9TextEntry textEntry = idToTextEntry.get(identifier);
		if (textEntry == null) { return null; }
		
		long valueOffset = textEntry.getStringOffset();
		handler.setNextReadOffset(valueOffset);
		byte[] stringData = handler.continueReadingBytesUpToNextTerminator(valueOffset + 0xFF);
		String result = WhyDoesJavaNotHaveThese.stringFromAsciiBytes(stringData);
		return result;
	}

}
