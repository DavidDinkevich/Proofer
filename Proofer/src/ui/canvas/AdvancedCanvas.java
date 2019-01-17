package ui.canvas;

import static javafx.scene.input.KeyEvent.KEY_PRESSED;
import static javafx.scene.input.KeyEvent.KEY_RELEASED;
import static javafx.scene.input.MouseEvent.MOUSE_DRAGGED;
import static javafx.scene.input.MouseEvent.MOUSE_MOVED;
import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;
import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import geometry.Dimension;
import geometry.Vec2;
import geometry.shapes.Arc;
import geometry.shapes.Ellipse;
import geometry.shapes.Polygon;
import geometry.shapes.Rect;
import geometry.shapes.Segment;
import geometry.shapes.Vertex;

import javafx.event.EventType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;

import util.Utils;

public class AdvancedCanvas {
	
	public static final String SHIFT = "SHIFT";
	public static final String CAPS_LOCK = "CAPS";
	public static final String ENTER = "ENTER";
	
		
	private Map<EventType<?>, InputEvent> inputEvents;
	private List<String> keysDown;
	private Vec2.Mutable pmouse, mouse;
	
	private Canvas canvas;
	protected GraphicsContext gc;
	
	private Brush.Builder brush;
	
	private Dimension size;
	
	public AdvancedCanvas(int w, int h) {
		
		size = Dimension.requireNonNegative(new Dimension(w, h));
		keysDown = new ArrayList<>();
		inputEvents = new HashMap<>();
		brush = new Brush.Builder();
		mouse = new Vec2.Mutable();
		pmouse = new Vec2.Mutable();
		
		canvas = new Canvas(w, h);
		gc = canvas.getGraphicsContext2D();
		
		// Enables key events
		canvas.setFocusTraversable(true);
		
		/*
		 * EVENTS
		 */

		canvas.addEventFilter(MOUSE_MOVED, e -> {
			inputEvents.put(MOUSE_MOVED, e);
			updateMouseLoc(e);
		});
		
		canvas.addEventFilter(MOUSE_DRAGGED, e -> {
			inputEvents.put(MOUSE_DRAGGED, e);
			updateMouseLoc(e);
		});
		
		canvas.addEventFilter(MOUSE_PRESSED, e -> {
			inputEvents.put(MOUSE_PRESSED, e);
		});
		
		canvas.addEventFilter(MOUSE_RELEASED, e -> {
			inputEvents.put(MOUSE_RELEASED, e);
		});
		
		canvas.addEventFilter(KEY_PRESSED, e -> {
			inputEvents.put(KEY_PRESSED, e);
			
			String key = e.getText();
			String code = e.getCode().toString();
			
			if (key.length() == 1) {
				if (!keysDown.contains(key))
					keysDown.add(key);
			}
			else if (!keysDown.contains(code)) {
				keysDown.add(e.getCode().toString());
			}
		});
		
		canvas.addEventFilter(KEY_RELEASED, e -> {
			inputEvents.put(KEY_RELEASED, e);
			
			String key = e.getText();
			String code = e.getCode().toString();
			
			if (key.length() == 1) {
				keysDown.remove(key);
			} else {
				keysDown.remove(code);
			}
		});
		
		// BY DEFAULT
		canvas.setScaleY(-1);
		setTranslation(getCenterLocRaw());
		gc.setFont(new Font(Font.getDefault().getName(), 15));
		gc.setLineCap(StrokeLineCap.ROUND);
	}
	
	private void updateMouseLoc(MouseEvent e) {
		final float mx = (float) e.getX();
		final float my = (float) e.getY();

		pmouse.set(mouse);
		mouse.set(mx, my);
	}
		
	public void redraw() {
		// Background
		clearRect(getTranslation().negated(), getSize());
	}
	
	public boolean keysAreDown(String...keys) {
		for (String key : keys) {
			if (!keysDown.contains(key)) {
				return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends InputEvent> T getInputEvent(EventType<?> type) {
		return (T) inputEvents.get(type);
	}
	
	public Brush.Builder getBrush() {
		return brush;
	}
	
	private void applyBrush() {
		RGBAColor c = brush.getFill();
		gc.setFill(Color.rgb(c.getR(), c.getG(), c.getB(), c.getA()));

		RGBAColor s = brush.getStroke();
		gc.setStroke(Color.rgb(s.getR(), s.getG(), s.getB(), s.getA()));
		gc.setLineWidth(brush.getStrokeWeight());
	}
	
	public void setBrush(Brush brush) {
		this.brush.set(brush);
		applyBrush();
	}
	
	/*
	 * CONVENIENCE RENDERING METHODS (2D)
	 */
	
	/**
	 * Default rendering point for shapes in JavaFX
	 * @param loc
	 * @param size
	 * @return
	 */
	private Vec2 centerRect(Vec2 loc, float w, float h) {
		return new Vec2(loc.getX() - w / 2, loc.getY() - h / 2);
	}
	
	public void clearRect(Vec2 loc, Dimension size) {
		gc.clearRect(loc.getX(), loc.getY(), size.getWidth(), size.getHeight());
	}
	
	public void clearRect(Rect rect) {
		clearRect(rect.getCenter(), rect.getSize());
	}
	
	public void fillText(String text, Vec2 loc) {
		// Since default scale of Canvas is (1, -1), then when drawing text,
		// text must be flipped upside down again
		gc.save();
		gc.translate(loc.getX(), loc.getY());
		gc.scale(1, -1);
		gc.fillText(text, 0, 0);
		gc.restore();
	}
	
	public void strokeText(String text, Vec2 loc) {
		gc.strokeText(text, loc.getX(), loc.getY());
	}
	
	public void fillText(char c, Vec2 loc) {
		fillText(String.valueOf(c), loc);
	}

	public void strokeText(char c, Vec2 loc) {
		strokeText(String.valueOf(c), loc);
	}
	
	public void fillEllipse(Vec2 loc, float diam) {
		// Centered
		Vec2 c = centerRect(loc, diam, diam);
		gc.fillOval(c.getX(), c.getY(), diam, diam);
	}
	
	public void strokeEllipse(Vec2 loc, float diam) {
		// Centered
		Vec2 c = centerRect(loc, diam, diam);
		gc.strokeOval(c.getX(), c.getY(), diam, diam);
	}
	
	public void fillEllipse(Vec2 loc, Dimension size) {
		// Centered
		Vec2 c = centerRect(loc, size.getWidth(), size.getHeight());
		gc.fillOval(c.getX(), c.getY(), size.getWidth(), size.getHeight());
	}
	
	public void strokeEllipse(Vec2 loc, Dimension size) {
		// Centered
		Vec2 c = centerRect(loc, size.getWidth(), size.getHeight());
		gc.strokeOval(c.getX(), c.getY(), size.getWidth(), size.getHeight());
	}
	
	public void fillEllipse(Ellipse o) {
		fillEllipse(o.getCenter(), o.getSize());
	}
	
	public void strokeEllipse(Ellipse o) {
		strokeEllipse(o.getCenter(), o.getSize());
	}
	
	public void fillRect(Vec2 loc, Dimension size) {
		// Centered
		Vec2 c = centerRect(loc, size.getWidth(), size.getHeight());
		gc.fillRect(c.getX(), c.getY(), size.getWidth(), size.getHeight());
	}
	
	public void fillRect(Vec2 loc, float size) {
		// Centered
		Vec2 c = centerRect(loc, size, size);
		gc.fillRect(c.getX(), c.getY(), size, size);
	}
	
	public void strokeRect(Vec2 loc, Dimension size) {
		// Centered
		Vec2 c = centerRect(loc, size.getWidth(), size.getHeight());
		gc.strokeRect(c.getX(), c.getY(), size.getWidth(), size.getHeight());
	}
	
	public void strokeRect(Vec2 loc, float size) {
		// Centered
		Vec2 c = centerRect(loc, size, size);
		gc.strokeRect(c.getX(), c.getY(), size, size);
	}
	
	public void fillRect(Rect rect) {
		fillRect(rect.getCenter(), rect.getSize());
	}
	
	public void strokeRect(Rect rect) {
		strokeRect(rect.getCenter(), rect.getSize());
	}
	
	public void fillPolygon(Polygon p) {
		gc.fillPolygon(
			new double[] {
					p.getVertexLoc(0).getX(),
					p.getVertexLoc(1).getX(),
					p.getVertexLoc(2).getX()
			},
			new double[] {
					p.getVertexLoc(0).getY(),
					p.getVertexLoc(1).getY(),
					p.getVertexLoc(2).getY()
			},
			p.getVertexCount()
		);
	}
	
	public void strokePolygon(Polygon p) {
		gc.strokePolygon(
			new double[] {
					p.getVertexLoc(0).getX(),
					p.getVertexLoc(1).getX(),
					p.getVertexLoc(2).getX()
			},
			new double[] {
					p.getVertexLoc(0).getY(),
					p.getVertexLoc(1).getY(),
					p.getVertexLoc(2).getY()
			},
			p.getVertexCount()
		);
	}


	public void strokeLine(Vec2 point1, Vec2 point2) {
		gc.strokeLine(point1.getX(), point1.getY(), point2.getX(), point2.getY());
	}
	
	public void strokeLine(Segment seg) {
		strokeLine(seg.getVertexLoc(0), seg.getVertexLoc(1));
	}
	
	public void fillVertex(Vertex v) {
		fillText(v.getName(), v.getCenter());
	}
	
	public void strokeVertex(Vertex v) {
		strokeText(v.getName(), v.getCenter());
	}
	
	public void fillArc(Vec2 loc, Dimension size, float start, float stop) {
		Vec2 correctLoc = centerRect(loc, size.getWidth(), size.getHeight());
		gc.fillArc(
				correctLoc.getX(), correctLoc.getY(), 
				size.getWidth(), size.getHeight(), 
				// TODO: change my arc angle system to JavaFX's system
				-Utils.radiansToDegrees(start), -Utils.radiansToDegrees(stop-start),
				// TODO: add ArcType to Arc class
				ArcType.ROUND
		);
	}
	
	public void strokeArc(Vec2 loc, Dimension size, float start, float stop) {
		Vec2 correctLoc = centerRect(loc, size.getWidth(), size.getHeight());
		gc.strokeArc(
				correctLoc.getX(), correctLoc.getY(), 
				size.getWidth(), size.getHeight(), 
				// TODO: change my arc angle system to JavaFX's system
				-Utils.radiansToDegrees(start), -Utils.radiansToDegrees(stop-start),
				// TODO: add ArcType to Arc class
				ArcType.ROUND
		);
	}
	
	public void fillArc(Arc arc) {
		fillArc(arc.getCenter(), arc.getSize(), arc.getStartAngle(), arc.getStopAngle());
	}
	
	public void strokeArc(Arc arc) {
		strokeArc(arc.getCenter(), arc.getSize(), arc.getStartAngle(), arc.getStopAngle());
	}
	
	public void setTranslation(Vec2 vec) {
		gc.translate(vec.getX(), vec.getY());
	}
	
	public Vec2 getTranslation() {
		return new Vec2((float) gc.getTransform().getTx(), 
				(float) gc.getTransform().getTy());
	}
	
	// END CONVENIENCE RENDERING METHODS (2D)
	
	// BEGIN GRID CONVENIENCE METHODS
	
	/**
	 * Returns the given vector as a vector starting from
	 * the translation
	 * @return the mouse location
	 */
	public Vec2 getLocOnGrid(Vec2 loc) {
//		return Vec2.sub(new Vec2(loc.getX(), loc.getY()), getTranslation());
		return Vec2.sub(loc, getTranslation());
	}
	
	/**
	 * Get the location of the mouse as a vector
	 * from the translation.
	 * @return the mouse location
	 * @see AdvancedCanvas#getTranslation()
	 */
	public Vec2 getMouseLocOnGrid() {
		return getLocOnGrid(getMouse());
	}
	
	/**
	 * Get the old location of the mouse as a vector
	 * from the translation.
	 * @return the old mouse location
	 * @see AdvancedCanvas#getTranslation()
	 */
	public Vec2 getOldMouseLocOnGrid() {
		return getLocOnGrid(getPMouse());
	}
	
	/**
	 * Returns the center point of this {@link AdvancedCanvas}
	 * as a vector from the top left of the screen (0, 0)
	 * @return the center point of this {@link AdvancedCanvas}.
	 */
	public Vec2 getCenterLocRaw() {
		return new Vec2(getSize().getWidth() / 2, getSize().getHeight() / 2);
	}
	
	/**
	 * Get the center point of the {@link AdvancedCanvas} as a vector
	 * from the translation.
	 * @return the center point of this {@link AdvancedCanvas}.
	 * @see AdvancedCanvas#getTranslation()
	 */
	public Vec2 getCenterLocOnGrid() {
		return getLocOnGrid(getCenterLocRaw());
	}
	
	public void fillRect(double arg0, double arg1, double arg2, double arg3) {
		gc.fillRect(arg0, arg1, arg2, arg3);
	}
	
	public void fillText(String arg0, double arg1, double arg2) {
		gc.fillText(arg0, arg1, arg2);
	}
	
	public void strokeLine(double arg0, double arg1, double arg2, double arg3) {
		gc.strokeLine(arg0, arg1, arg2, arg3);
	}
	
	public void strokeOval(double arg0, double arg1, double arg2, double arg3) {
		gc.strokeOval(arg0, arg1, arg2, arg3);
	}
	
	// END GRID CONVENIENCE METHODS
	
	public List<String> getKeysDown() {
		return keysDown;
	}

	public Canvas getCanvas() {
		return canvas;
	}
	
	public Vec2 getPMouse() {
		return pmouse;
	}

	public Vec2 getMouse() {
		return mouse;
	}

	public Dimension getSize() {
		return size;
	}

}
