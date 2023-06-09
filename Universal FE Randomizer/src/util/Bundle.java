package util;

/**
 * Base Class for all OptionBundles for more descriptively being able to handle the different option bundles at the same time,
 * rather than just treating them all as Objects directly.
 */
public class Bundle {
    /**
     * The seed using which the last rom was generated
     */
    public String seed;

    /**
     * The version of the randomizer that the last seed was generated with
     */
    public Integer version;
}
