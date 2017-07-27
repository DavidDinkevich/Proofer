package geometry.proofs;

import java.util.Collection;
import java.util.List;

public interface Figure {
	public String getName();
	public void setName(String name);
	
	/**
	 * Get whether the given name is a valid name
	 * for this figure. NOTE: it does not have to EQUAL
	 * the name of this figure, it just must be valid.
	 */
	public boolean isValidName(String name);
	
	public List<Figure> getChildren();
	public Figure getChild(String name);
	
	default public boolean containsChild(String name) {
		return getChild(name) != null;
	}
	
	default public boolean containsChildren(Collection<Figure> figs) {
		for (Figure fig : figs) {
			if (!containsChild(fig.getName())) {
				return false;
			}
		}
		return true;
	}
}
