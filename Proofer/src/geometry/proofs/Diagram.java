package geometry.proofs;

import geometry.shapes.Angle;
import geometry.shapes.Segment;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;
import util.Utils;

import java.util.List;
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
		return newGoal == null ? null : setProofGoal(newGoal);
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
		return addFigureRelation(rel);
	}
	
	/**
	 * Apply the transitive postulate.
	 * @param rel the {@link FigureRelation} of type CONGRUENT to
	 * which the transitive postulate will be applied.
	 */
	private void applyTransitivePostulate(FigureRelation rel) {
		// Conditions
		if (
				// Figure relation type is not "congruent"
				rel.getRelationType() != FigureRelationType.CONGRUENT
				// Figures in iter must NOT be the same figure congruent to itself
				|| rel.isCongruentAndReflexive()
			)
			return;
		
		final int COUNT = relations.size();
		
		for (Figure sharedFriend : rel.getFigures()) {
			for (int i = 0; i < COUNT; i++) {
				FigureRelation iter = relations.get(i);
				
				// Conditions
				if (
						// Figure relation type is not "congruent"
						iter.getRelationType() != FigureRelationType.CONGRUENT
						// Figures in iter must be same type as sharedFriend
						|| iter.getFigure0().getClass() != sharedFriend.getClass()
						// Figures in iter must NOT be the same figure congruent to itself
						|| iter.isCongruentAndReflexive()
						// Iter must not be equal to rel
						|| iter.equals(rel)
						// Iteration must contain figure
						|| !iter.containsFigure(sharedFriend)
					)
					continue;
				
				Figure newFriend0 = rel.getFigure0().equals(sharedFriend) ?
						rel.getFigure1() : rel.getFigure0();
				Figure newFriend1 = iter.getFigure0().equals(sharedFriend) ?
						iter.getFigure1() : iter.getFigure0();
				
				FigureRelation newRel = new FigureRelation(
						FigureRelationType.CONGRUENT,
						newFriend0,
						newFriend1,
						null // Null parent
				);
				// Add the new relation
				if (!containsFigureRelation(newRel))
					relations.add(newRel);
			}
		}
	}
	
	/**
	 * Make the given angle a right angle, and make it congruent to all
	 * other right angles in the given list of {@link FigureRelation}s.
	 * @param a the angle
	 * @param parent the parent relation
	 * @return returns false if the given Angle name does not belong to an
	 * Angle in this Diagram, or if the given angle is already a right angle
	 * in this Diagram, true otherwise
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
		
		// If there is already a figure relation pair that makes the given
		// Angle a right angle, our job is already done, we can exit.
		if (containsFigureRelation(rel)) {
			return false;
		}
		
		// Add the new pair that makes the angle a right angle
		relations.add(rel);
		
		// Make new right angle congruent to all other right angles in collection
		for (int i = 0; i < relations.size() - 1; i++) {
			// (Above): we say "i < relations() '-1' " bc we don't want to compare
			// the relation pair to itself. Therefore, we must exclude it from the list.
			// Since we just added it to the list, we know that it is at the very end
			// of the list. The "-1" excludes the relation pair and makes sure that
			// we don't compare it to itself.

			FigureRelation pair = relations.get(i);
			if (pair.getRelationType() == FigureRelationType.RIGHT) {
				FigureRelation newPair = new FigureRelation(
						FigureRelationType.CONGRUENT,
						a,
						pair.getFigure0(),
						parent // Parent
					);
				
				// Ensure that we're not adding a duplicate
				if (!containsFigureRelation(newPair)) {
					relations.add(newPair);
				}
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
	
	/**
	 * Use the given information to create a {@link FigureRelation}.
	 * @return the {@link FigureRelation}
	 */
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
	
	/**
	 * Use the given information to create a {@link FigureRelation}
	 * and add the given {@link FigureRelation} to this {@link Diagram}.
	 * <p>
	 * NOTE: DO NOT USE THIS METHOD WITH TRIANGLES OR ANGLES. This is because
	 * with the given string names, it is impossible to know the difference
	 * between an {@link Angle} or a {@link Triangle}. 
	 * @param type the {@link FigureRelationType} of the {@link FigureRelation}
	 * @param fig0 the first {@link Figure}
	 * @param fig1 the second {@link Figure}
	 * @param parent the parent of the {@link FigureRelation}
	 * @return false if the given {@link FigureRelation} is already
	 * contained in this {@link Diagram}, true if the operation
	 * was successful
	 */
	public boolean addFigureRelation(FigureRelationType type, String fig0, String fig1,
			FigureRelation parent) {
		FigureRelation pair = valueOf(type, fig0, fig1, parent);
		return addFigureRelation(pair);
	}
	
	/**
	 * Add the given {@link FigureRelation} to this {@link Diagram}.
	 * @param pair the {@link FigureRelation}
	 * @return false if the given {@link FigureRelation} is already
	 * contained in this {@link Diagram}, true if the operation
	 * was successful
	 */
	public boolean addFigureRelation(FigureRelation pair) {
		if (containsFigureRelation(pair))
			return false;
		if (relations.add(pair)) {
			// If the relation declares that an angle is a right angle,
			// make this right angle congruent to all other right angles.
			if (pair.getRelationType() == FigureRelationType.RIGHT) {
				makeRightAngle(pair.getFigure0().getName(), pair);
			}
			// If the pair declares two figures congruent, apply the transitive
			// postulate
			else if (pair.getRelationType() == FigureRelationType.CONGRUENT) {
				applyTransitivePostulate(pair);
				// If two triangles are congruent, all of their corresponding children figures
				// are congruent as well
				if (!pair.isCongruentAndReflexive() && 
						pair.getFigure0().getClass() == Triangle.class) {
					// First triangle
					Triangle tri0 = pair.getFigure0();
					// Second triangle
					Triangle tri1 = pair.getFigure1();
					
					// For all corresponding angles
					for (Angle[] anglePair : Utils.getCorrespondingAngles(tri0, tri1)) {
						// Make congruent pair
						FigureRelation newPair = new FigureRelation(
								FigureRelationType.CONGRUENT,
								anglePair[0],
								anglePair[1],
								pair // TODO: change this to "corresponding angles"
						);
						addFigureRelation(newPair);
					}
					// For all corresponding segments
					for (Segment[] segPair : Utils.getCorrespondingSegments(tri0, tri1)) {
						// Make congruent pair
						FigureRelation newPair = new FigureRelation(
								FigureRelationType.CONGRUENT,
								segPair[0],
								segPair[1],
								pair // TODO: change this to "corresponding segments"
						);
						addFigureRelation(newPair);
					}
				}
			}
			return true; // Successfully added relation pair
		}
		return false; // Unsuccessful
	}
	
	public void addFigureRelation(Collection<FigureRelation> figs) {
		for (FigureRelation fig : figs) {
			addFigureRelation(fig);
		}
	}
	
	public boolean removeFigureRelation(FigureRelationType type, String fig0,
			String fig1, FigureRelation parent) {
		return relations.remove(valueOf(type, fig0, fig1, parent));
	}
	public boolean removeFigureRelation(FigureRelation pair) {
		return relations.remove(pair);
	}
	
	public boolean removeFigureRelation(Collection<FigureRelation> figs) {
		return relations.removeAll(figs);
	}
	
	public boolean containsFigureRelation(FigureRelation rel) {
		return relations.contains(rel);
	}
	
	public boolean containsFigureRelation(FigureRelationType type, String fig0,
			String fig1, FigureRelation parent) {
		return relations.contains(valueOf(type, fig0, fig1, parent));
	}
	
	public boolean containsFigureRelations(Collection<FigureRelation> figs) {
		return relations.containsAll(figs);
	}
	
	public FigureRelation getFirstRelationOfType(FigureRelationType type) {
		for (FigureRelation pair : relations) {
			if (pair.getRelationType() == type) {
				return pair;
			}
		}
		return null;
	}
	
	public List<FigureRelation> getAllFigureRelationsOfType(FigureRelationType type) {
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
