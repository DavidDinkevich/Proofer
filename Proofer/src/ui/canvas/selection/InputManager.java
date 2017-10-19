package ui.canvas.selection;

import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import ui.canvas.Brush;
import ui.canvas.Canvas;
import ui.canvas.DiagramCanvas;
import ui.canvas.Drawable;
import ui.canvas.GraphicsRectEllipse;
import ui.canvas.GraphicsShape;
import ui.canvas.GraphicsTriangle;
import ui.canvas.RenderList;
import ui.canvas.StyleManager;
import ui.canvas.event.CanvasAdapter;
import ui.swing.FigureRelationListPanel;
import ui.swing.FigureRelationPanel;
import ui.swing.ProofCustomizationPanel;

import geometry.Vec2;
import geometry.proofs.FigureRelation;
import geometry.proofs.FigureRelationType;
import geometry.shapes.Polygon;
import geometry.shapes.PolygonBuffer;
import geometry.shapes.Shape;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Controls and manages selections made on a {@link DiagramCanvas}.
 * @author David Dinkevich
 */
public class InputManager extends CanvasAdapter implements Drawable {
	public static final char NULL_KEY = '\0';
	public static char multipleSelectionKey = PConstants.SHIFT;
	
	private DiagramCanvas canvas;
	private RenderList renderList;
	private List<GraphicsShape<?>> selectables;
	private List<Selector<?, ?>> selectors;
	private List<Knob> knobs;
	private Knob selectedKnob;
	
	// Selection container
	
	private SelectionBox selectionContainer;
	private boolean displaySelectionContainer = false;
	
	// UIRelationMaker
	
	private UIRelationMaker relMaker;
	private boolean displayRelMaker;
	
	// Highlighting figures
	
	/**
	 * The highlighted figure
	 */
	private GraphicsShape<?> highlightedFig;

	public InputManager(DiagramCanvas canvas) {
		this.canvas = canvas;
		renderList = canvas.getRenderList();
		selectionContainer = new SelectionBox();
		relMaker = new UIRelationMaker();
		selectors = new ArrayList<>();
		knobs = new ArrayList<>();
		selectables = new ArrayList<>();
	}
	
	public boolean addSelectableFigure(GraphicsShape<?> shape) {
		// If the shape was successfully added
		if (selectables.add(shape)) {
			// If the shape is already selected
			if (shape.isSelected()) {
				// Create a selector for it
				createSelector(shape, true);
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
				System.out.println(selectors.size());
				// Get the selector
				Selector<?, ?> selForFigure = getSelectorForFigure(shape);
				// Destroy the selector
				destroySelector(selForFigure);
				System.out.println(selectors.size());
			}
			canvas.redraw();
			return true;
		}
		return false;
	}
	
	public List<GraphicsShape<?>> getSelectableFigures() {
		return Collections.unmodifiableList(selectables);
	}
	
	public List<Selector<?, ?>> getSelectors() {
		return selectors;
	}
	
	@Override
	public void draw(Canvas c) {
		// Draw the selection container onto the canvas without actually
		// adding the selection container as a graphics object.
		if (displaySelectionContainer) {
			selectionContainer.draw(c);
		}
		// Draw the UI relation maker onto the canvas without
		// adding it as a graphics object
		if (displayRelMaker)
			relMaker.draw(c);
	}
		
	@Override
	public void mousePressed(Canvas c, MouseEvent e) {
		if (canvas.mouseButton == PConstants.LEFT) {
			// Set the first corner of UI relation maker
			if (displayUIRelationMaker()) {
				relMaker.getShape().setVertexLoc(0, canvas.getMouseLocOnGrid(), true);
				return;
			}
			/*
			 * Check for selection among knobs
			 */
			for (Knob knob : knobs) {
				if (knob.getAllowSelections() && knob.getShape()
						.containsPoint(canvas.getMouseLocOnGrid(), true)) {
					selectedKnob = knob;
					return;
				}
			}
			
			selectedKnob = null; // The knob that may or may not have been clicked on
			// The figure that may or may not have been clicked on
			GraphicsShape<?> objectClickedOn = null;
			
			/*
			 * Check for selection among selectables
			 */
			for (GraphicsShape<?> o : selectables) {
				// If the object is selectable, and it was clicked on
				if (o.getAllowSelections() && o.getShape()
						.containsPoint(canvas.getMouseLocOnGrid(), true)) {
					objectClickedOn = o;
					break; // We already found the selected figure, no need to search further
				}
			}
			
			// If an object was clicked on
			if (objectClickedOn != null) {
				// Whether or not to add the newly selected figure to the list of
				// selected figures, or to deselect all other figures and select
				// only the newly selected one
				final boolean selectMultipleObjects = canvas.keyPressed &&
						canvas.keyCode == multipleSelectionKey;
				
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
			}
		}
	}
	
	@Override
	public void mouseDragged(Canvas c, MouseEvent e) {
		if (canvas.mouseButton == PConstants.LEFT) {
			// Expand UI relation maker
			if (displayUIRelationMaker()) {
				displayRelMaker = true;
				relMaker.getShape().setVertexLoc(1, c.getMouseLocOnGrid(), true);
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
				for (Selector<?, ?> sel : selectors) {
					Vec2 newSelLoc = dragSceneObject(sel.getShape().getCenter(true), false);
					sel.moveSelector(newSelLoc); // Don't snap to grid
					// If the shape of the selector's target object is a polygon,
					// fix its vertex names (we moved it)
					if (sel.getTargetObject().getShape() instanceof Polygon)
						fixVertexNames((Polygon)sel.getTargetObject().getShape());
				}
				canvas.redraw();
			}
		}
	}
	
	@Override
	public void mouseMoved(Canvas c, MouseEvent e) {
		highlightFigures();
	}
	
	@Override
	public void mouseReleased(Canvas c, MouseEvent e) {
		// Erase the selection container (if it exists)
		setDisplaySelectionContainer(false, true);
		// Erase UI relation maker
		setDisplayUIRelationMaker(false, true);
	}
	
	@Override
	public void keyPressed(Canvas c, KeyEvent e) {		
		if (canvas.keyCode != PConstants.CODED) {
			if (canvas.keyCode == PConstants.BACKSPACE) { // Don't have to check if key == CODED
				destroyAllSelectedObjects();
				canvas.redraw();
			}
			// If the user clicks space, snap selected object(s) to the grid
			else if (canvas.key == ' ') {
				for (Selector<?, ?> sel : selectors) {
					snapSelector(sel, true);
				}
			}
			// TODO: delete this
			else if (canvas.key == 'n') {
				Brush brush = StyleManager.getDefaultFigureBrush();

				Vertex[] tpoints = new Vertex[] {
						new Vertex(new Vec2(0f, -200.0f)),
						new Vertex(new Vec2(300f, 100f)),
						new Vertex(new Vec2(0f, 100f))
				};
				GraphicsTriangle tri = new GraphicsTriangle(brush,
						new Triangle(Arrays.asList(tpoints)));
				
				canvas.addDiagramFigure(tri);
				canvas.redraw();
			}
		}
	}
	
	/**
	 * Get the {@link Selector} for the given {@link GraphicsShape}
	 * @return the {@link Selector}, or null if it was not found
	 */
	private Selector<?, ?> getSelectorForFigure(GraphicsShape<?> shape) {
		for (Selector<?, ?> sel : selectors) {
			if (sel.getTargetObject().equals(shape))
				return sel;
		}
		return null;
	}
	
	/**
	 * Destroys all existing selectors. Selected objects are deselected.
	 */
	private void destroyAllSelectors() {
		// Remove knobs
		knobs.clear();
		// Deselect all selected objects
		for (int i = selectors.size()-1; i >= 0; i--) {
			Selector<?, ?> sel = selectors.get(i);
			// Deselect target object of selector
			sel.deselectTargetObject();
		}
		// Destroy all selectors in render list
		renderList.getLayerList(Selector.LAYER_NAME).clear();

		// Destroy all selectors
		selectors.clear();
	}
	
	private void destroySelector(Selector<?, ?> sel) {
		// Remove knobs
		knobs.removeAll(Arrays.asList(sel.getKnobs()));
		// Deselect target object of selector
		sel.deselectTargetObject();
		// Remove from render list
		renderList.remove(sel);
		// Remove from selectors list
		selectors.remove(sel);
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
	
	@SuppressWarnings("unchecked")
	private Selector<?, GraphicsShape<?>> createSelector(GraphicsShape<?> o, boolean redraw) {
		@SuppressWarnings("rawtypes")
		Selector sel = null;
		
		try {
			if (RectSelector.canSelect(o)) {
				sel = new RectSelector<>((GraphicsRectEllipse<?>)o);
			}
			else if (PolygonSelector.canSelect(o)) {
				sel = new PolygonSelector((GraphicsTriangle)o);
			}
		} catch (Exception e) {
			System.err.println("Error while trying to create a selector.");
			e.printStackTrace();
		}
		
		if (sel != null) {
			selectors.add(sel);
			sel.getShape().setResizeable(o.getShape().isResizeable());
			knobs.addAll(Arrays.asList(sel.getKnobs())); // Add to knobs list
			renderList.add(sel); // Add to render list
			
			if (redraw)
				canvas.redraw();
		}
		
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
			Vec2 snapPoint = canvas.getCanvasGrid().getPointOnGrid(dest);
			
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
		knob.moveKnob(dragSceneObject(knob.getShape().getCenter(true), true));
		
		// If the knob is not a PolygonSelectorKnob, we have no more business in this method.
		if (!(knob.getSelector().getTargetObject().getShape() instanceof Polygon))
			return;
		
		// Get the knob as a polygon knob
		PolygonSelectorKnob polyKnob = ((PolygonSelectorKnob) selectedKnob);
		
		// Get the name of the vertex that the knob moves		
		final char vertexName = polyKnob.getControlledVertex().getNameChar();					
		
		fixVertexName((Polygon)polyKnob.getSelector().getTargetObject().getShape(), vertexName);
	}
	
	private boolean fixVertexName(Polygon poly, char vertexName) {
		// If given polygon doesn't contain given vertex name, return error
		if (!poly.containsVertex(vertexName))
			return false;
		
		Vec2 vertexLoc = poly.getVertexLoc(vertexName, true);
		
		PolygonBuffer buff = canvas.getPolygonBuffer();
		
		// Check if the knob is snapped to the canvas's grid
		if (canvas.getCanvasGrid().pointIsSnapped(vertexLoc)) {
			// See if any other vertex shares the given vertex's location
			boolean otherVertSharesLoc = false;
			char otherVertName = '\n';
			outer:
			for (Polygon p : buff) {
				// For each OTHER vertex
				for (Vertex otherVert : p.getVertices()) {
					Vec2 otherVertLoc = otherVert.getCenter(true);
					otherVertName = otherVert.getNameChar();
					// If the vertices overlap and have different names
					if (otherVertName != vertexName && otherVertLoc.equals(vertexLoc)) {
						otherVertSharesLoc = true;
						break outer;
					}
				}
			}
			
			// If no other vertex shares given vertex's location
			if (!otherVertSharesLoc)
				return true; // Vertex safely moved
			
			// We found another vertex with the same loc as the given vertex
			buff.mergeVertices(poly, vertexName, otherVertName);
			return true;
		} else {
			buff.demergeVertices(poly, vertexName);
			return true;
		}
	}
	
	private void fixVertexNames(Polygon p) {
		for (int i = 0; i < p.getVertexCount(); i++) {
			fixVertexName(p, p.getVertexName(i));
		}
	}
	
	/**
	 * Snap the given {@link Selector} and its target object
	 * to the canvas grid.
	 * @param sel selector to snap
	 * @param redraw whether or not to redraw the canvas
	 */
	private void snapSelector(Selector<?, ?> sel, boolean redraw) {
		// Snap selector itself (if it's a polygon)
		if (sel.getShape() instanceof Polygon) {
			Polygon shape = (Polygon)sel.getShape();
			canvas.getCanvasGrid().snapToGrid(shape);
			fixVertexNames(shape);
			sel.updateKnobPositions();
		}
		// Snap target object of selector (if it's a polygon)
		if (sel.getTargetObject().getShape() instanceof Polygon) {
			Polygon targetObjectShape = (Polygon)sel.getTargetObject().getShape();
			canvas.getCanvasGrid().snapToGrid(targetObjectShape);
			fixVertexNames(targetObjectShape);
		}
		// Redraw if instructed to
		if (redraw)
			canvas.redraw();
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
		selectionContainer.setCorner2(loc); // Expand box
		// Check for objects covered by the selection container
		if (selectables.size() > 0) {
			for (GraphicsShape<?> selectable : selectables) {
				if (!selectable.getAllowSelections())
					continue;
				// If the object is covered by the box
				if (selectionContainer.coversObject(selectable.getShape(), true)) {
					// If the object is not selected
					if (!selectable.isSelected()) {
						// Select the object
						createSelector(selectable, true);
					}
				} else { // If the object is not covered by the box
					// If the object IS selected
					if (selectable.isSelected()) {
						// Deselect the object
						Selector<?, ?> sel = getSelectorForFigure(selectable);
						destroySelector(sel);
					}
				}
			}
		}
	}
	
	/**
	 * Handle the highlighting of figures when the mouse hovers
	 * over them.
	 */
	private void highlightFigures() {
		Vec2 mouseLoc = canvas.getMouseLocOnGrid();
		GraphicsShape<?> newHighlightedFig = null;
		
		/*
		 * Find the figure that the cursor hovers over.
		 */
		for (GraphicsShape<?> figure : selectables) {
			// If the cursor is hovering over this figure
			if (figure.getAllowSelections() && figure.getShape().containsPoint(mouseLoc, true)) {
				// Don't want to operate on the already highlighted figure if it is still
				// highlighted. NOTE: bc there is only ever one highlighted figure at a time,
				// we can just compare a figure's brush to see if it is the designated brush
				// for highlighted figures.
				// IF THE SAME FIGURE AS BEFORE HAS BEEN HIGHLIGHTED
				if (highlightedFig != null && figure.getBrush().equals(StyleManager
						.getHighlightedFigureBrush()))
					return;
				// A new figure has been highlighted
				newHighlightedFig = figure;
				break;
			}
		}
		
		// IF A NEW FIGURE IS HIGHLIGHTED
		if (newHighlightedFig != null) {
			restoreHighlightedFigure();
			
			// Update highlighted figure
			highlightedFig = newHighlightedFig;
			
			if (highlightedFig instanceof GraphicsTriangle) {
				highlightAnglesInPolygon((GraphicsTriangle)highlightedFig);
			}
			canvas.redraw();
		}
		// IF NO NEW FIGURE HAS BEEN HIGHLIGHTED, AND THE MOST RECENTLY HIGHLIGHTED
		// FIGURE IS NO LONGER HOVERED OVER BY THE MOUSE
		else {
			restoreHighlightedFigure();
		}
	}
	
	private void restoreHighlightedFigure() {
		if (highlightedFig != null) {
			highlightedFig = null;
			canvas.redraw();
		}
	}
		
	private void highlightAnglesInPolygon(GraphicsTriangle graphicsPoly) {
		graphicsPoly.unhighlightAllChildren(renderList);
		Vec2 mouse = canvas.getMouseLocOnGrid();
		graphicsPoly.highlightChildAtPoint(renderList, mouse);
	}
	
	private boolean displayUIRelationMaker() {
//		return canvas.keyPressed && canvas.key == PConstants.CODED && 
//				canvas.keyCode == PConstants.SHIFT;
		return false;
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
		/*
		 * If render = false (we want to make the UIRelationMaker disappear,
		 * we have to actually create a figure relation between the two figures
		 * on which the end-points of the UIRelationMaker lie.
		 */
		if (render == false) {
			// Get the end-points of the UIRelationMaker 
			List<Vec2> endpts = Arrays.asList(relMaker.getShape().getVertexLocations());
			// Num of diagram elements
			final int COUNT = canvas.getDiagramFigures().size();
			
			// For each figure
			for (int i = 0; i < COUNT; i++) {
				// Get the shape of the figure
				Shape shape0 = canvas.getDiagramFigures().get(i).getShape();
				// If the figure does NOT contain at least ONE of the UIRelationMaker's
				// end-points
				if (!shape0.containsAPointIn(endpts, true))
					continue;
				// For every figure
				for (int j = 0; j < COUNT; j++) {
					// Get the shape of the second figure
					Shape shape1 = canvas.getDiagramFigures().get(j).getShape();
					// If the second figure is the same as the first, OR if the second
					// figure does NOT contain at least ONE of the UIRelationMaker's
					// end-points
					if (i == j || !shape1.containsAPointIn(endpts, true))
						continue;
					// MAKE RELATION BETWEEN THE TWO FIGURES
					ProofCustomizationPanel proofPanel = canvas.getProofCustomizationPanel();
					// Get the FigureRelationListPanel
					FigureRelationListPanel listPanel = proofPanel.getFigureRelationListPanel();
					// Create the FigureRelation
					FigureRelation rel = new FigureRelation(
							FigureRelationType.CONGRUENT,
							shape0,
							shape1,
							null // Null parent?
					);
					// Add the FigureRelation
					listPanel.addFigureRelationPairPanel(new FigureRelationPanel(rel));
					return; // No more work to do
				}
			}
		}
	}
}
