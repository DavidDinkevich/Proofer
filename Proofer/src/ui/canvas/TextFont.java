package ui.canvas;

import processing.core.PConstants;

public class TextFont {
	private int size;
	private int alignmentX, alignmentY;
	
	public TextFont(int size) {
		this.size = size;
		alignmentX = PConstants.CENTER;
		alignmentY = PConstants.CENTER;
	}
	public TextFont() {
		this(10);
	}
	public TextFont(TextFont other) {
		size = other.size;
		alignmentX = other.alignmentX;
		alignmentY = other.alignmentY;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof TextFont))
			return false;
		TextFont tf = (TextFont)o;
		return tf.size == size && tf.alignmentX == alignmentX
				&& tf.alignmentY == alignmentY;
	}
	
	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + size;
		result = 31 * result + alignmentX;
		result = 31 * result + alignmentY;
		return result;
	}
	
	public int getAlignmentX() {
		return alignmentX;
	}
	public int getAlignmentY() {
		return alignmentY;
	}
	public int getSize() {
		return size;
	}
}
