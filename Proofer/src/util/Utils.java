package util;

import geometry.shapes.Segment;

/**
 * Utility class.
 * @author David Dinkevich
 */
public final class Utils {
	public static final String DELTA = "Δ";
	public static final String ANGLE_SYMBOL = "<";
	public static final String GEOMETRY_CHARS = DELTA + ANGLE_SYMBOL;

	// Don't instantiate an object of this class, or it'll bite you back...
	private Utils() {
		throw new AssertionError("Do NOT instantiate an object of this class!");
	}
	
	public static float degreesToRadians(float degrees) {
		final float PI = (float)Math.PI;
		return degrees * (PI/180.0f);
	}
	
	public static float radiansToDegrees(float radians) {
		final float PI = (float)Math.PI;
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
	 * @param a the first segment
	 * @param b the second segment
	 * @return the angle formed, or null if the segments do not share a vertex
	 */
	public static String getAngleBetween(Segment a, Segment b) {
		String seg0 = a.getName();
		String seg1 = b.getName();
		
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
				// Return null
				return null;
			}
			unshared1 = seg1.charAt(sharedCharIndex == 0 ? 1 : 0);
		}
		
		String angleName = 
				String.valueOf(unshared0) + String.valueOf(shared) + String.valueOf(unshared1);
		return angleName;
	}
}
