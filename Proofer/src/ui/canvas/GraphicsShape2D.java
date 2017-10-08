package ui.canvas;

import exceptions.IllegalSelectionException;

import geometry.shapes.Shape2D;

import ui.canvas.selection.Selector;

/**
 * Descendants of this class are 2 dimensional {@link GraphicsShape}s.
 * @author David Dinkevich
 */
public class GraphicsShape2D<T extends Shape2D> extends GraphicsShape<T> {
	
	public static final String LAYER_NAME = "shapes";
	
	// By default, GraphicsShape2Ds are NOT selectable
	private boolean allowSelections;
	private boolean selected;
	private Selector<?, ?> selector;
	
	protected GraphicsShape2D(Brush brush, T shape) {
		super(brush, shape);
		init();
	}
	protected GraphicsShape2D(T shape) {
		super(shape);
		init();
	}
	protected GraphicsShape2D(Brush brush) {
		super(brush);
		init();
	}
	protected GraphicsShape2D() {
		init();
	}
	private void init() {
		setLayer(LAYER_NAME);
	}
	/**
	 * Copy constructor. This does not copy the <i>selected</i> property,
	 * so products of this method will not be selected.
	 */
	public GraphicsShape2D(GraphicsShape2D<T> copy) {
		super(copy);
		allowSelections = copy.allowSelections;
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
