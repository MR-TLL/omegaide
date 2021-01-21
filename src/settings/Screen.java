package settings;
import static ide.utils.UIManager.*;

import java.awt.Color;
import java.awt.Font;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ide.utils.DataManager;
import ide.utils.UIManager;
import say.swing.JFontChooser;
import settings.comp.Comp;
import settings.comp.TextComp;
import ui.SDKSelector;
public class Screen extends JDialog {
	//Light + Dark Mode
	public static final Color color1 = new Color(255, 0, 0, 130);
	public static final Color color2 = new Color(255, 0, 0, 220);
	public static final Color color3 = new Color(255, 255, 200, 220);
	public static final Color color4 = new Color(0, 0, 255, 130);
	public static final Color color5 = new Color(0, 0, 255, 220);
	public static final Color color6 = new Color(0, 255, 0, 130);
	public static final Color color7 = new Color(0, 255, 0, 220);

	//Fonts
	public static final Font PX14 = new Font("Ubuntu Mono", Font.BOLD, 14);
	public static final Font PX16 = new Font("Ubuntu Mono", Font.BOLD, 16);
	public static final Font PX18 = new Font("Ubuntu Mono", Font.BOLD, 18);
	public static final Font PX28 = new Font("Ubuntu Mono", Font.BOLD, 28);

	//Components
	public Comp projectComp;
	public Comp jdkComp;
	public Comp classPathComp;
	public Comp modulePathComp;
	public TextComp compileComp;
	public TextComp runComp;
	public JTextField compileTimeField;
	public JTextField runTimeField;

	public Comp ideComp;
	public Comp javaComp;
	public Comp suggestionComp;
	public Comp asteriskComp;
	public Comp fontComp;
	public Comp whatsNewComp;

	public Comp closeComp;
	public Comp applyComp;

	public LinkedList<JComponent> projectComps = new LinkedList<>();
	public LinkedList<JComponent> ideComps = new LinkedList<>();

	
	//Temporary Changes
	public volatile boolean realTime;
	public volatile boolean asterisk;
	public static String lastPath = null;
	
	public Screen(ide.Screen window){
		super(window, "Set up Omega IDE", true);
		setIconImage(window.getIconImage());
		setUndecorated(true);
		setSize(500, 310);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		init();
	}

	public void init(){
		JPanel contentPanel = new JPanel(null);
		setContentPane(contentPanel);
		setLayout(null);
		contentPanel.setBackground(c2);

		//Project Components
		JFileChooser fileChooser = new JFileChooser();
		SDKSelector sdkSelector = new SDKSelector(ide.Screen.getScreen());

		projectComp = new Comp("Project Specific Settings", c1, c2, c3, ()->{});
		projectComp.setBounds(0, 0, getWidth(), 50);
		projectComp.setFont(PX18);
		projectComp.setClickable(false);
		projectComp.createRightArrow(getWidth() - 40, projectComp.getHeight()/2 - 25/2, 25, 25, ()->setView("IDE"));
		projectComp.rightComp.setFont(PX18);
		add(projectComp);
		projectComps.add(projectComp);

		jdkComp = new Comp("Select a Java SE Environment", c1, c2, c3, ()->{
			sdkSelector.setVisible(true);
			String sel = sdkSelector.getSelection();
			if(sel != null) {
				if(ide.Screen.getFileView().getProjectManager() != null) {
					lastPath = sel;
					jdkComp.setText(sel);
				}
			}
		});
		jdkComp.setBounds(0, 50, getWidth(), 40);
		jdkComp.setFont(PX16);
		add(jdkComp);
		projectComps.add(jdkComp);

		classPathComp = new Comp("Manage ClassPath", c1, c2, c3, ()->{
			ide.Screen.getFileView().getDependencyView().setVisible(true);
		});
		classPathComp.setBounds(0, 90, getWidth(), 50);
		classPathComp.setFont(PX16);
		add(classPathComp);
		projectComps.add(classPathComp);

		modulePathComp = new Comp("Manage ModulePath", c1, c2, c3, ()->{
			ide.Screen.getFileView().getModuleView().setVisible(true);
		});
		modulePathComp.setBounds(0, 140, getWidth(), 50);
		modulePathComp.setFont(PX16);
		add(modulePathComp);
		projectComps.add(modulePathComp);

		compileComp = new TextComp("Compile Time", c1, c2, c3, ()->{});
		compileComp.setClickable(false);
		compileComp.setBounds(0, 190, 100, 40);
		compileComp.setFont(PX14);
		compileComp.arcX = 0;
		compileComp.arcY = 0;
		add(compileComp);
		projectComps.add(compileComp);

		compileTimeField = new JTextField();
		compileTimeField.setToolTipText("Only One Compile Time Argument Allowed starting with \'-\'");
		compileTimeField.setBounds(100, 190, getWidth() - 100, 40);
		compileTimeField.setFont(PX14);
		add(compileTimeField);
		projectComps.add(compileTimeField);

		runComp = new TextComp("Run Time", c1, c2, c3, ()->{});
		runComp.setClickable(false);
		runComp.setBounds(0, 230, 100, 40);
		runComp.setFont(PX14);
		runComp.arcX = 0;
		runComp.arcY = 0;
		add(runComp);
		projectComps.add(runComp);

		runTimeField = new JTextField();
		runTimeField.setToolTipText("Only One Run Time Argument Allowed starting with \'-\'");
		runTimeField.setBounds(100, 230, getWidth() - 100, 40);
		runTimeField.setFont(PX14);
		add(runTimeField);
		projectComps.add(runTimeField);

		//IDE Components
		ideComp = new Comp("Universal Settings", c1, c2, c3, ()->{});
		ideComp.setBounds(0, 0, getWidth(), 50);
		ideComp.setFont(PX18);
		ideComp.setClickable(false);
		ideComp.createLeftArrow(15, ideComp.getHeight()/2 - 25/2, 25, 25, ()->setView("PROJECT"));
		ideComp.leftComp.setFont(PX18);
		add(ideComp);
		ideComps.add(ideComp);

		javaComp = new Comp("Select Folder Containing the Java SE(s)", c1, c2, c3, ()->{
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setDialogTitle("Choose Path to the folder contaning the jdks");
			fileChooser.setApproveButtonText("Select This As Default Java Root");
			int res = fileChooser.showOpenDialog(this);
			if(res == JFileChooser.APPROVE_OPTION) {
				javaComp.setToolTipText(fileChooser.getSelectedFile().getAbsolutePath());
				String text = javaComp.getToolTipText();
				text = text.substring(text.lastIndexOf(java.io.File.separator) + 1);
				javaComp.setText("Java SE(s) location : " + text);
				DataManager.setPathToJava(fileChooser.getSelectedFile().getAbsolutePath());
			}
		});
		javaComp.setBounds(0, 50, getWidth(), 40);
		javaComp.setFont(PX16);
		add(javaComp);
		ideComps.add(javaComp);

		suggestionComp = new Comp("", c1, c2, c3, ()->{});
		suggestionComp.setBounds(0, 90, getWidth(), 50);
		suggestionComp.setFont(PX16);
		suggestionComp.createToggle(DataManager.isContentAssistRealTime(), "Content Assist is Real-Time", "Content Assist is Stopped",
				(value)->{
					realTime = value;
				});
		add(suggestionComp);
		ideComps.add(suggestionComp);

		asteriskComp = new Comp("", c1, c2, c3, ()->{});
		asteriskComp.setBounds(0, 140, getWidth(), 50);
		asteriskComp.setFont(PX16);
		asteriskComp.createToggle(DataManager.isUsingStarImports(), "Using asterisk imports(import package.*)", "Using named Imports(import package.ClassName)", 
				(value)->{
					asterisk = value;
				});
		add(asteriskComp);
		ideComps.add(asteriskComp);

		JFontChooser fontC = new JFontChooser();
		fontComp = new Comp("Change Editor Font\'s Name and Size", c1, c2, c3, ()->{
			UIManager.setData(fontC);
			fontC.setSelectedFontStyle(Font.BOLD);
			fontC.setSelectedFont(new Font(UIManager.fontName, Font.BOLD, UIManager.fontSize));
			int res = fontC.showDialog(this);
			if(res == JFontChooser.OK_OPTION) {
				Font font = fontC.getSelectedFont();
				UIManager.fontName = font.getName();
				UIManager.fontSize = font.getSize();
				ide.Screen.getFileView().getScreen().getUIManager().save();
				ide.Screen.getFileView().getScreen().loadThemes();
			}
		});
		fontComp.setBounds(0, 190, getWidth(), 40);
		fontComp.setFont(PX16);
		add(fontComp);
		ideComps.add(fontComp);

		whatsNewComp = new Comp("Whats New in Omega IDE " + ide.Screen.VERSION, c1, c2, c3, ()->{
			ide.utils.ToolMenu.infoScreen.setVisible(true);
		});
		whatsNewComp.setBounds(0, 230, getWidth(), 40);
		whatsNewComp.setFont(PX16);
		add(whatsNewComp);
		ideComps.add(whatsNewComp);

		closeComp = new Comp("Close", c1, c2, c3, ()->setVisible(false));
		closeComp.setFont(PX18);
		closeComp.setBounds(0, 270, getWidth()/2, 40);
		add(closeComp);

		applyComp = new Comp("Apply", c1, c2, c3, this::apply);
		applyComp.setFont(PX18);
		applyComp.setBounds(getWidth()/2, 270, getWidth()/2, 40);
		add(applyComp);
	}
	
	public void apply() {
		//Code Assist Panel
		DataManager.setContentAssistRealTime(realTime);
		DataManager.setUseStarImports(asterisk);
		ide.Screen.getScreen().getToolMenu().contentComp.setToolTipText(DataManager.isContentAssistRealTime() ? "Content Assist is ON" : "Content Assist is Stopped");
		ide.Screen.getScreen().getToolMenu().asteriskComp.setToolTipText(DataManager.isUsingStarImports() ? "Using Asterisk Imports" : "Using Named Imports");
		ide.Screen.getScreen().getToolMenu().contentComp.repaint();
		ide.Screen.getScreen().getToolMenu().asteriskComp.repaint();
		//Project
		ide.Screen.getFileView().getProjectManager().setJDKPath(lastPath);
		//Arguments
		ide.Screen.getFileView().getProjectManager().compile_time_args = compileTimeField.getText();
		ide.Screen.getFileView().getProjectManager().run_time_args = runTimeField.getText();
	}
	
	public void load() {
          try{
     		suggestionComp.setToggle(realTime = DataManager.isContentAssistRealTime());
     		asteriskComp.setToggle(asterisk = DataManager.isUsingStarImports());
     		if(ide.Screen.getFileView().getProjectManager().jdkPath != null)
     			jdkComp.setText("Using JDK v" + importIO.JDKReader.version);
     		else
     			jdkComp.setText("Select a Java SE Environment");
     		String text = DataManager.getPathToJava();
     		if(text == null) {
     			javaComp.setText("Select Folder Containing the Java SE(s)");
     		}
     		else {
     			text = text.substring(text.lastIndexOf(java.io.File.separator) + 1);
     			javaComp.setText("Java SE(s) location : " + text);
     		}
     		compileTimeField.setText(ide.Screen.getFileView().getProjectManager().compile_time_args != null ? ide.Screen.getFileView().getProjectManager().compile_time_args : "");
     		runTimeField.setText(ide.Screen.getFileView().getProjectManager().run_time_args != null ? ide.Screen.getFileView().getProjectManager().run_time_args : "");
          }
          catch(Exception ec){}
	}

	public void setView(String name){
		load();
		if(name.equals("PROJECT")){
			ideComps.forEach(e->e.setVisible(false));
			projectComps.forEach(e->e.setVisible(true));
			repaint();
		}
		else{
			projectComps.forEach(e->e.setVisible(false));
			ideComps.forEach(e->e.setVisible(true));
			repaint();
		}
	}
	
	@Override
	public void setVisible(boolean value) {
		if(value) {
			setView("PROJECT");
		}
		super.setVisible(value);
	}
}