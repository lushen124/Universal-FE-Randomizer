package ui.gba.tabs;

import fedata.general.FEBase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.GridData;
import ui.ItemAssignmentView;
import ui.WeaponsView;

public class ItemsTab extends YuneTabItem {

    public ItemsTab(CTabFolder parent, FEBase.GameType type) {
        super(parent, type);
    }

    @Override
    protected void compose() {
        GridData weaponsData = new GridData();
        weaponsData.verticalSpan = 3;
        addView(new WeaponsView(container, SWT.NONE, type), weaponsData);
        addView(new ItemAssignmentView(container, SWT.NONE, type));
    }

    @Override
    protected String getTabName() {
        return "Items";
    }

    @Override
    protected String getTabTooltip() {
        return "Contains all Setting related to Items. For Example Weapon Stats, and Weapon Assignment";
    }

    @Override
    protected int numberColumns() {
        return 2;
    }
}
