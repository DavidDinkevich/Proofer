package geometry.proofs;

import geometry.shapes.Segment;

public class PerpendicularFigureRelation extends FigureRelation {

	private char intersectVert;
	
	public PerpendicularFigureRelation(Segment fig0, Segment fig1, char intersectVert) {
		super(FigureRelationType.PERPENDICULAR, fig0, fig1);
		this.intersectVert = intersectVert;
	}

	@Override
	public boolean equals(Object o) {
		if (!super.equals(o))
			return false;
		if (!(o instanceof PerpendicularFigureRelation))
			return false;
		PerpendicularFigureRelation other = (PerpendicularFigureRelation) o;
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
