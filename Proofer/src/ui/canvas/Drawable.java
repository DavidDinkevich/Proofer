package ui.canvas;

import ui.canvas.diagram.UIDiagramLayers;

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
	/**
	 * Get the layer of this Drawable.
	 * @return the layer
	 */
	default public UIDiagramLayers getLayer() {
		return UIDiagramLayers.DEFAULT;
	}
}
