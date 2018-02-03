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
					break;
				case MIDPOINT:
					handleMidpoint(pair);
				default:
					break;
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
	
	/**
	 * Convenience method to check if two figures are congruent
	 */
	private boolean isCongruent(String f0, String f1) {
		return diagram.containsFigureRelation(FigureRelationType.CONGRUENT, f0, f1, null);
	}
	
	/**
	 * Convenience method to check if two figures are congruent
	 */
	private boolean isCongruent(Figure f0, Figure f1) {
		return diagram.containsFigureRelation(
				new FigureRelation(FigureRelationType.CONGRUENT, f0, f1, null));
	}
	
	private void handlePerpendicularPair(FigureRelation pair) {
		// Make sure the given FigureRelation is of type PERPENDICULAR
		if (pair.getRelationType() != FigureRelationType.PERPENDICULAR) {
			throw new IllegalArgumentException("Given FigureRelation"
					+ " must be of type PERPENDICULAR");
		}
				
		String angleName = Utils.getAngleBetween(
				pair.getFigure0().getName(), pair.getFigure1().getName());
		diagram.makeRightAngle(angleName, pair);
	}
	
	private void handleBisectPair(FigureRelation pair) {
		// Make sure the given FigureRelation is of type BISECTS
		if (pair.getRelationType() != FigureRelationType.BISECTS) {
			throw new IllegalArgumentException("Given FigureRelation"
					+ " must be of type BISECTS");
		}
		
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
		// Make sure the given FigureRelation is of type MIDPOINT
		if (pair.getRelationType() != FigureRelationType.MIDPOINT) {
			throw new IllegalArgumentException("Given FigureRelation"
					+ " must be of type MIDPOINT");

		}
		
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
		diagram.addFigureRelation(rel);
	}
	
	private void handleSimilarTriangles(FigureRelation pair) {
		// Make sure the given FigureRelation is of type SIMILAR
		if (pair.getRelationType() != FigureRelationType.SIMILAR) {
			throw new IllegalArgumentException("Given FigureRelation"
					+ " must be of type SIMILAR");
		}
		
		Triangle tri0 = pair.getFigure0();
		Triangle tri1 = pair.getFigure1();
		
		// Get corresponding angles in triangles
		List<Angle[]> corrAngles = Utils.getCorrespondingAngles(tri0, tri1);

		// Length of corrAngles should always be 3
		for (int i = 0; i < corrAngles.size(); i++) {
			FigureRelation rel = new FigureRelation(
					FigureRelationType.CONGRUENT,
					corrAngles.get(i)[0],
					corrAngles.get(i)[1],
					pair // Parent
			);
			diagram.addFigureRelation(rel);
		}
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
								
				// Check if triangles are congruent (SSS, SAS, ASA)
				if (
						congruentBySSS(tri0, tri1) // SSS
					 || congruentBySAS(tri0, tri1) // SAS
					 || congruentByASA(tri0, tri1) // ASA
					) {
					
					// Make triangles congruent
					diagram.addFigureRelation(
						FigureRelationType.CONGRUENT,
						tri0.getName(),
						tri1.getName(),
						null // TODO: make this SSS
					);
				}
			}
		}
	}
	
	private boolean congruentBySSS(Triangle tri0, Triangle tri1) {
		// Three congruent segments
		return getRecordedCorrespondingSegs(tri0, tri1).size() == 3;
	}
	
	private boolean congruentBySAS(Triangle tri0, Triangle tri1) {		
		// Get the angles of each triangle
		Angle[] tri0Angles = tri0.getAngles();
		Angle[] tri1Angles = tri1.getAngles();

		// For each angle in the first triangle
		for (int i = 0; i < tri0Angles.length; i++) {
			Angle a0 = tri0Angles[i]; // Get the angle
			
			// For each angle in the second triangle
			for (int j = 0; j < tri1Angles.length; j++) {
				Angle a1 = tri1Angles[j]; // Get the angle
				
				// If the two angles are congruent (if the Diagram contains a
				// FigureRelation that says so)
				if (isCongruent(a0, a1)) {
					
					// Get the adjacent segments of each angle
					Segment[] segs0 = tri0.getAdjacentSegments(a0.getNameShort());
					Segment[] segs1 = tri1.getAdjacentSegments(a1.getNameShort());
					
					// Keep track of the number of congruent, adjacent pairs of segments
					// for each angle (2 are needed to make the two triangles
					// congruent)
					int congruentSegmentPairs = 0;
					
					// Check for congruent adjacent segments
					for (Segment s0 : segs0) {
						for (Segment s1 : segs1) {
							// Check if congruent
							if (isCongruent(s0.getName(), s1.getName())) {
								// If the adjacent segments are congruent, update variable
								++congruentSegmentPairs;
							}
						}
					}
					
					// 2 congruent adjacent segments are needed for the triangles
					// to be congruent by SAS
					if (congruentSegmentPairs >= 2) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private boolean congruentByASA(Triangle tri0, Triangle tri1) {
		// Get the sides of each triangle
		Segment[] segs0 = tri0.getSides();
		Segment[] segs1 = tri1.getSides();
		
		// For each side in the first triangle
		for (int i = 0; i < segs0.length; i++) {
			Segment s0 = segs0[i]; // Get the side
			// For each side in the second triangle
			for (int j = 0; j < segs1.length; j++) {
				Segment s1 = segs1[j]; // Get the side
				
				// IF THESE SIDES ARE CONGRUENT (if the Diagram contains a
				// FigureRelation that says so)
				if (isCongruent(s0.getName(), s1.getName())) {
					// Get the adjacent angles around the first segment
					String[] adjacentAngles0 = 
							Utils.getSurroundingAngles(tri0.getName(), s0.getName());
					// Get the adjacent angles around the second segment
					String[] adjacentAngles1 =
							Utils.getSurroundingAngles(tri1.getName(), s1.getName());
					
					// Keep track of the number of congruent, adjacent pairs of angles
					// for each segment (2 are needed to make the two triangles
					// congruent)
					int congruentAnglePairs = 0;
					
					// Check for congruent adjacent pairs of angles
					
					// For each adjacent angle of the first segment
					for (int a = 0; a < adjacentAngles0.length; a++) {
						Angle a0 = tri0.getAngle(adjacentAngles0[a]); // Get the angle
						// For each adjacent angle of the second segment
						for (int b = 0; b < adjacentAngles1.length; b++) {
							// Get the angle
							Angle a1 = tri1.getAngle(adjacentAngles1[b]);

							// IF THE TWO ANGLES ARE CONGRUENT
							if (isCongruent(a0, a1)) {
								// If the adjacent angles are congruent, update variable
								++congruentAnglePairs;
							}
						}
					}
					
					// 2 congruent adjacent angles are needed for the triangles
					// to be congruent by SAS
					if (congruentAnglePairs >= 2) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Get the corresponding segments in the two given triangles as defined
	 * by the figure relations in the diagram's figure relations list.
	 * @param tri0 the first triangle
	 * @param tri1 the second triangle
	 * @return the list of corresponding segments as defined by figure
	 * relations
	 */
	private List<Segment[]> getRecordedCorrespondingSegs(
			Triangle tri0, Triangle tri1) {
		// List of pairs
		List<Segment[]> list = new ArrayList<>();
		
		// Get all the corresponding segments between the two triangles
		for (Segment[] pair : Utils.getCorrespondingSegments(tri0, tri1)) {
			// Make sure there is a figure relation that says that the figures
			// in each pair of corresponding segments are congruent
			for (FigureRelation rel : diagram.getFigureRelations()) {
				// Check if figures are congruent
				if (rel.containsFigure(pair[0]) && rel.containsFigure(pair[1])
						&& rel.getRelationType() == FigureRelationType.CONGRUENT) {
					list.add(pair); // Add the pair
					break;
				}
			}
		}
		
		return list;
	}
}
