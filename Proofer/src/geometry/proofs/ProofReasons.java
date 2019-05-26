package geometry.proofs;

public enum ProofReasons {
	GIVEN("Given"),
	REFLEXIVE("Reflexive Postulate"),
	SSS("SSS"), SAS("SAS"), ASA("ASA"),
	PERPENDICULAR("Perpendicular segments make right angles"),
	BISECTS("A segment bisector divides a segment into two congruent halves"),
	MIDPOINT("A midpoint divides a segment into two congruent halves"),
	CORR_ANGLES("Corresponding angles of congruent triangles are congruent"),
	CORR_SEGMENTS("Corresponding segments of congruent triangles are congruent"),
	ISOSCELES("Isosceles triangle theorem"),
	ISOSCELES_OPP_ANGLES("Angles opposite the congruent segments of an isosceles triangle "
			+ "are congruent"),
	OPP_ISOSCELES("Base angle theorem (isosceles)"),
	ISOSCELES_OPP_SEGMENTS("Sides opposite of the base angles of an isosceles triangle "
			+ "are congruent");
	
	private String text;
	
	private ProofReasons(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
}
