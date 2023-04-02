package random.gba.randomizer.shuffling.data;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Dto containing the information of which pixels from the source image need to
 * be placed where in the compressed format
 */
public class PortraitChunkInfo {
	
	/**
	 * the position and dimensions of the current chunk in the original Portrait
	 */
	private Rectangle rect;
	
	/**
	 * the point in the formatted frame to place the current chunk
	 */
	private Point point;

	public PortraitChunkInfo(Rectangle rect, Point point) {
		this.rect = rect;
		this.point = point;
	}

	public Point getPoint() {
		return point;
	}

	public Rectangle getRect() {
		return rect;
	}

	public void setRect(Rectangle rect) {
		this.rect = rect;
	}

	public void setPoint(Point point) {
		this.point = point;
	}
	
	public String toString() {
		return String.format("PortraitChunkInfo: rectangle: %s, point %s", rect, point);
	}
}