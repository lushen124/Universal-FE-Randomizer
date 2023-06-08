package ui;

import fedata.general.FEBase;
import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import ui.common.Preloadable;

public class YuneViewContainer extends Composite  implements Preloadable {

    protected GameType type;

    public YuneViewContainer(Composite parent, GameType loadedType) {
        super(parent, SWT.NONE);
        this.type = loadedType;
    }
}
