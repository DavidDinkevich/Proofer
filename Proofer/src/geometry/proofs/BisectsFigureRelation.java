package geometry.proofs;

import geometry.shapes.Segment;

public class BisectsFigureRelation extends FigureRelation {

	private char intersectVert;
	
	public BisectsFigureRelation(Segment fig0, Segment fig1, 
			FigureRelation parent, char intersectVert) {
		
		super(FigureRelationType.BISECTS, fig0, fig1, parent);
		this.intersectVert = intersectVert;
	}

	@Override
	public boolean equals(Object o) {
		if (!super.equals(o))
			return false;
		if (!(o instanceof BisectsFigureRelation))
			return false;
		BisectsFigureRelation other = (BisectsFigureRelation) o;
		return other.intersectVert == intersectVert;
	}
	
	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + Character.hashCode(intersectVert);
		return result;
	}
	
	public char getIntersectVert() {
		return intersectVert;
	}
	
}
