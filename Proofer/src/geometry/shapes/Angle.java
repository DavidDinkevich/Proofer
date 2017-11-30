package geometry.shapes;

import java.util.Arrays;
import java.util.List;

import geometry.Measurements;
import geometry.Vec2;
import geometry.proofs.Figure;

import util.Utils;

public final class Angle extends AbstractShape {
	private Vertex[] vertices;
	
	public Angle(List<Vertex> vertices) {
		init();
		this.vertices = new Vertex[3];
		for (int i = 0; i < vertices.size() || i < 3; i++) {
			if (i >= vertices.size())
				this.vertices[i] = new Vertex();
			else
				this.vertices[i] = vertices.get(i);
		}
		setCenter(vertices.get(1).getCenter(false), false);
		syncNameWithVertexNames();
	}
	
	public Angle(Vertex[] vertices) {
		init();
		this.vertices = new Vertex[3];
		for (int i = 0; i < 3; i++) {
			if (i >= vertices.length)
				this.vertices[i] = new Vertex();
			else
				this.vertices[i] = vertices[i];
		}
		setCenter(vertices[1].getCenter(false), false);
		syncNameWithVertexNames();
	}
	
	public Angle(Vertex v0, Vertex v1, Vertex v2) {
		this(new Vertex[] { v0, v1, v2 });
	}
	
	public Angle(String name) {
		init();
		vertices = new Vertex[3];
		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = new Vertex();
		}
		setCenter(vertices[1].getCenter(false), false);
		setName(Utils.mergeStringsAndEnsureCapacity(3, 3, getName(), name));
	}
	
	public Angle(Segment a, Segment b) {
		this(Utils.getAngleBetween(a, b));
	}
	
	public Angle() {
		this("\0\0\0");
	}
	
	public Angle(Angle other) {
		vertices = Arrays.copyOf(other.vertices, other.vertices.length);
		setName(other.getName());
		setCenter(other.vertices[1].getCenter(false), false);
	}
	
	private void syncNameWithVertexNames() {
		StringBuilder b = new StringBuilder(vertices.length);
		for (int i = 0; i < vertices.length; i++) {
			b.append(vertices[i].getNameChar());
		}
		setName(b.toString());
	}
	
	private void init() {
		setNameLengthRange(3, 3, false);
	}
	
	/**
	 * Get whether the given string is a valid name for an {@link Angle}.
	 * @param name the name to be checked
	 * @return whether it is a valid name for an {@link Angle}.
	 */
	public static boolean isValidAngleName(String name) {
		return name.length() == 3;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Angle))
			return false;
		Angle a = (Angle)o;
		return isValidName(a.getName());
	}
	
//	@Override
//	public int hashCode() {
//		int result = 17;
//		result = 31 * result + vertices.hashCode();
//		return result;
//	}
	
	@Override
	public boolean containsPoint(Vec2 point, boolean toScale) {
		// Derive an Arc from this Angle and use its containsPoint()
		return Utils.getArc(this).containsPoint(point, toScale);
	}
	
	/**
	 * Get the measurement of this angle.
	 * @param mes the unit of measurement
	 * @return the measurement
	 */
	public float getAngle(Measurements mes) {
		Vec2 side0 = Vec2.sub(
				vertices[0].getCenter(false), vertices[1].getCenter(false));
		Vec2 side1 = Vec2.sub(
				vertices[2].getCenter(false), vertices[1].getCenter(false));
		final float angle = Vec2.angleBetween(side0, side1);
		return mes == Measurements.RADIANS ? angle : Utils.radiansToDegrees(angle);
	}
	
	/**
	 * Get the measurement of this angle in RADIANS.
	 * @return the measurement of this angle in RADIANS
	 */
	public float getAngle() {
		return getAngle(Measurements.RADIANS);
	}
	
	@Override
	public void setName(String name) {
		super.setName(Utils.mergeStringsAndEnsureCapacity(3, 3, getName(), name));
		// Name will definitely be 3 chars long because we ensured that capacity above
		for (int i = 0; i < vertices.length; i++) {
			vertices[i].setName(getName().charAt(i));
		}
	}
	
	@Override
	public String toString() {
		return Utils.ANGLE_SYMBOL + getName();
	}
	
	@Override
	public boolean isValidName(String name) {
		return super.isValidName(name) && 
				getNameShort().charAt(0) == name.charAt(1);
	}
	
	public String getNameShort() {
		return String.valueOf(getName().charAt(1));
	}

	/**
	 * Get the 2 sides of this {@link Angle}.
	 */
	public Segment[] getSides() {
		return new Segment[] {
				new Segment(vertices[1], vertices[0]),
				new Segment(vertices[1], vertices[2])
		};
	}
	
	/**
	 * Convenient way of getting the vertices of this {@link Angle}.
	 * @return the vertices of this {@link Angle}
	 */
	public List<Vertex> getVertices() {
		return Arrays.asList(vertices);
	}
	
	public List<Figure> getChildren() {
		return Arrays.asList(vertices);
	}

	@Override
	public Figure getChild(String name) {
		for (Vertex vertex : vertices) {
			if (vertex.isValidName(name))
				return vertex;
		}
		return null;
	}
}
