package omega.snippet;
import omega.utils.UIManager;
import omega.utils.IconManager;
import omega.Screen;
import omega.launcher.Door;
import omega.utils.Editor;
import omega.comp.TextComp;
import java.io.File;
import Omega.IDE;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import static omega.utils.UIManager.*;
public class SnippetView extends JDialog {
	private static final Font FONT = new Font("Ubuntu Mono", Font.BOLD, 16);
	private RSyntaxTextArea textArea;
	private JTextField textField;
	private LinkedList<Door> doors = new LinkedList<>();
	private BufferedImage image;
	private JPanel leftPanel;
	private int block;
	private volatile Snippet snip;
	private JScrollPane pane;
	public SnippetView(omega.Screen screen){
		super(screen);
		setUndecorated(true);
		setIconImage(screen.getIconImage());
		setModal(false);
		setTitle("Snippet Manager");
		setSize(700, 605);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(null);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				saveView();
			}
		});
		init();
	}

	private void init(){
		//Door System
          image = IconManager.buildImage;
		leftPanel = new JPanel(null);
		pane = new JScrollPane(leftPanel);
		pane.setBounds(0, 0, 250, getHeight());
		add(pane);

		//View System
		textField = new JTextField();
		textField.setBounds(250, 0, getWidth() - 250 - 120, 40);
		textField.setToolTipText("Enter Snippet Name with alphabets, numbers and symbols(except \';\') without whitespaces");
		add(textField);

		TextComp add = new TextComp("+", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{
               if(textField.getText().contains(" ") || textField.getText().equals("")) {
                    textField.setText("See Tooltip for Naming the Snippets");
                    return;
               }
               SnippetBase.add(textField.getText(), textArea.getText(), textArea.getCaretPosition(), textArea.getCaretLineNumber());
               setView(SnippetBase.getAll().getLast());
               loadDoors();
	     });
		add.setBounds(getWidth() - 120, 0, 40, 40);
		add.setFont(FONT);
          add.setArc(0, 0);
		add(add);

		TextComp rem = new TextComp("-", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, ()->{
               SnippetBase.remove(textField.getText());
               loadDoors();
               textField.setText("");
               textArea.setText("");
               this.snip = null;
	     });
		rem.setBounds(getWidth() - 80, 0, 40, 40);
		rem.setFont(FONT);
          rem.setArc(0, 0);
		add(rem);

		TextComp close = new TextComp("x", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->{
               dispose();
               saveView();
	     });
		close.setBounds(getWidth() - 40, 0, 40, 40);
		close.setFont(FONT);
          close.setArc(0, 0);
		add(close);

		textArea = new RSyntaxTextArea();
		textArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVA);
		textArea.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				saveView();
			}
		});
		if(!omega.utils.UIManager.isDarkMode()) {
			textField.setBackground(c2);
			textField.setForeground(glow);
			leftPanel.setBackground(c2);
			try {
			     Theme.load(Editor.class.getResourceAsStream("/idea.xml")).apply(textArea);
			}catch(Exception e) {}
		}
		else {
			omega.utils.UIManager.setData(textField);
			omega.utils.UIManager.setData(leftPanel);
			try {
			     Theme.load(Editor.class.getResourceAsStream("/dark.xml")).apply(textArea);
			}catch(Exception e) {}
		}
		textField.setFont(new Font(UIManager.fontName, Font.BOLD, UIManager.fontSize));
		RTextScrollPane scrollPane = new RTextScrollPane(textArea);
		scrollPane.setBounds(250, 40, getWidth() - 250, getHeight() - 40);
		add(scrollPane);
	}
	
	public void setView(Snippet snip) {
		saveView();
		this.snip = snip;
		textField.setText(snip.base);
		textArea.setText(snip.code);
		textArea.setCaretPosition(snip.caret);
	}
	
	public void saveView() {
		if(snip == null) return;
		if(!snip.base.equals(textField.getText())) return;
		snip.base = textField.getText();
		snip.code = textArea.getText();
		snip.caret = textArea.getCaretPosition();
		snip.line = textArea.getCaretLineNumber();
		SnippetBase.save();
	}

	private void loadDoors() {
		doors.forEach(leftPanel::remove);
		doors.clear();
		block = -40;
		for(Snippet snip : SnippetBase.getAll()) {
			Door door = new Door(File.separator + "\\" + File.separator + snip.base, image, ()->setView(snip));
			door.setBounds(0, block += 40, 250, 40);
               door.setBackground(c2);
               door.setForeground(TOOLMENU_COLOR1);
			doors.add(door);
			leftPanel.add(door);
		}
		if(block == -40) return;
		leftPanel.setPreferredSize(new Dimension(250, block + 40));
		pane.getVerticalScrollBar().setVisible(true);
		pane.getVerticalScrollBar().setValue(0);
		repaint();
	}
	
	@Override
	public void setVisible(boolean value) {
		super.setVisible(value);
		if(value) {
			loadDoors();
		}
	}
}