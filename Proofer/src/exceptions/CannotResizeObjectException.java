package exceptions;

/**
 * Occurs when a {@link Resizable} object is attempted to be scaled illegally.
 * For instance, if the scale is set to <= 0.
 * @author David Dinkevich
*/
@SuppressWarnings("serial")
public class CannotResizeObjectException extends RuntimeException {
	public CannotResizeObjectException(String message) {
		super(message);
	}
	
	public CannotResizeObjectException() {
	}
}
