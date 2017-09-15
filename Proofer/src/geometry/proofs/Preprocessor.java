package geometry.proofs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import geometry.Vec2;
import geometry.shapes.Angle;
import geometry.shapes.Segment;
import geometry.shapes.Segment.Slope;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;

import ui.canvas.DiagramCanvas;
import ui.canvas.GraphicsShape2D;
import ui.swing.FigureRelationListPanel;
import ui.swing.FigureRelationPanel;

import util.Utils;

public class Preprocessor {
	private DiagramCanvas canvas;
	private FigureRelationListPanel figRelPanel;
	
	public Preprocessor(DiagramCanvas canvas, FigureRelationListPanel figRelPanel) {
		this.canvas = canvas;
		this.figRelPanel = figRelPanel;
	}
	
	public Diagram generateDiagram() {
		Diagram diagram = new Diagram();
		
		// Gather figures
		for (GraphicsShape2D<?> shape : canvas.getDiagramElements()) {
			diagram.addFigure(shape.getShape());
		}
		
		// Add and include all hidden figures
		addHiddenFigures(diagram);
		
		// Make vertical angles congruent
		handleVerticalAngles(diagram);
		
		// Determine given
		for (FigureRelationPanel panel : figRelPanel.getFigureRelationPairPanels()) {
			if (!panel.hasContent())
				continue;
			// Figure relation type
			FigureRelationType relType =
					(FigureRelationType)panel.getRelationBox().getSelectedItem();
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
					searchForFigure(diagram, figText1),
					null // Null parent?
			);
			// Add the given
			diagram.addFigureRelationPair(rel);
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
				(FigureRelationType)goalPanel.getRelationBox().getSelectedItem(),
				searchForFigure(diagram, goalPanel.getFigTextField0().getText()),
				searchForFigure(diagram, goalPanel.getFigTextField1().getText()),
				null // Null parent?
		);
		diagram.setProofGoal(proofGoal);
		
		return diagram;
	}
	
	private Figure searchForFigure(Diagram diagram, String name) {
		Figure fig = null;
		// Angle or triangle
		if (name.length() == 4) { // 3 cars and a special character
			final boolean figIsTri = name.startsWith(Utils.DELTA);
			final boolean figIsAngle = !figIsTri && name.startsWith(Utils.ANGLE_SYMBOL);
			if (figIsTri)
				fig = diagram.getFigure(name.substring(1), Triangle.class);
			else if (figIsAngle)
				fig = diagram.getFigure(name.substring(1), Angle.class);
		}
		// Not triangle or angle
		else
			fig = diagram.getFigure(name);
		return fig;
	}
	
	/**
	 * Get all of the shared vertices between two triangles
	 * @param tri0 the first triangle
	 * @param tri1 the second triangle
	 * @return the shared vertices
	 */
	private List<Vertex> getSharedVertices(Triangle tri0, Triangle tri1) {
		List<Vertex> commonVertices = null;
		outer:
			for (Vertex vert0 : tri0) {
				for (Vertex vert1 : tri1) {
					if (vert0.equals(vert1)) {
						if (commonVertices == null)
							commonVertices = new ArrayList<>();
						commonVertices.add(vert0);
						continue outer;
					}
				}
			}
		return commonVertices == null ? Collections.emptyList() : commonVertices;
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
	private void addHiddenFigures(Diagram diagram) {
		final int COUNT = diagram.getFigures().size();
		
		for (int i = 0, lastCheckedTri = i-1; i < COUNT; i++, lastCheckedTri++) {
			// If the element's shape is a Triangle
			if (diagram.getFigures().get(i) instanceof Triangle) {
				// Get the Triangle
				Triangle tri0 = (Triangle)diagram.getFigures().get(i);
				// Loop through all of the other elements in the list
				for (int j = 0; j < COUNT; j++) {
					// Prevent comparing the same element AND comparing this Triangle
					// with the most recently checked Triangle
					if (i == j || j == lastCheckedTri)
						continue;
					// If the second element's shape is a Triangle
					if (diagram.getFigures().get(j) instanceof Triangle) {
						// Get the second element's shape
						Triangle tri1 = (Triangle)diagram.getFigures().get(j);
						// Add straight lines to diagram's list of elements
						for (Vertex sharedVertex : getSharedVertices(tri0, tri1)) {
							SimpleEntry<Collection<Figure>, Collection<Figure>> figures =
									identifyHiddenFigures(tri0, tri1, sharedVertex);
							diagram.addFigures(figures.getKey());
							diagram.addFigures(figures.getValue());
						}
					}
				}
			}
		}
	}
	
	private SimpleEntry<Collection<Figure>, Collection<Figure>>
		identifyHiddenFigures(Triangle tri0, Triangle tri1, Vertex sharedVertex) {
		Collection<Figure> segs = new ArrayList<>();
		Collection<Figure> angles = new ArrayList<>();
		
		// Loop through segments in first triangle
		for (Segment seg0 : tri0.getSides()) {
			// If the segment DOES NOT contain the shared vertex (given)
			if (!seg0.getName().contains(sharedVertex.getName()))
				continue;
			// Go through segments of second triangle
			for (Segment seg1 : tri1.getSides()) {
				// If segment in second triangle does NOT contains the shared vertex
				if (!seg1.getName().contains(sharedVertex.getName()))
					continue;
				// If we're analyzing the same segment, we can't combine it
				if (seg1.equals(seg0))
					continue;
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
					segs.add(newStraightLine);
				}
				// Add hidden angles
				else {
					String angleName = Utils.getAngleBetween(seg0.getName(), seg1.getName());
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
					angles.add(newAngle);
					System.err.println(newAngle.getName());
				}
			}
		}
		return new SimpleEntry<>(segs, angles);
	}
	
	private Vertex[] getFarthestVertices(List<Vertex> vertices) {
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
				final float newDist = Vec2.dist(vertices.get(i).getCenter(true),
						vertices.get(j).getCenter(true));
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
	
	private void preprocessGivenInfo(Diagram diagram) {
		// To avoid a ConcurrentModificationException
		List<FigureRelation> buff = new ArrayList<>(diagram.getFigureRelations());
		// For each figure relation pair
		for (FigureRelation pair : buff) {
			preprocessIntersectingLines(diagram, pair);
		}
	}
	
	/**
	 * Preprocess intersecting lines (bisecting lines, perpendicular lines)
	 * @param diagram
	 * @param pair
	 */
	private void preprocessIntersectingLines(Diagram diagram, FigureRelation pair) {
		// If the pair type is of type perpendicular or bisecting
		if (pair.getRelationType() == FigureRelationType.BISECTS
				|| pair.getRelationType() == FigureRelationType.PERPENDICULAR) {
			// Get the first segment
			Segment seg0 = pair.getFigure0();
			// Get the second segment
			Segment seg1 = pair.getFigure1();
			
			if (seg0 == null || seg1 == null)
				throw new AssertionError("This shouldn't be possible");
						
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
	 * Handle bisecting pairs. (Convert bisecting pairs to midpoint pairs.)
	 * @param diagram the diagram
	 * @param pair the figure relation pair to be handled
	 */
	private void preprocessBisectingPairs(Diagram diagram, FigureRelation pair) {
		// Get the segment being bisecTED
		Segment seg1 = pair.getFigure1();
		// Get the midpoint loc of the second segment (segment being bisecTED)
		Vec2 midptLoc = seg1.getCenter(true);
		// Get the vertex at that position
		Vertex midpt = getVertexAtLoc(diagram, midptLoc);
		
		if (midpt == null)
			throw new NullPointerException("No vertex at midpoint");

		// TODO: verify technique
		/*
		 * Remove the bisecting pair from the diagram's given information
		 * (all bisecting pairs are replaced with more specific midpoint
		 * pairs).
		 */
		diagram.removeFigureRelationPair(pair);
		
		Segment bisectedSeg = pair.getFigure1();
		
		// Construct/add new midpoint pair
		diagram.addFigureRelationPair(
				FigureRelationType.MIDPOINT,
				midpt.getName(),
				bisectedSeg.getName(),
				pair
		);
	}
	
	/**
	 * Handle perpendicular pairs.
	 * @param diagram the diagram
	 * @param pair the figure relation pair to be handled
	 */
	private void preprocessPerpendicularPairs(Diagram diagram, FigureRelation pair) {
		// The two segments in the given figure relation pair
		Segment seg0 = pair.getFigure0(); // The intersectING segment
		Segment seg1 = pair.getFigure1(); // The intersectED segment
		
		// Get the vertex at the location at which the two segments intersect
		Vertex poi = getVertexAtLoc(diagram,
				Segment.getPointOfIntersection(seg0, seg1, true));
		
		// The full intersecting segment
		Segment fullIntersectingSeg = pair.getFigure0();
		
		// The endpoint(s) of the intersectING segment that do not LIE on the
		// intersectED segment
		List<String> nonIntersectingVerts = new ArrayList<>();
		// Iterate through endpoints of intersectING vertices
		for (int i = 0; i < fullIntersectingSeg.getName().length(); i++) {
			final char c = fullIntersectingSeg.getName().charAt(i);
			if (poi.getNameChar() != c) {
				nonIntersectingVerts.add(String.valueOf(c));
			}
		}
		
		// Remove the original figure relation pair, replace it with more specific ones
		// that the proof solver can process
		diagram.removeFigureRelationPair(pair);
		
		// Replace given, original figure relation pair with more specific
		// ones
		for (String nonIntersectingVert : nonIntersectingVerts) {
			// Segment from point of intersection to an endpoint of the intersectING segment
			// that does not lie on the intersectED segment.
			Segment smallIntersectingSeg =
					diagram.getFigure(nonIntersectingVert + poi.getName());
			
			// Segment from point of intersection to an endpoint of the intersectED segment
			Segment baseSeg0 =
					diagram.getFigure(poi.getName() + seg1.getName().substring(0, 1));
			// Segment from point of intersection to the other
			// endpoint of the intersectED segment
			Segment baseSeg1 =
					diagram.getFigure(poi.getName() + seg1.getName().substring(1));
			
			// Add more specific figure relation pairs to replace the given one
			for (int i = 0; i < 2; i++) {
				diagram.addFigureRelationPair(
						FigureRelationType.PERPENDICULAR,
						smallIntersectingSeg.getName(),
						(i == 0 ? baseSeg0 : baseSeg1).getName(),
						pair // Parent
				);
			}
		}		
	}
	
	private Vertex getVertexAtLoc(Diagram diag, Vec2 loc) {
		// Check for vertex in given diagram
		for (Figure fig : diag.getFigures()) {
			// If the shape being checked is a vertex
			if (fig instanceof Vertex) {
				// Cast the shape as a vertex
				Vertex vert = (Vertex)fig;
				// If the vertex's loc is equal to the given loc
				if (vert.getCenter(true).equals(loc))
					// Return the vertex
					return vert;
			}
		}
		return null;
	}
	
	private void handleVerticalAngles(Diagram diagram) {
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
				
				if (Utils.areVerticalAngles(a0, a1)) {
					FigureRelation rel = new FigureRelation(
							FigureRelationType.CONGRUENT,
							a0,
							a1,
							null // Null parent?
					);
					diagram.addFigureRelationPair(rel);
				}
			}
		}
	}
	
	public DiagramCanvas getDiagramCanvas() {
		return canvas;
	}

	public void setDiagramCanvas(DiagramCanvas canvas) {
		this.canvas = canvas;
	}

	public FigureRelationListPanel getFigureRelationListPanel() {
		return figRelPanel;
	}

	public void setFigureRelationListPanel(FigureRelationListPanel figRelPanel) {
		this.figRelPanel = figRelPanel;
	}
}
