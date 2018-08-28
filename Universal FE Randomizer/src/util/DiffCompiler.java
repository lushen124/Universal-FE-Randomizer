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
	
	public DiffCompiler() {
		super();
		
		diffArray = new ArrayList<>();
	}
	
	public void addDiffsFromFile(String diffName) throws IOException {
		InputStream stream = DiffApplicator.class.getClassLoader().getResourceAsStream(diffName + ".diff");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
		String currentLine = bufferedReader.readLine();
		while(currentLine != null) {
			Scanner scanner = new Scanner(currentLine);
			scanner.useDelimiter("[\\s\\W]+");
			long nextAddress = scanner.nextLong(16);
			int existingValue = scanner.nextInt(16);
			int newValue = scanner.nextInt(16);
			
			addDiff(new Diff(nextAddress, 1, new byte[] {(byte)(newValue & 0xFF)}, new byte[] {(byte)(existingValue & 0xFF)}));
			scanner.close();
			currentLine = bufferedReader.readLine();
		}
		
		stream.close();
	}
	
	public void addDiff(Diff newDiff) {
		diffArray.add(newDiff);
	}
	
	public byte[] byteArrayWithDiffs(byte[] byteArray, long startingOffset) {
		byte[] resultByteArray = byteArray.clone();
		AddressRange range = new AddressRange(startingOffset, startingOffset + byteArray.length);
		
		for (Diff diff : diffArray) {
			if (range.contains(diff.address)) {
				int offset = (int)(diff.address - startingOffset);
				for (int i = 0; i < diff.length; i++) {
					if (offset + i >= resultByteArray.length) {
						break;
					}
					resultByteArray[offset + i] = diff.changes[i];
				}
			}
		}
		
		return resultByteArray;
	}
}
