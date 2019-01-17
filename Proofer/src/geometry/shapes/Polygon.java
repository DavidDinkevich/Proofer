package geometry.shapes;

import geometry.Vec2;

/**
 * {@link Polygon}s are shapes whose structures are defined by
 * {@link Vertex}es. These shapes must provide a way to retrieve
 * {@link Vertex}es by name (char) and by index.
 * @author David Dinkevich
 */
public interface Polygon extends Shape2D {
	/**
	 * Get the number of {@link Vertex}es.
	 */
	public int getVertexCount();
	
	/**
	 * Get the location of the {@link Vertex} with the given name.
	 * @param vertexName the location of the requested {@link Vertex}.
	 * @return the {@link Vertex}'s location.
	 */
	public Vec2 getVertexLoc(char vertexName);

	/**
	 * Get the location of the {@link Vertex} at the given index.
	 * @param index the index of the location of the requested {@link Vertex}.
	 * @return the {@link Vertex}'s location.
	 */
	public Vec2 getVertexLoc(int index);

	/**
	 * Set the location of the {@link Vertex} with the given name. This may
	 * resize and relocate this shape.
	 * @param vertexName the name of the {@link Vertex} to be relocated.
	 * @param newLoc the new location of the {@link Vertex} with the given name.
	 * @return true if a {@link Vertex} with the given name was found
	 * and its location was set. False otherwise.
	 */
	public boolean setVertexLoc(char vertexName, Vec2 newLoc);

	/**
	 * Set the location of the {@link Vertex} at the given index. This may
	 * resize and relocate this shape.
	 * @param index the index of the {@link Vertex} to be relocated.
	 * @param newLoc the new location of the {@link Vertex} at the given index.
	 */
	public void setVertexLoc(int index, Vec2 newLoc);
	
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
	
	/**
	 * Get a list of this {@link Polygon}'s vertices
	 * @return the list of vertices
	 */
	public Vertex[] getVertices();
	
	/**
	 * Get whether this {@link Polygon} contains a vertex with the given name.
	 * @param vertexName the name of the vertex being checked
	 * @return whether or not this {@link Polygon} contains a vertex with
	 * the given name.
	 */
	default public boolean containsVertex(char vertexName) {
		return getName().contains(String.valueOf(vertexName));
	}
	
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
	
	/**
	 * Set the name of this {@link Polygon} to match its vertices. This
	 * is essential after changing the name of a vertex in this {@link Polygon}
	 * by using the vertex's own setName() method as opposed to using this
	 * {@link Polygon}'s setVertexName() methods.
	 */
	// TODO: make this protected?
	default public void syncNameWithVertexNames() {
		Vertex[] vertices = getVertices();
		StringBuilder b = new StringBuilder(vertices.length);
		for (int i = 0; i < vertices.length; i++) {
			b.append(vertices[i].getNameChar());
		}
		setName(b.toString());
	}
}
