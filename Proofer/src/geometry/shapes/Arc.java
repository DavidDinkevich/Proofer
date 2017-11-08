package geometry.shapes;

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
		// Possibility: compare attributes of arc
	}
	
	@Override
	public boolean containsPoint(Vec2 point, boolean includeScale) {
		// Center of arc
		Vec2 center = getCenter(includeScale);
		// Vector FROM center of arc TO point
		Vec2 pointFromCenter = Vec2.sub(point, center);
		// Raw heading of pointFromCenter vector (raw = directly from getHeading() method)
		final float pointHeadingRaw = pointFromCenter.getHeading();
		// Convert the raw heading to angle-scale that the arc uses
		final float pointHeadingCorrected = pointHeadingRaw < 0f ?
				Utils.TWO_PI + pointHeadingRaw : pointHeadingRaw;
		/*
		 * Vector is < arc radius  AND  startAngle ≤ heading ≤ stopAngle
		 */
		return pointHeadingCorrected > startAngle && pointHeadingRaw <= stopAngle
				&& Vec2.dist(point, center) <= size.getWidth()/2f;
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
