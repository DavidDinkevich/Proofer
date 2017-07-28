package geometry.shapes;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import geometry.Vec2;
import geometry.proofs.Figure;

public class Segment extends AbstractShape {
	private Vertex[] vertices;
	
	public Segment(Vertex[] vertices) {
		if (vertices.length < 2) {
			throw new IllegalArgumentException("A segment must have 2 vertices.");
		}
		this.vertices = new Vertex[] { vertices[0], vertices[1] };
		setNameLengthRange(2, 2, false);
		syncNameWithVertexNames();
	}
	
	public Segment(Collection<Vertex> vertices) {
		this(vertices.toArray(new Vertex[2]));
	}
	
	public Segment(Vertex v0, Vertex v1) {
		this(new Vertex[] { v0, v1 });
	}
	
	public Segment(String name) {
		this(new Vertex(), new Vertex());
		setName(name);
	}
	
	public Segment() {
		this(new Vertex(), new Vertex());
	}
	
	public Segment(Segment other) {
		super(other);
		// TODO: copy internal vertices in other.vertices
		vertices = other.vertices;
		syncNameWithVertexNames();
	}
	
	private void syncNameWithVertexNames() {
		setName(vertices[0].getName() + vertices[1].getName());
	}
	
	/**
	 * Get whether the given string is a valid name for a {@link Segment}.
	 * @param name the name to be checked
	 * @return whether it is a valid name for a {@link Segment}.
	 */
	public static boolean isValidSegmentName(String name) {
		return name.length() == 2;
	}
	
	@Override
	public void setName(String name) {
		super.setName(name);
		// Update vertices names
		if (vertices != null) {
			for (int i = 0; i < vertices.length && i < getName().length(); i++) {
				vertices[i].setName(getName().charAt(i));
			}
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (!super.equals(o))
			return false;
		if (o instanceof Segment) {
			Segment seg = (Segment)o;
			return seg.vertices.equals(vertices);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + vertices.hashCode();
		return result;
	}
	
	@Override
	public boolean containsPoint(Vec2 point, boolean incorporateScale) {
		final float dist1 = Vec2.dist(
				incorporateScale ? vertices[0].getScaledCenter() : vertices[0].getCenter(), point);
		final float dist2 = Vec2.dist(
				incorporateScale ? vertices[1].getScaledCenter() : vertices[1].getCenter(), point);
		return dist1 + dist2 == getLength();
	}
	
	public float getLength() {
		return Vec2.dist(vertices[0].getScaledCenter(), vertices[1].getScaledCenter());
	}
	
	@Override
	public void setScaledCenter(Vec2 loc) {
		Vec2 offset = Vec2.sub(loc, getScaledCenter());
		vertices[0].setScaledCenter(Vec2.add(vertices[0].getScaledCenter(), offset));
		vertices[1].setScaledCenter(Vec2.add(vertices[1].getScaledCenter(), offset));
	}
	
	@Override
	public Vec2 getScaledCenter() {
		Vec2 point1 = vertices[0].getScaledCenter();
		Vec2 point2 = vertices[1].getScaledCenter();
		return new Vec2((point1.getX() + point2.getX())/2, (point1.getY() + point2.getY())/2);
	}
	
	@Override
	public Vec2 getCenter() {
//		updateCenter();
//		return super.getCenter();
		Vec2 point1 = vertices[0].getCenter();
		Vec2 point2 = vertices[1].getCenter();
		return new Vec2((point1.getX() + point2.getX())/2, (point1.getY() + point2.getY())/2);
	}
	
	@Override
	public void setCenter(Vec2 newLoc) {
		Vec2 old = getCenter();		
		if (newLoc.equals(old))
			return;
		// Update the locations of the vertices
		Vec2 diff = Vec2.sub(newLoc, old);
		for (Vertex v : getVertices()) {
			v.setCenter(Vec2.add(v.getCenter(), diff));
		}
		super.setCenter(newLoc);
	}
	
	public Vec2 getSlope() {
		return Vec2.sub(vertices[1].getScaledCenter(), vertices[0].getScaledCenter());
	}
	
	public boolean containsVertex(char name) {
		return getName().indexOf(name) > -1;
	}
	
	public Vertex[] getVertices() {
		return vertices;
	}
	
	/**
	 * Get the location of the {@link Vertex} at the given index.
	 * @param index the index of the location of the requested {@link Vertex}.
	 * @param includeScale whether or not to incorporate the scale into
	 * calculations.
	 * @return the {@link Vertex}'s location.
	 */
	public Vec2 getVertexLoc(int index, boolean includeScale) {
		return includeScale ? vertices[index].getScaledCenter()
				: vertices[index].getCenter();
	}
	
	/**
	 * Get the location of the {@link Vertex} with the given name.
	 * @param vertexName the location of the requested {@link Vertex}.
	 * @param includeScale whether or not to incorporate the scale into
	 * calculations.
	 * @return the {@link Vertex}'s location.
	 */
	public Vec2 getVertexLoc(char vertexName, boolean includeScale) {
		final int index = getName().indexOf(vertexName);
		if (index < 0)
			return null;
		return getVertexLoc(index, includeScale);
	}
	
	/**
	 * Get a list of the locations of the {@link Vertex}es.
	 * @return the locations as an array of {@link Vec2}s.
	 */
	public Vec2[] getVertexLocations() {
		return new Vec2[] { vertices[0].getScaledCenter(), vertices[1].getScaledCenter() };
	}
	
	private void updateCenter() {
		Vec2 point1 = vertices[0].getCenter();
		Vec2 point2 = vertices[1].getCenter();
		setCenter(new Vec2((point1.getX() + point2.getX())/2, (point1.getY() + point2.getY())/2));
	}
	
	/**
	 * Set the location of the {@link Vertex} at the given index.
	 * @param index the index of the {@link Vertex} to be relocated.
	 * @param includeScale whether or not to incorporate the scale into
	 * calculations.
	 * @param newLoc the new location of the {@link Vertex} at the given index.
	 */
	public void setVertexLoc(int index, Vec2 newLoc, boolean includeScale) {
//		if (!isResizeable())
//			throw new CannotResizeObjectException();
		Vertex v = vertices[index];
		if (includeScale)
			v.setScaledCenter(newLoc);
		else
			v.setCenter(newLoc);
		updateCenter(); // Update the center of this segment
	}
	
	/**
	 * Set the location of the {@link Vertex} with the given name.
	 * @param vertexName the name of the {@link Vertex} to be relocated.
	 * @param newLoc the new location of the {@link Vertex} with the given name.
	 * @param includeScale whether or not to incorporate the scale into
	 * calculations.
	 * @return true if a {@link Vertex} with the given name was found
	 * and its location was set. False otherwise.
	 */
	public boolean setVertexLoc(char vertexName, Vec2 newLoc, boolean includeScale) {
//		if (!isResizeable())
//			throw new CannotResizeObjectException();
		final int index = getName().indexOf(vertexName);
		if (index < 0)
			return false;
		setVertexLoc(index, newLoc, includeScale);
		return true;
	}
	
	/**
	 * Set the name of the {@link Vertex} at the given index.
	 * @param vertexIndex the index of the {@link Vertex} whose name
	 * will be changed.
	 * @param newName the new name to give to the {@link Vertex} at
	 * the given index.
	 */
	public void setVertexName(int vertexIndex, char newName) {
		vertices[vertexIndex].setName(newName);
		syncNameWithVertexNames();
	}

	/**
	 * Rename the {@link Vertex} with the given name to the given new name.
	 * @param currName the name of the {@link Vertex} that will be renamed.
	 * @param newName the new name of the {@link Vertex}.
	 */
	public boolean setVertexName(char currName, char newName) {
		final int index = getName().indexOf(currName);
		if (index < 0)
			return false;
		setVertexName(index, newName);
		return true;
	}
	
	/**
	 * Get the name of the {@link Vertex} at the given index.
	 * @param index the index of the vertex whose name will be retrieved.
	 * @return the name of the vertex at the given index.
	 */
	public char getVertexName(int index) {
		return vertices[index].getNameChar();
	}
	
	@Override
	public List<Figure> getChildren() {
		return Arrays.asList(vertices);
	}
	
	@Override
	public Figure getChild(String name) {
		for (Vertex v : vertices) {
			if (v.isValidName(name))
				return v;
		}
		return null;
	}
}
