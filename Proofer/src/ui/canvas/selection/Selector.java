package ui.canvas.selection;

import exceptions.IllegalSelectionException;

import geometry.Vec2;
import geometry.shapes.Shape;

import ui.canvas.AdvancedCanvas;
import ui.canvas.GraphicsShape;
import ui.canvas.StyleManager;
import ui.canvas.diagram.UIDiagramLayers;

/**
 * {@linkplain Selector}s select {@link GraphicsShape}s.
 * @author David Dinkevich
 */
public abstract class Selector<SelShape extends Shape, TargetType extends GraphicsShape<?>>
extends GraphicsShape<SelShape> {
	
	private TargetType targetObject;
	private Knob[] knobs;
	
	public Selector(SelShape shape) {
		super(shape);
		setLayer(UIDiagramLayers.SELECTOR);
	}
	
	/**
	 * {@link Selector}s are NOT selectable. A selector's knobs
	 * and target object are.
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
	public final void setSelected(boolean val) {
		throw new IllegalSelectionException("Cannot select a selector");
	}
	
	@Override
	public void draw(AdvancedCanvas c) {
		// If this selector is resizeable
		if (isResizeable()) {
			// Draw all the knobs
			for (Knob k : getKnobs()) {
				k.draw(c);
			}				
		// If this selector is not selectable
		} else {
			// Set the brush to draw the selector's body.
			// The process of drawing the body will be completed by this
			// selector's subclasses
			c.setBrush(StyleManager.getSelectorBrush());
		}
	}
	
	/**
	 * Set the target object of this {@link Selector}.
	 */
	public void setTargetObject(TargetType object) {
		// Deselect the current target object (if there is one)
		deselectTargetObject();
		// Update target object
		targetObject = object;
		targetObject.setSelected(true);
		createSelectorShape();
		createKnobs();
		// Set the name of this selector to that of its target object
		getShape().setName(object.getShape().getName());
	}
	public TargetType getTargetObject() {
		return targetObject;
	}
	
	/**
	 * Deselect the current target object.
	 * @return true if the target object was successfully deselected,
	 * false if it wasn't, or if there isn't a target object.
	 */
	public boolean deselectTargetObject() {
		if (targetObject != null) {
			targetObject.setSelected(false);
			targetObject = null;
			knobs = null;
			return true;
		}
		return false;
	}
		
	/**
	 * Moving a {@link Selector} also moves its target object (assuming there is one)
	 */
	public void moveSelector(Vec2 loc) {
		// Check to save us from having to update the knob positions for no reason
		if (!loc.equals(getShape().getCenter())) {
			// Move selector's Shape
			getShape().setCenter(loc);
			// Move target object's shape
			targetObject.getShape().setCenter(loc);
			// Move knobs
			updateKnobPositions();
		}
	}
	
	/**
	 * Get the knobs of this {@link Selector}. If this selector has no target object,
	 * this will return null.
	 * @return the knobs of this {@link Selector}, or null if there is no
	 * target object.
	 */
	public Knob[] getKnobs() {
		return targetObject == null ? null : knobs;
	}
	
	/**
	 * Set the knobs of this {@link Selector}.
	 * @param knobs the knobs.
	 */
	protected void setKnobs(Knob[] knobs) {
		this.knobs = knobs;
	}
	
	/**
	 * Create the {@link Knob}s for this {@link Selector}.
	 */
	protected abstract void createKnobs();
	/**
	 * Get the default positions of the {@link Knob}s of this selector.
	 * "Default position" = the position that the knob ideally should be
	 * on this selector.
	 */
	public abstract Vec2[] getKnobPositions();
	
	/**
	 * Return each {@link Knob} to its default position on the {@link Selector}.
	 * "Default position" = the position that the knob ideally should be
	 * on this selector.
	 */
	public void updateKnobPositions() {
		Vec2[] locs = getKnobPositions();
		for (int i = 0; i < getKnobs().length; i++) {
			getKnobs()[i].getShape().setCenter(locs[i]);
		}
	}
	
	/**
	 * Create and the shape of this {@link Selector}.
	 */
	protected abstract void createSelectorShape();
	
	/**
	 * A selector is resizeable if and only if its target object is resizeable.
	 * If there is no target object, the selector is NOT resizeable.
	 */
	public boolean isResizeable() {
		return getTargetObject() != null ? getTargetObject().getShape().isResizeable() : false;
	}
}
