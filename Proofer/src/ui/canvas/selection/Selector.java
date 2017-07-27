package ui.canvas.selection;

import exceptions.IllegalSelectionException;
import geometry.Vec2;
import geometry.shapes.AbstractShape2D;
import ui.canvas.Canvas;
import ui.canvas.GraphicsShape;
import ui.canvas.GraphicsShape2D;
import ui.canvas.StyleManager;

/**
 * {@linkplain Selector}s select {@link GraphicsShape}s.
 * @author David Dinkevich
 */
public abstract class Selector<SelShape extends AbstractShape2D, TargetType extends GraphicsShape2D<?>>
extends GraphicsShape2D<SelShape> {

	public static final String LAYER_NAME;
	
	static {
		LAYER_NAME = "selectors";
	}
	
	private TargetType targetObject;
	private Knob[] knobs;
	
	public Selector(SelShape shape) {
		super(shape);
		setLayer(LAYER_NAME);
		if (getShape() != null)
			setResizeable(false);
	}
	public Selector() {
		this(null);
	}
	
	/**
	 * {@link Selector}s are NOT selectable.
	 */
	@Override
	public final boolean getAllowSelections() {
		return false;
	}
	
	@Override
	public final void setAllowSelection(boolean selectable) {
		throw new IllegalSelectionException("Cannot select a selector");
	}
	
	@Override
	public void draw(Canvas c) {
		if (getShape() != null) {
			if (isResizeable()) {
				for (Knob k : getKnobs()) {
					k.draw(c);
				}				
			} else {
				c.setBrush(StyleManager.getSelectorBrush());
			}
		}
	}
	
	/**
	 * Set the target object of this {@link Selector}.
	 */
	public void setTargetObject(TargetType object) {
		if (object == null) {
			deselectTargetObject();
			return;
		}
		targetObject = object;
		targetObject.selectWithSelector(this);
		createSelectorShape();
		createKnobs();
	}
	public TargetType getTargetObject() {
		return targetObject;
	}
	
	public void deselectTargetObject() {
		if (targetObject != null) {
			targetObject.deselect();
			targetObject = null;
			knobs = null;
		}
	}
	
	/**
	 * Returns true if this {@link Selector} can select the given object.
	 */
	public static boolean canSelect(GraphicsShape<?> o) {
		return o instanceof GraphicsShape2D;
	}
		
	/**
	 * Moving a {@link Selector} also moves its target object (assuming there is one)
	 */
	@Override
	public void setLoc(Vec2 loc) {
		// Check to save us from having to update the knob positions for no reason
		if (!loc.equals(getLoc())) {
			super.setLoc(loc);
			targetObject.setLoc(loc);
			updateKnobPositions();
		}
	}
	
	@Override
	public void setScale(Vec2 scale, Vec2 dilationPoint) {
		if (getShape() != null) {
			super.setScale(scale, dilationPoint);
			updateKnobPositions();
			/*
			 * Notice that the scale of the knobs are not changed.
			 * This is because we don't want the knobs to get bigger,
			 * we want them to stay the same size.
			 */
		}
	}
	
	public Knob[] getKnobs() {
		if (knobs == null) {
			createKnobs();
		}
		return knobs;
	}
	protected void setKnobs(Knob[] knobs) {
		this.knobs = knobs;
	}
	
	/**
	 * Create the {@link Knob}s for this {@link Selector}.
	 */
	protected abstract void createKnobs();
	/**
	 * Get the default positions for the {@link Knob}s of this selector.
	 */
	public abstract Vec2[] getKnobPositions();
	
	/**
	 * Return each {@link Knob} to its default position on the {@link Selector}.
	 */
	public void updateKnobPositions() {
		Vec2[] locs = getKnobPositions();
		for (int i = 0; i < getKnobs().length; i++) {
			getKnobs()[i].setLoc(locs[i]);
		}
	}
	
	/**
	 * Create and the shape of this {@link Selector}. The size of the shape is
	 * determined by the size of the target object. If no target object exists, this
	 * method will do nothing.
	 */
	protected abstract void createSelectorShape();
	
	@Override
	public boolean isResizeable() {
		return getTargetObject() != null ? getTargetObject().isResizeable() : super.isResizeable();
	}
}
