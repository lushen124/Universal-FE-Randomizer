package io.gcn;

import io.FileHandler;
import util.Diff;
import util.DiffCompiler;
import util.WhyDoesJavaNotHaveThese;

public class GCNFileHandler {
	private GCNFSTFileEntry fileEntry;
	private FileHandler handler;
	
	private long nextReadOffset = 0;
	
	private boolean isBatchReading = false;
	private long savedReadOffset = 0;
	
	protected DiffCompiler diffCompiler;
	
	public GCNFileHandler(GCNFSTFileEntry file, FileHandler handler) {
		this.fileEntry = file;
		this.handler = handler;
		nextReadOffset += fileEntry.fileOffset;
		
		diffCompiler = new DiffCompiler();
	}
	
	private long getMaxOffset() {
		return fileEntry.fileOffset + fileEntry.fileSize;
	}
	
	public long getNextReadOffset() {
		return nextReadOffset - fileEntry.fileOffset;
	}
	
	public void setNextReadOffset(long newOffset) {
		nextReadOffset = newOffset + fileEntry.fileOffset;
	}
	
	public byte continueReadingNextByte() {
		if (isBatchReading) {
			return handler.continueReadingNextByte();
		}
		
		long oldOffset = handler.getNextReadOffset();
		handler.setNextReadOffset(nextReadOffset);
		byte outputByte = handler.continueReadingNextByte();
		nextReadOffset++;
		handler.setNextReadOffset(oldOffset);
		return outputByte;
	}
	
	public void beginBatchRead() {
		if (!isBatchReading) {
			isBatchReading = true;
			savedReadOffset = handler.getNextReadOffset();
			handler.setNextReadOffset(nextReadOffset);
		}
	}
	
	public void endBatchRead() {
		if (isBatchReading) {
			isBatchReading = false;
			handler.setNextReadOffset(savedReadOffset);
		}
	}
	
	public byte[] continueReadingBytes(int numBytes) {
		long readIndex = nextReadOffset;
		
		long oldOffset = handler.getNextReadOffset();
		if (!isBatchReading) { handler.setNextReadOffset(nextReadOffset); }
		byte[] outputBytes = handler.continueReadingBytes((int)Math.min(numBytes, getMaxOffset() - nextReadOffset));
		nextReadOffset = handler.getNextReadOffset();
		if (!isBatchReading) { handler.setNextReadOffset(oldOffset); }
		
		diffCompiler.applyDiffs(outputBytes, readIndex);
		
		return outputBytes;
	}
	
	public int continueReadingBytes(byte[] buffer) {
		long readIndex = nextReadOffset;
		
		long oldOffset = handler.getNextReadOffset();
		if (!isBatchReading) { handler.setNextReadOffset(nextReadOffset); }
		int bytesRead = 0;
		if (buffer.length + nextReadOffset > getMaxOffset()) {
			int remainingBytes = (int)(getMaxOffset() - nextReadOffset);
			byte[] actualBytes = continueReadingBytes(remainingBytes);
			WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(actualBytes, buffer, 0, actualBytes.length);
			bytesRead = actualBytes.length;
		} else {
			bytesRead = handler.continueReadingBytes(buffer);
		}
		
		nextReadOffset = handler.getNextReadOffset();
		if (!isBatchReading) { handler.setNextReadOffset(oldOffset); }
		
		diffCompiler.applyDiffs(buffer, readIndex);
		
		return bytesRead;
	}
	
	public byte[] continueReadingBytesUpToNextTerminator(long maxOffset) {
		long readIndex = nextReadOffset;
		
		long oldOffset = handler.getNextReadOffset();
		if (!isBatchReading) { handler.setNextReadOffset(nextReadOffset); }
		byte[] outputBytes = handler.continueReadingBytesUpToNextTerminator(maxOffset + nextReadOffset);
		nextReadOffset = handler.getNextReadOffset();
		if (!isBatchReading) { handler.setNextReadOffset(oldOffset); }
		
		diffCompiler.applyDiffs(outputBytes, readIndex);
		
		return outputBytes;
	}
	
	public byte[] readBytesAtOffset(long offset, int numBytes) {
		assert offset + numBytes < getMaxOffset() : "Attempted to read beyond the bounds of the file.";
		long oldOffset = handler.getNextReadOffset();
		byte[] outputBytes = handler.readBytesAtOffset(offset + fileEntry.fileOffset, numBytes);
		nextReadOffset = handler.getNextReadOffset();
		handler.setNextReadOffset(oldOffset);
		
		diffCompiler.applyDiffs(outputBytes, offset);
		
		return outputBytes;
	}

	
	public long getFileLength() {
		return fileEntry.fileSize;
	}
	
	public boolean hasChanges() {
		return !diffCompiler.diffArray.isEmpty();
	}
	
	public void addChange(Diff change) {
		assert change.address < getFileLength() : "Invalid address for change.";
		
		diffCompiler.addDiff(change);
	}
}
