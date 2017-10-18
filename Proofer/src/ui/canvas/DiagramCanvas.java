package ui.canvas;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import geometry.Dimension;
import geometry.Vec2;
import geometry.shapes.Polygon;
import geometry.shapes.PolygonBuffer;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;

import ui.canvas.selection.InputManager;
import ui.swing.ProofCustomizationPanel;

public class DiagramCanvas extends Canvas {
	private static final long serialVersionUID = -6415389681784791979L;
	
	private ProofCustomizationPanel parentPanel;
	private InputManager inputManager;
	private DiagramCanvasGrid canvasGrid;
	private PolygonBuffer polyBuff;
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
		setInputManager(inputManager = new InputManager(this));
		setCanvasGrid(canvasGrid = new DiagramCanvasGrid(this, new Dimension(50)));
		diagramFigures = new ArrayList<>();
		polyBuff = new PolygonBuffer();
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
		
		GraphicsTriangle tri2 = new GraphicsTriangle(brush, new Triangle(Arrays.asList(
				new Vertex[] {
						new Vertex(new Vec2(-10, 100)),
						new Vertex(new Vec2(-10, -200)),
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
		
		getRenderList().draw(this);
		
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
		getRenderList().add(shape);
		// If it's a polygon, add it to PolygonBuffer
		if (shape.getShape() instanceof Polygon) {
			polyBuff.addPoly((Polygon)shape.getShape());
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
			getRenderList().remove(shape);
			// If it's a polygon, remove from polygon buffer
			if (shape.getShape() instanceof Polygon) {
				polyBuff.removePoly((Polygon)shape.getShape());
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

	public PolygonBuffer getPolygonBuffer() {
		return polyBuff;
	}
	
	public ProofCustomizationPanel getProofCustomizationPanel() {
		return parentPanel;
	}
}
