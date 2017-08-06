package geometry.shapes;

import java.util.Arrays;
import java.util.List;

import geometry.Measurements;
import geometry.Vec2;
import geometry.proofs.Figure;

import util.Utils;

public final class Angle implements Figure {
	private Vertex[] vertices;
	private String name = "";
	
	public Angle(List<Vertex> vertices) {
		this.vertices = new Vertex[3];
		for (int i = 0; i < vertices.size() || i < 3; i++) {
			if (i >= vertices.size())
				this.vertices[i] = new Vertex();
			else
				this.vertices[i] = vertices.get(i);
		}
		syncNameWithVertexNames();
	}
	
	public Angle(Vertex[] vertices) {
		this.vertices = new Vertex[3];
		for (int i = 0; i < vertices.length || i < 3; i++) {
			if (i >= vertices.length)
				this.vertices[i] = new Vertex();
			else
				this.vertices[i] = vertices[i];
		}
		syncNameWithVertexNames();
	}
	
	public Angle(Vertex v0, Vertex v1, Vertex v2) {
		this(new Vertex[] { v0, v1, v2 });
	}
	
	public Angle(String name) {
		this.name = Utils.mergeStringsAndEnsureCapacity(3, 3, this.name, name);
		vertices = new Vertex[3];
		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = new Vertex();
		}
	}
	
	public Angle(Segment a, Segment b) {
		this(makeAngle(a, b));
	}
	
	public Angle() {
		this("\0\0\0");
	}
	
	public Angle(Angle other) {
		name = other.getName();
		vertices = Arrays.copyOf(other.vertices, other.vertices.length);
	}
	
	private static List<Vertex> makeAngle(Segment a, Segment b) {
		String name = Utils.getAngleBetween(a, b);
		if (name == null)
			throw new NullPointerException();
		
		Vertex shared = (Vertex)a.getChild(name.substring(1, 2));
		Vertex unshared0, unshared1;
		String unshared0Name = name.substring(0, 1);
		String unshared1Name = name.substring(2);
		
		if (a.getChild(unshared0Name) == null) {
			unshared0 = (Vertex)b.getChild(unshared0Name);
			unshared1 = (Vertex)a.getChild(unshared1Name);
		} else {
			unshared1 = (Vertex)b.getChild(unshared1Name);
			unshared0 = (Vertex)a.getChild(unshared0Name);
		}
		return Arrays.asList(unshared0, shared, unshared1);
	}
	
	private void syncNameWithVertexNames() {
		StringBuilder b = new StringBuilder(vertices.length);
		for (int i = 0; i < vertices.length; i++) {
			b.append(vertices[i].getNameChar());
		}
		name = b.toString();
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
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + name.hashCode();
//		result = 31 * result + vertices.hashCode();
		return result;
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
		this.name = Utils.mergeStringsAndEnsureCapacity(3, 3, this.name, name);
		// Name will definitely be 3 chars long because we ensured that capacity above
		for (int i = 0; i < vertices.length; i++) {
			vertices[i].setName(getName().charAt(i));
		}
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public boolean isValidName(String name) {
		return Figure.super.isValidName(name) && 
				getNameShort().charAt(0) == name.charAt(1);
	}
	
	public String getNameShort() {
		return String.valueOf(getName().charAt(1));
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
