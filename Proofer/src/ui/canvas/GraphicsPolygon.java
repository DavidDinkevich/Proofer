package ui.canvas;

import java.util.Arrays;

import geometry.shapes.Polygon;

public class GraphicsPolygon<T extends Polygon> extends GraphicsShape<T> {
	private TextFont vertexTextFont;
	private GraphicsVertex[] vertices;
	private boolean drawVertices = true;
	private boolean drawName = true;
	
	public GraphicsPolygon(Brush brush, T shape) {
		super(brush, shape);
		vertexTextFont = StyleManager.getTextFont();
		vertices = new GraphicsVertex[shape.getVertexCount()];
		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = new GraphicsVertex(getShape().getVertices()[i]);
			vertices[i].setTextFont(vertexTextFont);
		}
	}
	public GraphicsPolygon(T shape) {
		this(new Brush(), shape);
	}
	// Package private
	GraphicsPolygon(GraphicsPolygon<T> other) {
		super(other);
		vertexTextFont = other.vertexTextFont;
		vertices = Arrays.copyOf(other.vertices, other.vertices.length);
		drawVertices = other.drawVertices;
		drawName = other.drawName;
	}
	
	@Override
	public void draw(Canvas c) {
		super.draw(c);
		
		c.polygon(getShape());
		
		// Draw vertex labels
		
		if (drawVertices) {
			for (GraphicsVertex v : vertices) {
				v.draw(c);
			}
		}
		
		// Draw name at center of triangle
		if (drawName)
			c.text(getShape().getName(), getShape().getCenter(true));
	}
	public TextFont getVertexTextFont() {
		return vertexTextFont;
	}
	public void setVertexTextFont(TextFont vertexTextFont) {
		this.vertexTextFont = vertexTextFont;
	}
	
	public boolean doDrawName() {
		return drawName;
	}
	public void setDrawName(boolean drawName) {
		this.drawName = drawName;
	}
	
	public boolean doDrawVertices() {
		return drawVertices;
	}
	public void setDrawVertices(boolean drawVertices) {
		this.drawVertices = drawVertices;
	}
}
