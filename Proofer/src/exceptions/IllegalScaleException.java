package exceptions;

/**
 * Occurs when an object is attempted to be scaled illegally.
 * For instance, if the scale is set to <= 0.
 * @author David Dinkevich
 */
@SuppressWarnings("serial")
public class IllegalScaleException extends RuntimeException {
	public IllegalScaleException(String message) {
		super(message);
	}
}
