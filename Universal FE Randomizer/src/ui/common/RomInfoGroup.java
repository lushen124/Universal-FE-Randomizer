package ui.common;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import ui.MainView;

public class RomInfoGroup extends YuneGroup {

    protected Label romName;
    protected Label romCode;
    protected Label friendlyName;
    protected Label length;
    protected Label checksum;
    protected Button importSettings;
    protected Button writeLogging;
    protected Button exportSettings;
    protected long crc32;

    public RomInfoGroup(Composite parent) {
        super(parent);
    }

    @Override
    protected void compose() {
        // row1
        romName = new Label(group, SWT.NONE);
        romCode = new Label(group, SWT.NONE);
        friendlyName = new Label(group, SWT.NONE);
        importSettings = new Button(group, SWT.PUSH);
        importSettings.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
        importSettings.setText("Import Settings");
        // row 2
        length = new Label(group, SWT.NONE);
        checksum = new Label(group, SWT.NONE);
        writeLogging = new Button(group, SWT.CHECK);
        writeLogging.setText("Write logging to file?");


        exportSettings = new Button(group, SWT.PUSH);
        exportSettings.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
        exportSettings.setText("Export Settings");
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
        gridLayout.numColumns = 4;
        gridLayout.makeColumnsEqualWidth = false;
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

    public void updateImportExportListeners(MainView mainView, GameType type) {
        // First delete all existing listeners form the buttons
        for (Listener listener : importSettings.getListeners(SWT.Selection)) {
            importSettings.removeListener(SWT.Selection, listener);
        }
        for (Listener listener : exportSettings.getListeners(SWT.Selection)) {
            exportSettings.removeListener(SWT.Selection, listener);
        }
    }

    public Button getWriteLogging() {
        return writeLogging;
    }
}