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
		vertices = new Vertex[2];
		// Copy internal vertices
		for (int i = 0; i < 2; i++) {
			vertices[i] = new Vertex(other.vertices[i]);
		}
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
	
	/**
	 * Get the point (as a {@link Vec2}) at which the following two
	 * {@link Segment}s intersect.
	 * @param a the first segment
	 * @param b the second segment
	 * @return the point at which the two segments meet
	 */
	public static Vec2 getPointOfIntersection(Segment a, Segment b) {
		Slope slopeA = a.getSlope();
		Slope slopeB = b.getSlope();
		
		// If slopes are equal, there is either no point of intersection,
		// or lines lie on top of each other!
		if (slopeA.getSlope().equals(slopeB.getSlope())) {
			return null;
		}
		
		// If one of the lines is vertical, and the other is horizontal,
		// special way for calculating poi
		if (slopeA.isHorizontalOrVertical() && slopeB.isHorizontalOrVertical()) {
			final boolean aVertical = slopeA.isVertical();
			Vec2 endPointA = a.getVertexLoc(0);
			Vec2 endPointB = b.getVertexLoc(0);
			return aVertical ? new Vec2(endPointA.getX(), endPointB.getY()) : 
				new Vec2(endPointB.getX(), endPointA.getY());
		}
		
		final float slopeARaw = slopeA.getSlopeRaw();
		final float slopeBRaw = slopeB.getSlopeRaw();
		final float yIntA = a.getYIntercept();
		final float yIntB = b.getYIntercept();
		
		final float x = (yIntB - yIntA) / (slopeARaw - slopeBRaw);
	    final float y = slopeARaw * x + yIntA;
		return new Vec2(x, y);
	}
	
//	public static void main(String[] args) {
//		Vec2 scale = new Vec2(2, 2);
//		Segment seg0 = new Segment(new Vertex(new Vec2(-3, 2)), new Vertex(new Vec2(7, 2)));
//		seg0.setScale(scale);
//		Segment seg1 = new Segment(new Vertex(), new Vertex(new Vec2(3, 3)));
////		Segment seg1 = new Segment(new Vertex(new Vec2(4, 5)), new Vertex(new Vec2(4, -2)));
//		seg1.setScale(scale);
//		
//		System.out.println(Segment.getPointOfIntersection(seg0, seg1, true));
//	}
	
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
	
//	@Override
//	public boolean equals(Object o) {
//		if (!super.equals(o))
//			return false;
//		if (o instanceof Segment) {
//			Segment seg = (Segment)o;
//			return seg.vertices.equals(vertices);
//		}
//		return false;
//	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o) && o instanceof Segment;
	}
	
	@Override
	public int hashCode() {
		int result = super.hashCode();
//		result = 31 * result + vertices.hashCode();
		return result;
	}
	
	@Override
	public boolean containsPoint(Vec2 point) {
		final float dist1 = Vec2.dist(
				vertices[0].getCenter(), point);
		final float dist2 = Vec2.dist(
				vertices[1].getCenter(), point);
		return dist1 + dist2 == getLength();
	}
		
	public float getLength() {
		return Vec2.dist(vertices[0].getCenter(),
				vertices[1].getCenter());
	}
	
	@Override
	public Vec2 getCenter() {
		Vec2 point1 = vertices[0].getCenter();
		Vec2 point2 = vertices[1].getCenter();
		return new Vec2((point1.getX() + point2.getX())/2, 
				(point1.getY() + point2.getY())/2);
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
	
	public Slope getSlope() {
		return new Slope(
				Vec2.sub(vertices[1].getCenter(), vertices[0].getCenter()));
	}
	
	public float getYIntercept() {
		Vec2 point = vertices[0].getCenter();
		Slope slope = getSlope();
		final float slopeNum = slope.getSlopeRaw();
		final float yInt = point.getY() - (slopeNum * point.getX());
		return yInt;
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
	 * @return the {@link Vertex}'s location.
	 */
	public Vec2 getVertexLoc(int index) {
		return vertices[index].getCenter();
	}
	
	/**
	 * Get the location of the {@link Vertex} with the given name.
	 * @param vertexName the location of the requested {@link Vertex}.
	 * @return the {@link Vertex}'s location.
	 */
	public Vec2 getVertexLoc(char vertexName) {
		final int index = getName().indexOf(vertexName);
		if (index < 0)
			return null;
		return getVertexLoc(index);
	}
	
	/**
	 * Get a list of the locations of the {@link Vertex}es.
	 * @return the locations as an array of {@link Vec2}s.
	 */
	public Vec2[] getVertexLocations() {
		return new Vec2[] { vertices[0].getCenter(), vertices[1].getCenter() };
	}
	
	private void updateCenter() {
		Vec2 point1 = vertices[0].getCenter();
		Vec2 point2 = vertices[1].getCenter();
		setCenter(new Vec2((point1.getX() + point2.getX())/2, 
				(point1.getY() + point2.getY())/2));
	}
	
	/**
	 * Set the location of the {@link Vertex} at the given index.
	 * @param index the index of the {@link Vertex} to be relocated.
	 * @param newLoc the new location of the {@link Vertex} at the given index.
	 */
	public void setVertexLoc(int index, Vec2 newLoc) {
//		if (!isResizeable())
//			throw new CannotResizeObjectException();
		// TODO: why is the above check commented out? Check all.
		Vertex v = vertices[index];
		v.setCenter(newLoc);
		
		updateCenter(); // Update the center of this segment
	}
	
	/**
	 * Set the location of the {@link Vertex} with the given name.
	 * @param vertexName the name of the {@link Vertex} to be relocated.
	 * @param newLoc the new location of the {@link Vertex} with the given name.
	 * @return true if a {@link Vertex} with the given name was found
	 * and its location was set. False otherwise.
	 */
	public boolean setVertexLoc(char vertexName, Vec2 newLoc) {
//		if (!isResizeable())
//			throw new CannotResizeObjectException();
		final int index = getName().indexOf(vertexName);
		if (index < 0)
			return false;
		setVertexLoc(index, newLoc);
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
	
	public static class Slope {
		private Vec2 slope;
		
		public Slope(Vec2 slope) {
			this.slope = slope;
		}
		
		public Slope() {
			this(new Vec2(1, 1));
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == this)
				return true;
			if (!(o instanceof Slope))
				return false;
			return getSlopeRaw() == ((Slope)o).getSlopeRaw();
		}
		
		@Override
		public int hashCode() {
			int result = 17;
			result = 31 * result + slope.hashCode();
			return result;
		}
		
		@Override
		public String toString() {
			return slope.toString();
		}
		
		public Vec2 getSlope() {
			return slope;
		}
		
		public float getSlopeX() {
			return slope.getX();
		}
		
		public float getSlopeY() {
			return slope.getY();
		}
		
		public float getSlopeRaw() {
			return slope.getY() / slope.getX();
		}
		
		public boolean isHorizontal() {
			return slope.getY() == 0;
		}
		
		public boolean isVertical() {
			return slope.getX() == 0;
		}
		
		public boolean isHorizontalOrVertical() {
			return isHorizontal() || isVertical();
		}
	}	
}
