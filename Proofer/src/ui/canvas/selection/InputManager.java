package ui.canvas.selection;

import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import ui.canvas.Brush;
import ui.canvas.Canvas;
import ui.canvas.Drawable;
import ui.canvas.GraphicsPolygonChild;
import ui.canvas.GraphicsRectEllipse;
import ui.canvas.GraphicsShape;
import ui.canvas.GraphicsTriangle;
import ui.canvas.StyleManager;
import ui.canvas.event.CanvasAdapter;
import ui.swing.FigureRelationListPanel;
import ui.swing.FigureRelationPanel;
import ui.swing.ProofCustomizationPanel;
import ui.canvas.diagram.DiagramCanvas;
import ui.canvas.diagram.DiagramCanvasGrid;
import ui.canvas.diagram.UIDiagramLayers;
import ui.canvas.diagram.RenderList;

import geometry.Vec2;
import geometry.proofs.Figure;
import geometry.proofs.FigureRelationType;
import geometry.shapes.Angle;
import geometry.shapes.Polygon;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;
import geometry.shapes.VertexBuffer;

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
	private static char multipleSelectionKey = PConstants.SHIFT;
	private static char uiRelMakerKey = PConstants.SHIFT;
	
	// Ease of access
	
	private DiagramCanvas canvas;
	private DiagramCanvasGrid canvasGrid;
	private RenderList renderList;
	private VertexBuffer vertexBuff;
	
	// Selection
	
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
	
	// Highlighting polygon children
	
	private List<GraphicsPolygonChild<?>> polyChildren;
	
	public InputManager(DiagramCanvas canvas) {
		this.canvas = canvas;
		renderList = canvas.getRenderList();
		canvasGrid = canvas.getCanvasGrid();
		vertexBuff = canvas.getVertexBuffer();
		selectionContainer = new SelectionBox();
		relMaker = new UIRelationMaker();
		selectors = new ArrayList<>();
		knobs = new ArrayList<>();
		selectables = new ArrayList<>();
		polyChildren = new ArrayList<>();
	}
	
	public static char getUIRelationMakerKey() {
		return uiRelMakerKey;
	}
	
	public static void setUIRelationMakerKey(char c) {
		uiRelMakerKey = c;
	}
	
	public static char getMultipleFigureSelectionKey() {
		return multipleSelectionKey;
	}
	
	public static void setMultipleFigureSelectionKey(char c) {
		multipleSelectionKey = c;
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
			 * Check for selection among selectables. Loop backwards
			 * because we want to check the figures on the TOP first.
			 */
			for (int i = selectables.size()-1; i >= 0; i--) {
				GraphicsShape<?> o = selectables.get(i);
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
					if (sel.getTargetObject().getShape() instanceof Polygon) {
						Polygon poly = (Polygon)sel.getTargetObject().getShape();
						updateVertexNamesInPolygon(poly);
					}
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
		// Release UI relation maker
		releaseUIRelationMaker(true); // true = redraw canvas
	}
	
	@Override
	public void keyReleased(Canvas c, KeyEvent e) {
		if (canvas.keyCode == PConstants.SHIFT)
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
				Selector<?, ?> selForFigure = getSelectorForFigure(shape);
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
				// If the current element in polygon-children list is one of
				// the polygon's children
				if (polyChildren.get(i).getShape().equals(child)) {
					// Remove it from the polygon-children list
					polyChildren.remove(i);
				}
			}
		}
	}
	
	/**
	 * It is essential to use this after modifying the name of a
	 * {@link GraphicsPolygon}.
	 */
	private void reloadPolygonChildren() {
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
	
	public List<Selector<?, ?>> getSelectors() {
		return selectors;
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
		renderList.getLayerList(UIDiagramLayers.SELECTOR).clear();

		// Destroy all selectors
		selectors.clear();
	}
	
	private void destroySelector(Selector<?, ?> sel) {
		// Remove knobs
		knobs.removeAll(Arrays.asList(sel.getKnobs()));
		// Deselect target object of selector
		sel.deselectTargetObject();
		// Remove from render list
		renderList.removeDrawable(sel);
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
			renderList.addDrawable(sel); // Add to render list
			
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
		knob.moveKnob(dragSceneObject(knob.getShape().getCenter(true), true));
		
		// If the knob is not a PolygonSelectorKnob, we have no more business in this method.
		if (!(knob.getSelector().getTargetObject().getShape() instanceof Polygon))
			return;
		
		// Get the knob as a polygon knob
		PolygonSelectorKnob polyKnob = ((PolygonSelectorKnob) selectedKnob);
		Vertex controlledVert = polyKnob.getControlledVertex();
		updateVertexName(controlledVert, true);
	}
	
	/**
	 * Snap the given {@link Selector} and its target object
	 * to the canvas grid.
	 * @param sel selector to snap
	 * @param redraw whether or not to redraw the canvas
	 */
	private void snapSelector(Selector<?, ?> sel, boolean redraw) {
		// In the case of a PolygonSelector
		if (sel instanceof PolygonSelector) {
			// Get the selector as a PolygonSelector
			PolygonSelector polySel = (PolygonSelector)sel;
			// Get the knobs of the selector
			PolygonSelectorKnob[] knobs = (PolygonSelectorKnob[]) polySel.getKnobs();
			// For each knob
			for (PolygonSelectorKnob knob : knobs) {
				// Get the location of the knob
				Vec2 knobLoc = knob.getControlledVertex().getCenter(true);
				// Get the nearest snap point of the knob
				Vec2 nearestSnap = canvasGrid.getNearestSnapPoint(knobLoc);
				// Move the knob
				knob.moveKnob(nearestSnap);
			}
			// Update the name of the target polygon figure
			updateVertexNamesInPolygon(polySel.getTargetObject().getShape());
			
			// Update the name of the selector
			polySel.getShape().setName(polySel.getTargetObject().getShape().getName());
		}
		// Redraw if instructed to
		if (redraw)
			canvas.redraw();
	}
	
	/**
	 * A helper method for {@link VertexBuffer#updateVertexName(Vertex, boolean)}.
	 * @param vert the vertex to be updated
	 * @param whether or not to regenerate the polygon-children
	 */
	private void updateVertexName(Vertex vert, boolean reloadPolyChildren) {
		// Location of the vertex to be updated
		Vec2 vertLoc = vert.getCenter(true);
		
		// Whether to merge the vertex with another, or demerge it from another
		final boolean mergeVert = canvasGrid.pointIsSnapped(vertLoc);
		
		// Update the vertex in the VertexBuffer
		final boolean vertexModified = vertexBuff.updateVertexName(vert, mergeVert);
		
		if (vertexModified) {
			reloadPolygonChildren();
		}
	}
	
	private void updateVertexNamesInPolygon(Polygon p) {
		Vertex[] vertices = p.getVertices();
		
		for (int i = 0; i < p.getVertexCount(); i++) {
			updateVertexName(vertices[i], false);
		}
		// Update polygons
		reloadPolygonChildren();
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
		// Mouse position
		Vec2 mouse = canvas.getMouseLocOnGrid();
				
		// For each graphics polygon child
		for (GraphicsPolygonChild<?> child : polyChildren) {
			// If the mouse is hovering over the child
			if (child.getShape().containsPoint(mouse, true)) {
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
	
	private boolean displayUIRelationMaker() {
		// If no figures are selected AND the user is holding shift
		return selectors.isEmpty() && canvas.mousePressed && canvas.keyPressed && 
				canvas.key == PConstants.CODED && 
				canvas.keyCode == uiRelMakerKey;
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
	 * the {@link RelationPair} to the {@link FigureRelationListPanel}.
	 * @param redraw whether or not to redraw the canvas
	 */
	private void releaseUIRelationMaker(boolean redraw) {
		/*
		 * After the UIRelationMaker is released, we set both end-points to [0, 0]
		 * (this will make the length of the UIRelationMaker = 0).
		 * Therefore, if the length of the UIRelationMaker is 0, then we can tell
		 * that the UIRelationMaker has NOT been dragged and cannot be released.
		 */
		if (relMaker.getShape().getLength(false) == 0f)
			return;
		/*
		 * Because we're "releasing" the UIRelationMaker, we want to make it
		 * disappear
		 */
		setDisplayUIRelationMaker(false, redraw);
		// Get the end-points of the UIRelationMaker 
		List<Vec2> endpts = Arrays.asList(relMaker.getShape().getVertexLocations());
		// Make UIRelationMaker disappear
		for (Vertex v : relMaker.getShape().getVertices())
			v.setCenter(Vec2.ZERO, true);
		
		/*
		 * Compile a list of all of the figures in the diagram that can be
		 * "connected"--a RelationPair can be made between them.
		 * 
		 * 		connectables = polygon children  +  diagram figures
		 */
		List<GraphicsShape<?>> connectables = new ArrayList<>(polyChildren);
		connectables.addAll(canvas.getDiagramFigures());
		
		// Num of connectable figures
		final int COUNT = connectables.size();
		
		// For each figure
		for (int i = 0; i < COUNT; i++) {
			// Get the the figure
			GraphicsShape<?> shape0 = connectables.get(i);
			// If the figure does NOT contain at least ONE of the UIRelationMaker's
			// end-points
			if (!shape0.getShape().containsAPointIn(endpts, true))
				continue;
			// For every other figure
			for (int j = 0; j < COUNT; j++) {
				// Get the second figure
				GraphicsShape<?> shape1 = connectables.get(j);
				// If the second figure is the same as the first, OR if the second
				// figure does NOT contain at least ONE of the UIRelationMaker's
				// end-points
				if (i == j || !shape1.getShape().containsAPointIn(endpts, true))
					continue;
				// MAKE RELATION BETWEEN THE TWO FIGURES
				ProofCustomizationPanel proofPanel = canvas.getProofCustomizationPanel();
				// Get the FigureRelationListPanel
				FigureRelationListPanel listPanel = proofPanel.getFigureRelationListPanel();
				
				// Create the relation panel
				FigureRelationPanel relPanel = new FigureRelationPanel(
						FigureRelationType.CONGRUENT,
						shape0.toString(),
						shape1.toString()
				);
				
				// Add the FigureRelation
				listPanel.addFigureRelationPairPanel(relPanel);
				return; // No more work to do
			}
		}
	}
}
