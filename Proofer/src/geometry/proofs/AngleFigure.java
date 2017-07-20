package geometry.proofs;

import java.util.Arrays;
import java.util.List;

import util.Utils;

public final class AngleFigure extends SimpleFigure {
	private VertexFigure vertex0;
	private VertexFigure vertex1;
	private VertexFigure vertex2;
	
	public AngleFigure(String name) {
		vertex0 = new VertexFigure();
		vertex1 = new VertexFigure();
		vertex2 = new VertexFigure();
		setName(name);
	}
	public AngleFigure() {
		this("\0\0\0");
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o) && o instanceof AngleFigure;
	}
	
	@Override
	public void setName(String name) {
		super.setName(Utils.mergeStringsAndEnsureCapacity(3, 3, getName(), name));
		vertex0.setName(getName().substring(0, 1));
		vertex1.setName(getNameShort());
		vertex2.setName(getName().substring(2));
	}
	
	public String getNameShort() {
		return String.valueOf(getName().charAt(1));
	}
	
	@Override
	public boolean isValidName(String name) {
		if (name.length() != 3)
			return false;
		if (!(getName().charAt(1) == name.charAt(1)))
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
	
	@Override
	public List<Figure> getChildren() {
		return Arrays.asList(vertex0, vertex1, vertex2);
	}
	@Override
	public Figure getChild(String name) {
		if (name.equals(vertex0.getName()))
			return vertex0;
		if (name.equals(vertex1.getName()))
			return vertex1;
		if (name.equals(vertex2.getName()))
			return vertex2;
		return null;
	}
}