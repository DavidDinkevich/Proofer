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
	public static Diagram compileFigures(DiagramCanvas canvas, Diagram.Policy policy) {
		Diagram diagram = new Diagram(policy);
		
		// Gather figures
		for (GraphicsShape<?> shape : canvas.getDiagramFigures()) {
			diagram.addFigure(shape.getShape());
		}
		
		// Add and include all hidden figures
		addHiddenFigures(diagram);
		
		if (policy == Diagram.Policy.FIGURES_AND_RELATIONS) {
			// Make vertical angles congruent
			handleVerticalAngles(diagram);
		}
		
		return diagram;
	}

	/**
	 * Prepare a {@link Diagram} to be processed by {@link ProofSolver}.
	 * @param canvas the {@link}
	 * @param figRelPanel
	 * @return the diagram
	 */
	public static Diagram generateDiagram(DiagramCanvas canvas, 
			FigureRelationListPanel figRelPanel) {
		
		// Compile the figures
		Diagram diagram = compileFigures(canvas, Diagram.Policy.FIGURES_AND_RELATIONS);
				
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
		
		// Add hidden vertices
		addHiddenVerticesAndSegments(diagram);
		
		do {
			final int COUNT = diagram.getFigures().size();
			figuresWereAdded = false;
			
			// Loop through segments
			for (int i = 0; i < COUNT-1; i++) {
				// If the inspected figure is a segment
				if (diagram.getFigures().get(i).getClass() != Segment.class)
					continue;
				// Get the segment
				Segment seg0 = (Segment) diagram.getFigures().get(i);
				// Loop through figures again
				for (int j = i + 1; j < COUNT; j++) {
					// If the inspected figure is a segment
					if (diagram.getFigures().get(j).getClass() != Segment.class)
						continue;
					// Get the segment
					Segment seg1 = (Segment) diagram.getFigures().get(j);
					// Get the hidden figure created by the two segments (or null)
					Figure hiddenFig = identifyHiddenSegOrAngle(seg0, seg1);
					// If we've found a hidden figure AND it does not already exist, add it
					if (hiddenFig != null && diagram.addHiddenFigure(hiddenFig)) {
						figuresWereAdded = true; // Update variable
						// If the hidden figure we found is an angle, store it
						if (hiddenFig instanceof Angle)
							hiddenAngles.add((Angle) hiddenFig);
					}
				}
			}
	
			// Try to find hidden triangles with the new hidden angles we found (or didn't)
			List<Triangle> hiddenTris = identifyHiddenTriangles(diagram, hiddenAngles);
			// If hidden triangles were added to the diagram
			if (diagram.addHiddenFigures(hiddenTris))
				figuresWereAdded = true; // Update variable
		} while (figuresWereAdded);
	}
	
	/**
	 * Find and add hidden vertices (vertices created by two intersecting segments,
	 * not including segment end-points). This method will also create and add
	 * the new segments created by the hidden vertex (the hidden vertex "chops" the segment
	 * into two).
	 * @param diag the diagram
	 */
	private static void addHiddenVerticesAndSegments(Diagram diag) {
		// Total number of figures before we add more
		final int numFigures = diag.getFigures().size();
		
		// For each figure
		for (int i = 0; i < numFigures - 1; i++) {
			// Only segments
			if (diag.getFigures().get(i).getClass() != Segment.class)
				continue;
			// Get the segment
			Segment seg0 = (Segment) diag.getFigures().get(i);
			// For every other figure
			for (int j = i + 1; j < numFigures; j++) {
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
						diag.addHiddenFigure(newVertex);
						
						// Add the 4 new "split" segments created by the hidden vertex
						Vertex[] seg0Vertices = seg0.getVertices();
						Vertex[] seg1Vertices = seg1.getVertices();
						Segment s0 = new Segment(seg0Vertices[0], newVertex);
						Segment s1 = new Segment(seg0Vertices[1], newVertex);
						Segment s2 = new Segment(seg1Vertices[0], newVertex);
						Segment s3 = new Segment(seg1Vertices[1], newVertex);
						
						diag.addHiddenFigures(Arrays.asList(s0, s1, s2, s3));
					}
				}
			}
		}		
	}
	
	/**
	 * Identify the hidden {@link Segment} or {@link Angle} between the
	 * given segments.
	 * @param seg0 the first segment
	 * @param seg1 the second segment
	 * @return the hidden segment OR figure, or null if the two given segments
	 * do not connect at one vertex
	 */
	private static Figure identifyHiddenSegOrAngle(Segment seg0, Segment seg1) {
//		// If we're analyzing the same segment, we can't combine it
		if (seg0.equals(seg1))
			return seg0;
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
			Vertex[] newSegVerts = ProofUtils.getFarthestVertices(segVerts);
			
			// The new, combined straight line
			Segment newStraightLine = new Segment(newSegVerts);
			return newStraightLine;
		}
		// Add hidden angles
		else {
			// Get the angle between the two segments
			String angleName = ProofUtils.getAngleBetween(seg0.getName(), seg1.getName());
			// Get the three vertices of the angle
			String unsharedVert0 = angleName.substring(0, 1);
			String unsharedVert1 = angleName.substring(2);
			String sharedVert = angleName.substring(1, 2);
			Vertex first, second, third;
			first = (Vertex) (seg0.containsChild(unsharedVert0) ?
					seg0.getChild(unsharedVert0) : seg0.getChild(unsharedVert1));
			third = (Vertex) (first.getNameChar() == unsharedVert0.charAt(0) ?
					seg1.getChild(unsharedVert1) : seg1.getChild(unsharedVert0));
			second = (Vertex) seg0.getChild(sharedVert);
			// Create the new angle
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
		
		// For each figure
		for (int i = 0; i < diagram.getFigures().size() - 1; i++) {
			// If the figure is NOT an angle, we don't care about it
			if (diagram.getFigures().get(i).getClass() != Angle.class)
				continue;
			// Get the angle
			Angle a0 = (Angle) diagram.getFigures().get(i);
			// For each other figure
			for (int j = i + 1; j < diagram.getFigures().size(); j++) {
				// Only angles
				if (diagram.getFigures().get(j).getClass() != Angle.class)
					continue;
				// Get the other angle
				Angle a1 = (Angle) diagram.getFigures().get(j);
				// If they are vertical angles, make them congruent
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
	
//	private static boolean isSegmentEndpoint(Diagram diag, Vec2 loc) {
//		for (Figure fig : diag.getFigures()) {
//			if (fig.getClass() == Segment.class) {
//				Segment seg = (Segment) fig;
//				for (Vec2 endpt : seg.getVertexLocations()) {
//					if (endpt.equals(loc))
//						return true;
//				}
//			}
//		}
//		return false;
//	}
	
//	/**
//	 * Get all of the shared vertices between two triangles
//	 * @param tri0 the first triangle
//	 * @param tri1 the second triangle
//	 * @return the shared vertices
//	 */
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
	
	private static char generateNewVertexName(Diagram diag) {
		outer:
		for (char c = 'A'; c <= 'Z';) {
			for (Figure fig : diag.getFigures()) {
				if (fig.isValidName("" + c)) {
					++c;
					continue outer;
				}
			}
			return c;
		}
		throw new NullPointerException("No more available vertex names.");
	}
		
	private static Vertex getVertexAtLoc(Diagram diag, Vec2 loc) {
		// Check for vertex in given diagram
		for (Figure fig : diag.getFigures()) {
			// If the shape being checked is a vertex
			if (fig.getClass() == Vertex.class) {
				// Cast the shape as a vertex
				Vertex vert = (Vertex)fig;
				// If the vertex's loc is equal to the given loc
//				if (vert.getCenter().equals(loc))
				// This prevents minor rounding errors
				if (Vec2.dist(vert.getCenter(), loc) < 0.05f) {
					// Return the vertex
					return vert;
				}
			}
		}
//		throw new NullPointerException("No vertex at given location");
		return null;
	}
	
	/*
	 * END UTILITY METHODS
	 */
	
}
