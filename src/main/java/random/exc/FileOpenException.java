package random.exc;

public class FileOpenException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9001L;

	public FileOpenException() {
		super("Failed to open file.");
	}

}
