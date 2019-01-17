package ui.canvas;

import geometry.shapes.Shape;

import ui.canvas.diagram.UIDiagramLayers;

/**
 * Represents a child in a {@link GraphicsPolygon}. 
 * Can be rendered to the screen by the {@link Drawable#draw(Layer)} method.
 * @author David Dinkevich
 */
public abstract class GraphicsPolygonChild<T extends Shape> extends GraphicsShape<T> {
	private GraphicsTriangle parentTri;
	
	@SuppressWarnings("unchecked")
	public GraphicsPolygonChild(Brush brush, GraphicsTriangle tri, String childName) {
		// Specify that we want to use the constructor that accepts a Shape object
		super(brush, (T)tri.getShape().getChild(childName));
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
		GraphicsPolygonChild<?> p = (GraphicsPolygonChild<?>)o;
		return super.equals(o) && parentTri.equals(p.parentTri);
	}
	
	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + parentTri.hashCode();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see ui.canvas.GraphicsShape#getShape()
	 * 
	 * We're overriding this method because this class uses the
	 * Figure.getChild() method. We cannot guarantee that the
	 * getChild() method will return a child that updates its
	 * location and size in relation to the figure. As a result,
	 * we have to make sure that when accessing the shape of this
	 * GraphicsPolygonChild, the shape returned is the most updated
	 * version of the child.
	 */
	@SuppressWarnings("unchecked")
	private T updateShape(String shapeName) {
		setShape((T)parentTri.getShape().getChild(shapeName));
		return super.getShape();
	}
	
	@Override
	public T getShape() {
		String shapeName = super.getShape().getName();
		return updateShape(shapeName);
	}
	
	public GraphicsTriangle getParentPolygon() {
		return parentTri;
	}
	
	public void setParentPolygon(GraphicsTriangle poly) {
		parentTri = poly;
	}
}
