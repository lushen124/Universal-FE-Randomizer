package random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import fedata.FEBase;
import fedata.FEChapter;
import fedata.FEChapterItem;
import fedata.fe7.FE7Chapter;
import fedata.fe7.FE7ChapterItem;
import fedata.fe7.FE7Data;
import io.FileHandler;
import util.AddressRange;
import util.Diff;
import util.DiffCompiler;
import util.FileReadHelper;

public class ChapterLoader {
	
	private FEBase.GameType gameType;
	
	private FEChapter[] chapters;
	private FEChapterItem[] rewards;

	public ChapterLoader(FEBase.GameType gameType, FileHandler handler) {
		super();
		this.gameType = gameType;
		
		switch (gameType) {
			case FE7:
				int numberOfChapters = FE7Data.Chapter.values().length;
				chapters = new FEChapter[numberOfChapters];
				int i = 0;
				List<FEChapterItem> items = new ArrayList<FEChapterItem>();
				for (FE7Data.Chapter chapter : FE7Data.Chapter.values()) {
					long offset = chapter.charactersOffset;
					int numberOfUnits = chapter.numberOfUnits;
					byte[] chapterData = handler.readBytesAtOffset(offset, FE7Data.BytesPerChapterUnit * numberOfUnits);
					Set<Integer> doNotTouchIndices = chapter.doNotChangeIndexes();
					chapters[i++] = new FE7Chapter(chapterData, offset, numberOfUnits, doNotTouchIndices, chapter.isClassSafe());
					
					AddressRange locationEventRange = chapter.locationEventRange;
					if (locationEventRange != null) {
						byte[] locationEventRaw = FileReadHelper.readBytesInRange(locationEventRange, handler);
						for (int locationOffset = 0; locationOffset < locationEventRaw.length; locationOffset += 4) {
							if (locationEventRaw[locationOffset] == 0x07) {
								if (locationEventRaw[locationOffset + 1] == 0 &&
									locationEventRaw[locationOffset + 3] == 0) {
									byte itemID = locationEventRaw[locationOffset + 4];
									if (itemID != 0 &&
										locationEventRaw[locationOffset + 5] == 0 &&
										locationEventRaw[locationOffset + 6] == 0 &&
										locationEventRaw[locationOffset + 7] == 0) {
										byte[] relevantData = Arrays.copyOfRange(locationEventRaw, locationOffset, locationOffset + 12);
										int itemOffset = locationEventRange.start + locationOffset;
										FE7ChapterItem item = new FE7ChapterItem(relevantData, itemOffset);
										locationOffset += 8;
										items.add(item);
									}
								}
							}
						}
					}
					
					AddressRange scriptRange = chapter.scriptRange;
					if (scriptRange != null) {
						byte[] eventScriptRaw = FileReadHelper.readBytesInRange(scriptRange, handler);
						for (int scriptOffset = 0; scriptOffset < eventScriptRaw.length; scriptOffset += 4) {
							if (eventScriptRaw[scriptOffset] == 0x5B) {
								if (eventScriptRaw[scriptOffset + 1] == 0 &&
									eventScriptRaw[scriptOffset + 2] == 0 &&
									eventScriptRaw[scriptOffset + 3] == 0) {
									byte itemID = eventScriptRaw[scriptOffset + 4];
									if (itemID != 0 &&
											eventScriptRaw[scriptOffset + 5] == 0 &&
											eventScriptRaw[scriptOffset + 6] == 0 &&
											eventScriptRaw[scriptOffset + 7] == 0) {
										byte[] relevantData = Arrays.copyOfRange(eventScriptRaw, scriptOffset, scriptOffset + 8);
										long itemOffset = scriptRange.start + scriptOffset;
										FE7ChapterItem item = new FE7ChapterItem(relevantData, itemOffset);
										scriptOffset += 4;
										items.add(item);
									}
								}
							}
						}
					}
				}
				rewards = items.toArray(new FEChapterItem[items.size()]);
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
	
	public FEChapterItem[] allRewards() {
		switch (gameType) {
			case FE7:
				return rewards;
			default:
				return new FEChapterItem[] {};
		}
	}
	
	public void commit() {
		for (FEChapter chapter : chapters) {
			chapter.commitChanges();
		}
		for (FEChapterItem item : rewards) {
			item.commitChanges();
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
		for (FEChapterItem item : rewards) {
			item.commitChanges();
			if (item.hasCommittedChanges()) {
				byte[] rewardData = item.getData();
				Diff rewardDiff = new Diff(item.getAddressOffset(), rewardData.length, rewardData, null);
				compiler.addDiff(rewardDiff);
			}
		}
	}
}
