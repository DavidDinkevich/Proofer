package geometry.proofs;

import java.util.List;

import geometry.Vec2;
import geometry.shapes.Angle;
import geometry.shapes.Arc;
import geometry.shapes.Segment;
import geometry.shapes.Segment.Slope;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

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
	 * @return the diagram if successful, or null if not successful
	 */
	public static Diagram generateDiagram(DiagramCanvas canvas, 
			FigureRelationListPanel figRelPanel) {
		
		// Compile the figures
		Diagram diagram = compileFigures(canvas, Diagram.Policy.FIGURES_AND_RELATIONS);
		
		// Preprocess given, return null in case of error
		if (preprocessGiven(diagram, canvas, figRelPanel) < 0)
			return null;
		
		// Preprocess perpendicular pair
		preprocessPerpendicularPairs(diagram);
		// Preprocess bisecting pairs
		preprocessBisectingPairs(diagram);
		
		return diagram;
	}
	
	/**
	 * Incorporates user-inputted information into the given Diagram
	 * @return -1 in the case of failure, 0 in case of success
	 */
	private static int preprocessGiven(Diagram diag, DiagramCanvas canvas,
			FigureRelationListPanel figRelPanel) {
		
		// Get given information
		for (FigureRelationPanel panel : figRelPanel.getFigureRelationPanels()) {
			// Ensure content
			if (!panel.hasContent())
				continue;
			
			FigureRelation rel = parseFigureRelationPanel(panel, diag);
			if (rel == null)
				return -1;
			rel.setReason(ProofReasons.GIVEN);
			// Add the given
			diag.addFigureRelation(rel);
		}
		
		// Get proof objective
		FigureRelationPanel objectivePanel = figRelPanel.getProofGoalPanel();
		FigureRelation proofObjective = parseFigureRelationPanel(objectivePanel, diag);
		if (proofObjective == null)
			return -1;
		proofObjective.setReason(ProofReasons.NONE);
		diag.setProofGoal(proofObjective);
		
		return 0; // Success
	}
	
	/**
	 * Ensure that the given {@link FigureRelationPanel} produces a valid {@link FigureRelation}
	 * @return a {@link FigureRelation} containing the panel's contents, or null if the panel does
	 * not contain a valid {@link FigureRelation}
	 */
	private static FigureRelation parseFigureRelationPanel(FigureRelationPanel panel,
			Diagram diag) {
		// Get the components of the panel
		FigureRelationType relType = panel.getRelationType();
		String figText0 = panel.getFigTextField0().getText();
		String figText1 = panel.getFigTextField1().getText();
		
		// Get the first figure
		Figure fig0 = searchForFigure(diag, figText0);
		if (fig0 == null) {
			showNonexistantFigureDialog(figText0);
			return null;
		}
		// Get second figure
		Figure fig1 = null;
		if (!FigureRelationType.isSingleFigureRelationType(relType)) {
			fig1 = searchForFigure(diag, figText1);
			if (fig1 == null) {
				showNonexistantFigureDialog(figText1);
				return null;
			}
		}
		// Make sure the figure relation is legal
		if (!FigureRelation.isLegalRelation(relType, fig0, fig1)) {
			String message = fig0.getClass().getSimpleName() + " " + relType + " " + 
						fig1.getClass().getSimpleName() + " is not a legal " + "statement";
			Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
			alert.showAndWait();
			// Failure
			return null;
		}

		return new FigureRelation(relType, fig0, fig1);
	}
		
	/**
	 * Display dialog showing that the figure was not found
	 */
	private static void showNonexistantFigureDialog(String name) {
		Alert alert = new Alert(AlertType.ERROR, "\"" + name + "\" does not exist", 
					ButtonType.OK);
		alert.showAndWait();
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
		else {
			fig = diagram.getFigure(name);
		}
		
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
		// Add hidden vertices
		addHiddenVerticesAndSegments(diagram);

		boolean figuresWereAdded = false;
		do {
			final int COUNT = diagram.getFigures().size();
			figuresWereAdded = false;
			
			for (int i = 0; i < COUNT-1; i++) {
				if (diagram.getFigures().get(i).getClass() != Segment.class)
					continue;
				Segment seg0 = (Segment) diagram.getFigures().get(i);
				for (int j = i + 1; j < COUNT; j++) {
					if (diagram.getFigures().get(j).getClass() != Segment.class)
						continue;
					Segment seg1 = (Segment) diagram.getFigures().get(j);
					// Get/add the hidden figure created by the two segments (or null)
					Figure hiddenFig = addHiddenSegmentOrAngle(diagram, seg0, seg1);
					// If we've found a new hidden figure
					if (hiddenFig != null)
						figuresWereAdded = true;
				}
			}
		} while (figuresWereAdded);
		
		// Make sure segments that fully contain other segments are compounds
		handleOverlappingSegments(diagram);
		
		// Add hidden triangles
		addHiddenTriangles(diagram);
	}
	
	/**
	 * Identify the hidden {@link Segment} or {@link Angle} between the
	 * given segments, and, if a segment, represent it with a new compound segment (a segment
	 * created by two other segments).
	 * @param seg0 the first segment
	 * @param seg1 the second segment
	 * @return the hidden segment OR angle, or null if the two given segments
	 * do not connect at one vertex
	 */
	private static Figure addHiddenSegmentOrAngle(Diagram diag, Segment seg0, Segment seg1) {
		// If we're analyzing the same segment, we can't combine it
		if (seg0.equals(seg1))
			return null;
		// Get the shared vertex between the two segments
		Vertex sharedVertex = ProofUtils.getSharedVertex(seg0, seg1);
		if (sharedVertex == null)
			return null;
		
		// Check if the two segments are parallel
		// Get slope of segment 0
		Slope seg0Slope = seg0.getSlope();
		// Get slope of segment 1
		Slope seg1Slope = seg1.getSlope();
		
		// Compare slopes: if the slopes are the same, add a new hidden segment.
		// Otherwise, add the hidden angle
		if (seg0Slope.equals(seg1Slope)) {
			// Combine segments		
			// The new, compound segment
			Segment newCompoundSegment = ProofUtils.getCompoundSegment(seg0, seg1);
			// Add the new segment as a hidden figure
			if (diag.addHiddenFigure(newCompoundSegment)) {
				// Mark as a compound segment
				diag.markAsCompoundSegment(newCompoundSegment);
				diag.addComponentVertex(newCompoundSegment.getName(), sharedVertex);
				return newCompoundSegment;
			}
		}
		// Add hidden angles
		else {
			// Create the new angle
			Angle newAngle = ProofUtils.getAngleBetween(seg0, seg1);
			// Add the new angle as a hidden figure
			if (diag.addHiddenFigure(newAngle)) {
				return newAngle;
			}
		}
		
		// No figure found
		return null;
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
		
		for (int i = 0; i < numFigures - 1; i++) {
			if (diag.getFigures().get(i).getClass() != Segment.class)
				continue;
			Segment seg0 = (Segment) diag.getFigures().get(i);
			for (int j = i + 1; j < numFigures; j++) {
				if (diag.getFigures().get(j).getClass() != Segment.class)
					continue;
				Segment seg1 = (Segment) diag.getFigures().get(j);
				
				// IF the segments intersect
				if (Segment.segmentsDoIntersect(seg0, seg1)) {
					// Get the point of intersection
					Vec2 poi = Segment.getPointOfIntersection(seg0, seg1);
					// Create a new vertex at the given intersection
					Vertex newVertex = null;
					// Skip if there already is a vertex at the given location
					if (getVertexAtLoc(diag, poi) == null) {
						// Create the vertex at the poi
						newVertex = new Vertex(generateNewVertexName(diag), poi);
						// Add the vertex (since it didn't exist before)
						diag.addHiddenFigure(newVertex);
						
						// Since there is now a vertex at the poi that is NOT a segment
						// end point, the two intersecting segments have now become
						// compound segments.
						diag.markAsCompoundSegment(seg0);
						diag.markAsCompoundSegment(seg1);
						diag.addComponentVertex(seg0.getName(), newVertex);
						diag.addComponentVertex(seg1.getName(), newVertex);
					}
					/*
					 * Even if there is a vertex at the poi, it is possible that
					 * the vertex is the endpoint of one/both of the intersecting segments.
					 * If so, we still want to count it.
					 * There are two possibilities: either the segments form a T or they form
					 * an L (not necessarily at right angles). In the former case, we want to 
					 * make the segment that contains the other segment's endpoint into a 
					 * compound segment
					 */
					else {
						// See if the vertex is a segment end point
						newVertex = getSegmentEndpoint(diag, poi);
						final char vname = newVertex.getNameChar();
						// If the segments form a T and not an L
						if (!(seg0.containsVertex(vname) && seg1.containsVertex(vname))) {
							Segment newCompSeg = seg0.containsVertex(newVertex.getNameChar()) ?
									seg1 : seg0;
							diag.markAsCompoundSegment(newCompSeg);
							diag.addComponentVertex(newCompSeg.getName(), newVertex);							
						}
					}
					
					// Add the 4 new "split" segments created by the hidden vertex
					Vertex[] seg0Vertices = seg0.getVertices();
					Vertex[] seg1Vertices = seg1.getVertices();
					
					Segment[] newSegs = {
						new Segment(seg0Vertices[0], newVertex),
						new Segment(seg0Vertices[1], newVertex),
						new Segment(seg1Vertices[0], newVertex),
						new Segment(seg1Vertices[1], newVertex)
					};

					// Add each new segment
					for (Segment newSeg : newSegs) {
						// In the case where two segments intersect and the poi
						// is one of the segment's end points, this can cause a bug
						// where one of the segments is the poi listed twice
						if (!newSeg.getVertexLoc(0).equals(newSeg.getVertexLoc(1))) {
							diag.addHiddenFigure(newSeg);
						}
					}
				}
			}
		}
		
		// Last thing to do is add any segments that were created by two
		// hidden vertices
		addConnectionSegments(diag);
	}
	
	/**
	 * Adds "connection segments". Connection Segments are segments that were created by 
	 * two hidden vertices (i.e., whose endpoints are both hidden vertices) AND lie 
	 * completely on top of another pre-existing segment.
	 * @param diag the diagram
	 */
	private static void addConnectionSegments(Diagram diag) {
		// Get a list of all hidden vertices
		List<Vertex> hiddenVerts = diag.getHiddenFigures(Vertex.class);
		
		// For each hidden vertex
		for (int i = 0; i < hiddenVerts.size() - 1; i++) {
			// For each other hidden vertex
			loop2:
			for (int j = i + 1; j < hiddenVerts.size(); j++) {
				// Create the hypothetical segment that would exist between the two
				// hidden vertices
				Segment connectingSeg = new Segment(hiddenVerts.get(i), hiddenVerts.get(j));
				// For each segment
				for (Figure fig : diag.getFigures()) {
					if (fig.getClass() != Segment.class)
						continue;
					Segment seg = (Segment) fig;
					// Make sure the hypothetical segment lies completely on top of a
					// pre-existing segment
					if (seg.containsSegment(connectingSeg)) {
						// Add the segment
						diag.addHiddenFigure(connectingSeg);
						continue loop2;
					}
				}
			}
		}
	}
	
	private static void handleOverlappingSegments(Diagram diag) {
		// Total number of figures before we add more
		final int numFigures = diag.getFigures().size();

		for (int i = 0; i < numFigures - 1; i++) {
			if (diag.getFigures().get(i).getClass() != Segment.class)
				continue;
			Segment seg0 = (Segment) diag.getFigures().get(i);
			for (int j = i + 1; j < numFigures; j++) {
				if (diag.getFigures().get(j).getClass() != Segment.class)
					continue;
				Segment seg1 = (Segment) diag.getFigures().get(j);
				// If (1) the segments are not on top of each other, and (2) one contains both
				// endpoints of the other
				if (seg0.getLength() != seg1.getLength() &&
						(seg0.containsSegment(seg1) || seg1.containsSegment(seg0))) {
					// Make the longer one a compound segment
					Segment longer = seg0.getLength() > seg1.getLength() ? seg0 : seg1;
					Segment shorter = seg0.getLength() > seg1.getLength() ? seg1 : seg0;
					diag.markAsCompoundSegment(longer);
					diag.addComponentVertices(longer.getName(), shorter.getVerticesList());
				}
			}
		}
	}
	
	/**
	 * Identify hidden triangles in a diagram
	 * @param diag the diagram
	 */
	private static void addHiddenTriangles(Diagram diagram) {
		for (Angle angle : diagram.getAllAnglesAndSynonyms()) {
			String originalAngleName = angle.getName();
			// Vertices
			String sharedVert = originalAngleName.substring(1, 2);
			String v0 = originalAngleName.substring(0, 1);
			String v1 = originalAngleName.substring(2); 
			// Derive three hypothetical segments that WOULD exist if there was a hidden triangle
			String[] hypoSegs = { v0 + sharedVert, sharedVert + v1, v0 + v1 };
			
			// Check if the three derived segments exist in the diagram
			if (!diagram.containsFigures(hypoSegs))
				continue;
			
			// New triangle
			Vertex vertex0 = (Vertex) angle.getChild(v0);
			Vertex vertex1 = (Vertex) angle.getChild(v1);
			Vertex shared = (Vertex) angle.getChild(sharedVert);
			Triangle triangle = new Triangle(vertex0, shared, vertex1);
			diagram.addHiddenFigure(triangle);
		}
	}
	
	/**
	 * The primary goal of this method is to convert all standard 
	 * {@link FigureRelation}s of type {@link FigureRelationType#BISECTS}
	 * to {@link SegmentBisectorFigureRelation}s, which are more detailed. The
	 * {@link ProofSolver} requires and assumes that all bisects FigureRelations
	 * will be of this type.
	 * @param diagram the diagram
	 */
	private static void preprocessBisectingPairs(Diagram diagram) {
		// Total number of FigureRelations before modification
		final int relCount = diagram.getFigureRelations().size();
		for (int i = 0; i < relCount; i++) {
			FigureRelation pair = diagram.getFigureRelations().get(i);
			if (pair.getRelationType() == FigureRelationType.BISECTS) {
				if (pair.getFigure1() instanceof Segment) {
					handleSegmentBisector(diagram, pair);
				} else {
					handleAngleBisector(diagram, pair);
				}
			}
		}
	}
	
	private static void handleSegmentBisector(Diagram diagram, FigureRelation pair) {
		// Get the segment being bisecTED
		Segment bisectedSeg = pair.getFigure1();
		// Get the midpoint loc of the second segment (segment being bisecTED)
		Vec2 midptLoc = bisectedSeg.getCenter();
		// Get the vertex at that position
		Vertex midpt = getVertexAtLoc(diagram, midptLoc);
		
		if (midpt == null) {
			throw new NullPointerException("No vertex at midpoint of segment: " + bisectedSeg);
		}

		// Convert bisector to largest compound segment (for standardization)
		Segment largestCompSegBisector = diagram
				.getLargestCompoundSegmentOf(pair.getFigure0().getName());
		
		// REPLACE THE GIVEN FigureRelation WITH A MORE DESCRIPTIVE type of figure relation

		SegmentBisectorFigureRelation bisectsRel = new SegmentBisectorFigureRelation(
				largestCompSegBisector == null ? pair.getFigure0() : largestCompSegBisector,
				bisectedSeg,
				midpt.getNameChar()
		);
		bisectsRel.addParents(pair.getParents());
		bisectsRel.setReason(pair.getReason());
		
		final int REL_INDEX = diagram.getFigureRelations().indexOf(pair);
		diagram.getFigureRelations().set(REL_INDEX, bisectsRel);
	}
	
	private static void handleAngleBisector(Diagram diagram, FigureRelation pair) {
		// Retrieve both figures involved
		Segment bisectingSeg = pair.getFigure0();
		Angle angle = pair.getFigure1();
		
		// Get the largest bisector (largest compound segment)
		Segment largestBisector = diagram.getLargestCompoundSegmentOf(bisectingSeg.getName());
		// Point of intersection between angle and segment
		String pointOfIntersection = angle.getNameShort();
		/*
		 * Get the 2nd endpoint of the SMALLEST bisector (the first is the point of intersection)
		 */
		String endpoint;
		// If the bisector is NOT a compound segment, we just have to get it's other vertex
		if (!diagram.isCompoundSegment(largestBisector.getName())) {
			endpoint = ProofUtils.getOtherVertex(largestBisector.getName(), pointOfIntersection);
		}
		// If it is a compound segment, we need to do some more stuff
		else {
			List<Vertex> compSegVerts = diagram.getComponentVertices(largestBisector.getName());
			// Get the index of the point of intersection in the list of the largest bisector's
			// component vertices
			int indexOfPOI = 0;
			for (; !compSegVerts.get(indexOfPOI).isValidName(pointOfIntersection); indexOfPOI++);
			// There are two options: the second endpoint is the vertex BEFORE the POI, or
			// the one afterward. Whichever one lies inside the main angle is the second endpoint.
			
			// If the index of the POI is 0, then there is no vertex before it, 
			// so the endpoint is after the POI, at index 1
			if (indexOfPOI == 0) {
				endpoint = compSegVerts.get(indexOfPOI + 1).getName(); // indexOfPOI = 1
			}
			// If the index of the POI is the last index in the array, then there is no vertex after
			// it, so the endpoint is before the POI, at indexOfPOI-1
			else if (indexOfPOI == compSegVerts.size() - 1) {
				endpoint = compSegVerts.get(indexOfPOI - 1).getName();
			} else {
				Vertex before = compSegVerts.get(indexOfPOI - 1);
				Vertex after = compSegVerts.get(indexOfPOI + 1);
				Arc angleArc = ProofUtils.getArc(angle);
				endpoint = ProofUtils.arcContainsPoint(angleArc, before.getCenter(), -1f) 
						? before.getName() : after.getName();
			}
		}
		
		// Construct the name of the smallest bisector
		String smallestBisector = pointOfIntersection + endpoint;
		
		// Create the new, more detailed type of figure relation
		AngleBisectorFigureRelation newRel = new AngleBisectorFigureRelation(
				largestBisector, angle, smallestBisector, endpoint);
		newRel.addParents(pair.getParents());
		newRel.setReason(pair.getReason());

		// Replace the original figure relation
		final int REL_INDEX = diagram.getFigureRelations().indexOf(pair);
		diagram.getFigureRelations().set(REL_INDEX, newRel);
	}
	
	/**
	 * The primary goal of this method is to convert all standard 
	 * {@link FigureRelation}s of type {@link FigureRelationType#PERPENDICULAR}
	 * to {@link PerpendicularFigureRelation}s, which are more detailed. The
	 * {@link ProofSolver} requires and assumes that all perpendicular FigureRelations
	 * will be of this type.
	 * @param diagram the diagram
	 */
	private static void preprocessPerpendicularPairs(Diagram diagram) {
		// Total number of FigureRelations before modification
		final int relCount = diagram.getFigureRelations().size();
		for (int i = 0; i < relCount; i++) {
			FigureRelation pair = diagram.getFigureRelations().get(i);
			if (pair.getRelationType() == FigureRelationType.PERPENDICULAR) {			
				// The two segments in the given figure relation pair
				Segment seg0 = pair.getFigure0(); // The intersectING segment
				Segment seg1 = pair.getFigure1(); // The intersectED segment
				
				// Get the vertex at the location at which the two segments intersect
				Vertex poi = getVertexAtLoc(diagram, Segment.getPointOfIntersection(seg0, seg1));
				
				// REPLACE THE GIVEN FigureRelation WITH A MORE DESCRIPIVE 
				// PerpendicularFigureRelation
				
				PerpendicularFigureRelation perpRel = new PerpendicularFigureRelation(
					// Make sure segments are the largest compound segments
					diagram.getLargestCompoundSegmentOf(pair.getFigure0().getName()),
					diagram.getLargestCompoundSegmentOf(pair.getFigure1().getName()),
					poi.getNameChar()
				);
				perpRel.addParents(pair.getParents());
				perpRel.setReason(pair.getReason());
				
				final int REL_INDEX = diagram.getFigureRelations().indexOf(pair);
				diagram.getFigureRelations().set(REL_INDEX, perpRel);
			}
		}
	}
		
	private static void handleVerticalAngles(Diagram diagram) {
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
					rel.setReason(ProofReasons.VERTICAL_ANGLES_CONGRUENT);
					diagram.addFigureRelation(rel);
				}
			}
		}
	}
	
	/*
	 * UTILITY METHODS
	 */
	
	private static Vertex getSegmentEndpoint(Diagram diag, Vec2 loc) {
		for (Figure fig : diag.getFigures()) {
			if (fig.getClass() == Segment.class) {
				Segment seg = (Segment) fig;
				for (Vertex vertex : seg.getVertices()) {
					// Dist to account for small rounding errors
					if (Vec2.dist(vertex.getCenter(), loc) < 0.05f) {
						return vertex;
					}
				}
			}
		}
		return null;
	}
	
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
