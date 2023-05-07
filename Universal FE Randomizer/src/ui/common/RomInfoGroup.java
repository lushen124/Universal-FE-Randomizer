package ui.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class RomInfoGroup extends YuneGroup {

    protected Label romName;
    protected Label romCode;
    protected Label friendlyName;
    protected Label length;
    protected Label checksum;

    public RomInfoGroup(Composite parent) {
        group = new Group(parent, SWT.NONE);
        group.setLayout(GuiUtil.formLayoutWithMargin());
        group.setText("ROM Info");
        compose();
    }

    @Override
    protected void compose() {
        GridLayout gridLayout = GuiUtil.gridLayoutWithMargin();
        gridLayout.numColumns = 3;
        gridLayout.makeColumnsEqualWidth = true;
        group.setLayout(gridLayout);

        romName = new Label(group, SWT.NONE);
        romCode = new Label(group, SWT.NONE);
        friendlyName = new Label(group, SWT.NONE);
        length = new Label(group, SWT.NONE);
        checksum = new Label(group, SWT.NONE);
    }

    public void setVisible(boolean visible) {
        group.setVisible(visible);
    }

    public void setRomName(String romName) {
        this.romName.setText(romName);
    }

    public void setRomCode(String romCode) {
        this.romCode.setText(romCode);
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName.setText(friendlyName);
    }

    public void setLength(String length) {
        this.length.setText(length);
    }

    public void setChecksum(String checksum) {
        this.checksum.setText(checksum);
    }

}
