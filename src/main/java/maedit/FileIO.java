package maedit;

import java.awt.FileDialog;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;


public class FileIO {
	private final static Logger LOGGER = Logger.getLogger(FileIO.class.getName());
	
	String fileName;
	MarkdownToHtml htmlRenderer;
	JFrame frame;
	MarkdownPane markdownPane;
	
	public FileIO(JFrame frame, MarkdownPane markdownPane, MarkdownToHtml htmlRenderer) {
		this.frame = frame;
		this.markdownPane = markdownPane;
		this.htmlRenderer = htmlRenderer;
	}
	
	public void save() {
        if (fileName == null)
            saveAs();
        else
            writeTextToFile(markdownPane.getText(), fileName);
    }
    
	public void saveAs() {
        fileName = getFileFromDialog("Save Markdown", FileDialog.SAVE, "*.md");
        if (fileName != null)
            writeTextToFile(markdownPane.getText(), fileName);
    }

    public void exportHtml() {
    	String htmlFileName = getFileFromDialog("Export HTML", FileDialog.SAVE, "*.html");
        if (htmlFileName != null)
            writeTextToFile(htmlRenderer.getHtmlAsString(markdownPane.getText()), htmlFileName);
    }
    
    private void writeTextToFile(String text, String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(text);
            writer.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void open() {
        String openFileName = getFileFromDialog("Open Markdown", FileDialog.LOAD, "*.md");
        if (openFileName == null)
            return;

        fileName = openFileName;
        try {
            markdownPane.setText(new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String getFileFromDialog(String title, int mode, String filter) {
        FileDialog fileDialog = new FileDialog(frame, title, mode);
        fileDialog.setFile(filter);
        fileDialog.setVisible(true);

        return getPath(fileDialog);
    }

    private String getPath(FileDialog dialog) {
        String path = null;
        if (dialog.getFile() != null)
            path = dialog.getDirectory() + File.separator + dialog.getFile();

        LOGGER.log(Level.INFO, "Path selected: {0}", path);
        return path;
    }
}
