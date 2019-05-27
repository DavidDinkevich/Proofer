package geometry.proofs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import geometry.Dimension;
import geometry.Vec2;
import geometry.shapes.Angle;
import geometry.shapes.Arc;
import geometry.shapes.Segment;
import geometry.shapes.Triangle;
import geometry.shapes.Vertex;

import util.Utils;

import static geometry.proofs.FigureRelationType.CONGRUENT;
import static geometry.proofs.FigureRelationType.RIGHT;
import static geometry.proofs.FigureRelationType.ISOSCELES;
import static geometry.proofs.FigureRelationType.MIDPOINT;
import static geometry.proofs.FigureRelationType.PERPENDICULAR;
import static geometry.proofs.FigureRelationType.PARALLEL;
import static geometry.proofs.FigureRelationType.SIMILAR;
import static geometry.proofs.FigureRelationType.COMPLEMENTARY;
import static geometry.proofs.FigureRelationType.SUPPLEMENTARY;


public class ProofUtils {
	
	// Don't instantiate an object of this class, or it'll bite you back...
	private ProofUtils() {
		throw new AssertionError("Do NOT instantiate an object of this class!");
	}
	
	public static final String DELTA = "^";
	public static final String ANGLE_SYMBOL = "<";
	public static final String GEOMETRY_CHARS = DELTA + ANGLE_SYMBOL;

	public static boolean isPermissibleText(char c) {
		return GEOMETRY_CHARS.contains(String.valueOf(c)) 
				|| c >= 'A' && c <= 'Z';
	}
	
	/**
	 * Returns an array of Strings representing the {@link FigureRelationType}s
	 * that a user can pick from in the UI
	 * @return the array of Strings
	 */
	public static String[] getUserFigureRelationTypes() {
		return new String[] {
			"congruent", "parallel", "perpendicular", "bisects",
			"similar", "isosceles", "supplementary", "complementary", "right",
			"midpoint"
		};
	}
	
	/**
	 * Returns the {@link FigureRelationType} whose name is equal
	 * to the given String
	 */
	public static FigureRelationType toFigureRelationType(String str) {
		for (FigureRelationType type : FigureRelationType.values()) {
			if (type.toString().equals(str))
				return type;
		}
		return null;
	}
	
	/**
	 * Returns a formatted String version of the statement of the given {@link FigureRelation}
	 * @param rel the relation whose statement will be formatted
	 * @return the formatted String
	 */
	public static String formatFigureRelationStatement(FigureRelation rel) {
		FigureRelationType type = rel.getRelationType();
		
		if (type == CONGRUENT || type == PERPENDICULAR || type == PARALLEL || type == SIMILAR
				|| type == SUPPLEMENTARY || type == COMPLEMENTARY) {
			return rel.getFigure0() + " is " + type + " to " + rel.getFigure1();
		}
		else if (type == RIGHT) {
			return rel.getFigure0() + " is a right angle";
		}
		else if (type == ISOSCELES) {
			return rel.getFigure0() + " is an isosceles triangle";
		}
		else if (type == MIDPOINT) {
			return rel.getFigure0() + " is the midpoint of " + rel.getFigure1();
		}
				
		return rel.getStatement();
	}
	
	/**
	 * Get the two vertices in the given list that are the farthest apart from each
	 * other.
	 * @param vertices the list of vertices
	 * @return the two farthest vertices
	 */
	public static Vertex[] getFarthestVertices(List<Vertex> vertices) {
		if (vertices.size() < 2) {
			throw new IllegalArgumentException("List of vertices must have at least 2 elements");
		}
		if (vertices.size() == 2) {
			return vertices.toArray(new Vertex[2]);
		}
		// The pair of farthest vertices
		Vertex[] pair = new Vertex[2];
		// Distance of the previously checked pair of vertices
		float prevDist = 0f;
		for (int i = 0; i < vertices.size()-1; i++) {
			for (int j = i + 1; j < vertices.size(); j++) {
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
	
	/**
	 * Add the given vertex to the given list of vertices--the vertex will be placed in a position
	 * such that the distance between it and the first vertex in the list will be shorter than
	 * the distance between the first vertex and the next vertex in the list.
	 * @param vert the vertex to add
	 * @param vertices the list to add the vertex to
	 * @return the index at which the vertex was added, or -1 if the vertex is already contained
	 * in the list
	 */
	public static int addLeastToGreatestDist(Vertex vert, List<Vertex> vertices) {
		if (vertices.isEmpty()) {
			vertices.add(vert);
			return 0;
		}
		
		// No duplicates
		if (!vertices.contains(vert)) {
			// Location of the first vertex
			Vec2 startLoc = vertices.get(0).getCenter();
			// For each vertex (start at 2nd element because we will compare each vertex
			// to the first element which is an end-point)
			for (int i = 1; i < vertices.size(); i++) {
				Vertex v = vertices.get(i);
				// Measure the distance from first vertex to the vertex at i
				final float startToNextVertex = Vec2.dist(startLoc, v.getCenter());
				// Measure the distance from the first vertex to the given vertex
				final float startToNewVertex = Vec2.dist(startLoc, vert.getCenter());
				// If the distance to the given vertex is LESS than the distance to the
				// vertex at i, add the new vertex BEFORE the vertex at i
				if (startToNewVertex <= startToNextVertex) {
					vertices.add(i, vert);
					return i;
				}
			}
			// Add the vertex to the end of the list (will not happen if the for loop fires)
			vertices.add(vert);
		}
		return -1;
	}
	
	/**
	 * Get the name of the angle formed between two segments that share
	 * a vertex.
	 * @param a the first segment's name
	 * @param b the second segment's name
	 * @return the angle formed, or null if the segments do not share a vertex
	 */
	public static String getAngleBetween(String seg0, String seg1) {
		// Get shared vertex between segments
		char shared, unshared0, unshared1; // 1 shared, 2 unshared
		final int index = seg1.indexOf(seg0.charAt(0));
		if (index >= 0) {
			shared = seg1.charAt(index);
			unshared0 = seg0.charAt(1);
			unshared1 = seg1.charAt(index == 0 ? 1 : 0);
		} else {
			unshared0 = seg0.charAt(0);
			shared = seg0.charAt(1);
			final int sharedCharIndex = seg1.indexOf(shared);
			// If there is no shared vertex
			if (sharedCharIndex < 0) {
				// Throw exception
				throw new IllegalArgumentException("The given 2 Segments do not share"
						+ " a common vertex.");
			}
			unshared1 = seg1.charAt(sharedCharIndex == 0 ? 1 : 0);
		}
		
		String angleName = 
				String.valueOf(unshared0) + String.valueOf(shared) + String.valueOf(unshared1);
		return angleName;
	}
	
	/**
	 * Get the angle formed between the two given segments
	 * @param a the first segment
	 * @param b the second segment
	 * @return the angle
	 */
	public static Angle getAngleBetween(Segment a, Segment b) {
		String name = getAngleBetween(a.getName(), b.getName());
		if (name == null)
			throw new NullPointerException();
		
		Vertex shared = (Vertex)a.getChild(name.substring(1, 2));
		Vertex unshared0, unshared1;
		String unshared0Name = name.substring(0, 1);
		String unshared1Name = name.substring(2);
		
		if (a.getChild(unshared0Name) == null) {
			unshared0 = (Vertex)b.getChild(unshared0Name);
			unshared1 = (Vertex)a.getChild(unshared1Name);
		} else {
			unshared1 = (Vertex)b.getChild(unshared1Name);
			unshared0 = (Vertex)a.getChild(unshared0Name);
		}
		return new Angle(Arrays.asList(unshared0, shared, unshared1));
	}
	
	/**
	 * Get the vertex shared between the given two segments.
	 * Example of valid parameters: "AB", "BC"
	 * @param seg0 the first segment
	 * @param seg1 the second segment
	 * @return the shared vertex
	 */
	public static String getSharedVertex(String seg0, String seg1) {
		if (Utils.containsAllChars(seg0, seg1))
			return null;
		for (int i = 0; i < seg0.length(); i++) {
			final int sharedCharIndex = seg1.indexOf(seg0.charAt(i));
			if (sharedCharIndex >= 0)
				return String.valueOf(seg1.charAt(sharedCharIndex));
		}
		return null;
	}
	
	/**
	 * Get the vertex shared between the given two segments.
	 * Example of valid parameters: "AB", "BC"
	 * @param seg0 the first segment
	 * @param seg1 the second segment
	 * @return the shared vertex
	 */
	public static Vertex getSharedVertex(Segment seg0, Segment seg1) {
		String shared = getSharedVertex(seg0.getName(), seg1.getName());
		if (shared != null) {
			return seg0.getVertex(shared.charAt(0));
		}
		return null;
	}
	
	/**
	 * Get the compound segment (a segment formed by two other segments) of the
	 * two given segments. THE SEGMENTS MUST SHARE A VERTEX AND HAVE THE SAME SLOPE.
	 * @param seg0 the first segment
	 * @param seg1 the second segment
	 * @return the compound segment, or null if the segments do not share one common vertex
	 * or do not have the same slope
	 */
	public static Segment getCompoundSegment(Segment seg0, Segment seg1) {
		// Must form a straight line and share ONE vertex
		if (!seg0.getSlope().equals(seg1.getSlope()) 
				|| getSharedVertex(seg0.getName(), seg1.getName()) == null)
			return null;
		
		// Vertices of both segments in one list
		List<Vertex> segVerts = new ArrayList<>(Arrays.asList(seg0.getVertices()));
		segVerts.addAll(Arrays.asList(seg1.getVertices()));
		// ---------------------
		// Vertices of new segment--farthest apart
		Vertex[] newSegVerts = ProofUtils.getFarthestVertices(segVerts);
		return new Segment(newSegVerts);
	}
	
	/**
	 * Get the opposite vertex of the given vertex in the given segment.
	 * @param seg the segment
	 * @param vert the vertex whose opposite will be returned
	 * @return the opposite vertex of the given vertex
	 */
	public static String getOtherVertex(String seg, String vert) {
		return seg.charAt(0) == vert.charAt(0) ? seg.substring(1) : seg.substring(0, 1);
	}
	
	/**
	 * Get the angles in the given triangle adjacent to the
	 * given segment. 
	 * @param tri the triangle
	 * @param seg the segment
	 * @return the surrounding angles
	 */
	public static String[] getSurroundingAngles(String tri, String seg) {			
		return new String[] {
				getFullNameOfAngle(tri, seg.substring(0, 1)),
				getFullNameOfAngle(tri, seg.substring(1))
		};
		
//		if (arrayContainsAll(triChars, segChars)) {
//				throw new IllegalArgumentException("The given Segment is "
//						+ "not a part of the given Triangle");
//		}	
	}
	
	/**
	 * For the given vertex, get the full name   of the angle at the vertex
	 * in the given triangle.
	 * @param tri the triangle
	 * @param angleShortName the vertex
	 * @return the full name of the angle at the given vertex
	 */
	public static String getFullNameOfAngle(String tri, String angleShortName) {
		if (angleShortName.length() != 1 || !Triangle.isValidTriangleName(tri))
			return null;
		StringBuilder name = new StringBuilder();
		for (char c : tri.toCharArray()) {
			if (c != angleShortName.charAt(0))
				name.append(c);
		}
		name.insert(1, angleShortName);
		return name.toString();
	}
	
	/**
	 * Get whether the given two angles are vertical angles.
	 * @param a the first angle
	 * @param b the second angle
	 * @return whether they are vertical angles
	 */
	public static boolean areVerticalAngles(Angle a, Angle b) {		
		// Check if the two angles share ONE common vertex
		String endVerts0 = a.getName().substring(0, 1) + a.getName().substring(2);
		String endVerts1 = b.getName().substring(0, 1) + b.getName().substring(2);
		
		final boolean shareVert = 
				// Middle vertices are equal
				a.getNameShort().equals(b.getNameShort()) 
				// ONLY share the middle vertex, no other vertices shared
				&& !(endVerts0.contains(endVerts1.substring(0, 1)) 
						|| endVerts0.contains(endVerts1.substring(1)));
		
		// Get corresponding segments, check if they're aligned
		Segment[][] corrSegs = getCorrespondingSegments(a, b);
		final boolean segsAligned = 
				// a0s0 == a1s0
				corrSegs[0][0].getSlope().equals(corrSegs[0][1].getSlope())
				// a0s1 == a1s1
			&&	corrSegs[1][0].getSlope().equals(corrSegs[1][1].getSlope());
				
		return shareVert && segsAligned;
	}
	
	/**
	 * Compare the two Angles to see if they are synonyms.
	 * @param a the first {@link Angle}
	 * @param b the second {@link Angle}
	 * @return 0 if a = b, 1 if a > b, -1 if a < b, -2 if angles don't share a vertex, -3 if angles
	 * share a vertex but are not aligned.
	 */
	public static int compareAngleSynonyms(Angle a, Angle b) {
		// Check if the angles share a center vertex
		final boolean shareVertex = a.getCenter().equals(b.getCenter());
		if (shareVertex) {
			Segment[][] corrSegs = getCorrespondingSegments(a, b);
			Segment a0s0 = corrSegs[0][0];
			Segment a1s0 = corrSegs[0][1];
			Segment a0s1 = corrSegs[1][0];
			Segment a1s1 = corrSegs[1][1];			
			
			// Get end points of each segment
			List<Vec2> a0s0Points = Arrays.asList(a0s0.getVertexLocations());
			List<Vec2> a0s1Points = Arrays.asList(a0s1.getVertexLocations());
			List<Vec2> a1s0Points = Arrays.asList(a1s0.getVertexLocations());
			List<Vec2> a1s1Points = Arrays.asList(a1s1.getVertexLocations());
			
			// Angles are equal--on top of each other perfectly
			if (
					// The first pair of segments are equal
					a0s0.containsPoints(a1s0Points) && a1s0.containsPoints(a0s0Points)
					// The second pair of segments are equal
				&&  a0s1.containsPoints(a1s1Points) && a1s1.containsPoints(a0s1Points)	
			) {
				return 0;
			}
			
			// Angle b is on top of Angle a, Angle a is bigger
			if (a0s0.containsPoints(a1s0Points) && a0s1.containsPoints(a1s1Points)) {
				return 1;
			}
			
			// Angle a is on top of Angle b, Angle b is bigger
			if (a1s0.containsPoints(a0s0Points) && a1s1.containsPoints(a0s1Points)) {
				return -1;
			}
			
			// Angles are not aligned
			return -3;
		} else {
			// Angles don't share a vertex, not synonyms
			return -2;
		}
	}
	
	/**
	 * Get the pairs of corresponding angles between the two given
	 * {@link Triangle}s.
	 * @param tri0 the first triangle
	 * @param tri1 the second triangle
	 * @return the pairs of corresponding angles
	 */
	public static List<Angle[]> getCorrespondingAngles(Triangle tri0, Triangle tri1) {
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
	
	/**
	 * Get the pairs of corresponding segments between the two given
	 * {@link Triangle}s.
	 * @param tri0 the first triangle
	 * @param tri1 the second triangle
	 * @return the pairs of corresponding segments
	 */
	public static List<Segment[]> getCorrespondingSegments(
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
				if (a0.getLength() == a1.getLength()) {
					list.add(new Segment[] { a0, a1 });
					usedSegments.add(i); // Record used segment
					continue outer;
				}
			}
		}
		
		return list;
	}
	
	/**
	 * Get the corresponding segments of the given two angles <i>that share a
	 * common vertex</i>
	 * <p>
	 * Format: [ [ a0s0, a1s0 ] , [ a0s1, a1s1 ]  ]
	 * @param a the first angle
	 * @param b the second angle
	 * @return the list of corresponding segments if the angles share a common vertex, null
	 * if the angles do not share a common vertex, empty if the angles are not aligned.
	 */
	public static Segment[][] getCorrespondingSegments(Angle a, Angle b) {
		Segment a0s0 = a.getSides()[0];
		Segment a0s1 = a.getSides()[1];
		Segment a1s0 = b.getSides()[0];
		Segment a1s1 = b.getSides()[1];
		// Make sure the segments in the 2 pairs are parallel to each other
		a1s0 = a1s0.getSlope().equals(a0s0.getSlope()) ? a1s0 : b.getSides()[1];
		a1s1 = a1s1.getSlope().equals(a0s1.getSlope()) ? a1s1 : b.getSides()[0];
		
		return new Segment[][] { { a0s0, a1s0 }, { a0s1, a1s1 } };
	}
	
	/**
	 * Get the name of the vertex opposite to the given segment.
	 * For instance, in triangle "ABC", the vertex opposite of the
	 * segment "AB" will be "C".
	 * @param tri the triangle
	 * @param seg the segment
	 * @return the vertex opposite to the given segment in the given
	 * triangle
	 */
	public static String getOppositeVertex(String tri, String seg) {
		for (char c : tri.toCharArray()) {
			String vertex = String.valueOf(c);
			if (!seg.contains(vertex)) {
				return vertex;
			}
		}
		return null;
	}
	
	/**
	 * Get the {@link Arc} formed in the given list of {@link Vertex}es.
	 * @param verts the list of vertices from which the {@link Arc} will
	 * be derived.
	 * @param arcSize the size of the {@link Arc}
	 * @return the {@link Arc}
	 */
	public static Arc getArc(List<Vertex> verts, Dimension arcSize) {
		// Ensure size of array is 3
		if (verts.size() != 3) {
			throw new IllegalArgumentException("Length of list of vertices must = 3!!!");
		}
		Vec2 otherVert0 = verts.get(0).getCenter();
		Vec2 otherVert1 = verts.get(2).getCenter();
		Vec2 vertex = verts.get(1).getCenter();
		
		// Get the headings of both of the segments
		float arcVert0Heading = Vec2.sub(otherVert0, vertex).getHeading();
		float arcVert1Heading = Vec2.sub(otherVert1, vertex).getHeading();
		
		/*
		 * We can't just use the startHeading as it is for the start angle of the arc.
		 * This is because Vec2.getHeading() returns an angle on the following scale:
		 * 			 -PI/2
		 * 		 PI		     0
		 * 			  PI/2
		 * However, the Arc class uses a different scale:
		 * 			 1.5 PI
		 * 		PI			 0
		 * 			  PI/2
		 * We have to account for this.
		 */

		arcVert0Heading = arcVert0Heading < 0f ? Utils.TWO_PI + arcVert0Heading 
				: arcVert0Heading;		
		arcVert1Heading = arcVert1Heading < 0f ? Utils.TWO_PI + arcVert1Heading 
				: arcVert1Heading;
		
		/*
		 * Decide which segment heading will be the starting angle
		 */
		
		float startAngle = Math.min(arcVert0Heading, arcVert1Heading);
		float endAngle = Math.max(arcVert0Heading, arcVert1Heading);
				
		if (endAngle - startAngle > Utils.PI) {
			final float temp = startAngle;
			startAngle = endAngle;
			endAngle = temp == 0f ? Utils.TWO_PI : temp; // Replace angle 0 with angle 360
		}
				
		// Create the arc
		Arc arc = new Arc(vertex, arcSize, startAngle, endAngle);
		return arc;
	}
	
	/**
	 * Get whether the given point lies within the given arc, within <code>maxDist</code>.
	 * Alternatively, one can input -1 for <code>maxDist</code> indicating that distance
	 * from the arc does not matter.
	 * @param a the arc
	 * @param point the point
	 * @param maxDist the maximum distance away from the center of the arc that the point can
	 * be to be considered within the arc
	 * @return whether the given point lies within the arc within <code>maxDist</code>
	 */
	public static boolean arcContainsPoint(Arc a, Vec2 point, float maxDist) {
		// Center of arc
		Vec2 center = a.getCenter();
		// Vector FROM center of arc TO point
		Vec2 pointFromCenter = Vec2.sub(point, center);
		
		// DETERMINE THE HEADING OF THE MOUSE
		// Raw heading of pointFromCenter vector (raw = directly from getHeading() method)
		final float pointHeadingRaw = pointFromCenter.getHeading();
		// Convert the raw heading to angle-scale that the arc uses
		final float pointHeadingCorrected = pointHeadingRaw < 0f ?
				Utils.TWO_PI + pointHeadingRaw : pointHeadingRaw;
		
		// Minimum and maximum boundaries of the arc, within which the mouse must be
	
		// If the start angle is greater than the stop angle, we need to convert
		// the start angle to its raw value (a negative value)
		final float min = a.getStartAngle() >= a.getStopAngle() ? a.getStartAngle()-Utils.TWO_PI 
				: a.getStartAngle();
		final float max = a.getStopAngle();
		
		// Choose which heading to use--the corrected version or the raw version
		final float finalHeading = a.getStartAngle() >= a.getStopAngle() ? 
				pointHeadingRaw : pointHeadingCorrected;
		
		// Get if point is within radius of arc (user can make maxDist -1 to signify that
		// distance does not matter
		final boolean pointCloseEnough = (maxDist < 0) || (pointFromCenter.getMag() <= maxDist);
	
		/*
		 * Vector is <= arc radius  AND  startAngle <= heading <= stopAngle
		 */
		return pointCloseEnough && finalHeading >= min && finalHeading <= max;

	}
	
	/**
	 * Get the {@link Arc} formed in the given {@link Angle}.
	 * <p>
	 * The size of the arc is the length of the shorter side of the
	 * given {@link Angle}
	 * @param angle the {@link Angle} from which the {@link Arc}
	 * will be derived.
	 * @return the {@link Arc}
	 */
	public static Arc getArc(List<Vertex> verts) {
		// Middle vertex
		Vertex middle = verts.get(1);
		// A side of the angle
		Segment s0 = new Segment(middle, verts.get(0));
		// A side of the angle
		Segment s1 = new Segment(middle, verts.get(2));
		// Get lengths of sides
		final float s0Len = s0.getLength();
		final float s1Len = s1.getLength();
		// Arc size is length of shorter side of angle
		Dimension arcSize = new Dimension(Math.min(s0Len, s1Len));
		return getArc(verts, arcSize);
	}
	
	/**
	 * Get the {@link Arc} formed in the given {@link Angle}.
	 * @param angle the {@link Angle} from which the {@link Arc}
	 * will be derived.
	 * @param arcSize the size of the {@link Arc}
	 * @return the {@link Arc}
	 */
	public static Arc getArc(Angle angle, Dimension arcSize) {
		return getArc(angle.getVertices(), arcSize);
	}
	
	/**
	 * Get the {@link Arc} formed in the given {@link Angle}.
	 * <p>
	 * The size of the arc is the length of the shorter side of the
	 * given {@link Angle}
	 * @param angle the {@link Angle} from which the {@link Arc}
	 * will be derived.
	 * @return the {@link Arc}
	 */
	public static Arc getArc(Angle angle) {
		// Vertices of angles
		return getArc(angle.getVertices());
	}
	
	/**
	 * Get the {@link Arc} formed between two intersecting {@link Segment}s.
	 * @param a the first segment
	 * @param b the second segment
	 * @param arcSize the size of the arc (the width and height)
	 * @return the {@link Arc}
	 */
	public static Arc getArc(Segment a, Segment b, Dimension arcSize) {
		List<Vertex> verts = getAngleBetween(a, b).getVertices();
		return getArc(verts, arcSize);
	}

}
