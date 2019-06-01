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
	public boolean isSingleFigureRelationType() {
		return this == RIGHT || this == ISOSCELES;
	}
	
	/**
	 * Get whether the given {@link FigureRelationType} is symmetrical.
	 */
	public static boolean isSymmetricalFigureRelationType(FigureRelationType type) {
		if (type.isSingleFigureRelationType())
			return false;
		switch (type) {
		case BISECTS: case ISOSCELES: case MIDPOINT: return false;
		default: return true;
		}
	}
}
