package ui.canvas;

import geometry.Vec2;
import geometry.shapes.Vertex;
import ui.canvas.selection.Selector;

// TODO: implement unimplemented methods
public class GraphicsVertex extends GraphicsShape<Vertex> {
	private TextFont textFont;
	
	public GraphicsVertex(Brush brush, Vertex shape) {
		super(brush, shape);
		textFont = new TextFont();
	}
	
	public GraphicsVertex(Vertex shape) {
		super(shape);
		textFont = new TextFont();
	}
	
	public GraphicsVertex(Brush brush) {
		super(brush);
		textFont = new TextFont();
	}
	
	public GraphicsVertex() {
		super();
		textFont = new TextFont();
	}
	
	public GraphicsVertex(GraphicsVertex o) {
		super(o);
		textFont = o.textFont;
	}
	
	@Override
	public void draw(Canvas c) {
		super.draw(c);
		c.textAlign(textFont.getAlignmentX(), textFont.getAlignmentY());
		c.textSize(textFont.getSize());
		
		Vec2 center = getShape().getCenter(true);
		Vec2 offset = new Vec2(10, -26);
		Vec2 labelLoc = Vec2.add(center, offset);
		
		c.text(getShape().getName(), labelLoc);
	}
	
	public TextFont getTextFont() {
		return textFont;
	}
	
	public void setTextFont(TextFont textFont) {
		this.textFont = textFont;
	}

	@Override
	public boolean getAllowSelections() {
		return false;
	}

	@Override
	public void setAllowSelection(boolean selectable) {
		
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void selectWithSelector(Selector<?, ?> sel) {		
	}

	@Override
	public Selector<?, ?> deselect() {
		return null;
	}

	@Override
	public Selector<?, ?> getSelector() {
		return null;
	}
	
}
