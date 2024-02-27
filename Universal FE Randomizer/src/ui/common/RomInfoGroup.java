package ui.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

public class RomInfoGroup extends YuneGroup {

    protected Label romName;
    protected Label romCode;
    protected Label friendlyName;
    protected Label length;
    protected Label checksum;
    protected long crc32;

    public RomInfoGroup(Composite parent) {
        super(parent);
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
    protected Layout getGroupLayout() {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        gridLayout.makeColumnsEqualWidth = true;
        gridLayout.verticalSpacing = 1;
        gridLayout.horizontalSpacing = 50;
        return gridLayout;
    }

    public void setRomName(String romName) {
        this.romName.setText("ROM Name: " + romName);
    }

    public void setRomCode(String romCode) {
        this.romCode.setText("ROM Code: " + romCode);
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName.setText("Display Name: " + friendlyName);
    }

    public void setLength(long length) {
        this.length.setText("File Length: " + length);
    }

    public void setChecksum(long checksum) {
        crc32 = checksum;
        this.checksum.setText("CRC-32: " + checksum);
    }

    public String getChecksum() {
        return this.checksum.getText();
    }

    public long getCrc32() {
        return this.crc32;
    }


    @Override
    public String getGroupTitle() {
        return "ROM Info";
    }
}
