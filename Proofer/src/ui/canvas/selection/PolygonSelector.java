package ui.canvas.selection;

import geometry.Vec2;
import geometry.shapes.Polygon;
import geometry.shapes.SimplePolygon;

import ui.canvas.GraphicsShape;
import ui.canvas.AdvancedCanvas;
import ui.canvas.GraphicsPolygon;

/**
 * A {@link Selector} to select {@link GraphicsPolygon}s.
 * @author David Dinkevich
 */
public class PolygonSelector extends Selector<Polygon, GraphicsPolygon<?>> {
	public PolygonSelector(GraphicsPolygon<?> o) {
		// Copy the vertices AND THE NAME of the target object.
		// (we can derive a name from the names of the vertices)
		super(new SimplePolygon(o.getShape().getVertices()));
		setTargetObject(o);
	}
	public PolygonSelector() {
		super(new SimplePolygon());
	}
	
	public static boolean canSelect(GraphicsShape<?> o) {
		return o instanceof GraphicsPolygon;
	}
	
	@Override
	public void draw(AdvancedCanvas c) {
		super.draw(c);
		// If this selector is not resizeable
		if (!isResizeable()) {
			// Draw the polygon's body
			c.fillPolygon(getShape());
		}
	}
	
	@Override
	protected void createKnobs() {
		// Can't make knobs unless there is a target object
		if (getTargetObject() == null)
			return;
		
		setKnobs(new PolygonSelectorKnob[3]); // 3 knobs
		// Get the positions of the knobs
		Vec2[] locs = getKnobPositions();
		// For each new knob
		for (int i = 0; i < getKnobs().length; i++) {
			// Instantiate
			getKnobs()[i] = new PolygonSelectorKnob();
			// Set center
			getKnobs()[i].getShape().setCenter(locs[i]);
			// Tell them that this selector is da boss
			getKnobs()[i].setSelector(this);
			PolygonSelectorKnob knob = (PolygonSelectorKnob)getKnobs()[i];
			// Give them a vertex to control (move, etc.)
			knob.setControlledVertex(getShape().getVertices()[i]);
		}
	}

	@Override
	public Vec2[] getKnobPositions() {
		// Assuming there is a target object, the knobs' locations are the locations
		// of the vertices
		return getTargetObject() == null ? null : getShape().getVertexLocations();
	}
	
	@Override
	protected void createSelectorShape() {
		if (getTargetObject() != null) {
			// Copy the shape
			setShape(new SimplePolygon(getTargetObject().getShape().getVertices()));
		}
	}
	
	public void setVertexLoc(char vertexLabel, Vec2 point) {
		if (getShape() == null) {
			return;
		}
		// Update selector shape
		getShape().setVertexLoc(vertexLabel, point);
		// Update target object shape
		getTargetObject().getShape().setVertexLoc(vertexLabel, point);
	}
}
