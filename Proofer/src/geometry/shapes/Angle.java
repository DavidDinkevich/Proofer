package geometry.shapes;

import java.util.Arrays;
import java.util.List;

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
	
	public Angle() {
		this("\0\0\0");
	}
	
	public Angle(Angle other) {
		name = other.getName();
		vertices = Arrays.copyOf(other.vertices, other.vertices.length);
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
		return isValidName(a.getName()) && vertices.equals(a.vertices);
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + name.hashCode();
		result = 31 * result + vertices.hashCode();
		return result;
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
