package ui.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import ui.gba.tabs.RomInfoDto;

public class RomInfoGroup extends YuneGroup {

    protected Label romName;
    protected Label romCode;
    protected Label friendlyName;
    protected Label length;
    protected Label checksum;

    public RomInfoGroup(Composite parent) {
        super(parent);
        ((Group) group).setText("ROM Info");
    }

    @Override
    protected void compose() {
        romName = new Label(group, SWT.NONE);
        romCode = new Label(group, SWT.NONE);
        friendlyName = new Label(group, SWT.NONE);
        length = new Label(group, SWT.NONE);
        checksum = new Label(group, SWT.NONE);
    }

    public void initialize(RomInfoDto dto) {
        setFriendlyName(dto.getFriendlyName());
        setRomName(dto.getRomName());
        setRomCode(dto.getRomCode());
        setChecksum(dto.getChecksum());
        setLength(dto.getLength());
    }

    @Override
    protected void createGroup(Composite parent) {
        // Override to make this a proper group rather than a Composite itself
        group = new Group(parent, SWT.NONE);
        group.setLayout(getGroupLayout());
    }

    @Override
    protected Layout getGroupLayout(){
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        gridLayout.makeColumnsEqualWidth = true;
        gridLayout.verticalSpacing = 1;
        return gridLayout;
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
