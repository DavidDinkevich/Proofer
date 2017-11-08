package ui.canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import geometry.Vec2;
import geometry.shapes.Angle;
import geometry.shapes.Arc;
import geometry.shapes.Segment;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;
import geometry.shapes.Shape;

import util.Utils;

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
	
	public List<Shape> getShapesOfChildren() {
		List<Shape> shapes = null;
		
		for (int i = 0; i < getShape().getVertexCount(); i++) {
			// Angles
			String vertName = String.valueOf(getShape().getName().charAt(i));
			// Get segments adjacent to vertex
			Segment[] adjSegs = getShape().getAdjacentSegments(vertName);
			// The size of the arc
			final float distToVert = Math.min(adjSegs[0].getLength(true), 
					adjSegs[1].getLength(true)) * 0.2f;
			// Create the arc
			Arc arc = Utils.getArcBetween(adjSegs[0], adjSegs[1], distToVert * 2f);
			// Set the name of the arc to the name of the vertex
			arc.setName(vertName);
			
			if (shapes == null)
				shapes = new ArrayList<>();
			
			shapes.add(arc);
		}
		
		return shapes == null ? Collections.emptyList() : shapes;
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
}
