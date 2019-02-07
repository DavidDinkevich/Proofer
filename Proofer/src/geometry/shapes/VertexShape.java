package geometry.shapes;

import geometry.Vec2;

/**
 * Represents a {@link Shape} that is a connection of vertices.
 */
public interface VertexShape extends Shape {
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
	 * {@link VertexShape}.
	 * @return the locations as an array of {@link Vec2}s.
	 */
	public Vec2[] getVertexLocations();
	
	/**
	 * Get a list of this {@link VertexShape}'s vertices
	 * @return the list of vertices
	 */
	public Vertex[] getVertices();
	
	/**
	 * Get the {@link Vertex} with the given name
	 * @param name the name
	 * @return the vertex
	 */
	default public Vertex getVertex(char name) {
		for (Vertex v : getVertices()) {
			if (v.getNameChar() == name) {
				return v;
			}
		}
		return null;
	}
	
	/**
	 * Get whether this {@link VertexShape} contains a vertex with the given name.
	 * @param vertexName the name of the vertex being checked
	 * @return whether or not this {@link VertexShape} contains a vertex with
	 * the given name.
	 */
	default public boolean containsVertex(char vertexName) {
		return getName().contains(String.valueOf(vertexName));
	}
	
	/**
	 * Set the name of this {@link VertexShape} to match its vertices. This
	 * is essential after changing the name of a vertex in this {@link VertexShape}
	 * by using the vertex's own setName() method as opposed to using this
	 * {@link VertexShape}'s setVertexName() methods.
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
