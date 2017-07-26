package geometry.proofs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import geometry.Vec2;
import geometry.shapes.Segment;
import geometry.shapes.Shape;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;

import ui.canvas.DiagramCanvas;
import ui.canvas.GraphicsShape2D;
import ui.swing.FigureRelationListPanel;
import ui.swing.FigureRelationPairPanel;

import util.Utils;

public class Preprocessor {
	private DiagramCanvas canvas;
	private FigureRelationListPanel figRelPanel;
	
	public Preprocessor(DiagramCanvas canvas, FigureRelationListPanel figRelPanel) {
		this.canvas = canvas;
		this.figRelPanel = figRelPanel;
	}
	
	public Diagram process() {
		return generateDiagram();
	}
	
	private Diagram generateDiagram() {
		Diagram diagram = new Diagram();
		
		// Add and include all hidden figures
		addHiddenFigures(diagram);
		
		// Determine figures
		for (GraphicsShape2D<?> shape : canvas.getDiagramElements()) {
			diagram.addFigure(determineFigure(shape.getShape()));
		}
		
		// Determine given
		for (FigureRelationPairPanel panel : figRelPanel.getFigureRelationPairPanels()) {
			if (!panel.isFilledCompletely())
				continue;
			FigureRelationType relType =
					(FigureRelationType)panel.getRelationBox().getSelectedItem();
			String figText0 = panel.getFigTextField0().getText();
			String figText1 = panel.getFigTextField1().getText();
				
			FigureRelationPair rel = new FigureRelationPair(
					relType,
					searchForFigure(diagram, figText0),
					searchForFigure(diagram, figText1)
			);
			diagram.addFigureRelationPair(rel);
		}
		
		correctGivenInformation(diagram);
		
		// Determine proof goal (to prove)
		FigureRelationPairPanel goalPanel = figRelPanel.getProofGoalPanel();
		FigureRelationPair proofGoal = new FigureRelationPair(
				(FigureRelationType)goalPanel.getRelationBox().getSelectedItem(),
				searchForFigure(diagram, goalPanel.getFigTextField0().getText()),
				searchForFigure(diagram, goalPanel.getFigTextField1().getText())
		);
		diagram.setProofGoal(proofGoal);
		
		return diagram;
	}
	
	private Figure determineFigure(Shape shape) {
		if (shape instanceof Triangle) {
			return new TriangleFigure(shape.getName());
		}
		// TODO: include more shapes
		return null;
	}
	
	private Figure searchForFigure(Diagram diagram, String name) {
		Figure fig = null;
		if (name.length() == 4) { // 3 cars and a special character
			final boolean figIsTri = name.startsWith(Utils.DELTA);
			final boolean figIsAngle = !figIsTri && name.startsWith(Utils.ANGLE_SYMBOL);
			if (figIsTri)
				fig = diagram.getFigure(name.substring(1), TriangleFigure.class);
			else if (figIsAngle)
				fig = diagram.getFigure(name.substring(1), AngleFigure.class);
		}
		else
			fig = diagram.getFigure(name);
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
	private void addHiddenFigures(Diagram diagram) {
		final int COUNT = canvas.getDiagramElements().count();
		
		for (int i = 0, lastCheckedTri = i-1; i < COUNT; i++, lastCheckedTri++) {
			// If the element's shape is a Triangle
			if (canvas.getDiagramElements().get(i).getShape() instanceof Triangle) {
				// Get the Triangle
				Triangle tri0 = (Triangle)canvas.getDiagramElements().get(i).getShape();
				// Loop through all of the other elements in the list
				for (int j = 0; j < COUNT; j++) {
					// Prevent comparing the same element AND comparing this Triangle
					// with the most recently checked Triangle
					if (i == j || j == lastCheckedTri)
						continue;
					// If the second element's shape is a Triangle
					if (canvas.getDiagramElements().get(j).getShape() instanceof Triangle) {
						// Get the second element's shape
						Triangle tri1 = (Triangle)canvas.getDiagramElements().get(j).getShape();
						
						//
						for (Vertex sharedVertex : getSharedVertices(tri0, tri1)) {
							diagram.addFigures(getStraightLines(tri0, tri1, sharedVertex));
						}
						
						
					}
				}
			}
		}
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
	
	private Collection<Figure> getStraightLines(Triangle tri0, Triangle tri1, Vertex sharedVertex) {
		Collection<Figure> segs = new ArrayList<>();
		
		// Loop through segments in first triangle
		for (Segment seg0 : tri0.getSides()) {
			// If the segment DOES NOT contains the shared vertex (given)
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
				Vec2 seg0Slope = seg0.getSlope();
				// Get slope of segment 1
				Vec2 seg1Slope = seg1.getSlope();
				// Compare slopes
				if (seg0Slope.equals(seg1Slope)) {
					// Combine segments
					
					// Vertices of both segments in one list
					List<Vertex> segVerts = new ArrayList<>(seg0.getVertices());
					segVerts.addAll(seg1.getVertices());
					// ---------------------
					// Vertices of new segment--farthest apart
					Vertex[] newSegVerts = getFarthestVertices(segVerts);
					
					SegmentFigure fig =
							new SegmentFigure(newSegVerts[0].getName()
									+ newSegVerts[1].getName());
					segs.add(fig);
				}
			}
		}
		return segs;
	}
	
	private Vertex[] getFarthestVertices(List<Vertex> vertices) {
		Vertex[] pair = new Vertex[2];
		float prevDist = 0f;
		for (int i = 0; i < vertices.size(); i++) {
			for (int j = 0; j < vertices.size(); j++) {
				if (i == j)
					continue;
				final float newDist = Vec2.dist(vertices.get(i).getScaledCenter(),
						vertices.get(j).getScaledCenter());
				if (newDist > prevDist) {
					prevDist = newDist;
					pair[0] = vertices.get(i);
					pair[1] = vertices.get(j);
				}
			}
		}
		return pair;
	}
	
	private Segment getSegment(Diagram diagram, String name) {
		// Search the list of FIGURES, because it may contain
		// hidden figures that are not a diagram element
		SegmentFigure segFig = (SegmentFigure)diagram.getFigure(name);
		if (segFig != null) {
			return new Segment(segFig)
		}
		
		// Search segments belonging to triangles
		for (GraphicsShape2D<?> gShape : canvas.getDiagramElements()) {
			if (gShape.getShape() instanceof Triangle) {
				Triangle tri = (Triangle)gShape.getShape();
				Segment seg = tri.getSegment(name.charAt(0), name.charAt(1));
				if (seg != null) {
					return seg;
				}
			}
		}
		return null;
	}
	
	public Vertex getVertexAtLoc(Vec2 loc) {
		for (GraphicsShape2D<?> gShape : canvas.getDiagramElements()) {
			if (gShape.getShape() instanceof Triangle) {
				Triangle tri = (Triangle)gShape.getShape();
				for (Vertex vert : tri.getVertices()) {
					if (vert.getScaledCenter().equals(loc))
						return vert;
				}
			}
		}
		return null;
	}
		
	private void correctGivenInformation(Diagram diagram) {
		/*
		 * Bisecting pairs --> convert "bisect" to "midpoint"
		 */
		// Get all bisecting pairs
		List<FigureRelationPair> bisectingPairs =
				diagram.getAllRelationPairsWithType(FigureRelationType.BISECTS);
		// For each bisecting pair
		for (FigureRelationPair pair : bisectingPairs) {
			// Get the name of the first segment
			String segName0 = pair.getFigure0().getName();
			// Get the name of the second segment
			String segName1 = pair.getFigure1().getName();
			// Get the first segment
			Segment seg0 = getSegment(diagram, segName0);
			// Get the second segment
			Segment seg1 = getSegment(diagram, segName1);
			
			if (seg0 == null || seg1 == null)
				throw new AssertionError("This shouldn't be possible");
			
			// Get the midpoint loc of the second segment (segment being bisecTED)
			Vec2 midptLoc = seg1.getScaledCenter();
			// Get the vertex at that position
			Vertex midpt = getVertexAtLoc(midptLoc);
			
			if (midpt == null)
				throw new NullPointerException("No vertex at midpoint");
			
			/*
			 * Remove the bisecting pair from the diagram's given information
			 * (all bisecting pairs are replaced with more specific midpoint
			 * pairs.
			 */
			diagram.removeFigureRelationPair(pair);
			
			// Construct/add new midpoint pair
			diagram.addFigureRelationPair(
					FigureRelationType.MIDPOINT,
					midpt.getName(),
					segName1
			);
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
