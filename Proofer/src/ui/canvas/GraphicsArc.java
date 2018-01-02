package ui.canvas;

import geometry.shapes.Arc;

import util.Utils;

public class GraphicsArc extends GraphicsShape<Arc> {
	public GraphicsArc(Brush brush, Arc shape) {
		super(brush, shape);
	}
	
	public GraphicsArc(Arc shape) {
		this(new Brush(), shape);
	}
	
	public GraphicsArc(Brush brush) {
		this(brush, new Arc());
	}
	
	public GraphicsArc() {
		this(new Brush());
	}
	
	public GraphicsArc(GraphicsArc other) {
		super(other);
		setShape(new Arc(other.getShape()));
	}
	
	@Override
	public void draw(Canvas c) {
		super.draw(c);
		/*
		 * Handle situation in which the start angle is greater than the end angle.
		 */
		final float startAngle = getShape().getStartAngle();
		final float endAngle = getShape().getStopAngle();
		
		if (startAngle > endAngle) {
			// Convert the start angle to the "raw" format (allows negative values)
			final float newStartAngle = startAngle-Utils.TWO_PI;
			// Draw the arc with the new start angle
			c.arc(getShape().getCenter(true), getShape().getSize(), newStartAngle, endAngle);
		} else {
			// Draw the arc normally
			c.arc(getShape());
		}
	}
}
