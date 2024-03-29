package ui.canvas;

import geometry.shapes.Ellipse;

/**
 * A {@link GraphicsEllipse} that graphically represents {@link Ellipse}s.
 * @author David Dinkevich
 */
public class GraphicsEllipse extends GraphicsRectEllipse<Ellipse> {
	public GraphicsEllipse(Brush brush, Ellipse re) {
		super(brush, re);
	}

	public GraphicsEllipse(Ellipse re) {
		super(re);
	}
	
	public GraphicsEllipse(GraphicsEllipse other) {
		super(other);
		setShape(new Ellipse(other.getShape()));
	}
	
	@Override
	public void draw(AdvancedCanvas c) {
		super.draw(c);
		c.fillEllipse(getShape());
		c.strokeEllipse(getShape());
	}
}
