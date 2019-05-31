package ui.canvas.diagram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javafx.scene.paint.Color;

import geometry.Dimension;
import geometry.Vec2;
import geometry.proofs.Diagram;
import geometry.proofs.Figure;
import geometry.proofs.FigureRelation;
import geometry.proofs.Preprocessor;
import geometry.proofs.ProofReasons;
import geometry.shapes.Shape;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;
import geometry.shapes.VertexBuffer;
import geometry.shapes.VertexBufferListener;
import geometry.shapes.VertexShape;

import ui.FigureRelationListPanel;
import ui.FigureRelationPanel;
import ui.canvas.AdvancedCanvas;
import ui.canvas.Brush;
import ui.canvas.GraphicsPolygonChild;
import ui.canvas.GraphicsShape;
import ui.canvas.GraphicsTriangle;
import ui.canvas.GraphicsVertexBuffer;
import ui.canvas.StyleManager;
import ui.canvas.selection.InputManager;
import ui.canvas.selection.SelectionBox;
import ui.canvas.selection.Selector;
import ui.canvas.selection.UIRelationMaker;

import static geometry.proofs.FigureRelationType.CONGRUENT;


public class DiagramCanvas extends AdvancedCanvas implements VertexBufferListener {
	
	public enum UIDiagramLayers {
		DEFAULT, GRID, GRAPHICS_SHAPE, POLYGON_COMPONENT, SELECTOR, KNOB,
		INVISIBLE_HIDDEN_FIGURES, VERTICES
	}
	
	private List<GraphicsShape<?>> diagramFigures;
	
	private DiagramCanvasGrid canvasGrid;
	private RenderList renderList;
	private VertexBuffer vertexBuff;
	private InputManager inputManager;
	private FigureRelationListPanel figRelListPanel;
	
	// Selection container
	
	private SelectionBox selectionContainer;
	private boolean displaySelectionContainer;
	
	// UIRelationMaker
	
	private UIRelationMaker relMaker;
	private boolean displayRelMaker;
	
	// Highlighting polygon children
	
	private List<GraphicsPolygonChild<?>> polyChildren;
	
	// Rendering hidden vertices
	
	private List<Vertex> recentHiddenVertices;
	

	public DiagramCanvas(FigureRelationListPanel figRelListPanel, float w, float h) {
		super(w, h);
		
		canvasGrid = new DiagramCanvasGrid(this, new Dimension(50f));
		diagramFigures = new ArrayList<>();
		renderList = new RenderList();
		vertexBuff = new VertexBuffer();
		vertexBuff.getListeners().add(this);
		inputManager = new InputManager(this);
		this.figRelListPanel = figRelListPanel;
		
		/*
		 * RENDERING TOOLS
		 */
		
		displaySelectionContainer = false;
		selectionContainer = new SelectionBox();
		polyChildren = new ArrayList<>();
		recentHiddenVertices = new ArrayList<>();
		relMaker = new UIRelationMaker();
		
		
		// Add layers to render list
		for (UIDiagramLayers lay : UIDiagramLayers.values()) {
			renderList.addLayer(lay);
		}
		
		// Add a GraphicsVertexBuffer to render the VertexBuffer
		renderList.addDrawable(new GraphicsVertexBuffer(vertexBuff));
		
		// DESTROY SELECTORS WHEN FOCUS IS LOST
		getCanvas().focusedProperty().addListener(e -> {
			if (!getCanvas().isFocused()) {
				inputManager.destroyAllSelectors();
				redraw();
			}
		});
	}

	@Override
	public void redraw() {
		super.redraw();
		
		gc.setFill(Color.PINK);
		fillRect(Vec2.ZERO, Dimension.TEN);
				
		// Draw canvas first
		canvasGrid.draw(this);
		renderList.draw(this);		
		
		// Draw the selection container onto the canvas without actually
		// adding the selection container as a graphics object.
		// TODO: why???
		if (displaySelectionContainer) {
			selectionContainer.draw(this);
		}

		// Draw the UI relation maker onto the canvas without
		// adding it as a graphics object
		if (displayRelMaker)
			relMaker.draw(this);
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
		
		// Find/remove/update invisible hidden figures
		updateInvisibleHiddenFigures();
	}
	
	public void addDiagramFigures(Collection<GraphicsShape<?>> elements) {
		for (GraphicsShape<?> shape : elements) {
			addDiagramFigure(shape);
		}
	}
	
	public boolean removeDiagramFigure(GraphicsShape<?> shape) {
		// Remove from diagram figures list
		if (diagramFigures.remove(shape)) {
			// Find/remove/update invisible hidden figures
			updateInvisibleHiddenFigures();
			
			// Remove from RenderList
			renderList.removeDrawable(shape);
			// Remove VertexShapes from the VertexBuffer
			if (shape.getShape() instanceof VertexShape) {
				vertexBuff.removeVertexShape((VertexShape) shape.getShape());
			}
			
			// Remove from list of selectables
			if (inputManager != null) {
				inputManager.removeSelectableFigure(shape);
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
		// Highlight or unhighlight invisible hidden figures
//		updateInvisibleHiddenFigures();
	}
	
	/*
	 * BEGIN RENDERING METHODS
	 */
	
	private void updateInvisibleHiddenFigures() {
		/*
		 * Update list of invisible hidden figures
		 */
		
		// Get list of all figures, included invisible hidden ones
		Diagram snapshot = Preprocessor.compileFigures(this, Diagram.Policy.FIGURES_ONLY);
		
		// Remove outdated figures
		renderList.clearLayerList(UIDiagramLayers.INVISIBLE_HIDDEN_FIGURES);
		
		// Triangles
		for (Triangle fig : snapshot.getHiddenFigures(Triangle.class)) {
			
			// Add the hidden invisible figure
			if (isHiddenInvisibleFigure(snapshot, fig.getName())) {
				// Get a copy of the invisible triangle, and make a GraphicsShape for it
				Triangle tri = new Triangle((Triangle) fig);
				GraphicsTriangle gtri = new GraphicsTriangle(tri);
				gtri.setBrush(StyleManager.getInvisibleHiddenFigureBrush());
				gtri.setLayer(UIDiagramLayers.INVISIBLE_HIDDEN_FIGURES);
				renderList.addDrawable(gtri);
//				System.out.println("ADDED INVISIBLE HIDDEN TRI: " + tri);
//				System.out.println("Hidden triangles (diagram): " + diag.getHiddenFigures(Triangle.class));
//				System.out.println("Hidden triangles (render list): " + 
//						renderList.getLayerList(UIDiagramLayers.INVISIBLE_HIDDEN_FIGURES));
			} 
		}
	}
	
	/**
	 * Get whether the given figure is an invisible hidden figure
	 * NOTE: CURRENTLY THIS ONLY WORKS WITH TRIANGLES
	 */
	private boolean isHiddenInvisibleFigure(Diagram diag, String name) {
		return !containsDiagramFigure(name, Triangle.class);
	}
	
	public boolean displayUIRelationMaker() {
		// If no figures are selected AND the user is holding shift
//		KeyEvent e = canvas.getInputEvent(KEY_PRESSED);
		// If e is null, then no key has been pressed yet. Thus shift is not down.
//		return e == null ? false : selectors.isEmpty() && e.isShiftDown();
		return inputManager.getSelectors().isEmpty() && keysAreDown(AdvancedCanvas.SHIFT);
	}
	
	/**
	 * Set whether or not the {@link UIRelationMaker} is rendered to the
	 * canvas.
	 * @param render whether or not to render the {@link UIRelationMaker} box
	 * @param redraw whether or not to redraw the canvas
	 */
	public void setDisplayUIRelationMaker(boolean render, boolean redraw) {
		// If the new val is the same as the current val, there is nothing to do
		if (displayRelMaker == render) {
			return;
		}
		// Update
		displayRelMaker = render;
		// Stop displaying the selection box (both cannot coexist)
		setDisplaySelectionContainer(false, false);
		if (redraw)
			redraw();
	}
	
	/**
	 * "Release" the {@link UIRelationMaker}. This makes the {@link UIRelationMaker}
	 * disappear, creates a {@link RelationPair} between the two figures
	 * on which the end-points of the {@link UIRelationMaker} lie, and adds
	 * the {@link RelationPair} to the {@link Diagram}.
	 * @param redraw whether or not to redraw the canvas
	 */
	public void releaseUIRelationMaker(boolean redraw) {		
		/*
		 * After the UIRelationMaker is released, we set both end-points to [0, 0]
		 * (this will make the length of the UIRelationMaker = 0).
		 * Therefore, if the length of the UIRelationMaker is 0, then we can tell
		 * that the UIRelationMaker has NOT been dragged and cannot be released.
		 */
		if (relMaker.getShape().getLength() == 0f)
			return;
		
		// Get the end-points of the UIRelationMaker 
		List<Vec2> endpts = Arrays.asList(relMaker.getShape().getVertexLocations());

		// Compile a list of all of the figures in the diagram that can be
		// "connected"--a RelationPair can be made between them.
		// 	    connectables = polygon children  +  diagram figures
		// Polygon children are added first because they are "on top"
		List<GraphicsShape<?>> connectables = new ArrayList<>(polyChildren);
		connectables.addAll(getDiagramFigures());
		
		// There is a possibility that more than 2 figures will contain the
		// endpoints of the UIRelationMaker. We only want the first 2, and
		// will quit the loop after the first 2 are found.
		List<Shape> selectedShapes = new ArrayList<>(2);
				
		for (int i = 0; i < connectables.size(); i++) {
			Shape shape = connectables.get(i).getShape();
			// If the shape contains one of the endpoints
			if (shape.containsAPointIn(endpts)) {
				// Record the shape
				selectedShapes.add(shape);
			}
			// Only want first two shapes found
			if (selectedShapes.size() >= 2)
				break;
		}
		
		// Because we're "releasing" the UIRelationMaker, we want to make it
		// disappear
		setDisplayUIRelationMaker(false, redraw);
		
		// Set vertices to center
		for (Vertex v : relMaker.getShape().getVertices())
			v.setCenter(Vec2.ZERO);
		
		// Create the figure relation
		if (selectedShapes.size() == 2) {
			Figure f0 = selectedShapes.get(0);
			Figure f1 = selectedShapes.get(1);
			
			if (FigureRelation.isLegalRelation(CONGRUENT, f0, f1)) {
				FigureRelation rel = new FigureRelation(CONGRUENT, f0, f1);
				rel.setReason(ProofReasons.GIVEN);
				FigureRelationPanel panel = new FigureRelationPanel(
						FigureRelationPanel.Type.GIVEN, CONGRUENT, f0.toString(), f1.toString());
				figRelListPanel.addFigureRelationPanel(panel);
			}
		}

	}
	
	public UIRelationMaker getUIRelationMaker() {
		return relMaker;
	}
	
	/**
	 * Set whether or not the {@link SelectionBox} is rendered to the
	 * canvas.
	 * @param render whether or not to render the selection container box 
	 * @param redraw whether or not to redraw the canvas
	 */
	public void setDisplaySelectionContainer(boolean render, boolean redraw) {
		if (displaySelectionContainer != render) {
			displaySelectionContainer = render;
			if (redraw)
				redraw();
		}
	}
	
	public boolean doDisplaySelectionContainer() {
		return displaySelectionContainer;
	}
	
	public void expandSelectionContainer(Vec2 loc) {
		// Expand box
		selectionContainer.setCorner2(loc);
		// Check for objects covered by the selection container
		if (inputManager.getSelectableFigures().size() > 0) {
			for (GraphicsShape<?> selectable : inputManager.getSelectableFigures()) {
				if (!selectable.getAllowSelections())
					continue;
								
				// If the object is covered by the box
				if (selectionContainer.coversObject(selectable.getShape())) {
					// If the object is not selected
					if (!selectable.isSelected()) {
						// Select the object
						inputManager.createSelector(selectable, true);
					}
				} else { // If the object is not covered by the box
					// If the object IS selected
					if (selectable.isSelected()) {
						// Deselect the object
						Selector sel = inputManager.getSelectorForFigure(selectable.getShape());
						inputManager.destroySelector(sel);
					}
				}
			}
		}
	}
	
	public SelectionBox getSelectionContainer() {
		return selectionContainer;
	}
	
	/**
	 * A helper method for {@link VertexBuffer#updateVertexName(Vertex, boolean)}.
	 * @param vert the vertex to be updated
	 * @param whether or not to regenerate the polygon-children
	 */
	public void updateVertexName(Vertex vert, boolean reloadPolyChildren) {
		// Location of the vertex to be updated
		Vec2 vertLoc = vert.getCenter();
		
		// Whether to merge the vertex with another, or demerge it from another
		final boolean mergeVert = canvasGrid.pointIsSnapped(vertLoc);
		
		// Update the vertex in the VertexBuffer
		final boolean vertexModified = vertexBuff.updateVertexName(vert, mergeVert);
		
		// Reload hidden vertices
		reloadHiddenVertices();
		
		// Highlight or unhighlight invisible hidden figures
		updateInvisibleHiddenFigures();

		if (vertexModified && reloadPolyChildren) {
			// Update polygon children
			reloadPolygonChildren();
		}
	}
	
	public void updateVertexNamesInVertexShape(VertexShape p) {
		Vertex[] vertices = p.getVertices();
		
		for (int i = 0; i < p.getVertexCount(); i++) {
			// False bc no need to update polygon children after EACH vertex,
			// we can do it once at the very end
			updateVertexName(vertices[i], false);
		}
		// Update polygons
		reloadPolygonChildren();
	}
	
	/**
	 * Handle the highlighting of figures when the mouse hovers
	 * over them.
	 */
	public void highlightFigures() {
		// Mouse position
		Vec2 mouse = getMouseLocOnGrid();
								
		// For each graphics polygon child
		for (GraphicsPolygonChild<?> child : polyChildren) {
//			GraphicsPolygonAngle angleChild = (GraphicsPolygonAngle)child;
			// If the mouse is hovering over the child
			if (child.getShape().containsPoint(mouse)) {
//			if (angleChild.getArcShape().containsPoint(mouse)) {
				// If the child hovered over is not already in the render list
				if (!renderList.contains(child)) {
					// Add it to the render list
					renderList.addDrawable(child);
					// Redraw
					redraw();
				}
				// We found the child that is hovered over by the mouse.
				// Business is done here
				break;
			}
			// If the mouse is not hovering over this child
			else {
				// If the child is already in the render list
				if (renderList.contains(child)) {
					// Remove the child from the render list
					renderList.removeDrawable(child);
					// Redraw
					redraw();
				}
			}	
		}
	}
	
	/**
	 * Updates all hidden vertices in the {@link VertexBuffer}.
	 */
	public void reloadHiddenVertices() {
		// Clear all previous hidden vertices from VertexBuffer
		vertexBuff.removeVertices(recentHiddenVertices);
		// Clear all previous hidden vertices from buffer
		recentHiddenVertices.clear();
		
		// Create a snapshot Diagram of this canvas at this point in time
		Diagram snapshot = Preprocessor.compileFigures(this, Diagram.Policy.FIGURES_ONLY);
		// Get the hidden vertices
		List<Vertex> newHiddenVerts = snapshot.getHiddenFigures(Vertex.class);
		
		// Add the hidden vertices to the VertexBuffer
		vertexBuff.addVertices(newHiddenVerts);
		// Add the hidden vertices to the buffer
		recentHiddenVertices.addAll(newHiddenVerts);
	}
	
	public void addPolygonChildren(GraphicsTriangle poly) {
		// Create a GraphicsPolygonChild for each child and add it
		for (Figure child : poly.getShape().getChildren()) {
			// Get the brush for the GraphicsPolygonChild
			Brush gChildBrush = StyleManager.getHighlightedFigureBrush();			
			// Ask the graphics polygon for the graphics child for the child's name
			GraphicsPolygonChild<?> gChild = poly.getGraphicsChild(child.getName());
			// Safety
			if (gChild == null)
				continue;
			// Set brush
			gChild.setBrush(gChildBrush);
			// Add the graphics child
			polyChildren.add(gChild);
		}
	}
	
	public void removePolygonChildren(GraphicsTriangle poly) {
		// For each of the polygon's children
		for (Figure child : poly.getShape().getChildren()) {
			// For each element in the polygon-children list
			for (int i = polyChildren.size()-1; i >= 0; i--) {
				GraphicsPolygonChild<?> polyChild = polyChildren.get(i);
				// If the current element in polygon-children list is one of
				// the polygon's children
				if (polyChild.getShape().equals(child)) {
					// Remove it from the polygon-children list
					polyChildren.remove(i);
					// Remove it from the RenderList (it may not currently, be in the
					// RenderList, but this is just to make sure
					renderList.removeDrawable(polyChild);
				}
			}
		}
	}
	
	/**
	 * It is essential to use this after modifying the name of a
	 * {@link GraphicsPolygon}.
	 */
	public void reloadPolygonChildren() {
		// Clear the whole list in the RenderList
		renderList.clearLayerList(UIDiagramLayers.POLYGON_COMPONENT);
		// Delete the currently existing polygon children
		polyChildren.clear();

		// For each GraphicsPolygon
		for (GraphicsShape<?> gShape : inputManager.getSelectableFigures()) {
			if (gShape instanceof GraphicsTriangle) {
				GraphicsTriangle gPoly = (GraphicsTriangle)gShape;
				// Add its new polygon children
				addPolygonChildren(gPoly);
			}
		}
	}
	
	/*
	 * END RENDERING METHODS
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
