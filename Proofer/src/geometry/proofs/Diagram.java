package geometry.proofs;

import geometry.shapes.Angle;
import geometry.shapes.Segment;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;

import util.Node;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import static geometry.proofs.FigureRelationType.CONGRUENT;
import static geometry.proofs.FigureRelationType.RIGHT;
import static geometry.proofs.FigureRelationType.PARALLEL;
import static geometry.proofs.FigureRelationType.PERPENDICULAR;
import static geometry.proofs.FigureRelationType.SUPPLEMENTARY;
import static geometry.proofs.FigureRelationType.COMPLEMENTARY;


public class Diagram {
	
	public static enum Policy {
		FIGURES_AND_RELATIONS, FIGURES_ONLY
	}
	
	private Policy policy;
	
	private List<DiagramListener> listeners;
	
	private List<Figure> figures;
	private List<FigureRelation> relations;
	private FigureRelation proofGoal;
	
	/**
	 * This list is necessary to make
	 * sure that only one angle of a set of angle synonyms are included in the diagram, so as to
	 * avoid compatibility issues (for example, when adding {@link FigureRelation}s, the
	 * relation is true for all synonyms, so only one "representative" angle is needed.
	 * The representative angle synonym is always the first in its respective list of synonyms,
	 * and is called the "primary angle synonym".
	 */
	private List<List<Angle>> angleSynonyms;
	
	/**
	 * Map of all hidden figures sorted by type.
	 */
	private Map<Class<?>, List<Figure>> hiddenFigures;
	
	/**
	 * Map of all compound segments and their respective component vertices
	 */
	public List<Node<Segment, Vertex>> compoundSegments;
	
	public Diagram(Policy policy) {
		this.policy = Objects.requireNonNull(policy);
		
		figures = new ArrayList<>();
		relations = new ArrayList<>();
		angleSynonyms = new ArrayList<>();
		compoundSegments = new ArrayList<>();
		listeners = new ArrayList<>();
		hiddenFigures = new HashMap<>();
		hiddenFigures.put(Vertex.class, new ArrayList<>());
		hiddenFigures.put(Angle.class, new ArrayList<>());
		hiddenFigures.put(Segment.class, new ArrayList<>());
		hiddenFigures.put(Triangle.class, new ArrayList<>());
	}
	
	public Diagram() {
		this(Policy.FIGURES_AND_RELATIONS);
	}
	
	public Policy getPolicy() {
		return policy;
	}

	public List<DiagramListener> getListeners() {
		return listeners;
	}
	
	/**
	 * Add a <i>HIDDEN</i> {@link Figure} to this {@link Diagram}. This will also add
	 * all of the given children's children (and their children, and so on). Lastly, it will
	 * add a default reflexive postulate and if necessary transitive postulate 
	 * {@link FigureRelation}.
	 * <p>
	 * NOTE: if the given figure is a secondary angle synonym, it will be store internally
	 * in this {@link Diagram} (and accessible via {@link Diagram#getAngleSynonyms(String)})
	 * but it will NOT be added as a normal figure.
	 * @param fig the figure to add
	 * @return false IF it is a duplicate OR if it is not a primary angle synonym. 
	 * True otherwise.
	 */
	public boolean addHiddenFigure(Figure fig) {
		if (addFigure(fig)) {
			hiddenFigures.get(fig.getClass()).add(fig);
			return true;
		}
		return false;
	}
	
	public boolean addHiddenFigures(Collection<? extends Figure> figs) {
		boolean result = false;
		for (Figure fig : figs) {
			if (addHiddenFigure(fig))
				result = true;
		}
		return result;
	}
	
	/**
	 * Get the list of hidden {@link Figure}s of the given type.
	 * @param type the type of hidden figures to retrieve
	 * @return the list of hidden figures (of the given type)
	 */
	// Will crash if types don't match up
	@SuppressWarnings("unchecked")
	public <T extends Figure> List<T> getHiddenFigures(Class<T> type) {
		return Collections.unmodifiableList((List<T>) hiddenFigures.get(type));
	}
	
	public FigureRelation getProofGoal() {
		return proofGoal;
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
	
	/*
	 * FIGURES
	 */
	
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
	
	/**
	 * Add a (non-hidden) {@link Figure} to this {@link Diagram}. This will also add
	 * all of the given children's children (and their children, and so on). Lastly, it will
	 * add a default reflexive postulate and if necessary transitive postulate 
	 * {@link FigureRelation}.
	 * <p>
	 * NOTE: if the given figure is a secondary angle synonym, it will be store internally
	 * in this {@link Diagram} (and accessible via {@link Diagram#getAngleSynonyms(String)})
	 * but it will NOT be added as a normal figure.
	 * @param fig the figure to add
	 * @return false IF it is a duplicate OR if it is not a primary angle synonym. 
	 * True otherwise.
	 */
	public boolean addFigure(Figure fig) {
		Objects.requireNonNull(fig);
		// No duplicates
		if (containsFigure(fig))
			return false;
		// If it's an angle, handle angle synonyms
		if (fig.getClass() == Angle.class) {
			// Add the angle to the list of angle synonyms
			addAngle((Angle) fig);
			// If the angle is NOT a primary angle synonym, then we don't want to add it
			if (!isPrimaryAngleSynonym(fig.getName())) {
				// Return false because the figure was not added (the fact that the angle was
				// added to the angle synonyms list is irrelevant)
				return false;
			}
		}
		// Add the figure
		figures.add(fig);
		
		if (policy == Policy.FIGURES_AND_RELATIONS) {
			// Apply the reflexive postulate
			applyReflexivePostulate(fig);
		}
		
		// Notify listeners that a figure was added
		for (DiagramListener listener : listeners) {
			listener.figureWasAdded(fig);
		}
		
		// REPEAT THIS STEP RECURSIVELY FOR ALL CHILDREN OF THIS FIGURE
		for (Figure child : fig.getChildren()) {
			addFigure(child);
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
	
	public boolean containsFigures(String[] figs) {
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
	
	@SuppressWarnings("unchecked")
	public <T extends Figure> List<T> getFiguresOfType(Class<T> type) {
		List<T> figs = new ArrayList<>();
		for (Figure fig : figures) {
			if (fig.getClass() == type)
				figs.add((T) fig);
		}
		return figs;
	}
	
	/*
	 * ANGLE SYNONYMS
	 */
	
	public List<Angle> getAngleSynonyms(String angle) {
		for (List<Angle> subList : angleSynonyms) {
			for (Angle a : subList) {
				if (a.isValidName(angle)) {
					return Collections.unmodifiableList(subList);
				}
			}
		}
		return null;
	}
	
	public List<Angle> getAllAnglesAndSynonyms() {
		List<Angle> all = new ArrayList<>();
		for (List<Angle> subList : angleSynonyms) {
			all.addAll(subList);
		}
		return all;
	}
	
	public boolean containsAngleSynonym(String angle) {
//		List<Angle> subList = getAngleSynonyms(angle);
//		return subList != null;
		return isPrimaryAngleSynonym(angle) || isSecondaryAngleSynonym(angle);
	}
	
	public boolean isPrimaryAngleSynonym(String a) {
		for (List<Angle> subList : angleSynonyms) {
			if (subList.get(0).isValidName(a)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isSecondaryAngleSynonym(String a) {
		for (List<Angle> subList : angleSynonyms) {
			if (subList.size() > 1) {
				for (int i = 1; i < subList.size(); i++) {
					if (subList.get(i).isValidName(a)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Get the primary angle synonym of the set of angle synonyms that the given angle
	 * belongs to.
	 * @param angle the angle
	 * @return the primary angle synonym, or null if the given angle is not contained
	 * in this diagram.
	 */
	public Angle getPrimaryAngleSynonym(String angle) {
		List<Angle> synSet = getAngleSynonyms(angle);
		return synSet == null ? null : synSet.get(0);
	}
	
	/**
	 * Adds the given {@link Angle} to the list of angle synonyms. 
	 * @param angle the angle
	 * @return true if the angle was added, false if it is already contained in the list
	 */
	private boolean addAngle(Angle angle) {
		// For each list of angle synonyms
		for (List<Angle> subList : angleSynonyms) {
			// If it contains the given angle, we're not going to add it (no duplicates)
			if (subList.contains(angle)) {
				return false;
			}
			// Otherwise, check if it is a synonym of the other angles in this sublist, and if it
			// is the smallest, move it to the front (it will become the new primary angle synonym)
			final int result = ProofUtils.compareAngleSynonyms(angle, subList.get(0));
			// The angle IS a synonym (as opposed to -3, -2, which mean that it is NOT a synonym)
			if (result > -2) {				
				// If the angle is SMALLER than the existing smallest angle 
				// (for this set of synonyms), then insert the angle to the front of the set.
				// It will become the new primary angle synonym
				// ALSO, remove the old primary angle synonym from the list of figures, as
				// now it is a secondary angle synonym, and SASs are not included in the list
				// of figures
				if (result == -1) {
					figures.remove(subList.get(0));
					subList.add(0, angle);
				} 
				// Otherwise, if result = 0 or 1, just append it to the end
				else {
					subList.add(angle);
				}
				return true;
			}
		}

		// If this angle does NOT have any angle synonyms, make an new list for it
		List<Angle> newSubList = new ArrayList<>();
		newSubList.add(angle);
		angleSynonyms.add(newSubList);
		return true;
	}
	
	/*
	 * COMPOUND FIGURES
	 */
	
	/**
	 * Designate the given Segment as a compound segment. This stores it in a buffer and
	 * allows component vertices to be added to it in the future.
	 * @param seg the segment to mark as a compound segment
	 * @return false if the given segment is already a compound segment, true otherwise
	 */
	public boolean markAsCompoundSegment(Segment seg) {
		if (getCompoundSegmentNode(seg.getName()) == null) {
			Node<Segment, Vertex> newNode = new Node<Segment, Vertex>(seg);
			// End points are automatically added
			newNode.getChildren().addAll(seg.getVerticesList());
			return compoundSegments.add(newNode);
		}
		return false;
	}
	
	/**
	 * Add a component {@link Vertex} to the given compound segment. The list of
	 * component vertices will be sorted in the order they lie on the {@link Segment}.
	 * @param seg the compound segment
	 * @param vertex the new component vertex
	 * @return false if the given segment is not designated as a compound segment, or if
	 * the given component vertex is already contained
	 */
	public boolean addComponentVertex(String seg, Vertex vertex) {
		// Get the corresponding node
		Node<Segment, Vertex> node = getCompoundSegmentNode(seg);
		if (node == null)
			return false;
		
		// Add the vertex in order
		if (ProofUtils.addLeastToGreatestDist(vertex, node.getChildren()) >= 0) {
			// Create the segments connecting every other existing component vertex to
			// this new component vertex
			for (Vertex compV : getComponentVertices(seg)) {
				// Don't make a segment between this vertex and itself
				if (compV.isValidName(vertex.getName()))
					continue;
				// New segment
				Segment newSeg = new Segment(compV, vertex);
				
				// In the case where two segments intersect and the poi
				// is one of the segment's end points, this can cause a bug
				// where one of the segments is the poi listed twice
				if (!newSeg.getVertexLoc(0).equals(newSeg.getVertexLoc(1))) {
					addHiddenFigure(newSeg);
				}
			}
			return true; // Vertex was successfully added
		}
		return false;
	}
	
	/**
	 * Add the given vertices as component vertices to the given compound segment
	 * @param seg the compound segment
	 * @param vertices the component vertices
	 * @return true if ALL the vertices were added
	 */
	public boolean addComponentVertices(String seg, List<Vertex> vertices) {
		boolean success = true;
		for (Vertex vert : vertices) {
			if (!addComponentVertex(seg, vert)) {
				success = false;
			}
		}
		return success;
	}
	
	/**
	 * Get a list of all the {@link Vertex}es lying on the given
	 * compound segment (including the compound segment's end-points).
	 * @return the vertices, or null if the given segment was not designated as a compound segment
	 */
	public List<Vertex> getComponentVertices(String seg) {
		Node<Segment, Vertex> node = getCompoundSegmentNode(seg);
		if (node == null)
			return null;
		return breakToUnitComponentVertices(node, new ArrayList<>());
	}
		
	/**
	 * Get the component segments of the given compound segment.
	 * @return the list of component segments, or null if the given compound segment
	 * is not <i>marked</i> as a compound segment in this {@link Diagram}
	 * @see Diagram#markAsCompoundSegment(Segment)
	 */
	public Segment[] getComponentSegments(String seg) {
		List<Vertex> componentVertices = getComponentVertices(seg);
		Segment[] segs = new Segment[componentVertices.size() - 1];
		for (int i = 0; i < componentVertices.size() - 1; i++) {
			Segment segment = new Segment(componentVertices.get(i), componentVertices.get(i + 1));
			segs[i] = segment;
		}
		return segs;
	}
	
	/**
	 * Get the largest compound segment that the given segment lies on.
	 */
	public Segment getLargestCompoundSegmentOf(String seg) {
		// Stats of the largest segment
		Segment largestSeg = null;
		int mostComponentVerts = 0;
		// For each compound segment
		for (Node<Segment, Vertex> node : compoundSegments) {
			// Check that this compound segment contains both endpoints of the query segment
			int count = 0;
			List<Vertex> componentVertices = getComponentVertices(node.getObject().getName());
			for (Vertex v : componentVertices) {
				if (v.getNameChar() == seg.charAt(0) || v.getNameChar() == seg.charAt(1)) {
					++count;
				}
			}
			// Size of the candidate compound segment
			final int candidate = componentVertices.size();
			// If the candidate compound segment contains the query AND is larger
			// than the previous largest compound segment, replace it
			if (count == 2 && candidate > mostComponentVerts) {
				largestSeg = node.getObject();
				mostComponentVerts = candidate;
			}
		}
		
		// If largestSeg = null, then it is likely the largest existing compound segment.
		// Therefore, we will just return it instead of null.
		return largestSeg == null ? getFigure(seg) : largestSeg;
	}
	
	/**
	 * Get whether the given segment is a compound segment
	 */
	public boolean isCompoundSegment(String seg) {
		if (!Segment.isValidSegmentName(seg))
			throw new IllegalArgumentException(seg + " is not a segment");
		return getCompoundSegmentNode(seg) != null;
	}
	
	/**
	 * Get the corresponding {@link Node} entry for the given {@link Segment}
	 * @return the entry, or null if there is no entry for the given segment
	 */
	protected Node<Segment, Vertex> getCompoundSegmentNode(String seg) {
		for (Node<Segment, Vertex> node : compoundSegments) {
			if (node.getObject().isValidName(seg))
				return node;
		}
		return null;
	}
	
	/**
	 * Return ALL the component vertices of the given segment, not just the "top-level" ones.
	 * (The children vertices stored in the compound segment nodes are only the most direct
	 * children of the segment, and we want THEIR children, and grandchildren, and so on).
	 */
	private List<Vertex> breakToUnitComponentVertices(Node<Segment, Vertex> node, 
			List<Vertex> all) {
		if (node != null) {
			// Add all children
			for (Vertex v : node.getChildren()) {
				ProofUtils.addLeastToGreatestDist(v, all);
			}
			// Do the same for each component segment
			for (int i = 0; i < node.getChildren().size() - 1; i++) {
				Vertex first = node.getChildren().get(i);
				Vertex second = node.getChildren().get(i + 1);
				// Don't call this method on the originally given segment, StackOverflowException
				if (!node.getObject().isValidName(first.getName() + second.getName())) {
					Segment segment = new Segment(first, second);
					breakToUnitComponentVertices(getCompoundSegmentNode(segment.getName()), all);
				}
			}
		}
		return all;
	}
		
	/*
	 * FIGURE RELATIONS
	 */	
	
	/**
	 * Apply the reflexive postulate
	 * @param the figure
	 */
	private boolean applyReflexivePostulate(Figure fig) {
		if (fig.getClass() == Vertex.class)
			return false;
		FigureRelation rel = new FigureRelation(CONGRUENT, fig, fig);
		rel.setReason(ProofReasons.REFLEXIVE);
		return addFigureRelation(rel);
	}
	
	/**
	 * Apply the transitive postulate to {@link FigureRelation}s of type CONGRUENT, SIMILAR,
	 * or PARALLEL. Do not try with others, success is not guaranteed.
	 * @param rel the {@link FigureRelation} to which the transitive postulate will be applied
	 * @param queryType the type of relationship ({@link FigureRelationType}) that another
	 * {@link FigureRelation} must have in order for the transitive postulate to be applied
	 * on the original, given {@link FigureRelation}, and it
	 * @param newRelType the {@link FigureRelationType} of {@link FigureRelation}s that are
	 * created as a result of the transitive postulate
	 * @param reasonForNewRel the {@link ProofReasons} justifying the {@link FigureRelation}s
	 * created by the transitive postulate
	 */
	private void applyTransitivePostulate(FigureRelation rel, FigureRelationType queryType,
			FigureRelationType newRelType, ProofReasons reasonForNewRel) {
		// Conditions
		if (rel.isCongruentAndReflexive())
			return;
		
		final int COUNT = relations.size();
		
		for (Figure sharedFriend : rel.getFigures()) {
			for (int i = 0; i < COUNT; i++) {
				FigureRelation iter = relations.get(i);
				
				// EXIT CONDITIONS
				if (
						// Figure relation type is not the same as rel (param)
						iter.getRelationType() != queryType
						// Figures in iter must be same type as sharedFriend
						|| iter.getFigure0().getClass() != sharedFriend.getClass()
						// Figures in iter must NOT be the same figure congruent to itself
						|| iter.isCongruentAndReflexive()
						// Iter must not be equal to rel
						|| FigureRelation.safeEquals(iter, rel)
						// Iteration must contain figure
						|| !iter.containsFigure(sharedFriend)
					)
					continue;
				
				Figure newFriend0 = rel.getFigure0().equals(sharedFriend) ?
						rel.getFigure1() : rel.getFigure0();
				Figure newFriend1 = iter.getFigure0().equals(sharedFriend) ?
						iter.getFigure1() : iter.getFigure0();
								
				FigureRelation newRel = new FigureRelation(newRelType, newFriend0, newFriend1);
				newRel.setReason(reasonForNewRel);
				newRel.addParent(iter);
				newRel.addParent(rel);
				// Add the new relation
				if (!containsFigureRelation(newRel))
					relations.add(newRel);
			}
		}
	}
	
	/**
	 * Make the given angle congruent to all
	 * other right angles in the list of {@link FigureRelation}s.<p>
	 * NOTE: this does NOT add the given {@link FigureRelation} to the {@link Diagram}.
	 * @param rightAngleRel the {@link FigureRelation} that makes the angle
	 * a right angle
	 */
	private void makeRightAngle(FigureRelation rightAngleRel) {
		// Get the angle
		Angle angle = rightAngleRel.getFigure0();
		// Make new right angle congruent to all other right angles in collection
		for (int i = 0; i < relations.size() - 1; i++) {
			// (Above): we say "i < relations.size() '-1' " bc we don't want to compare
			// the relation pair to itself. Therefore, we must exclude it from the list.
			// Since we just added it to the list, we know that it is at the very end
			// of the list. The "-1" excludes the relation pair and makes sure that
			// we don't compare it to itself.
			
			FigureRelation pair = relations.get(i);
			if (pair.getRelationType() == RIGHT) {
				FigureRelation newPair = new FigureRelation(
						CONGRUENT, angle, pair.getFigure0());
				newPair.addParent(rightAngleRel);
				newPair.addParent(pair);
				newPair.setReason(ProofReasons.RIGHT_ANGLES_CONGRUENT);

				// Ensure that we're not adding a duplicate
				if (!containsFigureRelation(newPair)) {
					relations.add(newPair);
				}
			}
			/*
			 * Add transitive right angles (angles that are congruent to this right angle
			 * are also right angles themselves
			 */
			else if (pair.getRelationType() == CONGRUENT) {
				// Get the pair of angles (this right angle and another congruent angle)
				List<Figure> figs = pair.getFigures();
				final int indexOfAngle = figs.indexOf(angle);
				if (indexOfAngle >= 0) {
					// Make the other angle a right angle
					Angle otherAngle = (Angle) figs.get(indexOfAngle == 0 ? 1 : 0);
					FigureRelation rightRel = new FigureRelation(RIGHT, otherAngle, null);
					rightRel.addParents(Arrays.asList(pair, rightAngleRel));
					rightRel.setReason(ProofReasons.TRANSITIVE);
					addFigureRelation(rightRel);
				}
			}
		}
	}
	
	/**
	 * PRECONDITION: the given {@link FigureRelation} makes two angles congruent.<p>
	 * If one of the angles in the given {@link FigureRelation} is a right angle, make
	 * the other angle a right angle (because angles congruent to right angles are right).
	 */
	private void addTransitiveRightAngles(FigureRelation pair) {
		// For each angle (2)
		List<Figure> figs = pair.getFigures();
		for (int i = 0; i < figs.size(); i++) {
			Angle a = (Angle) figs.get(i);
			// If this angle is a right angle
			FigureRelation hypoRel = getFigureRelation(RIGHT, a, null);
			if (hypoRel != null) {
				// Get the other angle
				final int indexOfOther = i == 0 ? 1 : 0;
				Angle b = (Angle) figs.get(indexOfOther);
				// Make the other angle a right angle
				FigureRelation rightRel = new FigureRelation(RIGHT, b, null);
				rightRel.addParents(Arrays.asList(pair, hypoRel));
				rightRel.setReason(ProofReasons.TRANSITIVE);
				addFigureRelation(rightRel);
				break;
			}
		}
	}
	
	/**
	 * This method enforces the following principle: if a pair of supplementary/complementary
	 * angles are supplementary/complementary to the same pair of congruent angles, they are
	 * congruent.<p>
	 * PRECONDITION: the given {@link FigureRelation} is not reflexive and makes two 
	 * angles congruent
	 * @param rel the pair of supplementary/complementary angles that are congruent
	 * @param suppOrComp (MUST BE COMPLEMENTARY OR SUPPLEMENTARY); the type of relationship
	 * ({@link FigureRelation}) that will be created by this method
	 */
	private void addTransitiveSuppCompAngles(FigureRelation rel, FigureRelationType suppOrComp) {
		// Enforce preconditions
		if (rel.getRelationType() != CONGRUENT || rel.isCongruentAndReflexive())
			throw new IllegalArgumentException("FigureRelationType must be non-reflexive and "
					+ "of type CONGRUENT");
		else if (!(suppOrComp == SUPPLEMENTARY || suppOrComp == COMPLEMENTARY))
			throw new IllegalArgumentException("supOrComp param must be of type COMPLEMENTARY"
					+ " or SUPPLEMENTARY");
		else if (!(rel.getFigure0() instanceof Angle))
			throw new IllegalArgumentException("This method can only be applied to a "
					+ "FigureRelation involving two congruent angles");
		
		// For each figure in the given FigureRelation, find another FigureRelation that contains
		// that figure (whose FigureRelationType is the suppOrComp param)
		final int count = relations.size();
		FigureRelation friend0 = null, friend1 = null;
		for (int i = 0; i < count; i++) {
			FigureRelation currRel = relations.get(i);
			FigureRelationType currRelType = currRel.getRelationType();
			// Correct type
			if (currRelType != suppOrComp)
				continue;
			// Must contain the first figure of the given FigureRelation
			else if (currRel.containsFigure(rel.getFigure0())) {
				friend0 = currRel;
			}
			// Must contain the second figure of the given FigureRelation
			else if (currRel.containsFigure(rel.getFigure1())) {
				friend1 = currRel;
			}
			// Once we've found both friends, we need look no more
			if (friend0 != null && friend1 != null)
				break;
		}
		// Make the two new friends congruent
		if (friend0 != null && friend1 != null) {
			Figure newFriend0 = friend0.getFigure0().equals(rel.getFigure0()) ? 
					friend0.getFigure1() : friend0.getFigure0();
			Figure newFriend1 = friend1.getFigure0().equals(rel.getFigure1()) ? 
					friend1.getFigure1() : friend1.getFigure0();
					
			FigureRelation newRel = new FigureRelation(CONGRUENT, newFriend0, newFriend1);
			newRel.addParents(Arrays.asList(rel, friend0, friend1));
			newRel.setReason(suppOrComp == COMPLEMENTARY ? ProofReasons.COMP_ANGLES_TO_CONG_ANGLES
					: ProofReasons.SUPP_ANGLES_TO_CONG_ANGLES);
			addFigureRelation(newRel);
		}
	}
	
	private void identifyComplementaryAngles(FigureRelation rel) {
		// Get information about the right angle
		Angle rightAngle = rel.getFigure0();
		Segment[] rightAngleSides = rightAngle.getSides();
		
		List<Segment> segs = getFiguresOfType(Segment.class);
		for (Segment seg : segs) {
			// CONDITIONS:
			if (
				// No component segments, just compound and independent segments
				   seg.equals(getLargestCompoundSegmentOf(seg.getName()))
				// Must run through the center of the angle
				&& seg.containsPoint(rightAngle.getCenter())
				// Cannot contain either side of the angle (we want it to go through the angle,
				// not along it)
				&& !seg.containsSegment(rightAngleSides[0])
				&& !seg.containsSegment(rightAngleSides[1])
			) {
				// Name of the angle being bisected
				String rightAngleName = rightAngle.getName();
				// Get the smallest segment cutter
				String smallestCutter = ProofUtils.getSmallestAngleCutter(this, rightAngle, seg);
				// Smallest cutter endpoint (opposite of POI)
				String smallestCutterEndpt = smallestCutter.substring(1);
				// Get the two complementary angles
				String angle0 = rightAngleName.substring(0, 2) + smallestCutterEndpt;
				String angle1 = smallestCutterEndpt + rightAngleName.substring(1);
				
				FigureRelation compRel = new FigureRelation(
						COMPLEMENTARY,
						getPrimaryAngleSynonym(angle0),
						getPrimaryAngleSynonym(angle1)
				);
				compRel.addParent(rel);
				compRel.setReason(ProofReasons.DEF_COMP);
				addFigureRelation(compRel);
			}
		}
	}
	
	/**
	 * Add the given {@link FigureRelation} to this {@link Diagram}.
	 * @param pair the {@link FigureRelation}
	 * @return false if the given {@link FigureRelation} is already
	 * contained in this {@link Diagram}, true if the operation
	 * was successful
	 */
	@SuppressWarnings("incomplete-switch") // Don't care--only want specific cases
	public boolean addFigureRelation(FigureRelation pair) {
		// Enforce policy
		if (policy == Policy.FIGURES_ONLY) {
			throw new IllegalStateException("Operation goes against policy: " 
				+ Policy.FIGURES_ONLY);
		}
		
		// No duplicates!!
		if (containsFigureRelation(pair))
			return false;
		FigureRelationType relType = pair.getRelationType();
		
		if (relations.add(pair)) {
			switch (relType) {
			// If the relation declares that an angle is a right angle,
			// make this right angle congruent to all other right angles.
			case RIGHT:
				makeRightAngle(pair); 
				identifyComplementaryAngles(pair);
				break;
			case CONGRUENT: case SIMILAR: case PARALLEL:
				// Apply the transitive postulate
				applyTransitivePostulate(pair, relType, relType, ProofReasons.TRANSITIVE);
				// If the relation is not congruent and reflexive
				if (relType == CONGRUENT && !pair.isCongruentAndReflexive()) {
					// If this FigureRelation involves two angles
					if (pair.getFigure0() instanceof Angle) {
						// Make it a right angle if it is congruent to a right angle
						addTransitiveRightAngles(pair);
						// If an angle is supp/complementary to one of a pair of congruent angles, 
						// it is supp/comp to the other
						applyTransitivePostulate(pair, SUPPLEMENTARY, SUPPLEMENTARY, 
								ProofReasons.SUPP_ANGLE_TO_CONG_ANGLES);
						applyTransitivePostulate(pair, COMPLEMENTARY, COMPLEMENTARY, 
								ProofReasons.COMP_ANGLE_TO_CONG_ANGLES);
						// If a pair of supp/comp angles are supp/comp to the same pair of
						// congruent angles, they are congruent
						addTransitiveSuppCompAngles(pair, SUPPLEMENTARY);
						addTransitiveSuppCompAngles(pair, COMPLEMENTARY);
					}
					// If two triangles are congruent, all of their corresponding children figures
					// are congruent as well
					else if (pair.getFigure0() instanceof Triangle) {
						// Make parts of congruent triangles congruent
						addFigureRelations(ProofUtils.getCongruentPartsOfCongruentTriangles(
								this, pair.getFigure0(), pair.getFigure1(), pair));
					}
				}
				break;
			case SUPPLEMENTARY:
				// If two angles are supplementary to the same angle, they are congruent
				applyTransitivePostulate(pair, SUPPLEMENTARY, CONGRUENT, 
						ProofReasons.SHARED_SUPPLEMENTARY_ANGLE);
				break;
			case COMPLEMENTARY:
				// If two angles are complementary to the same angle, they are congruent
				applyTransitivePostulate(pair, COMPLEMENTARY, CONGRUENT, 
						ProofReasons.SHARED_COMPLEMENTARY_ANGLE);
				// Now that we have a new pair of complementary angles, check to see
				// if they are complementary to a pair of congruent angles, which would make
				// them congruent
				final int count = relations.size();
				for (int i = 0; i < count; i++) {
					FigureRelation rel = relations.get(i);
					if (rel.getRelationType() == CONGRUENT && !rel.isCongruentAndReflexive()
						&& rel.getFigure0() instanceof Angle) {
						addTransitiveSuppCompAngles(rel, COMPLEMENTARY);
					}
				}
				break;
			case PERPENDICULAR:
				// If two segments are perpendicular to the same segment, they are parallel
				applyTransitivePostulate(pair, PERPENDICULAR, PARALLEL, 
						ProofReasons.PERPENDICULAR_TRANSITIVE);
				break;
			}
			return true; // Added FigureRelation
		}
		return false; // Didn't add FigureRelation
	}
	
	public FigureRelation getFigureRelation(FigureRelationType type, Figure f0, Figure f1) {
		FigureRelation query = new FigureRelation(type, f0, f1);
		for (FigureRelation rel : relations) {
			if (FigureRelation.safeEquals(rel, query))
				return rel;
		}
		return null;
	}
	
	public void addFigureRelations(Collection<FigureRelation> figs) {
		for (FigureRelation fig : figs) {
			addFigureRelation(fig);
		}
	}
	
	public void addFigureRelations(FigureRelation[] figs) {
		for (FigureRelation fig : figs) {
			addFigureRelation(fig);
		}
	}
	
//	public boolean removeFigureRelation(FigureRelationType type, String fig0,
//			String fig1, FigureRelation parent) {
//		return relations.remove(valueOf(type, fig0, fig1, parent));
//	}
	public boolean removeFigureRelation(FigureRelation pair) {
		return relations.remove(pair);
	}
	
	public boolean removeFigureRelation(Collection<FigureRelation> figs) {
		return relations.removeAll(figs);
	}
	
	public boolean containsFigureRelation(FigureRelation rel) {
		return relations.contains(rel);
	}
	
//	public boolean containsFigureRelation(FigureRelationType type, String fig0,
//			String fig1, FigureRelation parent) {
//		return relations.contains(valueOf(type, fig0, fig1, parent));
//	}
	
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
		return relations;
	}
}
