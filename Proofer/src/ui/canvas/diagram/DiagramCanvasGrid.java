package ui.canvas.diagram;

import java.util.Collection;

import geometry.Dimension;
import geometry.Vec2;
import geometry.shapes.Polygon;
import geometry.shapes.Vertex;

import javafx.scene.input.MouseEvent;

import ui.canvas.AdvancedCanvas;
import ui.canvas.Drawable;
import ui.canvas.StyleManager;
import ui.canvas.diagram.DiagramCanvas.UIDiagramLayers;


public class DiagramCanvasGrid implements Drawable {
	
	private DiagramCanvas dcanvas;
	
	private boolean drawGrid;
	private Dimension.Mutable cellSize;
	
	// Minimum distance for a vertex to be snapped to a snap-point
	private float snapDist;
	

	public DiagramCanvasGrid(DiagramCanvas dcanvas, Dimension defCellSize) {
		this.dcanvas = dcanvas;
		this.cellSize = Dimension.Mutable.requireNonNegative(defCellSize);
		drawGrid = true;
		// Snap dist = min cell side / 5
		snapDist = Math.min(defCellSize.getWidth(), defCellSize.getHeight()) / 5f;
		
		/*
		 * Enable dragging
		 */
		
		dcanvas.getCanvas().addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
			if (e.isSecondaryButtonDown()) {
				Vec2 from = dcanvas.getPMouse();
				Vec2 to = dcanvas.getMouse();
				Vec2 offset = Vec2.sub(to, from);
				dcanvas.setTranslation(offset);
			}
			
			dcanvas.redraw();
		});

	}
	
	@Override
	public void draw(AdvancedCanvas c) {
		if (drawGrid) {
			c.setBrush(StyleManager.getCanvasGridBrush());
//			c.getCanvas().getGraphicsContext2D().setStroke(Color.rgb(0, 0, 0, 1));
			
			Dimension cSize = dcanvas.getSize();
			
			Vec2 bottomRightRaw = new Vec2(cSize.getWidth(), -cSize.getHeight());
			Vec2 halfScreen = Vec2.div(bottomRightRaw, 2);
			Vec2 topLeft = Vec2.sub(c.getCenterLocOnGrid(), halfScreen);
			Vec2 bottomRight = Vec2.add(c.getCenterLocOnGrid(), halfScreen);

			// TODO: clean up
			
			// Vertical lines -- left of origin (dir = middle to left)
			for (int x = 0; x > topLeft.getX(); x -= cellSize.getWidth()) {
				dcanvas.strokeLine(x, topLeft.getY(), x, bottomRight.getY());
			}
			// Vertical lines -- right of origin (dir = middle to right)
			for (int x = (int)cellSize.getWidth(); x < bottomRight.getX(); 
					x += cellSize.getWidth()) {
				
				dcanvas.strokeLine(x, topLeft.getY(), x, bottomRight.getY());
			}
			
			// Horizontal lines -- above origin (dir = middle to top)
			for (int y = 0; y < topLeft.getY(); y += cellSize.getHeight()) {
				dcanvas.strokeLine(topLeft.getX(), y, bottomRight.getX(), y);
			}
			// Vertical lines -- below origin (dir = middle to bottom)
			for (int y = (int)cellSize.getHeight(); y > bottomRight.getY(); 
					y -= cellSize.getHeight()) {
				
				dcanvas.strokeLine(topLeft.getX(), y, bottomRight.getX(), y);
			}
			
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
	
	@Override
	public UIDiagramLayers getLayer() {
		return UIDiagramLayers.GRID;
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
		v.setCenter(getNearestSnapPoint(v.getCenter()));
	}
	
	/**
	 * Find the nearest snap-point to each of the {@link Vertex}es
	 * of the given {@link SimplePolygon} and set the vertices' location
	 * to that snap-point.
	 * @param shape the {@link SimplePolygon} to snap to the grid.
	 */
	public void snapToGrid(Polygon shape) {
		for (int i = 0; i < shape.getVertexCount(); i++) {
			Vec2 vertLoc = shape.getVertexLoc(i);
			shape.setVertexLoc(i, getNearestSnapPoint(vertLoc));
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
	 * Set the cell size.
	 */
	public void setCellSize(Dimension size) {
		cellSize.set(Dimension.requireNonNegative(size));
	}
	
	public Dimension getCellSize() {
		return cellSize;
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
		// TODO: can it be 0?
		if (newDist < 0)
			throw new IllegalArgumentException("Snap distance cannot be < 0");
		final float longSide = Math.max(cellSize.getWidth(), cellSize.getHeight());
		if (longSide > newDist) {
			throw new IllegalArgumentException("Given snap distance is too long");
		}
		snapDist = newDist;
	}

	
}
