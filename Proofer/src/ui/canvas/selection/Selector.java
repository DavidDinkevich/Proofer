package ui.canvas.selection;

import geometry.Vec2;
import geometry.shapes.Vertex;
import geometry.shapes.VertexShape;

import ui.canvas.AdvancedCanvas;
import ui.canvas.GraphicsEllipse;
import ui.canvas.GraphicsShape;
import ui.canvas.StyleManager;
import ui.canvas.diagram.DiagramCanvas.UIDiagramLayers;


public class Selector extends GraphicsShape<VertexShape> {
	
	private Knob[] knobs;
	private GraphicsShape<? extends VertexShape> target;

	public Selector(GraphicsShape<? extends VertexShape> target) {
		super(target.getShape());
		this.target = target;
		target.setSelected(true);
		
		setLayer(UIDiagramLayers.SELECTOR);

		// Create knobs
		knobs = new Knob[target.getShape().getVertexCount()];
		Vertex[] vertices = target.getShape().getVertices();
		for (int i = 0; i < knobs.length; i++) {
			knobs[i] = new Knob(this, vertices[i]);
		}
		
		updateKnobPositions();
	}
	
	@Override
	public void draw(AdvancedCanvas c) {
		// Draw knobs
		for (Knob knob : knobs) {
			knob.draw(c);
		}
	}
	
	/**
	 * Return each {@link Knob} to its default position on the {@link Selector}.
	 * "Default position" = the position that the knob ideally should be
	 * on this selector.
	 */
	public void updateKnobPositions() {
		for (int i = 0; i < getKnobs().length; i++) {
			// TODO: change target to getshape to test that the shape is moving
			// with target
			getKnobs()[i].getShape().setCenter(target.getShape().getVertexLoc(i));
		}
	}
	
	public void setSelectorLoc(Vec2 loc) {
		target.getShape().setCenter(loc);
		updateKnobPositions();
	}
	
	public void deselectTarget() {
		target.setSelected(false);
		knobs = null;
		target = null;
	}
	
	public Knob[] getKnobs() {
		return knobs;
	}
	
	public GraphicsShape<? extends VertexShape> getTarget() {
		return target;
	}
	
	public static class Knob extends GraphicsEllipse {

		private Selector sel;
		private Vertex controlledVertex;
		
		public Knob(Selector sel, Vertex controlledVertex) {
			super(new GraphicsEllipse(StyleManager.getKnobBody()));
			setLayer(UIDiagramLayers.KNOB);
			// TODO:
//			setAllowSelection(false);
			getShape().setName("" + controlledVertex);
			this.controlledVertex = controlledVertex;
			this.sel = sel;
		}
		
		public void moveKnob(Vec2 newLoc) {
			// Move vertex in selector
			controlledVertex.setCenter(newLoc);
			// Move the knob itself
			getShape().setCenter(newLoc);
		}
		
		public Selector getSelector() {
			return sel;
		}
				
		public Vertex getControlledVertex() {
			return controlledVertex;
		}
	}

}
