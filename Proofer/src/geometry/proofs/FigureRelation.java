package geometry.proofs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import exceptions.IllegalRelationException;
import geometry.shapes.Angle;
import geometry.shapes.Segment;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;

public class FigureRelation {
	private FigureRelationType relType;
	private Figure figure0;
	private Figure figure1;
	private boolean isCongruentAndReflexive;
	private List<FigureRelation> parents;
	private String reason;
	
	public FigureRelation(FigureRelationType type, Figure fig0, Figure fig1) {
		relType = type;
		figure0 = Objects.requireNonNull(fig0);
		
		// In single figure relations, the second figure is null
		if (!type.isSingleFigureRelation()) {
			figure1 = Objects.requireNonNull(fig1);
		} else {
			figure1 = fig1;
		}
		
		if (!isLegalRelation(this)) {
			System.err.println("Illegal Relation: " + this);
			throw new IllegalRelationException(this);
		}
		
		isCongruentAndReflexive = relType == FigureRelationType.CONGRUENT
				&& figure0.equals(figure1);
		
		parents = new ArrayList<>();
		reason = "";
	}
	
	public boolean isCongruentAndReflexive() {
		return isCongruentAndReflexive;
	}
	
	public static boolean isLegalRelation(FigureRelation rel) {
		Figure figure0 = rel.getFigure0();
		Figure figure1 = rel.getFigure1();
		FigureRelationType relType = rel.getRelationType();
		
		switch (relType) {
		case CONGRUENT:
			return figure0.getClass() == figure1.getClass()
			&& figure0.getClass() != Vertex.class;
		case PARALLEL:
		case PERPENDICULAR:
			return figure0.getClass() == Segment.class
			&& figure1.getClass() == Segment.class
			&& !figure0.equals(figure1);
		case BISECTS:
			return figure0.getClass() == Segment.class &&
			(figure1.getClass() == Segment.class ||
			figure1.getClass() == Angle.class)
			&& !figure0.equals(figure1);
		case SIMILAR:
			return figure0.getClass() == Triangle.class &&
			figure1.getClass() == Triangle.class
			&& !figure0.equals(figure1);
		case ISOSCELES:
			return figure0.getClass() == Triangle.class && figure1 == null;
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
		return relType + " ( " + figure0 + ", " + figure1 + " )"
				+ (!reason.isEmpty() ? " -- " + reason : "");
	}
	
	/**
	 * Get whether this {@link FigureRelation} is a relation type
	 * that only concerns one figure (such as RIGHT or ISOSCELES).
	 */
	public boolean isSingleFigureRelation() {
		return relType.isSingleFigureRelation();
	}
	
	public void addParents(Collection<FigureRelation> parents) {
		this.parents.addAll(parents);
	}
	
	public void addParent(FigureRelation parent) {
		parents.add(parent);
	}
	
	public boolean containsFigure(Figure fig) {
		return figure0.equals(fig) || (figure1 != null ? figure1.equals(fig) : fig == null);
	}

	public FigureRelationType getRelationType() {
		return relType;
	}

	// Will crash if misused
	@SuppressWarnings("unchecked")
	public <T extends Figure> T getFigure0() {
		return (T)figure0;
	}

	// Will crash if misused
	@SuppressWarnings("unchecked")
	public <T extends Figure> T getFigure1() {
		return (T)figure1;
	}
	
	public List<Figure> getFigures() {
		return Arrays.asList(figure0, figure1);
	}

	public List<FigureRelation> getParents() {
		return parents;
	}
	
	public void setReason(String reason) {
		this.reason = Objects.requireNonNull(reason);
	}
	
	public String getReason() {
		return reason;
	}
}
