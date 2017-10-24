package ui.canvas.selection;

import geometry.Vec2;
import geometry.shapes.Ellipse;

import ui.canvas.GraphicsEllipse;
import ui.canvas.GraphicsShape;
import ui.canvas.StyleManager;
import ui.canvas.diagram.UIDiagramLayers;

/**
 * A knob/handle for adjusting the size of {@link GraphicsShape}s. Used in a
 * {@link Selector}.
 * @author David Dinkevich
 */
public abstract class Knob extends GraphicsEllipse {
	
	public static enum Directions {
		UpDown, LeftRight, All
	}
	private Directions direction;
	
	private Selector<?, ?> selector;
	
	public Knob() {
		super(new Ellipse(StyleManager.getKnobBody().getShape()));
		setBrush(StyleManager.getKnobBody().getBrush());
		setLayer(UIDiagramLayers.KNOB);
		setAllowSelection(true);
		direction = Directions.All;
	}
	
	/**
	 * Calculate where this {@link Knob} will be if moved to the given {@link Vec2} point.
	 * NOTE: this incorporates the {@link Knob}'s constraints/valid directions.
	 */
	public Vec2 correctLocation(Vec2 loc) {
		// If the knob can move in any direction, the knob can move to the given loc
		if (direction == Directions.All) {
			return loc;
		}
		
		// The center of this knob
		Vec2 center = getShape().getCenter(true);
		
		// Difference in location from the current point to loc
		Vec2 diff = Vec2.sub(loc, center);
		
		final boolean upDown = getDirection() == Directions.UpDown;
		final boolean leftRight = !upDown; // This is safe, because the option of
		// the knob's direction being Direction.ALL is precluded at the start of this
		// function.
		
		/*
		 * If the knob can only be moved up and down and the y pos of loc is the same
		 * as the current y pos, OR If the knob can only be moved left and right and
		 * the x pos of loc is the same as the current x pos, do nothing.
		 */
		if ((upDown && diff.getY() == 0f) || (leftRight && diff.getX() == 0f)) {
			return center;
		}
		return upDown ? new Vec2(center.getX(), loc.getY()) : new Vec2(loc.getX(), center.getY());
	}

	/**
	 * Moves this {@link Knob}, and if this {@link Knob} is attached to an object,
	 * adjusts the size of that object. NOTE: the {@link Knob} may not move exactly
	 * to the given location! This method uses the {@link Knob#correctLocation(Vec2)}
	 * method to incorporate this {@link Knob}'s movement constraints.
	 * @param newLoc the location to move to
	 */
	public void moveKnob(Vec2 newLoc) {		
		// If the selector is not resizeable
		if (getSelector() == null)
			return;
		if (!getSelector().isResizeable()) {
			return;
		}
		
		// If the new loc is the same as the current loc, there is nothing to do
		if (newLoc.equals(getShape().getCenter(true))) {
			return;
		}
		// Move to corrected location
		Vec2 correctedLoc = correctLocation(newLoc);
		getShape().setCenter(correctedLoc, true);

		// If this knob is attached to a selector
		if (getSelector() != null) {
			// Resize the selector, we just moved the knob, and that's what knob's are supposed
			// to do.
			resizeSelector(correctedLoc);
		}
	}
	
	/**
	 * Resize the {@link Selector} that this {@link Knob} is attached to
	 * (if there is one). If there isn't, this will do nothing.
	 * @param newPos the location of the {@link Knob} after it was moved.
	 */
	protected abstract void resizeSelector(Vec2 newPos);
	
	public void setSelector(Selector<?, ?> selector) {
		this.selector = selector;
	}
	public Selector<?, ?> getSelector() {
		return selector;
	}
	
	public void setDirection(Directions dir) {
		this.direction = dir;
	}
	public Directions getDirection() {
		return direction;
	}
}
