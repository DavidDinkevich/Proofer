package geometry.shapes;

public interface Shape2D extends Shape {
	/**
	 * Get the perimeter of this {@link Shape2D}.
	 * @return the perimeter
	 */
	public float getPerimeter();
	
	/**
	 * Get the area of this {@link Shape2D}.
	 * @return the area
	 */
	// TODO: separate this into two methods: getArea(), getScaledArea()
	public float getArea();
	/**
	 * Returns a {@link Rect} surrounding this {@link Shape2D} as closely
	 * as possible.
	 */
	public Rect getBoundaryRect();
}
