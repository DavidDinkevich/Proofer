package geometry.shapes;

import geometry.Dimension;
import geometry.Vec2;

/**
 * This object's sole purpose is to encapsulate the similarities between {@link Rect}s and
 * {@link Ellipse}s.
 * @author David Dinkevich
 */
public abstract class RectEllipse extends AbstractShape implements Shape2D {
	private Dimension.Mutable size;

	public RectEllipse(Vec2 loc, Dimension size) {
		super(loc);
		this.size = Dimension.Mutable.requireNonNegative(size);
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
		if (!(o instanceof RectEllipse))
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
		return new Rect(getCenter(), getSize());
	}
	
	public Dimension getSize() {
		return size;
	}
	
	public RectEllipse setSize(Dimension size) {
		this.size.set(Dimension.requireNonNegative(size));
		return this;
	}
	
	public RectEllipse setSize(float width, float height) {
		// TODO: enable
//		if (width < 0 || height < 0) {
//			throw new IllegalArgumentException("Width or height cannot be < 0");
//		}
		size.set(width, height);
		return this;
	}
	
	public RectEllipse setWidth(float width) {
		return setSize(width, size.getHeight());
	}
	
	public RectEllipse setHeight(float height) {
		return setSize(size.getWidth(), height);
	}
}
