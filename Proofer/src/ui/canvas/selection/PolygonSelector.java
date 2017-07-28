package ui.canvas.selection;

import geometry.Vec2;
import geometry.shapes.SimplePolygon;
import ui.canvas.GraphicsShape;
import ui.canvas.Canvas;
import ui.canvas.GraphicsPolygon;

/**
 * A {@link Selector} to select {@link GraphicsPolygon}s.
 * @author David Dinkevich
 */
public class PolygonSelector extends Selector<SimplePolygon, GraphicsPolygon<?>> {
	public PolygonSelector(GraphicsPolygon<?> o) {
		setTargetObject(o);
	}
	public PolygonSelector() {
		super(new SimplePolygon());
	}
	
	public static boolean canSelect(GraphicsShape<?> o) {
		return o instanceof GraphicsPolygon;
	}
	
	@Override
	public void draw(Canvas c) {
		super.draw(c);
		if (!isResizeable()) {
			c.polygon(getShape());
		}
	}
	
	@Override
	protected void createKnobs() {
		if (getShape() == null) {
			return;
		}
		setKnobs(new PolygonSelectorKnob[3]); // 3 knobs
		Vec2[] locs = getKnobPositions();
		for (int i = 0; i < getKnobs().length; i++) {
			getKnobs()[i] = new PolygonSelectorKnob();
			getKnobs()[i].setLoc(locs[i]);
			getKnobs()[i].setSelector(this);
			PolygonSelectorKnob knob = (PolygonSelectorKnob)getKnobs()[i];
			knob.setControlledVertex(getShape().getVertices()[i]);
		}
	}

	@Override
	public Vec2[] getKnobPositions() {
		return getShape() == null ? null : getShape().getVertexLocations();
	}
	
	@Override
	protected void createSelectorShape() {
		if (getTargetObject() != null) {
			setShape(getTargetObject().getShape());
		}
	}
	
	public void setVertexLoc(char vertexLabel, Vec2 point) {
		if (getShape() == null) {
			return;
		}
		// Update selector shape
		getShape().setVertexLoc(vertexLabel, point, true);
		// Update target object shape
		getTargetObject().getShape().setVertexLoc(vertexLabel, point, true);
	}
}
