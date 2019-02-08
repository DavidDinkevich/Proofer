package ui.canvas.diagram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.scene.paint.Color;

import geometry.Dimension;
import geometry.Vec2;
import geometry.proofs.Diagram;
import geometry.proofs.Figure;
import geometry.proofs.Preprocessor;
import geometry.shapes.Angle;
import geometry.shapes.Triangle;
import geometry.shapes.VertexBuffer;
import geometry.shapes.VertexBufferListener;
import geometry.shapes.VertexShape;

import ui.canvas.AdvancedCanvas;
import ui.canvas.Drawable;
import ui.canvas.GraphicsShape;
import ui.canvas.GraphicsTriangle;
import ui.canvas.GraphicsVertexBuffer;
import ui.canvas.StyleManager;
import ui.canvas.selection.InputManager;


public class DiagramCanvas extends AdvancedCanvas implements VertexBufferListener {
	
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
		vertexBuff.getListeners().add(this);
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
	
	@Override
	public void vertexNameChanged(char oldName, char newName) {
		System.out.println("Old name new name: " + oldName + ", " + newName);
		
		// Highlight or unhighlight invisible hidden figures
		updateInvisibleHiddenFigures();

	}
	
	/*
	 * BEGIN HIDDEN INVISIBLE FIGURES
	 */
	
	private void updateInvisibleHiddenFigures() {
		/*
		 * Update list of invisible hidden figures
		 */
		
		// Get list of all figures, included invisible hidden ones
		Diagram diag = Preprocessor.compileFigures(this);
		
		// Remove outdated figures
		removeOutdatedHiddenInvisibleFigures(diag);
		
		// Triangles
		for (Figure fig : diag.getFigures()) {
			// Make sure it's a triangle
			if (fig.getClass() != Triangle.class)
				continue;
			
			// Add the hidden invisible figure
			if (isHiddenInvisibleFigure(diag, fig.getName())) {
				// Get the invisible triangle, and make a GraphicsShape for it
				Triangle tri = (Triangle) fig;
				GraphicsTriangle gtri = new GraphicsTriangle(tri);
				gtri.setBrush(StyleManager.getInvisibleHiddenFigureBrush());
				gtri.setLayer(UIDiagramLayers.INVISIBLE_HIDDEN_FIGURES);
				renderList.addDrawable(gtri);
			} 
		}
	}
	
	private boolean isHiddenInvisibleFigure(Diagram diag, String name) {
		// If the figure is NOT contained in the Canvas, then it is a hidden figure
		// so we'll add it to the canvas (UNLESS it has been already added, in which
		// case we don't want duplicates). Finally, we don't want to add
		// "secondary triangles" (see isSecondaryTriangle() for description). Doing so
		// would result in ugly triangles being created on top of other ones.
		return !containsDiagramFigure(name, Triangle.class)
		&& !containsHiddenInvisibleFigure(name)
		&& !isSecondaryTriangle(diag, name);
	}
	
	private boolean containsHiddenInvisibleFigure(String name) {
		// Get list of invisible hidden figures
		List<Drawable> invisFigs = renderList.
				getLayerList(UIDiagramLayers.INVISIBLE_HIDDEN_FIGURES);

		for (Drawable drawable : invisFigs) {
			// We know it's a GraphicsShape because we're the ones who add all the
			// invisible hidden figures and we only add GraphicsShapes
			GraphicsShape<?> invisFigure = (GraphicsShape<?>) drawable;
			if (invisFigure.getShape().isValidName(name))
				return true;
		}
		
		return false;
	}
	
	private void removeOutdatedHiddenInvisibleFigures(Diagram diag) {
		// Get list of invisible hidden figures
		List<Drawable> invisFigs = renderList.
				getLayerList(UIDiagramLayers.INVISIBLE_HIDDEN_FIGURES);
		
		// Remove invisible hidden figures that may have existed in the
		// past but no longer exist. Such figures will exist within the
		// invisible hidden figures list but not in the diagram
		for (int i = invisFigs.size() - 1; i >= 0; i--) {
			// We know it's a GraphicsShape because we're the ones who add all the
			// invisible hidden figures and we only add GraphicsShapes
			GraphicsShape<?> invisFigure = (GraphicsShape<?>) invisFigs.get(i);
			// Get the type of the figure--cast is safe because we know the type
			// extends Figure
			@SuppressWarnings("unchecked")
			Class<? extends Figure> type = (Class<? extends Figure>) 
					invisFigure.getShape().getClass();
			
			if (!diag.containsFigure(invisFigure.getShape().getName(), type)) {
				// Remove from render list
				renderList.removeDrawable(invisFigure);
			}
		}
	}
	
	/**
	 * A "secondary triangle" in this context is simply a triangle that is composed
	 * of one or more angle synonyms.
	 * @param diag the diagram in which the triangle lies
	 * @param name the name of the triangle
	 * @return whether or not it is a "secondary triangle"
	 */
	private boolean isSecondaryTriangle(Diagram diag, String name) {
		Triangle tri = diag.getFigure(name, Triangle.class);
		if (tri == null) {
			return false;
		}
		
		for (Angle angle : tri.getAngles()) {
			if (diag.isSecondaryAngleSynonym(angle.getName()))
				return true;
		}
		return false;
	}
	
	/*
	 * END HIDDEN INVISIBLE FIGURES
	 */

	
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
