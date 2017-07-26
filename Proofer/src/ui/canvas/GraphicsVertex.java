package ui.canvas;

import geometry.Vec2;
import geometry.shapes.Shape;

public class GraphicsVertex extends GraphicsShape<Shape> {
	private TextFont textFont;
	
	public GraphicsVertex(Brush brush, Shape shape) {
		super(brush, shape);
		textFont = new TextFont(10);
	}
	public GraphicsVertex(Shape shape) {
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
		
		Vec2 loc = getShape().getScaledCenter();
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
