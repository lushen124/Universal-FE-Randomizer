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
import ui.views.YuneView;
import util.OptionRecorder.FE4OptionBundle;
import util.OptionRecorder.FE9OptionBundle;
import util.OptionRecorder.GBAOptionBundle;

import java.util.ArrayList;
import java.util.List;

/**
 * The Base class for all Yune Tab Items.
 *
 * Provides methods to help with layouting and letting the child classes be somewhat configurable by just overriding some methods.
 */
public abstract class YuneTabItem extends CTabItem implements Preloadable {
    protected Composite container;
    protected GameType type;
    protected List<YuneView> views = new ArrayList<>();

    public YuneTabItem(CTabFolder parent, GameType type) {
        super(parent, SWT.NONE);
        setText(getTabName());
        setToolTipText(getTabTooltip());
        this.type = type;
        setupDefaultMainContainer();
        compose();
        this.setControl(container);
    }

    /**
     * Creates the default Main Container of the Tab which consist of a Simple Container with a GridLayout.
     */
    protected void setupDefaultMainContainer() {
        container = new Composite(getParent(), SWT.NONE);

        GridLayout layout = GuiUtil.gridLayoutWithMargin();
        layout.numColumns = numberColumns();
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
    protected <T extends YuneView> T addView(T subview, Object layoutData) {
        subview.group.setLayoutData(layoutData);
        views.add(subview);
        return subview;
    }

    /**
     * Convenience overload of {@link #addView(YuneView, Object)} which passes the {@link GuiUtil#defaultGridData()} for the second parameter.
     */
    protected <T extends YuneView> T addView(T subview) {
        return addView(subview, GuiUtil.defaultGridData());
    }

    /**
     * Getter for the control of the TabItem
     */
    public Composite getContainer() {
        return this.container;
    }

    /**
     * Allows setting the col and rowSpan of the given View.
     * If the view already defines a LayoutData, then the colSpan and rowSpan are just added on.
     * Otherwise, the {@link GuiUtil#defaultGridData()} will be set for the view.
     */
    protected void setViewData(YuneView view, int colSpan, int rowSpan) {
        GridData data = view.group.getLayoutData() != null ? (GridData) view.group.getLayoutData() : (GridData) GuiUtil.defaultGridData();
        data.horizontalSpan = colSpan;
        data.verticalSpan = rowSpan;
        view.group.setLayoutData(data);
    }
}
