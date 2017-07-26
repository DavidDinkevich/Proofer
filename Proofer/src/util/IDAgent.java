package util;

/**
 * An {@link IDAgent} represents the position of an element in an {@link IDList}.
 * @author David Dinkevich
 */
public class IDAgent {
	private IDList<? extends Identifiable> idList;
	private int id;
	
	public IDAgent(IDList<?> idList, int id) {
		if (idList == null)
			throw new NullPointerException();
		this.idList = idList;
		this.id = id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof IDAgent))
			return false;
		IDAgent a = (IDAgent)o;
		return a.idList.getListCode() == idList.getListCode()
				&& a.id == id;
	}
	
	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + id;
		result = 31 * result + idList.hashCode();
		return result;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Get the {@link IDList} that this {@link IDAgent} belongs to.
	 * @return the {@link IDList}.
	 */
	public IDList<?> getIDList() {
		return idList;
	}
	/**
	 * Set the {@link IDList} that this {@link IDAgent} belongs to.
	 */
	public void setIDList(IDList<?> idList) {
		if (idList == null)
			throw new NullPointerException();
		this.idList = idList;
	}
}