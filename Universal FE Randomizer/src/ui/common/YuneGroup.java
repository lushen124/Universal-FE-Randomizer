package ui.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public abstract class YuneGroup {
    public Group group;

    public YuneGroup() {
    }

    public YuneGroup(Composite parent){
        group = new Group(parent, SWT.NONE);
        group.setLayout(GuiUtil.formLayoutWithMargin());
        compose();
    }

    protected abstract void compose();
//    protected abstract void dispose();
//    protected abstract void initialize();
}
