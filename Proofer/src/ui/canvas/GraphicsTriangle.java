package ui.canvas;

import java.util.ArrayList;
import java.util.List;

import geometry.Vec2;
import geometry.shapes.Angle;
import geometry.shapes.Arc;
import geometry.shapes.Segment;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;
import geometry.shapes.Shape;

import ui.canvas.diagram.UIDiagramLayers;
import ui.canvas.diagram.RenderList;

import util.Utils;

/**
 * A {@link GraphicsPolygon} that represents {@link Triangle}s.
 * @author David Dinkevich
 */
public class GraphicsTriangle extends GraphicsPolygon<Triangle> {
	private List<GraphicsShape<?>> childrenToRender;

	public GraphicsTriangle(Brush brush, Triangle shape) {
		super(brush, shape);
		childrenToRender = new ArrayList<>();
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
		childrenToRender = new ArrayList<>(other.childrenToRender);
	}
	
	@Override
	public void draw(Canvas c) {
		super.draw(c);
	}
	
	private boolean childrenToRenderContains(String name) {
		for (GraphicsShape<?> gShape : childrenToRender) {
			if (gShape.getShape().isValidName(name))
				return true;
		}
		return false;
	}
	
	public int getRenderedFigureCount() {
		return childrenToRender.size();
	}
	
	/**
	 * Get the {@link Shape} of the child with the given name.
	 * @param name the name of the {@link Shape}
	 * @return the {@link Shape}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Shape> T getShapeOfChild(String name) {
		// In the case of an angle
		if (Angle.isValidAngleName(name)) {
			String vertName = name.substring(1, 2);
			// Get segments adjacent to vertex
			Segment[] adjSegs = getShape().getAdjacentSegments(vertName);
			// The size of the arc
			final float distToVert = Math.min(adjSegs[0].getLength(true), 
					adjSegs[1].getLength(true)) * 0.2f;
			// Create the arc
			Arc arc = Utils.getArcBetween(adjSegs[0], adjSegs[1], distToVert * 2f);
			// Set the name of the arc to the name of the vertex
			arc.setName(vertName);
			return (T)arc;
		}
		return null;
	}
	
	/**
	 * Get the {@link Shape} of the child at the given point
	 * @param point the point where the child is
	 * @return the {@link Shape} of the child at the given point
	 */
	public Shape getChildAtPoint(Vec2 point) {
		// In the case of an Angle
		for (Vertex vert : getShape().getVertices()) {
			// Get the name of the vertex
			String vertName = vert.getName();
			String fullAngleName = Utils.getFullNameOfAngle(getShape().getName(), vertName);
			Arc arc = getShapeOfChild(fullAngleName);
			Vec2 cent = arc.getCenter(true);
			final float distToVert = arc.getSize().getWidth()/2f;
			// If the mouse is inside this polygon and the mouse is close enough to
			// the vertex
			if (getShape().containsPoint(point, true) &&
					Vec2.dist(point, cent) < distToVert) {
				return arc;
			}
		}
		return null;
	}
	
	/**
	 * Get the child at the given point and add it to the given
	 * {@link RenderList}
	 * @param rList the {@link RenderList}
	 * @param point the point at which the child lies
	 * @return whether or not a child exists at the given point, the
	 * child is not already part of this {@link GraphicsTriangle}'s internal
	 * render-list, and this process was completed without problems.
	 */
	public boolean renderChildAtPoint(RenderList rList, Vec2 point) {
		Shape comp = getChildAtPoint(point);
		if (comp == null)
			return false;
		return renderChild(rList, comp.getName());
	}
	
	public boolean renderChild(RenderList rList, String name) {
		if (childrenToRenderContains(name))
			return false;
		Shape shape = getShapeOfChild(name);
		if (shape instanceof Arc) {
			// Create graphics arc
			GraphicsArc gArc = new GraphicsArc(
					StyleManager.getHighlightedFigureBrush(),
					(Arc)shape
			);
			// Set layer of the graphics arc
			gArc.setLayer(UIDiagramLayers.POLYGON_COMPONENT);
								
			// Render Child
			childrenToRender.add(gArc);
			// Add to render list
			rList.addDrawable(gArc);
			return true;
		}
		return false;
	}

	/**
	 * Empty this {@link GraphicsTriangle}'s internal render-list, and
	 * remove all of the elements in the internal render-list from the
	 * given {@link RenderList}
	 * @param rList the {@link RenderList}
	 */
	public void eraseAllRenderedChildren(RenderList rList) {
		for (int i = childrenToRender.size()-1; i >= 0; i--) {
			rList.removeDrawable(childrenToRender.get(i));
			childrenToRender.remove(i);
		}
	}
}
