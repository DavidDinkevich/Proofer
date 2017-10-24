package ui.canvas;

import geometry.shapes.RectEllipse;

/**
 * A {@link GraphicsShape2D} that represents {@link RectEllipse}s.
 * @author David Dinkevich
 */
public abstract class GraphicsRectEllipse<T extends RectEllipse> extends GraphicsShape<T> {
	public GraphicsRectEllipse(Brush brush, T re) {
		super(brush, re);
	}
	
	public GraphicsRectEllipse(T re) {
		super(re);
	}
	
	protected GraphicsRectEllipse(GraphicsRectEllipse<T> other) {
		super(other);
	}
}
