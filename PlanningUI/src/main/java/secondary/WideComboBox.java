package secondary;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class WideComboBox extends JComboBox {

    public WideComboBox() {
        setMaximumRowCount(20);
    }

    public WideComboBox(final Object items[]) {
        super(items);
    }

    public WideComboBox(Vector items) {
        super(items);
    }

    public WideComboBox(ComboBoxModel aModel) {
        super(aModel);
    }

    private boolean layingOut = false;

    public void doLayout() {
        try {
            layingOut = true;
            super.doLayout();
        } finally {
            layingOut = false;
        }
    }

    public Dimension getSize() {
        Dimension dim = super.getSize();
        if (!layingOut) {
            dim.width = Math.max(dim.width, getPreferredSize().width);
        }
        return dim;
    }
}
