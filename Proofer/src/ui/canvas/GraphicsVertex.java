package ui.canvas;

import geometry.Vec2;
import geometry.shapes.AbstractShape;

public class GraphicsVertex extends GraphicsShape<AbstractShape> {
	private TextFont textFont;
	
	public GraphicsVertex(Brush brush, AbstractShape shape) {
		super(brush, shape);
		textFont = new TextFont(10);
	}
	public GraphicsVertex(AbstractShape shape) {
		super(shape);
		textFont = new TextFont(10);
	}
	public GraphicsVertex(Brush brush) {
		super(brush);
		textFont = new TextFont(10);
	}
	public GraphicsVertex() {
		super();
		textFont = new TextFont(10);
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
		
		Vec2 loc = getShape().getCenter(true);
		Vec2 labelLoc = new Vec2(loc.getX() + 10, loc.getY() - 26);
		c.text(getShape().getName(), labelLoc);
	}
	
	public TextFont getTextFont() {
		return textFont;
	}
	public void setTextFont(TextFont textFont) {
		this.textFont = textFont;
	}
	
}
