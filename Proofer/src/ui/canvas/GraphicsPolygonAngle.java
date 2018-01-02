package ui.canvas;

import java.util.List;

import geometry.Dimension;
import geometry.Vec2;
import geometry.shapes.Angle;
import geometry.shapes.Arc;
import geometry.shapes.Segment;
import geometry.shapes.Shape;
import geometry.shapes.Vertex;

import util.Utils;

/**
 * Represents an angle in a {@link GraphicsPolygon}.
 * Can be rendered to the screen by the {@link Drawable#draw(Canvas)} method.
 * @author David Dinkevich
 */
public class GraphicsPolygonAngle extends GraphicsPolygonChild<Angle> {
	
	// How much smaller this GraphicsPolygonAngle will be in comparison to the parent triangle
	private static final float SCALE_DOWN_FRACTION = 0.4f;

	public GraphicsPolygonAngle(Brush brush, GraphicsTriangle tri, String angleName) {
		super(brush, tri, validateGivenName(angleName));
	}

	public GraphicsPolygonAngle(GraphicsTriangle poly, String angleName) {
		super(poly, validateGivenName(angleName));
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o) && o instanceof GraphicsPolygonAngle;
	}
	
	/**
	 * Make sure that given name is a valid name for an angle. If it isn't,
	 * throw an IllegalArgumentException.
	 */
	private static String validateGivenName(String name) {
		if (!Angle.isValidAngleName(name))
			throw new IllegalArgumentException("Name is not valid for an Angle.");
		return name;
	}
	
	/*
	 * (non-Javadoc)
	 * The goal of overriding this method is to provide a <i>smaller</i>
	 * version of this Angle. We do this by shrinking the "sides" of the Angle.
	 * @see ui.canvas.GraphicsPolygonChild#getShape()
	 */
	@Override
	public Angle getShape() {
		// Make a copy of this Angle
		Angle copy = new Angle(super.getShape());
		// Get the center (middle vertex) of this Angle
		Vec2 center = copy.getCenter(true);
		// Get the vertices
		List<Vertex> verts = copy.getVertices();
		// Get a vector from the center of this Angle to one of the outer vertices
		Vec2.Mutable seg0 = 
				new Vec2.Mutable(Vec2.sub(verts.get(0).getCenter(true), center));
		Vec2.Mutable seg1 = 
				new Vec2.Mutable(Vec2.sub(verts.get(2).getCenter(true), center));
		// Shrink the vectors that we just got
		seg0.setMag(seg0.getMag() * SCALE_DOWN_FRACTION);
		seg1.setMag(seg1.getMag() * SCALE_DOWN_FRACTION);
		// Update the locations of the copy's vertices
		copy.getVertices().get(0).setCenter(Vec2.add(center, seg0), true);
		copy.getVertices().get(2).setCenter(Vec2.add(center, seg1), true);
		// Return the copy
		return copy;
	}
	
	/**
	 * The {@link Shape} of this {@link GraphicsPolygonAngle} is an {@link Angle}.
	 * However, when we draw this object to a {@link Canvas}, we want to draw an
	 * {@link Arc}, not an angle. This method derives an arc from the angle shape.
	 * @return the arc
	 */
	private Arc getArcShape() {
		// Get the name of this Angle's center vertex (it's in sync with the poly's vertices)
		String vertName = super.getShape().getChildren().get(1).getName();
		// Get segments adjacent to vertex
		Segment[] adjSegs = getParentPolygon().getShape().getAdjacentSegments(vertName);
		// Lengths of segments
		final float s0Len = adjSegs[0].getLength(true);
		final float s1Len = adjSegs[1].getLength(true);
		// The size of the arc: shorter segment * 0.2
		final float arcSize = Math.min(s0Len, s1Len) * SCALE_DOWN_FRACTION;
		// Derive arc
		return Utils.getArc(super.getShape(), new Dimension(arcSize));
	}
	
	@Override
	public void draw(Canvas c) {
		super.draw(c);
		// Draw the arc
		GraphicsArc arc = new GraphicsArc(getBrush(), getArcShape());
		arc.draw(c);
//		c.arc(getArcShape());
	}
}
