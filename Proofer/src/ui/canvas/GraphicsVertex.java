package ui.canvas;

import geometry.Vec2;
import geometry.shapes.Vertex;

public class GraphicsVertex extends GraphicsShape<Vertex> {
	
	public GraphicsVertex(Brush brush, Vertex shape) {
		super(brush, shape);
	}
	
	public GraphicsVertex(Vertex shape) {
		super(shape);
	}
	
	public GraphicsVertex(GraphicsVertex o) {
		super(o);
	}
	
	@Override
	public void draw(AdvancedCanvas c) {
		super.draw(c);
		
		Vec2 center = getShape().getCenter();
		Vec2 offset = new Vec2(10, -26);
		Vec2 labelLoc = Vec2.add(center, offset);
		
		c.fillText(getShape().getName(), labelLoc);
	}
	
	@Override
	public boolean getAllowSelections() {
		return false; // TODO: can vertices be selected?
	}
	
}
