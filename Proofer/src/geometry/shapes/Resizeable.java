package geometry.shapes;

/**
 * Objects that inherit from this interface can decide whether they are
 * resizable or not.
 * @author David Dinkevich
 */
interface Resizeable {
	public boolean isResizeable();
	public void setResizeable(boolean resizeable);
}
