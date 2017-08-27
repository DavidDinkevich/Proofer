package geometry.proofs;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import geometry.shapes.Angle;
import geometry.shapes.Segment;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;

import util.Utils;

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
		List<FigureRelation> fullGiven = new ArrayList<>(diagram.getFigureRelations());
		// Pre-algorithm preparation
		doPreAlgorithmOps(fullGiven);
		// Inflate the given, get all available figure relations
		fullGiven.addAll(inflateGiven());
		// Check if the proof goal is included in the inflated given
		for (FigureRelation pair : fullGiven) {
			if (pair.equals(diagram.getProofGoal())) {
				return proofWasSolved = result = true;
			}
		}
		proofWasSolved = true;
		return result = false;
	}
	
	private List<FigureRelation> inflateGiven() {
		List<FigureRelation> given = diagram.getFigureRelations();
		List<FigureRelation> fullGiven = new ArrayList<>();
		
		for (FigureRelation pair : given) {
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
				handleSimilarTriangles(fullGiven, pair);
				break;
			case COMPLEMENTARY:
			case SUPPLEMENTARY:
			case RIGHT:				
			case MIDPOINT:
				handleMidpoint(fullGiven, pair);
			}
		}
		return fullGiven;
	}
	
	private void doPreAlgorithmOps(List<FigureRelation> relations) {		
		applyReflexivePostulate(relations);
		makeAllRightAnglesCongruent(relations);
		handleVerticalAngles(relations);
	}
	
	/**
	 * Make the given angle a right angle, and make it congruent to all
	 * other right angles in the given list of {@link FigureRelation}s.
	 * @param a the angle
	 * @param relations the list of {@link FigureRelation}s.
	 */
	private void makeRightAngle(Angle a, List<FigureRelation> relations) {
		// Make angle a right angle
		FigureRelation rel = new FigureRelation(
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
			FigureRelation pair = relations.get(i);
			if (pair.getRelationType() == FigureRelationType.RIGHT) {
				FigureRelation newPair = new FigureRelation(
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
	private void applyReflexivePostulate(Collection<FigureRelation> relations) {
		// Enforce reflexive postulate--every figure is congruent to itself
		// Vertices cannot be "congruent"
		for (Figure fig : diagram.getFigures()) {
			if (fig.getClass() != Vertex.class) {
				FigureRelation pair = new FigureRelation(
						FigureRelationType.CONGRUENT,
						fig,
						fig
					);
				relations.add(pair);
			}
		}
		
	}
	
	// List needed for random access
	private void makeAllRightAnglesCongruent(List<FigureRelation> relations) {
		// Make all right angles congruent
		for (int i = 0; i < relations.size(); i++) {
			FigureRelation pair0 = relations.get(i);
			// If this pair marks an angle a right angle
			if (pair0.getRelationType() == FigureRelationType.RIGHT) {
				for (int j = 0; j < relations.size(); j++) {
					if (j == i)
						continue;
					FigureRelation pair1 = relations.get(j);
					if (pair1.getRelationType() == FigureRelationType.RIGHT) {
						FigureRelation rel = new FigureRelation(
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
			Collection<FigureRelation> fullGiven, FigureRelation pair) {
		// If two triangles are congruent, all of their corresponding children figures
		// are congruent as well
		if (pair.getFigure0().getClass() == Triangle.class) {
			Triangle tri0 = pair.getFigure0();
			Triangle tri1 = pair.getFigure1();
			for (int i = 0; i < tri0.getChildren().size(); i++) {
				Figure child0 = tri0.getChildren().get(i);
				Figure child1 = tri1.getChildren().get(i);
				FigureRelation rel = new FigureRelation(
						FigureRelationType.CONGRUENT,
						child0,
						child1
				);
				fullGiven.add(rel);
			}
		}
	}
	
	private void handlePerpendicularPair(
			List<FigureRelation> relations, FigureRelation pair) {
		String angleName = Utils.getAngleBetween(pair.getFigure0(), pair.getFigure1());
		Angle angle = diagram.getFigure(angleName, Angle.class);
		makeRightAngle(angle, relations);
	}
	
	private void handleBisectPair(
			Collection<FigureRelation> relations, FigureRelation pair) {
		Segment seg0 = pair.getFigure0();
		Segment seg1 = pair.getFigure1();
		
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
		
		FigureRelation rel = new FigureRelation(
				FigureRelationType.MIDPOINT,
				vert,
				seg1
		);
		handleMidpoint(relations, rel);
	}
	
	private void handleMidpoint(
			Collection<FigureRelation> relations, FigureRelation pair) {
		Vertex vert = pair.getFigure0();
		Segment seg = pair.getFigure1();
		
		Segment newSeg0 =
				diagram.getFigure(seg.getName().substring(0, 1) + vert.getName());
		Segment newSeg1 =
				diagram.getFigure(vert.getName() + seg.getName().substring(1));
		
		FigureRelation rel = new FigureRelation(
				FigureRelationType.CONGRUENT,
				newSeg0,
				newSeg1
		);
		relations.add(rel);
	}
	
	private void handleSimilarTriangles(
			Collection<FigureRelation> relations, FigureRelation pair) {
		Triangle tri0 = pair.getFigure0();
		Triangle tri1 = pair.getFigure1();
		
		// Get corresponding angles in triangles
		List<SimpleEntry<Angle, Angle>> corrAngles = getCorrespondingAngles(tri0, tri1);
		
		for (int i = 0; i < 3; i++) {
			FigureRelation rel = new FigureRelation(
					FigureRelationType.CONGRUENT,
					corrAngles.get(i).getKey(),
					corrAngles.get(i).getValue()
			);
			relations.add(rel);
		}
	}
	
	private List<SimpleEntry<Angle, Angle>> getCorrespondingAngles(
			Triangle tri0, Triangle tri1) {
		
		List<SimpleEntry<Angle, Angle>> list = new ArrayList<>();
		
		outer:
		for (Angle a0 : tri0.getAngles()) {
			for (Angle a1 : tri1.getAngles()) {
				if (a0.getAngle() == a1.getAngle()) {
					list.add(new SimpleEntry<>(a0, a1));
					continue outer;
				}
			}
		}
		
		return list;
	}
	
	private void handleVerticalAngles(Collection<FigureRelation> relations) {
		// For each figure
		for (int i = 0; i < diagram.getFigures().size(); i++) {
			// If the figure is NOT an angle, we don't care about it
			if (!(diagram.getFigures().get(i) instanceof Angle))
				continue;
			Angle a0 = (Angle)diagram.getFigures().get(i);
			// For each other figure
			for (int j = 0; j < diagram.getFigures().size(); j++) {
				// If we're looking at the same figure OR the figure we're looking
				// at is NOT an angle
				if (i == j || !(diagram.getFigures().get(j) instanceof Angle))
					continue;
				Angle a1 = (Angle)diagram.getFigures().get(j);
				
				if (areVerticalAngles(a0, a1)) {
					FigureRelation rel = new FigureRelation(
							FigureRelationType.CONGRUENT,
							a0,
							a1
					);
					System.out.println(rel);
//					if (!relations.contains(rel))
						relations.add(rel);
					System.out.println(relations.size());
				}
			}
		}
	}
	
	private boolean areVerticalAngles(Angle a, Angle b) {
		String name0 = a.getName();
		String name1 = b.getName();
		int sharedVertCount = 0;
		if (a.getNameShort().equals(b.getNameShort())) {
			for (int i = 0; i < name0.length(); i++) {
				if (name0.indexOf(name1.charAt(i)) > -1)
					continue;
				++sharedVertCount;
				break;
			}
			return sharedVertCount == 1;
		}
		return false;
	}
}
	
