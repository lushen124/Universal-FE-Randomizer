package ui.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

import fedata.general.FEBase.GameType;
import ui.views.YuneView;

public abstract class YuneTabFormItem extends YuneTabItem {
    public YuneTabFormItem(CTabFolder parent, GameType type) {
        super(parent, type);
    }
    
    /**
     * Creates the default Main Container of the Tab which consist of a Simple Container with a GridLayout.
     */
    protected void setupDefaultMainContainer() {
        container = new Composite(getParent(), SWT.NONE);

        FormLayout layout = new FormLayout();
        container.setLayout(layout);
    }

    /**
     * Adds a new View to the control of this TabItem
     *
     * @param subview    the view that will be added to the tab
     * @param layoutData the layout data that will be set for positioning the view
     * @param <T>        One of the Yune views, which extends Composite
     * @return returns the view that was added
     */
    protected <T extends YuneView> T addView(T subview, FormData layoutData) {
        subview.group.setLayoutData(layoutData);
        views.add(subview);
        return subview;
    }

    /**
     * Getter for the control of the TabItem
     */
    public Composite getContainer() {
        return this.container;
    }
}
