package util;

import geometry.Vec2;

public final class Utils {
	
	public static final float PI = (float)Math.PI;
	public static final float TWO_PI = (float)(Math.PI*2D);
	
	// Don't instantiate an object of this class, or it'll bite you back...
	private Utils() {
		throw new AssertionError("Do NOT instantiate an object of this class!");
	}
	
	public static float map(float val, float min0, float max0, float min1, 
			float max1) {
		throw new AssertionError("not implemented yet.");
	}
	
	public static float constrain(float val, float min, float max) {
		return val < min ? min : val > max ? max : val;
	}
	
	public static float degreesToRadians(float degrees) {
		return degrees * (PI/180.0f);
	}
	
	public static float radiansToDegrees(float radians) {
		return radians * (180.0f/PI);
	}
		
	public static float dist(Vec2 p1, Vec2 p2) {
		final float xdiff = (p2.getX() - p1.getX()) * (p2.getX() - p1.getX());
		final float ydiff = (p2.getY() - p1.getY()) * (p2.getY() - p1.getY());
		return (float) Math.sqrt(xdiff + ydiff);
	}
	
	public static float round(float num, int decimalPlaces) {
		// decimalPlaces has to be > 0
		if (decimalPlaces < 0) {
			throw new IllegalArgumentException("decimalPlaces has to be > 0");
		}
		
		if (decimalPlaces > 7) {
			throw new IllegalArgumentException(decimalPlaces + " decimal "
					+ "places is too precise for a Float");
		}
		
		final float placesMultiplier = (float) Math.pow(10f, decimalPlaces);
		
		// Round to nearest hundredth
		return Math.round(num * placesMultiplier) / placesMultiplier;
		
	}
	
	public static int max(int[] list) {
		if (list.length == 0) {
			throw new IllegalArgumentException("Given list must not be empty!");
		}
		int max = list[0];
		for (int i = 1; i < list.length; i++) {
			if (list[i] > max)
				max = list[i];
		}
		return max;
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
	
	/**
	 * Replaces the original String with the new String, under the following
	 * conditions: <p>
	 * 1) If the length of newStr is greater than maxLength, then newStr will
	 * be chopped when necessary.
	 * <p>
	 * 2) If the length of newStr is less than minLength, then the missing
	 * characters will be filled by the corresponding characters in oldStr.
	 * For example, if oldStr = "hello", newStr = "xy", minLength = 5,
	 * maxLength = 5, then the result would be "xyllo".
	 * @param minLength minimum length of the new String
	 * @param maxLength maximum length of the new String
	 * @param original the String to be replaced
	 * @param newStr the String that will replace the old String
	 * @return newStr, subject to modification if it is too short or too long
	 */
	// TODO: rename to: replaceStringEnsureCapacity
	public static String mergeStringsAndEnsureCapacity(
			int minLength, int maxLength, String original, String newStr) {
		// TODO: minLength should always be > 0
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
}
