package maedit;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;

public class ViewSynchronizer {
    MarkdownPane markdownPane;
    HtmlPane htmlPane;
    boolean currentlySynchronizing = false;
    
    public ViewSynchronizer(MarkdownPane markdownPane, HtmlPane htmlPane) {
        this.markdownPane = markdownPane;
        this.htmlPane = htmlPane;
        currentlySynchronizing = false;
    }
    
    public void init() {
        addMarkdownScrollBarAdjustmentListener();
    }

    private void addMarkdownScrollBarAdjustmentListener() {
        markdownPane.getScrollPane().getViewport().addChangeListener((ChangeEvent e) ->{
            JViewport viewport = markdownPane.getScrollPane().getViewport();
            Rectangle viewRect = viewport.getViewRect();
            Point p = viewRect.getLocation();
            int startIndex = markdownPane.getMarkdownArea().viewToModel(p);
            p.x += viewRect.width;
            p.y += viewRect.height;
            int endIndex = markdownPane.getMarkdownArea().viewToModel(p);
            
            System.out.println("Start: " + startIndex);
            System.out.println("End: " + endIndex);
        });
       
        markdownPane.getScrollPane().getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            if (currentlySynchronizing)
                return;
            currentlySynchronizing = true;
            
            currentlySynchronizing = false;
        });
    }
}
