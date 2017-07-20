package geometry.proofs;

public abstract class SimpleFigure implements Figure {
	private String name;
//	private List<Figure> children;
	
	public SimpleFigure(String name) {
		this.name = name.toUpperCase();
//		children = new ArrayList<>();
	}
	public SimpleFigure() {
		this("");
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o instanceof SimpleFigure)
			return isValidName(((SimpleFigure)o).getName());
		return false;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + name.hashCode();
		return result;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name.toUpperCase();
	}
}
