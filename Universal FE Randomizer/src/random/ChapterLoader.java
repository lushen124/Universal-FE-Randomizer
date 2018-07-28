package random;

import java.util.Set;

import fedata.FEBase;
import fedata.FEChapter;
import fedata.fe7.FE7Chapter;
import fedata.fe7.FE7Data;
import io.FileHandler;
import util.Diff;
import util.DiffCompiler;

public class ChapterLoader {
	
private FEBase.GameType gameType;
	
	private FEChapter[] chapters;

	public ChapterLoader(FEBase.GameType gameType, FileHandler handler) {
		super();
		this.gameType = gameType;
		
		switch (gameType) {
			case FE7:
				int numberOfChapters = FE7Data.Chapter.values().length;
				chapters = new FEChapter[numberOfChapters];
				int i = 0;
				for (FE7Data.Chapter chapter : FE7Data.Chapter.values()) {
					long offset = chapter.charactersOffset;
					int numberOfUnits = chapter.numberOfUnits;
					byte[] chapterData = handler.readBytesAtOffset(offset, FE7Data.BytesPerChapterUnit * numberOfUnits);
					Set<Integer> doNotTouchIndices = chapter.doNotChangeIndexes();
					chapters[i++] = new FE7Chapter(chapterData, offset, numberOfUnits, doNotTouchIndices);
				}
				break;
			default:
				break;
		}
	}
	
	public FEChapter[] allChapters() {
		switch (gameType) {
			case FE7:
				return chapters;
			default:
				return new FEChapter[] {};
		}
	}
	
	public void commit() {
		for (FEChapter chapter : chapters) {
			chapter.commitChanges();
		}
	}
	
	public void compileDiffs(DiffCompiler compiler) {
		for (FEChapter chapter : chapters) {
			chapter.commitChanges();
			if (chapter.hasCommittedChanges()) {
				byte[] chapterData = chapter.getData();
				Diff chapterDiff = new Diff(chapter.getAddressOffset(), chapterData.length, chapterData, null);
				compiler.addDiff(chapterDiff);
			}
		}
	}
}
