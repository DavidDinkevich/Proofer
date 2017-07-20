package exceptions;

/**
 * Occurs when a {@link Selectable} object is illegally selected.
 * @author David Dinkevich
 */
@SuppressWarnings("serial")
public class IllegalSelectionException extends RuntimeException {
	
	public IllegalSelectionException() {}

	public IllegalSelectionException(String message) {
		super(message);
	}
}
