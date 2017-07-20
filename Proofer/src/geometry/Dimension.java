package geometry;

/**
 * Embodies a 2d dimension, (width + height), with the option to allow
 * the width and height to have negative values. This class is immutable.
 * @author David Dinkevich
 */
public class Dimension {
	public static final Dimension ZERO = new Dimension(0);
	public static final Dimension ONE = new Dimension(1);
	public static final Dimension TEN = new Dimension(10);
	public static final Dimension ONE_HUNDRED = new Dimension(100);
	
	protected float width, height;
	protected boolean allowNegativeVals;
	
	public Dimension(float w, float h, boolean allowNegativeVals) {
		this.allowNegativeVals = allowNegativeVals;
		width = allowNegativeVals ? w : Math.abs(w);
		height = allowNegativeVals ? h : Math.abs(h);
	}
	public Dimension(float w, float h) {
		this(w, h, false);
	}
	public Dimension(float size) {
		this(size, size);
	}
	public Dimension(boolean allowNegativeVals) {
		this(0f, 0f, allowNegativeVals);
	}
	public Dimension() {
		this(0f, 0f, false);
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
					Float.compare(other.height, height) == 0 &&
					other.allowNegativeVals == allowNegativeVals;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + Float.floatToIntBits(width);
		result = 31 * result + Float.floatToIntBits(height);
		result = 31 * result + Boolean.hashCode(allowNegativeVals);
		return result;
	}
	
	@Override
	public String toString() {
		return "[w = " + width + ", h = " + height + "] - Allow Neg. Vals = " + 
				allowNegativeVals;
	}
	
	/*
	 * Performing mathematical operations (static)
	 */
	
	public static Dimension add(Dimension a, Dimension b, boolean allowNegVals) {
		return new Dimension(a.width + b.width, a.height + b.height, allowNegVals);
	}
	public static Dimension add(Dimension a, float f, boolean allowNegVals) {
		return new Dimension(a.width + f, a.height + f, allowNegVals);
	}
	public static Dimension sub(Dimension a, Dimension b, boolean allowNegVals) {
		return new Dimension(a.width - b.width, a.height - b.height, allowNegVals);
	}
	public static Dimension sub(Dimension a, float f, boolean allowNegVals) {
		return new Dimension(a.width - f, a.height - f, allowNegVals);
	}
	public static Dimension mult(Dimension a, Dimension b, boolean allowNegVals) {
		return new Dimension(a.width * b.width, a.height * b.height, allowNegVals);
	}
	public static Dimension mult(Dimension a, float f, boolean allowNegVals) {
		return new Dimension(a.width * f, a.height * f, allowNegVals);
	}
	public static Dimension div(Dimension a, Dimension b, boolean allowNegVals) {
		return new Dimension(a.width / b.width, a.height / b.height, allowNegVals);
	}
	public static Dimension div(Dimension a, float f, boolean allowNegVals) {
		return new Dimension(a.width / f, a.height / f, allowNegVals);
	}
	
	/*
	 * Performing mathematical operations (non-static)
	 */
	
	/**
	 * Returns the result of adding the given {@link Dimension} to this
	 * {@link Dimension}. As this class is <i>immutable,</i>
	 * the width and height values in this {@link Dimension} <i>are
	 * not changed</i>.
	 * <p>
	 * Also note that the product of this operation will only contain
	 * negative values if <i>this</i> {@link Dimension} allows negative
	 * values.
	 * @see Dimension#allowNegativeVals()
	 */
	public Dimension add(Dimension other) {
		return add(this, other, allowNegativeVals);
	}
	
	/**
	 * Returns the result of adding the given float to this
	 * {@link Dimension} As this class is <i>immutable,</i>
	 * the width and height values in this {@link Dimension} <i>are
	 * not changed</i>.
	 * <p>
	 * Also note that the product of this operation will only contain
	 * negative values if <i>this</i> {@link Dimension} allows negative
	 * values.
	 * @see Dimension#allowNegativeVals()
	 */
	public Dimension add(float n) {
		return add(this, n, allowNegativeVals);
	}
	
	/**
	 * Returns the result of subtracting the given {@link Dimension} from this
	 * {@link Dimension}. As this class is <i>immutable,</i>
	 * the width and height values in this {@link Dimension} <i>are
	 * not changed</i>.
	 * <p>
	 * Also note that the product of this operation will only contain
	 * negative values if <i>this</i> {@link Dimension} allows negative
	 * values.
	 * @see Dimension#allowNegativeVals()
	 */
	public Dimension sub(Dimension other) {
		return sub(this, other, allowNegativeVals);
	}
	
	/**
	 * Returns the result of subtracting the given float from this
	 * {@link Dimension}. As this class is <i>immutable,</i>
	 * the width and height values in this {@link Dimension} <i>are
	 * not changed</i>.
	 * <p>
	 * Also note that the product of this operation will only contain
	 * negative values if <i>this</i> {@link Dimension} allows negative
	 * values.
	 * @see Dimension#allowNegativeVals()
	 */
	public Dimension sub(float n) {
		return sub(this, n, allowNegativeVals);
	}
	
	/**
	 * Returns the result of multiplying this {@link Dimension} by the
	 * given {@link Dimension}. As this class is <i>immutable,</i>
	 * the width and height values in this {@link Dimension} <i>are
	 * not changed</i>.
	 * <p>
	 * Also note that the product of this operation will only contain
	 * negative values if <i>this</i> {@link Dimension} allows negative
	 * values.
	 * @see Dimension#allowNegativeVals()
	 */
	public Dimension mult(Dimension other) {
		return mult(this, other, allowNegativeVals);
	}
	
	/**
	 * Returns the result of multiplying this {@link Dimension} by the
	 * given float. As this class is <i>immutable,</i>
	 * the width and height values in this {@link Dimension} <i>are
	 * not changed</i>.
	 * <p>
	 * Also note that the product of this operation will only contain
	 * negative values if <i>this</i> {@link Dimension} allows negative
	 * values.
	 * @see Dimension#allowNegativeVals()
	 */
	public Dimension mult(float n) {
		return mult(this, n, allowNegativeVals);
	}
	
	/**
	 * Returns the result of dividing this {@link Dimension} by the
	 * given {@link Dimension}. As this class is <i>immutable,</i>
	 * the width and height values in this {@link Dimension} <i>are
	 * not changed</i>.
	 * <p>
	 * Also note that the product of this operation will only contain
	 * negative values if <i>this</i> {@link Dimension} allows negative
	 * values.
	 * @see Dimension#allowNegativeVals()
	 */
	public Dimension div(Dimension other) {
		return div(this, other, allowNegativeVals);
	}
	
	/**
	 * Returns the result of dividing this {@link Dimension} by the
	 * given float. As this class is <i>immutable,</i>
	 * the width and height values in this {@link Dimension} <i>are
	 * not changed</i>.
	 * <p>
	 * Also note that the product of this operation will only contain
	 * negative values if <i>this</i> {@link Dimension} allows negative
	 * values.
	 * @see Dimension#allowNegativeVals()
	 */
	public Dimension div(float n) {
		return div(this, n, allowNegativeVals);
	}
	
	
	public float getWidth() {
		return width;
	}
	public float getHeight() {
		return height;
	}
	public boolean allowNegativeVals() {
		return allowNegativeVals;
	}
	
	
	public static class Mutable extends Dimension {
		public Mutable(float w, float h, boolean allowNegativeVals) {
			super(w, h, allowNegativeVals);
		}
		public Mutable(float w, float h) {
			super(w, h);
		}
		public Mutable(float size) {
			super(size);
		}
		public Mutable(boolean allowNegativeVals) {
			super(allowNegativeVals);
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
		 * Set whether this mutable dimension allows
		 * its width and height values to be negative.
		 * @param val the new value
		 * @return this mutable dimension
		 */
		public Mutable setAllowNegativeVals(boolean val) {
			allowNegativeVals = val;
			return this;
		}
		
		/**
		 * Set the components of this mutable dimension.
		 * @param w the new width
		 * @param h the new height
		 * @param allowNegatives whether or not to allow width and height to be
		 * negative
		 * @return this mutable dimension
		 */
		public Mutable set(float w, float h, boolean allowNegatives) {
			width = w;
			height = h;
			allowNegativeVals = allowNegatives;
			return this;
		}
		
		/**
		 * Set the components of this mutable dimension.
		 * @param w the new width
		 * @param h the new height
		 * @return this mutable dimension
		 */
		public Mutable set(float w, float h) {
			return set(w, h, allowNegativeVals);
		}
		
		/**
		 * Set the x and y components of this mutable dimension
		 * to those of the given dimension.
		 * @param d the new dimension
		 * @return this mutable dimension
		 */
		public Mutable set(Dimension d) {
			return set(d.width, d.height, d.allowNegativeVals);
		}
		
		/*
		 * Mathematical computation
		 */
		
		/**
		 * Returns the result of adding the given {@link Dimension}
		 * to this mutable dimension. As this class is <i>mutable,</i>
		 * the width and height values are modified.
		 */
		@Override
		public Mutable add(Dimension other) {
			return set(super.add(other));
		}
		
		/**
		 * Returns the result of adding the given float
		 * to this mutable dimension. As this class is <i>mutable,</i>
		 * the width and height values are modified.
		 */
		@Override
		public Mutable add(float n) {
			return set(super.add(n));
		}
		
		/**
		 * Returns the result of subtracting the given {@link Dimension}
		 * from this mutable dimension. As this class is <i>mutable,</i>
		 * the width and height values are modified.
		 */
		@Override
		public Dimension sub(Dimension other) {
			return set(super.sub(other));
		}
		
		/**
		 * Returns the result of subtracting the given float
		 * from this mutable dimension. As this class is <i>mutable,</i>
		 * the width and height values are modified.
		 */
		@Override
		public Dimension sub(float n) {
			return set(super.sub(n));
		}
		
		/**
		 * Returns the result of multiplying this {@link Dimension}
		 * by the given dimension. As this class is <i>mutable,</i>
		 * the width and height values are modified.
		 */
		@Override
		public Dimension mult(Dimension other) {
			return set(super.mult(other));
		}
		
		/**
		 * Returns the result of multiplying this {@link Dimension}
		 * by the given float. As this class is <i>mutable,</i>
		 * the width and height values are modified.
		 */
		@Override
		public Dimension mult(float n) {
			return set(super.mult(n));
		}
		
		/**
		 * Returns the result of dividing this {@link Dimension}
		 * by the given dimension. As this class is <i>mutable,</i>
		 * the width and height values are modified.
		 */
		@Override
		public Dimension div(Dimension other) {
			return set(super.div(other));
		}
		
		/**
		 * Returns the result of dividing this {@link Dimension}
		 * by the given float. As this class is <i>mutable,</i>
		 * the width and height values are modified.
		 */
		@Override
		public Dimension div(float n) {
			return set(super.div(n));
		}
	}
}
