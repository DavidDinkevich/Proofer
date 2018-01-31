package geometry.shapes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import geometry.Vec2;

/**
 * A container that stores {@link Vertex}es and manages/modifies their
 * names based on their position.
 * @author David Dinkevich
 */
public class VertexBuffer implements Iterable<Vertex> {
	private List<Vertex> vertices;
	private VertexNameBuffer charList;
	/**
	 * List of Polygons whose vertices are stored in this
	 * {@link VertexBuffer}. When we modify their vertices,
	 * we have to "let the polygons know" and update them.
	 */
	private List<Polygon> polygons;
		
	public VertexBuffer() {
		vertices = new ArrayList<>();
		charList = new VertexNameBuffer();
		polygons = new ArrayList<>();
	}
	
	private void removeChar(Vertex vert) {
		charList.removeChar(vert.getNameChar());
		updateVertexNames();
	}
	
	private void updateVertexNames() {
		for (int i = 0; i < vertices.size(); i++) {
			vertices.get(i).setName(charList.getChars().get(i));
		}
	}
	
	/**
	 * Ensure that the given vertex is contained in this list. If
	 * it's not, throw an {@link IllegalArgumentException}.
	 * @param v the vertex
	 */
	private void validateVertexParameter(Vertex v) {
		if (v == null || !vertices.contains(v))
			throw new IllegalArgumentException("Vertex not contained in buffer.");
	}
	
	/**
	 * Add a {@link Vertex} to this {@link VertexBuffer}
	 * @param vertex the new vertex
	 */
	public void addVertex(Vertex vertex) {
		vertices.add(vertex);
		// Assign a name to the new vertex
		final char newName = charList.getUnusedChar();
		charList.addChar(newName);
		vertex.setName(newName);
	}
	
	/**
	 * Add multiple {@link Vertex}es to this {@link VertexBuffer}
	 * @param verts the list of vertices
	 */
	public void addVertices(Collection<Vertex> verts) {
		for (Vertex vert : verts) {
			addVertex(vert);
		}
	}
	
	/**
	 * Add multiple {@link Vertex}es to this {@link VertexBuffer}
	 * @param verts the list of vertices
	 */
	public void addVertices(Vertex[] verts) {
		for (Vertex vert : verts) {
			addVertex(vert);
		}
	}
	
	/**
	 * Remove the {@link Vertex} at the given index
	 * @param index the index at which the {@link Vertex} will be removed
	 * @return the {@link Vertex} that was removed
	 */
	public Vertex removeVertex(int index) {
		Vertex vert = vertices.remove(index);
		removeChar(vert);
		return vert;
	}
	
	/**
	 * Remove the given {@link Vertex}
	 * @param vert the {@link Vertex} that will be removed
	 * @return whether or not the {@link Vertex} was successfully removed
	 */
	public boolean removeVertex(Vertex vert) {
		if (vertices.remove(vert)) {
			removeChar(vert);
			return true;
		}
		return false;
	}
	
	/**
	 * Remove each {@link Vertex} in the given list
	 * @param vertices the list of {@link Vertex}es
	 */
	public void removeVertices(Collection<Vertex> vertices) {
		for (Vertex vert : vertices) {
			removeVertex(vert);
		}
	}
	
	/**
	 * Remove each {@link Vertex} in the given array
	 * @param vertices the list of {@link Vertex}es
	 */
	public void removeVertices(Vertex[] vertices) {
		for (Vertex vert : vertices) {
			removeVertex(vert);
		}
	}
	
	/**
	 * Get the {@link Vertex} at the given index
	 * @param index the index at which the {@link Vertex}
	 * will be retrieved
	 */
	public Vertex getVertex(int index) {
		return vertices.get(index);
	}
	
	/**
	 * Add the {@link Polygon} to this {@link VertexBuffer}. The
	 * Polygon's vertices will be added to this {@link VertexBuffer},
	 * and when they are modified, the Polygon will be updated via the
	 * {@link Polygon#syncNameWithVertexNames()} method.
	 * @param poly the {@link Polygon}
	 */
	public void addPolygon(Polygon poly) {
		// Add the polygon to the list
		polygons.add(poly);
		// Add the polygon's vertices to the list
		addVertices(poly.getVertices());
		// Update all of the polygons' names
		updatePolygons();
	}
	
	/**
	 * Remove the {@link Polygon} from this {@link VertexBuffer}
	 * @param poly the {@link Polygon}
	 */
	public void removePolygon(Polygon poly) {
		// Remove the polygon from the list
		polygons.remove(poly);
		// Remove the polygon's vertices
		removeVertices(poly.getVertices());
		// Update all of the polygons' names
		updatePolygons();
	}
	
	/**
	 * For each {@link Polygon}, call the Polygon's
	 * {@link Polygon#syncNameWithVertexNames()} method
	 */
	private void updatePolygons() {
		for (Polygon poly : polygons) {
			poly.syncNameWithVertexNames();
		}
	}
	
	/**
	 * Sets the name of the given {@link Vertex} to the given new name, and
	 * updates the internal {@link VertexNameBuffer}.
	 * @param vertex the vertex whose name will be updated
	 * @param newName the new name of the vertex
	 * @return whether or not the operation was successful
	 */
	private boolean setVertexName(Vertex vertex, char newName) {
		final char CURR_NAME = vertex.getNameChar();
		final int CURR_NAME_INDEX = charList.indexOf(CURR_NAME);
		
		// If the given vertex is not contained
		if (CURR_NAME_INDEX < 0) {
			// Crash the program
			validateVertexParameter(null); // null --> guaranteed to fail
		}
		
		// Update the VertexNameBuffer
		if (charList.set(CURR_NAME_INDEX, newName)) {
			vertex.setName(newName);
			return true;
		}
		return false;
	}
	
	/**
	 * Demerge two vertices
	 * @param vertex the vertex to be demerged
	 * @return true if the operation was successful--if the given {@link Polygon} contains
	 * the vertexName, false otherwise.
	 */
	public boolean demergeVertices(Vertex vertex) {
		// Make sure that given vertex is in list
		validateVertexParameter(vertex);
		
		// If there is more than one vertex with the given name
		if (charList.getInstanceCount(vertex.getNameChar()) > 1) {
			// Generate new name
			final char newName = charList.getUnusedChar();
			// Get the index of the vertex's current name
			final int INDEX = charList.indexOf(vertex.getNameChar());
			// Change the vertex's name in the VertexNameBuffer
			charList.set(INDEX, newName);
			// Change the vertex's name
			vertex.setName(newName);
			return true;			
		}
		return false;
	}
	
	/**
	 * Update the name of the given vertex. If there is another vertex
	 * in this {@link VertexBuffer} that shares the same location as the given
	 * vertex but does not have the same name, their names will be set to the
	 * same character. If they share the same name but are not on top of each
	 * other, they will be given separate names.
	 * @param vertex the vertex to be updated.
	 * @param mergeIfNecessary whether or not to merge the vertices if they
	 * are on top of each other
	 * @return true if the name of the given vertex changed
	 */
	public boolean updateVertexName(Vertex vertex, boolean mergeIfNecessary) {
		if (mergeIfNecessary) {			
			for (Vertex vert : vertices) {
				// Don't want to analyze same vertex
				if (vert.equals(vertex)) {
					continue;
				}
				Vec2 vertexLoc = vertex.getCenter(true);
				final char VERTEX_NAME = vertex.getNameChar();
				Vec2 otherVertLoc = vert.getCenter(true);
				final char OTHER_VERT_NAME = vert.getNameChar();
				
				if (OTHER_VERT_NAME != VERTEX_NAME && otherVertLoc.equals(vertexLoc)) {
					// We found another vertex with the same loc as the given vertex
					// and with a different name
					final boolean modified = setVertexName(vertex, OTHER_VERT_NAME);
					updatePolygons();
					return modified;
				}
			}
			return false; // No vertices were modified
		} else {
			final boolean modified = demergeVertices(vertex);
			updatePolygons();
			return modified;
		}
	}
	
	@Override
	public Iterator<Vertex> iterator() {
		return vertices.iterator();
	}

	/**
	 * Get the number of vertices stored in this {@link VertexBuffer}
	 * @return the count
	 */
	public int getVertexCount() {
		return vertices.size();
	}
	
	private static class VertexNameBuffer implements Iterable<Character> {
//		private static final Comparator<Character> ALPHABET_COMPARATOR = new Comparator<Character>() {
//			@Override
//			public int compare(Character c1, Character c2) {
//				return c1.compareTo(c2);
//			}
//		};

		private List<Character> chars;
		
		public VertexNameBuffer() {
			chars = new ArrayList<>();
		}
		
		private boolean isCapitalLetter(char c) {
			return c >= 'A' && c <= 'Z';
		}
		
		@Override
		public String toString() {
			return chars.toString();
		}
		
		@Override
		public Iterator<Character> iterator() {
			return chars.iterator();
		}
		
		public boolean addChar(char c) {
			if (!isCapitalLetter(c))
				return false;
			return chars.add(c);
		}
		
		public char getUnusedChar() {
			for (char c = 65; c < 65 + 26; c++) {
				if (!contains(c)) {
					return c;
				}
			}
			return '\0';
		}
		
		public boolean removeChar(char c) {
			if (!isCapitalLetter(c))
				return false;
			return chars.remove((Character)c);
		}
		
		public boolean set(int index, char newName) {
			return chars.set(index, newName) != null;
		}
		
		public boolean contains(char c) {
			return chars.contains(c);
		}
		
		/**
		 * Get how many times the given char is contained in this
		 * {@link VertexNameBuffer}.
		 */
		public int getInstanceCount(char ch) {
			int instances = 0;
			for (char c : chars) {
				if (c == ch)
					++instances;
			}
			return instances;
		}
		
		public int indexOf(char c) {
			for (int i = 0; i < chars.size(); i++) {
				if (chars.get(i) == c)
					return i;
			}
			return -1;
		}
		
		public List<Character> getChars() {
			return Collections.unmodifiableList(chars);
		}
	}	
}
