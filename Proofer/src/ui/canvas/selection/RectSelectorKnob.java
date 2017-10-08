package ui.canvas.selection;

import geometry.Dimension;
import geometry.Vec2;
import geometry.shapes.Ellipse;

/**
 * A {@link Knob} specifically for {@link RectSelector}s.
 * @author David Dinkevich
 */
public class RectSelectorKnob extends Knob {
	public RectSelectorKnob(Ellipse shape) {
		super(shape);
	}
	public RectSelectorKnob(float diam) {
		super(diam);
	}
	public RectSelectorKnob() {
	}

	@Override
	protected void resizeSelector(Vec2 newPos) {
		if (getSelector() == null)
			return;
		if (!getSelector().isResizeable())
			return;
				
		// Get the selector this knob is attached to
		RectSelector<?> selector = (RectSelector<?>)getSelector();
		
		// Dist from old location
		Vec2 dragDist = Vec2.sub(newPos, selector.getShape().getCenter(true));
		
		// Corner knobs
		if (getDirection() == Directions.All) {
			selector.setSize(new Dimension(dragDist.getX()*2f, dragDist.getY()*2f));
		}
		// Top bottom knob
		else if (getDirection() == Directions.UpDown) {
			selector.setHeight(dragDist.getY()*2f);
		}
		else { // Side knob
			selector.setWidth(dragDist.getX()*2f);
		}
	}
}