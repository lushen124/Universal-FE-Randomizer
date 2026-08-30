package random.exc;

public class NotReached {
	
	public static void trigger(String message) {
		assert false : message;
	}

}
