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

import geometry.Vec2;
import geometry.shapes.Polygon;
import geometry.shapes.PolygonBuffer;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;

import java.util.Arrays;

import util.IDList;

/**
 * Controls and manages selections made on a {@link DiagramCanvas}.
 * @author David Dinkevich
 */
public class InputManager extends CanvasAdapter implements Drawable {
	public static final char NULL_KEY = '\0';
	public static char multipleSelectionKey = PConstants.SHIFT;
	
	private DiagramCanvas canvas;
	private RenderList renderList;
	private IDList<GraphicsShape<?>> selectables;
	private IDList<Selector<?, ?>> selectors;
	private IDList<Knob> knobs;
	
	// Selection of objects
	
	private SelectionBox selectionContainer;
	private boolean displaySelectionContainer = false;
	private Knob selectedKnob;
	
	// Highlighting figures
	
	/**
	 * The brush that belonged to the highlighted figure
	 * before it was highlighted
	 */
	private Brush origHighlightedFigBrush;
	/**
	 * The highlighted figure
	 */
	private GraphicsShape<?> highlightedFig;

	public InputManager(DiagramCanvas canvas) {
		this.canvas = canvas;
		renderList = canvas.getRenderList();
		selectionContainer = new SelectionBox();
		selectors = new IDList<>();
		knobs = new IDList<>();
		selectables = new IDList<>();
	}
	
	@Override
	public void draw(Canvas c) {
		// Draw the selection manager onto the canvas without actually
		// adding the selection container as a graphics object.
		if (displaySelectionContainer) {
			selectionContainer.draw(c);
		}
	}
		
	@Override
	public void mousePressed(Canvas c, MouseEvent e) {
		if (canvas.mouseButton == PConstants.LEFT) {
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
			else if (selectors.count() > 0) {
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
				
				canvas.addDiagramElement(tri);
				canvas.redraw();
			}
		}
	}
	
	@Override
	public void graphicsObjectAdded(Canvas c, GraphicsShape<?> o) {
		if (o instanceof GraphicsShape) {
			selectables.addObject((GraphicsShape<?>)o);
		}
	}
	
	@Override
	public void graphicsObjectRemoved(Canvas c, GraphicsShape<?> o) {
		selectables.removeObject((GraphicsShape<?>) o);
	}
	
	/**
	 * Destroys all existing selectors. Selected objects are deselected.
	 */
	private void destroyAllSelectors() {
		// Deselect all selected objects
		for (int i = selectors.count()-1; i >= 0; i--) {
			Selector<?, ?> sel = selectors.get(i);
			// Remove knobs
			knobs.removeObjects(sel.getKnobs());
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
		knobs.removeObjects(sel.getKnobs());
		// Deselect target object of selector
		sel.deselectTargetObject();
		// Remove from render list
		renderList.remove(sel);
		// Remove from selectors list
		selectors.removeObject(sel);
	}
	
	/**
	 * Destroy all selected objects and their selectors.
	 */
	private void destroyAllSelectedObjects() {
		for (int i = selectables.count()-1; i >= 0; i--) {
			if (selectables.get(i).isSelected()) {
				canvas.getPolygonBuffer().removePoly((Polygon)selectables.get(i).getShape());
				canvas.removeGraphicsObject(selectables.get(i));
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
			selectors.addObject(sel);
			sel.getShape().setResizeable(o.getShape().isResizeable());
			knobs.addObjects(sel.getKnobs()); // Add to knobs list
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
		if (selectables.count() > 0) {
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
						destroySelector(selectable.getSelector());
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
			// Update highlighted figure
			highlightedFig = newHighlightedFig;
			// Remember the figure's brush before we change it
			origHighlightedFigBrush = newHighlightedFig.getBrush().buildBrush();
			// Set the figure's brush to the designated brush for highlighted
			// figures
			highlightedFig.setBrush(StyleManager.getHighlightedFigureBrush());
			canvas.redraw();
		}
		// IF NO NEW FIGURE HAS BEEN HIGHLIGHTED, AND THE MOST RECENTLY HIGHLIGHTED
		// FIGURE IS NO LONGER HOVERED OVER BY THE MOUSE
		else {
			if (highlightedFig != null) {
				highlightedFig.setBrush(origHighlightedFigBrush);
				highlightedFig = null;
				origHighlightedFigBrush = null;
				canvas.redraw();
			}
		}
	}
	
	public IDList<Selector<?, ?>> getSelectors() {
		return selectors;
	}
}
