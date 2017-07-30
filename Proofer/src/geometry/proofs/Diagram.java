package geometry.proofs;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Diagram {
	private List<Figure> figures;
	private List<FigureRelationPair> relations;
	private FigureRelationPair proofGoal;
	
	public Diagram() {
		figures = new ArrayList<>();
		relations = new ArrayList<>();
	}
	
	public FigureRelationPair getProofGoal() {
		return proofGoal;
	}
	
	/**
	 * Set the goal of this proof
	 * @return the old goal, or null if there was no previous
	 */
	public FigureRelationPair setProofGoal(FigureRelationType rel, String fig0, String fig1) {
		FigureRelationPair newGoal = valueOf(rel, fig0, fig1);
		if (newGoal == null)
			return null;
		return setProofGoal(newGoal);
	}
	
	/**
	 * Set the goal of this proof
	 * @param newGoal the new goal
	 * @return the old goal, or null if there was no previous
	 */
	public FigureRelationPair setProofGoal(FigureRelationPair newGoal) {
		FigureRelationPair old = proofGoal;
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
		if (containsFigure(fig))
			return false;
		figures.add(fig);
		
		List<Figure> buff = new ArrayList<>(fig.getChildren());
		for (Figure f : buff) {
			if (!containsFigure(f))
				figures.add(f);
		}
		
		while (!buff.isEmpty()) {
			List<Figure> buff2 = new ArrayList<>(buff);
			buff.clear();
			// Fill buffer
			for (int i = 0; i < buff2.size(); i++) {
				buff.addAll(buff2.get(i).getChildren());
			}
			// Add children
			for (Figure f : buff) {
				if (!containsFigure(f))
					figures.add(f);
			}
		}
		return true;
	}
	
	public void addFigures(Collection<Figure> figs) {
		for (Figure fig : figs) {
			addFigure(fig);
		}
	}
	
	public boolean removeFigure(Figure fig) {
		return figures.remove(fig);
	}
	
	public boolean removeFigures(Collection<Figure> figs) {
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
	
	public boolean containsFigures(Collection<Figure> figs) {
		return figures.containsAll(figs);
	}
	
	public List<Figure> getFigures() {
		return Collections.unmodifiableList(figures);
	}
	
	/////////////////////////////
	
	private FigureRelationPair valueOf(FigureRelationType type, String fig0, String fig1) {
		final boolean RIGHT_ANGLE = type == FigureRelationType.RIGHT;
		// Special search for right angles
		Figure figure0 = !RIGHT_ANGLE ? getFigure(fig0) : getFigure(fig0, AngleFigure.class);
		Figure figure1 = null;
		if (!RIGHT_ANGLE)
			figure1 = getFigure(fig1);
		if (figure0 == null || (!RIGHT_ANGLE && figure1 == null))
			return null;
		return new FigureRelationPair(type, figure0, figure1);
	}
	
	public boolean addFigureRelationPair(FigureRelationType type, String fig0, String fig1) {
		FigureRelationPair pair = valueOf(type, fig0, fig1);
		return pair == null ? false : relations.add(pair);
	}
	
	public boolean addFigureRelationPair(FigureRelationPair pair) {
		if (!containsFigures(pair.getFigures()))
			return false;
		return relations.add(pair);
	}
	
	public boolean addFigureRelationPairs(Collection<FigureRelationPair> figs) {
		for (FigureRelationPair fig : figs) {
			if (!containsFigures(fig.getFigures()))
				return false;
		}
		return relations.addAll(figs);
	}
	
	public boolean removeFigureRelationPair(FigureRelationType type, String fig0, String fig1) {
		return relations.remove(valueOf(type, fig0, fig1));
	}
	public boolean removeFigureRelationPair(FigureRelationPair pair) {
		return relations.remove(pair);
	}
	
	public boolean removeFigureRelationPairs(Collection<FigureRelationPair> figs) {
		return relations.removeAll(figs);
	}
	
	public boolean containsFigureRelationPair(FigureRelationPair rel) {
		return relations.contains(rel);
	}
	
	public boolean containsFigureRelationPair(FigureRelationType type, String fig0, String fig1) {
		return relations.contains(valueOf(type, fig0, fig1));
	}
	
	public boolean containsFigureRelationPairs(Collection<FigureRelationPair> figs) {
		return relations.containsAll(figs);
	}
	
	public FigureRelationPair getFirstRelationPairWithType(FigureRelationType type) {
		for (FigureRelationPair pair : relations) {
			if (pair.getRelationType() == type) {
				return pair;
			}
		}
		return null;
	}
	
	public List<FigureRelationPair> getAllRelationPairsWithType(FigureRelationType type) {
		List<FigureRelationPair> rels = null;
		for (FigureRelationPair pair : relations) {
			if (pair.getRelationType() == type) {
				if (rels == null)
					rels = new ArrayList<>();
				rels.add(pair);
			}
		}
		return rels == null ? Collections.emptyList() : rels;
	}
	
	public List<FigureRelationPair> getFigureRelationPairs() {
		return Collections.unmodifiableList(relations);
	}
}
