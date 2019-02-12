package geometry.shapes;

import java.util.Collection;

import geometry.Vec2;
import geometry.proofs.Figure;

/**
 * All children of this interface embody a geometric shape.
 * @author David Dinkevich
 */
public interface Shape extends Figure {
	/*
	 * Force implementing classes to override these Object methods
	 */
	public String toString();
	public boolean equals(Object o);
	public int hashCode();
	
	/**
	 * Get the center location of this {@link Shape}
	 * @return the center location as a {@link Vec2}.
	 */
	public Vec2 getCenter();
	
	/**
	 * Set the center location of this {@link Shape}
	 * @param loc the new center location as a {@link Vec2}.
	 */
	public void setCenter(Vec2 loc);
	
	/**
	 * Get whether the given point lies within this {@link Shape}.
	 * @param point the point to be checked.
	 * @return whether or not the point lies within this shape.
	 */
	public boolean containsPoint(Vec2 point);
	
	/**
	 * Get whether the list of points lie within this {@link Shape}.
	 * @param pts the points to be checked.
	 * @return whether or not the points lie within this shape.
	 */
	default public boolean containsPoints(Collection<Vec2> pts) {
		for (Vec2 point : pts) {
			if (!containsPoint(point))
				return false;
		}
		return true;
	}
	
	/**
	 * Get whether this {@link Shape} contains <i>at least one</i> point in the given
	 * list of points.
	 * @param pts the list of points
	 * @return whether this {@link Shape} contains <i>at least one</i> point in the given
	 * list of points.
	 */
	default public boolean containsAPointIn(Collection<Vec2> pts) {
		for (Vec2 point : pts) {
			if (containsPoint(point)) {
				return true;
			}
		}
		return false;
	}
}
