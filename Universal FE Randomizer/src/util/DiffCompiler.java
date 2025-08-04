package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import io.DiffApplicator;

public class DiffCompiler {
	
	public final ArrayList<Diff> diffArray;
	public final ArrayList<FindAndReplace> replaceArray;
	
	public DiffCompiler() {
		super();
		
		diffArray = new ArrayList<>();
		replaceArray = new ArrayList<>();
	}
	
	public void addDiffsFromFile(String diffName) throws IOException {
		addDiffsFromFile(diffName, 0);
	}
	
	public void addDiffsFromFile(String diffName, long addressOffset) throws IOException {
		InputStream stream = DiffApplicator.class.getClassLoader().getResourceAsStream(diffName + ".diff");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
		String currentLine = bufferedReader.readLine();
		while(currentLine != null) {
			Scanner scanner = new Scanner(currentLine);
			scanner.useDelimiter("[\\s\\W]+");
			long nextAddress = scanner.nextLong(16);
			int existingValue = scanner.nextInt(16);
			int newValue = scanner.nextInt(16);
			
			addDiff(new Diff(nextAddress + addressOffset, 1, new byte[] {(byte)(newValue & 0xFF)}, new byte[] {(byte)(existingValue & 0xFF)}));
			scanner.close();
			currentLine = bufferedReader.readLine();
		}
		
		stream.close();
	}
	
	public void addDiff(Diff newDiff) {
		diffArray.add(newDiff);
	}
	
	public void findAndReplace(FindAndReplace replace) {
		replaceArray.add(replace);
	}
	
	public byte[] byteArrayWithDiffs(byte[] byteArray, long startingOffset) {
		byte[] resultByteArray = byteArray.clone();
		applyDiffs(resultByteArray, startingOffset, resultByteArray.length);
		return resultByteArray;
	}
	
	public void applyDiffs(byte[] byteArray, long startingOffset) {
		applyDiffs(byteArray, startingOffset, byteArray.length);
	}
	
	public void applyDiffs(byte[] byteArray, long startingOffset, long length) {
		AddressRange range = new AddressRange(startingOffset, startingOffset + length);
		DebugPrinter.log(DebugPrinter.Key.DIFF, "Applying Diff to byte array starting from offset 0x" + Long.toHexString(startingOffset) + " for " + byteArray.length + " bytes.");
		
		for (Diff diff : diffArray) {
			if (range.contains(diff.address) || range.contains(diff.address + diff.length)) {
				//DebugPrinter.log(DebugPrinter.Key.DIFF, "Applying Diff starting from 0x" + Long.toHexString(diff.address));
				if (diff.address < startingOffset) {
					// We have a diff that started before this chunk but affects the start of this chunk.
					int diffStartIndex = (int)(startingOffset - diff.address);
					int remainingDiffLength = diff.length - diffStartIndex;
					for (int i = 0; i < remainingDiffLength; i++) {
						if (i >= byteArray.length) { break; }
						byteArray[i] = diff.changes[i + diffStartIndex];
					}
				} else {
					// This diff starts inside the chunk.
					int offset = (int)(diff.address - startingOffset);
					for (int i = 0; i < diff.length; i++) {
						if (offset + i >= byteArray.length) { break; }
						byteArray[offset + i] = diff.changes[i];
					}
				}
			}
		}
	}
	
	public Byte diffValueAtOffset(long offset) {
		for (Diff diff : diffArray) {
			AddressRange range = new AddressRange(diff.address, diff.address + diff.length);
			if (range.contains(offset)) {
				return diff.changes[(int)(offset - diff.address)];
			}
		}
		
		return null;
	}
}
