package geometry.proofs;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Deque;
import java.util.ArrayDeque;

import geometry.shapes.Angle;
import geometry.shapes.Segment;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;
import geometry.proofs.FigureRelationType;

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
		// Inflate the given, get all available figure relations
		inflateGiven();
		// Check if the proof goal is included in the inflated given
		for (FigureRelation pair : diagram.getFigureRelations()) {
			if (pair.equals(diagram.getProofGoal())) {
				traceback(pair);
				return proofWasSolved = result = true;
			}
		}
		proofWasSolved = true;
		return result = false;
	}
	
	private void traceback(FigureRelation target) {
		FigureRelation level = target;
		Deque<FigureRelation> stack = new ArrayDeque<>();
		
		while (level != null) {
			stack.push(level);
			level = level.getParent();
		}
		
		stack.forEach(System.out::println);
	}
	
	private void inflateGiven() {
		List<FigureRelation> given = new ArrayList<>(diagram.getFigureRelations());
		
		for (FigureRelation pair : given) {
			switch (pair.getRelationType()) {
			case CONGRUENT:
				// Null parent
				handleCongruentPair(pair, null);
				break;
			case PARALLEL:
				break;
			case PERPENDICULAR:
				handlePerpendicularPair(pair);
				break;
			case BISECTS:
				handleBisectPair(pair);
				break;
			case SIMILAR:
				handleSimilarTriangles(pair);
				break;
			case COMPLEMENTARY:
			case SUPPLEMENTARY:
			case RIGHT:
				break;
			case MIDPOINT:
				handleMidpoint(pair);
			}
		}		
	}
	
	private void handleCongruentPair(FigureRelation pair, FigureRelation parent) {
		// If two triangles are congruent, all of their corresponding children figures
		// are congruent as well
		if (pair.getFigure0().getClass() == Triangle.class) {
			// Ensure given parent is not null
//			if (parent == null)
//				throw new NullPointerException("Null parent");
			// First triangle
			Triangle tri0 = pair.getFigure0();
			// Second triangle
			Triangle tri1 = pair.getFigure1();
			// For each child figure in the first triangle
			for (int i = 0; i < tri0.getChildren().size(); i++) {
				// Child figure of the first triangle
				Figure child0 = tri0.getChildren().get(i);
				// Child figure of the second triangle
				Figure child1 = tri1.getChildren().get(i);
				// Ignore vertices--cannot make vertices congruent
				if (child0.getClass() == Vertex.class || child1.getClass() == Vertex.class)
					continue;
				// Make congruent pair
				FigureRelation rel = new FigureRelation(
						FigureRelationType.CONGRUENT,
						child0,
						child1,
						pair // Parent
				);
				diagram.addFigureRelationPair(rel);
			}
		}
	}
	
	private void handlePerpendicularPair(FigureRelation pair) {
		String angleName = Utils.getAngleBetween(pair.getFigure0(), pair.getFigure1());
		diagram.makeRightAngle(angleName, pair);
	}
	
	private void handleBisectPair(FigureRelation pair) {
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
				seg1,
				pair // Parent
		);
		handleMidpoint(rel);
	}
	
	private void handleMidpoint(FigureRelation pair) {
		Vertex vert = pair.getFigure0();
		Segment seg = pair.getFigure1();
		
		Segment newSeg0 =
				diagram.getFigure(seg.getName().substring(0, 1) + vert.getName());
		Segment newSeg1 =
				diagram.getFigure(vert.getName() + seg.getName().substring(1));
		
		FigureRelation rel = new FigureRelation(
				FigureRelationType.CONGRUENT,
				newSeg0,
				newSeg1,
				pair // Parent
		);
		diagram.addFigureRelationPair(rel);
	}
	
	private void handleSimilarTriangles(FigureRelation pair) {
		Triangle tri0 = pair.getFigure0();
		Triangle tri1 = pair.getFigure1();
		
		// Get corresponding angles in triangles
		List<SimpleEntry<Angle, Angle>> corrAngles = getCorrespondingAngles(tri0, tri1);

		// Length of corrAngles should always be 3
		for (int i = 0; i < corrAngles.size(); i++) {
			FigureRelation rel = new FigureRelation(
					FigureRelationType.CONGRUENT,
					corrAngles.get(i).getKey(),
					corrAngles.get(i).getValue(),
					pair // Parent
			);
			diagram.addFigureRelationPair(rel);
		}
	}
	
	private List<SimpleEntry<Angle, Angle>> getCorrespondingAngles(
			Triangle tri0, Triangle tri1) {
		// List of Map entries
		List<SimpleEntry<Angle, Angle>> list = new ArrayList<>();
		
		// For each angle in the first triangle
		outer:
		for (Angle a0 : tri0.getAngles()) {
			// For each angle in the second triangle
			for (Angle a1 : tri1.getAngles()) {
				// If the measures of the angles are equal
				if (a0.getAngle() == a1.getAngle()) {
					list.add(new SimpleEntry<>(a0, a1));
					continue outer;
				}
			}
		}
		
		return list;
	}
}
	
