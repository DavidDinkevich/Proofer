package geometry.proofs;

import java.util.Arrays;
import java.util.List;

import util.Utils;

public final class SegmentFigure extends SimpleFigure {
	private VertexFigure vertex0;
	private VertexFigure vertex1;
	
	public SegmentFigure(String name) {
		vertex0 = new VertexFigure();
		vertex1 = new VertexFigure();
		setName(name);
	}
	public SegmentFigure() {
		this("\0\0");
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o) && o instanceof SegmentFigure;
	}

	@Override
	public void setName(String name) {
		super.setName(Utils.mergeStringsAndEnsureCapacity(2, 2, getName(), name));
		vertex0.setName(getName().substring(0, 1));
		vertex1.setName(getName().substring(1));
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
	
	@Override
	public List<Figure> getChildren() {
		return Arrays.asList(vertex0, vertex1);
	}
	
	@Override
	public Figure getChild(String name) {
		if (name.equals(vertex0.getName()))
			return vertex0;
		if (name.equals(vertex1.getName()))
			return vertex1;
		return null;
	}
}