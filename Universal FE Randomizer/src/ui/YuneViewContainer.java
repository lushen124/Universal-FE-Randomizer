package ui;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import ui.common.Preloadable;

/**
 * A YuneViewContainer is a container which will be created as part of the MainView and is responsible for managing the
 * different Views (f.e. BasesView, GrowthsView, etc.).
 *
 * The responsibilities are:
 * <ul>
 *     <li>Create the correct Views depending on GameType</li>
 *     <li>Provide the layout how they are placed</li>
 *     <li>Forward Preloading of the Options</li>
 *     <li>Forward or Perform the Updating of a Bundle with the newly
 *     selected options when starting the randomizer, or swapping layouts</li>
 * </ul>
 */
public abstract class YuneViewContainer extends Composite implements Preloadable {

    protected GameType type;

    /**
     * Common Constructor,
     */
    public YuneViewContainer(Composite parent, GameType loadedType) {
        super(parent, SWT.NONE);
        this.type = loadedType;
        compose();
    }

    /**
     * This method is called by the constructor to create and layout the different views within the View Container
     */
    protected abstract void compose();
}
