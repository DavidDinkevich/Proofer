package ui.canvas;

import geometry.Dimension;
import geometry.shapes.Angle;
import geometry.shapes.Arc;
import geometry.shapes.Segment;
import geometry.shapes.Shape;

import util.Utils;

/**
 * Represents an angle in a {@link GraphicsPolygon}.
 * Can be rendered to the screen by the {@link Drawable#draw(Canvas)} method.
 * @author David Dinkevich
 */
public class GraphicsPolygonAngle extends GraphicsPolygonChild<Angle> {

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
	
	/**
	 * The {@link Shape} of this {@link GraphicsPolygonAngle} is an {@link Angle}.
	 * However, when we draw this object to a {@link Canvas}, we want to draw an
	 * {@link Arc}, not an angle. This method derives an arc from the angle shape.
	 * @return the arc
	 */
	private Arc getArcShape() {
		// Get the name of the center vertex (it's in sync with the poly's name)
		String vertName = getShape().getChildren().get(1).getName();
		// Get segments adjacent to vertex
		Segment[] adjSegs = getParentPolygon().getShape().getAdjacentSegments(vertName);
		// Lengths of segments
		final float s0Len = adjSegs[0].getLength(true);
		final float s1Len = adjSegs[1].getLength(true);
		// Fraction of shorter segment that will be arc size
		final float fraction = 0.4f;
		// The size of the arc: shorter segment * 0.2
		final float arcSize = Math.min(s0Len, s1Len) * fraction;
		// Derive arc
		return Utils.getArc(getShape(), new Dimension(arcSize));
	}
	
	@Override
	public void draw(Canvas c) {
		super.draw(c);
		// Draw the arc
		c.arc(getArcShape());
	}
}
