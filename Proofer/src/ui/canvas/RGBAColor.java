package ui.canvas;

import java.util.Arrays;

public class RGBAColor {
	
	protected int[] col;
	protected float a;
	
	public RGBAColor(int r, int g, int b, float a) {
		ensureValidNums(r, g, b);
		ensureValidAlpha(a);
		col = new int[] { r, g, b };
		this.a = a;
	}
	
	public RGBAColor(int r, int g, int b) {
		this(r, g, b, 1f);
	}
	
	public RGBAColor(int gray, float a) {
		this(gray, gray, gray, a);
	}
	
	public RGBAColor(int gray) {
		this(gray, 1f);
	}
	
	public RGBAColor() {
		this(100, 100, 100, 1f);
	}
	
	protected void ensureValidNums(float... nums) {
		for (float i : nums) {
			if (i < 0f || i > 255f) {
				throw new IllegalArgumentException("RGB values must be "
						+ "0 <= x <= 255");
			}
		}
	}
	
	protected void ensureValidAlpha(float a) {
		if (a < 0f || a > 1f) {
			throw new IllegalArgumentException("Alpha value must be 0 <= a <= 1");
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (other instanceof RGBAColor) {
			RGBAColor o = (RGBAColor) other;
			return a == o.a && Arrays.equals(col, o.col);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + Arrays.hashCode(col);
		result = 31 * result + Float.floatToIntBits(a);
		return result;
	}
	
	public int getR() {
		return col[0];
	}
	
	public int getG() {
		return col[1];
	}
	
	public int getB() {
		return col[2];
	}
	
	public float getA() {
		return a;
	}
	
	public static class Mutable extends RGBAColor {
		
		public Mutable(RGBAColor other) {
			set(other);
		}
		
		public Mutable() {
		}

		public Mutable(int r, int g, int b, float a) {
			super(r, g, b, a);
		}

		public Mutable(int r, int g, int b) {
			super(r, g, b);
		}

		public Mutable(int gray, float a) {
			super(gray, a);
		}

		public Mutable(int gray) {
			super(gray);
		}
		
		
		public Mutable set(RGBAColor other) {
			return set(other.col[0], other.col[1], other.col[2], other.a);
		}
		
		public Mutable set(int r, int g, int b, float a) {
			setR(r);
			setG(g);
			setB(b);
			return setA(a);
		}
		
		public Mutable set(int r, int g, int b) {
			return set(r, g, b, a);
		}
		
		public Mutable set(int gray, float a) {
			return set(gray, gray, gray, a);
		}
		
		public Mutable set(int gray) {
			return set(gray, a);
		}

		public Mutable setR(int r) {
			ensureValidNums(r);
			col[0] = r;
			return this;
		}
		
		public Mutable setG(int g) {
			ensureValidNums(g);
			col[1] = g;
			return this;
		}
		
		public Mutable setB(int b) {
			ensureValidNums(b);
			col[2] = b;
			return this;
		}
		
		public Mutable setA(float a) {
			ensureValidAlpha(a);
			this.a = a;
			return this;
		}
	}
	
	
}
