package ui.canvas.diagram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import geometry.Dimension;
import geometry.Vec2;
import geometry.shapes.Polygon;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;
import geometry.shapes.VertexBuffer;

import javafx.scene.paint.Color;

import ui.canvas.AdvancedCanvas;
import ui.canvas.Brush;
import ui.canvas.GraphicsShape;
import ui.canvas.GraphicsTriangle;
import ui.canvas.GraphicsVertexBuffer;
import ui.canvas.StyleManager;
import ui.canvas.selection.InputManager;

public class DiagramCanvas extends AdvancedCanvas {
	
	private List<GraphicsShape<?>> diagramFigures;
	
	private DiagramCanvasGrid canvasGrid;
	private RenderList renderList;
	private VertexBuffer vertexBuff;
	private InputManager inputManager;

	public DiagramCanvas(int w, int h) {
		super(w, h);
		
		canvasGrid = new DiagramCanvasGrid(this, new Dimension(50f));
		diagramFigures = new ArrayList<>();
		renderList = new RenderList();
		vertexBuff = new VertexBuffer();
		inputManager = new InputManager(this);
		
		// Add layers to render list
		for (UIDiagramLayers lay : UIDiagramLayers.values()) {
			renderList.addLayer(lay);
		}
		
		// Add a GraphicsVertexBuffer to render the VertexBuffer
		renderList.addDrawable(new GraphicsVertexBuffer(vertexBuff));		
	}

	@Override
	public void redraw() {
		super.redraw();
		
		gc.setFill(Color.PINK);
		fillRect(Vec2.ZERO, Dimension.TEN);
				
		// Draw canvas first
		canvasGrid.draw(this);
		renderList.draw(this);		
		inputManager.draw(this);

	}
	
	public void addDiagramFigure(GraphicsShape<?> shape) {
		// Add to list to diagram figures list
		diagramFigures.add(shape);
		// Add to render list
		renderList.addDrawable(shape);
		// If it's a polygon, add it to VertexBuffer
		if (shape.getShape() instanceof Polygon) {
			Polygon poly = (Polygon)shape.getShape();
			vertexBuff.addPolygon(poly);
		}
		// Add to list of selectables
		if (inputManager != null) {
			inputManager.addSelectableFigure(shape);
		}
	}
	
	public void addDiagramFigures(Collection<GraphicsShape<?>> elements) {
		for (GraphicsShape<?> shape : elements) {
			addDiagramFigure(shape);
		}
	}
	
	public boolean removeDiagramFigure(GraphicsShape<?> shape) {
		// Remove from diagram figures list
		if (diagramFigures.remove(shape)) {
			// Remove from RenderList
			renderList.removeDrawable(shape);
			// Remove from list of selectables
			if (inputManager != null) {
				inputManager.removeSelectableFigure(shape);
			}
			// If it's a polygon, remove from Vertex
			if (shape.getShape() instanceof Polygon) {
				Polygon poly = (Polygon)shape.getShape();
				vertexBuff.removePolygon(poly);
			}
			return true;
		}
		return false;
	}
	
	public void removeDiagramFigures(Collection<GraphicsShape<?>> elements) {
		for (GraphicsShape<?> shape : elements) {
			removeDiagramFigure(shape);
		}
	}
	
	public List<GraphicsShape<?>> getDiagramFigures() {
		return diagramFigures;
	}
	
	public RenderList getRenderList() {
		return renderList;
	}
	
	public DiagramCanvasGrid getCanvasGrid() {
		return canvasGrid;
	}
	
	public InputManager getInputManager() {
		return inputManager;
	}
	
	public VertexBuffer getVertexBuffer() {
		return vertexBuff;
	}
	
}
