package cz.martlin.jevernote.misc;

public class JevernoteException extends Exception {

	private static final long serialVersionUID = 5005103825744406195L;

	public JevernoteException(String message, Throwable cause) {
		super(message, cause);
	}

	public JevernoteException(String message) {
		super(message);
	}

	public JevernoteException(Throwable cause) {
		super(cause);
	}

}
