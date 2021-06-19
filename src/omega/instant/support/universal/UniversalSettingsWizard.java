package omega.instant.support.universal;
import omega.*;
import java.io.*;
import omega.utils.*;
import java.awt.*;
import java.util.*;
import omega.comp.*;
import javax.swing.*;
import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class UniversalSettingsWizard extends JDialog{
	private TextComp titleComp;
	private NoCaretField runField;
	private NoCaretField compileField;
	private TextComp runWorkDirComp;
	private TextComp compileWorkDirComp;
	private TextComp listMakerComp;
	public LinkedList<ListMaker> lists = new LinkedList<>();
	private JScrollPane scrollPane;
	private FlexPanel panel;
	private int block = 0;
	public UniversalSettingsWizard(Window window){
		super(window, "Universal Settings Wizard");
		setModal(true);
		setUndecorated(true);
		setResizable(false);
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		setResizable(false);
		setSize(600, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		init();
	}
	public void init(){
		FileSelectionDialog fc = new FileSelectionDialog(this);
		fc.setTitle("Select Working Directory");
		
		titleComp = new TextComp("Universal Settings Wizard", TOOLMENU_COLOR3, c2, c2, null);
		titleComp.setBounds(0, 0, getWidth(), 30);
		titleComp.setFont(PX14);
		titleComp.attachDragger(this);
		titleComp.setArc(0, 0);
		titleComp.setClickable(false);
		add(titleComp);
		
		TextComp label0 = new TextComp("Run Command", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
		label0.setBounds(10, 50, 150, 25);
		label0.setFont(PX14);
		label0.setClickable(false);
		add(label0);
		
		runField = new NoCaretField("", "Enter Run Command", TOOLMENU_COLOR3, c2, TOOLMENU_COLOR2);
		runField.setBounds(180, 50, getWidth() - 320, 25);
		runField.setFont(PX14);
		add(runField);
		
		runWorkDirComp = new TextComp("Working Directory", "Choose Working Directory When Building", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR2, ()->{
			fc.setCurrentDirectory(new File(omega.Screen.getFileView().getProjectPath()));
			LinkedList<File> selections = fc.selectDirectories();
			if(!selections.isEmpty()){
				runWorkDirComp.setToolTipText(selections.get(0).getAbsolutePath());
				runWorkDirComp.setText(runWorkDirComp.getToolTipText().substring(runWorkDirComp.getToolTipText().lastIndexOf(File.separator) + 1));
			}
		});
		runWorkDirComp.setLocation(runField.getX() + runField.getWidth() + 5, 50);
		runWorkDirComp.setSize(getWidth() - runWorkDirComp.getX() - 10, 25);
		runWorkDirComp.setFont(PX14);
		add(runWorkDirComp);
		
		TextComp label1 = new TextComp("Compile Command", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
		label1.setBounds(10, 100, 150, 25);
		label1.setFont(PX14);
		label1.setClickable(false);
		add(label1);
		
		compileField = new NoCaretField("", "Enter Compile Command", TOOLMENU_COLOR3, c2, TOOLMENU_COLOR2);
		compileField.setBounds(180, 100, getWidth() - 320, 25);
		compileField.setFont(PX14);
		add(compileField);
		
		compileWorkDirComp = new TextComp("Working Directory", "Choose Working Directory When Running", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR2, ()->{
			fc.setCurrentDirectory(new File(Screen.getFileView().getProjectPath()));
			LinkedList<File> selections = fc.selectDirectories();
			if(!selections.isEmpty()){
				compileWorkDirComp.setToolTipText(selections.get(0).getAbsolutePath());
				compileWorkDirComp.setText(compileWorkDirComp.getToolTipText().substring(compileWorkDirComp.getToolTipText().lastIndexOf(File.separator) + 1));
			}
		});
		compileWorkDirComp.setLocation(runField.getX() + runField.getWidth() + 5, 100);
		compileWorkDirComp.setSize(getWidth() - compileWorkDirComp.getX() - 10, 25);
		compileWorkDirComp.setFont(PX14);
		add(compileWorkDirComp);
		TextComp closeComp = new TextComp("Close", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, this::dispose);
		closeComp.setBounds(getWidth()/2 - 100 - 110, 150, 100, 25);
		closeComp.setFont(PX14);
		add(closeComp);
		listMakerComp = new TextComp("Add a List Maker", TOOLMENU_COLOR4_SHADE, c2, TOOLMENU_COLOR4, this::addList);
		listMakerComp.setBounds(getWidth()/2 - 100, 150, 200, 25);
		listMakerComp.setFont(PX14);
		add(listMakerComp);
		TextComp applyComp = new TextComp("Apply", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, this::apply);
		applyComp.setBounds(getWidth()/2 - 100 + 210, 150, 100, 25);
		applyComp.setFont(PX14);
		add(applyComp);
		
		scrollPane = new JScrollPane(panel = new FlexPanel(null, c2, c2));
		scrollPane.setBounds(0, 200, 600, 300);
		scrollPane.setBackground(c2);
		add(scrollPane);
	}
	public void addList(){
		ListMaker listMaker = new ListMaker();
		listMaker.setLocation(0, block);
		panel.add(listMaker);
		lists.add(listMaker);
		block += 30;
		panel.setPreferredSize(new Dimension(600, block));
		
		setSize(600, lists.isEmpty() ? 180 : (200 + (lists.size() > 6 ? 300 : (lists.size() * 30))));
		setLocationRelativeTo(null);
		if(getHeight() >= 500){
			scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
			scrollPane.getVerticalScrollBar().setVisible(true);
			panel.repaint();
		}
	}
	public void apply(){
		boolean checkPassed = true;
		for(ListMaker list : lists){
			if(list.isEnabled()){
				if(!list.validateListMaker())
					checkPassed = false;
			}
		}
		if(!checkPassed)
			return;
		Screen.getFileView().getArgumentManager().compile_time_args = compileField.getText();
		Screen.getFileView().getArgumentManager().run_time_args = runField.getText();
		Screen.getFileView().getArgumentManager().compileDir = compileWorkDirComp.getToolTipText();
		Screen.getFileView().getArgumentManager().runDir = runWorkDirComp.getToolTipText();
		Screen.getFileView().getArgumentManager().units.clear();
		lists.forEach(list->{
			if(list.validateListMaker() && list.isEnabled()){
				Screen.getFileView().getArgumentManager().units.add(list);
			}
		});
		Screen.getFileView().getArgumentManager().save();
	}
	@Override
	public void setVisible(boolean value){
		if(value){
			block = 0;
			lists.forEach(panel::remove);
			lists.clear();
               compileField.setText(Screen.getFileView().getArgumentManager().compile_time_args);
               runField.setText(Screen.getFileView().getArgumentManager().run_time_args);
               compileWorkDirComp.setToolTipText(Screen.getFileView().getArgumentManager().compileDir.equals("") ? "Working Directory" : Screen.getFileView().getArgumentManager().compileDir);
               runWorkDirComp.setToolTipText(Screen.getFileView().getArgumentManager().runDir.equals("") ? "Working Directory" : Screen.getFileView().getArgumentManager().runDir);
               try{
				compileWorkDirComp.setText(compileWorkDirComp.getToolTipText().substring(compileWorkDirComp.getToolTipText().lastIndexOf(File.separator) + 1));
				runWorkDirComp.setText(runWorkDirComp.getToolTipText().substring(runWorkDirComp.getToolTipText().lastIndexOf(File.separator) + 1));
               }
               catch(Exception e){
               	
               }
               Screen.getFileView().getArgumentManager().units.forEach(unit->{
               	unit.setLocation(0, block);
               	panel.add(unit);
               	lists.add(unit);
               	block += 30;
          	});
			setSize(600, lists.isEmpty() ? 180 : (200 + (lists.size() > 6 ? 500 : (lists.size() * 30))));
			setLocationRelativeTo(null);
		}
		super.setVisible(value);
	}
}
