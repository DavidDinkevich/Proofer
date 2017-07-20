package ui.canvas.event;

import processing.event.KeyEvent;
import processing.event.MouseEvent;
import ui.canvas.Canvas;
import ui.canvas.GraphicsShape;

/**
 * Inheritants of this class receive {@link Canvas} events.
 * @author David Dinkevich
 */
public interface CanvasListener {
	public void mousePressed(Canvas c, MouseEvent e);
	public void mouseReleased(Canvas c, MouseEvent e);
	public void mouseClicked(Canvas c, MouseEvent e);
	public void mouseDragged(Canvas c, MouseEvent e);
	public void mouseMoved(Canvas c, MouseEvent e);
	public void mouseWheel(Canvas c, MouseEvent e);
	
	public void keyPressed(Canvas c, KeyEvent e);
	public void keyReleased(Canvas c, KeyEvent e);
	public void keyTyped(Canvas c, KeyEvent e);
	
	/**
	 * {@link CanvasListener}s that choose to implement this method
	 * should be EXTREMELY careful in what they do within it. Lengthy
	 * operations within this method could significantly slow down
	 * the frame rate.
	 * @param c the canvas
	 */
	void canvasRedrew(Canvas c);
	public void translationChanged(Canvas c);
	
	public void graphicsObjectAdded(Canvas c, GraphicsShape<?> object);
	public void graphicsObjectRemoved(Canvas c, GraphicsShape<?> object);
}
