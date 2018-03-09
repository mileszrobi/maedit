package maedit;

import java.awt.event.AdjustmentEvent;
import javax.swing.JScrollBar;

public class ScrollBarSynchronizer {
    JScrollBar leftScrollBar, rightScrollBar;
    boolean currentlySynchronizing = false;
    
    public ScrollBarSynchronizer(JScrollBar leftScrollBar, JScrollBar rightScrollBar) {
        this.leftScrollBar = leftScrollBar;
        this.rightScrollBar = rightScrollBar;
        currentlySynchronizing = false;
    }
    
    public void init() {
        addAdjustmentListener(leftScrollBar, rightScrollBar);
        addAdjustmentListener(rightScrollBar, leftScrollBar);
    }
    
    private void addAdjustmentListener(JScrollBar thisScrollBar, JScrollBar otherScrollBar) {
        thisScrollBar.addAdjustmentListener((AdjustmentEvent e) -> {
            if (currentlySynchronizing)
                return;
            currentlySynchronizing = true;
            int thisMax = thisScrollBar.getMaximum();
            int otherMax = otherScrollBar.getMaximum();
            if (thisMax != 0)
                otherScrollBar.setValue(e.getValue() * otherMax / thisMax);
            currentlySynchronizing = false;
        });
    }
}
