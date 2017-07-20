package ui.canvas.selection;

import geometry.Dimension;
import geometry.Vec2;
import geometry.shapes.Rect;
import ui.canvas.GraphicsRectEllipse;
import ui.canvas.GraphicsShape;
import ui.canvas.selection.Knob.Directions;

/**
 * A {@link Selector} to select {@link GraphicsRectEllipse}s.
 * @author David Dinkevich
 */
public class RectSelector<T extends GraphicsRectEllipse<?>> extends Selector<Rect, T> {
	/**
	 * Distance from target object.
	 * NOT CURRENTLY IMPLEMENTED!
	 */
	private float offset = 0f;
	
	public RectSelector(T targetObject) {
		super(new Rect(targetObject.getLoc(), targetObject.getSize()));
		setTargetObject(targetObject);
	}
	public RectSelector() {
	}
	
	public static boolean canSelect(GraphicsShape<?> o) {
		return o instanceof GraphicsRectEllipse;
	}
	
	@Override
	public void createKnobs() {
		if (getShape() == null) {
			return;
		}

		setKnobs(new RectSelectorKnob[8]); // 8 knobs		
		
		Vec2[] locs = getKnobPositions();
		for (int i = 0; i < getKnobs().length; i++) {
			// Create knob
			getKnobs()[i] = new RectSelectorKnob();
			
			// Shortcut
			RectSelectorKnob knob = (RectSelectorKnob)getKnobs()[i];

			// Set selector of the knob to this
			knob.setSelector(this);
			// Set position
			knob.setLoc(locs[i]);
			
			/*
			 * Set knob types. The getKnobPositions() method orders the position
			 * like this:
			 *     1. First four knobs --> corner knobs (top left, top right,
			 *        bottom right, bottom left)
			 *     2. 5th & 6th knobs --> top/bottom knobs respectively
			 *     3. 7th & 8th knobs --> right/left side knobs respectively
			 */
			
			if (i < 4) { // First four --> corner knobs
				knob.setDirection(Directions.All);
			}
			else if (i < 6) { // 5th & 6th top/bottom knobs
				knob.setDirection(Directions.UpDown);
			}
			else { // 7th & 8th side knobs
				knob.setDirection(Directions.LeftRight);
			}
		}		
	}
	
	/**
	 * Ordering of the knobs:
	 *     1. First four knobs --> corner knobs (top left, top right,
	 *        bottom right, bottom left)
	 *     2. 5th & 6th knobs --> top/bottom knobs respectively
	 *     3. 7th & 8th knobs --> right/left side knobs respectively
	 */
	@Override
	public Vec2[] getKnobPositions() {
		if (getShape() == null) {
			return null;
		}

		Dimension size = getShape().getSizeIncludeScale();
		
		Vec2 loc = getShape().getScaledCenter();

		Vec2[] positions = {
				// Corners
				
				getShape().getVertex(0).getScaledCenter(),
				getShape().getVertex(1).getScaledCenter(),
				getShape().getVertex(2).getScaledCenter(),
				getShape().getVertex(3).getScaledCenter(),
				
				// Middle knobs
				
				// Top middle
				new Vec2(loc.getX(), loc.getY()-size.getHeight()/2f),
				// Bottom middle
				new Vec2(loc.getX(), loc.getY() + size.getHeight()/2f),
				// Right middle
				new Vec2(loc.getX() + size.getWidth()/2f, loc.getY()),
				// Left middle
				new Vec2(loc.getX() - size.getWidth()/2f, loc.getY())
		};

		return positions;
	}

	@Override
	protected void createSelectorShape() {
		if (getTargetObject() != null) {
			setShape(new Rect(getTargetObject().getShape().getBoundaryRect()));
		}
	}
	
	/**
	 * Get the size of this {@link RectSelector}. If this {@link RectSelector} has a target
	 * object, this will return the size of the target object. Otherwise, null.
	 * @return the size, or null
	 */
	public Dimension getSize() {
		return getShape().getSizeIncludeScale() == null ? null : getShape().getSizeIncludeScale();
	}
	
	/**
	 * Set the size. This also sets the size of the target object (if there is one) to
	 * the new size, and updates the {@link RectSelectorKnob}s positions.
	 * @param size the new size
	 */
	public void setSize(Dimension size) {
		if (getShape() == null) {
			return;
		}
		if (!getShape().isResizeable()) {
			return;
		}
		
		Dimension.Mutable newSize = new Dimension.Mutable(size);
		
		/*
		 * Prevents the rectangle from reaching a width/height of 0
		 */
		if (size.getWidth() == 0f) {
//			size = new Dimension(getSize().getWidth(), size.getHeight());
			newSize.setWidth(getSize().getWidth());
		}
		if (size.getHeight() == 0f) {
//			size = new Dimension(size.getWidth(), getSize().getHeight());
			newSize.setHeight(getSize().getHeight());
		}
		
		// Update selector shape size
		getShape().setSize(newSize);
		// Update target object size
		getTargetObject().getShape().setSize(newSize);
		// Update the positions of the knobs
		updateKnobPositions();
	}
	
	/**
	 * Set the width. This also sets the width of the target object (if there is one) to
	 * the new width, and updates the {@link RectSelectorKnob}s positions.
	 * @param width the new width
	 */
	public void setWidth(float width) {
		if (getShape() != null) {
			setSize(new Dimension(width, getSize().getHeight()));
		}
	}
	/**
	 * Set the height. This also sets the height of the target object (if there is one) to
	 * the new height, and updates the {@link RectSelectorKnob}s positions.
	 * @param height the new height
	 */
	public void setHeight(float height) {
		if (getShape() != null) {
			setSize(new Dimension(getSize().getWidth(), height));
		}
	}
	
	public float getOffset() {
		return offset;
	}
	public void setOffset(float offset) {
		this.offset = offset;
	}
}
