package ui.canvas;

/**
 * All object that inherit from this interface must be able to draw
 * themselves to a <code>Canvas</code>.
 * @author David Dinkevich
 */
public interface Drawable {
	/**
	 * Draw to a Canvas.
	 * @param c the Canvas.
	 */
	public void draw(Canvas c);
}
