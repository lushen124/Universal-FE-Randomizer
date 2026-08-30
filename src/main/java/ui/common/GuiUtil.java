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

    public static final int DEFAULT_ITEM_WIDTH_300 = 300;

    /**
     * Default value for the margin of a composite.
     */
    public static final int DEFAULT_MARGIN_5 = 5;

    /**
     * Constructs the Default Layout data for Widgets inside this Tab.
     * <p>
     * By Default that is GridData telling the widget to be in the top left of it's grid, while not grabbing excess space horizintally or vertically.
     *
     * @param widthMultiplier the multiple of {@link GuiUtil#DEFAULT_ITEM_WIDTH_300} pixels this element should have for a width
     * @param widthModifier a number of pixels by which to adjust the width hint of the object this will be applied to.
     */
    public static Object defaultGridData(int widthMultiplier, int widthModifier) {
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
        gridData.widthHint = DEFAULT_ITEM_WIDTH_300 * widthMultiplier + widthModifier;
        return gridData;
    }

    /**
     * Constructs the Default Layout data for Widgets inside this Tab.
     * <p>
     * By Default that is GridData telling the widget to be in the top left of it's grid, while not grabbing excess space horizintally or vertically.
     *
     * @param widthMultiplier the multiple of {@link GuiUtil#DEFAULT_ITEM_WIDTH_300} pixels this element should have for a width
     */
    public static Object defaultGridData(int widthMultiplier) {
        return defaultGridData(widthMultiplier, 0);
    }

    /**
     * Convenience overload of {@link #defaultGridData(int)} which sets the width of the element to 1 x {@link GuiUtil#DEFAULT_ITEM_WIDTH_300}
     */
    public static Object defaultGridData() {
        return defaultGridData(1);
    }

    /**
     * Returns a GridLayout with a margin of 5px in all directions
     */
    public static GridLayout gridLayoutWithMargin(){
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginLeft = DEFAULT_MARGIN_5;
        gridLayout.marginTop = DEFAULT_MARGIN_5;
        gridLayout.marginRight = DEFAULT_MARGIN_5;
        gridLayout.marginBottom = DEFAULT_MARGIN_5;
        return gridLayout;
    }

    /**
     * Returns a FormLayout with a margin of 5px in all directions
     */
    public static FormLayout formLayoutWithMargin(){
        FormLayout formLayout = new FormLayout();
        formLayout.marginLeft = DEFAULT_MARGIN_5;
        formLayout.marginTop = DEFAULT_MARGIN_5;
        formLayout.marginRight = DEFAULT_MARGIN_5;
        formLayout.marginBottom = DEFAULT_MARGIN_5;
        return formLayout;
    }
}
