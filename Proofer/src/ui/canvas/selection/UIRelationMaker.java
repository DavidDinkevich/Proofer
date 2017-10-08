package ui.canvas.selection;

import ui.canvas.GraphicsSegment;
import ui.canvas.StyleManager;

import geometry.Vec2;
import geometry.shapes.Segment;
import geometry.shapes.Vertex;

/**
 * A segment that can be dragged by the mouse and expanded. When
 * the mouse is released, a {@link FigureRelation} will be formed between
 * the two figures on which the end-points of the segment lie.
 * @author David Dinkevich
 */
public class UIRelationMaker extends GraphicsSegment {
	
	public UIRelationMaker(Vec2 v0, Vec2 v1) {
		super(StyleManager.getUIRelationMakerBrush(), makeShape(v0, v1));
	}
	
	public UIRelationMaker() {
		this(Vec2.ZERO, Vec2.ZERO);
	}
	
	private static Segment makeShape(Vec2 v0, Vec2 v1) {
		return new Segment(new Vertex('A', v0), new Vertex('B', v1));
	}
	
}
