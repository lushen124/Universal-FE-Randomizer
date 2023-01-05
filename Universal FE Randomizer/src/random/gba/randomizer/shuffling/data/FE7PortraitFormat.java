package random.gba.randomizer.shuffling.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.win32.SIZE;

/**
 * Class providing the Format of the FE6 Portraits for insertion
 */
public class FE7PortraitFormat extends PortraitFormat {

	@Override
	public List<PortraitChunkInfo> getMainPortraitChunks() {
		List<PortraitChunkInfo> result = new ArrayList<>();
		// See: https://www.dropbox.com/sh/3m004vettv9g3og/AADKwPB0kLMKSU8I4dvK1pa7a/Crazycolorz5's%20Stuff/EA%20Formatting%20Suite/Portrait%20Formatter?dl=0&preview=PortraitFormatter.hs&subfolder_nav_tracking=1
		result.add(new PortraitChunkInfo(new Rectangle(2, 0, 8, 4), new Point(0, 0)));
		result.add(new PortraitChunkInfo(new Rectangle(2, 4, 8, 4), new Point(8, 0)));
		result.add(new PortraitChunkInfo(new Rectangle(2, 8, 4, 2), new Point(16, 0)));
		result.add(new PortraitChunkInfo(new Rectangle(6, 8, 4, 2), new Point(16, 2)));
		result.add(new PortraitChunkInfo(new Rectangle(0, 6, 2, 4), new Point(20, 0)));
		result.add(new PortraitChunkInfo(new Rectangle(10, 6, 2, 4), new Point(22, 0)));
		result.add(new PortraitChunkInfo(new Rectangle(12, 6, 4, 4), new Point(24, 0))); 
		result.add(new PortraitChunkInfo(new Rectangle(12, 8, 4, 2), new Point(24, 2))); 
		result.add(new PortraitChunkInfo(new Rectangle(12, 10, 4, 2), new Point(28, 0))); 
		result.add(new PortraitChunkInfo(new Rectangle(12, 12, 4, 2), new Point(28, 2))); 
		return result;
	}

	@Override
	public List<PortraitChunkInfo> getMouthChunks() {
		List<PortraitChunkInfo> result = new ArrayList<>();
		result.add(new PortraitChunkInfo(new Rectangle(0, 10, 4, 2), new Point(0, 0)));
		result.add(new PortraitChunkInfo(new Rectangle(4, 10, 4, 2), new Point(0, 2)));
		result.add(new PortraitChunkInfo(new Rectangle(8, 10, 4, 2), new Point(0, 4)));
		result.add(new PortraitChunkInfo(new Rectangle(0, 12, 4, 2), new Point(0, 6)));
		result.add(new PortraitChunkInfo(new Rectangle(4, 12, 4, 2), new Point(0, 8)));
		result.add(new PortraitChunkInfo(new Rectangle(8, 12, 4, 2), new Point(0, 10)));
		return result;
	}

	@Override
	public List<PortraitChunkInfo> getMiniPortraitChunks() {
		return Arrays.asList(new PortraitChunkInfo(new Rectangle(12, 2, 4, 4), new Point(0, 0)));
	}

	@Override
	public SIZE getMainPortraitSize() {
		SIZE size = new SIZE();
		size.cx = 32;
		size.cy = 4;
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
		SIZE size = new SIZE();
		size.cx = 4;
		size.cy = 12;
		return size;
	}

	@Override
	public boolean isMiniCompressed() {
		return true;
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