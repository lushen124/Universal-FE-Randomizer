package ui.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

/**
 * Util class that contains some functions to make SWT Layouting less verbose.
 */
public class GuiUtil {

    /**
     * Arbitrary Default width for all objects in a grid based layout.
     */
    public static final int DEFAULT_ITEM_WIDTH_280 = 280;

    /**
     * Constructs the Default Layout data for Widgets inside this Tab.
     * <p>
     * By Default that is GridData telling the widget to be in the top left of it's grid, while not grabbing excess space horizintally or vertically.
     *
     * @param widthMultiplier the multiple of {@link GuiUtil#DEFAULT_ITEM_WIDTH_280} pixels this element should have for a width
     */
    public static Object defaultGridData(int widthMultiplier, boolean considerMargins) {
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
        int margins = considerMargins ? widthMultiplier * 10 : 0;
        gridData.widthHint = DEFAULT_ITEM_WIDTH_280 * widthMultiplier + margins;
        return gridData;
    }

    /**
     * Constructs the Default Layout data for Widgets inside this Tab.
     * <p>
     * By Default that is GridData telling the widget to be in the top left of it's grid, while not grabbing excess space horizintally or vertically.
     *
     * @param widthMultiplier the multiple of {@link GuiUtil#DEFAULT_ITEM_WIDTH_280} pixels this element should have for a width
     */
    public static Object defaultGridData(int widthMultiplier) {
        return defaultGridData(widthMultiplier, false);
    }

    /**
     * Convenience overload of {@link #defaultGridData(int)} which sets the width of the element to 1 x {@link GuiUtil#DEFAULT_ITEM_WIDTH_280}
     */
    public static Object defaultGridData() {
        return defaultGridData(1);
    }

    /**
     * Returns a GridLayout with a margin of 5px in all directions
     */
    public static GridLayout gridLayoutWithMargin(){
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginLeft = 5;
        gridLayout.marginTop = 5;
        gridLayout.marginRight = 5;
        gridLayout.marginBottom = 5;
        return gridLayout;
    }

    /**
     * Returns a FormLayout with a margin of 5px in all directions
     */
    public static FormLayout formLayoutWithMargin(){
        FormLayout formLayout = new FormLayout();
        formLayout.marginLeft = 5;
        formLayout.marginTop = 5;
        formLayout.marginRight = 5;
        formLayout.marginBottom = 5;
        return formLayout;
    }
}
