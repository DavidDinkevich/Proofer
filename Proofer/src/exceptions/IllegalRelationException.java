package exceptions;

@SuppressWarnings("serial")
public class IllegalRelationException extends RuntimeException {

	public IllegalRelationException() {
		this("Illegal relation type between figures.");
	}

	public IllegalRelationException(String arg0) {
		super(arg0);
	}

	public IllegalRelationException(Throwable arg0) {
		super(arg0);
	}

	public IllegalRelationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public IllegalRelationException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
