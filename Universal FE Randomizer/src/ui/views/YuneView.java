package ui.views;

import fedata.general.FEBase;
import fedata.general.FEBase.GameType;
import org.eclipse.swt.widgets.Composite;
import ui.common.GuiUtil;
import ui.common.YuneGroup;

/**
 * The baseclass for all yune Views
 */
public abstract class YuneView<T> extends YuneGroup {

    protected GameType type;

    /**
     * Empty Default constructor.
     * <p>
     * If you use this constructor you need to call the methods {@link #createGroup(Composite)} and {@link #compose()} yourself.
     * <p>
     * This can be useful when you have to set some special parameters before the {@link #compose()} method is called, for example due to layouting parameters.
     */
    public YuneView() {
        super();
    }

    /**
     * Constructor for views which are specific to a single game.
     */
    public YuneView(Composite parent) {
        super(parent);
    }

    /**
     * Main Constructor for Views that are used by multiple games, such as the bases view.
     * Sets the current gameType before the {@link #compose()} method is called.
     *
     * @param parent
     * @param type
     */
    public YuneView(Composite parent, GameType type) {
        super();
        createGroup(parent);
        this.type = type;
        compose();
    }

    /**
     * Called to initialize the selected options of the view on loading a game.
     */
    public abstract void initialize(T bundle);

    /**
     * Called when the Selections in the view have to be collected into the Option model.
     * F.e. when randomization starts, or the layout is swapped.
     */
    public abstract T getOptions();
}
