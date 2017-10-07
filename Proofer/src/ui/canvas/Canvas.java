package ui.canvas;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import ui.canvas.event.CanvasListener;
import ui.canvas.selection.InputManager;

import java.awt.EventQueue;

import java.util.ArrayList;
import java.util.Collection;

import geometry.Dimension;
import geometry.Vec2;
import geometry.shapes.Ellipse;
import geometry.shapes.Polygon;
import geometry.shapes.Rect;
import geometry.shapes.Segment;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;

public class Canvas extends PApplet {
	private static final long serialVersionUID = 8449932945696910322L;
	
	private ArrayList<CanvasListener> listeners;
	private Brush.Builder brush;
	private RenderList renderList;
	private Vec2.Mutable translation;
	private int background;
	
	public Canvas(Dimension size, int background) {
		this.background = background;
		setSize((int)size.getWidth(), (int)size.getHeight()-20);
		
		brush = new Brush.Builder();
		listeners = new ArrayList<>();
		renderList = new RenderList();
	}
	public Canvas(Dimension size) {
		this(size, 255);
	}
	public Canvas(int background) {
		this(Dimension.ONE_HUNDRED, background);
	}
	
	/*
	 * RENDERING
	 */
	
	@Override
	public void setup() {
		/*
		 * All rectangles' location point in this program is their center.
		 */
		rectMode(CENTER);
		
		// For better performance, Canvases do not continuously redraw
		noLoop();
		
		// Default translation is at the center of the screen
		translation = new Vec2.Mutable(width/2, height/2);					
	}
		
	@Override
	public void draw() {
		translate(translation);
		// Flip the y axis--make canvas coord system = a normal Cartesian coord plane
//		scale(1, -1);
		background(background);
						
		// Notify listeners
		for (CanvasListener c : listeners) {
			c.canvasRedrew(this);
		}
	}
		
	@Override
	public void mousePressed(MouseEvent e) {		
		// Notify listeners
		for (CanvasListener c : listeners) {
			c.mousePressed(this, e);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		// Notify listeners
		for (CanvasListener c : listeners) {
			c.mouseReleased(this, e);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// Notify listeners
		for (CanvasListener c : listeners) {
			c.mouseClicked(this, e);
		}
	}
	
	@Override
	public void mouseWheel(MouseEvent e) {
		// Notify listeners
		for (CanvasListener c : listeners) {
			c.mouseWheel(this, e);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// Notify listeners
		for (CanvasListener c : listeners) {
			c.mouseDragged(this, e);
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		// Notify listeners
		for (CanvasListener c : listeners) {
			c.mouseMoved(this, e);
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		// Prevent program exiting if escape is pressed
		// "keyCode == 27" is for mac.
		if (key == CODED && keyCode == ESC || keyCode == 27) {
			key = InputManager.NULL_KEY;
		}
		
		// Notify listeners
		for (CanvasListener c : listeners) {
			c.keyPressed(this, e);
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		// Notify listeners
		for (CanvasListener c : listeners) {
			c.keyReleased(this, e);
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// Notify listeners
		for (CanvasListener c : listeners) {
			c.keyTyped(this, e);
		}
	}
	
	// TODO: notify listeners when grid is moved
	
	/**
	 * Add a {@link GraphicsShape} to the {@link Canvas}.
	 * @param o the object to add
	 */
	public void addGraphicsObject(GraphicsShape<?> o) {
		renderList.add(o);
		// Notify listeners
		for (CanvasListener c : listeners) {
			c.graphicsObjectAdded(this, o);
		}
	}
	
	public void addGraphicsObjects(Collection<GraphicsShape<?>> o) {
		for (GraphicsShape<?> object : o) {
			addGraphicsObject(object);
		}
	}
	public void removeGraphicsObject(GraphicsShape<?> o) {
		renderList.remove(o);
		// Notify listeners
		for (CanvasListener c : listeners) {
			c.graphicsObjectRemoved(this, o);
		}
	}
	
	public void removeGraphicsObjects(Collection<GraphicsShape<?>> o) {
		for (GraphicsShape<?> g : o) {
			removeGraphicsObject(g);
		}
	}
	
	/*
	 * Convenience rendering methods
	 */
	
	public Brush getBrush() {
		return brush;
	}
	public void setBrush(Brush brush) {
		this.brush.set(brush);

		if (brush.renderFill())
			fill(brush.getFill(), brush.getAlpha());
		else
			noFill();
		if (brush.renderStroke()) {
			strokeWeight(brush.getStrokeWeight());
			stroke(brush.getStroke(), brush.getAlpha());
		} else {
			noStroke();
		}
	}
	
	public void redraw() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Canvas.super.redraw();
			}
		});
	}
	
	public void translate(Vec2 translation) {
		translate(translation.getX(), translation.getY());
	}
	
	public void text(String text, Vec2 loc) {
		text(text, loc.getX(), loc.getY());
	}
	public void text(char c, Vec2 loc) {
		text(c, loc.getX(), loc.getY());
	}
	
	public void ellipse(Vec2 loc, Dimension size) {
		ellipse(loc.getX(), loc.getY(), size.getWidth(), size.getHeight());
	}
	public void ellipse(Ellipse o) {
		ellipse(o.getCenter(true), o.getSize(true));
	}
	public void circle(Vec2 loc, float diam) {
		ellipse(loc.getX(), loc.getY(), diam, diam);
	}
	public void rect(Vec2 loc, Dimension size) {
		rect(loc.getX(), loc.getY(), size.getWidth(), size.getHeight());
	}
	public void rect(Rect rect) {
		rect(rect.getCenter(true), rect.getSize(true));
	}
	public void triangle(Vec2 p1, Vec2 p2, Vec2 p3) {
		triangle(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());
	}
	public void triangle(Vec2[] points) {
		triangle(points[0], points[1], points[2]);
	}
	public void triangle(Triangle o) {
		triangle(new Vec2[] {
				o.getVertexLoc(0, true),
				o.getVertexLoc(1, true),
				o.getVertexLoc(2, true)
		});
	}
	public void line(Vec2 point1, Vec2 point2) {
		line(point1.getX(), point1.getY(), point2.getX(), point2.getY());
	}
	public void line(Segment seg) {
		line(seg.getVertexLoc(0, true), seg.getVertexLoc(1, true));
	}
	
	public void vertex(Vec2 loc) {
		vertex(loc.getX(), loc.getY());
	}
	public void vertex(Vertex v) {
		vertex(v.getCenter(true));
	}
	
	public void polygon(Polygon p) {
		beginShape();
		for (int i = 0; i < p.getVertexCount(); i++) {
			vertex(p.getVertexLoc(i, true));
		}
		endShape(PConstants.CLOSE);
	}
	
	/** Get a random int within the given parameters. Inclusive, inclusive. */
	public static int randInt(int min, int max) {
		return (int)new PApplet().random(min, max+1);
	}
	
	/**
	 * Can't call this "getSize()" because Component took that name already.
	 */
	public Dimension getDimensions() {
		return new Dimension(width, height);
	}
	
	/**
	 * Returns the given vector as a vector starting from
	 * the translation
	 * @return the mouse location
	 */
	public Vec2 getLocOnGrid(Vec2 loc) {
		return Vec2.sub(loc, translation);
	}
	
	/**
	 * Returns the mouse location as a vector from
	 * the top left of the screen (0, 0)
	 * @return the mouse location
	 */
	public Vec2 getMouseLocRaw() {
		return new Vec2(mouseX, mouseY);
	}
	
	/**
	 * Get the location of the mouse as a vector
	 * from the translation.
	 * @return the mouse location
	 * @see Canvas#getTranslation()
	 */
	public Vec2 getMouseLocOnGrid() {
		return getLocOnGrid(getMouseLocRaw());
	}
	
	/**
	 * Returns the old mouse location as a vector from
	 * the top left of the screen (0, 0)
	 * @return the mouse location
	 */
	public Vec2 getOldMouseLocRaw() {
		return new Vec2(pmouseX, pmouseY);
	}
	
	/**
	 * Get the old location of the mouse as a vector
	 * from the translation.
	 * @return the old mouse location
	 * @see Canvas#getTranslation()
	 */
	public Vec2 getOldMouseLocOnGrid() {
		return getLocOnGrid(getOldMouseLocRaw());
	}
	
	/**
	 * Returns the center point of this {@link Canvas}
	 * as a vector from the top left of the screen (0, 0)
	 * @return the center point of this {@link Canvas}.
	 */
	public Vec2 getCenterLocRaw() {
		return new Vec2(width/2, height/2);
	}
	
	/**
	 * Get the center point of the {@link Canvas} as a vector
	 * from the translation.
	 * @return the center point of this {@link Canvas}.
	 * @see Canvas#getTranslation()
	 */
	public Vec2 getCenterLocOnGrid() {
		return getLocOnGrid(new Vec2(width/2, height/2));
	}
	
	public RenderList getRenderList() {
		return renderList;
	}
	
	public Vec2 getTranslation() {
		return translation;
	}
	public void setTranslation(Vec2 translation) {
		this.translation.set(translation);
	}

	public boolean addCanvasListener(CanvasListener e) {
		return listeners.add(e);
	}
	public CanvasListener removeCanvasListener(int index) {
		return listeners.remove(index);
	}
	public boolean removeCanvasListener(Object o) {
		return listeners.remove(o);
	}
	
	public int getBackgroundColor() {
		return background;
	}
	public void setBackground(int background) {
		this.background = background;
	}
}

