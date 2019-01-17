package ui.canvas;

import geometry.shapes.Rect;

/**
 * A {@link GraphicsRectEllipse} that visualizes {@link Rect}s.
 * @author David Dinkevich
 *
 */
public class GraphicsRect extends GraphicsRectEllipse<Rect> {
	public GraphicsRect(Brush brush, Rect re) {
		super(brush, re);
	}

	public GraphicsRect(Rect re) {
		super(re);
	}
	
	public GraphicsRect(GraphicsRect other) {
		super(other);
		setShape(new Rect(other.getShape()));
	}
	
	@Override
	public void draw(AdvancedCanvas c) {
		super.draw(c);
		c.fillRect(getShape());
		c.strokeRect(getShape());
	}
}
