package random.gba.randomizer.shuffling.data;

import java.util.List;
import java.util.Optional;

import fedata.general.FEBase;
import org.eclipse.swt.internal.win32.SIZE;

/**
 * Abstract base class for the GBA FE Portrait Formats
 */
public abstract class PortraitFormat {
	
	/**
	 * Returns an Instance of the Portrait Format belonging to the given Game Type.
	 */
	public static PortraitFormat getPortraitFormatForGame(FEBase.GameType g) {
		if (g.equals(FEBase.GameType.FE6)) {
			return new FE6PortraitFormat();
		} else if (g.equals(FEBase.GameType.FE7)) {
			return new FE7PortraitFormat();
		} else if (g.equals(FEBase.GameType.FE8)) {
			return new FE8PortraitFormat();
		}

		throw new IllegalArgumentException(String.format("No Portrait Format found for parameter %s", g.name()));
	}


	/**
	 * Returns the chunk of the complete Portrait image that belongs to the MainPortrait
	 */
	public abstract List<PortraitChunkInfo> getMainPortraitChunks();

	
	/**
	 * Returns the chunk of the complete Portrait image that belongs to the Mini Portrait
	 */
	public abstract List<PortraitChunkInfo> getMiniPortraitChunks();

	
	/**
	 * Returns the chunk of the complete Portrait image that belongs to the Mouth Frames 
	 */
	public abstract List<PortraitChunkInfo> getMouthChunks();

	/**
	 * Returns true if the Main Portrait should be compressed
	 */
	public abstract boolean isMainPortraitCompressed();

	
	/**
	 * Returns true if the Mini Portrait should be compressed
	 */
	public abstract boolean isMiniCompressed();

	
	/**
	 * Returns the size of the Main Portrait in Chunks
	 */
	public abstract SIZE getMainPortraitSize();

	/**
	 * Returns the size of the Mini Portrait in Chunks
	 */
	public abstract SIZE getMiniPortraitSize();

	/**
	 * Returns the size of the Mouth Frames in Chunks
	 */
	public abstract SIZE getMouthChunksSize();
	
	/**
	 * Returns an optional of a byte array contianing the bytes that should be prefixed before all main Portraits 
	 */
	public abstract Optional<byte[]> getMainPortraitPrefix();
}