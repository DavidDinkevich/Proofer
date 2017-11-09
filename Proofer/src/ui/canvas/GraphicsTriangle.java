package ui.canvas;

import geometry.shapes.Angle;
import geometry.shapes.Triangle;

/**
 * A {@link GraphicsPolygon} that represents {@link Triangle}s.
 * @author David Dinkevich
 */
public class GraphicsTriangle extends GraphicsPolygon<Triangle> {
	
	public GraphicsTriangle(Brush brush, Triangle shape) {
		super(brush, shape);
	}
	
	public GraphicsTriangle(Triangle shape) {
		this(new Brush(), shape);
	}
	
	public GraphicsTriangle(Brush brush) {
		this(brush, new Triangle());
	}
	
	public GraphicsTriangle() {
		this(new Brush());
	}
	
	public GraphicsTriangle(GraphicsTriangle other) {
		super(other);
		setShape(new Triangle(other.getShape()));
	}
	
	@Override
	public void draw(Canvas c) {
		super.draw(c);
	}
	
	public GraphicsPolygonChild getGraphicsChild(String name) {
		if (Angle.isValidAngleName(name)) {
			GraphicsPolygonChild child = new GraphicsPolygonAngle(this, name);
			return child;
		}
		return null;
	}
}
