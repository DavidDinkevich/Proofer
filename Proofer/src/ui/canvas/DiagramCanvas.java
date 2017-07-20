package ui.canvas;

import java.util.Arrays;
import java.util.Collection;

import geometry.Dimension;
import geometry.Vec2;
import geometry.shapes.Polygon;
import geometry.shapes.PolygonBuffer;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;

import ui.canvas.selection.SelectionManager;

import util.IDList;

public class DiagramCanvas extends Canvas {
	private static final long serialVersionUID = -6415389681784791979L;
	
	private SelectionManager selectionManager;
	private DiagramCanvasGrid canvasGrid;
	private PolygonBuffer polyBuff;
	private IDList<GraphicsShape2D<?>> diagramElements;

	public DiagramCanvas(Dimension size, int background) {
		super(size, background);
		_init();
	}

	public DiagramCanvas(Dimension size) {
		super(size);
		_init();
	}

	public DiagramCanvas(int background) {
		super(background);
		_init();
	}
	
	private void _init() { // Underscore bc init() already exists in PApplet
		setSelectionManager(selectionManager = new SelectionManager(this));
		setCanvasGrid(canvasGrid = new DiagramCanvasGrid(this, new Dimension(50)));
		diagramElements = new IDList<>();
		polyBuff = new PolygonBuffer();
	}
	
	@Override
	public void setup() {
		super.setup();
		
		/*
		 * EVERYTHING BELLOW IS TEMPORARY, FOR TESTING PURPOSES.
		 * WILL BE DELETED
		 */
		
		Brush.Builder builder = new Brush.Builder().setFill(StyleManager.PINK).setStrokeWeight(2.5f)
				.setStroke(StyleManager.PINK).setAlpha(75);
		Brush brush = builder.buildBrush();

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
		
		addDiagramElements(Arrays.asList(tri, tri2));
	}
	
	@Override
	public void draw() {
		super.draw();
		
		// Draw canvas first
		if (canvasGrid != null)
			canvasGrid.draw(this);
		
		getRenderList().draw(this);
		
		if (selectionManager != null)
			selectionManager.draw(this);
	}
	
	/**
	 * Return this {@link Canvas}'s {@link SelectionManager}. If
	 * no selection manager has been assigned to this {@link Canvas},
	 * this will return null.
	 */
	public SelectionManager getSelectionManager() {
		return selectionManager;
	}
	
	/**
	 * Set this {@link Canvas}'s {@link SelectionManager}. If this
	 * {@link Canvas} already has a selection manager, the given
	 * manager will override the old one.
	 * @return the old {@link SelectionManager}, or null if this
	 * {@link Canvas} did not previously have one.
	 */
	public SelectionManager setSelectionManager(SelectionManager sel) {
		SelectionManager old = selectionManager;
		selectionManager = sel;
		if (selectionManager != null)
			addCanvasListener(selectionManager);
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
	
	public void addDiagramElement(GraphicsShape2D<?> shape) {
		diagramElements.addObject(shape);
		addGraphicsObject(shape);
		if (shape.getShape() instanceof Polygon) {
			polyBuff.addPoly((Polygon)shape.getShape());
		}
	}
	
	public void addDiagramElements(Collection<GraphicsShape2D<?>> elements) {
		for (GraphicsShape2D<?> shape : elements) {
			addDiagramElement(shape);
		}
	}
	
	public boolean removeDiagramElement(GraphicsShape2D<?> shape) {
		if (diagramElements.removeObject(shape)) {
			removeGraphicsObject(shape);
			if (shape.getShape() instanceof Polygon) {
				polyBuff.removePoly((Polygon)shape.getShape());
			}
			return true;
		}
		return false;
	}
	
	public void removeDiagramElements(Collection<GraphicsShape2D<?>> elements) {
		for (GraphicsShape2D<?> shape : elements) {
			removeDiagramElement(shape);
		}
	}
	
	public IDList<GraphicsShape2D<?>> getDiagramElements() {
		return diagramElements;
	}

	public PolygonBuffer getPolygonBuffer() {
		return polyBuff;
	}

	public void setPolygonBuffer(PolygonBuffer polyBuff) {
		this.polyBuff = polyBuff;
	}
}
