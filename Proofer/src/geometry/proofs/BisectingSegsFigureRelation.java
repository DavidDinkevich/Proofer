package geometry.proofs;

import geometry.shapes.Segment;

public class BisectingSegsFigureRelation extends FigureRelation {

	private char intersectVert;
	
	public BisectingSegsFigureRelation(Segment fig0, Segment fig1, char intersectVert) {
		super(FigureRelationType.BISECTS, fig0, fig1);
		this.intersectVert = intersectVert;
	}

	@Override
	public boolean equals(Object o) {
		if (!super.equals(o))
			return false;
		if (!(o instanceof BisectingSegsFigureRelation))
			return false;
		BisectingSegsFigureRelation other = (BisectingSegsFigureRelation) o;
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
