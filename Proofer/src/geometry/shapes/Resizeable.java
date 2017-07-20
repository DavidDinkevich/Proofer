package geometry.shapes;

/**
 * Objects that inherit from this interface can decide whether they are
 * resizeable or not.
 * @author David Dinkevich
 */
interface Resizeable {
	public boolean isResizeable();
	public void setResizeable(boolean resizeable);
}
