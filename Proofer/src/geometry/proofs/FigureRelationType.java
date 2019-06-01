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
	 * Get whether the given {@link FigureRelationType} is a relation type
	 * that only concerns one figure (such as RIGHT or ISOSCELES).
	 */
	public static boolean isSingleFigureRelationType(FigureRelationType type) {
		return type == RIGHT || type == ISOSCELES;
	}
	
	/**
	 * Get whether the given {@link FigureRelationType} is symmetrical.
	 */
	public static boolean isSymmetricalFigureRelationType(FigureRelationType type) {
		if (isSingleFigureRelationType(type))
			return false;
		switch (type) {
		case BISECTS: case ISOSCELES: case MIDPOINT: return false;
		default: return true;
		}
	}
}
