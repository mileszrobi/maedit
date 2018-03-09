package maedit;

import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;

public class MarkdownPane {
    JTextArea markdownArea;
    JScrollPane scrollPane;

    public MarkdownPane() {
        markdownArea = new JTextArea();
        scrollPane = new JScrollPane(markdownArea);
        scrollPane.setMinimumSize(new Dimension(320, 200));
    }
    
    JScrollPane getScrollPane() {
        return scrollPane;
    }
    
    Document getDocument() {
        return markdownArea.getDocument();
    }
    
    String getText() {
        return markdownArea.getText();
    }

    void setText(String text) {
        markdownArea.setText(text);
    }
}
