package ui.canvas;

import geometry.shapes.Arc;
import util.Utils;

/**
 * Represents an angle in a {@link GraphicsPolygon}.
 * Can be rendered to the screen by the {@link Drawable#draw(Canvas)} method.
 * @author David Dinkevich
 */
public class GraphicsPolygonAngle extends GraphicsPolygonChild {

	public GraphicsPolygonAngle(Brush brush, GraphicsTriangle tri, String childName) {
		super(brush, tri, childName);
	}

	public GraphicsPolygonAngle(GraphicsTriangle poly, String childName) {
		super(poly, childName);
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o) && o instanceof GraphicsPolygonAngle;
	}
	
	@Override
	public void draw(Canvas c) {
		super.draw(c);
		// Get the name of the parent polygon
		String parentPolyName = getParentPolygon().getShape().getName();
		/*
		 * Get the full name of this angle in the parent polygon.
		 * This is necessary because we can't just use the name of
		 * the shape (it's an arc, whose name is one char long). Therefore,
		 * we must use a utility method to get the full name of the angle.
		 */
		String fullName = Utils.getFullNameOfAngle(parentPolyName, getShape().getName());
		// Using the full name of the angle, get the child in the parent polygon
		Arc newShape = getParentPolygon().getShapeOfChild(fullName);
		if (newShape != null) {
			// Update the shape (why not?)
			setShape(newShape);
			// Draw the arc
			c.arc((Arc)getShape());
		}
	}
}
