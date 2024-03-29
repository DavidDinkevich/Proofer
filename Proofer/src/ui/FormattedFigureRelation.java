package ui;

import geometry.proofs.FigureRelation;
import geometry.proofs.ProofUtils;

/**
 * Wrapper class for {@link FigureRelation}s to be inserted into a Table.
 */
public class FormattedFigureRelation {
	
	private FigureRelation rel;
	/** Position in a proof */
	private int index;
	
	public FormattedFigureRelation(FigureRelation rel, int index) {
		this.rel = rel;
		this.index = index;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof FormattedFigureRelation))
			return false;
		FormattedFigureRelation other = (FormattedFigureRelation) o;
		return index == other.index && FigureRelation.safeEquals(rel, other.rel);
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + rel.hashCode();
		result = 31 * result + index;
		return result;
	}
	
	public FigureRelation getFigureRelation() {
		return rel;
	}
	
	public String getReason() {
		return rel.getReason().getText();
	}
	
	public String getStatement() {
		return ProofUtils.formatFigureRelationStatement(rel);
	}
	
	public String getIndex() {
		return "" + index;
	}
}
