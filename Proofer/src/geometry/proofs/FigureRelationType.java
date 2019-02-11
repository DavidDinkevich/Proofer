package geometry.proofs;

public enum FigureRelationType {
	// All
	CONGRUENT,
	// Segments
	PARALLEL, PERPENDICULAR, BISECTS,
	// Triangles
	SIMILAR, ISOSCELES, SAS, ASA, SSS,
	// Angles
	SUPPLEMENTARY, COMPLEMENTARY, RIGHT,
	// Vertices
	MIDPOINT;
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
	
	/**
	 * Get whether this {@link FigureRelationType} is a relation type
	 * that only concerns one figure (such as RIGHT or ISOSCELES).
	 */
	public boolean isSingleFigureRelation() {
		return this == RIGHT || this == ISOSCELES;
	}
}
