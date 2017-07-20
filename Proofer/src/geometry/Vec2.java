package geometry;

/**
 * Represents a 2 dimensional mathematical vector.
 * <p>
 * NOTE: this is built off of the open source processing
 * <code>PVector</code> class, which can be found here:
 * https://github.com/processing/processing/blob/master/core/src/processing/core/PVector.java
 * 
 * @author David Dinkevich
 */
public class Vec2 {
	public static final Vec2 ZERO = new Vec2(0, 0);
	
	protected float x, y;
	
	public Vec2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2() {
		x = ZERO.x;
		y = ZERO.y;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Vec2))
			return false;
		Vec2 v = (Vec2)o;
		return x == v.x && y == v.y;
	}
	
	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + Float.floatToIntBits(x);
		result = 31 * result + Float.floatToIntBits(y);
		return result;
	}
	
	@Override
	public String toString() {
		return "[ " + x + " , " + y + " ]";
	}

	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	
	public float getMag() {
		return (float) Math.sqrt(x*x + y*y);
	}
	
	public float getMagSq() {
		return (x*x + y*y);
	}
	
	public Vec2 valueAtMag(float mag) {
		return normalized().mult(mag);
	}
	
	/**
	 * Get the value of this {@link Vec2} at a normalized state
	 * (as a unit vector).
	 * NOTE: As this class is <i>immutable,
	 * the x and y values in <i>this {@link Vec2} are
	 * not affected or changed.</i>
	 */
	public Vec2 normalized() {
		float x, y;
		final float m = getMag();
		
		if (m > 0) {
			x = this.x/m;
			y = this.y/m;
		} else {
			x = this.x;
			y = this.y;
		}
		return new Vec2(x, y);
	}
	
	/**
	 * Get the negated version of this {@link Vec2}.
	 * NOTE: As this class is <i>immutable,
	 * the x and y values in <i>this {@link Vec2} are
	 * not affected or changed.</i>
	 * @return the negated version of this vector.
	 */
	public Vec2 negated() {
		return new Vec2(-x, -y);
	}
	
	/*
	 * Non-static add/sub/div/mult operations
	 */
	
	/**
	 * Returns the result of adding the given {@link Vec2} to this
	 * {@link Vec2}. As this class is <i>immutable,</i>
	 * the x and y values in <i>this {@link Vec2} are
	 * not changed</i>.
	 */
	public Vec2 add(Vec2 v) {
		return new Vec2(x+v.x, y+v.y);
	}
	
	/**
	 * Returns the result of adding the given float to this
	 * {@link Vec2}. As this class is <i>immutable,</i>
	 * the x and y values in <i>this {@link Vec2} are
	 * not changed</i>.
	 */
	public Vec2 add(float n) {
		return new Vec2(x+n, y+n);
	}
	
	/**
	 * Returns the result of subtracting the given {@link Vec2} from
	 * this {@link Vec2}. As this class is <i>immutable,</i>
	 * the x and y values in this {@link Vec2} <i>are
	 * not changed</i>.
	 */
	public Vec2 sub(Vec2 v) {
		return new Vec2(x-v.x, y-v.y);
	}
	
	/**
	 * Returns the result of subtracting the given float from
	 * this {@link Vec2}. As this class is <i>immutable,</i>
	 * the x and y values in this {@link Vec2} <i>are
	 * not changed</i>.
	 */
	public Vec2 sub(float n) {
		return new Vec2(x-n, y-n);
	}
	
	/**
	 * Returns the result of multiplying this {@link Vec2} by the
	 * given {@link Vec2}. As this class is <i>immutable,</i>
	 * the x and y values in this {@link Vec2} <i>are
	 * not changed</i>.
	 */
	public Vec2 mult(Vec2 v) {
		return new Vec2(x*v.x, y*v.y);
	}
	
	/**
	 * Returns the result of multiplying this {@link Vec2} by the
	 * given float. As this class is <i>immutable,</i>
	 * the x and y values in this {@link Vec2} <i>are
	 * not changed</i>.
	 */
	public Vec2 mult(float n) {
		return new Vec2(x*n, y*n);
	}
	
	/**
	 * Returns the result of dividing this {@link Vec2} by the
	 * given {@link Vec2}. As this class is <i>immutable,</i>
	 * the x and y values in this {@link Vec2} <i>are
	 * not changed</i>.
	 */
	public Vec2 div(Vec2 v) {
		return new Vec2(x/v.x, y/v.y);
	}
	
	/**
	 * Returns the result of dividing this {@link Vec2} by the
	 * given float. As this class is <i>immutable,</i>
	 * the x and y values in this {@link Vec2} <i>are
	 * not changed</i>.
	 */
	public Vec2 div(float n) {
		return new Vec2(x/n, y/n);
	}
	
	/*
	 * Static add/sub/div/mult operations
	 */
	
	public static Vec2 add(Vec2 v1, Vec2 v2) {
		return new Vec2(v1.x + v2.x, v1.y + v2.y);
	}
	public static Vec2 add(Vec2 v1, float n) {
		return new Vec2(v1.x+n, v1.y+n);
	}
	
	public static Vec2 sub(Vec2 v1, Vec2 v2) {
		return new Vec2(v1.x - v2.x, v1.y - v2.y);
	}
	public static Vec2 sub(Vec2 v1, float n) {
		return new Vec2(v1.x-n, v1.y-n);
	}
	
	public static Vec2 mult(Vec2 v1, Vec2 v2) {
		return new Vec2(v1.x*v2.x, v1.y*v2.y);
	}
	public static Vec2 mult(Vec2 v1, float n) {
		return new Vec2(v1.x*n, v1.y*n);
	}
	
	public static Vec2 div(Vec2 v1, Vec2 v2) {
		return new Vec2(v1.x/v2.x, v1.y/v2.y);
	}
	public static Vec2 div(Vec2 v1, float n) {
		return new Vec2(v1.x/n, v1.y/n);
	}
	
	/*
	 * DISTANCE
	 */
	
	/**
	 * Get the Euclidean distance between two {@link Vec2}s.
	 */
	public static float dist(Vec2 v1, Vec2 v2) {
		final float dx = v1.x - v2.x;
		final float dy = v1.y - v2.y;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}
	
	/**
	 * Get the distance from this {@link Vec2} to the given
	 * other {@link Vec2}.
	 */
	public float dist(Vec2 other) {
		float dx = x - other.x;
		float dy = y - other.y;
		return (float) Math.sqrt(dx*dx + dy*dy);
	}
	
	/*
	 * DOT
	 */
	
	public float dot(float x, float y) {
		return this.x*x + this.y*y;
	}
	
	public float dot(Vec2 vec) {
		return dot(vec.x, vec.y);
	}
	
	public static float dot(Vec2 v1, Vec2 v2) {
		return v1.x*v2.x + v1.y*v2.y;
	}
	
	/**
	 * Calculate the angle of rotation for this vector.
	 * @return the angle.
	 */
	public float getHeading() {
		return (float) Math.atan2(y, x);
	}
	
	/**
	 * Get the value of this vector at the given rotation (theta).
	 * <p>
	 * As this is an immutable class, the magnitude, x, and y components
	 * of this vector are <i>not</i> affected.
	 */
	public Vec2 valueAtRotation(float theta) {
		float x = this.x;
		float y = this.y;
		float temp = x;

	    x = x*((float)Math.cos(theta)) - y*((float)Math.sin(theta));
	    y = temp*((float)Math.sin(theta)) + y*((float)Math.cos(theta));
	    
	    return new Vec2(x, y);
	}
	
	private static float lerp(float start, float stop, float amt) {
		return start + (stop-start) * amt;
	}
	
	/**
	 * Linear interpolate between two vectors by the given amount.
	 * @param start the vector to start from
	 * @param stop the vector to lerp to
	 */
	public static Vec2 lerp(Vec2 start, Vec2 stop, float amt) {
		final float x, y;
		x = lerp(start.x, stop.x, amt);
		y = lerp(start.y, stop.y, amt);
		return new Vec2(x, y);
	}
	
	/**
	 * Linear interpolate this vector to the given vector by the given
	 * amount.
	 * <p>
	 * NOTE: Because this class is immutable, the x and y
	 * values of this vector will not be affected.
	 */
	public Vec2 lerp(Vec2 to, float amt) {
		return lerp(this, to, amt);
	}
	
	/**
	 * Calculates and returns the angle (in radians) between two vectors.
	 *
	 * @param v1
	 *            the x, y, and z components of a {@link Vec2}.
	 * @param v2
	 *            the x, y, and z components of a {@link Vec2}
	 * @see https://github.com/processing/processing/blob/master/core/src/processing/core/PVector.java
	 */
	static public float angleBetween(Vec2 v1, Vec2 v2) {

		// We get NaN if we pass in a zero vector which can cause problems
		// Zero seems like a reasonable angle between a (0,0) vector and
		// something else
		if (v1.x == 0 && v1.y == 0)
			return 0.0f;
		if (v2.x == 0 && v2.y == 0)
			return 0.0f;

		final double dot = dot(v1, v2);
		final double v1mag = Math.sqrt(v1.x * v1.x + v1.y * v1.y);
		final double v2mag = Math.sqrt(v2.x * v2.x + v2.y * v2.y);
		// This should be a number between -1 and 1, since it's "normalized"
		final double amt = dot / (v1mag * v2mag);
		// But if it's not due to rounding error, then we need to fix it
		// http://code.google.com/p/processing/issues/detail?id=340
		// Otherwise if outside the range, acos() will return NaN
		// http://www.cppreference.com/wiki/c/math/acos
		if (amt <= -1) {
			return (float)Math.PI;
		} else if (amt >= 1) {
			// http://code.google.com/p/processing/issues/detail?id=435
			return 0;
		}
		return (float) Math.acos(amt);
	}
	
	/**
	 * Represents a <i>mutable</i> vector. This differs from {@link Vec2}
	 * because {@link Vec2} is immutable, while this is mutable.
	 * @author David Dinkevich
	 */
	public static class Mutable extends Vec2 {
		public Mutable(float x, float y) {
			super(x, y);
		}
		public Mutable() {
			super();
		}
		public Mutable(Vec2 v) {
			set(v);
		}
		
		/**
		 * Set the x component of this vector.
		 * @param newX the new x
		 * @return this mutable vector.
		 */
		public Mutable setX(float newX) {
			x = newX;
			return this;
		}
		
		/**
		 * Set the y component of this vector.
		 * @param newY the new y
		 * @return this mutable vector.
		 */
		public Mutable setY(float newY) {
			y = newY;
			return this;
		}
		
		/**
		 * Set the x and y components of this vector to those
		 * of the given vector.
		 * @return this mutable vector.
		 */
		public Mutable set(Vec2 vec) {
			setX(vec.getX());
			setY(vec.getY());
			return this;
		}
		
		/**
		 * Set the x and y components of this vector.
		 * @param newX the new x
		 * @param newY the new y
		 * @return this mutable vector.
		 */
		public Mutable set(float newX, float newY) {
			setX(newX);
			setY(newY);
			return this;
		}
		
		/**
		 * Normalize the vector to length 1 (make it a unit vector).
		 * As this class is <i>mutable,</i> the x and y values are modified.
		 * @return this mutable vector
		 */
		public Mutable normalize() {
			final float m = getMag();
		    if (m != 0 && m != 1) {
		      div(m);
		    }
		    return this;
		}
		
		/**
		 * Negate this vector.
		 * As this class is <i>mutable,</i> the x and y values are modified.
		 * @return this vector after being negated
		 */
		public Vec2 negate() {
			return set(negated());
		}
		
		/**
		 * Set the magnitude of this vector.
		 * As this class is <i>mutable,</i> the x and y values are modified.
		 * @param mag the new magnitude.
		 * @return
		 */
		public Mutable setMag(float mag) {
			normalize();
			mult(mag);
			return this;
		}
		
		/**
		 * Linear interpolate this vector to the given vector by the given amount.
		 * As this class is <i>mutable,</i> the x and y values are modified.
		 */
		public Mutable lerp(Vec2 to, float amount) {
			return set(super.lerp(to, amount));
		}
		
		/**
		 * Rotate the vector by an angle, magnitude remains the same.
		 * As this class is <i>mutable,</i> the x and y values are modified.
		 * @param theta the angle of rotation
		 * @return this mutable vector.
		 */
		public Mutable rotate(float theta) {
			return set(super.valueAtRotation(theta));
		}
		
		/**
		 * Returns the result of adding the given {@link Vec2}
		 * to this mutable vector. As this class is <i>mutable,</i>
		 * the x and y values are modified.
		 */
		@Override
		public Mutable add(Vec2 vec) {
			return set(super.add(vec));
		}
		
		/**
		 * Returns the result of adding this {@link Mutable} {@link Vec2}
		 * to the given float. As this class is <i>mutable,</i>
		 * the x and y values are modified.
		 */
		@Override
		public Mutable add(float n) {
			return set(super.add(n));
		}
		
		/**
		 * Returns the result of subtracting the given {@link Vec2}
		 * from this mutable vector. As this class is <i>mutable,</i>
		 * the x and y values are modified.
		 */
		@Override
		public Mutable sub(Vec2 vec) {
			return set(super.sub(vec));
		}
		
		/**
		 * Returns the result of subtracting the given float
		 * from this mutable vector. As this class is <i>mutable,</i>
		 * the x and y values are modified.
		 */
		@Override
		public Mutable sub(float n) {
			return set(super.sub(n));
		}
		
		/**
		 * Returns the result of multiplying this {@link Mutable} {@link Vec2}
		 * by the given {@link Vec2}. As this class is <i>mutable,</i>
		 * the x and y values are modified.
		 */
		@Override
		public Mutable mult(Vec2 vec) {
			return set(super.mult(vec));
		}
		
		/**
		 * Returns the result of multiplying this {@link Mutable} {@link Vec2}
		 * by the given float. As this class is <i>mutable,</i>
		 * the x and y values are modified.
		 */
		@Override
		public Mutable mult(float n) {
			return set(super.mult(n));
		}
		
		/**
		 * Returns the result of dividing this {@link Mutable} {@link Vec2}
		 * by the given {@link Vec2}. As this class is <i>mutable,</i>
		 * the x and y values are modified.
		 */
		@Override
		public Mutable div(Vec2 vec) {
			return set(super.div(vec));
		}
		
		/**
		 * Returns the result of dividing this {@link Mutable} {@link Vec2}
		 * by the given float. As this class is <i>mutable,</i>
		 * the x and y values are modified.
		 */
		@Override
		public Mutable div(float n) {
			return set(super.div(n));
		}
	}
}
