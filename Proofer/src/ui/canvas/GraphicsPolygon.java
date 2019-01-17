package ui.canvas;

import java.util.Arrays;

import geometry.shapes.Polygon;

public class GraphicsPolygon<T extends Polygon> extends GraphicsShape<T> {
	private GraphicsVertex[] vertices;
	private boolean drawVertices = false;
	private boolean drawName = false;
		
	public GraphicsPolygon(Brush brush, T shape) {
		super(brush, shape);
		// TODO: crashes
//		vertexTextFont = StyleManager.getTextFont();
		vertices = new GraphicsVertex[shape.getVertexCount()];
		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = new GraphicsVertex(getShape().getVertices()[i]);
		}
	}
	public GraphicsPolygon(T shape) {
		this(new Brush(), shape);
	}
	// Package private
	GraphicsPolygon(GraphicsPolygon<T> other) {
		super(other);
		vertices = Arrays.copyOf(other.vertices, other.vertices.length);
		drawVertices = other.drawVertices;
		drawName = other.drawName;
	}
	
	@Override
	public void draw(AdvancedCanvas c) {
		super.draw(c);
		
		c.fillPolygon(getShape());
		c.strokePolygon(getShape());
		
		// Draw vertex labels
		
		if (drawVertices) {
			for (GraphicsVertex v : vertices) {
				v.draw(c);
			}
		}
		
		// Draw name at center of triangle
		if (drawName)
			c.fillText(getShape().getName(), getShape().getCenter());
	}
		
	public boolean drawName() {
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
