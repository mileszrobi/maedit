package maedit;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public class ContentSynchronizer {
    private final static Logger LOGGER = Logger.getLogger(ContentSynchronizer.class.getName());
            
    Document document;
    MarkdownToHtml htmlRenderer;
    JTextComponent target;
    
    public ContentSynchronizer(Document document, MarkdownToHtml htmlRenderer, JTextComponent target) {
        this.document = document;
        this.htmlRenderer = htmlRenderer;
        this.target = target;
    }
    
    public void init() {
            document.addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                syncToHtml();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                syncToHtml();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                syncToHtml();
            }
        });

    }
    
    void syncToHtml() {
        try {
            target.setText(htmlRenderer.getHtmlAsString(document.getText(0, document.getLength())));
        } catch (BadLocationException ex) {
            LOGGER.log(Level.SEVERE, "Could not get text from editor.", ex);
        }
    }
}
