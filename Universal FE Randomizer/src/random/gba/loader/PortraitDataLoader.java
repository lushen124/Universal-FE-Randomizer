package random.gba.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.FileHandler;
import random.gba.randomizer.shuffling.GBAFEShufflingDataProvider;
import random.gba.randomizer.shuffling.data.GBAFEPortraitData;
import util.Diff;
import util.DiffCompiler;

public class PortraitDataLoader {
	private GBAFEShufflingDataProvider provider;

	/**
	 * The Main Unit portaits
	 */
	private Map<Integer, GBAFEPortraitData> portraitData = new HashMap<>();

	public PortraitDataLoader(GBAFEShufflingDataProvider provider, FileHandler handler) {
		this.provider = provider;
		long baseAddress = provider.portraitDataTableAddress();
		// Skip the first two entries in the portrait table, they are junk
		for (int i = 2; i < provider.numberOfPortraits(); i++) {
			long offset = baseAddress + (provider.bytesPerPortraitEntry() * i);
			byte[] originalData = handler.readBytesAtOffset(offset, provider.bytesPerPortraitEntry());
			portraitData.put(i, provider.portraitDataWithData(originalData, offset, i));
			portraitData.values().forEach(pdd -> pdd.setNewPalette(handler.readBytesAtOffset(pdd.getPalettePointerAsLong(), 32)));
		}
	}

	public GBAFEPortraitData getPortraitDataByFaceId(int faceId) {
		return portraitData.get(faceId);
	}

	public void commit() {
		for (GBAFEPortraitData portrait : portraitData.values()) {
			portrait.commitChanges();
		}
	}

	public void compileDiffs(DiffCompiler compiler) {
		List<Integer> handledPortraits = new ArrayList<>();
		for (GBAFEPortraitData portrait : portraitData.values()) {
			portrait.commitChanges();

			// If this portrait doesn't have changes or was already handled by a different
			// related portrait, skip it
			if (!portrait.hasCommittedChanges() || handledPortraits.contains(portrait.getFaceId())) {
				continue;
			}

			Diff paletteDiff = new Diff(portrait.getPalettePointerAsLong(), portrait.getNewPalette().length,
					portrait.getNewPalette(), null);
			compiler.addDiff(paletteDiff);

			compiler.addDiff(createPortraitDiff(portrait));

			handledPortraits.addAll(synchronizeRelatedPortraits(portrait.getFaceId(), portrait, compiler));
			handledPortraits.add(portrait.getFaceId());
		}
	}

	private Diff createPortraitDiff(GBAFEPortraitData portrait) {
		return new Diff(portrait.getAddressOffset(), portrait.getData().length, portrait.getData(), null);
	}
	
	/**
	 * Doesn't really belong here, but it's the most convienent place to have it..
	 */
	public List<Integer> getRelatedNameIndicies(int nameIndex){
		return provider.getRelatedNames(nameIndex);
	}

	/**
	 * Some characters have multiple different Portraits, usually this is because in
	 * one they default to having eyes open and one where their eyes are closed, we
	 * should ensure that these are all changed.
	 * 
	 * FE7 has a lot of different variations for the lords... I'm not about to make
	 * that kinda variation for every character in the games, so just override it 
	 * with the default Portrait of the character being randomized in that slot...
	 * 
	 * Similarly for FE8, Lyon, Eirika, and Ephraim have flashback variations,
	 * unless there is a smart way to convert the pallet to the muted variant, it
	 * makes more sense to just override them (though it will still look silly)
	 */
	private List<Integer> synchronizeRelatedPortraits(Integer faceId, GBAFEPortraitData mainPortraitData,
			DiffCompiler compiler) {
		List<Integer> relatedPortraits = provider.getRelatedPortraits(faceId);
		List<Integer> handledPortraits = new ArrayList<>();
		if (relatedPortraits == null) {
			// no related portraits, nothing to do here
			return handledPortraits;
		}

		// For all the related Portraits, override the information that we change (everything but which frame is used)
		for (Integer relatedPortraitId : relatedPortraits) {
			GBAFEPortraitData relatedPortrait = portraitData.get(relatedPortraitId);
			relatedPortrait.setFacialFeatureCoordinates(mainPortraitData.getFacialFeatureCoordinates());
			relatedPortrait.setMainPortraitPointer(mainPortraitData.getMainPortraitPointer());
			relatedPortrait.setMiniPortraitPointer(mainPortraitData.getMiniPortraitPointer());
			if (relatedPortrait.useSeparateMouthFrames()) {
				relatedPortrait.setMouthFramesPointer(mainPortraitData.getMouthFramesPointer());
			}
			relatedPortrait.setPalettePointer(mainPortraitData.getPalettePointer());

			handledPortraits.add(relatedPortraitId);
			compiler.addDiff(createPortraitDiff(relatedPortrait));
		}

		return handledPortraits;
	}
}