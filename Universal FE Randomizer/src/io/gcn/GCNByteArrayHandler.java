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
		return nextReadIndex < byteArray.length ? byteArray[nextReadIndex++] : 0;
	}

	@Override
	public byte[] continueReadingBytes(int numBytes) {
		byte[] result = Arrays.copyOfRange(byteArray, nextReadIndex, Math.min(byteArray.length, nextReadIndex + numBytes));
		nextReadIndex += result.length;
		return result;
	}

	@Override
	public int continueReadingBytes(byte[] buffer) {
		int bytesRead = 0;
		while (bytesRead < buffer.length && nextReadIndex < byteArray.length) {
			buffer[bytesRead++] = byteArray[nextReadIndex++];
		}
		
		return bytesRead;
	}

	@Override
	public byte[] continueReadingBytesUpToNextTerminator(long maxOffset) {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		while (nextReadIndex < maxOffset && nextReadIndex < byteArray.length) {
			builder.appendByte(byteArray[nextReadIndex++]);
		}
		return builder.toByteArray();
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
