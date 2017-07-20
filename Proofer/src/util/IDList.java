package util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An {@link IDList} is a dynamic array that stores implementors of the
 * {@link Identifiable} interface. In a normal array or ArrayList,
 * in order to delete an object, one must either access its index in
 * the array, or rely on its {@link Object#equals(Object)} method. However, this
 * is not always practical. In an {@link IDList}, each {@link Identifiable}
 * is assigned a unique {@link IDAgent}. When the object is requested to be removed
 * from this {@link IDList}, the {@link IDAgent} it was given is used to find
 * the {@link Identifiable}'s position in the {@link IDList}.
 * @author David Dinkevich
 */
public class IDList<E extends Identifiable> implements Iterable<E> {
	private List<E> identifiables;
	private long listCode; // Id of this list
	
	private static long instances = 0; // Total number of instances of this class created
	
	/**
	 * Creates an {@link IDList}.
	 */
	public IDList() {
		listCode = instances++; // Generate id
		identifiables = new ArrayList<>();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof IDList))
			return false;
		IDList<?> test = (IDList<?>)o;
		return listCode == test.listCode;
	}
	
	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + Long.hashCode(listCode);
		for (E element : identifiables) {
			// IDLists cannot store null elements
			result = 31 * result + element.hashCode();
		}
		return result;
	}
	
	@Override
	public Iterator<E> iterator() {
		return identifiables.iterator();
	}
	
	private void updateIds() {
		for (int i = 0; i < identifiables.size(); i++) {
			identifiables.get(i).getAgent(listCode).setId(i);
		}
	}
	
	public void addObject(E o) {
		if (o == null)
			throw new NullPointerException("Cannot add a null object.");
		IDAgent agent = new IDAgent(this, identifiables.size());
		o.addId(agent); // Add this list to identifiable object
		identifiables.add(o);
	}
	
	public void addObjects(E[] objects) {
		for (E o : objects) {
			addObject(o);
		}
	}
	
	private boolean removeObject(int i, boolean updateIds) {
		E object = identifiables.get(i);
		// If the object doesn't exist within the list
		if (object == null)
			return false;
		return removeObject(object, updateIds);
	}
	
	private boolean removeObject(E object, boolean updateIds) {
		if (object == null)
			throw new NullPointerException("Cannot remove a null object.");
		// Get the IDAgent of object to delete
		IDAgent agent = object.getAgent(listCode);
		
		// If the object doesn't contain the agent
		if (agent == null)
			return false;
		
		// Remove object
		identifiables.remove(agent.getId());
		
		// Remove this id manager from object's list
		object.removeId(agent);
		
		if (updateIds) {
			// Update all of the remaining IDAgents' IDs
			updateIds();
		}
		
		return true; // Success
	}
	
	public boolean removeObject(E object) {		
		return removeObject(object, true);
	}
	
	public boolean removeObjects(E[] objects) {
		boolean success = false;
		for (int i = objects.length-1; i >= 0; i--) {
			success |= removeObject(objects[i], false);
		}
		// Update ids after modifying list
		updateIds();
		return success;
	}
	
	public boolean contains(IDAgent agent) {
		// Saves us from looping and wasting time
		if (agent == null)
			return false;
		for (Identifiable o : identifiables) {
			if (o.getAgent(listCode).equals(agent)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(E o) {
		return contains(o.getAgent(listCode));
	}
	
	// Slow...
	public E get(IDAgent a) {
		for (E o : identifiables) {
			IDAgent agent = o.getAgent(listCode);
			if (agent.equals(a)) {
				return o;
			}
		}
		return null;
	}
	
	public E get(int index) {
		return identifiables.get(index);
	}
	
	/**
	 * Remove all objects in this {@link IDList}.
	 * @return true if all objects were removed successfully
	 */
	public boolean clear() {
		boolean success = true;
		while (identifiables.size() > 0) {
			success |= removeObject(identifiables.size()-1, false);
		}
		// Update all ids after modifying list
		updateIds();
		return success;
	}
	
	/**
	 * Get the number of total amount of elements in this
	 * {@link IDList}.
	 * @return the count
	 */
	public int count() {
		return identifiables.size();
	}

	/**
	 * Get the list code of this {@link IDList}. The list code
	 * is what identifies this {@link IDList} and distinguishes it
	 * from others.
	 * @return the list code
	 */
	public long getListCode() {
		return listCode;
	}
}

