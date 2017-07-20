package geometry.shapes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import exceptions.CannotResizeObjectException;
import geometry.Dimension;
import geometry.Vec2;
import geometry.proofs.Figure;

public class Rect extends RectEllipse implements Polygon {
	private Vertex[] vertices;
	// Buffer for storing the vertices in a List<Vertex> format
	private List<Vertex> verticesListBuff;
	// Buffer for storing the locations of the vertices
	private Vec2[] vertexLocs;
	// Buffer for storing the sides of this rect (Segments)
	private Segment[] segs;
	// Buffer for storing the angles of this rect (Angles)
	private Angle[] angles;
	
	public Rect(Vec2 loc, Dimension size) {
		super(loc, size);
		init();
	}
	
	public Rect(Vec2 loc) {
		super(loc, Dimension.ZERO);
		init();
	}
	
	public Rect(Dimension size) {
		super(size);
		init();
	}
	
	public Rect(String name) {
		this(Dimension.ZERO);
		init();
		setName(name);
	}
	
	public Rect() {
		init();
	}
	
	public Rect(RectEllipse other) {
		super(other);
		setName(other.getName()); // This will correct the name by making sure it has 4 chars.
	}
	
	private void init() {
		setNameLengthRange(4, 4, true);
	}
	
	// Get the vertices in an array format, instead of a list.
	// Instantiate the array if necessary.
	private Vertex[] getVerticesArray() {
		if (vertices == null) {
			vertices = new Vertex[getVertexCount()];
		}
		return vertices;
	}
	
	private Vec2 calculateVertexLocation(int vertIndex, boolean includeScale) {
		Dimension s = getSize();
		Vec2 center = includeScale ? getScaledCenter() : getCenter();
		Vec2 loc;
		
		switch (vertIndex) {
		case 0:
			loc = new Vec2(
					center.getX() - s.getWidth()/2f, center.getY() - s.getHeight()/2f);
			break;
		case 1:
			loc = new Vec2(
					center.getX() + s.getWidth()/2f, center.getY() - s.getHeight()/2f);
			break;
		case 2:
			loc = new Vec2(
					center.getX() + s.getWidth()/2f, center.getY() + s.getHeight()/2f);
			break;
		case 3:
			loc = new Vec2(
					center.getX() - s.getWidth()/2f, center.getY() + s.getHeight()/2f);
			break;
		default: // Should never occur
			throw new IndexOutOfBoundsException("Invalid index: " + vertIndex);
		}
		return loc;
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o) && o instanceof Rect;
	}
	
	/**
	 * Returns this {@link Rect}. Any changes performed on the boundary
	 * rect will affect the original.
	 */
	@Override
	public Rect getBoundaryRect() {
		return this;
	}
	
	/**
	 * Determines whether the two given {@link Rect}s are overlapping.
	 * @param r1 the first rectangle
	 * @param r2 the second rectangle
	 * @param incorporateScale whether or not to incorporate the rects' scale
	 * @return true if the rects overlap, false otherwise.
	 */
	public static boolean rectsOverlap(Rect r1, Rect r2, boolean incorporateScale) {
		Vec2 loc1 = incorporateScale ? r1.getScaledCenter() : r1.getCenter();
		Vec2 loc2 = incorporateScale ? r2.getScaledCenter() : r2.getCenter();
		Dimension size1 = incorporateScale ? r1.getSizeIncludeScale() : r1.getSize();
		Dimension size2 = incorporateScale ? r2.getSizeIncludeScale() : r2.getSize();
		
		return loc1.getX() + size1.getWidth()/2 > loc2.getX() - size2.getWidth()/2
				&& loc1.getX() - size1.getWidth()/2 < loc2.getX() + size2.getWidth()/2
				&& loc1.getY() + size1.getHeight()/2 > loc2.getY() - size2.getHeight()/2
				&& loc1.getY() - size1.getHeight()/2 < loc2.getY() + size2.getHeight()/2;
	}

	@Override
	public float getArea() {
		return getSizeIncludeScale().getWidth() * getSizeIncludeScale().getHeight();
	}

	@Override
	public float getPerimeter() {
		return (getSizeIncludeScale().getWidth()*2) + (getSizeIncludeScale().getHeight()*2);
	}
	
	@Override
	public boolean containsPoint(Vec2 point, boolean incorporateScale) {
		Dimension size = incorporateScale ? getSizeIncludeScale() : getSize();
		Vec2 loc = incorporateScale ? getScaledCenter() : getCenter();
		return point.getX() > loc.getX() - size.getWidth()/2f && point.getX() < loc.getX() + size.getWidth()/2f 
				&& point.getY() > loc.getY() - size.getHeight()/2f && point.getY() < loc.getY() + 
				size.getHeight()/2f;
	}
	
	@Override
	public int getVertexCount() {
		return 4; // Rectangles have four corners...
	}

	/**
	 * Get the {@link Vertex} at the given index. NOTE:
	 * The index of a Vertex on a Rect must be 0, 1, 2, or 3.
	 * Anything else will result in an {@link IllegalArgumentException} being thrown.
	 * @param index the index of the requested {@link Vertex}, either 0, 1, 2, or 3.
	 * @return the {@link Vertex}.
	 * @throws IllegalArgumentException if the given index is not 0, 1, 2, or 3.
	 */
	private Vertex getVertex(int index) {
		if (index < 0 || index > 3) {
			throw new IllegalArgumentException(
					"The index of a Vertex on a Rect must be 0, 1, 2, or 3");
		}
		
		// Create the vertex if it has not been created already
		if (getVerticesArray()[index] == null) {
			Vertex v = new Vertex(getName().charAt(index));
			getVerticesArray()[index] = v;
		}
		
		// Make the location/scale of the vertex is updated
		getVerticesArray()[index].setScale(getScale());
		Vec2 loc = calculateVertexLocation(index, false);			
		getVerticesArray()[index].setCenter(loc);
		
		return getVerticesArray()[index];
	}
	
	/**
	 * Set the location of the {@link Vertex} with the given name. This will
	 * resize and relocate this {@link Rect}.
	 * @param vertexName the name of the {@link Vertex} to be relocated.
	 * @param newLoc the new location of the {@link Vertex} with the given name.
	 * @return true if a {@link Vertex} with the given name was found
	 * and its location was set. False otherwise.
	 */
	@Override
	public boolean setVertexLoc(char vertexName, Vec2 newLoc, boolean includeScale) {
		if (!isResizeable())
			throw new CannotResizeObjectException();
		// Get the index of the given vertex
		final int vertIndex = getName().indexOf(vertexName);
		if (vertIndex < 0) {
			return false;
		}
		setVertexLoc(vertIndex, newLoc, includeScale);
		return true;
	}
	
	/**
	 * Set the location of the {@link Vertex} at the given index. This will
	 * resize and relocate this {@link Rect}.
	 * @param index the index of the {@link Vertex} to be relocated.
	 * @param newLoc the new location of the {@link Vertex} at the given index.
	 */
	@Override
	public void setVertexLoc(int index, Vec2 newLoc, boolean includeScale) {
		if (!isResizeable())
			throw new CannotResizeObjectException();
		if (index < 0 || index > 3) {
			throw new IllegalArgumentException("The index of a Vertex on a Rect must be 0, 1, 2, or 3");
		}
		
		// Get the index of the vertex opposite to the given one (farthest away).
		int oppIndex;
		switch (index) {
		case 0: oppIndex = 2; break;
		case 1: oppIndex = 3; break;
		case 2: oppIndex = 0; break;
		default: oppIndex = 1; break; // Case 3:
		}
		
		Vertex opp = getVertex(oppIndex);
		Vec2 oppLoc = includeScale ? opp.getScaledCenter() : opp.getScaledCenter();
		
		Vec2 loc = new Vec2(newLoc.getX() - (newLoc.getX()-oppLoc.getX())/2,
				newLoc.getY() - (newLoc.getY()-oppLoc.getY())/2);
		// Set the scale
		if (includeScale)
			setScaledCenter(loc);
		else
			setCenter(loc);
		// Update the size of the rect
		setSize(oppLoc.getX()-newLoc.getX(), newLoc.getY()-oppLoc.getY());
	}
	
	@Override
	public Vec2 getVertexLoc(char vertexName, boolean includeScale) {
		final int index = getName().indexOf(vertexName);
		if (index < 0)
			return null;
		return getVertexLoc(index, includeScale);
	}

	@Override
	public Vec2 getVertexLoc(int index, boolean includeScale) {
		return includeScale ? getVertex(index).getScaledCenter()
				: getVertex(index).getCenter();
	}

	@Override
	public char getVertexName(int index) {
		return getName().charAt(index);
	}
	
	/**
	 * Set the name of the {@link Vertex} at the given index.
	 * @param vertexIndex the index of the {@link Vertex} whose name
	 * will be changed.
	 * @param newName the new name to give to the {@link Vertex} at
	 * the given index.
	 * @throws IllegalArgumentException if the given index is not 0, 1, 2, or 3.
	 */
	@Override
	public void setVertexName(int vertexIndex, char newName) {
		if (vertexIndex < 0 || vertexIndex > 3) {
			throw new IllegalArgumentException("The index of a Vertex on a Rect must be 0, 1, 2, or 3");
		}
		Vertex v = getVertex(vertexIndex);
		if (v.getNameChar() != newName) {
			v.setName(newName);
			// Rename this rect
			StringBuilder build = new StringBuilder(getName());
			build.setCharAt(vertexIndex, newName);
			setName(build.toString());
		}
	}
	
	@Override
	public boolean setVertexName(char currName, char newName) {
		final int index = getName().indexOf(currName);
		if (index < 0) {
			return false;
		}
		setVertexName(index, newName);
		return true;
	}

	@Override
	public List<Vertex> getVertices() {
		if (verticesListBuff == null) {
			verticesListBuff = new ArrayList<>(4);
			for (int i = 0; i < getVertexCount(); i++) {
				verticesListBuff.add(getVertex(i));
			}
		}
		return verticesListBuff;
	}

	@Override
	public Vec2[] getVertexLocations() {
		if (vertexLocs == null) {
			vertexLocs = new Vec2[] {
					getVerticesArray()[0].getScaledCenter(),
					getVerticesArray()[1].getScaledCenter(),
					getVerticesArray()[2].getScaledCenter(),
					getVerticesArray()[3].getScaledCenter()
			};
		}
		return vertexLocs;
	}
	
	@Override
	public Segment getSide(String name) {
		return null;
	}
	
	@Override
	public List<Segment> getSides() {
		if (segs == null) {
			segs = new Segment[getVertexCount()];
			segs[0] = new Segment(getVertex(0), getVertex(1));
			segs[1] = new Segment(getVertex(1), getVertex(2));
			segs[2] = new Segment(getVertex(2), getVertex(3));
			segs[3] = new Segment(getVertex(3), getVertex(0));
		}
		return segs;
	}
	
	@Override
	public Angle getAngle(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Angle> getAngles() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Figure> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Figure getChild(String name) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isValidName(String name) {
		// TODO Auto-generated method stub
		return false;
	}
}
