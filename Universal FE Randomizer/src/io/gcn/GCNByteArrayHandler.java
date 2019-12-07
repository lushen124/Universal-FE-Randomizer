package io.gcn;

import java.util.Arrays;

import io.FileHandler;
import util.ByteArrayBuilder;

public class GCNByteArrayHandler extends GCNFileHandler {
	
	private byte[] byteArray;
	
	private int nextReadIndex;

	public GCNByteArrayHandler(GCNFSTFileEntry file, FileHandler handler, byte[] byteArray) {
		super(file, handler);
		
		this.byteArray = byteArray;
		nextReadIndex = 0;
	}
	
	@Override
	public long getNextReadOffset() {
		return nextReadIndex;
	}

	@Override
	public void setNextReadOffset(long newOffset) {
		nextReadIndex = (int)newOffset;
	}

	@Override
	public void beginBatchRead() {
		// Do nothing.
	}

	@Override
	public void endBatchRead() {
		// Do nothing.
	}

	@Override
	public byte continueReadingNextByte() {
		Byte diffedValue = diffCompiler.diffValueAtOffset(nextReadIndex);
		if (diffedValue != null) { return diffedValue; } 
		return nextReadIndex < byteArray.length ? byteArray[nextReadIndex++] : 0;
	}

	@Override
	public byte[] continueReadingBytes(int numBytes) {
		long readIndex = nextReadIndex;
		
		byte[] result = Arrays.copyOfRange(byteArray, nextReadIndex, Math.min(byteArray.length, nextReadIndex + numBytes));
		nextReadIndex += result.length;
		
		diffCompiler.applyDiffs(result, readIndex);
		return result;
	}

	@Override
	public int continueReadingBytes(byte[] buffer) {
		long readIndex = nextReadIndex;
		
		int bytesRead = 0;
		while (bytesRead < buffer.length && nextReadIndex < byteArray.length) {
			buffer[bytesRead++] = byteArray[nextReadIndex++];
		}
		
		diffCompiler.applyDiffs(buffer, readIndex);
		
		return bytesRead;
	}

	@Override
	public byte[] continueReadingBytesUpToNextTerminator(long maxOffset) {
		long readIndex = nextReadIndex;
		
		ByteArrayBuilder builder = new ByteArrayBuilder();
		while (nextReadIndex < maxOffset && nextReadIndex < byteArray.length) {
			byte byteRead = byteArray[nextReadIndex++];
			if (byteRead == 0) { break; }
			builder.appendByte(byteRead);
		}
		
		return diffCompiler.byteArrayWithDiffs(builder.toByteArray(), readIndex);
	}

	@Override
	public byte[] readBytesAtOffset(long offset, int numBytes) {
		setNextReadOffset(offset);
		return continueReadingBytes(numBytes);
	}

	@Override
	public long getFileLength() {
		return byteArray.length;
	}

}
