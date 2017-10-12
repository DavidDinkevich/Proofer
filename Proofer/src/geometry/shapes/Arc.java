package geometry.shapes;

import java.util.List;

import geometry.Dimension;
import geometry.Vec2;

import processing.core.PConstants;

import util.Utils;

public class Arc extends Vertex {
	private Dimension.Mutable size;
	private float startAngle;
	private float stopAngle;
	private int mode;
		
	public Arc(Vec2 loc, Dimension size, float startAngle, float stopAngle, int mode) {
		super(loc);
		this.size = new Dimension.Mutable(size);
		this.startAngle = startAngle;
		this.stopAngle = stopAngle;
		this.mode = mode;
	}
	
	public Arc(Vec2 loc, Dimension size, float startAngle, float endAngle) {
		this(loc, size, startAngle, endAngle, PConstants.PIE);
	}

	public Arc(char name) {
		this(Vec2.ZERO, Dimension.ONE, 0f, 0f, PConstants.PIE);
	}

	public Arc() {
		this('\0');
	}
	
	public Arc(Arc other) {
		super(other);
		size.set(other.size);
		startAngle = other.startAngle;
		stopAngle = other.stopAngle;
		mode = other.mode;
	}

	public Dimension getSize() {
		return size;
	}

	public void setSize(Dimension size) {
		this.size.set(size);
	}

	/**
	 * Get whether the given string is a valid name for an {@link Arc}.
	 * @param name the name to be checked
	 * @return whether it is a valid name for an {@link Arc}.
	 */
	public static boolean isValidArcName(String name) {
		return name.length() == 1;
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o) && o instanceof Arc;
	}
	
	/**
	 * Get the {@link Arc} formed between two intersecting {@link Segment}s.
	 * @param a the first segment
	 * @param b the second segment
	 * @param arcSize the size of the arc (the width and height)
	 * @return the {@link Arc}
	 */
	public static Arc getArcBetween(Segment a, Segment b, float arcSize) {
		List<Vertex> angle = Utils.getAngleBetween(a, b);
		Vec2 otherVert0 = angle.get(0).getCenter(true);
		Vec2 otherVert1 = angle.get(2).getCenter(true);
		Vec2 vertex = angle.get(1).getCenter(true);
		
		// Get the headings of both of the segments
		final float arcVert0Heading = Vec2.sub(otherVert0, vertex).getHeading();
		final float arcVert1Heading = Vec2.sub(otherVert1, vertex).getHeading();
		// Determine which segment's heading will be used for the arc's start angle
		float startHeading;
		if (arcVert0Heading > 0f) {
			startHeading = Math.abs(arcVert0Heading) < Math.abs(arcVert1Heading)
					? arcVert0Heading : arcVert1Heading;
		} else {
			startHeading = Math.abs(arcVert0Heading) > Math.abs(arcVert1Heading)
					? arcVert0Heading : arcVert1Heading;
		}
		
		/*
		 * We can't just use the startHeading as it is for the start angle of the arc.
		 * This is because Vec2.getHeading() returns an angle on the following scale:
		 * 			 -PI/2
		 * 		2PI		     0
		 * 			  PI/2
		 * However, the Arc class uses a different scale:
		 * 			 1.5 PI
		 * 		PI			 0
		 * 			  PI/2
		 * We have to account for this.
		 */
		final float startAngle = startHeading < 0f ? Utils.TWO_PI + startHeading 
				: startHeading;
		// Get the angle between the two segments
		final float angleBetween = Vec2.angleBetween(
				Vec2.sub(otherVert0, vertex), Vec2.sub(otherVert1, vertex));
		// The end angle
		final float endAngle = startAngle + angleBetween;
		
		// Create the arc
		return new Arc(vertex, new Dimension(arcSize), startAngle, endAngle);
	}

	public float getStartAngle() {
		return startAngle;
	}

	public void setStartAngle(float startAngle) {
		this.startAngle = startAngle;
	}

	public float getStopAngle() {
		return stopAngle;
	}

	public void setStopAngle(float stopAngle) {
		this.stopAngle = stopAngle;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
}
