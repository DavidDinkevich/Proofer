package geometry.proofs;

public enum FigureRelationType {
	// All
	CONGRUENT,
	// Segments
	PARALLEL, PERPENDICULAR, BISECTS,
	// Triangles
	SIMILAR, SAS, ASA, SSS,
	// Angles
	SUPPLEMENTARY, COMPLEMENTARY, RIGHT,
	// Vertices
	MIDPOINT;
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}