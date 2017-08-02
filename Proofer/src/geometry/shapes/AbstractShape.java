package geometry.shapes;

import exceptions.IllegalScaleException;

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
	private Vec2.Mutable dilationPoint;
	/**
	 * The scale of this object
	 */
	private Vec2.Mutable scale = new Vec2.Mutable(1, 1);
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
	
	
	public AbstractShape(Vec2.Mutable loc) {
		if (loc == null) {
			throw new NullPointerException("A Shape cannot be given a null location.");
		}
		this.center = loc;
		dilationPoint = new Vec2.Mutable();
	}
	public AbstractShape(Vec2 loc) {
		this(new Vec2.Mutable(loc));
	}
	public AbstractShape() {
		this(Vec2.ZERO);
	}
	public AbstractShape(AbstractShape shape) {
		minNameLength = shape.minNameLength;
		maxNameLength = shape.maxNameLength;
		setName(shape.getName());
		scale = new Vec2.Mutable(shape.scale);
		
		if (center != null) center.set(shape.center);
		else center = new Vec2.Mutable(shape.center);
		if (dilationPoint != null) dilationPoint.set(shape.dilationPoint);
		else dilationPoint = new Vec2.Mutable(shape.dilationPoint);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof AbstractShape))
			return false;
		AbstractShape s = (AbstractShape)o;
		/*
		 * Check if the name of the other shape is a valid name for this shape
		 * (not for name equality). Do this FIRST--before anything else
		 */
		return isValidName(s.getName()) && getCenter(false).equals(s.getCenter(false))
				&& scale.equals(s.scale);
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + scale.hashCode();
		result = 31 * result + center.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public boolean containsPoint(Vec2 point, boolean incorporateScale) {
		return getCenter(incorporateScale).equals(point);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		if (name == null)
			throw new NullPointerException("A Shape's name cannot be null.");
		name = name.toUpperCase();
		this.name = Utils.mergeStringsAndEnsureCapacity(minNameLength, maxNameLength, this.name, name);
	}
	
	/**
	 * Set the minimum and maximum character length for the name of
	 * this {@link AbstractShape}. When a {@link AbstractShape}'s name is set, if the given
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
	
	protected int getMinimumNameLength() {
		return minNameLength;
	}
	
	protected int getMaximumNameLength() {
		return maxNameLength;
	}

	@Override
	public Vec2 getCenter(boolean includeScale) {
		return includeScale ? Vec2.mult(center, scale) : center;
	}

	@Override
	public void setCenter(Vec2 loc, boolean includeScale) {
		if (loc == null)
			throw new NullPointerException("A Shape's location cannot be null.");
		if (includeScale)
			this.center.set(loc);
		else
			this.center.set(Vec2.div(loc, scale));
	}
	
	@Override
	public Vec2 getScale() {
		return scale;
	}
	
	@Override
	public Vec2 getDilationPoint() {
		return dilationPoint;
	}
	
	@Override
	public void setScale(Vec2 scale, Vec2 dilationPoint) {
		if (scale.getX() <= 0 || scale.getY() <= 0) {
			throw new IllegalScaleException("Scale width/height cannot be <= 0");
		}
		this.scale.set(scale);
//		this.dilationPoint.set(dilationPoint);
	}
	
	@Override
	public void setScale(Vec2 scale) {
		setScale(scale, Vec2.ZERO);
	}
}

