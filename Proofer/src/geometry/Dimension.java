package geometry;

import java.io.Serializable;

/**
 * Embodies a 2D dimension, (width + height). This class is immutable.
 * @author David Dinkevich
 */
public class Dimension implements Serializable {
	
	private static final long serialVersionUID = 7249892867940294132L;
	
	public static final Dimension ZERO = new Dimension(0f);
	public static final Dimension ONE = new Dimension(1f);
	public static final Dimension TEN = new Dimension(10f);
	public static final Dimension ONE_HUNDRED = new Dimension(100f);
	
	protected float width, height;
	
	public Dimension(float w, float h) {
		width = w;
		height = h;
	}

	public Dimension(float size) {
		this(size, size);
	}
	
	public Dimension() {
		this(0f);
	}
	
	public Dimension(Dimension other) {
		this(other.width, other.height);
	}
	
	public Dimension(java.awt.Dimension size) {
		this(size.width, size.height);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o instanceof Dimension) {
			Dimension other = (Dimension) o;
			return Float.compare(other.width, width) == 0 &&
					Float.compare(other.height, height) == 0;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + Float.floatToIntBits(width);
		result = 31 * result + Float.floatToIntBits(height);
		return result;
	}
	
	@Override
	public String toString() {
		return "[ w = " + width + ", h = " + height + " ]";
	}
	
	/*
	 * Performing mathematical operations (static)
	 */
	
	public static Dimension add(Dimension a, Dimension b) {
		return new Dimension(a.width + b.width, a.height + b.height);
	}
	public static Dimension add(Dimension a, float f) {
		return new Dimension(a.width + f, a.height + f);
	}
	public static Dimension sub(Dimension a, Dimension b) {
		return new Dimension(a.width - b.width, a.height - b.height);
	}
	public static Dimension sub(Dimension a, float f) {
		return new Dimension(a.width - f, a.height - f);
	}
	public static Dimension mult(Dimension a, Dimension b) {
		return new Dimension(a.width * b.width, a.height * b.height);
	}
	public static Dimension mult(Dimension a, float f) {
		return new Dimension(a.width * f, a.height * f);
	}
	public static Dimension div(Dimension a, Dimension b) {
		return new Dimension(a.width / b.width, a.height / b.height);
	}
	public static Dimension div(Dimension a, float f) {
		return new Dimension(a.width / f, a.height / f);
	}
	
	/*
	 * Getters
	 */
	
	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
	
	/**
	 * Returns true if either the width or height of the
	 * given {@link Dimension} is negative.
	 */
	public static boolean containsNegatives(Dimension d) {
		return d.getWidth() < 0f || d.getHeight() < 0f;
	}
	
	/**
	 * Returns the given {@link Dimension} if none of its values
	 * (width, height) are negative. Otherwise, an
	 * {@link IllegalArgumentException} is thrown.
	 * @param d the {@link Dimension}
	 * @return the {@link Dimension}
	 */
	public static Dimension requireNonNegative(Dimension d) {
		if (containsNegatives(d))
			throw new IllegalArgumentException("This Dimension cannot contain"
					+ " negative values.");
		return d;
	}
	

	public static class Mutable extends Dimension {

		private static final long serialVersionUID = 2356027853230892596L;

		public Mutable(float w, float h) {
			super(w, h);
		}
		
		public Mutable(float size) {
			super(size);
		}
		
		public Mutable() {
			super();
		}
		
		public Mutable(Dimension d) {
			set(d);
		}
		
		/**
		 * Set the width of this mutable dimension.
		 * @param w the new width
		 * @return this mutable dimension
		 */
		public Mutable setWidth(float w) {
			width = w;
			return this;
		}
		
		/**
		 * Set the height of this mutable dimension.
		 * @param h the new height
		 * @return this mutable dimension
		 */
		public Mutable setHeight(float h) {
			height = h;
			return this;
		}

		/**
		 * Set the components of this mutable dimension.
		 * @param w the new width
		 * @param h the new height
		 * @return this mutable dimension
		 */
		public Mutable set(float w, float h) {
			width = w;
			height = h;
			return this;
		}
		
		/**
		 * Set the components of this mutable dimension.
		 * @param v the new width and height
		 * @return this mutable dimension
		 */
		public Mutable set(float v) {
			return set(v, v);
		}
		
		/**
		 * Set the width and height of this mutable dimension
		 * to those of the given dimension.
		 * @param d the new dimension
		 * @return this mutable dimension
		 */
		public Mutable set(Dimension d) {
			return set(d.width, d.height);
		}
		
		/*
		 * Mathematical computation
		 */
		
		/**
		 * Returns the result of adding the given {@link Dimension}
		 * to this mutable dimension.
		 */
		public Mutable add(Dimension other) {
			return set(width + other.width, height + other.height);
		}
		
		/**
		 * Returns the result of adding the given float
		 * to this mutable dimension.
		 */
		public Mutable add(float n) {
			return set(width + n, height + n);
		}
		
		/**
		 * Returns the result of subtracting the given {@link Dimension}
		 * from this mutable dimension.
		 */
		public Dimension sub(Dimension other) {
			return set(width - other.width, height - other.height);
		}
		
		/**
		 * Returns the result of subtracting the given float
		 * from this mutable dimension.
		 */
		public Dimension sub(float n) {
			return set(width - n, height - n);
		}
		
		/**
		 * Returns the result of multiplying this {@link Dimension}
		 * by the given dimension.
		 */
		public Dimension mult(Dimension other) {
			return set(width * other.width, height * other.height);
		}
		
		/**
		 * Returns the result of multiplying this {@link Dimension}
		 * by the given float.
		 */
		public Dimension mult(float n) {
			return set(width * n, height * n);
		}
		
		/**
		 * Returns the result of dividing this {@link Dimension}
		 * by the given dimension.
		 */
		public Dimension div(Dimension other) {
			return set(width / other.width, height / other.height);
		}
		
		/**
		 * Returns the result of dividing this {@link Dimension}
		 * by the given float.
		 */
		public Dimension div(float n) {
			return set(width / n, height / n);
		}
		
		/**
		 * Returns a mutable version of the given {@link Dimension} if
		 * none of its values (width, height) are negative. Otherwise, an
		 * {@link IllegalArgumentException} is thrown.
		 * @param d the {@link Dimension}
		 * @return a copy of the {@link Dimension.Mutable} version of the 
		 * given {@link Dimension}.
		 */
		public static Dimension.Mutable requireNonNegative(Dimension d) {
			if (containsNegatives(d))
				throw new IllegalArgumentException("The given "
						+ "Dimension cannot contain negative values.");
			return new Dimension.Mutable(d);
		}

	}
}