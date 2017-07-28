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
import ui.canvas.GraphicsShape2D;
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
public class SelectionManager extends CanvasAdapter implements Drawable {
	public static final char NULL_KEY = '\0';
	public static char multipleSelectionKey = PConstants.SHIFT;
	
	private DiagramCanvas canvas;
	private RenderList renderList;
	private IDList<GraphicsShape2D<?>> selectables;
	private IDList<Selector<?, ?>> selectors;
	private IDList<Knob> knobs;
	
	// Selection of objects
	
	private SelectionBox selectionContainer;
	private boolean displaySelectionContainer = false;
	private Knob selectedKnob;
	private boolean selectMultipleObjects = false;

	public SelectionManager(DiagramCanvas canvas) {
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
				if (knob.getAllowSelections() && knob.containsPoint(canvas.getMouseLocOnGrid(), true)) {
					selectedKnob = knob;
					return;
				}
			}
			
			selectedKnob = null;
			boolean objectClickedOn = false;
			/*
			 * Check for selection among selectables
			 */
			for (GraphicsShape2D<?> o : selectables) {
				// If the object is selectable, and it was clicked on
				if (o.getAllowSelections() && o.containsPoint(canvas.getMouseLocOnGrid(), true)) {
					objectClickedOn = true;
					
					// If the user does not hold the multiple selection key, destroy all selectors
					if (!selectMultipleObjects) {
						// Deselect all previously selected objects
						destroyAllSelectors();
					}
					// If <= 1 objects selected
					else if (!selectMultipleObjects && selectors.count() <= 1) {
						selectMultipleObjects = false;
					}
					else if (selectMultipleObjects && !o.isSelected()) {
						selectMultipleObjects = false;
					}
					
					// Create selector for object and select it - ONLY IF they are not already selected.
					// Don't want duplicate selectors
					if (!o.isSelected()) {
						createSelector(o, true);
					}
					
					break;
				}
			}
			// If no objects were clicked on
			if (!objectClickedOn) {
				destroyAllSelectors();
	
				// Set the first corner of the selection container to the mouse loc
				Vec2 mouseLoc = canvas.getMouseLocOnGrid();
				selectionContainer.setCorners(mouseLoc, mouseLoc);
				// Allow selection container box to be rendered to the canvas.
				setDisplaySelectionContainer(true, false);
				
			} else {
				// Disallow selection container box to be rendered to the canvas.
				setDisplaySelectionContainer(false, true);
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
			else if (selectedKnob != null) {
				dragKnob(selectedKnob);
				canvas.redraw(); // Redraw the canvas
			}
			
			// Move all existing selectors. NOTE: moving selectors will move their
			// target objects as well
			else if (selectors.count() > 0) {
				for (Selector<?, ?> sel : selectors) {
//					dragSelector(sel, true); // Move the selector, and snap its vertices to the grid
					sel.setLoc(dragSceneObject(sel.getLoc(), false)); // Don't snap to grid
					// If the shape of the selector's target object is a polygon,
					// fix its vertex names (we moved it)
					if (sel.getTargetObject().getShape() instanceof Polygon)
						fixVertexNames((Polygon)sel.getTargetObject().getShape());
					canvas.redraw();
				}
			}
		}
	}
	
//	private void dragSelector(Selector<?, ?> sel, boolean snapVerticesToGrid) {
//		// We don't want to snap the selector to the grid, we want to snap its VERTICES
//		sel.setLoc(dragSceneObject(sel.getLoc(), false)); // Don't snap to grid
//		
//		if (snapVerticesToGrid) {
//			if (Vec2.dist(canvas.getOldMouseLocOnGrid(), canvas.getMouseLocOnGrid()) < 1.2f) {
//				snapSelector(sel, true);
//			}
//		}
//	}
	
	@Override
	public void mouseReleased(Canvas c, MouseEvent e) {
		// Erase the selection container (if it exists)
		setDisplaySelectionContainer(false, true);
	}
	
	@Override
	public void keyPressed(Canvas c, KeyEvent e) {
		if (canvas.key == PConstants.CODED) {
			// If the multiple selection key is being held
			if (canvas.keyCode == multipleSelectionKey) {
				selectMultipleObjects = true;
			}			
		} else {
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
				Brush.Builder builder = new Brush.Builder().setFill(StyleManager.PINK).setStrokeWeight(2.5f)
						.setStroke(StyleManager.PINK).setAlpha(75);

				Vertex[] tpoints = new Vertex[] {
						new Vertex(new Vec2(0f, -200.0f)),
						new Vertex(new Vec2(300f, 100f)),
						new Vertex(new Vec2(0f, 100f))
				};
				GraphicsTriangle tri = new GraphicsTriangle(builder.buildBrush(), new Triangle(Arrays.asList(tpoints)));
				tri.setAllowSelection(true);
				tri.setResizeable(true);
				
				canvas.addDiagramElement(tri);
				canvas.redraw();
			}
		}
	}
	
	@Override
	public void graphicsObjectAdded(Canvas c, GraphicsShape<?> o) {
		if (o instanceof GraphicsShape2D) {
			selectables.addObject((GraphicsShape2D<?>)o);
		}
	}
	
	@Override
	public void graphicsObjectRemoved(Canvas c, GraphicsShape<?> o) {
		selectables.removeObject((GraphicsShape2D<?>) o);
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
	
	@SuppressWarnings("unchecked")
	private Selector<?, GraphicsShape2D<?>> createSelector(GraphicsShape2D<?> o, boolean redraw) {
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
			sel.setResizeable(o.isResizeable());
			knobs.addObjects(sel.getKnobs()); // Add to knobs list
			renderList.add(sel); // Add to render list
			
			if (redraw)
				canvas.redraw();
		}
		
		return sel;
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
			if (Vec2.dist(snapPoint, to) - 1f < Vec2.dist(snapPoint, from)) { // For some reason, the -1 smoothens process on macs.
				return snapPoint;
			}
			// If the object is moving away from the snap point,
			// don't snap it.
			return to;
		}
		
		return dest;
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
//				for (int i = 0; i < p.getVertexCount(); i++) {
//					Vec2 vLoc = p.getVertexLoc(i, true);
//					final char vName = p.getVertexName(i);
//					// If the vertices overlap and have different names
//					if (!(vertexName == vName) && vertexLoc.equals(vLoc)) {
//						otherVertexName = vName;
//						otherVertLoc = vLoc;
////						otherVert = pv;
//						break outer;
//					}
//				}
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
	
	private void dragKnob(Knob knob) {		
		// Drag knob
		knob.moveKnob(dragSceneObject(knob.getLoc(), true));
		
		// If the knob is not a PolygonSelectorKnob, we have no more business in this method.
		if (!(knob.getSelector().getTargetObject().getShape() instanceof Polygon))
			return;
		
		// Get the knob as a polygon knob
		PolygonSelectorKnob polyKnob = ((PolygonSelectorKnob) selectedKnob);
		
		// Get the name of the vertex that the knob moves		
		final char vertexName = polyKnob.getControlledVertex().getNameChar();					

		fixVertexName((Polygon)polyKnob.getSelector().getTargetObject().getShape(), vertexName);
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
			for (GraphicsShape2D<?> selectable : selectables) {
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
			selectMultipleObjects = true;
		}
	}
	
	public IDList<Selector<?, ?>> getSelectors() {
		return selectors;
	}
}
