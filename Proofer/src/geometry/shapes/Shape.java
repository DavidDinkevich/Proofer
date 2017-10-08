package geometry.shapes;

import java.util.Collection;

import geometry.Vec2;
import geometry.proofs.Figure;

/**
 * All children of this interface embody a geometric shape.
 * @author David Dinkevich
 */
public interface Shape extends Figure, Scalable, Resizeable {
	/*
	 * Force implementing classes to override these Object methods
	 */
	public String toString();
	public boolean equals(Object o);
	public int hashCode();
	
	/**
	 * Get the center location of this {@link Shape}
	 * @param includeScale whether to pay attention to scale
	 * @return the center location as a {@link Vec2}.
	 */
	public Vec2 getCenter(boolean includeScale);
	
	/**
	 * Set the center location of this {@link Shape}
	 * @param includeScale whether to pay attention to scale
	 * @param loc the new center location as a {@link Vec2}.
	 */
	public void setCenter(Vec2 loc, boolean includeScale);
	
	/**
	 * Get whether the given point lies within this {@link Shape}.
	 * @param point the point to be checked.
	 * @param includeScale whether or not to ignore the scale of this {@link Shape}.
	 * @return whether or not the point lies within this shape.
	 */
	public boolean containsPoint(Vec2 point, boolean includeScale);
	
	/**
	 * Get whether the list of points lie within this {@link Shape}.
	 * @param pts the points to be checked.
	 * @param toScale whether or not to ignore the scale of this {@link Shape}.
	 * @return whether or not the points lie within this shape.
	 */
	default public boolean containsPoints(Collection<Vec2> pts, boolean toScale) {
		for (Vec2 point : pts) {
			if (!containsPoint(point, toScale))
				return false;
		}
		return true;
	}
}
