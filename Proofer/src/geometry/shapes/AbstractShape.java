package geometry.shapes;

import java.util.Objects;

import geometry.Vec2;

import util.Utils;

/**
 * An abstract, incomplete implementation of the {@link Shape}
 * interface.
 * @author David Dinkevich
 */
public abstract class AbstractShape implements Shape {
	/**
	 * The center point of this {@link AbstractShape}.
	 */
	private Vec2.Mutable center;

	/**
	 * Get whether this {@link AbstractShape} is resizable.
	 */
	private boolean resizeable = true;

	/**
	 * The name of this {@link AbstractShape}.
	 */
	private String name = "";
	
	/**
	 * The minimum length of the name of this {@link AbstractShape}
	 */
	private int minNameLength = 0;

	/**
	 * The maximum length of the name of this {@link AbstractShape}
	 */
	private int maxNameLength = 10;
	
	
	public AbstractShape(Vec2 loc) {
		center = new Vec2.Mutable(Objects.requireNonNull(loc));
	}
	
	public AbstractShape() {
		this(Vec2.ZERO);
	}
	
	public AbstractShape(AbstractShape shape) {
		// TODO: use this()
		minNameLength = shape.minNameLength;
		maxNameLength = shape.maxNameLength;
		setName(shape.getName());

		if (center != null)
			center.set(shape.center);
		else
			center = new Vec2.Mutable(shape.center);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof AbstractShape))
			return false;
		AbstractShape s = (AbstractShape)o;
//		/*
//		 * Check if the name of the other shape is a valid name for this shape
//		 * (not for name equality). Do this FIRST--before anything else
//		 */
//		return isValidName(s.getName()) && getCenter(false).equals(s.getCenter(false))
//				&& scale.equals(s.scale);
		return isValidName(s.getName());
	}
	
	@Override
	public int hashCode() {
		int result = 17;
//		result = 31 * result + center.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public boolean containsPoint(Vec2 point) {
		return getCenter().equals(point);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		Objects.requireNonNull(name);
		name = name.toUpperCase();
		this.name = Utils.mergeStringsAndEnsureCapacity(minNameLength, 
				maxNameLength, this.name, name);
	}
	
	/**
	 * Set the minimum and maximum character length for the name of
	 * this {@link AbstractShape}. When a {@link AbstractShape}'s name is set, 
	 * if the given
	 * new name is too long, it will be cut off when necessary. If it is too
	 * short, null chars will be inserted to reach the minimum length.
	 * @param minLength the new minimum length
	 * @param maxLength the new maximum length
	 * @param updateName whether or not to update the name to fit
	 * the new settings.
	 */
	protected void setNameLengthRange(int minLength, int maxLength, boolean updateName) {
		minNameLength = minLength;
		maxNameLength = maxLength;
		if (updateName)
			setName(getName());
	}
	
	public int getMinimumNameLength() {
		return minNameLength;
	}
	
	public int getMaximumNameLength() {
		return maxNameLength;
	}

	@Override
	public Vec2 getCenter() {
		return center;
	}

	@Override
	public void setCenter(Vec2 loc) {
		if (loc == null)
			throw new NullPointerException("A Shape's location cannot be null.");
		center.set(loc);
	}
	
	@Override
	public boolean isResizeable() {
		return resizeable;
	}

	@Override
	public void setResizeable(boolean resizeable) {
		this.resizeable = resizeable;
	}
}
