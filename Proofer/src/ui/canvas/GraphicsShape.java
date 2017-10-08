package ui.canvas;

import exceptions.IllegalSelectionException;

import geometry.shapes.Shape;

import ui.canvas.selection.Selectable;
import ui.canvas.selection.Selector;

import util.IdentifiableObject;

/**
 * A graphical representation of a {@link Shape}.
 * @author David Dinkevich
 */
public abstract class GraphicsShape<T extends Shape>
extends IdentifiableObject
implements Drawable, Selectable {	
	
	public static final String LAYER_NAME = "shapes";

	private T shape;
	private Brush.Builder brush;
	// By default, GraphicsShapes ARE selectable
	private boolean allowSelections = true;
	private boolean selected;
	private Selector<?, ?> selector;
	
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
		allowSelections = o.allowSelections;
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
	
	@Override
	public boolean getAllowSelections() {
		return allowSelections;
	}
	
	@Override
	public void setAllowSelection(boolean selectable) {
		this.allowSelections = selectable;
	}
	
	@Override
	public boolean isSelected() {
		return selected;
	}
	
	@Override
	public void selectWithSelector(Selector<?, ?> sel) {
		if (!allowSelections) {
			throw new IllegalSelectionException("Cannot select a selectable object"
					+ "that does not allow selections.");
		}
		selected = true;
		selector = sel;
	}
	
	@Override
	public Selector<?, ?> deselect() {
		selected = false;
		Selector<?, ?> sel = selector;
		selector = null;
		return sel;
	}
	
	@Override
	public Selector<?, ?> getSelector() {
		if (!allowSelections) {
			throw new IllegalSelectionException("Cannot get selector from a selectable object "
					+ "that does not allow selections.");
		}
		return selector;
	}
}
