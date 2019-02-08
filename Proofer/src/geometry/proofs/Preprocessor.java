package geometry.proofs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import geometry.Vec2;
import geometry.shapes.Angle;
import geometry.shapes.Segment;
import geometry.shapes.Segment.Slope;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;

import ui.FigureRelationListPanel;
import ui.FigureRelationPanel;
import ui.canvas.GraphicsShape;
import ui.canvas.diagram.DiagramCanvas;

import static geometry.proofs.FigureRelationType.CONGRUENT;


public final class Preprocessor {
	
	// No instantiating this class
	private Preprocessor() {}
	
	/**
	 * Create a {@link Diagram} and fill it with {@link Figure}s from a 
	 * {@link DiagramCanvas}
	 * @param canvas the {@link DiagramCanvas}
	 * @return the newly created {@link Diagram}
	 */
	public static Diagram compileFigures(DiagramCanvas canvas) {
		Diagram diagram = new Diagram();
		
		// Gather figures
		for (GraphicsShape<?> shape : canvas.getDiagramFigures()) {
			diagram.addFigure(shape.getShape());
		}
		
		// Add and include all hidden figures
		addHiddenFigures(diagram);
		// Make vertical angles congruent
		handleVerticalAngles(diagram);
		
		return diagram;
	}

	/**
	 * Prepare a {@link Diagram} to be processed by {@link ProofSolver}.
	 * @param canvas the {@link}
	 * @param figRelPanel
	 * @return
	 */
	public static Diagram generateDiagram(DiagramCanvas canvas, 
			FigureRelationListPanel figRelPanel) {
		
		// Compile the figures
		Diagram diagram = compileFigures(canvas);
		
		// Determine given
		for (FigureRelationPanel panel : figRelPanel.getFigureRelationPanels()) {
			if (!panel.hasContent())
				continue;
			// Figure relation type
			FigureRelationType relType = panel.getRelationType();
			// First figure name
			String figText0 = panel.getFigTextField0().getText();
			// Second figure name
			String figText1 = panel.getFigTextField1().getText();
			
			/*
			 * If there are no  figures that match the names
			 * of figures given in the figure relation pair panel,
			 * the following instantiation of a figure relation pair will
			 * crash.
			 */
			FigureRelation rel = new FigureRelation(
					relType,
					// Get first figure
					searchForFigure(diagram, figText0),
					// Get second figure
					searchForFigure(diagram, figText1)
			);
			rel.setReason("Given");
			// Add the given
			diagram.addFigureRelation(rel);
		}
		
		preprocessGivenInfo(diagram);
		
		// Determine proof goal (to prove)
		FigureRelationPanel goalPanel = figRelPanel.getProofGoalPanel();
		// Check if the proof goal panel has content
		if (!goalPanel.hasContent()) {
			System.err.println("Diagram has no goal.");
			return null;
		}
		
		FigureRelation proofGoal = new FigureRelation(
				goalPanel.getRelationType(),
				searchForFigure(diagram, goalPanel.getFigTextField0().getText()),
				searchForFigure(diagram, goalPanel.getFigTextField1().getText())
		);
		proofGoal.setReason("Proof Goal");
		diagram.setProofGoal(proofGoal);
		
		return diagram;
	}
	
	private static Figure searchForFigure(Diagram diagram, String name) {
		Figure fig = null;
		// Angle or triangle
		if (name.length() == 4) { // 3 cars and a special character
			final boolean figIsTri = name.startsWith(ProofUtils.DELTA);
			final boolean figIsAngle = !figIsTri && name.startsWith(ProofUtils.ANGLE_SYMBOL);
			if (figIsTri)
				fig = diagram.getFigure(name.substring(1), Triangle.class);
			else if (figIsAngle) {
				fig = diagram.getPrimaryAngleSynonym(name.substring(1));
			}
		}
		// Not triangle or angle
		else
			fig = diagram.getFigure(name);
		if (fig == null)
			System.err.println("Could not find figure with name: " + name);
		return fig;
	}
	
	/**
	 * Get all of the shared vertices between two triangles
	 * @param tri0 the first triangle
	 * @param tri1 the second triangle
	 * @return the shared vertices
	 */
//	private List<Vertex> getSharedVertices(Triangle tri0, Triangle tri1) {
//		List<Vertex> commonVertices = null;
//		outer:
//		for (Vertex vert0 : tri0) {
//			for (Vertex vert1 : tri1) {
//				if (vert0.equals(vert1)) {
//					if (commonVertices == null)
//						commonVertices = new ArrayList<>();
//					commonVertices.add(vert0);
//					continue outer;
//				}
//			}
//		}
//		return commonVertices == null ? Collections.emptyList() : commonVertices;
//	}
	
	/**
	 * If I have two right triangles, ABC and DBC,
	 *    C
	 * A  B  D
	 * the segment AD would not exist (it should, it is a valid segment). The reason for this is
	 * because the vertices A and D are in two different figures, and the computer does not 
	 * know that they form a straight segment. Similarly, the triangle ACD would also not exist,
	 * because it is the combination of triangles ABC and DBC. These are called "hidden figures",
	 * because they are not direct components of a figure. This method ensures that they too are
	 * included in the diagram and can be safely referenced.
	 * @param diagram the diagram that contains the hidden figures.
	 */
	private static void addHiddenFigures(Diagram diagram) {		
		List<Angle> hiddenAngles = new ArrayList<>();
		boolean figuresWereAdded = false;
		
		do {
			// Add hidden vertices
			addHiddenVertices(diagram);

			final int COUNT = diagram.getFigures().size();
			figuresWereAdded = false;
			
			// Loop through segments
			for (int i = 0; i < COUNT-1; i++) {
				// If the inspected figure is a segment
				if (diagram.getFigures().get(i) instanceof Segment) {
					// Capture the segment
					Segment seg0 = (Segment)diagram.getFigures().get(i);
					
					// Loop through figures again
					for (int j = i + 1; j < COUNT; j++) {
						if (diagram.getFigures().get(j) instanceof Segment) {
							Segment seg1 = (Segment)diagram.getFigures().get(j);
			
							// Add hidden figures
							Figure hiddenFig = identifyHiddenSegOrAngle(seg0, seg1);
							// If we've found a hidden figure
							if (hiddenFig != null) {
								// If the figure was added to the diagram (it was not contained before)
								if (diagram.addFigure(hiddenFig)) {
									figuresWereAdded = true; // Update variable
									// If the hidden figure we found is an angle, store it
									if (hiddenFig instanceof Angle)
										hiddenAngles.add((Angle)hiddenFig);
								}
							}
						}
					}
				}
			}
						
			// Add hidden triangles
			List<Triangle> hiddenTris = identifyHiddenTriangles(diagram, hiddenAngles);
			// If hidden triangles were added to the diagram
			if (diagram.addFigures(hiddenTris))
				figuresWereAdded = true; // Update variable
		} while (figuresWereAdded);
	}
	
	private static void addHiddenVertices(Diagram diag) {
		// For each figure
		for (int i = 0; i < diag.getFigures().size() - 1; i++) {
			// Only segments
			if (diag.getFigures().get(i).getClass() != Segment.class)
				continue;
			// Get the segment
			Segment seg0 = (Segment) diag.getFigures().get(i);
			// For every other figure
			for (int j = i + 1; j < diag.getFigures().size(); j++) {
				// Only segments
				if (diag.getFigures().get(j).getClass() != Segment.class)
					continue;
				// Get the segment
				Segment seg1 = (Segment) diag.getFigures().get(j);
				
				// IF the segments intersect
				if (Segment.segmentsDoIntersect(seg0, seg1)) {
					// Get the point of intersection
					Vec2 poi = Segment.getPointOfIntersection(seg0, seg1);
					// Skip if there already is a vertex at the given location
					if (getVertexAtLoc(diag, poi) == null) {
						// Create a new vertex at the given intersection
						Vertex newVertex = new Vertex(generateNewVertexName(diag), poi);
						// Add the vertex
						diag.addFigure(newVertex);
						System.out.println("Segments: " + seg0 + ", " + seg1 + " : " + poi);
						System.out.println("Added vertex: " + newVertex);
					}
				}
			}
		}
	}
	
	private static void addHiddenIntersectionSegs(Diagram diag) {
		
	}
	
	/**
	 * Identify the hidden {@link Segment} or {@link Angle} between the
	 * given segments
	 * @param seg0 the first segment
	 * @param seg1 the second segment
	 * @return the hidden segment OR figure, or null if the two given segments
	 * do not connect at one vertex
	 */
	private static Figure identifyHiddenSegOrAngle(Segment seg0, Segment seg1) {
//		// If we're analyzing the same segment, we can't combine it
//		if (seg0.equals(seg1))
//			return new Figure[0];
		// Get the shared vertex between the two segments
		String sharedVertex = ProofUtils.getSharedVertex(seg0.getName(), seg1.getName());
		if (sharedVertex == null)
			return null;
		
		// Check if the two segments are parallel
		// Get slope of segment 0
		Slope seg0Slope = seg0.getSlope();
		// Get slope of segment 1
		Slope seg1Slope = seg1.getSlope();
				
		/*
		 * Compare slopes: if the slopes are the same, add a new hidden segment.
		 * Otherwise, add the hidden angle.z
		 */
		if (seg0Slope.equals(seg1Slope)) {
			// Combine segments
			
			// Vertices of both segments in one list
			List<Vertex> segVerts = new ArrayList<>(Arrays.asList(seg0.getVertices()));
			segVerts.addAll(Arrays.asList(seg1.getVertices()));
			// ---------------------
			// Vertices of new segment--farthest apart
			Vertex[] newSegVerts = getFarthestVertices(segVerts);
			
			// The new, combined straight line
			Segment newStraightLine = new Segment(newSegVerts);
			return newStraightLine;
		}
		// Add hidden angles
		else {
			String angleName = ProofUtils.getAngleBetween(seg0.getName(), seg1.getName());
			String unsharedVert0 = angleName.substring(0, 1);
			String unsharedVert1 = angleName.substring(2);
			String sharedVert = angleName.substring(1, 2);
			Vertex first, second, third;
			first = (Vertex) (seg0.containsChild(unsharedVert0) ?
					seg0.getChild(unsharedVert0) : seg0.getChild(unsharedVert1));
			third = (Vertex) (first.getNameChar() == unsharedVert0.charAt(0) ?
					seg1.getChild(unsharedVert1) : seg1.getChild(unsharedVert0));
			second = (Vertex) seg0.getChild(sharedVert);
			Angle newAngle = new Angle(first, second, third);
			return newAngle;
		}
	}
	
	/**
	 * Identify hidden triangles in a diagram
	 * @param diag the diagram
	 * @param hiddenAngles the List of hidden angles to use
	 * to find the hidden triangles
	 * @return a List of hidden triangles
	 */
	private static List<Triangle> identifyHiddenTriangles(Diagram diag, List<Angle> hiddenAngles) {
		List<Triangle> hiddenTriangles = null;
		
		// Add hidden triangles
		for (Figure a : hiddenAngles) {
			String originalAngleName = a.getName();
			// Vertices
			String sharedVert = originalAngleName.substring(1, 2);
			String v0 = originalAngleName.substring(0, 1);
			String v1 = originalAngleName.substring(2); 
			// Derive two hypothetical angles that WOULD exist if there was a hidden triangle
			String secondAngle = sharedVert + v0 + v1;
			String thirdAngle = sharedVert + v1 + v0;
			
			// Check if the two derived angles (secondAngle, thirdAngle) exist in the diagram
			if (!(diag.containsAngleSynonym(secondAngle) 
				&& diag.containsAngleSynonym(thirdAngle))) {
				continue;
			}
			// New triangle
			Angle originalAngle = (Angle)a;
			Vertex vertex0 = (Vertex) originalAngle.getChild(v0);
			Vertex vertex1 = (Vertex) originalAngle.getChild(v1);
			Vertex shared = (Vertex) originalAngle.getChild(sharedVert);
			Triangle triangle = new Triangle(vertex0, shared, vertex1);
			if (hiddenTriangles == null)
				hiddenTriangles = new ArrayList<>();
			hiddenTriangles.add(triangle);
		}
		
		return hiddenTriangles == null ? Collections.emptyList() : hiddenTriangles;
	}
	
	private static void preprocessGivenInfo(Diagram diagram) {
		// To avoid a ConcurrentModificationException
		List<FigureRelation> buff = new ArrayList<>(diagram.getFigureRelations());
		// For each figure relation pair
		for (FigureRelation pair : buff) {
			switch (pair.getRelationType()) {
			case BISECTS:
				preprocessBisectingPairs(diagram, pair);
				break;
			case PERPENDICULAR:
				preprocessPerpendicularPairs(diagram, pair);
				break;
			default:
				break;
			}
		}
	}
		
	/**
	 * The primary goal of this method is to convert all standard 
	 * {@link FigureRelation}s of type {@link FigureRelationType#BISECTS}
	 * to {@link BisectsFigureRelation}s, which are more detailed. The
	 * {@link ProofSolver} requires and assumes that all bisects FigureRelations
	 * will be of this type.
	 * @param diagram the diagram
	 * @param pair the {@link FigureRelation} to be handled
	 */
	private static void preprocessBisectingPairs(Diagram diagram, FigureRelation pair) {
		// Get the segment being bisecTED
		Segment bisectedSeg = pair.getFigure1();
		// Get the midpoint loc of the second segment (segment being bisecTED)
		Vec2 midptLoc = bisectedSeg.getCenter();
		// Get the vertex at that position
		Vertex midpt = getVertexAtLoc(diagram, midptLoc);
		
		if (midpt == null)
			throw new NullPointerException("No vertex at midpoint");

		// REPLACE THE GIVEN FigureRelation WITH A MORE DESCRIPIVE 
		// BisectsFigureRelation

		BisectsFigureRelation bisectsRel = new BisectsFigureRelation(
				pair.getFigure0(),
				bisectedSeg,
				midpt.getNameChar()
		);
		bisectsRel.addParents(pair.getParents());
		bisectsRel.setReason(pair.getReason());
		
		final int REL_INDEX = diagram.getFigureRelations().indexOf(pair);
		diagram.getFigureRelations().set(REL_INDEX, bisectsRel);
	}
	
	/**
	 * The primary goal of this method is to convert all standard 
	 * {@link FigureRelation}s of type {@link FigureRelationType#PERPENDICULAR}
	 * to {@link PerpendicularFigureRelation}s, which are more detailed. The
	 * {@link ProofSolver} requires and assumes that all perpendicular FigureRelations
	 * will be of this type.
	 * @param diagram the diagram
	 * @param pair the {@link FigureRelation} to be handled
	 */
	private static void preprocessPerpendicularPairs(Diagram diagram, FigureRelation pair) {
		// The two segments in the given figure relation pair
		Segment seg0 = pair.getFigure0(); // The intersectING segment
		Segment seg1 = pair.getFigure1(); // The intersectED segment
		
		// Get the vertex at the location at which the two segments intersect
		Vertex poi = getVertexAtLoc(diagram, Segment.getPointOfIntersection(seg0, seg1));
		
		// REPLACE THE GIVEN FigureRelation WITH A MORE DESCRIPIVE 
		// PerpendicularFigureRelation
		
		PerpendicularFigureRelation perpRel = new PerpendicularFigureRelation(
			pair.getFigure0(),
			pair.getFigure1(),
			poi.getNameChar()
		);
		perpRel.addParents(pair.getParents());
		perpRel.setReason(pair.getReason());
		
		final int REL_INDEX = diagram.getFigureRelations().indexOf(pair);
		diagram.getFigureRelations().set(REL_INDEX, perpRel);
	}
		
	private static void handleVerticalAngles(Diagram diagram) {
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
				
				if (ProofUtils.areVerticalAngles(a0, a1)) {
					FigureRelation rel = new FigureRelation(CONGRUENT, a0, a1);
					rel.setReason("Vertical Angle");
					diagram.addFigureRelation(rel);
				}
			}
		}
	}
	
	/*
	 * UTILITY METHODS
	 */
	
	private static boolean isSegmentEndpoint(Diagram diag, Vec2 loc) {
		for (Figure fig : diag.getFigures()) {
			if (fig.getClass() == Segment.class) {
				Segment seg = (Segment) fig;
				for (Vec2 endpt : seg.getVertexLocations()) {
					if (endpt.equals(loc))
						return true;
				}
			}
		}
		return false;
	}
	
	private static char generateNewVertexName(Diagram diag) {
		for (char c = 'A'; c <= 'Z';) {
			for (Figure fig : diag.getFigures()) {
				if (fig.isValidName("" + c)) {
					++c;
					continue;
				}
			}
			return c;
		}
		throw new NullPointerException("No more available vertex names.");
	}
	
	private static Vertex[] getFarthestVertices(List<Vertex> vertices) {
		// The pair of farthest vertices
		Vertex[] pair = new Vertex[2];
		// Distance of the previously checked pair of vertices
		float prevDist = 0f;
		for (int i = 0; i < vertices.size(); i++) {
			for (int j = 0; j < vertices.size(); j++) {
				// Don't wanna compare the same vertices
				if (i == j)
					continue;
				// Distance between the two vertices currently being checked
				final float newDist = Vec2.dist(vertices.get(i).getCenter(),
						vertices.get(j).getCenter());
				// If the distance of the vertices currently being checked is greater
				// than the previously farthest recorded pair of vertices,
				// update the pair of farthest vertices
				if (newDist > prevDist) {
					prevDist = newDist;
					pair[0] = vertices.get(i);
					pair[1] = vertices.get(j);
				}
			}
		}
		return pair;
	}
	
	private static Vertex getVertexAtLoc(Diagram diag, Vec2 loc) {
		// Check for vertex in given diagram
		for (Figure fig : diag.getFigures()) {
			// If the shape being checked is a vertex
			if (fig instanceof Vertex) {
				// Cast the shape as a vertex
				Vertex vert = (Vertex)fig;
				// If the vertex's loc is equal to the given loc
				if (vert.getCenter().equals(loc))
					// Return the vertex
					return vert;
			}
		}
//		throw new NullPointerException("No vertex at given location");
		return null;
	}
	
	/*
	 * END UTILITY METHODS
	 */
	
}
