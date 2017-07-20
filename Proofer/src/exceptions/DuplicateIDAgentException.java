package exceptions;

/**
 * Occurs when an {@link IDAgent} is added to an {@link IdentifiableObject}
 * that already contains an {@link IDAgent} belonging to the same {@link IDManager}.
 * ({@link IdentifiableObject}s cannot contain multiple {@link IDAgent}s belonging to the same
 * {@link IDManager}.)
 * @author David Dinkevich
 */
@SuppressWarnings("serial")
public class DuplicateIDAgentException extends RuntimeException {
	public DuplicateIDAgentException(String message) {
		super(message);
	}
	public DuplicateIDAgentException() {
		super("Cannot contain multiple IDAgents belonging to"
				+ "the same IDList.");
	}
}
