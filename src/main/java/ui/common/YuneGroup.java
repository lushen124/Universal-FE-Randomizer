package ui.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;

public abstract class YuneGroup {
    public Composite group;

    /**
     * Default no-args constructor
     */
    public YuneGroup() {
    }

    /**
     * Constructor that automatically creates a group with default margin of 5 pixels in all directions in the given Parent Composite
     */
    public YuneGroup(Composite parent) {
        createGroup(parent);
        compose();
    }

    /**
     * Method that can be overriden to add all the elements in this group
     */
    protected abstract void compose();

    /**
     * Dispose of all elements in this group
     */
    public void dispose() {
        group.dispose();
    }

    /**
     * Applies the given visibility status to the group
     */
    public void setVisible(boolean visible) {
        this.group.setVisible(visible);
    }

    /**
     * Factory of the Group that this Object wraps.
     * <p>
     * By default the group is a simple Composite, but by overriding this method you can change it to any subclass of Composite aswell.
     */
    protected void createGroup(Composite parent) {
        if (getGroupTitle() == null) {
            group = new Composite(parent, SWT.NONE);
        } else {
            group = new Group(parent, SWT.NONE);
            ((Group) group).setText(getGroupTitle());
        }
        group.setToolTipText(getGroupTooltip());
        group.setLayout(getGroupLayout());
    }

    /**
     * By Default a YuneGroup contains a FormLayout with default margins of 5px.
     * But this method can be overriden to allow using any Layout for the Group.
     */
    protected Layout getGroupLayout() {
        return GuiUtil.formLayoutWithMargin();
    }

    /**
     * Can be overriden to define the title of this group that will be displayed in the UI.
     * <p>
     * If no title should be shown (this return null), then this will cause the group to be created as a Composite rather than as a Group.
     */
    protected String getGroupTitle() {
        return null;
    }

    /**
     * Can be overriden to define the Tooltip of this group that will be displayed in the UI.
     */
    protected String getGroupTooltip(){
        return null;
    }
}
