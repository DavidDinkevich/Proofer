package ui.canvas;

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
}
