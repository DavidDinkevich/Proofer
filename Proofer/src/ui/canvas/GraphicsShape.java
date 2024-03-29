package ui.canvas;

import geometry.shapes.Shape;

import ui.canvas.diagram.DiagramCanvas.UIDiagramLayers;
import ui.canvas.selection.Selectable;

/**
 * A graphical representation of a {@link Shape}.
 * @author David Dinkevich
 */
public abstract class GraphicsShape<T extends Shape> implements Drawable, Selectable {	
	private T shape;
	private Brush.Builder brush;

	private boolean allowSelections;
	private boolean selected;
	
	// The Layer to which this object belongs
	private UIDiagramLayers layer;
	
	public GraphicsShape(Brush brush, T shape) {
		if (shape == null)
			throw new NullPointerException("A GraphicsShape's Shape may not be null!!!");
		this.shape = shape;
		this.brush = new Brush.Builder(brush);
		layer = UIDiagramLayers.GRAPHICS_SHAPE;
		// By default, GraphicsShapes ARE selectable
		allowSelections = true;
	}
	
	public GraphicsShape(T shape) {
		this(new Brush.Builder(), shape);
	}
	
	public GraphicsShape(GraphicsShape<T> o) {
		brush = new Brush.Builder(o.brush);
		layer = o.layer;
		allowSelections = o.allowSelections;
	}
	
	@Override
	public String toString() {
		return shape.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof GraphicsShape))
			return false;
		GraphicsShape<?> other = (GraphicsShape<?>)o;
		return getBrush().equals(other.getBrush()) && getShape().equals(other.getShape());
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + shape.hashCode();
		result = 31 * result + brush.hashCode();
		return result;
	}
	
	@Override
	public void draw(AdvancedCanvas c) {
		c.setBrush(getBrush());
	}
	
	public T getShape() {
		return shape;
	}
	protected void setShape(T shape) {
		if (shape == null)
			throw new NullPointerException("A GraphicsShape's Shape may not be null!!!");
		this.shape = shape;
	}
	
	public Brush.Builder getBrush() {
		return brush;
	}
	
	public void setBrush(Brush brush) {
		this.brush.set(brush);
	}
	
	@Override
	public UIDiagramLayers getLayer() {
		return layer;
	}
	
	public void setLayer(UIDiagramLayers layer) {
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
	public void setSelected(boolean val) {
		selected = val;
	}
}
