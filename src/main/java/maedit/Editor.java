package maedit;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class Editor {
    private final static Logger LOGGER = Logger.getLogger(Editor.class.getName());

    JFrame frame;
    String fileName;
    MarkdownPane markdownPane;
    HtmlPane[] htmlPanes;
    int activeHtmlPane = 0;

    MarkdownToHtml htmlRenderer;
    FileIO fileIO;
    ViewSynchronizer viewSynchronizer;
    AboutBox aboutBox;
    
    JPanel rightPanel;
    
    RefreshThread refreshThread;

    public static void main(String[] args) throws IOException {
        new Editor().init();
    }

    public Editor() {
        frame = new JFrame("Maed It!");
        markdownPane = new MarkdownPane();
        htmlPanes = new HtmlPane[2];
        htmlPanes[0] = new HtmlPane();
        htmlPanes[1] = new HtmlPane();
        
        htmlRenderer = new MarkdownToHtml();
        fileIO = new FileIO(frame, markdownPane, htmlRenderer);
        aboutBox = new AboutBox();
        refreshThread = new RefreshThread();
    }

    public void init() {
        frame.setJMenuBar(buildMenuBar());
        frame.getContentPane().add(buildSplitPane(), BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        viewSynchronizer = new ViewSynchronizer(markdownPane, htmlPanes[0]);
        viewSynchronizer.init();
    	refreshThread.start();
        markdownPane.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
            	refreshThread.dirty();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            	refreshThread.dirty();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            	refreshThread.dirty();
            }
        });

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
        fileMenu.add(buildMenuItem("Open", KeyEvent.VK_O, fileIO::open));
        fileMenu.add(buildMenuItem("Save", KeyEvent.VK_S, fileIO::save));
        fileMenu.add(buildMenuItem("Save as", KeyEvent.VK_A, fileIO::saveAs));
        fileMenu.add(buildMenuItem("Export HTML", KeyEvent.VK_E, fileIO::exportHtml));

        JMenu helpMenu = new JMenu("Help");
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
        splitPane.setRightComponent(buildRightPanel());
        splitPane.setDividerLocation(320);
        return splitPane;
    }
    
    private JPanel buildRightPanel() {
    	rightPanel = new JPanel();
    	CardLayout cardLayout = new CardLayout();
    	rightPanel.setLayout(cardLayout);
    	rightPanel.add(htmlPanes[0].getScrollPane());
    	rightPanel.add(htmlPanes[1].getScrollPane());
    	cardLayout.first(rightPanel);
    	
    	return rightPanel;
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

    private void about() {
        JOptionPane.showMessageDialog(frame, aboutBox.editorPane, "About Maed It!",
                JOptionPane.PLAIN_MESSAGE);
    }
    
    class RefreshThread extends Thread {
    	boolean dirty = false;
    	
		@Override
		public void run() {
			while (true) {
				if (dirty)
					try {
						syncMarkdownToHtml();
					} catch (BadLocationException e) {
						LOGGER.log(Level.WARNING, "Html conversion failed", e);
					}
				else
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
			}
		}
		
		public synchronized void dirty() {
			dirty = true;
		}
		
		public synchronized void clean() {
			dirty = false;
		}
    	
		protected void syncMarkdownToHtml() throws BadLocationException {
			clean();
			var md = markdownPane.getDocument();
			String html = "<html><body>"
        			+ htmlRenderer.getHtmlAsString(md.getText(0, md.getLength()))
        			+ "</body></html>";
			int inactive = activeHtmlPane == 0 ? 1 : 0;
			var rect = htmlPanes[activeHtmlPane].getTextTarget().getVisibleRect();
			htmlPanes[inactive].getTextTarget().setText(html);			
			
			htmlPanes[inactive].getTextTarget().scrollRectToVisible(rect);
			SwingUtilities.invokeLater(() -> {
				viewSynchronizer.setHtmlPane(htmlPanes[inactive]);
				var layout = (CardLayout) rightPanel.getLayout();
				if (activeHtmlPane == 0)
					layout.last(rightPanel);
				else
					layout.first(rightPanel);

				activeHtmlPane = inactive;
			});
		}
    }

}


