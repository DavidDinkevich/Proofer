package ui.canvas;

import geometry.shapes.Arc;
import geometry.shapes.Shape;

import ui.canvas.diagram.UIDiagramLayers;
import util.Utils;

/**
 * Represents a child in a {@link GraphicsPolygon}. 
 * Can be rendered to the screen by the {@link Drawable#draw(Canvas)} method.
 * @author David Dinkevich
 */
public class GraphicsPolygonChild extends GraphicsShape<Shape> {
	private GraphicsTriangle parentTri;
	
	public GraphicsPolygonChild(Brush brush, GraphicsTriangle tri, String childName) {
		// Specify that we want to use the constructor that accepts a Shape object
		super((Shape)tri.getShapeOfChild(childName));
		parentTri = tri;
		setLayer(UIDiagramLayers.POLYGON_COMPONENT);
	}

	public GraphicsPolygonChild(GraphicsTriangle poly, String childName) {
		this(new Brush(), poly, childName);
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
		String fullName = Utils.getFullNameOfAngle(parentTri.getShape().getName(), getShape().getName());
		Shape newShape = parentTri.getShapeOfChild(fullName);
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
