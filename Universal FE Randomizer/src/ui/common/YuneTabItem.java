package ui.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import fedata.general.FEBase.GameType;
import ui.views.YuneView;

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
    protected abstract void setupDefaultMainContainer();
	
	/**
     * Must be overridden by each individual tab to arrange the views inside the tab control
     */
    protected abstract void compose();

    /**
     * The Name that will be displayed on this tab.
     */
    public abstract String getTabName();

    /**
     * The Tooltip that will be displayed when hovering over this tab
     */
    protected abstract String getTabTooltip();
    
    public abstract Composite getContainer();
    
    public boolean validate() {
    	return true;
    }
    
    public String getValidationError() {
    	return null;
    }
}
