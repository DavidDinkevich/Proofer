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
public class PolygonBuffer implements Iterable<Polygon> {
	private List<Polygon> pgons;
	private VertexNameBuffer charList;
		
	public PolygonBuffer() {
		pgons = new ArrayList<>();
		charList = new VertexNameBuffer();
	}
	
	private void removeChars(Polygon shape) {
		for (int i = 0; i < shape.getVertexCount(); i++) {
			charList.removeChar(shape.getVertexName(i));
		}
		updateVertexNames();
	}
	
	private void updateVertexNames() {
		int j = 0;
		for (Polygon shape : this) {
			for (int i = 0; i < shape.getVertexCount(); i++, j++) {
				shape.setVertexName(i, charList.getChars().get(j));
			}
		}
	}
	
	/**
	 * Get the index (in the char buffer) of the given char belonging to the given polygon.
	 * @param poly the polygon
	 * @param name the name of the vertex
	 * @return the index, or1 if the given poly doesn't contain the given char,
	 * or if this poly buffer does not contain the given polygon.
	 */
	private int getIndexOfCharInPoly(Polygon poly, char name) {
		int index = poly.getName().indexOf(name);
		if (index < 0)
			return1;
		
		boolean buffContainsPoly = false;
		for (Polygon p : this) {
			if (p.equals(poly)) {
				buffContainsPoly = true;
				break;
			}
			index += p.getVertexCount();
		}
		if (buffContainsPoly == false)
			return1;
		return index;
	}
	
	public void addPoly(Polygon shape) {
		pgons.add(shape);
		for (int i = 0; i < shape.getVertexCount(); i++) {
			final char newName = charList.getUnusedChar();
			charList.addChar(newName);
			shape.setVertexName(i, newName);
		}
	}
	
	public void addPolygons(Collection<Polygon> shapes) {
		for (Polygon shape : shapes) {
			addPoly(shape);
		}
	}
	
	public Polygon removePoly(int index) {
		Polygon shape = pgons.remove(index);
		removeChars(shape);
		return shape;
	}
	
	public boolean removePoly(Polygon shape) {
		if (pgons.remove(shape)) {
			removeChars(shape);
			return true;
		}
		return false;
	}
	
	public Polygon getPoly(int index) {
		return pgons.get(index);
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
	public boolean mergeVertices(Polygon poly, char currName, char newName) {
		final int INDEX = getIndexOfCharInPoly(poly, currName);
		if (INDEX < 0)
			return false;
		
		if (charList.set(INDEX, newName)) {
			poly.setVertexName(currName, newName);
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
	public boolean demergeVertices(Polygon p, char vertexName) {
		if (!pgons.contains(p) || !p.containsVertex(vertexName))
			return false;
		
		// If there is more than one vertex with the given name
		if (charList.getInstanceCount(vertexName) > 1) {
			final char newName = charList.getUnusedChar(); // Generate new name
			final int INDEX = getIndexOfCharInPoly(p, vertexName);
			charList.set(INDEX, newName); // Update char list
			p.setVertexName(vertexName, newName); // Update polygon
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
	public boolean updateVertexName(
			Polygon poly, char vertexName, boolean mergeIfNecessary) {
		// If given polygon doesn't contain given vertex name, return error
		if (!poly.containsVertex(vertexName))
			return false;
		
		Vec2 vertexLoc = poly.getVertexLoc(vertexName, true);
				
		if (mergeIfNecessary) {
			// See if any other vertex shares the given vertex's location
			boolean otherVertSharesLoc = false;
			char otherVertName = '\n';
			outer:
			for (Polygon p : pgons) {
				// For each OTHER vertex
				for (Vertex otherVert : p.getVertices()) {
					// Details
					Vec2 otherVertLoc = otherVert.getCenter(true);
					otherVertName = otherVert.getNameChar();
					// If the vertices overlap and have different names
					if (otherVertName != vertexName && otherVertLoc.equals(vertexLoc)) {
						otherVertSharesLoc = true;
						break outer;
					}
				}
			}
			
			// If no other vertex shares given vertex's location
			if (!otherVertSharesLoc)
				return true; // No need to merge anything
			
			// We found another vertex with the same loc as the given vertex
			// and with a different name
			mergeVertices(poly, vertexName, otherVertName);
		} else {
			demergeVertices(poly, vertexName);
		}
		return true;
	}
	
	@Override
	public Iterator<Polygon> iterator() {
		return pgons.iterator();
	}

	/**
	 * Get the total count of all of the vertices belonging to all of the {@link Polygon}
	 * in this {@link PolygonBuffer}.
	 * @return the count
	 */
	public int getVertexCount() {
		int result = 0;
		for (Polygon s : pgons) {
			result += s.getVertexCount();
		}
		return result;
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
		
		public List<Character> getChars() {
			return Collections.unmodifiableList(chars);
		}
	}	
}
