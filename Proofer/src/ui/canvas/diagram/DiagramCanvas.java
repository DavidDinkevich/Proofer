package ui.canvas.diagram;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import geometry.Dimension;
import geometry.Vec2;
import geometry.shapes.Polygon;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;
import geometry.shapes.VertexBuffer;

import ui.canvas.Brush;
import ui.canvas.Canvas;
import ui.canvas.GraphicsShape;
import ui.canvas.GraphicsTriangle;
import ui.canvas.StyleManager;
import ui.canvas.selection.InputManager;
import ui.swing.ProofCustomizationPanel;

public class DiagramCanvas extends Canvas {
	private static final long serialVersionUID = -6415389681784791979L;
	
	private ProofCustomizationPanel parentPanel;
	private InputManager inputManager;
	private DiagramCanvasGrid canvasGrid;
	private VertexBuffer vertexBuff;
	private RenderList renderList;
	
	private List<GraphicsShape<?>> diagramFigures;

	public DiagramCanvas(ProofCustomizationPanel parentPanel,
			Dimension size, int background) {
		super(size, background);
		this.parentPanel = parentPanel;
		_init();
	}

	public DiagramCanvas(ProofCustomizationPanel parentPanel, Dimension size) {
		super(size);
		this.parentPanel = parentPanel;
		_init();
	}

	public DiagramCanvas(ProofCustomizationPanel parentPanel, int background) {
		super(background);
		this.parentPanel = parentPanel;
		_init();
	}
	
	private void _init() { // Underscore bc init() already exists in PApplet
		renderList = new RenderList();
		diagramFigures = new ArrayList<>();
		vertexBuff = new VertexBuffer();
		// Add layers to render list
		for (UIDiagramLayers lay : UIDiagramLayers.values()) {
			renderList.addLayer(lay);
		}
		setCanvasGrid(canvasGrid = new DiagramCanvasGrid(this, new Dimension(50f)));
		setInputManager(inputManager = new InputManager(this));
	}
	
	@Override
	public void setup() {
		super.setup();
		
		/*
		 * EVERYTHING BELLOW IS TEMPORARY, FOR TESTING PURPOSES.
		 * WILL BE DELETED
		 */
		
		Brush brush = StyleManager.getDefaultFigureBrush();

		Vertex[] tpoints = new Vertex[] {
				new Vertex(new Vec2(0f, -200.0f)),
				new Vertex(new Vec2(300f, 100f)),
				new Vertex(new Vec2(0f, 100f))
		};
		GraphicsTriangle tri = new GraphicsTriangle(brush, new Triangle(Arrays.asList(tpoints)));
		tri.setAllowSelection(true);
		tri.setSelected(true);
		
		GraphicsTriangle tri2 = new GraphicsTriangle(brush, new Triangle(Arrays.asList(
				new Vertex[] {
						new Vertex(new Vec2(-10f, 100f)),
						new Vertex(new Vec2(-10f, -200f)),
						new Vertex(new Vec2(-300f, 100f))
					}
		)));
		tri2.setAllowSelection(true);
		
		addDiagramFigures(Arrays.asList(tri, tri2));
	}
	
	@Override
	public void draw() {
		super.draw();
		
		// Draw canvas first
		if (canvasGrid != null)
			canvasGrid.draw(this);
		
		renderList.draw(this);
		
		if (inputManager != null)
			inputManager.draw(this);
	}
	
	/**
	 * Return this {@link Canvas}'s {@link InputManager}. If
	 * no {@link InputManager} has been assigned to this {@link Canvas},
	 * this will return null.
	 */
	public InputManager getInputManager() {
		return inputManager;
	}
	
	/**
	 * Set this {@link Canvas}'s {@link InputManager}. If this
	 * {@link Canvas} already has an {@link InputManager}, the given
	 * manager will override the old one.
	 * @return the old {@link InputManager}, or null if this
	 * {@link Canvas} did not previously have one.
	 */
	public InputManager setInputManager(InputManager sel) {
		InputManager old = inputManager;
		inputManager = sel;
		if (inputManager != null)
			addCanvasListener(inputManager);
		else {
			removeCanvasListener(old);
		}
		return old;
	}
	
	/**
	 * Get this RenderList
	 * @return the render list
	 */
	public RenderList getRenderList() {
		return renderList;
	}
	
	/**
	 * Return this {@link Canvas}'s {@link DiagramCanvasGrid}. If
	 * no {@link DiagramCanvasGrid} has been assigned to this {@link Canvas},
	 * this will return null.
	 */
	public DiagramCanvasGrid getCanvasGrid() {
		return canvasGrid;
	}
	
	/**
	 * Set this {@link Canvas}'s {@link DiagramCanvasGrid}. If this
	 * {@link Canvas} already has a {@link DiagramCanvasGrid}, the given
	 * manager will override the old one.
	 * @return the old {@link DiagramCanvasGrid}, or null if this
	 * {@link Canvas} did not previously have one.
	 */
	public DiagramCanvasGrid setCanvasGrid(DiagramCanvasGrid g) {
		DiagramCanvasGrid old = canvasGrid;
		canvasGrid = g;
		if (canvasGrid != null)
			addCanvasListener(canvasGrid);
		else
			removeCanvasListener(old);
		return old;
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

	public VertexBuffer getVertexBuffer() {
		return vertexBuff;
	}
	
	public ProofCustomizationPanel getProofCustomizationPanel() {
		return parentPanel;
	}
}
