package geometry.shapes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import exceptions.CannotResizeObjectException;

import geometry.Dimension;
import geometry.Vec2;
import geometry.proofs.Figure;

/**
 * {@link SimplePolygon}s are shapes whose structures are defined by
 * {@link Vertex}es. These shapes must provide a way to retrieve
 * {@link Vertex}es by name (char) and by index.
 * @author David Dinkevich
 */
public class SimplePolygon extends AbstractShape2D implements Polygon, Iterable<Vertex> {
	protected List<Vertex> vertices;
	
	private List<Figure> children;
	private List<Segment> segments;
	private List<Angle> angles;
	
	private static int MIN_VERT_COUNT = 3;
	
	public SimplePolygon(String name) {
		// Ensure name is long enough to be valid
		if (name.length() < MIN_VERT_COUNT) {
			throw new IllegalArgumentException(tooFewVerticesMessage());
		}
		vertices = new ArrayList<>();
		for (int i = 0; i < name.length(); i++) {
			vertices.add(new Vertex(name.charAt(i)));
		}
		init();
		setName(name);
	}
	
	public SimplePolygon(int vertexNum) {
		this(createNullString(vertexNum));
	}
	
	public SimplePolygon(Collection<Vertex> verts) {
		// Ensure list of vertices is long enough to be valid
		if (verts.size() < MIN_VERT_COUNT) {
			throw new IllegalArgumentException(tooFewVerticesMessage());
		}
		vertices = new ArrayList<>(verts);
		init();
		syncNameWithVertexNames();
	}
	
	public SimplePolygon() {
		this("\0\0\0");
	}
	
	public SimplePolygon(SimplePolygon shape) {
		super(shape);
		// Copy array
		vertices = new ArrayList<>();
		for (Vertex v : shape) {
			// Copy individual vertices
			vertices.add(new Vertex(v));
		}
	}
	
	private void init() {
		setNameLengthRange(MIN_VERT_COUNT, -1, false);
	}
	
	private String tooFewVerticesMessage() {
		return "A polygon must have at least " + MIN_VERT_COUNT + " vertices.";
	}
	
	@Override
	public boolean equals(Object o) {
		if (!super.equals(o))
			return false;
		if (!(o instanceof SimplePolygon))
			return false;
		SimplePolygon p = (SimplePolygon)o;
		return vertices.equals(p.vertices);
	}
	
	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (vertices == null ? 0 : vertices.hashCode());
		return result;
	}
	
	private static String createNullString(int length) {
		StringBuilder b = new StringBuilder();
		b.setLength(length);
		return b.toString();
	}
	
	private void syncNameWithVertexNames() {
		StringBuilder b = new StringBuilder(vertices.size());
		for (int i = 0; i < vertices.size(); i++) {
			b.append(vertices.get(i).getNameChar());
		}
		setName(b.toString());
	}
	
	@Override
	public void setName(String name) {
		super.setName(name);
		// Update vertices names
		if (vertices != null) {
			for (int i = 0; i < vertices.size() && i < getName().length(); i++) {
				vertices.get(i).setName(getName().charAt(i));
			}
		}
	}
	
	@Override
	public void setCenter(Vec2 loc, boolean includeScale) {
		if (loc.equals(getCenter(includeScale)))
			return;
		Vec2 old = super.getCenter(includeScale);
		// Update the locations of the vertices
		Vec2 diff = Vec2.sub(loc, old);
		for (Vertex v : getVertices()) {
			v.setCenter(Vec2.add(v.getCenter(includeScale), diff), includeScale);
		}
		super.setCenter(loc, includeScale);
	}
	
	@Override
	public Vec2 getCenter(boolean includeScale) {
		// The center is the x and y average of the vertices
		Vec2.Mutable center = new Vec2.Mutable();
		for (Vertex v : vertices)
			center.add(v.getCenter(includeScale));
		center.div(getVertexCount());
		// Update the internal center variable
		super.setCenter(center, includeScale);
		return center;
	}
	
	@Override
	public void setScale(Vec2 scale, Vec2 dilationPoint) {
		if (scale.equals(getScale()) || dilationPoint.equals(getDilationPoint())) {
			return;
		}
		super.setScale(scale, dilationPoint);
		
		for (Vertex v : getVertices()) {
			v.setScale(getScale(), getDilationPoint());
		}
	}
	
	@Override
	public int getVertexCount() {
		return getName().length();
	}
	
	private Vertex getVertex(char name) {
		final int index = getName().indexOf(name);
		if (index < 0) {
			return null;
		}
		return vertices.get(index);
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
		return vertices.get(index).getCenter(includeScale);
	}
	
	@Override
	public boolean setVertexLoc(char vertexName, Vec2 newLoc, boolean includeScale) {
		if (!isResizeable())
			throw new CannotResizeObjectException();
		final int index = getName().indexOf(vertexName);
		if (index < 0)
			return false;
		setVertexLoc(index, newLoc, includeScale);
		return true;
	}
	
	@Override
	public void setVertexLoc(int index, Vec2 newLoc, boolean includeScale) {
		if (!isResizeable())
			throw new CannotResizeObjectException();
		Vertex v = vertices.get(index);
		v.setCenter(newLoc, includeScale);
	}
	
	@Override
	public Vertex[] getVertices() {
		return vertices.toArray(new Vertex[vertices.size()]);
	}
	
	/**
	 * Get a list containing all of the {@link Vertex}es whose name
	 * is contained within the given list of names.
	 * @param names the list of names of {@link Vertex}es that will be
	 * retrieved.
	 * @return a list of {@link Vertex}es whose names were contained
	 * within the given list of strings. 
	 */
	public List<Vertex> getVertices(List<Character> names) {
		List<Vertex> vs = new ArrayList<Vertex>();
		names:
		for (int i = 0; i < names.size(); i++) {
			final char name = names.get(i);
			for (int j = 0; j < vertices.size(); j++) {
				if (vertices.get(j).getNameChar() == name) {
					vs.add(vertices.get(j));
					continue names;
				}
			}
		}
		return vs;
	}
	
	@Override
	public boolean setVertexName(char vertexName, char newName) {
		Vertex v = getVertex(vertexName);
		if (v == null)
			return false;
		v.setName(newName);
		syncNameWithVertexNames();
		return true;
	}
	
	@Override
	public void setVertexName(int vertexIndex, char newName) {
		vertices.get(vertexIndex).setName(newName);
		syncNameWithVertexNames();
	}
	
	@Override
	public char getVertexName(int index) {
		return vertices.get(index).getNameChar();
	}
	
	@Override
	public Segment getSide(String name) {
		if (!Segment.isValidSegmentName(name))
			throw new IllegalArgumentException("Invalid segment name.");
		List<Vertex> vs = getVertices(Arrays.asList(name.charAt(0), name.charAt(1)));
		if (vs.size() < 2) {
			System.err.println("This Polygon does not contain "
					+ "at least two Vertices with the given names.");
			return null;
		}
		return new Segment(vs.get(0), vs.get(1));
	}
	
	@Override
	public Segment[] getSides() {
		if (segments == null) {
			segments = new ArrayList<>();
			final int NUM_SEGS = getVertexCount();
			// First three segs
			for (int i = 0; i < NUM_SEGS-1; i++) {
				final char c0 = getName().charAt(i);
				final char c1 = getName().charAt(i+1);
				segments.add(new Segment(getVertex(c0), getVertex(c1)));
			}
			// Last seg (the seg that seals the shape)
			segments.add(new Segment(
					getVertex(getName().charAt(0)),
					getVertex(getName().charAt(getName().length()-1)))
			);
		}
		return segments.toArray(new Segment[segments.size()]);
	}
	
	@Override
	public Angle getAngle(String name) {
		// Ensure the name is a valid angle name
		if (!Angle.isValidAngleName(name))
			throw new IllegalArgumentException("Invalid angle name.");
		// Get the vertices in this polygon that match those in the given angle name.
		List<Vertex> vertices = getVertices(
				Arrays.asList(name.charAt(0), name.charAt(1), name.charAt(2)));
		// Ensure that the angle exists in this polygon
		if (vertices.size() < 3) {
			System.err.println("This Polygon does not contain "
					+ "at least three Vertices with the given names.");
			return null;
		}
		return new Angle(vertices.subList(0, 3));
	}
	
	@Override
	public Angle[] getAngles() {
		if (angles == null) {
			final int NUM_ANGLES = getVertexCount();
			angles = new ArrayList<>();
			for (int i = 0; i < NUM_ANGLES-2; i++) {
				angles.add(new Angle(vertices.get(i), vertices.get(i+1), vertices.get(i+2)));
			}
			angles.add(new Angle(
					vertices.get(NUM_ANGLES-2), vertices.get(NUM_ANGLES-1), vertices.get(0)));
			angles.add(new Angle(
					vertices.get(NUM_ANGLES-1), vertices.get(0), vertices.get(1)));
		}
		return angles.toArray(new Angle[angles.size()]);
	}
	
	@Override
	public Vec2[] getVertexLocations() {
		Vec2[] locs = new Vec2[vertices.size()];
		for (int i = 0; i < locs.length; i++) {
			locs[i] = vertices.get(i).getCenter(true);
		}
		return locs;
	}
	
	@Override
	public Iterator<Vertex> iterator() {
		return vertices.iterator();
	}
	
	@Override
	public float getPerimeter() {
		float perim = 0;
		for (Segment seg : getSides()) {
			perim += seg.getLength(true);
		}
		return perim;
	}
	
	@Override
	public float getArea() {
		// TODO: how do I do this for an irregular polygon?
		throw new UnsupportedOperationException("This op. is not yet implemented.");
	}
	
	@Override
	public Rect getBoundaryRect() {
		float minX = 0, maxX = 0, minY = 0, maxY = 0;
		
		for (Vec2 vert : getVertexLocations()) {
			if (vert.getX() < minX)
				minX = vert.getX();
			if (vert.getX() > maxX)
				maxX = vert.getX();
			if (vert.getY() < minY)
				minY = vert.getY();
			if (vert.getY() > maxY)
				maxY = vert.getY();
		}
		Vec2 rectLoc = new Vec2(minX + (maxX-minX)/2, minY + (maxY-minY)/2);
		Dimension rectSize = new Dimension(maxX-minX, maxY-minY);
		return new Rect(rectLoc, rectSize);
	}

	@Override
	public List<Figure> getChildren() {
		if (children == null) {
			children = new ArrayList<>();
			children.addAll(Arrays.asList(getVertices()));
			children.addAll(Arrays.asList(getAngles()));
			children.addAll(Arrays.asList(getSides()));
		}
		return children;
	}
	

	@Override
	public Figure getChild(String name) {
		if (name.length() == 1)
			return getVertex(name.charAt(0)); // Convert name to char
		if (name.length() == 2)
			return getSide(name);
		if (name.length() == 3)
			return getAngle(name);
		return null;
	}
}
