package ui.canvas.diagram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.scene.paint.Color;

import geometry.Dimension;
import geometry.Vec2;
import geometry.proofs.Figure;
import geometry.shapes.VertexBuffer;
import geometry.shapes.VertexShape;

import ui.canvas.AdvancedCanvas;
import ui.canvas.GraphicsShape;
import ui.canvas.GraphicsVertexBuffer;
import ui.canvas.selection.InputManager;


public class DiagramCanvas extends AdvancedCanvas {
	
	public enum UIDiagramLayers {
		DEFAULT, GRID, INVISIBLE_HIDDEN_FIGURES, 
		GRAPHICS_SHAPE, POLYGON_COMPONENT, SELECTOR, KNOB
	}
	
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
		
		// Add VertexShapes to the VertexBuffer
		if (shape.getShape() instanceof VertexShape) {
			vertexBuff.addVertexShape((VertexShape) shape.getShape());
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
			
			// Remove VertexShapes from the VertexBuffer
			if (shape.getShape() instanceof VertexShape) {
				vertexBuff.removeVertexShape((VertexShape) shape.getShape());
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
	
	public boolean containsDiagramFigure(String name, Class<?> c) {
		for (GraphicsShape<?> gshape : diagramFigures) {
			Figure shape = gshape.getShape();
			if (shape.getClass() == c && shape.isValidName(name)) {
				return true;
			}
		}
		return false;
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
