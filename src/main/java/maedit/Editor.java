package maedit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.FileDialog;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class Editor {

    private final static Logger LOGGER = Logger.getLogger(Editor.class.getName());

    JFrame frame;
    String fileName;
    MarkdownPane markdownPane;
    HtmlPane htmlPane;
    MarkdownToHtml htmlRenderer;
    AboutBox aboutBox;

    public static void main(String[] args) throws IOException {
        new Editor().init();
    }

    public Editor() {
        frame = new JFrame("Maed It!");
        markdownPane = new MarkdownPane();
        htmlPane = new HtmlPane();
        htmlRenderer = new MarkdownToHtml();
        aboutBox = new AboutBox();
    }

    public void init() {
        frame.setJMenuBar(buildMenuBar());
        frame.getContentPane().add(buildSplitPane(), BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        new ContentSynchronizer(markdownPane.getDocument(), htmlRenderer, htmlPane.getTextTarget()).init();
        new ScrollBarSynchronizer(markdownPane.getScrollPane().getVerticalScrollBar(),
                htmlPane.getScrollPane().getVerticalScrollBar()).init();

        setIcon();
    }

    private void setIcon() {
        try {
            frame.setIconImage(ImageIO.read(getClass().getResource("/maedit/icon.png")));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Could not read icon image.", ex);
        }
    }
    
    private JMenuBar buildMenuBar() {
        JMenu fileMenu = new JMenu("File");
        // fileMenu.setMnemonic(KeyEvent.VK_F); // ALT+chars don't play nice with the text area

        fileMenu.add(buildMenuItem("Open", KeyEvent.VK_O, this::open));
        fileMenu.add(buildMenuItem("Save", KeyEvent.VK_S, this::save));
        fileMenu.add(buildMenuItem("Save as", KeyEvent.VK_A, this::saveAs));
        fileMenu.add(buildMenuItem("Export", KeyEvent.VK_E, this::export));

        JMenu helpMenu = new JMenu("Help");
        // helpMenu.setMnemonic(KeyEvent.VK_H);

        helpMenu.add(buildMenuItem("About Maed It!", KeyEvent.VK_M, this::about));

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);

        return menuBar;
    }

    private JMenuItem buildMenuItem(String name, int mnemonic, VoidMethod action) {
        JMenuItem menuItem = new JMenuItem(name, mnemonic);
        menuItem.addActionListener(createActionListener(name, action));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                mnemonic, ActionEvent.CTRL_MASK));
        return menuItem;
    }

    private JSplitPane buildSplitPane() {
        JSplitPane splitPane = new JSplitPane();
        splitPane.setPreferredSize(new Dimension(640, 400));
        splitPane.setLeftComponent(markdownPane.getScrollPane());
        splitPane.setRightComponent(htmlPane.getScrollPane());
        splitPane.setDividerLocation(320);
        return splitPane;
    }

    interface VoidMethod {
        void apply();
    }

    private ActionListener createActionListener(final String operation, final VoidMethod func) {
        return (ActionEvent event) -> {
            try {
                func.apply();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Operation failed: " + operation, e);
                JOptionPane.showMessageDialog(frame, e.getMessage(), operation + " Failed", JOptionPane.ERROR_MESSAGE);
            }
        };
    }

    private void save() {
        if (fileName == null) {
            saveAs();
        } else {
            writeTextToFile(markdownPane.getText(), fileName);
        }
    }

    private void saveAs() {
        fileName = getFileFromDialog("Save Markdown", FileDialog.SAVE, "*.md");
        if (fileName != null) {
            writeTextToFile(markdownPane.getText(), fileName);
        }
    }

    private void export() {
        String htmlFileName = getFileFromDialog("Export HTML", FileDialog.SAVE, "*.html");
        if (htmlFileName != null) {
            writeTextToFile(htmlRenderer.getHtml(markdownPane.getText()), htmlFileName);
        }
    }

    private void writeTextToFile(String text, String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(text);
            writer.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void open() {
        String openFileName = getFileFromDialog("Open Markdown", FileDialog.LOAD, "*.md");
        if (openFileName == null) {
            return;
        }
        fileName = openFileName;
        try {
            markdownPane.setText(new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    String getFileFromDialog(String title, int mode, String filter) {
        FileDialog fileDialog = new FileDialog(frame, title, mode);
        fileDialog.setFile(filter);
        fileDialog.setVisible(true);

        return getPath(fileDialog);
    }

    String getPath(FileDialog dialog) {
        String path = null;
        if (dialog.getFile() != null) {
            path = dialog.getDirectory() + File.separator + dialog.getFile();
        }
        LOGGER.log(Level.INFO, "Path selected: {0}", path);
        return path;
    }

    private void about() {
        JOptionPane.showMessageDialog(frame, aboutBox.editorPane, "About Maed It!",
                JOptionPane.PLAIN_MESSAGE);
    }
}
