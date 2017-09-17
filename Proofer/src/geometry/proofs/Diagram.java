package geometry.proofs;

import java.util.List;

import geometry.shapes.Angle;
import geometry.shapes.Vertex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Diagram {
	private List<Figure> figures;
	private List<FigureRelation> relations;
	private FigureRelation proofGoal;
	
	public Diagram() {
		figures = new ArrayList<>();
		relations = new ArrayList<>();
	}
	
	public FigureRelation getProofGoal() {
		return proofGoal;
	}
	
	/**
	 * Set the goal of this proof
	 * @return the old goal, or null if there was no previous
	 */
	public FigureRelation setProofGoal(FigureRelationType rel, String fig0, String fig1,
			FigureRelation parent) {
		FigureRelation newGoal = valueOf(rel, fig0, fig1, parent);
		if (newGoal == null)
			return null;
		return setProofGoal(newGoal);
	}
	
	/**
	 * Set the goal of this proof
	 * @param newGoal the new goal
	 * @return the old goal, or null if there was no previous
	 */
	public FigureRelation setProofGoal(FigureRelation newGoal) {
		FigureRelation old = proofGoal;
		proofGoal = newGoal;
		return old;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Figure> T getFigure(String name) {
		for (Figure fig : figures) {
			if (fig.isValidName(name))
				return (T)fig;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Figure> T getFigure(String name, Class<T> type) {
		for (Figure fig : figures) {
			if (type == fig.getClass() && fig.isValidName(name)) {
				return (T)fig;
			}
		}
		return null;
	}
	
	public boolean addFigure(Figure fig) {
		// Add the figure
		if (!addFigureAndRelations(fig))
			return false;
		
		// Fill a buffer array with the figure's children
		List<Figure> buff = new ArrayList<>(fig.getChildren());
		// For each child figure in buff
		for (Figure f : buff) {
			// If the figure is not contained, add it.
			addFigureAndRelations(f);
		}
		
		// While the buffer is not empty
		while (!buff.isEmpty()) {
			// Create a second buffer (with the children of the original figure),
			// which is a copy of the first
			List<Figure> buff2 = new ArrayList<>(buff);
			// Clear the first buffer
			buff.clear();
			// Fill first buffer with the children of the figures
			// in the second buffer
			for (int i = 0; i < buff2.size(); i++) {
				buff.addAll(buff2.get(i).getChildren());
			}
			// Add figures from first buff to official list of figures
			for (Figure f : buff) {
				addFigureAndRelations(f);
			}
		}
		return true;
	}
	
	/**
	 * Add the given List of figures
	 * @param figs the List
	 * @return true if the diagram was modified (if a single figure was added)
	 */
	public boolean addFigures(Collection<? extends Figure> figs) {
		boolean result = false;
		for (Figure fig : figs) {
			if (addFigure(fig))
				result = true;
		}
		return result;
	}
	
	private boolean addFigureAndRelations(Figure fig) {
		if (containsFigure(fig))
			return false;
		figures.add(fig);
		applyReflexivePostulate(fig);
		return true;
	}
	
	/**
	 * Apply the reflexive postulate
	 * @param the figure
	 */
	private boolean applyReflexivePostulate(Figure fig) {
		if (fig.getClass() == Vertex.class)
			return false;
		FigureRelation rel = new FigureRelation(
				FigureRelationType.CONGRUENT,
				fig,
				fig,
				null // Null parent
		);
		return addFigureRelationPair(rel);
	}
	
	/**
	 * Make the given angle a right angle, and make it congruent to all
	 * other right angles in the given list of {@link FigureRelation}s.
	 * @param a the angle
	 * @param parent the parent relation
	 * @return returns success
	 */
	public boolean makeRightAngle(String angle, FigureRelation parent) {
		Angle a = getFigure(angle, Angle.class);
		final int angleIndex = figures.indexOf(a);
		if (angleIndex < 0)
			return false;
		// Make angle a right angle
		FigureRelation rel = new FigureRelation(
				FigureRelationType.RIGHT,
				a,
				null,
				parent // Parent
			);
		relations.add(rel);
		// Make new right angle congruent to all other right angles in collection
		for (int i = 0; i < relations.size(); i++) {
			if (i == angleIndex)
				continue;
			FigureRelation pair = relations.get(i);
			if (pair.getRelationType() == FigureRelationType.RIGHT) {
				FigureRelation newPair = new FigureRelation(
						FigureRelationType.CONGRUENT,
						a,
						pair.getFigure0(),
						parent // Parent
					);
				relations.add(newPair);
			}
		}
		return true;
	}
	
	public boolean removeFigure(Figure fig) {
		return figures.remove(fig);
	}
	
	public boolean removeFigures(Collection<? extends Figure> figs) {
		return figures.removeAll(figs);
	}
	
	public boolean containsFigure(Figure fig) {
		return figures.contains(fig);
	}
	
	public boolean containsFigure(String name) {
		return getFigure(name) != null;
	}
	
	public boolean containsFigure(String name, Class<? extends Figure> c) {
		return getFigure(name, c) != null;
	}
	
	public boolean containsFigures(Collection<String> figs) {
		for (String fig : figs) {
			if (!containsFigure(fig))
				return false;
		}
		return true;
	}
	
	public boolean containsFigures(Collection<String> figs, Class<? extends Figure> c) {
		for (String fig : figs) {
			if (!containsFigure(fig, c))
				return false;
		}
		return true;
	}
	
	public List<Figure> getFigures() {
		return Collections.unmodifiableList(figures);
	}
	
	/////////////////////////////
	
	private FigureRelation valueOf(FigureRelationType type, String fig0, String fig1,
			FigureRelation parent) {
		final boolean RIGHT_ANGLE = type == FigureRelationType.RIGHT;
		// Special search for right angles
		Figure figure0 = !RIGHT_ANGLE ? getFigure(fig0) : getFigure(fig0, Angle.class);
		Figure figure1 = null;
		if (!RIGHT_ANGLE)
			figure1 = getFigure(fig1);
		if (figure0 == null || (!RIGHT_ANGLE && figure1 == null))
			return null;
		return new FigureRelation(type, figure0, figure1, parent);
	}
	
	public boolean addFigureRelationPair(FigureRelationType type, String fig0, String fig1,
			FigureRelation parent) {
		FigureRelation pair = valueOf(type, fig0, fig1, parent);
		return addFigureRelationPair(pair);
	}
	
	public boolean addFigureRelationPair(FigureRelation pair) {
		if (containsFigureRelationPair(pair))
			return false;
		if (relations.add(pair)) {
			// If the relation declares that an angle is a right angle,
			// make this right angle congruent to all other right angles.
			if (pair.getRelationType() == FigureRelationType.RIGHT) {
				makeRightAngle(pair.getFigure0().getName(), pair);
			}
			return true;
		}
		return false;
	}
	
	public void addFigureRelationPairs(Collection<FigureRelation> figs) {
		for (FigureRelation fig : figs) {
			addFigureRelationPair(fig);
		}
	}
	
	public boolean removeFigureRelationPair(FigureRelationType type, String fig0,
			String fig1, FigureRelation parent) {
		return relations.remove(valueOf(type, fig0, fig1, parent));
	}
	public boolean removeFigureRelationPair(FigureRelation pair) {
		return relations.remove(pair);
	}
	
	public boolean removeFigureRelationPairs(Collection<FigureRelation> figs) {
		return relations.removeAll(figs);
	}
	
	public boolean containsFigureRelationPair(FigureRelation rel) {
		return relations.contains(rel);
	}
	
	public boolean containsFigureRelationPair(FigureRelationType type, String fig0,
			String fig1, FigureRelation parent) {
		return relations.contains(valueOf(type, fig0, fig1, parent));
	}
	
	public boolean containsFigureRelationPairs(Collection<FigureRelation> figs) {
		return relations.containsAll(figs);
	}
	
	public FigureRelation getFirstRelationPairWithType(FigureRelationType type) {
		for (FigureRelation pair : relations) {
			if (pair.getRelationType() == type) {
				return pair;
			}
		}
		return null;
	}
	
	public List<FigureRelation> getAllRelationPairsWithType(FigureRelationType type) {
		List<FigureRelation> rels = null;
		for (FigureRelation pair : relations) {
			if (pair.getRelationType() == type) {
				if (rels == null)
					rels = new ArrayList<>();
				rels.add(pair);
			}
		}
		return rels == null ? Collections.emptyList() : rels;
	}
	
	public List<FigureRelation> getFigureRelations() {
		return Collections.unmodifiableList(relations);
	}
}
