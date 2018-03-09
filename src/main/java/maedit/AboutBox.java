package maedit;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.event.HyperlinkEvent;
import org.apache.pdfbox.io.IOUtils;

public class AboutBox {
    private final static Logger LOGGER = Logger.getLogger(AboutBox.class.getName());

    JEditorPane editorPane;

    public AboutBox() {
        String text = readHtmlFile();
        editorPane = new JEditorPane("text/html", text);
        editorPane.setEditable(false);
        editorPane.setBackground(new JLabel().getBackground());

        editorPane.addHyperlinkListener((HyperlinkEvent e) -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                openUrlInDefaultBrowser(e.getURL());
            }
        });
    }

    private String readHtmlFile() {
        String text = "";
        try {
            text = new String(IOUtils.toByteArray(getClass().getResourceAsStream("/maedit/about.html")));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Could not read about.html.", ex);
        }
        return text;
    }

    private void openUrlInDefaultBrowser(URL url) {
        if (!Desktop.isDesktopSupported())
            return;
        Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.BROWSE))
            try {
                desktop.browse(url.toURI());
            } catch (IOException | URISyntaxException exc) {
                LOGGER.log(Level.SEVERE, "Could not start browser.", exc);
            }
    }
}
