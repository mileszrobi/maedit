package maedit;

import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;


public class HtmlPane {
    JEditorPane editorPane;
    JScrollPane scrollPane;
    
    public HtmlPane() {
		editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
    	
        scrollPane = new JScrollPane(editorPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setMinimumSize(new Dimension(320, 200));
    }
    
    public JScrollPane getScrollPane() {
        return scrollPane;
    }
    
    public JEditorPane getTextTarget() {
        return editorPane;
    }
    
    public void show() {
    	scrollPane.setVisible(true);
    }
    
    public void hide() {
    	scrollPane.setVisible(false);
    }
}

