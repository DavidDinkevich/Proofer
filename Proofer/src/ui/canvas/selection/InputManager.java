package ui.canvas.selection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import geometry.Vec2;
import geometry.proofs.Figure;
import geometry.proofs.Preprocessor;
import geometry.shapes.Angle;
import geometry.shapes.Segment;
import geometry.shapes.Shape;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;
import geometry.shapes.VertexBuffer;
import geometry.shapes.VertexShape;
import geometry.proofs.Diagram;

import ui.canvas.AdvancedCanvas;
import ui.canvas.Brush;
import ui.canvas.Drawable;
import ui.canvas.GraphicsPolygonChild;
import ui.canvas.GraphicsSegment;
import ui.canvas.GraphicsShape;
import ui.canvas.GraphicsTriangle;
import ui.canvas.StyleManager;
import ui.canvas.diagram.DiagramCanvas;
import ui.canvas.diagram.DiagramCanvasGrid;
import ui.canvas.diagram.RenderList;
import ui.canvas.diagram.UIDiagramLayers;
import ui.canvas.selection.Selector.Knob;

import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;
import static javafx.scene.input.MouseEvent.MOUSE_DRAGGED;
import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;
import static javafx.scene.input.MouseEvent.MOUSE_MOVED;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;
import static javafx.scene.input.KeyEvent.KEY_RELEASED;


public class InputManager implements Drawable {
	
	// Ease of access
	
	private DiagramCanvas canvas;
	private DiagramCanvasGrid canvasGrid;
	private RenderList renderList;
	private VertexBuffer vertexBuff;
	
	// Selection
	
	private List<GraphicsShape<?>> selectables;
	private List<Selector> selectors;
	private List<Knob> knobs;
	private Knob selectedKnob;
	
	// Selection container
	
	private SelectionBox selectionContainer;
	private boolean displaySelectionContainer;
	
	// UIRelationMaker
	
	private UIRelationMaker relMaker;
	private boolean displayRelMaker;
	
	// Highlighting polygon children
	
	private List<GraphicsPolygonChild<?>> polyChildren;

	
	public InputManager(DiagramCanvas canvas) {
		
		/*
		 * Ease of access
		 */
		this.canvas = canvas;
		renderList = canvas.getRenderList();
		canvasGrid = canvas.getCanvasGrid();
		vertexBuff = canvas.getVertexBuffer();
		
		/*
		 * Selection
		 */
		
		selectables = new ArrayList<>();
		selectors = new ArrayList<>();
		knobs = new ArrayList<>();
		// Null when no knob is selected
		selectedKnob = null;
		displaySelectionContainer = false;
		selectionContainer = new SelectionBox();
		
		/*
		 * Highlighting Polygon Children
		 */
		
		polyChildren = new ArrayList<>();
		
		/*
		 * UIRelationMaker
		 */
		
		relMaker = new UIRelationMaker();
		
		/*
		 * EVENTS
		 */
		
		canvas.getCanvas().addEventFilter(MOUSE_PRESSED, e -> {
			handleMousePressed(e);
		});
		
		canvas.getCanvas().addEventFilter(MOUSE_DRAGGED, e -> {
			handleMouseDragged(e);
		});
		
		canvas.getCanvas().addEventFilter(MOUSE_RELEASED, e -> {
			handleMouseReleased(e);
		});
		
		canvas.getCanvas().addEventFilter(MOUSE_MOVED, e -> {
			handleMouseMoved(e);
		});

		canvas.getCanvas().addEventFilter(KEY_PRESSED, e -> {
			handleKeyPressed(e);
		});
		
		canvas.getCanvas().addEventFilter(KEY_RELEASED, e -> {
			handleKeyReleased(e);
		});
	}
	
	@Override
	public void draw(AdvancedCanvas c) {
		// Draw the selection container onto the canvas without actually
		// adding the selection container as a graphics object.
		// TODO: why???
		if (displaySelectionContainer) {
			selectionContainer.draw(c);
		}

		// Draw the UI relation maker onto the canvas without
		// adding it as a graphics object
		if (displayRelMaker)
			relMaker.draw(c);
	}
	
	/*
	 * BEGIN EVENT HANDLING METHODS
	 */
	
	private void handleMousePressed(MouseEvent e) {
		// No right click
		if (e.isSecondaryButtonDown())
			return;
		
		// Set the first corner of UIRelationMaker
		if (canvas.keysAreDown(AdvancedCanvas.SHIFT) && displayUIRelationMaker()) {
			relMaker.getShape().setVertexLoc(0, canvas.getMouseLocOnGrid());
			return;
		}
			
		/*
		 * Check for selection among knobs
		 */
		for (Knob knob : knobs) {
			if (knob.getAllowSelections() && knob.getShape()
					.containsPoint(canvas.getMouseLocOnGrid())) {
				selectedKnob = knob;
				return;
			}
		}
		
		selectedKnob = null; // No knob was selected
		// The figure that may or may not have been clicked on
		GraphicsShape<?> objectClickedOn = null;		
		
		/*
		 * Check for selection among selectables. Loop backwards
		 * because we want to check the figures on the TOP first.
		 */
		for (int i = selectables.size()-1; i >= 0; i--) {
			GraphicsShape<?> o = selectables.get(i);
			// If the object is selectable, and it was clicked on
			if (o.getAllowSelections() && o.getShape()
					.containsPoint(canvas.getMouseLocOnGrid())) {
				objectClickedOn = o;
				break; // We already found the selected figure, no need to search further
			}
		}
		
		// If an object was clicked on
		if (objectClickedOn != null) {
			// Whether or not to add the newly selected figure to the list of
			// selected figures, or to deselect all other figures and select
			// only the newly selected one
			final boolean selectMultipleObjects = e.isShiftDown();
			
			// If the figure clicked on is NOT already selected
			if (!objectClickedOn.isSelected()) {
				// If the user does not hold the multiple selection key, destroy all selectors
				if (!selectMultipleObjects) {
					// Deselect all previously selected objects
					destroyAllSelectors();
				}
				
				// Create selector for object and select it - ONLY IF they are not
				// already selected. Don't want duplicate selectors.
				createSelector(objectClickedOn, true);
			}
			
			// Disallow selection container box to be rendered to the canvas.
			setDisplaySelectionContainer(false, true);
		}
		// If no objects were clicked on
		else {
			// No object clicked on, destroy all selectors
			destroyAllSelectors();

			// Set the first corner of the selection container to the mouse loc
			Vec2 mouseLoc = canvas.getMouseLocOnGrid();
			selectionContainer.setCorners(mouseLoc, mouseLoc);
			// Allow selection container box to be rendered to the canvas.
			setDisplaySelectionContainer(true, false);
		
			canvas.redraw();
		}		
	}
	
	private void handleMouseDragged(MouseEvent e) {
		// No right click
		if (e.isSecondaryButtonDown())
			return;
				
		// Expand UI relation maker
		if (displayUIRelationMaker()) {
			displayRelMaker = true;
			relMaker.getShape().setVertexLoc(1, canvas.getMouseLocOnGrid());
			canvas.redraw();
			return;
		}
		
		// Expand/shrink selection container - ONLY IF it is being displayed (active)
		if (displaySelectionContainer) {
			expandSelectionContainer(canvas.getMouseLocOnGrid());
			canvas.redraw();
		}
		
		// Drag knob -- expand or shrink a figure
		else if (selectedKnob != null) {
			dragKnob(selectedKnob);
			canvas.redraw(); // Redraw the canvas
		}
		
		// Move all existing selectors. NOTE: moving selectors will move their
		// target objects as well
		else if (!selectors.isEmpty()) {
			for (Selector sel : selectors) {
				Vec2 newSelLoc = dragSceneObject(sel.getShape().getCenter(), false);
				sel.setSelectorLoc(newSelLoc); // Don't snap to grid
				updateVertexNamesInPolygon(sel.getTarget().getShape());
			}
			canvas.redraw();
		}
	}
	
	private void handleMouseReleased(MouseEvent e) {
		// Erase the selection container (if it exists)
		setDisplaySelectionContainer(false, true);
		// Release UI relation maker
		releaseUIRelationMaker(true); // true = redraw canvas
	}
	
	private void handleMouseMoved(MouseEvent e) {
		highlightFigures();
	}
	
	private void handleKeyPressed(KeyEvent e) {
		if (e.getCode().equals(KeyCode.BACK_SPACE)) {
			destroyAllSelectedObjects();
			canvas.redraw();
		}
		
		// If the user clicks space, snap selected object(s) to the grid
		else if (e.getCode().equals(KeyCode.SPACE)) {
			for (Selector sel : selectors) {
				snapSelector(sel, true);
			}
		}

		else if (e.getCode().equals(KeyCode.N)) {
			/*
			 * EVERYTHING BELLOW IS TEMPORARY, FOR TESTING PURPOSES.
			 * WILL BE DELETED
			 */
			
			Brush brush = StyleManager.getDefaultFigureBrush();
			
			final int SIDE = 100;

			Vertex[] tpoints = new Vertex[] {
					new Vertex(new Vec2(0f, SIDE)),
					new Vertex(new Vec2(SIDE*2, -SIDE)),
					new Vertex(new Vec2(0f, -SIDE))
			};
			GraphicsTriangle tri = new GraphicsTriangle(brush, 
					new Triangle(Arrays.asList(tpoints)));
//			tri.setAllowSelection(false);
			tri.setSelected(true);
			canvas.addDiagramFigure(tri);
			
//			GraphicsTriangle tri2 = new GraphicsTriangle(brush, new Triangle(Arrays.asList(
//					new Vertex[] {
//							new Vertex(new Vec2(-10f, -SIDE)),
//							new Vertex(new Vec2(-10f, SIDE)),
//							new Vertex(new Vec2(-SIDE*2, -SIDE))
//						}
//			)));
////			tri2.setAllowSelection(false);
			
//			addDiagramFigures(Arrays.asList(tri, tri2));
			

			canvas.redraw();
		}
		
		else if (e.getCode().equals(KeyCode.M)) {
			Brush brush = StyleManager.getStrokeFigureBrush();
			Segment seg = new Segment(new Vertex(Vec2.ZERO), new Vertex(new Vec2(100, 100)));
			seg.setName("AB");
			GraphicsSegment gseg = new GraphicsSegment(brush, seg);
			gseg.setSelected(true);
			canvas.addDiagramFigure(gseg);
			canvas.redraw();
		}
		
	}
	
	private void handleKeyReleased(KeyEvent e) {
		if (e.isShiftDown()) {
			// Erase UI relation maker
			setDisplayUIRelationMaker(false, true);
		}
	}

	/*
	 * END EVENT HANDLING METHODS
	 */

	/*
	 * BEGIN PRIVATE HELPER METHODS
	 */
	
	public boolean addSelectableFigure(GraphicsShape<?> shape) {
		// If the shape was successfully added
		if (selectables.add(shape)) {
			// If the shape is already selected
			if (shape.isSelected()) {
				// Create a selector for it
				createSelector(shape, true);
			}
			// If the shape is a polygon, add its children to the
			// list of selectable figures
			if (shape instanceof GraphicsTriangle) { // TODO: change to polygon
				// We know it's a GraphicsTriangle
				GraphicsTriangle gPoly = (GraphicsTriangle)shape;
				addPolygonChildren(gPoly);
			}
			return true;
		}
		return false;
	}
	
	public boolean removeSelectableFigure(GraphicsShape<?> shape) {
		// If successfully removed figure
		if (selectables.remove(shape)) {
			// If the figure is selected at the time of removal
			if (shape.isSelected()) {
				// Get the selector
				Selector selForFigure = getSelectorForFigure(shape.getShape());
				// Destroy the selector
				destroySelector(selForFigure);
			}
			// If the figure is a polygon, remove its children
			if (shape instanceof GraphicsTriangle) { // TODO: change this to polygon
				removePolygonChildren((GraphicsTriangle) shape);
			}
			
			canvas.redraw();
			return true;
		}
		return false;
	}
	
	public List<GraphicsShape<?>> getSelectableFigures() {
		return Collections.unmodifiableList(selectables);
	}
	
	private void addPolygonChildren(GraphicsTriangle poly) {
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
	
	private void removePolygonChildren(GraphicsTriangle poly) {
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
	private void reloadPolygonChildren() {
		// Clear the whole list in the RenderList
		renderList.clearLayerList(UIDiagramLayers.POLYGON_COMPONENT);
		// Delete the currently existing polygon children
		polyChildren.clear();

		// For each GraphicsPolygon
		for (GraphicsShape<?> gShape : selectables) {
			if (gShape instanceof GraphicsTriangle) {
				GraphicsTriangle gPoly = (GraphicsTriangle)gShape;
				// Add its new polygon children
				addPolygonChildren(gPoly);
			}
		}
	}
	
	public List<Selector> getSelectors() {
		return selectors;
	}
	
	/**
	 * Get the {@link Selector} for the given {@link GraphicsShape}
	 * @return the {@link Selector}, or null if it was not found
	 */
	private Selector getSelectorForFigure(Shape shape) {
		for (Selector sel : selectors) {
			if (sel.getTarget().getShape().equals(shape)) {
				return sel;
			}
		}
		return null;
	}

	/**
	 * Destroys all existing selectors. Selected objects are deselected.
	 */
	private void destroyAllSelectors() {
		// Deselect all selected objects
		for (int i = selectors.size()-1; i >= 0; i--) {
			Selector sel = selectors.get(i);
			// Deselect target object of selector
			sel.deselectTarget();
		}
		
		// Remove knobs
		knobs.clear();
		// Destroy all selectors in render list
		renderList.clearLayerList(UIDiagramLayers.SELECTOR);
		// Destroy all selectors
		selectors.clear();				
	}
	
	private void destroySelector(Selector sel) {
		// Deselect target object of selector
		sel.deselectTarget();
		// Remove knobs
		knobs.removeAll(Arrays.asList(sel.getKnobs()));
		// Remove from selectors list
		selectors.remove(sel);
		// Remove from render list
		renderList.removeDrawable(sel);
	}
		
	/**
	 * Destroy all selected objects and their selectors.
	 */
	private void destroyAllSelectedObjects() {
		for (int i = selectables.size()-1; i >= 0; i--) {
			if (selectables.get(i).isSelected()) {
				// Remove from diagram
				canvas.removeDiagramFigure(selectables.get(i));
			}
		}
		destroyAllSelectors();
	}
	
	private Selector createSelector(GraphicsShape<?> o, boolean redraw) {
		// TODO: dangerous cast
		@SuppressWarnings("unchecked")
		Selector sel = new Selector((GraphicsShape<? extends VertexShape>) o);
		selectors.add(sel);
		knobs.addAll(Arrays.asList(sel.getKnobs())); // Add to knobs list
		renderList.addDrawable(sel); // Add to render list
		
		if (redraw)
			canvas.redraw();
		
		return sel;
	}
	
	private boolean displayUIRelationMaker() {
		// If no figures are selected AND the user is holding shift
//		KeyEvent e = canvas.getInputEvent(KEY_PRESSED);
		// If e is null, then no key has been pressed yet. Thus shift is not down.
//		return e == null ? false : selectors.isEmpty() && e.isShiftDown();
		return selectors.isEmpty() && canvas.keysAreDown(AdvancedCanvas.SHIFT);
	}
	
	/**
	 * Set whether or not the {@link UIRelationMaker} is rendered to the
	 * canvas.
	 * @param render whether or not to render the {@link UIRelationMaker} box
	 * @param redraw whether or not to redraw the canvas
	 */
	private void setDisplayUIRelationMaker(boolean render, boolean redraw) {
		// If the new val is the same as the current val, there is nothing to do
		if (displayRelMaker == render) {
			return;
		}
		// Update
		displayRelMaker = render;
		if (redraw)
			canvas.redraw();
	}
	
	/**
	 * "Release" the {@link UIRelationMaker}. This makes the {@link UIRelationMaker}
	 * disappear, creates a {@link RelationPair} between the two figures
	 * on which the end-points of the {@link UIRelationMaker} lie, and adds
	 * the {@link RelationPair} to the {@link Diagram}.
	 * @param redraw whether or not to redraw the canvas
	 */
	private void releaseUIRelationMaker(boolean redraw) {		
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
		connectables.addAll(canvas.getDiagramFigures());
		
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
	}
	
	/**
	 * Set whether or not the {@link SelectionBox} is rendered to the
	 * canvas.
	 * @param render whether or not to render the selection container box 
	 * @param redraw whether or not to redraw the canvas
	 */
	private void setDisplaySelectionContainer(boolean render, boolean redraw) {
		if (displaySelectionContainer != render) {
			displaySelectionContainer = render;
			if (redraw)
				canvas.redraw();
		}
	}
	
	private void expandSelectionContainer(Vec2 loc) {
		// Expand box
		selectionContainer.setCorner2(loc);
		// Check for objects covered by the selection container
		if (selectables.size() > 0) {
			for (GraphicsShape<?> selectable : selectables) {
				if (!selectable.getAllowSelections())
					continue;
				// If the object is covered by the box
				if (selectionContainer.coversObject(selectable.getShape())) {
					// If the object is not selected
					if (!selectable.isSelected()) {
						// Select the object
						createSelector(selectable, true);
					}
				} else { // If the object is not covered by the box
					// If the object IS selected
					if (selectable.isSelected()) {
						System.out.println("Selectors: " + selectors);
						// Deselect the object
						Selector sel = getSelectorForFigure(selectable.getShape());
						destroySelector(sel);
					}
				}
			}
		}
	}
	
	/**
	 * Calculates the center location of the given object after being dragged
	 * from one point to another.
	 * @param objectCenter the center location of the object being dragged
	 * @return the center location of the given object after being dragged
	 */
	private Vec2 dragSceneObject(Vec2 objectCenter, boolean snapToGrid) {
		Vec2 from = canvas.getOldMouseLocOnGrid();
		Vec2 to = canvas.getMouseLocOnGrid();
		
		Vec2 offset = Vec2.sub(from, objectCenter);
		Vec2 dest = Vec2.sub(to, offset);
		
		if (snapToGrid) {
			Vec2 snapPoint = canvasGrid.getPointOnGrid(dest);
			
			// If the object is being moved towards the snap point,
			// snap the object
			// For some reason, the -1 smoothens process on macs.
			if (Vec2.dist(snapPoint, to) - 1f < Vec2.dist(snapPoint, from)) {
				return snapPoint;
			}
			// If the object is moving away from the snap point,
			// don't snap it.
			return to;
		}
		
		return dest;
	}
	
	private void dragKnob(Knob knob) {		
		// Drag knob
		knob.moveKnob(dragSceneObject(knob.getShape().getCenter(), true));
		
		// If the knob is snapped to the grid, we have to refresh the polygon 
		// children (because there is a possibility that a polygon's name was 
		// modified)
		Vec2 knobLoc = knob.getShape().getCenter();
		if (canvasGrid.pointIsSnapped(knobLoc)) {
			// Reload the polygon children
			reloadPolygonChildren();
		}
		
		final char controlledVertName = knob.getControlledVertex();
		Vertex controlledVert = new Vertex(controlledVertName, knobLoc);
		updateVertexName(controlledVert, true);
	}
	
	/**
	 * A helper method for {@link VertexBuffer#updateVertexName(Vertex, boolean)}.
	 * @param vert the vertex to be updated
	 * @param whether or not to regenerate the polygon-children
	 */
	private void updateVertexName(Vertex vert, boolean reloadPolyChildren) {
		// Location of the vertex to be updated
		Vec2 vertLoc = vert.getCenter();
		
		// Whether to merge the vertex with another, or demerge it from another
		final boolean mergeVert = canvasGrid.pointIsSnapped(vertLoc);
		
		// Update the vertex in the VertexBuffer
		final boolean vertexModified = vertexBuff.updateVertexName(vert, mergeVert);
		
		if (vertexModified) {
			// Update polygon children
			reloadPolygonChildren();
			// Highlight or unhighlight invisible hidden figures
			updateInvisibleHiddenFigures();
		}
	}
	
	private void updateVertexNamesInPolygon(VertexShape p) {
		Vertex[] vertices = p.getVertices();
		
		for (int i = 0; i < p.getVertexCount(); i++) {
			updateVertexName(vertices[i], false);
		}
		// Update polygons
		reloadPolygonChildren();
	}
	
	/**
	 * Snap the given {@link Selector} and its target object
	 * to the canvas grid.
	 * @param sel selector to snap
	 * @param redraw whether or not to redraw the canvas
	 */
	private void snapSelector(Selector sel, boolean redraw) {
		for (Knob knob : sel.getKnobs()) {
			// Get the location of the knob
			Vec2 knobLoc = knob.getShape().getCenter();
			// Get the nearest snap point of the knob
			Vec2 nearestSnap = canvasGrid.getNearestSnapPoint(knobLoc);
			// Move the knob
			knob.moveKnob(nearestSnap);
		}
		// Update the name of the target polygon figure
		updateVertexNamesInPolygon(sel.getTarget().getShape());
		
		// Update the name of the selector
//		sel.getShape().setName(sel.getTarget().getShape().getName());
		
		// Redraw if instructed to
		if (redraw)
			canvas.redraw();
	}
	
	private void updateInvisibleHiddenFigures() {
		/*
		 * Update list of invisible hidden figures
		 */
		
		// Get list of all figures, included invisible hidden ones
		Diagram diag = Preprocessor.compileFigures(canvas);
		
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
		return !canvas.containsDiagramFigure(name, Triangle.class)
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
		
	/**
	 * Handle the highlighting of figures when the mouse hovers
	 * over them.
	 */
	private void highlightFigures() {
		// Mouse position
		Vec2 mouse = canvas.getMouseLocOnGrid();
								
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
					canvas.redraw();
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
					canvas.redraw();
				}
			}	
		}
	}
	
	/*
	 * END PRIVATE HELPER METHODS
	 */
	
	
}
