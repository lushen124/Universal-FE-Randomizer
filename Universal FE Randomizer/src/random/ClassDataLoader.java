package random;

import java.util.HashMap;
import java.util.Map;

import fedata.FEBase;
import fedata.FEClass;
import fedata.fe7.FE7Class;
import fedata.fe7.FE7Data;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;

public class ClassDataLoader {

private FEBase.GameType gameType;
	
	private Map<Integer, FEClass> classMap = new HashMap<Integer, FEClass>();

	public ClassDataLoader(FEBase.GameType gameType, FileHandler handler) {
		super();
		this.gameType = gameType;
		
		switch (gameType) {
			case FE7:
				for (FE7Data.CharacterClass charClass : FE7Data.CharacterClass.values()) {
					long offset = FE7Data.CharacterClass.dataOffsetForClass(charClass);
					byte[] classData = handler.readBytesAtOffset(offset, FE7Data.BytesPerClass);
					classMap.put(charClass.ID, new FE7Class(classData, offset));
				}
				break;
			default:
				break;
		}
	}
	
	public FEClass classForID(int classID) {
		switch (gameType) {
			case FE7:
				return classMap.get(classID);
			default:
				return null;
		}
	}
	
	public void commit() {
		for (FEClass charClass : classMap.values()) {
			charClass.commitChanges();
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (FEClass charClass : classMap.values()) {
			charClass.commitChanges();
			if (charClass.hasCommittedChanges()) {
				Diff charDiff = new Diff(charClass.getAddressOffset(), charClass.getData().length, charClass.getData(), null);
				compiler.addDiff(charDiff);
			}
		}
	}
	
}
