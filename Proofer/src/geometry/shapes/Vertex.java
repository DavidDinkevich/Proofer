package geometry.shapes;

import java.util.Collections;
import java.util.List;

import geometry.Vec2;
import geometry.proofs.Figure;

/**
 * Represents a geometric vertex, with an identification label (char).
 * @author David Dinkevich
 */
public class Vertex extends AbstractShape {
	
	public Vertex(char name, Vec2 loc) {
		super(loc);
		setNameLengthRange(1, 1, false);
		setName(name);
	}
	
	public Vertex(char name) {
		this(name, new Vec2());
	}
	
	public Vertex(Vec2 loc) {
		this('\0', loc);
	}
	
	public Vertex() {
		this('\0');
	}
	
	public Vertex(Vertex shape) {
		super(shape);
	}
	
	public static boolean isValidVertexName(String s) {
		return s.length() == 1;
	}
	
	public void setName(char name) {
		setName(String.valueOf(name));
	}
	
	public char getNameChar() {
		return getName().charAt(0);
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o) && o instanceof Vertex;
	}

	@Override
	public List<Figure> getChildren() {
		return Collections.emptyList();
	}
	
	@Override
	public Figure getChild(String name) {
		return null;
	}
}
