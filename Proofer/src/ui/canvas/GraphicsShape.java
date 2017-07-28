package ui.canvas;

import geometry.Vec2;
import geometry.shapes.AbstractShape;

import util.IdentifiableObject;

/**
 * A graphical representation of a {@link AbstractShape}.
 * @author David Dinkevich
 */
public abstract class GraphicsShape<T extends AbstractShape> extends IdentifiableObject
implements Drawable {	

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
	public void draw(Canvas c) {
		c.setBrush(getBrush());
	}
		
	public boolean offScreen(Canvas c) {
		return getLoc().getX() < 0f || getLoc().getX() >= c.width || getLoc().getY() < 0f
				|| getLoc().getY() >= c.height;
	}
	
	public T getShape() {
		return shape;
	}
	protected void setShape(T shape) {
		this.shape = shape;
	}
	
	public Vec2 getLoc() {
		return shape.getCenter(true);
	}

	public void setLoc(Vec2 loc) {
		shape.setCenter(loc, true);
	}
	
	/**
	 * Delegate method for setting the scale of this {@link GraphicsShape}'s shape.
	 * @param scale the new scale
	 */
	public void setScale(Vec2 scale) {
		shape.setScale(scale);
	}
	
	/**
	 * Delegate method for setting the scale of this {@link GraphicsShape}'s shape.
	 * @param scale the new scale
	 * @param dilationPoint the point of dilation
	 */
	public void setScale(Vec2 scale, Vec2 dilationPoint) {
		shape.setScale(scale, dilationPoint);
	}
	
	/**
	 * Delegate method for getting the scale of this {@link GraphicsShape}'s shape.
	 */
	public Vec2 getScale() {
		return shape.getScale();
	}
	/**
	 * Delegate method for getting whether this {@link GraphicsShape}'s shape contains
	 * the given point.
	 */
	public boolean containsPoint(Vec2 point, boolean incorporateScale) {
		return shape.containsPoint(point, incorporateScale);
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
