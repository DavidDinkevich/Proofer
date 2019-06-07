package geometry.proofs;

public enum ProofReasons {
	NONE("none"),
	GIVEN("Given"),
	REFLEXIVE("Reflexive Postulate"), TRANSITIVE("Transitive Postulate"),

	RIGHT_ANGLES_CONGRUENT("All right angles are congruent"),
	VERTICAL_ANGLES_CONGRUENT("Vertical angles are congruent"),

	SSS("SSS"), SAS("SAS"), ASA("ASA"),
	
	PERPENDICULAR("Perpendicular segments make right angles"),
	OPP_PERPENDICULAR("Right angles make perpendicular segments"),

	SEGMENT_BISECTOR("A segment bisector divides a segment into two congruent halves"),
	ANGLE_BISECTOR("An angle bisector divides an angle into two congruent halves"),
	MIDPOINT("A midpoint divides a segment into two congruent halves"),
	
	CORR_ANGLES_CONG_TRIANGLES("Corresponding angles of congruent triangles are congruent"),
	CORR_SEGMENTS("Corresponding segments of congruent triangles are congruent"),
	
	SIMILAR("Two triangles are similar if they share at least two pairs of congruent angles"),
	CORR_ANGLES_SIMILAR_TRIANGLES("Corresponding angles of similar triangles are congruent"),
	
	ISOSCELES("Isosceles triangle theorem"),
	ISOSCELES_OPP_ANGLES("Angles opposite the congruent segments of an isosceles triangle "
			+ "are congruent"),
	OPP_ISOSCELES("Opposite of the isosceles triangle theorem"),
	ISOSCELES_OPP_SEGMENTS("Sides opposite of the base angles of an isosceles triangle "
			+ "are congruent"),
	
	SHARED_SUPPLEMENTARY_ANGLE("If two angles are supplementary to the same angle, the two angles"
			+ " are congruent"),
	SHARED_COMPLEMENTARY_ANGLE("If two angles are complementary to the same angle, the two angles"
			+ " are congruent"),
	SUPP_ANGLE_TO_CONG_ANGLES("If an angle is supplementary to one of a pair of congruent angles, "
			+ "it is supplementary to the other"),
	COMP_ANGLE_TO_CONG_ANGLES("If an angle is complementary to one of a pair of congruent angles,"
			+ " it is complementary to the other"),
	SUPP_ANGLES_TO_CONG_ANGLES("If a pair of supplementary angles are supplementary to the"
			+ " same pair of congruent angles, then they are congruent"),
	COMP_ANGLES_TO_CONG_ANGLES("If a pair of complementary angles are complementary to the"
			+ " same pair of congruent angles, then they are congruent"),
	
	PERPENDICULAR_TRANSITIVE("If two segments are perpendicular to the same segment, those"
			+ " two segments are parallel");
	
	private String text;
	
	private ProofReasons(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
}
