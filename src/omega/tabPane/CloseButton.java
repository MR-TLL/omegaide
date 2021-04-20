package omega.tabPane;
import omega.tree.*;
import omega.popup.OPopupWindow;
import java.io.File;
import omega.Screen;
import omega.utils.UIManager;
import omega.comp.TextComp;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
public class CloseButton extends JComponent {

	private CloseAction closeAction;
	
	public CloseButton() {
		setFocusable(false);
		UIManager.setData(this);
          setForeground(UIManager.TOOLMENU_COLOR1);
		final Color fore = getForeground();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setForeground(Color.ORANGE);
				repaint();
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setForeground(fore);
				repaint();
			}
			@Override
			public void mousePressed(MouseEvent e) {
				setForeground(Color.GREEN);
				repaint();
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				setForeground(fore);
				repaint();
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				closeAction.onClose();
			}
		});
	}
	
	public CloseButton setOnClose(CloseAction closeAction) {
		this.closeAction = closeAction;
		return this;
	}

	@Override
	public void paint(Graphics g2D) {
		Graphics2D g = (Graphics2D)g2D;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(getForeground());
		g.setFont(getFont());
		g.drawString("X", 4, getFont().getSize()-3);
	}

	@Override
	public void setFont(Font f) {
		super.setFont(f);
		setPreferredSize(new Dimension(16, 16));
		setSize(getPreferredSize());
	}

	public interface CloseAction {
		void onClose();
	}
	
	public interface FocusAction {
		void onFocus();
	}

	private static LinkedList<JTextArea> areas = new LinkedList<>();

	public static JPanel create(Component c, String name, CloseAction closeAction, FocusAction focusAction, String toolTip, OPopupWindow popUp) {
		CloseButton closeButton = new CloseButton().setOnClose(closeAction);
		closeButton.setFont(new Font("Ubuntu", Font.BOLD, 14));

		JTextArea textField = new JTextArea(!toolTip.startsWith("src") ? ("{" + name + "}") : name);
		textField.setForeground(Branch.getColor(name));
          textField.setBackground(UIManager.c2);
		final Color FORE = textField.getForeground();
		final MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				areas.forEach((a)->{
					if(a != textField)
						a.setForeground(FORE);
					else
						a.setForeground(UIManager.glow);
					a.repaint();
				});
				focusAction.onFocus();
			}
		};
		textField.setBorder(null);
		textField.setToolTipText(toolTip);
		textField.setEditable(false);
		textField.setFont(closeButton.getFont());
		textField.addMouseListener(mouseAdapter);
		areas.add(textField);
		
		String baseName = getBaseName(name);
          TextComp iconButton = null;
          if(UIManager.isDarkMode())
               iconButton = new TextComp(baseName, UIManager.c1, UIManager.c2, UIManager.c3, ()->{});
          else {
               Color alpha = new Color(FORE.getRed(), FORE.getGreen(), FORE.getBlue(), 40);
               iconButton = new TextComp(baseName, textField.getBackground(), alpha, FORE, ()->{});
          }
		iconButton.setPreferredSize(new Dimension(baseName.length() > 2 ? (baseName.length() > 3 ? 40 : 25) : 20, 16));
		iconButton.setFont(omega.settings.Screen.PX16);
		
		if(popUp != null) {
			iconButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					popUp.setVisible(true);
                         popUp.setLocation(e.getLocationOnScreen());
				}
			});
		}
		JPanel panel = new JPanel();
		UIManager.setData(panel);
		panel.setLayout(new FlowLayout());
		panel.add(iconButton);
		panel.add(textField);
		panel.add(closeButton);
		panel.addMouseListener(mouseAdapter);
		c.addMouseListener(mouseAdapter);
		areas.forEach((a)->{
			if(a != textField) {
				a.setForeground(FORE);
			}
			else
				a.setForeground(UIManager.glow);
			a.repaint();
		});
		return panel;
	}
	
	public static String getBaseName(String ext) {
		if(ext.equals("Compilation"))
			return "JVM";

          if(ext.equals("Building"))
               return "IDE";

          else if(ext.equals("Terminal"))
               return "Shell";
               
          else if(ext.equals("File Operation"))
               return "Task";

          if(ext.contains("Run("))
               return "JVM";
               
          if(ext.contains("Run ") || ext.contains("Run") || ext.contains("Build"))
               return "IDE";

          if(!ext.contains("."))
               return "?";

          if(ext.equals(".projectInfo") || ext.equals(".sources")|| ext.equals(".args") || ext.equals(".natives") || ext.equals(".dependencies") || ext.equals(".modules") || ext.equals(".ui") || ext.equals(".content") || ext.equals(".resources"))
               return "IDE";

		ext = ext.substring(ext.lastIndexOf('.'));
		
		if(ext.equals(".java") || ext.equals(".class"))
			return "J";
          else if(ext.equals(".py"))
               return "Py";
          else if(ext.equals(".groovy"))
               return "G";
		else if(ext.equals(".js") || ext.equals(".html"))
			return "Web";
		else if(ext.equals(".rs"))
			return "R";
		else if(ext.equals(".txt"))
			return "T";
		else if(ext.equals(".exe") || ext.equals(".cmd") || ext.equals(".bat") || ext.equals(".dll"))
			return "Win";
		else if(ext.equals(".xml") || ext.equals(".fxml"))
			return "Xml";
		else if(ext.equals(".dmg"))
			return "Mac";
		else if(ext.equals(".sh") || ext.equals(".run"))
			return "Linux";
		return "?";
	}

}
