package geometry.shapes;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import geometry.Vec2;
import geometry.proofs.Figure;

public class Segment extends SimplePolygon {
	public Segment(Collection<Vertex> vertices) {
		super(vertices);
		setNameLengthRange(-1, 2, false);
	}
	
	public Segment(Vertex v0, Vertex v1) {
		this(Arrays.asList(v0, v1));
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
	public boolean equals(Object o) {
		if (!super.equals(o))
			return false;
		return o instanceof Segment;
	}
	
	@Override
	public boolean containsPoint(Vec2 point, boolean incorporateScale) {
		final float dist1 = Vec2.dist(
				incorporateScale ? getVertex(0).getScaledCenter() : getVertex(0).getCenter(), point);
		final float dist2 = Vec2.dist(
				incorporateScale ? getVertex(1).getScaledCenter() : getVertex(1).getCenter(), point);
		return dist1 + dist2 == getLength();
	}
	
	public float getLength() {
		return Vec2.dist(getVertex(0).getScaledCenter(), getVertex(1).getScaledCenter());
	}
	
	@Override
	public void setScaledCenter(Vec2 loc) {
		Vec2 offset = Vec2.sub(loc, getScaledCenter());
		getVertex(0).setScaledCenter(Vec2.add(getVertex(0).getScaledCenter(), offset));
		getVertex(1).setScaledCenter(Vec2.add(getVertex(1).getScaledCenter(), offset));
	}
	
	@Override
	public Vec2 getScaledCenter() {
		Vec2 point1 = getVertex(0).getScaledCenter();
		Vec2 point2 = getVertex(1).getScaledCenter();
		return new Vec2((point1.getX() + point2.getX())/2, (point1.getY() + point2.getY())/2);
	}
	
	@Override
	public Vec2 getCenter() {
		updateCenter();
		return super.getCenter();
	}
	
	public Vec2 getSlope() {
		return Vec2.sub(getVertex(1).getScaledCenter(), getVertex(0).getScaledCenter());
	}
	
	@Override
	public boolean isValidName(String name) {
		if (name.length() != 2)
			return false;
		boolean contains = true;
		for (char c : name.toCharArray()) {
			if (getName().indexOf(c) > -1)
				continue;
			contains = false;
			break;
		}
		return contains;
	}
	
	public List<Vertex> getVertices() {
		return Arrays.asList(getVertex(0), getVertex(1));
	}
	
	@Override
	public List<Figure> getChildren() {
		return Arrays.asList(getVertex(0), getVertex(1));
	}
	
	@Override
	public Figure getChild(String name) {
		if (getVertex(0).isValidName(name))
			return getVertex(0);
		if (getVertex(1).isValidName(name))
			return getVertex(1);
		return null;
	}
	
	private void updateCenter() {
		Vec2 point1 = getVertex(0).getCenter();
		Vec2 point2 = getVertex(1).getCenter();
		setCenter(new Vec2((point1.getX() + point2.getX())/2, (point1.getY() + point2.getY())/2));
	}

	@Override
	public boolean setVertexLoc(char vertexName, Vec2 newLoc) {
		final boolean result = super.setVertexLoc(vertexName, newLoc);
		updateCenter();
		return result;
	}

	@Override
	public void setVertexLoc(int index, Vec2 newLoc) {
		super.setVertexLoc(index, newLoc);
		updateCenter();
	}
}
