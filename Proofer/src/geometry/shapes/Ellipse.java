package geometry.shapes;

import java.util.Collections;
import java.util.List;

import geometry.Dimension;
import geometry.Vec2;
import geometry.proofs.Figure;

/**
 * An Ellipse.
 * @author David Dinkevich
 */
public class Ellipse extends RectEllipse {
	
	public Ellipse(Vec2 loc, Dimension size) {
		super(loc, size);
		init();
	}
	public Ellipse(Vec2 loc) {
		super(loc, Dimension.ZERO);
		init();
	}
	public Ellipse(Dimension size) {
		super(size);
		init();
	}
	public Ellipse() {
		init();
	}
	public Ellipse(RectEllipse other) {
		super(other);
	}
	
	private void init() {
		setNameLengthRange(1, 1, false);
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o) && o instanceof Ellipse;
	}

	@Override
	public float getPerimeter() {
		throw new RuntimeException("The math is too hard for me :(");
	}

	@Override
	public float getArea() {
		// Specifically for an ellipse, but works for a circle too
		return ((float)Math.PI)*getSize().getWidth()*getSize().getHeight();
	}

	@Override
	public boolean containsPoint(Vec2 point) {
//		float rx = getSize().getWidth() / 2f;
//		float ry = getSize().getHeight() / 2f;
//		float tx = (getCenterPoint().x - (getCenterPoint().x + rx)) / rx;
//		float ty = (getCenterPoint().y - (getCenterPoint().y + ry)) / ry;
//		return tx * tx + ty * ty < 1f;
		
		Vec2 loc = getCenter();
		Dimension size = getSize();
		if (size.getWidth() == size.getHeight()) { // If this ellipse is a circle
			return Vec2.dist(loc, point) < size.getWidth()/2f;
		} else {
			// TODO: THIS IS NOT MATHEMATICALLY CORRECT. THIS TREATS THE ELLIPSE AS A RECT
			return point.getX() > loc.getX() - size.getWidth()/2f && point.getX() <
					loc.getX() + size.getWidth()/2f 
					&& point.getY() > loc.getY() - size.getHeight()/2f && point.getY()
					< loc.getY() + size.getHeight()/2f;
		}
	}
	
	@Override
	public Rect getBoundaryRect() {
		return new Rect(this);
	}
	
	@Override
	public List<Figure> getChildren() {
		return Collections.emptyList();
	}
	
	@Override
	public Figure getChild(String name) {
		return null;
	}
	
	@Override
	public boolean isValidName(String name) {
		// Name length is only one char
		return getName().equals(name);
	}
}
