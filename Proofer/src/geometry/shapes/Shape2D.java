package geometry.shapes;

public interface Shape2D extends Shape, Resizeable {
	/**
	 * Get the perimeter of this {@link AbstractShape2D}.
	 * @return the perimeter
	 */
	public float getPerimeter();
	
	/**
	 * Get the area of this {@link AbstractShape2D}.
	 * @return the area
	 */
	// TODO: separate this into two methods: getArea(), getScaledArea()
	public float getArea();
	/**
	 * Returns a {@link Rect} surrounding this {@link AbstractShape2D} as closely
	 * as possible.
	 */
	public Rect getBoundaryRect();
}
