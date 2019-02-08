package geometry.shapes;

import java.util.Arrays;
import java.util.List;

import geometry.Vec2;

import geometry.proofs.ProofUtils;


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
	
	public Triangle(Vertex vert0, Vertex vert1, Vertex vert2) {
		this(Arrays.asList(vert0, vert1, vert2));
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
	 * @return whether or not the shapes overlap
	 */
	public static boolean overlap(Rect rect, Triangle tri) {
		// TODO: check if any of the sides intersect
		/*
		 * Start by checking if triangle contains rect's vertices because
		 * Rect's containsPoint() method is faster than that of Triangle
		 */
		for (int i = 0; i < 4; i++) {
			Vec2 vLoc = rect.getVertexLoc(i);
			if (tri.containsPoint(vLoc)) {
				return true;
			}
		}
		// Check if rect contains triangle's vertices
		for (Vec2 loc : tri.getVertexLocations()) {
			if (rect.containsPoint(loc)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get whether the given String is a valid name for a Triangle
	 * @param name the name to be tested
	 * @return the result
	 */
	public static boolean isValidTriangleName(String name) {
		return name.length() == 3; // TODO: Should also not contain any duplicates
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o) && o instanceof Triangle;
	}
	
	@Override
	public String toString() {
		return ProofUtils.DELTA + getName();
	}

	@Override
	public float getArea() {
		final float x1 = getVertexLoc(0).getX(),
					y1 = getVertexLoc(0).getY(),
					x2 = getVertexLoc(1).getX(),
					y2 = getVertexLoc(1).getY(),
					x3 = getVertexLoc(2).getX(),
					y3 = getVertexLoc(2).getY();
		return Math.abs((x1*(y2-y3) + x2*(y3-y1) + x3*(y1-y2))/2.0f);
	}

	@Override
	public boolean containsPoint(Vec2 point) {
		Vertex pVertex = new Vertex(point);
		
		/* Calculate area of triangle PBC */
		Vertex[] va1 = { pVertex, getVertices()[1], getVertices()[2] };
		final float A1 = new Triangle(Arrays.asList(va1)).getArea();

		/* Calculate area of triangle PAC */
		Vertex[] va2 = { getVertices()[0], pVertex, getVertices()[2] };
		final float A2 = new Triangle(Arrays.asList(va2)).getArea();

		/* Calculate area of triangle PAB */
		Vertex[] va3 = { getVertices()[0], getVertices()[1], pVertex };
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
	
	/**
	 * Get the segments adjacent to the given vertex.
	 * @param vert the vertex
	 * @return the 2 segments, or null if the given vertex is not contained
	 * in this triangle.
	 */
	public Segment[] getAdjacentSegments(String vert) {
		final int indexOfVert = getName().indexOf(vert);
		if (indexOfVert < 0)
			return null;
		StringBuilder builder = new StringBuilder(getName());
		builder.deleteCharAt(indexOfVert);
		return new Segment[] {
				(Segment)getChild(vert + builder.charAt(0)),
				(Segment)getChild(vert + builder.charAt(1))
		};
	}
}
