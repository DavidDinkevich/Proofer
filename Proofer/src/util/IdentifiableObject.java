package util;

import java.util.ArrayList;
import java.util.Iterator;

import exceptions.DuplicateIDAgentException;

/**
 * A concrete implementation of the {@link Identifiable} interface.
 * @author David Dinkevich
 */
public class IdentifiableObject implements Identifiable {
	private ArrayList<IDAgent> ids;
	
	public IdentifiableObject() {
		ids = new ArrayList<>();
	}
	/** Copy constructor. */
	public IdentifiableObject(IdentifiableObject o) {
		ids = new ArrayList<>();
		for (IDAgent a : o.ids) {
			// This works because all of the elements in an IDList MUST
			// no matter what extend Identifiable
			@SuppressWarnings("unchecked")
			IDList<Identifiable> idList = ((IDList<Identifiable>)a.getIDList());
			idList.addObject(this);
		}
	}
	
	@Override
	public void addId(IDAgent agent) {
		if (!containsAgentInManager(agent.getIDList().getListCode()))
			ids.add(agent);
		else
			throw new DuplicateIDAgentException();
	}
	
	@Override
	public IDAgent removeId(long listCode) {
		IDAgent a = getAgent(listCode);
		ids.remove(a);
		return a;
	}
	
	@Override
	public boolean removeId(IDAgent agent) {
		return ids.remove(agent);
	}
	
	@Override
	public IDAgent getAgent(long listCode) {
		for (IDAgent a : ids) {
			if (a.getIDList().getListCode() == listCode)
				return a;
		}
		return null;
	}
	
	@Override
	public IDAgent getAgent(int index) {
		return ids.get(index);
	}
	
	@Override
	public boolean containsAgentInManager(long listCode) {
		return getAgent(listCode) != null;
	}

	@Override
	public Iterator<IDAgent> iterator() {
		return ids.iterator();
	}
}
