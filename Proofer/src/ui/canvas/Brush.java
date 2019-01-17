package ui.canvas;

/**
 * @author David Dinkevich
 */
public class Brush {
	public static final RGBAColor BLACK, WHITE, RED, BLUE, LIGHT_BLUE, GREEN,
							YELLOW, ORANGE, PURPLE, PINK, BROWN;
	
	static {		
		BLACK = new RGBAColor(0);
		WHITE = new RGBAColor(255);
		RED = new RGBAColor(255, 0, 0);
		BLUE = new RGBAColor(0, 0, 255);
		LIGHT_BLUE = new RGBAColor(50, 150, 255);
		GREEN = new RGBAColor(0, 255, 0);
		YELLOW = new RGBAColor(255, 255, 0);
		ORANGE = new RGBAColor(255, 100, 0);
		PURPLE = new RGBAColor(150, 50, 255);
		PINK = new RGBAColor(255, 0, 255);
		BROWN = new RGBAColor(120, 75, 0);
	}
		
	protected RGBAColor.Mutable fill;
	protected RGBAColor.Mutable stroke;
	protected float strokeWeight;

	protected Brush(Builder b) {
		fill = new RGBAColor.Mutable(b.fill);
		stroke = new RGBAColor.Mutable(b.stroke);
		strokeWeight = b.strokeWeight;
	}
	public Brush() {
		fill = new RGBAColor.Mutable();
		stroke = new RGBAColor.Mutable();
		strokeWeight = 1f;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Brush))
			return false;
		Brush b = (Brush)o;
		return     b.fill.equals(fill)
				&& b.stroke.equals(stroke)
				&& b.strokeWeight == strokeWeight;
	}
	
	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + fill.hashCode();
		result = 31 * result + stroke.hashCode();
		result = 31 * result + Float.floatToIntBits(strokeWeight);
		return result;
	}

	public RGBAColor getFill() {
		return fill;
	}
	
	public RGBAColor getStroke() {
		return stroke;
	}
		
	public float getStrokeWeight() {
		return strokeWeight;
	}
	
	
	public static class Builder extends Brush {
		public Builder(Brush b) {
			set(b);
		}
		
		public Builder() {
		}
		
		public Builder set(Brush b) {
			fill.set(b.fill);
			stroke.set(b.stroke);
			strokeWeight = b.strokeWeight;
			return this;
		}
		
		public Brush buildBrush() {
			return new Brush(this);
		}
		
		@Override
		public RGBAColor.Mutable getFill() {
			return fill;
		}
		
		@Override
		public RGBAColor.Mutable getStroke() {
			return stroke;
		}
		
		public Builder setFill(RGBAColor fill) {
			this.fill.set(fill);
			return this;
		}

		public Builder setStroke(RGBAColor stroke) {
			this.stroke.set(stroke);
			return this;
		}
		
		public Builder setStrokeWeight(float strokeWeight) {
			this.strokeWeight = strokeWeight;
			return this;
		}	
	}
}
