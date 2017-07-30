package ui.canvas;

import geometry.Dimension;
import geometry.shapes.RectEllipse;

/**
 * A {@link GraphicsShape2D} that represents {@link RectEllipse}s.
 * @author David Dinkevich
 */
public abstract class GraphicsRectEllipse<T extends RectEllipse> extends GraphicsShape2D<T> {
	public GraphicsRectEllipse(Brush brush, T re) {
		super(brush, re);
	}
	public GraphicsRectEllipse(Brush brush) {
		super(brush);
	}
	public GraphicsRectEllipse(T re) {
		super(re);
	}
	public GraphicsRectEllipse() {
	}
	
	protected GraphicsRectEllipse(GraphicsRectEllipse<T> other) {
		super(other);
	}
	
	@Override
	public boolean offScreen(Canvas c) {
		final float x = getLoc().getX(), y = getLoc().getY();
		final int w = c.width, h = c.height;
		
		Dimension size = getShape().getSize(true);
		
		return x - size.getWidth()/2f < 0f || x + size.getWidth()/2 >= w || y - size.getHeight()/2 < 0f
				|| y + size.getHeight()/2f >= h;
	}
	
	/**
	 * Get the size of this {@link GraphicsRectEllipse}. This includes the stroke weight
	 * <i>if</i> this object's {@link GraphicsShape2D#includeStrokeWeightInCalculations()} = true.
	 * Otherwise, it doesn't.
	 * @return the size
	 */
	public Dimension getSize() {
		Dimension size = getShape().getSize(true);
		return includeStrokeWeightInCalculations() ? Dimension.
				add(size, getBrush().getStrokeWeight(), false) : size;
	}
	/**
	 * Get the size of this {@link GraphicsRectEllipse}. This will include the stroke weight
	 * if the boolean parameter = true. Otherwise, it won't. NOTE: this object's
	 * {@link GraphicsShape2D#includeStrokeWeightInCalculations()} will be set to the boolean
	 * parameter.
	 */
	public Dimension getSize(boolean includeStrokeWeight) {
		setIncludeStrokeWeightInCalculations(includeStrokeWeight);
		return getSize();
	}
}
