package ui.canvas;

import ui.canvas.diagram.UIDiagramLayers;

/**
 * All object that inherit from this interface must be able to draw
 * themselves to a <code>AdvancedCanvas</code>.
 * @author David Dinkevich
 */
public interface Drawable {
	/**
	 * Draw to a AdvancedCanvas.
	 * @param c the AdvancedCanvas.
	 */
	public void draw(AdvancedCanvas c);
	/**
	 * Get the layer of this Drawable.
	 * @return the layer
	 */
	default public UIDiagramLayers getLayer() {
		return UIDiagramLayers.DEFAULT;
	}
}
