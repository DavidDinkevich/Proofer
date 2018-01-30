package geometry.shapes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import geometry.Vec2;

/**
 * TODO: write description
 * @author David Dinkevich
 */
public class VertexBuffer implements Iterable<Vertex> {
	private List<Vertex> vertices;
	private VertexNameBuffer charList;
		
	public VertexBuffer() {
		vertices = new ArrayList<>();
		charList = new VertexNameBuffer();
	}
	
	private void removeChar(Vertex vert) {
		charList.removeChar(vert.getNameChar());
		updateVertexNames();
	}
	
	private void removeChars(List<Vertex> vertices) {
		for (int i = 0; i < vertices.size(); i++) {
			removeChar(vertices.get(i));
		}
	}
	
	private void updateVertexNames() {
		for (int i = 0; i < vertices.size(); i++) {
			vertices.get(i).setName(charList.getChars().get(i));
		}
	}
	
	public void addVertex(Vertex vertex) {
		vertices.add(vertex);
		// Assign a name to the new vertex
		final char newName = charList.getUnusedChar();
		charList.addChar(newName);
		vertex.setName(newName);
	}
	
	public void addVertices(Collection<Vertex> verts) {
		for (Vertex vert : verts) {
			addVertex(vert);
		}
	}
	
	public void addVertices(Vertex[] verts) {
		for (Vertex vert : verts) {
			addVertex(vert);
		}
	}
	
	public Vertex removeVertex(int index) {
		Vertex vert = vertices.remove(index);
		removeChar(vert);
		return vert;
	}
	
	public boolean removeVertex(Vertex vert) {
		if (vertices.remove(vert)) {
			removeChar(vert);
			return true;
		}
		return false;
	}
	
	public void removeVertices(Collection<Vertex> vertices) {
		for (Vertex vert : vertices) {
			removeVertex(vert);
		}
	}
	
	public void removeVertices(Vertex[] vertices) {
		for (Vertex vert : vertices) {
			removeVertex(vert);
		}
	}
	
	public Vertex getVertex(int index) {
		return vertices.get(index);
	}
	
	/**
	 * Merge two vertices. This will change the name of the vertex with name "currName"
	 * to the given "newName".
	 * @param poly the {@link Polygon} that contains the vertex with name "currName".
	 * @param currName the current name of the vertex (before merging)
	 * @param newName the name of the vertex that the vertex with name "currName"
	 * will be merged with. The "currName" vertex's name after the merge will be the "newName"
	 * @return true if the operation was successful--if the given {@link Polygon} contains
	 * the "currName", false otherwise.
	 */
	public boolean mergeVertices(Vertex vertex, char newName) {
		final char CURR_NAME = vertex.getNameChar();
		final int CURR_NAME_INDEX = charList.indexOf(CURR_NAME);
		
		if (charList.set(CURR_NAME_INDEX, newName)) {
			vertex.setName(newName);
			return true;
		}
		return false;
	}
	
	/**
	 * Demerge two vertices.
	 * @param p the polygon that the vertex to demerge is in
	 * @param vertexName the name of the vertex to be demerged
	 * @return true if the operation was successful--if the given {@link Polygon} contains
	 * the vertexName, false otherwise.
	 */
	public boolean demergeVertices(Vertex vertex) {
		if (!vertices.contains(vertex)) {
			throw new IllegalArgumentException("Vertex not contained in buffer.");
		}
		
		// If there is more than one vertex with the given name
		if (charList.getInstanceCount(vertex.getNameChar()) > 1) {
			final char newName = charList.getUnusedChar(); // Generate new name
			final int INDEX = charList.indexOf(vertex.getNameChar());
			charList.set(INDEX, newName); // Update char list
			vertex.setName(newName); // Update vertex
			return true;			
		}
		return false;
	}
	
	/**
	 * Update the name of the vertex in the given polygon. This is necessary
	 * when the vertex's location is changed: you might want to merge the vertex
	 * with another if it shares the other's location, or demerge it if it no longer
	 * shares the other's location.
	 * @param poly the polygon whose vertex will be updated
	 * @param vertexName the name of the vertex to be updated
	 * @param mergeIfNecessary whether or not to merge the given vertex with
	 * another IF it shares the location of another vertex.
	 * @return false if the given vertex name is not contained within the given
	 * polygon. true otherwise. 
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
					mergeVertices(vertex, OTHER_VERT_NAME);
				}
			}
		} else {
			demergeVertices(vertex);
		}
		return true;
	}
	
	@Override
	public Iterator<Vertex> iterator() {
		return vertices.iterator();
	}

	/**
	 * Get the total count of all of the vertices belonging to all of the {@link Polygon}
	 * in this {@link PolygonBuffer}.
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
