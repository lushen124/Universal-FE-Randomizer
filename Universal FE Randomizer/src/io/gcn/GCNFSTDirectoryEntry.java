package io.gcn;

import java.util.ArrayList;
import java.util.List;

public class GCNFSTDirectoryEntry extends GCNFSTEntry {
	// These are index offsets. They are the offset from the beginning of fst.bin if multiplied by 0xC.
	public long parentOffset;
	public long nextOffset;
	
	public List<GCNFSTEntry> childEntries;
	
	public GCNFSTDirectoryEntry() {
		childEntries = new ArrayList<GCNFSTEntry>();
	}
}