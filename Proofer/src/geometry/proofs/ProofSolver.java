package geometry.proofs;

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
		
		System.out.println("----------------Traceback---------------");
		stack.forEach(System.out::println);
	}
	
	private void inflateGiven() {
		int totalRelsAdded;
		
		do {
			// Total number of figure relations BEFORE inflating the given
			final int relCountBefore = diagram.getFigureRelations().size();
			
			for (int i = 0; i < relCountBefore; i++) {
				FigureRelation pair = diagram.getFigureRelations().get(i);
				
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
			
			// Discover congruent triangles
			findAndAddCongruentTriangles();
			
			// Update
			totalRelsAdded = diagram.getFigureRelations().size() - relCountBefore;
		
		// Keep inflating the given while there are still figure relations to add
		} while (totalRelsAdded > 0);
		
		System.out.println("--------Figure Relations---------");
		diagram.getFigureRelations().forEach(System.out::println);
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
		String angleName = Utils.getAngleBetween(
				pair.getFigure0().getName(), pair.getFigure1().getName());
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
		List<Angle[]> corrAngles = getCorrespondingAngles(tri0, tri1);

		// Length of corrAngles should always be 3
		for (int i = 0; i < corrAngles.size(); i++) {
			FigureRelation rel = new FigureRelation(
					FigureRelationType.CONGRUENT,
					corrAngles.get(i)[0],
					corrAngles.get(i)[1],
					pair // Parent
			);
			diagram.addFigureRelationPair(rel);
		}
	}
	
	private List<Angle[]> getCorrespondingAngles(
			Triangle tri0, Triangle tri1) {
		// List of pairs
		List<Angle[]> list = new ArrayList<>();
		// As we loop through the pairs of angles between the triangles,
		// We need to keep track of which pairs of angles correspond to each
		// other. For ex., if angle "ABC" in triangle #1 corresponds with angle
		// "DEF" in triangle #2, angle "DEF" cannot correspond to any other
		// angle in the FIRST triangle. We will keep track of all of the angles
		// in triangle #2 to ensure that we don't use them twice.
		List<Integer> usedAngles = new ArrayList<>(); // Indices
		
		// For each angle in the first triangle
		outer:
		for (Angle a0 : tri0.getAngles()) {
			// For each angle in the second triangle
			for (int i = 0; i < tri1.getAngles().length; i++) {
				if (usedAngles.contains(i))
					continue;
				Angle a1 = tri1.getAngles()[i];
				// If the measures of the angles are equal
				if (a0.getAngle() == a1.getAngle()) {
					list.add(new Angle[] { a0, a1 });
					usedAngles.add(i); // Record used angle
					continue outer;
				}
			}
		}
		
		return list;
	}
	
	private List<Segment[]> getCorrespondingSegments(
			Triangle tri0, Triangle tri1) {
		// List of pairs
		List<Segment[]> list = new ArrayList<>();
		// As we loop through the pairs of segments between the triangles,
		// We need to keep track of which pairs of segments correspond to each
		// other. For ex., if segment "AB" in triangle #1 corresponds with segment
		// "DE" in triangle #2, segment "DE" cannot correspond to any other
		// segment in the FIRST triangle. We will keep track of all of the segments
		// in triangle #2 to ensure that we don't use them twice.
		List<Integer> usedSegments = new ArrayList<>(); // Indices
		
		// For each segment in the first triangle
		outer:
		for (Segment a0 : tri0.getSides()) {
			// For each segment in the second triangle
			for (int i = 0; i < tri1.getSides().length; i++) {
				if (usedSegments.contains(i))
					continue;
				Segment a1 = tri1.getSides()[i];
				// If the measures of the segments are equal
				if (a0.getLength(false) == a1.getLength(false)) {
					list.add(new Segment[] { a0, a1 });
					usedSegments.add(i); // Record used segment
					continue outer;
				}
			}
		}
		
		return list;
	}
	
	private void findAndAddCongruentTriangles() {
		// We don't want to check the same PAIR of triangles more than once,
		// so we'll create a list to store the pairs we've checked already
		List<int[]> checkedTriPairs = new ArrayList<>();
		
		// For each figure
		for (int i = 0; i < diagram.getFigures().size(); i++) {
			// Make sure figure is a triangle
			if (!(diagram.getFigures().get(i) instanceof Triangle))
				continue;
			// For each other figure
			j_loop:
			for (int j = 0; j < diagram.getFigures().size(); j++) {
				// Make sure we're not comparing the same two figures AND
				// the second figure must be a triangle
				if (i == j || !(diagram.getFigures().get(j) instanceof Triangle))
					continue;
				
				// Don't want to compare pairs of triangles that have
				// already been compared
				for (int[] triPair : checkedTriPairs) {
					if ((triPair[0] == i && triPair[1] == j) ||
							(triPair[0] == j && triPair[1] == i))
						continue j_loop;
				}
				
				// Convenience
				Triangle tri0 = (Triangle)diagram.getFigures().get(i);
				Triangle tri1 = (Triangle)diagram.getFigures().get(j);
				
				// Remember this pair of triangles--don't want to use again
				checkedTriPairs.add(new int[] { i, j });
				
				// SSS
				if (getCorrespondingSegments(tri0, tri1).size() == 3) {
					// Make triangles congruent
					diagram.addFigureRelationPair(
						FigureRelationType.CONGRUENT,
						tri0.getName(),
						tri1.getName(),
						null // TODO: make this SSS
					);
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
	
