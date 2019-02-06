package geometry.shapes;

/**
 * {@link Polygon}s are <i>2D</i> shapes whose structures are defined by
 * {@link Vertex}es.
 * @author David Dinkevich
 * @see {@link VertexShape}
 */
public interface Polygon extends VertexShape, Shape2D {	
	/**
	 * Get the {@link Segment} with the given name..
	 * @param name the name of the {@link Segment}.
	 * @return the {@link Segment}, or null if this {@link Polygon} does not contain
	 * a segment with the given name.
	 * @throws IllegalArgumentException if the given {@link Segment} name
	 * is illegal (as per {@link Segment#isValidSegmentName(String)})
	 */
	public Segment getSide(String name);
	
	/**
	 * Get a list of  all the sides of this {@link Polygon}
	 */
	public Segment[] getSides();
	
	/**
	 * Get the {@link Angle} with the given name.
	 * @param name the name of the angle to be retrieved
	 * @return the {@link Angle}
	 */
	public Angle getAngle(String name);
	
	/**
	 * Get a list of the {@link Angle}s of this {@link Polygon}
	 * @return the list of {@link Angle}s
	 */
	public Angle[] getAngles();
}
