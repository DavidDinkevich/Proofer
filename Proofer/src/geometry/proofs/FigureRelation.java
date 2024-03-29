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

import static geometry.proofs.FigureRelationType.CONGRUENT;
import static geometry.proofs.FigureRelationType.PERPENDICULAR;
import static geometry.proofs.FigureRelationType.BISECTS;


public class FigureRelation {
	private FigureRelationType relType;
	private Figure figure0;
	private Figure figure1;
	private boolean isCongruentAndReflexive;
	private List<FigureRelation> parents;
	private ProofReasons reason;
	
	public FigureRelation(FigureRelationType type, Figure fig0, Figure fig1) {
		relType = type;
		figure0 = Objects.requireNonNull(fig0);
		
		// In single figure relations, the second figure is null
		if (!FigureRelationType.isSingleFigureRelationType(type)) {
			figure1 = Objects.requireNonNull(fig1);
		} else {
			figure1 = fig1;
		}
		
		if (!isLegalRelation(relType, figure0, figure1)) {
			System.err.println("Illegal Relation: " + this);
			throw new IllegalRelationException(this);
		}
		
		isCongruentAndReflexive = relType == CONGRUENT && figure0.equals(figure1);
		
		parents = new ArrayList<>();
		reason = ProofReasons.NONE;
	}
	
	protected FigureRelation(FigureRelationType type) {
		relType = type;
	}
	
	public boolean isCongruentAndReflexive() {
		return isCongruentAndReflexive;
	}
	
	public static boolean isLegalRelation(FigureRelationType type, Figure f0, Figure f1) {		
		switch (type) {
		case CONGRUENT:
			return f0.getClass() == f1.getClass()
			&& f0.getClass() != Vertex.class;
		case PARALLEL:
		case PERPENDICULAR:
			return f0.getClass() == Segment.class
			&& f1.getClass() == Segment.class
			&& !f0.equals(f1);
		case BISECTS:
			return f0.getClass() == Segment.class &&
			(f1.getClass() == Segment.class ||
			f1.getClass() == Angle.class)
			&& !f0.equals(f1);
		case SIMILAR:
			return f0.getClass() == Triangle.class &&
			f1.getClass() == Triangle.class
			&& !f0.equals(f1);
		case ISOSCELES:
			return f0.getClass() == Triangle.class && f1 == null;
		case RIGHT:
			return f0.getClass() == Angle.class && f1 == null;
		case COMPLEMENTARY:
		case SUPPLEMENTARY:
			return f0.getClass() == Angle.class && f1.getClass() == Angle.class
			&& !f0.equals(f1);
		case MIDPOINT:
			return f0.getClass() == Vertex.class &&
			f1.getClass() == Segment.class;
		default:
			return false;
		}
	}
	
	/**
	 * Get whether the two given {@link FigureRelation}s are equal. Unlike the default equals()
	 * method, this accounts for two {@link Figure Relation}s of the same 
	 * {@link FigureRelationType} but of different classes.
	 * @param a the first {@link Figure Relation}
	 * @param b the second {@link Figure Relation}
	 * @return whether or not they are equal
	 */
	public static boolean safeEquals(FigureRelation a, FigureRelation b) {
		FigureRelationType typeA = a.getRelationType();
		FigureRelationType typeB = b.getRelationType();
		// For types PERPENDICULAR and BISECTS, there are multiple classes that can
		// hold such relations. In these cases, just compare the figures
		// Perpendicular relations are symmetrical
		if (typeA == PERPENDICULAR && typeB == PERPENDICULAR) {
			return (a.figure0.equals(b.figure0) && a.figure1.equals(b.figure1))
					|| (a.figure0.equals(b.figure1) && a.figure1.equals(b.figure0));
		}
		// Segment bisector relations are NOT symmetrical
		else if (typeA == BISECTS && typeB == BISECTS) {
			return a.getFigure0().equals(b.getFigure0()) && a.getFigure1().equals(b.getFigure1());
		} else {
			return a.equals(b);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof FigureRelation))
			return false;
		FigureRelation pair = (FigureRelation)o;
		
		// If the relation type is symmetrical, then the order in which figures are congruent
		// does not matter
		if (pair.relType == relType && FigureRelationType
				.isSymmetricalFigureRelationType(relType)) {
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
				+ (reason == null ? " -- " + reason : "");
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
	
	public String getStatement() {
		return getFigure0() + " " + relType + " " + getFigure1();
	}
	
	public void setReason(ProofReasons reason) {
		this.reason = Objects.requireNonNull(reason);
	}
	
	public ProofReasons getReason() {
		return reason;
	}
}
