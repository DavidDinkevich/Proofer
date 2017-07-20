package geometry.proofs;

public enum FigureRelationType {
	// All
	CONGRUENT,
	// Segments
	PARALLEL, PERPENDICULAR, BISECTS,
	// Triangles
	SIMILAR,
	// Angles
	SUPPLEMENTARY, COMPLEMENTARY, VERTICAL, RIGHT,
	// Vertices
	MIDPOINT;
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}