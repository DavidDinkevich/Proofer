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
import static geometry.proofs.FigureRelationType.ISOSCELES;


public class ProofSolver {
	private boolean proofWasSolved = false;
	private boolean result = false;
	private FigureRelation[] traceback;
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
		if (!proofWasSolved) {
			throw new RuntimeException("Proof was not yet solved");
		}
		return result;
	}
	
	public FigureRelation[] getTraceback() {
		return traceback;
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
		for (int i = 0; i < diagram.getFigureRelations().size(); i++) {
			FigureRelation pair = diagram.getFigureRelations().get(i);
			if (FigureRelation.safeEquals(pair, diagram.getProofGoal())) {
				// TRACEBACK PROCESS
				Deque<FigureRelation> traceback = new ArrayDeque<>();
				traceback(pair, traceback);
				// Store traceback
				this.traceback = traceback.toArray(new FigureRelation[traceback.size()]);
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
			
			// Discover congruent triangles
			findCongruentTriangles();
			// Discover isosceles triangles
			findIsoscelesTriangles();
			// Discover similar triangles
			findSimilarTriangles();
			// Find perpendicular segments
			findPerpendicularSegments();
			
			// Update
			totalRelsAdded = diagram.getFigureRelations().size() - relCountBefore;
		
		// Keep inflating the given while there are still figure relations to add
		} while (totalRelsAdded > 0);
		
		System.out.println("--------Figure Relations---------");
		diagram.getFigureRelations().forEach(System.out::println);
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
				Angle angle = diagram.getPrimaryAngleSynonym(angleName);
				FigureRelation rel = new FigureRelation(RIGHT, angle, null);
				rel.addParent(perpRel);
				rel.setReason(ProofReasons.PERPENDICULAR);
				diagram.addFigureRelation(rel);
			}
		}
	}
	
	private void handleBisectPair(FigureRelation pair) {
		// DETERMINE WHETHER THIS IS A SEGMENT OR ANGLE BISECTOR
		if (pair instanceof SegmentBisectorFigureRelation) {
			handleSegmentBisector((SegmentBisectorFigureRelation) pair);
		} else {
			handleAngleBisector((AngleBisectorFigureRelation) pair);
		}
	}
	
	/**
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
	 * @param pair the {@link SegmentBisectorFigureRelation} to be handled
	 * @see ProofSolver#handlePerpendicularPair(FigureRelation)
	 */
	private void handleSegmentBisector(SegmentBisectorFigureRelation bisectsRel) {		
		String intersectedSeg = bisectsRel.getFigure1().getName();
		final char intersectVert = bisectsRel.getIntersectVert();
		
		String newSeg0 = String.valueOf(intersectVert) + intersectedSeg.substring(0, 1);
		String newSeg1 = String.valueOf(intersectVert) + intersectedSeg.substring(1);
		
		FigureRelation rel = new FigureRelation(
				CONGRUENT,
				diagram.getFigure(newSeg0),
				diagram.getFigure(newSeg1)
		);
		rel.addParent(bisectsRel);
		rel.setReason(ProofReasons.SEGMENT_BISECTOR);
		diagram.addFigureRelation(rel);
	}
	
	private void handleAngleBisector(AngleBisectorFigureRelation pair) {
		// Angle being bisected
		Angle mainAngle = pair.getFigure1();
		// Name of the angle being bisected
		String mainAngleName = mainAngle.getName();
		// Get the two angles (halves of the original bisected angle)
		String angle0 = mainAngleName.substring(0, 2) + pair.getSmallestBisectorEndpoint();
		String angle1 = pair.getSmallestBisectorEndpoint() + mainAngleName.substring(1);
		
		// Create the new relation making the two halves congruent
		FigureRelation rel = new FigureRelation(
				CONGRUENT,
				diagram.getFigure(angle0, Angle.class),
				diagram.getFigure(angle1, Angle.class)
		);
		rel.addParent(pair);
		rel.setReason(ProofReasons.ANGLE_BISECTOR);
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
		rel.setReason(ProofReasons.MIDPOINT);
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
					CONGRUENT,
					diagram.getPrimaryAngleSynonym(corrAngles.get(i)[0].getName()),
					diagram.getPrimaryAngleSynonym(corrAngles.get(i)[1].getName())
			);
			rel.setReason(ProofReasons.CORR_ANGLES_SIMILAR_TRIANGLES);
			rel.addParent(pair);
			diagram.addFigureRelation(rel);
		}
	}
	
	private void findSimilarTriangles() {
		// For each figure
		for (int i = 0; i < diagram.getFigures().size()-1; i++) {
			// Make sure figure is a triangle
			if (diagram.getFigures().get(i).getClass() != Triangle.class)
				continue;
			// For each other figure
			for (int j = i + 1; j < diagram.getFigures().size(); j++) {
				// The second figure must be a triangle
				if (diagram.getFigures().get(j).getClass() != Triangle.class)
					continue;
				
				// Get the triangles
				Triangle tri0 = (Triangle) diagram.getFigures().get(i);
				Triangle tri1 = (Triangle) diagram.getFigures().get(j);
				
				// List of parents
				List<FigureRelation> parents = new ArrayList<>();
				
				// Must find at least two congruent pairs of angles
				for (Angle a : tri0.getAngles()) {
					for (Angle b : tri1.getAngles()) {
						FigureRelation query = diagram.getFigureRelation(CONGRUENT, a, b);
						if (query != null) {
							parents.add(query);
						}
					}
				}
				
				// Make the two triangles similar
				if (parents.size() >= 2) {
					FigureRelation rel = new FigureRelation(SIMILAR, tri0, tri1);
					rel.addParents(parents);
					rel.setReason(ProofReasons.SIMILAR);
					diagram.addFigureRelation(rel);
				}
			}
		}
	}
	
	private void findIsoscelesTriangles() {
		for (Triangle tri : diagram.getFiguresOfType(Triangle.class)) {
			// Get the name of the triangle
			String triName = tri.getName();
			// Get the triangle's segments
			Segment[] segs = tri.getSides();
			// Get the triangle's angles
			Angle[] angles = tri.getAngles();
			
			// Account for angle synonyms
			for (int i = 0; i < angles.length; i++) {
				angles[i] = diagram.getPrimaryAngleSynonym(angles[i].getName());
			}
						
			// For each segment
			for (int i = 0; i < 2; i++) {
				// For each segment located AFTER the above segment in the list of segments
				// (this prevents us from comparing a pair of segments twice. We don't
				// want to compare A to B and then B to A)
				for (int j = i + 1; j < 3; j++) {					
					// Find congruent segments, make opposite angles congruent
					FigureRelation segsRel = diagram
							.getFigureRelation(CONGRUENT, segs[i], segs[j]);
					if (segsRel != null) {
						// Get the opposite vertex from the FIRST segment
						String oppVert0 = ProofUtils.getOppositeVertex(triName, segs[i].getName());
						// Get the opposite vertex from the CURRENT segment
						String oppVert1 = ProofUtils.getOppositeVertex(triName, segs[j].getName());
						// GET THE ANGLES OPPOSITE OF THE CONGRUENT SEGMENTS
						// Get the angles at each of the vertices
						Angle a0 = tri.getAngle(ProofUtils.getFullNameOfAngle(triName, oppVert0));
						Angle a1 = tri.getAngle(ProofUtils.getFullNameOfAngle(triName, oppVert1));
						
						/*
						 * MAKE THE TRIANGLE ISOSCELES
						 */
						FigureRelation isoscelesRel = new FigureRelation(ISOSCELES, tri, null);
						isoscelesRel.setReason(ProofReasons.ISOSCELES);
						isoscelesRel.addParent(segsRel);
						diagram.addFigureRelation(isoscelesRel);

						/*
						 * MAKE THE TWO BASE ANGLES CONGRUENT (ISOSCELES TRIANGLE THEOREM)
						 */
						// Make the two angles congruent
						FigureRelation rel = new FigureRelation(
								CONGRUENT, 
								diagram.getPrimaryAngleSynonym(a0.getName()), 
								diagram.getPrimaryAngleSynonym(a1.getName())
						);
						rel.addParent(isoscelesRel);
						rel.setReason(ProofReasons.ISOSCELES_OPP_ANGLES);
						// Update Diagram
						diagram.addFigureRelation(rel);
					}

					// Find congruent angles, make opposite segments congruent
					FigureRelation anglesRel = diagram
							.getFigureRelation(CONGRUENT, angles[i], angles[j]);
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
						
						/*
						 * MAKE THE TRIANGLE ISOSCELES
						 */
						FigureRelation isoscelesRel = new FigureRelation(ISOSCELES, tri, null);
						isoscelesRel.setReason(ProofReasons.OPP_ISOSCELES);
						isoscelesRel.addParent(anglesRel);
						diagram.addFigureRelation(isoscelesRel);

						/*
						 * MAKE THE TWO OPPOSITE SEGMENTS CONGRUENT (ISOSCELES TRIANGLE THEOREM)
						 */
						// Make the segs congruent
						FigureRelation rel = new FigureRelation(CONGRUENT, segment0, segment1);
						rel.addParent(isoscelesRel);
						rel.setReason(ProofReasons.ISOSCELES_OPP_SEGMENTS);
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
			if (diagram.getFigures().get(i).getClass() != Triangle.class)
				continue;
			// For each other figure
			for (int j = i + 1; j < diagram.getFigures().size(); j++) {
				// The second figure must be a triangle
				if (diagram.getFigures().get(j).getClass() != Triangle.class)
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
					rel.setReason(ProofReasons.SSS);
					diagram.addFigureRelation(rel);
					continue;
				}
				
				// SAS
				
				List<FigureRelation> sasRels = congruentBySAS(tri0, tri1);
				if (sasRels.size() == 3) {
					FigureRelation rel = new FigureRelation(CONGRUENT, tri0, tri1);
					rel.addParents(sasRels);
					rel.setReason(ProofReasons.SAS);
					diagram.addFigureRelation(rel);
					continue;
				}
				
				// ASA
				
				List<FigureRelation> asaRels = congruentByASA(tri0, tri1);
				if (asaRels.size() == 3) {
					FigureRelation rel = new FigureRelation(CONGRUENT, tri0, tri1);
					rel.addParents(asaRels);
					rel.setReason(ProofReasons.ASA);
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
			// Make sure this is a primary angle synonym
			a0 = diagram.getPrimaryAngleSynonym(a0.getName());
//			 For each angle in the second triangle
			for (Angle a1 : tri1Angles) {
				// Make sure this is a primary angle synonym
				a1 = diagram.getPrimaryAngleSynonym(a1.getName());
				// Try to find if these angles are congruent
				FigureRelation congAngles = diagram.getFigureRelation(CONGRUENT, a0, a1);
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
							FigureRelation segRel = diagram.getFigureRelation(CONGRUENT, s0, s1);
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
				FigureRelation congSegs = diagram.getFigureRelation(CONGRUENT, s0, s1);
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
						// Make sure this is a primary angle synonym
						Angle a0 = diagram.getPrimaryAngleSynonym(angle0);
						for (String angle1 : adjacentAngles1) {
							// Make sure this is a primary angle synonym
							Angle a1 = diagram.getPrimaryAngleSynonym(angle1);
							FigureRelation anglesRel = diagram
									.getFigureRelation(CONGRUENT, a0, a1);
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
	
	private void findPerpendicularSegments() {
		// For each angle
		for (Angle a : diagram.getFiguresOfType(Angle.class)) {
			// See if this angle is a right angle
			FigureRelation rightAngleRelation = diagram.getFigureRelation(RIGHT, a, null);
			if (rightAngleRelation != null) {
				// Get the segments that compose the angle (they are perpendicular)
				String name = a.getName();
				String[] segs = { name.substring(0, 2), name.substring(1) };
				// Make the segments perpendicular, account for compound segments
				FigureRelation perpRel = new PerpendicularFigureRelation(
						diagram.getLargestCompoundSegmentOf(segs[0]),
						diagram.getLargestCompoundSegmentOf(segs[1]),
						a.getNameShort().charAt(0)
				);
				perpRel.addParent(rightAngleRelation);
				perpRel.setReason(ProofReasons.OPP_PERPENDICULAR);
				diagram.addFigureRelation(perpRel);
			}
		}
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
			FigureRelation hypoRel = diagram.getFigureRelation(CONGRUENT, pair[0], pair[1]);
			if (hypoRel != null) {
				list.add(hypoRel);
				break;
			}
		}
		
		return list;
	}
}
