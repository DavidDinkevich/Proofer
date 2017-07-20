package ui.canvas;

/**
 * Manipulates how a {@link GraphicsShape} is drawn to a {@link Canvas}.
 * <p>
 * This class is immutable.
 * @author David Dinkevich
 */
public class Brush {
	protected int fill;
	protected int stroke;
	protected float strokeWeight;
	protected float alpha;
	protected boolean renderFill;
	protected boolean renderStroke;
	
	protected Brush(Builder b) {
		fill = b.fill;
		stroke = b.stroke;
		strokeWeight = b.strokeWeight;
		alpha = b.alpha;
		renderFill = b.renderFill;
		renderStroke = b.renderStroke;
	}
	public Brush() {
		fill = 0;
		stroke = 0;
		strokeWeight = 1;
		alpha = 0;
		renderFill = true;
		renderStroke = true;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Brush))
			return false;
		Brush b = (Brush)o;
		return b.fill == fill && b.stroke == stroke && b.strokeWeight == strokeWeight
				&& b.alpha == alpha && b.renderFill == renderFill &&
				b.renderStroke == renderStroke;
	}
	
	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + fill;
		result = 31 * result +  stroke;
		result = 31 * result + Float.floatToIntBits(strokeWeight);
		result = 31 * result + Float.floatToIntBits(alpha);
		result = 31 * result + Boolean.hashCode(renderFill);
		result = 31 * result + Boolean.hashCode(renderStroke);
		return result;
	}

	public int getFill() {
		return fill;
	}
	public boolean renderFill() {
		return renderFill;
	}
	public boolean renderStroke() {
		return renderStroke;
	}
	public int getStroke() {
		return stroke;
	}
	public float getStrokeWeight() {
		return strokeWeight;
	}
	public float getAlpha() {
		return alpha;
	}
	
	public static class Builder extends Brush {
		public Builder(Brush b) {
			set(b);
		}
		public Builder() {
			alpha = 255; // Prevent alpha from being transparent (0) by default
		}
		
		public Builder set(Brush b) {
			fill = b.fill;
			stroke = b.stroke;
			strokeWeight = b.strokeWeight;
			alpha = b.alpha;
			renderFill = b.renderFill;
			renderStroke = b.renderStroke;
			return this;
		}
		
		public Brush buildBrush() {
			return new Brush(this);
		}
		
		public Builder setFill(int fill) {
			this.fill = fill;
			return this;
		}
		public Builder setStroke(int stroke) {
			this.stroke = stroke;
			return this;
		}
		public Builder setStrokeWeight(float strokeWeight) {
			this.strokeWeight = strokeWeight;
			return this;
		}
		public Builder setAlpha(float alpha) {
			this.alpha = alpha;
			return this;
		}
		public Builder setRenderFill(boolean renderFill) {
			this.renderFill = renderFill;
			return this;
		}
		public Builder setRenderStroke(boolean renderStroke) {
			this.renderStroke = renderStroke;
			return this;
		}
	}
}
