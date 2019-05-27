package geometry.proofs;

import geometry.shapes.Angle;
import geometry.shapes.Segment;

public class AngleBisectorFigureRelation extends FigureRelation {
	
	/** Smallest segment that bisects the angle */
	private String smallestBisector;
	/**
	 * The smallest bisector has two endpoints. The first is at the point of intersection
	 * where the segment bisects the angle. This is the opposite of that endpoint.
	 */
	private String smallestBisectorEndpoint;
	
	public AngleBisectorFigureRelation(Segment fig0, Angle fig1, String smallestBisector,
			String smallestBisectorEndpoint) {
		super(FigureRelationType.BISECTS, fig0, fig1);
		this.smallestBisector = smallestBisector;
		this.smallestBisectorEndpoint = smallestBisectorEndpoint;
	}

	@Override
	public boolean equals(Object o) {
		if (!super.equals(o))
			return false;
		if (!(o instanceof AngleBisectorFigureRelation))
			return false;
		AngleBisectorFigureRelation other = (AngleBisectorFigureRelation) o;
		return other.smallestBisectorEndpoint.equals(smallestBisectorEndpoint)
				&& other.smallestBisector.equals(smallestBisector);
	}
	
	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + smallestBisector.hashCode();
		result = 31 * result + smallestBisectorEndpoint.hashCode();
		return result;
	}
	
	/**
	 * Get the name of the smallest segment that bisects the angle
	 */
	public String getSmallestBisector() {
		return smallestBisector;
	}
	
	/**
	 * The smallest bisector has two endpoints. The first is at the point of intersection
	 * where the segment bisects the angle. This is the opposite of that endpoint.
	 */
	public String getSmallestBisectorEndpoint() {
		return smallestBisectorEndpoint;
	}
	
}
