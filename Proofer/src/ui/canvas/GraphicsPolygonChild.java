package ui.canvas;

import geometry.shapes.Arc;
import geometry.shapes.Shape;

/**
 * Represents a child in a {@link GraphicsPolygon}. 
 * Can be rendered to the screen by the {@link Drawable#draw(Canvas)} method.
 * @author David Dinkevich
 */
public class GraphicsPolygonChild extends GraphicsShape<Shape> {
	private GraphicsTriangle parentTri;
	
	public GraphicsPolygonChild(Brush brush, GraphicsTriangle tri) {
		parentTri = tri;
	}
	
	public GraphicsPolygonChild(GraphicsTriangle poly) {
		this(new Brush(), poly);
	}
	
	public GraphicsPolygonChild(Brush brush) {
		this(brush, new GraphicsTriangle());
	}
	
	public GraphicsPolygonChild() {
		this(new Brush());
	}
	
	@Override
	public boolean equals(Object o) {
		if (super.equals(o))
			return true;
		if (!(o instanceof GraphicsPolygonChild))
			return false;
		GraphicsPolygonChild p = (GraphicsPolygonChild)o;
		return parentTri.equals(p.parentTri);
	}
	
	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + parentTri.hashCode();
		return result;
	}
	
	@Override
	public void draw(Canvas c) {
		super.draw(c);
		Shape newShape = parentTri.getShapeOfChild(getShape().getName());
		if (newShape != null) {
			setShape(newShape);
			if (getShape() instanceof Arc) {
				c.arc((Arc)getShape());
			}
		}
	}
	
	public GraphicsTriangle getParentPolygon() {
		return parentTri;
	}
	
	public void setParentPolygon(GraphicsTriangle poly) {
		parentTri = poly;
	}
}
