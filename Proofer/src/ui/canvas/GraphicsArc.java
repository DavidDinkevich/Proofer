package ui.canvas;

import geometry.shapes.Arc;

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
		c.arc(getShape());
	}
}
