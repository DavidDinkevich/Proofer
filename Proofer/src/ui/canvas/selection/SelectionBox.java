package ui.canvas.selection;

import geometry.Vec2;
import geometry.shapes.Rect;
import geometry.shapes.Shape;
import geometry.shapes.Shape2D;
import geometry.shapes.Triangle;

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
		if (!(object instanceof Shape2D))
			return false; // TODO: accommodate objects of type Shape
		if (object instanceof Triangle) {
			return Triangle.overlap(getShape(), (Triangle) object);
		}
		// If boundary rects overlap
		return Rect.rectsOverlap(getShape(), ((Shape2D) object).getBoundaryRect());
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
