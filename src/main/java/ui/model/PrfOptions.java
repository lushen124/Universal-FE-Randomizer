package ui.model;

public class PrfOptions {
    public final boolean createPrfs;
    public final boolean unbreakablePrfs;

    public PrfOptions(boolean create, boolean unbreakablePrfs) {
        this.createPrfs = create;
        this.unbreakablePrfs = unbreakablePrfs;
    }
}
