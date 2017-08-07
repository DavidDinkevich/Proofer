package geometry.proofs;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import geometry.shapes.Angle;
import geometry.shapes.Segment;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;

public class FigureRelation {
	private final FigureRelationType relType;
	private final Figure figure0;
	private final Figure figure1;
	
	public FigureRelation(FigureRelationType type, Figure fig0, Figure fig1) {
		relType = type;
		figure0 = Objects.requireNonNull(fig0);
		
		// Only in RIGHT relation pairs can the second figure be null
		if (type != FigureRelationType.RIGHT) {
			figure1 = Objects.requireNonNull(fig1);
		} else {
			figure1 = fig1;
		}
		
		if (!isLegalRelation()) {
			throw new IllegalRelationException();
		}
	}
	
	private boolean isLegalRelation() {
		switch (relType) {
		case CONGRUENT:
			return figure0.getClass() == figure1.getClass()
			&& figure0.getClass() != Vertex.class;
		case PARALLEL:
		case PERPENDICULAR:
			return figure0.getClass() == Segment.class
			&& figure1.getClass() == Segment.class;
		case BISECTS:
			return figure0.getClass() == Segment.class &&
			(figure1.getClass() == Segment.class ||
			figure1.getClass() == Angle.class);
		case SIMILAR:
			return figure0.getClass() == Triangle.class &&
			figure1.getClass() == Triangle.class;
		case RIGHT:
			return figure0.getClass() == Angle.class && figure1 == null;
		case COMPLEMENTARY:
		case SUPPLEMENTARY:
//		case VERTICAL:
//			return figure0.getClass() == Angle.class && 
//			figure1.getClass() == Angle.class;
		case MIDPOINT:
			return figure0.getClass() == Vertex.class &&
			figure1.getClass() == Segment.class;
		default:
			return false;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof FigureRelation))
			return false;
		FigureRelation pair = (FigureRelation)o;
		
		// If the type of both of these pairs is "congruent", then it doesn't
		// matter which figure is "figure0" and "figure1"--symmetry doesn't matter
		if (pair.relType == relType && relType == FigureRelationType.CONGRUENT) {
			return (figure0.equals(pair.figure0) && figure1.equals(pair.figure1))
					|| (figure0.equals(pair.figure1) && figure1.equals(pair.figure0));
		}
		// Symmetry matters here
		return pair.relType == relType && figure0.equals(pair.figure0)
				// In the case of a single figure relation, (right angles, etc.),
				// the second figure is null. The following makes that possible.
				&& (figure1 == null ? pair.figure1 == null : figure1.equals(pair.figure1));
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + figure0.hashCode();
		// In a single figure relation, the second figure is null
		result = 31 * result + (figure1 == null ? 0 : figure1.hashCode());
		result = 31 * result + relType.hashCode();
		return result;
	}
	
	@Override
	public String toString() {
		return relType + " ( " + figure0 + ", " + figure1 + " )";
	}

	public FigureRelationType getRelationType() {
		return relType;
	}

	// Will crash if misused
	@SuppressWarnings("unchecked")
	public <T> T getFigure0() {
		return (T)figure0;
	}

	// Will crash if misused
	@SuppressWarnings("unchecked")
	public <T> T getFigure1() {
		return (T)figure1;
	}
	
	public List<Figure> getFigures() {
		return Arrays.asList(figure0, figure1);
	}
	
	public boolean containsFigure(Figure fig) {
		return figure0.equals(fig) || figure1 != null ? figure1.equals(fig) : fig == null;
	}
}