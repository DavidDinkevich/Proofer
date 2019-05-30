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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;


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
			// is the smallest, move it to the front (it will become the new primary angle synonym).
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
		return ProofUtils.addLeastToGreatestDist(vertex, node.getChildren()) >= 0;
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
		return largestSeg;
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
		
		if (node == null)
			return all;
		
		// Add all children
		for (Vertex v : node.getChildren()) {
			ProofUtils.addLeastToGreatestDist(v, all);
		}
		// Do the same for each component segment
		for (int i = 0; i < node.getChildren().size() - 1; i++) {
			Vertex first = node.getChildren().get(i);
			Vertex second = node.getChildren().get(i + 1);
			Segment segment = new Segment(first, second);
			breakToUnitComponentVertices(getCompoundSegmentNode(segment.getName()), all);
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
		FigureRelation rel = new FigureRelation(FigureRelationType.CONGRUENT, fig, fig);
		rel.setReason(ProofReasons.REFLEXIVE);
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
//				rel.getRelationType() != FigureRelationType.CONGRUENT
				// FigureRelation must not be reflexive
//				|| rel.isCongruentAndReflexive()
				rel.isCongruentAndReflexive()
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
						FigureRelationType.CONGRUENT, newFriend0, newFriend1);
				newRel.setReason(ProofReasons.TRANSITIVE);
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
	 * other right angles in the list of {@link FigureRelation}s.
	 * @param rightAngleRel the {@link FigureRelation} that makes the angle
	 * a right angle
	 */
	private void makeRightAngle(FigureRelation rightAngleRel) {
		// Get the angle
		Angle angle = rightAngleRel.getFigure0();
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
						FigureRelationType.CONGRUENT, angle, pair.getFigure0());
				newPair.addParent(rightAngleRel);
				newPair.addParent(pair);
				newPair.setReason(ProofReasons.RIGHT_ANGLES_CONGRUENT);

				// Ensure that we're not adding a duplicate
				if (!containsFigureRelation(newPair)) {
					relations.add(newPair);
				}
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
	public boolean addFigureRelation(FigureRelation pair) {
		// Enforce policy
		if (policy == Policy.FIGURES_ONLY) {
			throw new IllegalStateException("Operation goes against policy: " + Policy.FIGURES_ONLY);
		}
		
		// No duplicates!!
		if (containsFigureRelation(pair))
			return false;
		
		if (relations.add(pair)) {
			// If the relation declares that an angle is a right angle,
			// make this right angle congruent to all other right angles.
			if (pair.getRelationType() == FigureRelationType.RIGHT) {
				makeRightAngle(pair);
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
					for (Angle[] anglePair : ProofUtils.getCorrespondingAngles(tri0, tri1)) {
						// Make congruent pair
						FigureRelation newPair = new FigureRelation(
								FigureRelationType.CONGRUENT,
								anglePair[0],
								anglePair[1]
						);
						newPair.addParent(pair);
						newPair.setReason(ProofReasons.CORR_ANGLES_CONG_TRIANGLES);
						addFigureRelation(newPair);
					}
					// For all corresponding segments
					for (Segment[] segPair : ProofUtils.getCorrespondingSegments(tri0, tri1)) {
						// Make congruent pair
						FigureRelation newPair = new FigureRelation(
								FigureRelationType.CONGRUENT,
								segPair[0],
								segPair[1]
						);
						newPair.addParent(pair);
						newPair.setReason(ProofReasons.CORR_SEGMENTS);
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
