package ui.canvas.selection;

import geometry.Vec2;
import geometry.shapes.Vertex;

/**
 * A {@link Knob} specifically for {@link PolygonSelector}s.
 * @author David Dinkevich
 */
public class PolygonSelectorKnob extends Knob {
	/**
	 * The {@link Vertex} of the target {@link PolygonSelector} that this
	 * {@link PolygonSelectorKnob} moves/controls.
	 */
	private Vertex controlledVertex;
	
	public PolygonSelectorKnob() {
	}
	
	@Override
	protected void resizeSelector(Vec2 newPos) {
		if (getSelector() == null || controlledVertex == null) {
			return;
		}
		// Change corner position
		PolygonSelector sel = (PolygonSelector) getSelector();
		sel.setVertexLoc(controlledVertex.getNameChar(), newPos);
	}
	
	/**
	 * Get the {@link Vertex} of the target {@link PolygonSelector} that this
	 * {@link PolygonSelectorKnob} moves/controls.
	 */
	public Vertex getControlledVertex() {
		return controlledVertex;
	}
	/**
	 * Set the {@link Vertex} of the target {@link PolygonSelector} that this
	 * {@link PolygonSelectorKnob} moves/controls.
	 */
	public void setControlledVertex(Vertex vertex) {
		controlledVertex = vertex;
		// Set the name of this knob to that of the vertex it controls
		getShape().setName(controlledVertex.getName());
	}
}
