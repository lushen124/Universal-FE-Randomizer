package ui.common;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import util.OptionRecorder.FE4OptionBundle;
import util.OptionRecorder.FE9OptionBundle;
import util.OptionRecorder.GBAOptionBundle;

import java.util.ArrayList;
import java.util.List;

/**
 * The Base class for all Yune Tab Items
 */
public abstract class YuneTabItem extends CTabItem {
    protected Composite container;
    protected GameType type;
    protected List<Composite> views = new ArrayList<>();


    public YuneTabItem(CTabFolder parent, GameType type) {
        this(parent, type, false);
    }

    public YuneTabItem(CTabFolder parent, GameType type, boolean isScrollable) {
        super(parent, SWT.NONE);
        // Increase the Height of the text in the Tabs a bit
        FontData fontData = this.getFont().getFontData()[0];
        fontData.setHeight(13);
        this.setFont(new Font(this.getFont().getDevice(), fontData));

        setText(getTabName());
        setToolTipText(getTabTooltip());
        this.type = type;
        setupDefaultMainContainer(isScrollable);
        compose();
        this.setControl(container);
    }

    /**
     * Creates the default Main Container of the Tab which consist of a Simple Container with a GridLayout.
     */
    protected void setupDefaultMainContainer(boolean isScrollable) {
        container = new Composite(getParent(), SWT.NONE);
        GridLayout layout = new GridLayout(numberColumns(), false);
        layout.marginLeft = 5;
        layout.marginTop = 5;
        layout.marginRight = 5;
        layout.marginBottom = 5;
        container.setLayout(layout);
    }

    /**
     * Must be overridden by each individual tab to arrange the views inside the tab control
     */
    protected abstract void compose();

    /**
     * The Name that will be displayed on this tab.
     */
    protected abstract String getTabName();

    /**
     * The Tooltip that will be displayed when hovering over this tab
     */
    protected abstract String getTabTooltip();

    /**
     * This Method must be overriden by each individual Tab to control how many columns each row has.
     */
    protected abstract int numberColumns();

    /**
     * Adds a new View to the control of this TabItem
     *
     * @param subview    the view that will be added to the tab
     * @param layoutData the layout data that will be set for positioning the view
     * @param <T>        One of the Yune views, which extends Composite
     * @return returns the view that was added
     */
    protected <T extends Composite> T addView(T subview, Object layoutData) {
        subview.setLayoutData(layoutData);
        views.add(subview);
        return subview;
    }

    /**
     * Convenience overload of {@link #addView(Composite, Object)} which passes the {@link GuiUtil#defaultGridData()} for the second parameter.
     */
    protected <T extends Composite> T addView(T subview) {
        return addView(subview, GuiUtil.defaultGridData());
    }

    /**
     * Getter for the control of the TabItem
     */
    public Composite getContainer() {
        return this.container;
    }

    /**
     * Called to preload the options for GBAFE Tabs
     */
    public void preloadOptions(GBAOptionBundle bundle) {
        throw new UnsupportedOperationException();
    }

    public void updateOptionBundle(GBAOptionBundle bundle) {
        throw new UnsupportedOperationException();
    }

    /**
     * Called to preload the options for FE4 Tabs
     */
    public void preloadOptions(FE4OptionBundle bundle) {
        throw new UnsupportedOperationException();
    }

    public void updateOptionBundle(FE4OptionBundle bundle) {
        throw new UnsupportedOperationException();
    }

    /**
     * Called to preload the options for FE9 Tabs
     */
    public void preloadOptions(FE9OptionBundle bundle) {
        throw new UnsupportedOperationException();
    }

    public void updateOptionBundle(FE9OptionBundle bundle) {
        throw new UnsupportedOperationException();
    }

    protected void setViewData(Composite view, int colSpan, int rowSpan) {
        GridData data = view.getLayoutData() != null ? (GridData) view.getLayoutData() : (GridData) GuiUtil.defaultGridData();
        data.horizontalSpan = colSpan;
        data.verticalSpan = rowSpan;
        view.setLayoutData(data);
    }
}
