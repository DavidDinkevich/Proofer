package ui.canvas;

import geometry.shapes.Segment;

public class GraphicsSegment extends GraphicsShape<Segment> {
	public GraphicsSegment(Brush brush, Segment se) {
		super(brush, se);
	}
	
	public GraphicsSegment(Segment se) {
		super(se);
	}
	
	public GraphicsSegment(GraphicsSegment other) {
		super(other);
		setShape(new Segment(other.getShape()));
	}
	
	@Override
	public void draw(Canvas c) {
		super.draw(c);
		c.line(getShape());
	}
}
