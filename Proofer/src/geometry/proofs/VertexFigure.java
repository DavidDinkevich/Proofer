package geometry.proofs;

import java.util.Collections;
import java.util.List;

import util.Utils;

public final class VertexFigure extends SimpleFigure {
	public VertexFigure(String name) {
		setName(name);
	}
	
	public VertexFigure() {
		super("\0");
	}
	
	@Override
	public void setName(String name) {
		super.setName(Utils.mergeStringsAndEnsureCapacity(1, 1, getName(), name));
	}
	
	public char getNameChar() {
		return getName().charAt(0);
	}

	@Override
	public boolean isValidName(String name) {
		return getName().equals(name);
	}

	@Override
	public List<Figure> getChildren() {
		return Collections.<Figure>emptyList();
	}

	@Override
	public Figure getChild(String name) {
		return null;
	}
}
