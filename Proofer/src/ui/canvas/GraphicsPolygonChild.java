package ui.canvas;

import geometry.shapes.Shape;

import ui.canvas.diagram.UIDiagramLayers;

/**
 * Represents a child in a {@link GraphicsPolygon}. 
 * Can be rendered to the screen by the {@link Drawable#draw(Canvas)} method.
 * @author David Dinkevich
 */
public abstract class GraphicsPolygonChild extends GraphicsShape<Shape> {
	private GraphicsTriangle parentTri;
	
	public GraphicsPolygonChild(Brush brush, GraphicsTriangle tri, String childName) {
		// Specify that we want to use the constructor that accepts a Shape object
		super(brush, (Shape)tri.getShape().getChild(childName));
		parentTri = tri;
		setLayer(UIDiagramLayers.POLYGON_COMPONENT);
	}

	public GraphicsPolygonChild(GraphicsTriangle poly, String childName) {
		this(new Brush(), poly, childName);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GraphicsPolygonChild))
			return false;
		GraphicsPolygonChild p = (GraphicsPolygonChild)o;
		return super.equals(o) && parentTri.equals(p.parentTri);
	}
	
	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + parentTri.hashCode();
		return result;
	}
	
	public GraphicsTriangle getParentPolygon() {
		return parentTri;
	}
	
	public void setParentPolygon(GraphicsTriangle poly) {
		parentTri = poly;
	}
}
