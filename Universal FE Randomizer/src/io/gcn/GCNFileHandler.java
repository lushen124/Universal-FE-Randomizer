package io.gcn;

import io.FileHandler;
import util.DiffCompiler;
import util.WhyDoesJavaNotHaveThese;

public class GCNFileHandler {
	private GCNFSTFileEntry fileEntry;
	private FileHandler handler;
	
	private long nextReadOffset = 0;
	
	private boolean isBatchReading = false;
	private long savedReadOffset = 0;
	
	public GCNFileHandler(GCNFSTFileEntry file, FileHandler handler) {
		this.fileEntry = file;
		this.handler = handler;
		nextReadOffset += fileEntry.fileOffset;
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
		long oldOffset = handler.getNextReadOffset();
		if (!isBatchReading) { handler.setNextReadOffset(nextReadOffset); }
		byte[] outputBytes = handler.continueReadingBytes((int)Math.min(numBytes, getMaxOffset() - nextReadOffset));
		nextReadOffset = handler.getNextReadOffset();
		if (!isBatchReading) { handler.setNextReadOffset(oldOffset); }
		
		return outputBytes;
	}
	
	public int continueReadingBytes(byte[] buffer) {
		long oldOffset = handler.getNextReadOffset();
		if (!isBatchReading) { handler.setNextReadOffset(nextReadOffset); }
		int bytesRead = 0;
		if (buffer.length + nextReadOffset > getMaxOffset()) {
			int remainingBytes = (int)(getMaxOffset() - nextReadOffset);
			WhyDoesJavaNotHaveThese.copyBytesIntoByteArrayAtIndex(continueReadingBytes(remainingBytes), buffer, 0, remainingBytes);
			bytesRead = remainingBytes;
		} else {
			bytesRead = handler.continueReadingBytes(buffer);
		}
		
		nextReadOffset = handler.getNextReadOffset();
		if (!isBatchReading) { handler.setNextReadOffset(oldOffset); }
		
		return bytesRead;
	}
	
	public byte[] continueReadingBytesUpToNextTerminator(long maxOffset) {
		long oldOffset = handler.getNextReadOffset();
		if (!isBatchReading) { handler.setNextReadOffset(nextReadOffset); }
		byte[] outputBytes = handler.continueReadingBytesUpToNextTerminator(maxOffset + nextReadOffset);
		nextReadOffset = handler.getNextReadOffset();
		if (!isBatchReading) { handler.setNextReadOffset(oldOffset); }
		
		return outputBytes;
	}
	
	public byte[] readBytesAtOffset(long offset, int numBytes) {
		assert offset + numBytes < getMaxOffset() : "Attempted to read beyond the bounds of the file.";
		long oldOffset = handler.getNextReadOffset();
		byte[] outputBytes = handler.readBytesAtOffset(offset + fileEntry.fileOffset, numBytes);
		nextReadOffset = handler.getNextReadOffset();
		handler.setNextReadOffset(oldOffset);
		
		return outputBytes;
	}

	
	public long getFileLength() {
		return fileEntry.fileSize;
	}
	
}
