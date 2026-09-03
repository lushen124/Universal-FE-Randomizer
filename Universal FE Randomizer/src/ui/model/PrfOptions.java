package ui.model;

public class PrfOptions {
    public final boolean createPrfs;
    public final boolean unbreakablePrfs;
    public final boolean effectivePrfs;

    public PrfOptions(boolean create, boolean unbreakablePrfs, boolean effectivePrfs) {
        this.createPrfs = create;
        this.unbreakablePrfs = unbreakablePrfs;
        this.effectivePrfs = effectivePrfs;
    }
}
