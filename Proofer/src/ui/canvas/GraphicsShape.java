package ui.canvas;

import geometry.shapes.AbstractShape;
import geometry.shapes.Shape;

import ui.canvas.selection.Selectable;

import util.IdentifiableObject;

/**
 * A graphical representation of a {@link AbstractShape}.
 * @author David Dinkevich
 */
public abstract class GraphicsShape<T extends Shape>
extends IdentifiableObject
implements Drawable, Selectable {	

	private T shape;
	private Brush.Builder brush;
	
	// The Layer to which this object belongs
	private String layer = LayerManager.DEFAULT_LAYER;
	
	public GraphicsShape(Brush brush, T shape) {
		this.brush = new Brush.Builder(brush);
		this.shape = shape;
	}
	public GraphicsShape(Brush brush) {
		this(brush, null);
	}
	public GraphicsShape(T shape) {
		this(new Brush.Builder(), shape);
	}
	public GraphicsShape() {
		this(new Brush.Builder());
	}
	public GraphicsShape(GraphicsShape<T> o) {
		brush = new Brush.Builder(o.brush.buildBrush());
		layer = o.layer;
	}
	
	@Override
	public String toString() {
		return shape.toString();
	}
	
	@Override
	public void draw(Canvas c) {
		c.setBrush(getBrush());
	}
	
	public T getShape() {
		return shape;
	}
	protected void setShape(T shape) {
		this.shape = shape;
	}
	
	public Brush.Builder getBrush() {
		return brush;
	}
	public void setBrush(Brush brush) {
		this.brush.set(brush);
	}
	public String getLayer() {
		return layer;
	}
	public void setLayer(String layer) {
		this.layer = layer;
	}
}
