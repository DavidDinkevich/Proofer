package ui.canvas;

import java.util.ArrayList;
import java.util.List;

import geometry.Vec2;
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
	private List<String> componentsToRender;

	public GraphicsTriangle(Brush brush, Triangle shape) {
		super(brush, shape);
		componentsToRender = new ArrayList<>();
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
		componentsToRender = new ArrayList<>(other.componentsToRender);
	}
	
	@Override
	public void draw(Canvas c) {
		super.draw(c);
	}
	
	public boolean drawAngle(RenderList rList, Arc arc) {
		if (componentsToRender.contains(arc.getName()))
			return false;
				
		// Create graphics arc
		GraphicsArc gArc = new GraphicsArc(
				StyleManager.getHighlightedFigureBrush(),
				arc
		);
		// Set layer of the graphics arc
		gArc.setLayer("Polygon Components");
							
		// Render component
		componentsToRender.add(arc.getName());
		// Add to render list
		rList.add(gArc);
		
		return true;
	}
	
	public Shape getComponentAtPoint(Vec2 point) {
		// In the case of an Angle
		for (Vertex vert : getShape().getVertices()) {
			// Get the name of the vertex
			String vertName = vert.getName();
			// Get segments adjacent to vertex
			Segment[] adjSegs = getShape().getAdjacentSegments(vertName);
			// The distance to the vertex the mouse must be
			// the length of the smallest segment * 0.2f
			final float distToVert = Math.min(adjSegs[0].getLength(true), 
					adjSegs[1].getLength(true)) * 0.2f;
			// Get loc of vertex
			Vec2 centVLoc = vert.getCenter(true);
			// If the mouse is inside this polygon and the mouse is close enough to
			// the vertex
			if (getShape().containsPoint(point, true) &&
					Vec2.dist(point, centVLoc) < distToVert) {
				// Create the arc
				Arc arc = Utils.getArcBetween(adjSegs[0], adjSegs[1], distToVert * 2f);
				// Set the name of the arc to the name of the vertex
				arc.setName(vertName);
				return arc;
			}
		}
		return null;
	}
	
	public boolean highlightComponentAtPoint(RenderList rList, Vec2 point) {
		Shape comp = getComponentAtPoint(point);
		if (comp == null)
			return false;
		if (comp instanceof Arc) {
			// Draw the Angle to the canvas
			return drawAngle(rList, (Arc)comp);			
		}
		
		return false;
	}
	
	public void unhighlightComponentAtPoint(RenderList rList, Vec2 point) {
		
	}
	
	
}
