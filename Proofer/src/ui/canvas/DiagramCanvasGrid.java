package ui.canvas;

import java.util.Collection;

import geometry.Dimension;
import geometry.Vec2;
import geometry.shapes.Polygon;
import geometry.shapes.SimplePolygon;
import geometry.shapes.Vertex;

import processing.core.PConstants;
import processing.event.MouseEvent;

import ui.canvas.event.CanvasAdapter;
import ui.canvas.selection.Selector;

/**
 * Draws a grid, controls the scale of objects, translation of grid, etc.
 * @author David Dinkevich
 */
public class DiagramCanvasGrid extends CanvasAdapter implements Drawable {
	private DiagramCanvas canvas;	
	
	private boolean drawGrid = true;
	
	private Dimension defaultCellSize;
	private Dimension defaultPixelSize;
	private Dimension.Mutable pixelSize;
		
	// Scale
	
	private float scale = 1f;
	private float minScale = 0.1f;
	private float maxScale = 6f;
	
	// Minimum distance for a vertex to be snapped to a snap-point
	private float snapDist = 10f;
	
	public DiagramCanvasGrid(DiagramCanvas c, Dimension defaultCellSize) {
		canvas = c;
		this.defaultCellSize = defaultCellSize;
		defaultPixelSize = Dimension.ONE;
		pixelSize = new Dimension.Mutable(defaultPixelSize);
	}
	public DiagramCanvasGrid(DiagramCanvas c) {
		// Default cell size is 50x50
		this(c, new Dimension(50));
	}

	@Override
	public void draw(Canvas c) {
		if (!drawGrid)
			return;
		
		c.setBrush(StyleManager.getCanvasGridBrush());
		
		Dimension canvasSize = new Dimension(c.width, c.height);
		Dimension cellSize = getCellSize();
		
		Vec2 topLeft = Vec2.sub(c.getCenterLocOnGrid(),
				new Vec2(canvasSize.getWidth()/2, canvasSize.getHeight()/2));
		Vec2 bottomRight = Vec2.add(c.getCenterLocOnGrid(),
				new Vec2(canvasSize.getWidth()/2, canvasSize.getHeight()/2));
		
		// Vertical lines -- left of origin
		for (int x = 0; x > topLeft.getX(); x -= cellSize.getWidth()) {
			c.line(x, topLeft.getY(), x, bottomRight.getY());
		}
		// Vertical lines -- right of origin
		for (int x = (int)cellSize.getWidth(); x < bottomRight.getX(); x += cellSize.getWidth()) {
			c.line(x, topLeft.getY(), x, bottomRight.getY());
		}
		
		// Horizontal lines -- above origin
		for (int y = 0; y > topLeft.getY(); y -= cellSize.getHeight()) {
			c.line(topLeft.getX(), y, bottomRight.getX(), y);
		}
		// Vertical lines -- below origin
		for (int y = (int)cellSize.getHeight(); y < bottomRight.getY(); y += cellSize.getHeight()) {
			c.line(topLeft.getX(), y, bottomRight.getX(), y);
		}
	}
	
	@Override
	public void mouseWheel(Canvas c, MouseEvent e) {
		final float scrollDir = e.getCount();
		final float newScale = scrollDir/10f;	
		
		// Maximum zoom out/zoom in
		if (scale + newScale < minScale || scale + newScale > maxScale) {
			return;
		}
		// Change scale
		setScale(scale + newScale);
		
		Vec2 scaleVec = new Vec2(scale, scale);
		
		// Resize all scene objects
		for (GraphicsShape<?> o : canvas.getDiagramFigures()) {
			o.getShape().setScale(scaleVec, canvas.getMouseLocOnGrid());
		}
		// Resize the selectors of all scene objects (if any are selected)
		for (Selector<?, ?> sel : canvas.getInputManager().getSelectors()) {
			sel.setScale(scaleVec, canvas.getMouseLocOnGrid());
		}
		
		canvas.redraw();
	}
	
	@Override
	public void mouseDragged(Canvas c, MouseEvent e) {
		// Change translation by dragging right mouse button
		if (c.mouseButton == PConstants.RIGHT) {
			Vec2 from = canvas.getOldMouseLocOnGrid();
			Vec2 to = canvas.getMouseLocOnGrid();
			Vec2 offset = Vec2.sub(from, c.getTranslation());
			Vec2 dest = Vec2.sub(to, offset);
			c.setTranslation(dest);
			c.redraw();
		}
	}
	
	/**
	 * Get whether or not the given {@link Vec2} is on a snap-point.
	 * @param loc the location to check
	 * @return whether or not the location is on a snap-point.
	 */
	public boolean pointIsSnapped(Vec2 loc) {
		Vec2 nearestSnap = getNearestSnapPoint(loc);
		return loc.equals(nearestSnap);
	}

	/**
	 * Get where the given {@link Vec2} should lie on the grid. If
	 * its location is close enough to a snap-point, this function will return
	 * the snap-point's location. Otherwise, this will return the given
	 * vector's location.
	 * @param loc the location to analyze.
	 * @return where the given point should lie on the grid.
	 */
	public Vec2 getPointOnGrid(Vec2 loc) {
		Vec2 nearestSnap = getNearestSnapPoint(loc);
		
		if (Vec2.dist(loc, nearestSnap) < snapDist) {
			return nearestSnap;
		}
		return loc;
	}
	
	/**
	 * Get the nearest snap-point to the given location.
	 * @param loc the location to find the nearest snap-point.
	 * @return the nearest snap-point
	 */
	public Vec2 getNearestSnapPoint(Vec2 loc) {
		final float cellW = getCellSize().getWidth();
		final float cellH = getCellSize().getHeight();
		
		// Only work with positive vals
		final float locOffsetX = Math.abs(loc.getX()) % cellW;
		final float locOffsetY = Math.abs(loc.getY()) % cellH;
		
		Vec2.Mutable nearestSnap = new Vec2.Mutable(Vec2.ZERO);
		
		// Determine x loc
		if (locOffsetX < cellW/2f) {
			nearestSnap.setX(Math.abs(loc.getX()) - locOffsetX);
		} else {
			nearestSnap.setX(Math.abs(loc.getX()) + (cellW - locOffsetX));
		}
		
		// Determine y loc
		if (locOffsetY < cellH/2f) {
			nearestSnap.setY(Math.abs(loc.getY()) - locOffsetY);
		} else {
			nearestSnap.setY(Math.abs(loc.getY()) + (cellH - locOffsetY));
		}
		
		// Since we worked with positive vals before, we need to return
		// the coord with the proper pos/neg vals
		final float negValX = loc.getX() >= 0 ? 1 : -1;
		final float negValY = loc.getY() >= 0 ? 1 : -1;
		
		nearestSnap.setX(nearestSnap.getX() * negValX);
		nearestSnap.setY(nearestSnap.getY() * negValY);
				
		return nearestSnap;
	}
	
	/**
	 * Find the nearest snap-point to the given {@link Vertex}
	 * and set its location to that snap-point.
	 * @param v the vertex
	 */
	public void snapToGrid(Vertex v) {
		v.setCenter(getNearestSnapPoint(v.getCenter(true)), true);
	}
	
	/**
	 * Find the nearest snap-point to each of the {@link Vertex}es
	 * of the given {@link SimplePolygon} and set the vertices' location
	 * to that snap-point.
	 * @param shape the {@link SimplePolygon} to snap to the grid.
	 */
	public void snapToGrid(Polygon shape) {
		for (int i = 0; i < shape.getVertexCount(); i++) {
			Vec2 vertLoc = shape.getVertexLoc(i, true);
			shape.setVertexLoc(i, getNearestSnapPoint(vertLoc), true);
		}
	}
	
	/**
	 * For each {@link SimplePolygon} in the collection of shapes, 
	 * find the nearest snap-point to each of the {@link Vertex}es
	 * of the {@link SimplePolygon}s and set the vertices' location
	 * to that snap-point.
	 * @param shape the collection of shapes
	 */
	public void snapToGrid(Collection<? extends Polygon> shapes) {
		for (Polygon shape : shapes) {
			snapToGrid(shape);
		}
	}
	
	/**
	 * Set the cell size at the minimum scale.
	 */
	public void setDefaultCellSize(Dimension size) {
		defaultCellSize = size;
	}
	
	/**
	 * Get the cell size at the minimum scale.
	 */
	public Dimension getDefaultlCellSize() {
		return defaultCellSize;
	}
	
	/**
	 * Get the what the cell size would be at the given scale.
	 */
	public Dimension getCellSizeAtScale(float scale) {
		return Dimension.mult(defaultCellSize, scale, false);
	}
	
	/**
	 * Get the cell size at the current scale.
	 */
	public Dimension getCellSize() {
		return getCellSizeAtScale(scale);
	}
	
	/**
	 * Get the size of one pixel at the current scale.
	 */
	public Dimension getCurrentPixelSize() {
		return pixelSize;
	}
	
	/**
	 * The minimum distance for a point to be close enough
	 * to a snap-point to be snapped.
	 */
	public float getSnapDist() {
		return snapDist;
	}
	
	/**
	 * Set the minimum distance for a point to be close enough
	 * to a snap-point to be snapped.
	 * The snap distance <i>must not</i> be less than 0, nor
	 * can it be longer than the longest side of a cell on this grid.
	 * @param newDist the new snap distance
	 * @throws IllegalArgumentException if the snap distance is less than 0
	 * or if it is longer than the longest side of a cell on this grid
	 */
	public void setSnapDist(float newDist) {
		if (newDist < 0)
			throw new IllegalArgumentException("Snap distance cannot be < 0");
		final float longSide = Math.max(defaultCellSize.getWidth(), defaultCellSize.getHeight());
		if (longSide > newDist) {
			throw new IllegalArgumentException("Given snap distance is too long");
		}
		snapDist = newDist;
	}
	
	public float getScale() {
		return scale;
	}
	public void setScale(float scale) {
		this.scale = scale;
		// Update current pixel size
		pixelSize.set(Dimension.mult(defaultPixelSize, scale, false));
	}
	public float getMinScale() {
		return minScale;
	}
	public void setMinScale(float minScale) {
		this.minScale = minScale;
	}
	public float getMaxScale() {
		return maxScale;
	}
	public void setMaxScale(float maxScale) {
		this.maxScale = maxScale;
	}
	
	public boolean getDrawGrid() {
		return drawGrid;
	}
	public void setDrawGrid(boolean drawGrid) {
		this.drawGrid = drawGrid;
	}
}
