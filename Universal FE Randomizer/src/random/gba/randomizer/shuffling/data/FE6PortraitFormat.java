package random.gba.randomizer.shuffling.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.win32.SIZE;

/**
 * Class providing the Format of the FE6 Portraits for insertion <br>
 * See:
 * https://www.dropbox.com/sh/3m004vettv9g3og/AADFmL4DZVbE-nEHLS68rvF1a/Nintenlord/Hacking/PortraitInserter/Portraits?dl=0&preview=PortraitFormat.cs&subfolder_nav_tracking=1
 */
public class FE6PortraitFormat extends PortraitFormat {

	@Override
	public List<PortraitChunkInfo> getMainPortraitChunks() {
		List<PortraitChunkInfo> result = new ArrayList<>();
		result.add(new PortraitChunkInfo(new Rectangle(2, 0, 8, 4), new Point(0, 0)));
		result.add(new PortraitChunkInfo(new Rectangle(2, 4, 8, 4), new Point(8, 0)));
		result.add(new PortraitChunkInfo(new Rectangle(2, 8, 4, 2), new Point(16, 0)));
		result.add(new PortraitChunkInfo(new Rectangle(6, 8, 4, 2), new Point(16, 2)));
		result.add(new PortraitChunkInfo(new Rectangle(0, 6, 2, 4), new Point(20, 0)));
		result.add(new PortraitChunkInfo(new Rectangle(10, 6, 2, 4), new Point(22, 0)));
		result.add(new PortraitChunkInfo(new Rectangle(0, 10, 8, 4), new Point(24, 0))); 
		result.add(new PortraitChunkInfo(new Rectangle(8, 10, 4, 1), new Point(0, 4))); // Mouth Frames
		result.add(new PortraitChunkInfo(new Rectangle(8, 11, 4, 1), new Point(4, 4))); // Mouth Frames
		return result;
	}

	@Override
	public List<PortraitChunkInfo> getMouthChunks() {
		// For FE6 these are no separate Mouth Chunks
		return new ArrayList<>();
	}

	@Override
	public List<PortraitChunkInfo> getMiniPortraitChunks() {
		return Arrays.asList(new PortraitChunkInfo(new Rectangle(12, 2, 4, 4), new Point(0, 0)));
	}

	@Override
	public SIZE getMainPortraitSize() {
		SIZE size = new SIZE();
		size.cx = 32;
		size.cy = 5;
		return size;
	}

	@Override
	public SIZE getMiniPortraitSize() {
		SIZE size = new SIZE();
		size.cx = 4;
		size.cy = 4;
		return size;
	}

	@Override
	public SIZE getMouthChunksSize() {
		return null;
	}

	@Override
	public boolean isMiniCompressed() {
		return false;
	}

	@Override
	public boolean isMainPortraitCompressed() {
		return true;
	}

	@Override
	public Optional<byte[]> getMainPortraitPrefix() {
		return Optional.empty();
	}

}