package ui.canvas.selection;

import geometry.Vec2;
import geometry.shapes.Polygon;
import geometry.shapes.Rect;
import geometry.shapes.Segment;
import geometry.shapes.Shape;

import ui.canvas.GraphicsRect;
import ui.canvas.StyleManager;

/**
 * A box that stores all {@link Selector}s within it. The equivalent of the icon
 * selector box that appears when you drag the mouse over icons on a
 * computer home screen.
 * @author David Dinkevich
 */
public class SelectionBox extends GraphicsRect {
	private Vec2.Mutable corner1, corner2;
	
	public SelectionBox(Vec2 corner1, Vec2 corner2) {
		super(StyleManager.getSelectionContainerBrush(), new Rect());
		// Random name - just to be able to tell the difference between each vertex
		getShape().setName("ABCD");
		this.corner1 = new Vec2.Mutable(corner1);
		this.corner2 = new Vec2.Mutable(corner2);
		resize();
	}
	
	public SelectionBox() {
		this(Vec2.ZERO, Vec2.ZERO);
	}
		
	private void resize() {
		Vec2 loc = new Vec2(
				corner1.getX() + (corner2.getX() - corner1.getX()) / 2,
				corner1.getY() - (corner1.getY() - corner2.getY()) / 2
		);
		getShape().setCenter(loc);
		getShape().setSize(
				Math.abs(corner2.getX() - corner1.getX()), 
				Math.abs(corner1.getY() - corner2.getY())
		);		
	}
	
	/**
	 * Check if the given object is covered by this {@link SelectionBox}.
	 */
	public boolean coversObject(Shape object) {
		Segment[] mySides = getShape().getSides();

		if (object instanceof Polygon) {
			Polygon poly = (Polygon) object;
			Segment[] polySides = poly.getSides();
			for (Segment mySide : mySides) {
				for (Segment theirSide : polySides) {
					if (Segment.segmentsDoIntersect(mySide, theirSide)) {
						return true;
					}
				}
			}
		}
		else if (object instanceof Segment) {
			Segment other = (Segment) object;

			for (Segment mySide : mySides) {
				if (Segment.segmentsDoIntersect(mySide, other)) {
					return true;
				}
			}
		}
		else {
			return getShape().containsPoint(object.getCenter());
		}
		
		return false;
	}
	
	public void setCorner1(Vec2 corner1) {
		this.corner1.set(corner1);
		resize();
	}
	
	public void setCorner2(Vec2 corner2) {
		this.corner2.set(corner2);
		resize();
	}
	
	public void setCorners(Vec2 c1, Vec2 c2) {
		setCorner1(c1);
		setCorner2(c2);
		resize();
	}
}
