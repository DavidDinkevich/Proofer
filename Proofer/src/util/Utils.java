package util;

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

/**
 * Utility class.
 * @author David Dinkevich
 */
public final class Utils {
	public static final String DELTA = "Δ";
	public static final String ANGLE_SYMBOL = "<";
	public static final String GEOMETRY_CHARS = DELTA + ANGLE_SYMBOL;
	public static final float PI = (float)Math.PI;
	public static final float TWO_PI = (float)(Math.PI*2D);

	// Don't instantiate an object of this class, or it'll bite you back...
	private Utils() {
		throw new AssertionError("Do NOT instantiate an object of this class!");
	}
	
	public static float degreesToRadians(float degrees) {
		return degrees * (PI/180.0f);
	}
	
	public static float radiansToDegrees(float radians) {
		return radians * (180.0f/PI);
	}

	public static int max(int[] list) {
		if (list.length == 0) {
			throw new IllegalArgumentException("Given list must not be empty!");
		}
		int min = list[0];
		for (int i = 1; i < list.length; i++) {
			if (list[i] < min)
				min = list[i];
		}
		return min;
	}
	
	public static float max(float[] list) {
		if (list.length == 0) {
			throw new IllegalArgumentException("Given list must not be empty!");
		}
		float max = list[0];
		for (int i = 1; i < list.length; i++) {
			if (list[i] > max)
				max = list[i];
		}
		return max;
	}
	
	public static int min(int[] list) {
		if (list.length == 0) {
			throw new IllegalArgumentException("Given list must not be empty!");
		}
		int min = list[0];
		for (int i = 1; i < list.length; i++) {
			if (list[i] < min)
				min = list[i];
		}
		return min;
	}
	
	public static float min(float[] list) {
		if (list.length == 0) {
			throw new IllegalArgumentException("Given list must not be empty!");
		}
		float min = list[0];
		for (int i = 1; i < list.length; i++) {
			if (list[i] < min)
				min = list[i];
		}
		return min;
	}
	
	/**
	 * Get whether the given string (first string param) contains all
	 * of the chars in the second string parameter.
	 */
	public static boolean containsAllChars(String string0, String chars) {
		if (string0.length() != chars.length())
			return false;
		boolean containsAllChars = true;
		for (int i = 0; i < string0.length(); i++) {
			if (string0.indexOf(chars.charAt(i)) > -1)
				continue;
			containsAllChars = false;
			break;
		}
		return containsAllChars;
	}
		
	/**
	 * Get whether the given array contains the given element
	 * @param array the array
	 * @param element the element
	 * @return whether or not the array contains the element
	 */
	public static <T> boolean arrayContains(T[] array, T element) {
		for (T el : array) {
			if (el.equals(element))
				return true;
		}
		return false;
	}
	
	/**
	 * Get whether the given array contains all of the elements in the second
	 * array
	 * @param array the first array
	 * @param array2 the second array
	 * @return whether the given array contains all of the elements in the second
	 * array
	 */
	public static <T> boolean arrayContainsAll(T[] array, T[] array2) {
		int count = 0;
		outer:
		for (T el : array2) {
			for (T el2 : array) {
				if (el.equals(el2)) {
					++count;
					continue outer;
				}
			}
		}
		return count == array2.length;
	}
	
	public static String mergeStringsAndEnsureCapacity(
			int minLength, int maxLength, String original, String newStr) {
		final boolean tooShort = newStr.length() < minLength && minLength > 0;
		final boolean tooLong = newStr.length() > maxLength && maxLength > 0;
		// (Above) if the maxLength is -1, the length of the string can be unlimited
		
		if (!tooShort && !tooLong) {
			return newStr;
		}
		else if (tooLong) {
			return newStr.substring(0, maxLength);
		} else { // Given string is too short
			StringBuilder sb = new StringBuilder(newStr);
			sb.setLength(minLength);
			for (int i = newStr.length(); i < minLength; i++) {
				sb.setCharAt(i, i < original.length() ? original.charAt(i) : '\0');
			}
			return sb.toString();
		}
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
	 * Get a list of {@link Vertex}es that form the angle
	 * between the two given segments
	 * @param a the first segment
	 * @param b the second segment
	 * @return the list of vertices that compose the angle
	 */
	public static List<Vertex> getAngleBetween(Segment a, Segment b) {
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
		return Arrays.asList(unshared0, shared, unshared1);
	}
	
	/**
	 * Get the vertex shared between the given two segments.
	 * Example of valid parameters: "AB", "BC"
	 * @param seg0 the first segment
	 * @param seg1 the second segment
	 * @return the shared vertex
	 */
	public static String getSharedVertex(String seg0, String seg1) {
		if (containsAllChars(seg0, seg1))
			return null;
		for (int i = 0; i < seg0.length(); i++) {
			final int sharedCharIndex = seg1.indexOf(seg0.charAt(i));
			if (sharedCharIndex >= 0)
				return String.valueOf(seg1.charAt(sharedCharIndex));
		}
		return null;
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
		String name0 = a.getName();
		String name1 = b.getName();
		int sharedVertCount = 0;
		if (a.getNameShort().equals(b.getNameShort())) {
			for (int i = 0; i < name0.length(); i++) {
				// If the name of the first angle does not contain the char at the given
				// index in the second angle name.
				if (!(name0.indexOf(name1.charAt(i)) > -1))
					continue;
				++sharedVertCount;
			}
			return sharedVertCount == 1 && a.getAngle() == b.getAngle();
		}
		return false;
	}
	
	/**
	 * Get the pairs of corresponding angles between the two given
	 * {@link Triangle}s.
	 * @param tri0 the first triangle
	 * @param tri1 the second triangle
	 * @return the pairs of corresponding angles
	 */
	public static List<Angle[]> getCorrespondingAngles(
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
				if (a0.getLength(false) == a1.getLength(false)) {
					list.add(new Segment[] { a0, a1 });
					usedSegments.add(i); // Record used segment
					continue outer;
				}
			}
		}
		
		return list;
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
		Vec2 otherVert0 = verts.get(0).getCenter(true);
		Vec2 otherVert1 = verts.get(2).getCenter(true);
		Vec2 vertex = verts.get(1).getCenter(true);
		
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
				
		if (endAngle - startAngle > PI) {
			final float temp = startAngle;
			startAngle = endAngle;
			endAngle = temp == 0f ? TWO_PI : temp; // Replace angle 0 with angle 360
		}
				
		// Create the arc
		Arc arc = new Arc(vertex, arcSize, startAngle, endAngle);
		return arc;
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
		final float s0Len = s0.getLength(true);
		final float s1Len = s1.getLength(true);
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
		List<Vertex> verts = getAngleBetween(a, b);
		return getArc(verts, arcSize);
	}
}
