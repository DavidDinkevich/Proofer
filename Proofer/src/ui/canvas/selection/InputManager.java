package ui.canvas.selection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import geometry.Vec2;
import geometry.shapes.Segment;
import geometry.shapes.Shape;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;
import geometry.shapes.VertexShape;

import ui.canvas.AdvancedCanvas;
import ui.canvas.Brush;
import ui.canvas.GraphicsSegment;
import ui.canvas.GraphicsShape;
import ui.canvas.GraphicsTriangle;
import ui.canvas.StyleManager;
import ui.canvas.diagram.DiagramCanvas;
import ui.canvas.diagram.DiagramCanvasGrid;
import ui.canvas.diagram.RenderList;
import ui.canvas.diagram.DiagramCanvas.UIDiagramLayers;
import ui.canvas.selection.Selector.Knob;

import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;
import static javafx.scene.input.MouseEvent.MOUSE_DRAGGED;
import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;
import static javafx.scene.input.MouseEvent.MOUSE_MOVED;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;
import static javafx.scene.input.KeyEvent.KEY_RELEASED;


public class InputManager {
	
	// Ease of access
	
	private DiagramCanvas canvas;
	private DiagramCanvasGrid canvasGrid;
	private RenderList renderList;
	
	// Selection
	
	private List<GraphicsShape<?>> selectables;
	private List<Selector> selectors;
	private List<Knob> knobs;
	private Knob selectedKnob;
		
	public InputManager(DiagramCanvas canvas) {
		
		/*
		 * Ease of access
		 */
		this.canvas = canvas;
		renderList = canvas.getRenderList();
		canvasGrid = canvas.getCanvasGrid();
		
		/*
		 * Selection
		 */
		
		selectables = new ArrayList<>();
		selectors = new ArrayList<>();
		knobs = new ArrayList<>();
		// Null when no knob is selected
		selectedKnob = null;
		
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
	
	/*
	 * BEGIN EVENT HANDLING METHODS
	 */
	
	private void handleMousePressed(MouseEvent e) {
		// No right click
		if (e.isSecondaryButtonDown())
			return;
		
		// Set the first corner of UIRelationMaker
		if (canvas.keysAreDown(AdvancedCanvas.SHIFT) && canvas.displayUIRelationMaker()) {
			canvas.getUIRelationMaker().getShape().setVertexLoc(0, canvas.getMouseLocOnGrid());
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
			canvas.setDisplaySelectionContainer(false, true);
		}
		// If no objects were clicked on
		else {
			// No object clicked on, destroy all selectors
			destroyAllSelectors();

			// Set the first corner of the selection container to the mouse loc
			Vec2 mouseLoc = canvas.getMouseLocOnGrid();
			canvas.getSelectionContainer().setCorners(mouseLoc, mouseLoc);
			// Allow selection container box to be rendered to the canvas.
			canvas.setDisplaySelectionContainer(true, false);
		
			canvas.redraw();
		}		
	}
		
	private void handleMouseDragged(MouseEvent e) {
		// No right click
		if (e.isSecondaryButtonDown())
			return;
				
		// Expand UI relation maker
		if (canvas.displayUIRelationMaker()) {
			canvas.setDisplayUIRelationMaker(true);
			canvas.getUIRelationMaker().getShape().setVertexLoc(1, canvas.getMouseLocOnGrid());
			canvas.redraw();
			return;
		}
		
		// Expand/shrink selection container - ONLY IF it is being displayed (active)
		if (canvas.doDisplaySelectionContainer()) {
			canvas.expandSelectionContainer(canvas.getMouseLocOnGrid());
			canvas.redraw();
		}
		
		// Drag knob -- expand or shrink a figure
		else if (selectedKnob != null) {
			dragKnob(selectedKnob);
			// Update hidden vertices
			canvas.reloadHiddenVertices();
			canvas.redraw(); // Redraw the canvas
		}
		
		// Move all existing selectors. NOTE: moving selectors will move their
		// target objects as well
		else if (!selectors.isEmpty()) {
			for (Selector sel : selectors) {
				Vec2 newSelLoc = dragSceneObject(sel.getShape().getCenter(), false);
				sel.setSelectorLoc(newSelLoc); // Don't snap to grid
				// In case it gets snapped
				canvas.updateVertexNamesInPolygon(sel.getTarget().getShape());
				// Update hidden vertices
				canvas.reloadHiddenVertices();
			}
			canvas.redraw();
		}
	}
	
	private void handleMouseReleased(MouseEvent e) {
		// Erase the selection container (if it exists)
		canvas.setDisplaySelectionContainer(false, true);
		// Release UI relation maker
		canvas.releaseUIRelationMaker(true); // true = redraw canvas
	}
	
	private void handleMouseMoved(MouseEvent e) {
		canvas.highlightFigures();
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
			canvas.setDisplayUIRelationMaker(false, true);
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
				GraphicsTriangle gPoly = (GraphicsTriangle) shape;
				canvas.addPolygonChildren(gPoly);
			}
			
			// Update hidden vertices
			canvas.reloadHiddenVertices();
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
				canvas.removePolygonChildren((GraphicsTriangle) shape);
			}
			
			// Update hidden vertices
			canvas.reloadHiddenVertices();
			// Redraw the canvas
			canvas.redraw();
			return true;
		}
		return false;
	}
	
	public List<GraphicsShape<?>> getSelectableFigures() {
		return Collections.unmodifiableList(selectables);
	}
		
	public List<Selector> getSelectors() {
		return selectors;
	}
	
	/**
	 * Get the {@link Selector} for the given {@link GraphicsShape}
	 * @return the {@link Selector}, or null if it was not found
	 */
	public Selector getSelectorForFigure(Shape shape) {
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
	
	public void destroySelector(Selector sel) {
		// Remove knobs
		knobs.removeAll(Arrays.asList(sel.getKnobs()));
		// Deselect target object of selector
		sel.deselectTarget();
		// Remove from selectors list
		selectors.remove(sel);
		// Remove from render list
		renderList.removeDrawable(sel);
	}
			
	/**
	 * Destroy all selected objects and their selectors.
	 */
	public void destroyAllSelectedObjects() {
		for (int i = selectables.size()-1; i >= 0; i--) {
			if (selectables.get(i).isSelected()) {
				// Remove from diagram
				canvas.removeDiagramFigure(selectables.get(i));
			}
		}
		destroyAllSelectors();
	}
	
	public Selector createSelector(GraphicsShape<?> o, boolean redraw) {
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
			canvas.reloadPolygonChildren();
		}

		// Update the name of the vertex (in case it was snapped)
		Vertex controlledVert = knob.getControlledVertex();
		canvas.updateVertexName(controlledVert, true);
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
		canvas.updateVertexNamesInPolygon(sel.getTarget().getShape());
		
		// Update the name of the selector
//		sel.getShape().setName(sel.getTarget().getShape().getName());
		
		// Redraw if instructed to
		if (redraw)
			canvas.redraw();
	}
			
	/*
	 * END PRIVATE HELPER METHODS
	 */
	
}
