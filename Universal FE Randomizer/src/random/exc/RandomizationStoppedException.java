package random.exc;

/**
 * Simple exception which is thrown and handled whenever the GBA Randomizer runs into a blocking exception.
 */
public class RandomizationStoppedException extends RuntimeException {

	private static final long serialVersionUID = -3031327707060780891L;

    public RandomizationStoppedException(String message) {
        super(message);
    }
}
