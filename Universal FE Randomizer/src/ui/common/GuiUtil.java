package ui.common;

import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;

public class GuiUtil {
    public static FormLayout formLayoutWithMargin(){
        FormLayout formLayout = new FormLayout();
        formLayout.marginLeft = 5;
        formLayout.marginTop = 5;
        formLayout.marginRight = 5;
        formLayout.marginBottom = 5;
        return formLayout;
    }
    public static GridLayout gridLayoutWithMargin(){
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginLeft = 5;
        gridLayout.marginTop = 5;
        gridLayout.marginRight = 5;
        gridLayout.marginBottom = 5;
        return gridLayout;
    }
}
