package ui.canvas;

import java.util.Objects;

import geometry.shapes.Shape;
import geometry.shapes.VertexShape;

/**
 * Serves as wrapper class that allows non-"vertex shapes" (such as ellipses) to be
 * manipulated and treated as one.
 * @param <T> The class type that this wrapper will wrap
 */
/*
 * TODO: EllipseVertexWrapper - 5 vertices total
 * 		 VertexBuffer - one vertex per char in name
 *       Selector - all vertices
 * 
 */
public abstract class VertexWrapper<T extends Shape> implements VertexShape {
	
	private T target;
	
	public VertexWrapper(T target) {
		this.target = Objects.requireNonNull(target);
	}

	public T getTarget() {
		return target;
	}
	
}
