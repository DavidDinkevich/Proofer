package geometry.shapes;

import geometry.Dimension;
import geometry.Vec2;

/**
 * This object's sole purpose is to encapsulate the similarities between {@link Rect}s and
 * {@link Ellipse}s.
 * @author David Dinkevich
 */
public abstract class RectEllipse extends AbstractShape2D {
	private Dimension.Mutable size;

	public RectEllipse(Vec2 loc, Dimension size) {
		super(loc);
		this.size = new Dimension.Mutable(size);
	}
	
	public RectEllipse(Vec2 loc) {
		super(loc);
	}
	
	public RectEllipse(Dimension size) {
		this(Vec2.ZERO, size);
	}
	
	public RectEllipse() {
		size = new Dimension.Mutable();
	}
	
	public RectEllipse(RectEllipse other) {
		super(other);
		size = new Dimension.Mutable(other.size);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!super.equals(o))
			return false;
		return size.equals(((RectEllipse)o).size);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		return result;
	}

	public Rect getBoundaryRect() {
		Rect rect = new Rect(getCenter(false), getSize(false));
		rect.setScale(getScale());
		return rect;
	}
	
	public Dimension getSize(boolean includeScale) {
		if (includeScale) {
			Dimension scaleDimension = new Dimension(getScale().getX(), getScale().getY());
			return Dimension.mult(size, scaleDimension, false);
		} else {
			return size;
		}
	}
	
	public RectEllipse setSize(Dimension size, boolean includeScale) {
		if (!isResizeable())
			return null;
		if (includeScale) {
			// Incorporate scale
			Dimension scaleDimension = new Dimension(getScale().getX(), getScale().getY());
			this.size.set(Dimension.div(size, scaleDimension, this.size.allowNegativeVals()));
		} else {
			this.size.set(size);
		}
		return this;
	}
	
	public RectEllipse setSize(float width, float height, boolean includeScale) {
		if (!isResizeable()) {
			return null;
		}
		if (includeScale) {
			// Incorporate scale
			size.set(width * getScale().getX(), height * getScale().getY());
		} else {
			size.set(width, height);
		}
		return this;
	}
	
	public RectEllipse setWidth(float width, boolean includeScale) {
		return setSize(width, size.getHeight(), includeScale);
	}
	
	public RectEllipse setHeight(float height, boolean includeScale) {
		return setSize(size.getWidth(), height, includeScale);
	}
}
