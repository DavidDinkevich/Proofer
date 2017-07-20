package ui.canvas.event;

import processing.event.KeyEvent;
import processing.event.MouseEvent;
import ui.canvas.Canvas;
import ui.canvas.GraphicsShape;

/**
 * A convenience adapter for the {@link CanvasListener} interface.
 * @author David Dinkevich
 */
public abstract class CanvasAdapter implements CanvasListener {
	@Override
	public void mousePressed(Canvas c, MouseEvent e) {}
	@Override
	public void mouseReleased(Canvas c, MouseEvent e) {}
	@Override
	public void mouseClicked(Canvas c, MouseEvent e) {}
	@Override
	public void mouseDragged(Canvas c, MouseEvent e) {}
	@Override
	public void mouseMoved(Canvas c, MouseEvent e) {}
	@Override
	public void mouseWheel(Canvas c, MouseEvent e) {}
	@Override
	public void keyPressed(Canvas c, KeyEvent e) {}
	@Override
	public void keyReleased(Canvas c, KeyEvent e) {}
	@Override
	public void keyTyped(Canvas c, KeyEvent e) {}
	@Override
	public void canvasRedrew(Canvas c) {}
	@Override
	public void translationChanged(Canvas c) {}
	@Override
	public void graphicsObjectAdded(Canvas c, GraphicsShape<?> object) {}
	@Override
	public void graphicsObjectRemoved(Canvas c, GraphicsShape<?> object) {}
}
