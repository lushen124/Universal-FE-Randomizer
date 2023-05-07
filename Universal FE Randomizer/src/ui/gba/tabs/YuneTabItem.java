package ui.gba.tabs;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import java.util.ArrayList;
import java.util.List;

/**
 * The Base class for all Yune Tab Items
 */
public abstract class YuneTabItem extends CTabItem {

    /**
     * The main container of this Tab.
     */
    protected Composite container;


    protected GameType type;
    protected List<Composite> views = new ArrayList<>();

    public YuneTabItem(CTabFolder parent, GameType type) {
        super(parent, SWT.NONE);
        // Increase the Height of the text in the Tabs a bit
        FontData fontData = this.getFont().getFontData()[0];
        fontData.setHeight(13);
        this.setFont(new Font(this.getFont().getDevice(), fontData));

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
    protected void setupDefaultMainContainer(){
        container = new Composite(getParent(), SWT.NONE);
        GridLayout layout = new GridLayout(numberColumns(), true);
        layout.marginLeft = 5;
        layout.marginTop = 5;
        layout.marginRight = 5;
        layout.marginBottom = 5;
        container.setLayout(layout);
    }

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

    protected void addView(Composite subview, GridData data) {
        subview.setLayoutData(data);
        views.add(subview);
    }
    protected void addView(Composite subview) {
        GridData data = new GridData(SWT.LEFT, SWT.TOP, false, false);
        addView(subview, data);
    }

    public Composite getContainer(){
        return this.container;
    }

}
