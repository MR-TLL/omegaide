package omega.update;
import omega.comp.*;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.util.Scanner;
import omega.utils.UIManager;
import java.awt.Desktop;
import omega.utils.systems.creators.ChoiceDialog;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.fife.ui.rtextarea.RTextArea;

import omega.Screen;
public class Updater extends JDialog {
	private File releaseFile;
	private TextComp label;
	private RTextArea terminalArea;
	private Comp downBtn;
	private String version = null;
	private JScrollPane scrollPane;
	private static Font font = new Font("Ubuntu Mono", Font.BOLD, 16);
	public Updater(Screen screen){
		super(screen, "Release Updater");
		setModal(false);
		setIconImage(screen.getIconImage());
		setSize(660, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		init();
	}

	public void init(){
		label = new TextComp("Checking for Update", omega.utils.UIManager.c1, omega.utils.UIManager.c2, omega.utils.UIManager.c3, ()->{});
		label.setClickable(false);
          label.setFont(font);
          label.setPreferredSize(new Dimension(200, 40));
		add(label, BorderLayout.NORTH);

		terminalArea = new RTextArea("Downloading Release Information");
		terminalArea.setFont(font);
		terminalArea.setEditable(false);
          terminalArea.setBackground(omega.utils.UIManager.c3);
          terminalArea.setForeground(omega.utils.UIManager.c2);
          terminalArea.setHighlightCurrentLine(false);
		add(scrollPane = new JScrollPane(terminalArea), BorderLayout.CENTER);

		downBtn = new Comp("Install Update", omega.utils.UIManager.c1, omega.utils.UIManager.c2, omega.utils.UIManager.c3, ()->{
               new Thread(()->{
                    int res = Screen.getScreen().getChoiceDialog().show("Download Debian Setup", "Download Jar File");
                    if(res == ChoiceDialog.CANCEL)
                         return;
                    clean();
                    terminalArea.setText("");
                    downBtn.setVisible(false);
                    label.setText("Downloading Update");
                    String fileName = "out/omega-ide_" + version.substring(1) + "_all.deb";
                    if(res == ChoiceDialog.CHOICE_2)
                         fileName = "out/Omega IDE " + version + ".jar";
                    File debFile = download(fileName);
                    if(debFile == null){
                         label.setText("Problem Receiving installation File from server's end");
                         return;
                    }
                    terminalArea.setText("Update Downloaded\n");
                    print("");
                    print("The downloaded release file is in ~/.omega-ide/out folder");
                    print("");
                    label.setText("Installing....");
                    print("The Downloaded setup is in the \"out\" directory of the current project.");
                    print("Before installing close the IDE else your opened project may get corrupted!");
                    print("Running ... \"" + "java.awt.Desktop.getDesktop().open(debFile)\"");
                    try{
                         Desktop.getDesktop().open(debFile);
                    }catch(Exception ex){ print(ex.toString()); }
                    label.setText("Installing....Done!!");
               }).start();
	     });
		downBtn.setFont(font);
          downBtn.setPreferredSize(new Dimension(200, 40));
		downBtn.setVisible(false);
          add(downBtn, BorderLayout.SOUTH);
	}

	public File download(String name){
		try{
			String url = "https://raw.githubusercontent.com/omegaui/omegaide/main/"+name;
			Process pull = new ProcessBuilder("wget", url, "--output-document=.omega-ide" + File.separator + name).start();
			Scanner out = new Scanner(pull.getInputStream());
			Scanner err = new Scanner(pull.getErrorStream());
			new Thread(()->{
				while(pull.isAlive()){
					if(err.hasNextLine()) print(err.nextLine());
				}
				err.close();
			}).start();
			while(pull.isAlive()){
				if(out.hasNextLine()) print(out.nextLine());
			}
			out.close();
		}catch(Exception e){ System.err.println(e); }
          File file = new File(".omega-ide" + File.separator + name);
		return  file.exists() ? file : null;
	}

	public void check(){
		terminalArea.setText("Make Sure your internet is working!\n");
		releaseFile = download(".release");
		if(releaseFile == null) {
		     print("Unable to download Release File check whether wget is installed or not");
               return;
		}
		terminalArea.setText("------------------\nReading Release File\n");
		try{
			Scanner reader = new Scanner(releaseFile);
			version = reader.nextLine();
			label.setText("Updater");
               double v = Double.valueOf(version.substring(1));
               double myv = Double.valueOf(omega.Screen.VERSION.substring(1));
			if(v <= myv){
				clean();
				print("No updates avaliable, ");
				print("The latest version is already installed.");
				reader.close();
				return;
			}
			print("Update Available\n");
			print("Omega IDE " + version);
			print("Download Size : " + reader.nextLine() + "\n");
			print("Whats New!");
			while(reader.hasNextLine())
				print("\t" + reader.nextLine());
			reader.close();
			downBtn.setVisible(true);
		}
		catch(Exception e){ print(e.toString()); }
	}

	public void clean(){
		if(releaseFile == null) return;
		releaseFile.delete();
	}

	public void print(String text){
		terminalArea.append(text + "\n");
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
	}

	@Override
	public void setVisible(boolean value){
		super.setVisible(true);
          setTitle("Checking for Updates");
		if(value){
			check();
		}
		else{
			clean();
		}
          setTitle("Release Updater");
	}
}
