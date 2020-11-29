package maedit;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

public class ViewSynchronizer {
    MarkdownPane markdownPane;
    HtmlPane htmlPane;
    
    int markdownPanePosition = Integer.MAX_VALUE;
    
    public ViewSynchronizer(MarkdownPane markdownPane, HtmlPane htmlPane) {
        this.markdownPane = markdownPane;
        this.htmlPane = htmlPane;
    }
    
    void setHtmlPane(HtmlPane htmlPane) {
    	this.htmlPane = htmlPane;
    	markdownPanePosition = Integer.MAX_VALUE;
    	viewportChanged();
    }
    
    public void init() {
    	markdownPane.getScrollPane().getViewport().addChangeListener((ChangeEvent event) -> viewportChanged());
    }
    
    OffsetPair getSourcePosition(Element startElement) {
    	Element element = startElement;
    	while (element != null && !element.getAttributes().isDefined("sourceposition"))
    		element = element.getParentElement();
    	
    	if (element == null)
    		return new OffsetPair(startElement, -1, -1);

		String sourcePosition = (String)element.getAttributes().getAttribute("sourceposition");
		String[] boundStrings = sourcePosition.split("-");
		
		return new OffsetPair(element,
							  Integer.parseInt(boundStrings[0]),
							  Integer.parseInt(boundStrings[1]));
    }
    
	
	void viewportChanged() {
        JViewport viewport = markdownPane.getScrollPane().getViewport();
        Rectangle viewRect = viewport.getViewRect();
        Point p = viewRect.getLocation();
        
        int topOffset = markdownPane.getMarkdownArea().viewToModel2D(p);
        if (topOffset < markdownPanePosition) {
        	// Scrolling up - align top
        	alignTop(topOffset);
        } else {
        	// Scrolling down
            p.y += viewRect.height;
            int bottomPosition = markdownPane.getMarkdownArea().viewToModel2D(p);
            alignBottom(bottomPosition);
        }
        
        markdownPanePosition = topOffset;
	}
	
    void alignTop(int topOffset) {
		Rectangle2D position = markdownOffsetToHtmlPanePosition(topOffset);
		htmlPane.getTextTarget().scrollRectToVisible(new Rectangle(
    			(int)position.getX(),
    			(int)position.getY(),
    			htmlPane.getTextTarget().getVisibleRect().width,
    			htmlPane.getTextTarget().getVisibleRect().height));
    }
    
    void alignBottom(int bottomOffset) {
    	Rectangle2D position = markdownOffsetToHtmlPanePosition(bottomOffset);
    	htmlPane.getTextTarget().scrollRectToVisible(new Rectangle(
    			(int)position.getX(),
    			(int)position.getY() - htmlPane.getTextTarget().getVisibleRect().height + htmlPane.getTextTarget().getFont().getSize(),
        	    htmlPane.getTextTarget().getVisibleRect().width,
        	    htmlPane.getTextTarget().getVisibleRect().height));
    }

	Rectangle2D markdownOffsetToHtmlPanePosition(int markdownOffset) {
        Element element = findNearestElementForMarkdownOffset(htmlPane.getTextTarget().getDocument().getDefaultRootElement(), markdownOffset);
        
        OffsetPair sourceOffsetsForElement = getSourcePosition(element);
        double progressWithinElement = 
        		(double)(markdownOffset - sourceOffsetsForElement.start) 
        		/ (double)(sourceOffsetsForElement.end - sourceOffsetsForElement.start);  

        if (progressWithinElement < 0) // Sometimes we skip ahead within a line to the next element - thus the progress can be negative.
        	progressWithinElement = 0;
        
		int htmlOffset = (int)(element.getStartOffset() + progressWithinElement * (element.getEndOffset() - element.getStartOffset()));
		if (htmlOffset < 0) htmlOffset = 0;
		if (htmlOffset > htmlPane.getTextTarget().getDocument().getLength()) htmlOffset = htmlPane.getTextTarget().getDocument().getLength();
		
		try {
			return htmlPane.getTextTarget().modelToView2D(htmlOffset);
		} catch (BadLocationException e) {
			return new Rectangle2D.Float();
		}
	}
    
    class ElementAndDistance {
    	Element element;
    	
    	long lineDistance;
    	
    	ElementAndDistance(Element element, long lineDistance) {
    		this.element = element;
    		this.lineDistance = lineDistance;
    	}
    }
    private Element findNearestElementForMarkdownOffset(Element element, int position) {
    	return findNearestElementForMarkdownOffset(element, position, new ElementAndDistance(element, Long.MAX_VALUE)).element;
    }
    
    private ElementAndDistance findNearestElementForMarkdownOffset(Element element, int position, ElementAndDistance current) {
    	// Depth first so that the most specific element is found first
    	for (int i = 0; i < element.getElementCount(); i++)
    		current = findNearestElementForMarkdownOffset(element.getElement(i), position, current);
    	
    	var offsets = getSourcePosition(element);
    	
    	if (offsets.start <= position && position <= offsets.end)
    		return new ElementAndDistance(element, 0);
    	
    	if (position < offsets.start) {
    		long lineCount = countLinesBetweenPositions(position, offsets.start);
    		if (lineCount < current.lineDistance)
    			return new ElementAndDistance(element, lineCount);
    	}
    	
    	return current;
    }
    
    private long countLinesBetweenPositions(int start, int end) {
    	return markdownPane.getText().substring(start, end+1).lines().count();
    }
    
    class OffsetPair {
    	Element element;
    	int start, end;
    	
    	public OffsetPair(Element element, int start, int end) {
    		this.element = element;
    		this.start = start;
    		this.end = end;
    	}
    	
    	public String toString() {
    		return "Element: " + element + "; Start: " + start + "; End: " + end;
    	}
    }

}


