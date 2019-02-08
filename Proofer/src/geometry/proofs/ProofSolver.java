package geometry.proofs;

import java.util.ArrayList;
import java.util.List;
import java.util.Deque;
import java.util.ArrayDeque;

import geometry.shapes.Angle;
import geometry.shapes.Segment;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;

import static geometry.proofs.FigureRelationType.CONGRUENT;
import static geometry.proofs.FigureRelationType.MIDPOINT;
import static geometry.proofs.FigureRelationType.SIMILAR;
import static geometry.proofs.FigureRelationType.RIGHT;


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
	
	public boolean getResult() {
		return result;
	}
	
	public boolean proofWasSolved() {
		return proofWasSolved;
	}
	
	public boolean solve() {
		if (proofWasSolved)
			return result;
	
		if (diagram == null)
			throw new NullPointerException("Diagram is null");
	
		if (diagram.getProofGoal() == null)
			throw new NullPointerException("Proof goal is null.");
		
		// Solve proof here
		// Inflate the given, get all available FigureRelations
		inflateGiven();
		
		// Check if the proof goal is included in the inflated given
		for (FigureRelation pair : diagram.getFigureRelations()) {
			if (pair.equals(diagram.getProofGoal())) {
				
				Deque<FigureRelation> traceback = new ArrayDeque<>();
				traceback(pair, traceback);
				System.out.println("-----TRACEBACK-----");
				traceback.forEach(System.out::println);
				
				return proofWasSolved = result = true;
			}
		}
		
		proofWasSolved = true;
		result = false;
		
		return result;
	}
	
	private void traceback(FigureRelation begin, Deque<FigureRelation> traceback) {		
		traceback.push(begin);
		for (FigureRelation parent : begin.getParents()) {
			traceback(parent, traceback);
		}
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
			
			// Discover isosceles triangles
			findIsoscelesTriangles();
			// Discover congruent triangles
			findCongruentTriangles();			
			
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
	private FigureRelation getCongruentRel(Figure f0, Figure f1) {
		// TODO: document
		FigureRelation hypoRel = new FigureRelation(CONGRUENT, f0, f1);
		for (FigureRelation rel : diagram.getFigureRelations()) {
			if (rel.equals(hypoRel))
				return rel;
		}
		return null;
	}
	
	/**
	 * <i>This method, and the {@link ProofSolver} in general, assumes that
	 * the given {@link FigureRelation} is of type 
	 * {@link PerpendicularFigureRelation}</i>
	 * <p>
	 * DEFINITIONS
	 * <ul>
	 * <li>
	 * Primary Non-intersecting Vertex: the vertices of the intersectING
	 * {@link Segment}, <i>given that it is not the Point of Intersection</i>
	 * </li>
	 * <li>
	 * Secondary Non-intersecting Vertex: the vertices of the intersectED
	 * {@link Segment}, <i>given that it is not the Point of Intersection</i>
	 * </li>
	 * <li>
	 * In this example, AD is perpendicular to BC		
	 * 							A
	 * 						B   E    C
	 * 							D
	 * A and D are primary non-intersecting vertices, and B and C are
	 * secondary non-intersecting vertices. E is the point of intersection.
	 * </li>
	 * </ul>
	 * @param pair the {@link PerpendicularFigureRelation} to be handled
	 */
	private void handlePerpendicularPair(FigureRelation pair) {
		// Get a more detailed FigureRelation
		PerpendicularFigureRelation perpRel = (PerpendicularFigureRelation) pair;
		// Get segments involved
		Segment intersecting = perpRel.getFigure0();
		Segment intersected = perpRel.getFigure1();
		
		// Get the primary non-intersecting vertices
		String primNonIntersectVerts = "";
		for (char c : intersecting.getName().toCharArray()) {
			if (c != perpRel.getIntersectVert()) {
				primNonIntersectVerts += c;
			}
		}
		
		// Get the secondary non-intersecting vertices
		String secNonIntersectVerts = "";
		for (char c : intersected.getName().toCharArray()) {
			if (c != perpRel.getIntersectVert()) {
				secNonIntersectVerts += c;
			}
		}
		
		for (char primVert : primNonIntersectVerts.toCharArray()) {
			for (char secVert : secNonIntersectVerts.toCharArray()) {
				String angleName = 
					// Primary non-intersecting vertex
					String.valueOf(primVert) + 
					// Middle vertex
					String.valueOf(perpRel.getIntersectVert()) + 
					// Secondary non-intersecting vertex
					String.valueOf(secVert);
				// Make the angle a right angle in the Diagram
				Angle angle = diagram.getFigure(angleName, Angle.class);
				FigureRelation rel = new FigureRelation(RIGHT, angle, null);
				rel.addParent(perpRel);
				rel.setReason("Perpendicular");
				diagram.addFigureRelation(rel);
			}
		}
	}
	
	/**
	 * <i>This method, and the {@link ProofSolver} in general, assumes that
	 * the given {@link FigureRelation} is of type 
	 * {@link BisectsFigureRelation}</i>
	 * <p>
	 * DEFINITIONS
	 * <ul>
	 * <li>
	 * Primary Non-intersecting Vertex: the vertices of the intersectING
	 * {@link Segment}, <i>given that it is not the Point of Intersection</i>
	 * </li>
	 * <li>
	 * Secondary Non-intersecting Vertex: the vertices of the intersectED
	 * {@link Segment}, <i>given that it is not the Point of Intersection</i>
	 * </li>
	 * </ul>
	 * @param pair the {@link BisectsFigureRelation} to be handled
	 * @see ProofSolver#handlePerpendicularPair(FigureRelation)
	 */
	private void handleBisectPair(FigureRelation pair) {
		BisectsFigureRelation bisectsRel = (BisectsFigureRelation) pair;
		
		String intersectedSeg = bisectsRel.getFigure1().getName();
		final char intersectVert = bisectsRel.getIntersectVert();
		
		String newSeg0 = String.valueOf(intersectVert) + intersectedSeg.substring(0, 1);
		String newSeg1 = String.valueOf(intersectVert) + intersectedSeg.substring(1);
		
		FigureRelation rel = new FigureRelation(
				CONGRUENT,
				diagram.getFigure(newSeg0),
				diagram.getFigure(newSeg1)
		);
		rel.addParent(pair);
		rel.setReason("Bisects");
		diagram.addFigureRelation(rel);
		
	}
	
	private void handleMidpoint(FigureRelation pair) {
		// Make sure the given FigureRelation is of type MIDPOINT
		if (pair.getRelationType() != MIDPOINT) {
			throw new IllegalArgumentException("Given FigureRelation"
					+ " must be of type MIDPOINT");
		}
		
		Vertex vert = pair.getFigure0();
		Segment seg = pair.getFigure1();
		
		Segment newSeg0 =
				diagram.getFigure(seg.getName().substring(0, 1) + vert.getName());
		Segment newSeg1 =
				diagram.getFigure(vert.getName() + seg.getName().substring(1));
		
		FigureRelation rel = new FigureRelation(CONGRUENT, newSeg0, newSeg1);
		rel.setReason("Midpoint");
		rel.addParent(pair);
		diagram.addFigureRelation(rel);
	}
	
	private void handleSimilarTriangles(FigureRelation pair) {
		// Make sure the given FigureRelation is of type SIMILAR
		if (pair.getRelationType() != SIMILAR) {
			throw new IllegalArgumentException("Given FigureRelation"
					+ " must be of type SIMILAR");
		}
		
		Triangle tri0 = pair.getFigure0();
		Triangle tri1 = pair.getFigure1();
		
		// Get corresponding angles in triangles
		List<Angle[]> corrAngles = ProofUtils.getCorrespondingAngles(tri0, tri1);

		// Length of corrAngles should always be 3
		for (int i = 0; i < corrAngles.size(); i++) {
			FigureRelation rel = new FigureRelation(
					CONGRUENT, corrAngles.get(i)[0], corrAngles.get(i)[1]
			);
			rel.setReason("Corresponding angles congruent");
			rel.addParent(pair);
			diagram.addFigureRelation(rel);
		}
	}
	
	private void findIsoscelesTriangles() {
		// For each figure in the Diagram
		for (Figure fig : diagram.getFigures()) {
			// If the figure is not a Triangle, leave the function
			if (!(fig instanceof Triangle))
				continue;
						
			// Get the triangle
			Triangle tri = (Triangle)fig;
			// Get the name of the triangle
			String triName = tri.getName();
			// Get the triangle's segments
			Segment[] segs = tri.getSides();
			// Get the triangle's angles
			Angle[] angles = tri.getAngles();
						
			// For each segment
			for (int i = 0; i < 2; i++) {
				// For each segment located AFTER the above segment in the list of segments
				// (this prevents us from comparing a pair of segments twice. We don't
				// want to compare A to B and then B to A)
				for (int j = i + 1; j < 3; j++) {
					// Find congruent segments, make opposite angles congruent
					FigureRelation segsRel = getCongruentRel(segs[i], segs[j]);
					if (segsRel != null) {
						// Get the opposite vertex from the FIRST segment
						String oppVert0 = ProofUtils.getOppositeVertex(triName, segs[i].getName());
						// Get the opposite vertex from the CURRENT segment
						String oppVert1 = ProofUtils.getOppositeVertex(triName, segs[j].getName());
						// Get the angles at each of the vertices
						Angle a0 = tri.getAngle(ProofUtils.getFullNameOfAngle(triName, oppVert0));
						Angle a1 = tri.getAngle(ProofUtils.getFullNameOfAngle(triName, oppVert1));
						
						// Make the two angles congruent
						FigureRelation rel = new FigureRelation(
								CONGRUENT, 
								diagram.getPrimaryAngleSynonym(a0.getName()), 
								diagram.getPrimaryAngleSynonym(a1.getName())
						);
						rel.addParent(segsRel);
						rel.setReason("Isosceles Triangle Theorem");
						// Update Diagram
						diagram.addFigureRelation(rel);
					}
					// Find congruent angles, make opposite segments congruent
					FigureRelation anglesRel = getCongruentRel(angles[i], angles[j]);
					if (anglesRel != null) {
						// Get the middle vertex of the first angle
						String midVertex0 = angles[i].getNameShort();
						// Get the middle vertex of the second segment
						String midVertex1 = angles[j].getNameShort();
						// Get the segment in between of the two angles
						String middleSegment = midVertex0 + midVertex1;
						// Get the vertex opposite to the middle segment
						String oppVertex = ProofUtils.getOppositeVertex(triName, middleSegment);
						// Make the first segment
						String seg0 = oppVertex + midVertex0;
						// Make the second segment
						String seg1 = oppVertex + midVertex1;
						Segment segment0 = diagram.getFigure(seg0);
						Segment segment1 = diagram.getFigure(seg1);
						// Make the segs congruent
						FigureRelation rel = new FigureRelation(
								CONGRUENT, segment0, segment1
						);
						rel.addParent(anglesRel);
						rel.setReason("Isosceles Triangle Theorem");
						diagram.addFigureRelation(rel);
					}
				}
			}
		}
	}
	
	private void findCongruentTriangles() {
		// For each figure
		for (int i = 0; i < diagram.getFigures().size()-1; i++) {
			// Make sure figure is a triangle
			if (!(diagram.getFigures().get(i) instanceof Triangle))
				continue;
			// For each other figure
			for (int j = i + 1; j < diagram.getFigures().size(); j++) {
				// The second figure must be a triangle
				if (!(diagram.getFigures().get(j) instanceof Triangle))
					continue;
				
				// Get the triangles
				Triangle tri0 = (Triangle) diagram.getFigures().get(i);
				Triangle tri1 = (Triangle) diagram.getFigures().get(j);
				
				// Check if triangles are congruent (SSS, SAS, ASA)
				
				// SSS				
				// Get corresponding segments--3 pairs of congruent segments
				// are needed to make the triangles congruent by SSS
				List<FigureRelation> corrSegs = getCorrespondingSegments(tri0, tri1);
				if (corrSegs.size() == 3) {
					FigureRelation rel = new FigureRelation(CONGRUENT, tri0, tri1);
					rel.addParents(corrSegs);
					rel.setReason("SSS");
					diagram.addFigureRelation(rel);
					continue;
				}
				
				// SAS
				
				List<FigureRelation> sasRels = congruentBySAS(tri0, tri1);
				if (sasRels.size() == 3) {
					FigureRelation rel = new FigureRelation(CONGRUENT, tri0, tri1);
					rel.addParents(sasRels);
					rel.setReason("SAS");
					diagram.addFigureRelation(rel);
					continue;
				}
				
				// ASA
				
				List<FigureRelation> asaRels = congruentByASA(tri0, tri1);
				if (asaRels.size() == 3) {
					FigureRelation rel = new FigureRelation(CONGRUENT, tri0, tri1);
					rel.addParents(asaRels);
					rel.setReason("ASA");
					diagram.addFigureRelation(rel);
					continue;
				}
			}
		}
	}
	
	private List<FigureRelation> congruentBySAS(Triangle tri0, Triangle tri1) {
		// List of parents
		List<FigureRelation> parents = new ArrayList<>();
		
		// STRATEGY: find a pair of congruent angles, then compare surrounding
		// segments
		
		// Get the angles of each triangle
		Angle[] tri0Angles = tri0.getAngles();
		Angle[] tri1Angles = tri1.getAngles();

		// For each angle in the first triangle
		outer:
		for (Angle a0 : tri0Angles) {
			// For each angle in the second triangle
			for (Angle a1 : tri1Angles) {
				// Try to find if these angles are congruent
				FigureRelation congAngles = getCongruentRel(a0, a1);
				// If they are congruent
				if (congAngles != null) {
					// Get the adjacent Segments of each angle
					Segment[] segs0 = tri0.getAdjacentSegments(a0.getNameShort());
					Segment[] segs1 = tri1.getAdjacentSegments(a1.getNameShort());
					
					// For each pair of adjacent segments, check if the two
					// segments are congruent. If so, add them to the list
					// of parents
					for (Segment s0 : segs0) {
						for (Segment s1 : segs1) {
							FigureRelation segRel = getCongruentRel(s0, s1);
							if (segRel != null)
								parents.add(segRel);
						}
					}
					
					// Once we find an instance of SAS, we needn't check for more.
					// If we find two pairs of congruent segments adjacent to the
					// pair of angles, than we're done
					if (parents.size() == 2) {
						// Add these congruent angles
						parents.add(congAngles);
						break outer;
					} else {
						// If one pair of congruent segments were found
						// (not the min of two, then get rid of the one we found
						// since it's useless alone)
						parents.clear();
					}
				}
			}
		}
		
		return parents;
	}
	
	private List<FigureRelation> congruentByASA(Triangle tri0, Triangle tri1) {
		// List of parents
		List<FigureRelation> parents = new ArrayList<>();
		
		// STRATEGY: find a pair of congruent segments, then compare surrounding
		// angles
		
		// Get the sides of each triangle
		Segment[] segs0 = tri0.getSides();
		Segment[] segs1 = tri1.getSides();
		
		// For each side of the first triangle
		outer:
		for (Segment s0 : segs0) {
			// For each side of the second triangle
			for (Segment s1 : segs1) {
				// Try to find if these segments are congruent
				FigureRelation congSegs = getCongruentRel(s0, s1);
				// If they are congruent
				if (congSegs != null) {
					// Get the adjacent angles around the first segment
					String[] adjacentAngles0 = 
							ProofUtils.getSurroundingAngles(tri0.getName(), s0.getName());
					// Get the adjacent angles around the second segment
					String[] adjacentAngles1 =
							ProofUtils.getSurroundingAngles(tri1.getName(), s1.getName());
			
					// For each pair of adjacent angles, check if the two
					// angles are congruent. If so, add them to the list
					// of parents
					for (String angle0 : adjacentAngles0) {
						Angle a0 = tri0.getAngle(angle0);
						for (String angle1 : adjacentAngles1) {
							Angle a1 = tri1.getAngle(angle1);
							FigureRelation anglesRel = getCongruentRel(a0, a1);
							if (anglesRel != null)
								parents.add(anglesRel);
						}
					}
					
					// Once we find an instance of ASA, we needn't check for more.
					// If we find two pairs of congruent angles adjacent to the
					// pair of segments, than we're done
					if (parents.size() == 2) {
						// Add these congruent segments
						parents.add(congSegs);
						System.out.println("ASA Parents: " + parents);
						break outer;
					} else {
						// If one pair of congruent angles were found
						// (not the min of two, then get rid of the one we found
						// since it's useless alone)
						parents.clear();
					}

				}
			}
		}
		
		return parents;
	}
	
	/**
	 * Finds the corresponding Segments of the two given Triangles, and
	 * returns the respective FigureRelation that marks the two Segments as
	 * congruent (if one exists).
	 * @param tri0 the first triangle
	 * @param tri1 the second triangle
	 * @return the list of FigureRelations
	 */
	private List<FigureRelation> getCorrespondingSegments(Triangle tri0, Triangle tri1) {
		// List of relations
		List<FigureRelation> list = new ArrayList<>();
		
		for (Segment[] pair : ProofUtils.getCorrespondingSegments(tri0, tri1)) {
			// Hypothetical FigureRelation with two congruent Segments
			FigureRelation hypoRel = new FigureRelation(CONGRUENT, pair[0], pair[1]);
			// Try to find this hypothetical FigureRelation (if exists)
			for (FigureRelation rel : diagram.getFigureRelations()) {
				if (rel.equals(hypoRel)) {
					list.add(rel);
					break;
				}
			}
		}
		
		return list;
	}
}
