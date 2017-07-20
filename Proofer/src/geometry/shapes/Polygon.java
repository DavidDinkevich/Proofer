package geometry.shapes;

import java.util.List;

import geometry.Vec2;

/**
 * {@link Polygon}s are shapes whose structures are defined by
 * {@link Vertex}es. These shapes must provide a way to retrieve
 * {@link Vertex}es by name (char) and by index.
 * @author David Dinkevich
 */
public interface Polygon {
	/**
	 * Get the number of {@link Vertex}es.
	 */
	public int getVertexCount();
	/**
	 * Returns the {@link Vertex} with the given name.
	 * @param name the name of the requested {@link Vertex}.
	 * @return the {@link Vertex} with the given name, or null if
	 * a {@link Vertex} with the given name is not contained.
	 */
//	public Vertex getVertex(char name);
	/**
	 * Get the {@link Vertex} at the given index.
	 * @param index the index of the requested {@link Vertex}.
	 * @return the {@link Vertex}.
	 */
//	public Vertex getVertex(int index);
	
	/**
	 * Get the location of the {@link Vertex} with the given name.
	 * @param vertexName the location of the requested {@link Vertex}.
	 * @param includeScale whether or not to incorporate the scale into
	 * calculations.
	 * @return the {@link Vertex}'s location.
	 */
	public Vec2 getVertexLoc(char vertexName, boolean includeScale);
	/**
	 * Get the location of the {@link Vertex} at the given index.
	 * @param index the index of the location of the requested {@link Vertex}.
	 * @param includeScale whether or not to incorporate the scale into
	 * calculations.
	 * @return the {@link Vertex}'s location.
	 */
	public Vec2 getVertexLoc(int index, boolean includeScale);
	/**
	 * Set the location of the {@link Vertex} with the given name. This may
	 * resize and relocate this shape.
	 * @param vertexName the name of the {@link Vertex} to be relocated.
	 * @param newLoc the new location of the {@link Vertex} with the given name.
	 * @param includeScale whether or not to incorporate the scale into
	 * calculations.
	 * @return true if a {@link Vertex} with the given name was found
	 * and its location was set. False otherwise.
	 */
	public boolean setVertexLoc(char vertexName, Vec2 newLoc, boolean includeScale);
	/**
	 * Set the location of the {@link Vertex} at the given index. This may
	 * resize and relocate this shape.
	 * @param index the index of the {@link Vertex} to be relocated.
	 * @param includeScale whether or not to incorporate the scale into
	 * calculations.
	 * @param newLoc the new location of the {@link Vertex} at the given index.
	 */
	public void setVertexLoc(int index, Vec2 newLoc, boolean includeScale);
	/**
	 * Set the name of the {@link Vertex} at the given index.
	 * @param vertexIndex the index of the {@link Vertex} whose name
	 * will be changed.
	 * @param newName the new name to give to the {@link Vertex} at
	 * the given index.
	 */
	public void setVertexName(int vertexIndex, char newName);
	/**
	 * Rename the {@link Vertex} with the given name to the given new name.
	 * @param currName the name of the {@link Vertex} that will be renamed.
	 * @param newName the new name of the {@link Vertex}.
	 */
	public boolean setVertexName(char currName, char newName);
	
	/**
	 * Get the name of the {@link Vertex} at the given index.
	 * @param index the index of the vertex whose name will be retrieved.
	 * @return the name of the vertex at the given index.
	 */
	public char getVertexName(int index);
	
	/**
	 * Get all of locations of the {@link Vertex}es of this
	 * {@link Polygon}.
	 * @return the locations as an array of {@link Vec2}s.
	 */
	public Vec2[] getVertexLocations();
	
	public String getName();
	
	public List<Vertex> getVertices();
	
	/**
	 * Get the {@link Segment} with the given name..
	 * @param name the name of the {@link Segment}.
	 * @return the {@link Segment}, or null if this polygon does not contain
	 * a segment with the given name.
	 * @throws IllegalArgumentException if the given {@link Segment} name
	 * is illegal (as per {@link Segment#isValidSegmentName(String)})
	 */
	public Segment getSide(String name);
	
	/**
	 * Get a list of  all the sides of this {@link Polygon}
	 */
	public List<Segment> getSides();
	
	public Angle getAngle(String name);
	
	public List<Angle> getAngles();
}
