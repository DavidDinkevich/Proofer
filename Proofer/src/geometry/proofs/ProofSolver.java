package geometry.proofs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import geometry.shapes.Segment;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;

public class ProofSolver {
	private boolean proofWasSolved = false;
	private boolean result = false;
	private Diagram diagram;
	
	public ProofSolver(Diagram diagram) {
		this.diagram = diagram;
	}
	
	public ProofSolver() {
	}
	
	public Diagram setDiagram(Diagram diag) {
		proofWasSolved = false;
		Diagram d = diagram;
		diagram = diag;
		return d;
	}
	
	public Diagram getDiagram() {
		return diagram;
	}
	
	public boolean solve() {
		if (proofWasSolved)
			return result;
		if (diagram == null)
			throw new NullPointerException("Diagram is null");
		if (diagram.getProofGoal() == null)
			throw new NullPointerException("Proof goal is null.");
		// Solve proof here
		List<FigureRelationPair> fullGiven = new ArrayList<>(diagram.getFigureRelationPairs());
		doPreAlgorithmOperations(fullGiven);
		fullGiven.addAll(inflateGiven());
		for (FigureRelationPair pair : fullGiven) {
			if (pair.equals(diagram.getProofGoal())) {
				return proofWasSolved = result = true;
			}
		}
		proofWasSolved = true;
		return result = false;
	}
	
	private List<FigureRelationPair> inflateGiven() {
		List<FigureRelationPair> given = diagram.getFigureRelationPairs();
		List<FigureRelationPair> fullGiven = new ArrayList<>();
				
		for (FigureRelationPair pair : given) {
			switch (pair.getRelationType()) {
			case CONGRUENT:
				handleCongruentPair(fullGiven, pair);
				break;
			case PARALLEL:
				break;
			case PERPENDICULAR:
				handlePerpendicularPair(fullGiven, pair);
				break;
			case BISECTS:
				handleBisectPair(fullGiven, pair);
				break;
			case SIMILAR:
				
			case COMPLEMENTARY:
			case SUPPLEMENTARY:
			case RIGHT:
			case VERTICAL:
				
			case MIDPOINT:
				handleMidpoint(fullGiven, pair);
			}
		}
		return fullGiven;
	}
	
	private void doPreAlgorithmOperations(List<FigureRelationPair> relations) {		
		applyReflexivePostulate(relations);
		makeAllRightAnglesCongruent(relations);
	}
	
	/**
	 * Creates a {@link FigureRelationPair} rendering the given figure
	 * congruent to itself.
	 * @param fig the figure
	 * @return the {@link FigureRelationPair}, or null if the given figure is
	 * a {@link Vertex}.
	 */
	private FigureRelationPair createReflexiveRelationPair(Figure fig) {
		if (fig.getClass() != Vertex.class) {
			FigureRelationPair pair = new FigureRelationPair(
					FigureRelationType.CONGRUENT,
					fig,
					fig
				);
			return pair;
		}
		return null;
	}
	
	/**
	 * Make the given angle a right angle, and make it congruent to all
	 * other right angles in the given list of {@link FigureRelationPair}s.
	 * @param a the angle
	 * @param relations the list of {@link FigureRelationPair}s.
	 */
	private void makeRightAngle(AngleFigure a, List<FigureRelationPair> relations) {
		// Make angle a right angle
		FigureRelationPair rel = new FigureRelationPair(
				FigureRelationType.RIGHT,
				a,
				null
			);
		relations.add(rel);
		final int INDEX = relations.size()-1;
		// Make new right angle congruent to all other right angles in collection
		for (int i = 0; i < relations.size(); i++) {
			if (i == INDEX)
				continue;
			FigureRelationPair pair = relations.get(i);
			if (pair.getRelationType() == FigureRelationType.RIGHT) {
				FigureRelationPair newPair = new FigureRelationPair(
						FigureRelationType.CONGRUENT,
						a,
						pair.getFigure0()
					);
				relations.add(newPair);
			}
		}
	}
	
	/**
	 * Applies the reflexive postulate to all {@link Figure}s in the given
	 * {@link Collection} of {@link Figure}s.
	 * @param relations the collection of figures.
	 */
	private void applyReflexivePostulate(Collection<FigureRelationPair> relations) {
		// Enforce reflexive postulate--every figure is congruent to itself
		// Vertices cannot be "congruent"
		for (Figure fig : diagram.getFigures()) {
			if (fig.getClass() != Vertex.class) {
				FigureRelationPair pair = new FigureRelationPair(
						FigureRelationType.CONGRUENT,
						fig,
						fig
					);
				relations.add(pair);
			}
		}
		
	}
	
	// List needed for random access
	private void makeAllRightAnglesCongruent(List<FigureRelationPair> relations) {
		// Make all right angles congruent
		for (int i = 0; i < relations.size(); i++) {
			FigureRelationPair pair0 = relations.get(i);
			// If this pair marks an angle a right angle
			if (pair0.getRelationType() == FigureRelationType.RIGHT) {
				for (int j = 0; j < relations.size(); j++) {
					if (j == i)
						continue;
					FigureRelationPair pair1 = relations.get(j);
					if (pair1.getRelationType() == FigureRelationType.RIGHT) {
						FigureRelationPair rel = new FigureRelationPair(
								FigureRelationType.CONGRUENT,
								pair0.getFigure0(),
								pair1.getFigure0()
							);
						relations.add(rel);
					}
				}
			}
		}
	}
	
	private void handleCongruentPair(
			Collection<FigureRelationPair> fullGiven, FigureRelationPair pair) {
		if (pair.getFigure0().getClass() == Triangle.class) {
			Triangle tri0 = (Triangle)pair.getFigure0();
			Triangle tri1 = (Triangle)pair.getFigure1();
			for (int i = 0; i < tri0.getChildren().size(); i++) {
				Figure child0 = tri0.getChildren().get(i);
				Figure child1 = tri1.getChildren().get(i);
				FigureRelationPair rel = new FigureRelationPair(
						FigureRelationType.CONGRUENT,
						child0,
						child1
				);
				fullGiven.add(rel);
			}
		}
	}
	
	private void handlePerpendicularPair(
			List<FigureRelationPair> relations, FigureRelationPair pair) {
		String seg0 = ((Segment)pair.getFigure0()).getName();
		String seg1 = ((Segment)pair.getFigure1()).getName();
		
		// Get shared vertex between segments
		char shared, unshared0, unshared1; // 1 shared, 2 unshared
		final int index = seg1.indexOf(seg0.charAt(0));
		if (index >= 0) {
			shared = seg1.charAt(index);
			unshared0 = seg0.charAt(1);
			unshared1 = seg1.charAt(index == 0 ? 1 : 0);
		} else {
			unshared0 = seg0.charAt(0);
			shared = seg0.charAt(1);
			final int sharedCharIndex = seg1.indexOf(shared);
			unshared1 = seg1.charAt(sharedCharIndex == 0 ? 1 : 0);
		}
		
		// Angle 1
		String angleName = 
				String.valueOf(unshared0) + String.valueOf(shared) + String.valueOf(unshared1);
		AngleFigure angle = (AngleFigure)diagram.getFigure(angleName, AngleFigure.class);
		makeRightAngle(angle, relations);
	}
	
	private void handleBisectPair(
			Collection<FigureRelationPair> relations, FigureRelationPair pair) {
		Segment seg0 = (Segment)pair.getFigure0();
		Segment seg1 = (Segment)pair.getFigure1();
		
		final int index = seg1.getName().indexOf(seg0.getName().charAt(0));
		char sharedVertexName;
		Vertex vert;
		
		if (index >= 0) {
			sharedVertexName = seg1.getName().charAt(index);
			vert = (Vertex)seg1.getChild(String.valueOf(sharedVertexName));
		} else {
			sharedVertexName = seg0.getName().charAt(1);
			vert = (Vertex)seg0.getChild(String.valueOf(sharedVertexName));
		}
		
		FigureRelationPair rel = new FigureRelationPair(
				FigureRelationType.MIDPOINT,
				vert,
				seg1
		);
		handleMidpoint(relations, rel);
	}
	
	private void handleMidpoint(
			Collection<FigureRelationPair> relations, FigureRelationPair pair) {
		Vertex vert = (Vertex)pair.getFigure0();
		Segment seg = (Segment)pair.getFigure1();
		
		Segment newSeg0 =
				(Segment)diagram.getFigure(seg.getName().substring(0, 1) + vert.getName());
		Segment newSeg1 =
				(Segment)diagram.getFigure(vert.getName() + seg.getName().substring(1));
		
		FigureRelationPair rel = new FigureRelationPair(
				FigureRelationType.CONGRUENT,
				newSeg0,
				newSeg1
		);
		relations.add(rel);
		relations.add(createReflexiveRelationPair(newSeg0));
		relations.add(createReflexiveRelationPair(newSeg1));
	}
}
