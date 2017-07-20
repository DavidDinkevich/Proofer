package geometry.shapes;

import java.util.Collections;
import java.util.List;

import geometry.Dimension;
import geometry.Vec2;
import geometry.proofs.Figure;

/**
 * NOTE: This class is no longer supported. This will not receive updates.
 * Do not use this class, as it poses a significant danger of crashing.
 * @author David Dinkevich
 */
public class Ellipse extends RectEllipse {
	public Ellipse(Vec2 loc, Dimension size) {
		super(loc, size);
	}
	public Ellipse(Vec2 loc) {
		super(loc, Dimension.ZERO);
	}
	public Ellipse(Dimension size) {
		super(size);
	}
	public Ellipse() {
	}
	public Ellipse(RectEllipse other) {
		super(other);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!super.equals(o))
			return false;
		return o instanceof Ellipse;
	}

	@Override
	public float getPerimeter() {
		throw new RuntimeException("The math is too hard for me :(");
	}

	@Override
	public float getArea() {
		// Specifically for an ellipse, but works for a circle too
		return ((float)Math.PI)*getSizeIncludeScale().getWidth()*getSizeIncludeScale().getHeight();
	}

	@Override
	public boolean containsPoint(Vec2 point, boolean incorporateScale) {
//		float rx = getSize().getWidth() / 2f;
//		float ry = getSize().getHeight() / 2f;
//		float tx = (getCenterPoint().x - (getCenterPoint().x + rx)) / rx;
//		float ty = (getCenterPoint().y - (getCenterPoint().y + ry)) / ry;
//		return tx * tx + ty * ty < 1f;
		
		Vec2 loc = incorporateScale ? getScaledCenter() : getCenter();
		Dimension size = incorporateScale ? getSizeIncludeScale() : getSize();
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
		Rect rect = new Rect(getScaledCenter(), getSizeIncludeScale());
		rect.setScale(getScale());
		return rect;
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
		throw new UnsupportedOperationException("Not yet implemented.");
	}
}
