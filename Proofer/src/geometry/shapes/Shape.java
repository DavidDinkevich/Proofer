package geometry.shapes;

import geometry.Vec2;
import geometry.proofs.Figure;

/**
 * All children of this interface embody a geometric shape.
 * @author David Dinkevich
 */
public interface Shape extends Scalable, Figure {
	/**
	 * Get the name of this {@link Shape}.
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Set the name of this {@link Shape}.
	 * @param the new name
	 */
	public void setName(String name);
	
	/*
	 * Force implementing classes to override these Object methods
	 */
	public String toString();
	public boolean equals(Object o);
	public int hashCode();
	
	/**
	 * Get the center location of this {@link Shape} (without incorporating this
	 * {@link Shape}'s scale).
	 * @return the center location as a {@link Vec2}.
	 */
	public Vec2 getCenter();
	
	/**
	 * Set the center location of this {@link Shape} (without incorporating this
	 * {@link Shape}'s scale).
	 * @param loc the new center location as a {@link Vec2}.
	 */
	public void setCenter(Vec2 loc);
	
	/**
	 * Get the center location of this {@link Shape} and incorporate this
	 * {@link Shape}'s scale.
	 * @return the center location as a {@link Vec2}.
	 */
	public Vec2 getScaledCenter();
	
	/**
	 * Set the center location of this {@link Shape} and incorporate this
	 * {@link Shape}'s scale.
	 * @param loc the new center location
	 */
	public void setScaledCenter(Vec2 loc);
	
	/**
	 * Get whether the given point lies within this {@link Shape}.
	 * @param point the point to be checked.
	 * @param includeScale whether or not to ignore the scale of this {@link Shape}.
	 * @return whether or not the point lies within this shape.
	 */
	public boolean containsPoint(Vec2 point, boolean includeScale);
	
	
}
