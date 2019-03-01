package util;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an object (of type <code>T</code>) with 
 * parents/children of type <code>K</code>. 
 * @param <T> the type of the object
 * @param <K> the type of the parents and children
 */
public class Node<T, K> {
	
	private List<K> children, parents;
	private T object;
	
	public Node(T obj) {
		this.object = obj;
		children = new ArrayList<>();
		parents = new ArrayList<>();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result + ((parents == null) ? 0 : parents.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Node)) {
			return false;
		}
		Node<?, ?> bn = (Node<?, ?>) obj;
		return bn.getObject().equals(object) && bn.getChildren().equals(children)
				&& bn.getParents().equals(parents);
	}
	
	@Override
	public String toString() {
		return "{object=" + object + " chil.=" + children + " par.=" + parents + "}";
	}

	public List<K> getChildren() {
		return children;
	}
	
	public List<K> getParents() {
		return parents;
	}
	
	public T getObject() {
		return object;
	}
	
}
