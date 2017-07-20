package geometry.shapes;

import java.util.Arrays;
import java.util.List;

import geometry.Vec2;

public class Triangle extends SimplePolygon {
	private static String formatName(String name) {
		StringBuilder sb = new StringBuilder(name);
		sb.setLength(3);
		return sb.toString();
	}
	
	public Triangle(String name) {
		super(formatName(name));
		init();
	}
	
	public Triangle(List<Vertex> vertices) {
		super(vertices);
		init();
	}
	
	public Triangle() {
		super(3);
		init();
	}
	
	public Triangle(Triangle other) {
		super(other);
	}
	
	private void init() {
		setNameLengthRange(3, 3, false);
	}
	
	/**
	 * Get whether the given {@link Rect} and {@link Triangle} overlap.
	 * @param rect the rect
	 * @param tri the triangle
	 * @param incorporateScale whether or not to include the shapes'
	 * scales in the equation
	 * @return whether or not the shapes overlap
	 */
	public static boolean overlap(Rect rect, Triangle tri, boolean incorporateScale) {
		/*
		 * Start by checking if triangle contains rect's vertices because
		 * Rect's containsPoint() method is faster than that of Triangle
		 */
		for (int i = 0; i < 4; i++) {
			Vec2 vLoc = rect.getVertexLoc(i, incorporateScale);
			if (tri.containsPoint(vLoc, incorporateScale)) {
				return true;
			}
		}
		// Check if rect contains triangle's vertices
		for (Vec2 loc : tri.getVertexLocations()) {
			if (rect.containsPoint(loc, incorporateScale)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o) && o instanceof Triangle;
	}

	@Override
	public float getArea() {
		final float x1 = getVertices().get(0).getScaledCenter().getX(),
					y1 = getVertices().get(0).getScaledCenter().getY(),
					x2 = getVertices().get(1).getScaledCenter().getX(),
					y2 = getVertices().get(1).getScaledCenter().getY(),
					x3 = getVertices().get(2).getScaledCenter().getX(),
					y3 = getVertices().get(2).getScaledCenter().getY();
		return Math.abs((x1*(y2-y3) + x2*(y3-y1) + x3*(y1-y2))/2.0f);
	}

	@Override
	public boolean containsPoint(Vec2 point, boolean incorporateScale) {
		Vertex pVertex = new Vertex(point);
		
		/* Calculate area of triangle PBC */
		Vertex[] va1 = { pVertex, getVertices().get(1), getVertices().get(2) };
		final float A1 = new Triangle(Arrays.asList(va1)).getArea();

		/* Calculate area of triangle PAC */
		Vertex[] va2 = { getVertices().get(0), pVertex, getVertices().get(2) };
		final float A2 = new Triangle(Arrays.asList(va2)).getArea();

		/* Calculate area of triangle PAB */
		Vertex[] va3 = { getVertices().get(0), getVertices().get(1), pVertex };
		final float A3 = new Triangle(Arrays.asList(va3)).getArea();

		// Use ints to minimize error
		
		/* Get the sum of all individual triangles */
		final int SUM = Math.round(A1 + A2 + A3);
		/* Calculate area of triangle ABC */
		final int TOTAL_AREA = Math.round(getArea());
		
		return TOTAL_AREA == SUM;
	}
	
	@Override
	public final int getVertexCount() {
		return 3; // Triangles have 3 corners :-)
	}
	
	@Override
	public Vec2 getCenter() {
		Vec2 middle = new Vec2(
			(getVertices().get(0).getCenter().getX() + 
					getVertices().get(1).getCenter().getX() + 
					getVertices().get(2).getCenter().getX())/3f,
			(getVertices().get(0).getCenter().getY() + 
					getVertices().get(1).getCenter().getY() + 
					getVertices().get(2).getCenter().getY())/3f
		);
		super.setCenter(middle);
		return middle;
	}
	
	@Override
	public void setCenter(Vec2 newLoc) {
		if (newLoc.equals(getCenter()))
			return;
		
		Vec2 diff = Vec2.sub(newLoc, getCenter());
		for (int i = 0; i < getVertices().size(); i++) {
			Vec2 p = getVertices().get(i).getCenter();
			getVertices().get(i).setCenter(Vec2.add(p, diff));
		}
		super.setCenter(newLoc);
	}
	
	@Override
	public void setScale(Vec2 scale, Vec2 dilationPoint) {
		if (scale.equals(getScale()) || dilationPoint.equals(getDilationPoint())) {
			return;
		}
		super.setScale(scale, dilationPoint);
		
		for (Vertex v : getVertices()) {
			v.setScale(getScale(), dilationPoint);
		}		
	}
	
	@Override
	public boolean isValidName(String name) {
		if (name.length() != 3)
			return false;
		boolean contains = true;
		for (int i = 0; i < name.length(); i++) {
			if (getName().indexOf(name.charAt(i)) > -1)
				continue;
			contains = false;
			break;
		}
		return contains;
	}
}
