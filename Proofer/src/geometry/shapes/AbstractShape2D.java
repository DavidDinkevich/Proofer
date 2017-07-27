package geometry.shapes;

import geometry.Vec2;

public abstract class AbstractShape2D extends AbstractShape implements Resizeable {
	/** Determines whether this {@link AbstractShape2D} is resizeable or not. */
	private boolean resizeable = true;
	
	public AbstractShape2D(Vec2 loc) {
		super(loc);
	}
	public AbstractShape2D() {
		super();
	}
	public AbstractShape2D(AbstractShape2D shape) {
		super(shape);
		resizeable = shape.resizeable;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!super.equals(o))
			return false;
		AbstractShape2D shape = (AbstractShape2D)o;
		return shape.resizeable = resizeable;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (resizeable ? 1231 : 1237);
		return result;
	}
	
	/**
	 * Get the perimeter of this {@link AbstractShape2D}.
	 * @return the perimeter
	 */
	public abstract float getPerimeter();
	
	/**
	 * Get the area of this {@link AbstractShape2D}.
	 * @return the area
	 */
	// TODO: separate this into two methods: getArea(), getScaledArea()
	public abstract float getArea();
	/**
	 * Returns a {@link Rect} surrounding this {@link AbstractShape2D} as closely
	 * as possible.
	 */
	public abstract Rect getBoundaryRect();
	
	@Override
	public boolean isResizeable() {
		return resizeable;
	}
	@Override
	public void setResizeable(boolean resizeable) {
		this.resizeable = resizeable;
	}
}
