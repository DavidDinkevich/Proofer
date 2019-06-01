package geometry.proofs;

import java.util.Collections;
import java.util.List;

import util.Utils;

import static geometry.proofs.FigureRelationType.CONGRUENT;

public class CompoundFigureRelation<T extends Figure> extends FigureRelation {
	
	private List<T> left, right;
	
	public CompoundFigureRelation(List<T> left, List<T> right) {
		super(CONGRUENT);
		this.left = left;
		this.right = right;
	}
	
	@Override
	public boolean isCongruentAndReflexive() {
		return Utils.equalsNoOrder(left, right);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof CompoundFigureRelation))
			return false;
		@SuppressWarnings("unchecked")
		CompoundFigureRelation<T> other = (CompoundFigureRelation<T>) o;
		return (Utils.equalsNoOrder(left, other.left) && Utils.equalsNoOrder(right, other.right))
				|| (Utils.equalsNoOrder(left, other.right) 
						&& Utils.equalsNoOrder(right, other.left));
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + getRelationType().hashCode();
		result = 31 * result + left.hashCode();
		result = 31 * result + right.hashCode();
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(getRelationType().toString() + " ( ");
		for (int i = 0; i < left.size(); i++) {
			builder.append(left.get(i) + (i == left.size() - 1 ? "" : " + "));
		}
		builder.append(", ");
		for (int i = 0; i < right.size(); i++) {
			builder.append(right.get(i) + (i == right.size() - 1 ? "" : " + "));
		}
		builder.append(" )");
		return builder.toString();
	}

	@Override
	public boolean containsFigure(Figure fig) {
		return left.contains(fig) || right.contains(fig);
	}

	@SuppressWarnings("hiding")
	@Override
	public <T extends Figure> T getFigure0() {
		throw new UnsupportedOperationException("Use getLeftFigures() instead");
	}

	@SuppressWarnings("hiding")
	@Override
	public <T extends Figure> T getFigure1() {
		throw new UnsupportedOperationException("Use getRightFigures() instead");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Figure> getFigures() {
		return (List<Figure>) Utils.combineLists(left, right);
	}

	public List<T> getLeftFigures() {
		return Collections.unmodifiableList(left);
	}

	public List<T> getRightFigures() {
		return Collections.unmodifiableList(right);
	}
	
}
