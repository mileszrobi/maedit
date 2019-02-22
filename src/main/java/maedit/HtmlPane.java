package maedit;

import java.awt.Dimension;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

public class HtmlPane {
    JEditorPane htmlPane;
    JScrollPane scrollPane;
    
    public HtmlPane() {
        htmlPane = new JEditorPane();
        htmlPane.setEditable(false);

        scrollPane = new JScrollPane(htmlPane);
        scrollPane.setMinimumSize(new Dimension(320, 200));

        HTMLEditorKit kit = new HTMLEditorKit();
        htmlPane.setEditorKit(kit);

        Document doc = kit.createDefaultDocument();
        htmlPane.setDocument(doc);
        htmlPane.setText("");
    }
    
    public JScrollPane getScrollPane() {
        return scrollPane;
    }
    
    public JEditorPane getTextTarget() {
        return htmlPane;
    }
}