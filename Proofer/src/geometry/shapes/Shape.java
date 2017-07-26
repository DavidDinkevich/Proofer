package geometry.shapes;

import exceptions.IllegalScaleException;

import geometry.Vec2;
import geometry.proofs.Figure;

import util.Utils;

/**
 * Descendants of this class embody 2D geometric shapes.
 * @author David Dinkevich
 */
public abstract class Shape implements Scalable, Figure {
	/**
	 * The center point of this {@link Shape}.
	 */
	private Vec2.Mutable center;
	private Vec2.Mutable dilationPoint;
	/**
	 * The scale of this object
	 */
	private Vec2.Mutable scale = new Vec2.Mutable(1, 1);
	/**
	 * The name of this {@link Shape}.
	 */
	private String name = "";
	/**
	 * The minimum length of the name of this {@link Shape}
	 */
	private int minNameLength = 0;
	/**
	 * The maximum length of the name of this {@link Shape}
	 */
	private int maxNameLength = 10;
	
	
	public Shape(Vec2.Mutable loc) {
		if (loc == null) {
			throw new NullPointerException("A Shape cannot be given a null location.");
		}
		this.center = loc;
		dilationPoint = new Vec2.Mutable();
	}
	public Shape(Vec2 loc) {
		this(new Vec2.Mutable(loc));
	}
	public Shape() {
		this(Vec2.ZERO);
	}
	public Shape(Shape shape) {
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
		if (!(o instanceof Shape))
			return false;
		Shape s = (Shape)o;
		/*
		 * Check if the name of the other shape is a valid name for this shape
		 * (not for name equality). Do this FIRST--before anything else
		 */
		return isValidName(s.getName()) && getCenter().equals(s.getCenter())
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
	
	public boolean containsPoint(Vec2 point, boolean incorporateScale) {
		return incorporateScale ? getScaledCenter().equals(point)
				: getCenter().equals(point);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		if (name == null)
			throw new NullPointerException("A Shape's name cannot be null.");
		this.name = Utils.mergeStringsAndEnsureCapacity(minNameLength, maxNameLength, this.name, name);
	}
	
	/**
	 * Get whether the given name is a valid name
	 * for this shape. NOTE: it does not have to EQUAL
	 * this name, just be valid.
	 */
	public abstract boolean isValidName(String name);
	
	/**
	 * Set the minimum and maximum character length for the name of
	 * this {@link Shape}. When a {@link Shape}'s name is set, if the given
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

	/**
	 * Get the center location of this {@link Shape} and incorporate this
	 * {@link Shape}'s scale.
	 * @return the center location as a {@link Vec2}.
	 */
	public Vec2 getScaledCenter() {
		return Vec2.mult(getCenter(), scale);
	}
	
	/**
	 * Get the center location of this {@link Shape} (without incorporate this
	 * {@link Shape}'s scale).
	 * @return the center location as a {@link Vec2}.
	 */
	public Vec2 getCenter() {
		return center;
	}
	
	/**
	 * Set the center location of this {@link Shape} and incorporate this
	 * {@link Shape}'s scale.
	 * @param loc the new center location
	 */
	public void setScaledCenter(Vec2 loc) {
		setCenter(Vec2.div(loc, scale));
	}
	
	/**
	 * Set the center location of this {@link Shape} (without incorporate this
	 * {@link Shape}'s scale).
	 * @return the center location as a {@link Vec2}.
	 */
	public void setCenter(Vec2 loc) {
		if (loc == null)
			throw new NullPointerException("A Shape's location cannot be null.");
		this.center.set(loc);
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

