package update;
import settings.comp.*;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.fife.ui.rtextarea.RTextArea;

import ide.Screen;
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
		label = new TextComp("Checking for Update", ide.utils.UIManager.c1, ide.utils.UIManager.c2, ide.utils.UIManager.c3, ()->{});
		label.setClickable(false);
          label.setFont(font);
          label.setPreferredSize(new Dimension(200, 40));
		add(label, BorderLayout.NORTH);

		terminalArea = new RTextArea("Downloading Release Information");
		terminalArea.setFont(font);
		terminalArea.setEditable(false);
          terminalArea.setBackground(ide.utils.UIManager.c3);
          terminalArea.setForeground(ide.utils.UIManager.c2);
          terminalArea.setHighlightCurrentLine(false);
		add(scrollPane = new JScrollPane(terminalArea), BorderLayout.CENTER);

		downBtn = new Comp("Install Update", ide.utils.UIManager.c1, ide.utils.UIManager.c2, ide.utils.UIManager.c3, ()->{
               new Thread(()->{
                    clean();
                    terminalArea.setText("");
                    downBtn.setVisible(false);
                    label.setText("Downloading Update");
                    File debFile = download("out/omega-ide_" + version + "_all.deb");
                    if(debFile == null){
                         label.setText("Problem Receiving installation File from server' end");
                         return;
                    }
                    terminalArea.setText("Update Downloaded\n");
                    print("Make user you have gdebi installed if you are using debian");
                    label.setText("Installing....");
                    print("The Downloaded setup is in the \"out\" directory of the current project.");
                    print("Before installing close the IDE else your opened project may get corrupted!");
                    print("Running ... \"" + "java.awt.Desktop.getDesktop().open(debFile)\"");
                    try{
                         System.out.println(debFile);
                         java.awt.Desktop.getDesktop().open(debFile);
                    }catch(Exception ex){ print(ex.toString()); }
               }).start();
	     });
		downBtn.setFont(font);
          downBtn.setPreferredSize(new Dimension(200, 40));
		downBtn.setVisible(false);
          if(File.separator.equals("/"))
               add(downBtn, BorderLayout.SOUTH);
	}

	public File download(String name){
		try{
			String url = "https://raw.githubusercontent.com/omegaui/omegaide/main/"+name;
			Process pull = new ProcessBuilder("wget", url).start();
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
		if(name.contains("/")) name = name.substring(name.lastIndexOf('/') + 1).trim();
		return new File(name);
	}

	public void check(){
		terminalArea.setText("Make Sure your internet is working!\n");
		releaseFile = download(".release");
		if(releaseFile == null) print("Unable to download Release File check whether wget is installed or not");
		print("Downloaded Release File");
		terminalArea.setText("------------------\nReading Release File\n");
		try{
			Scanner reader = new Scanner(releaseFile);
			version = reader.nextLine();
			label.setText("Terminal");
               double v = Double.valueOf(version.substring(1));
               double myv = Double.valueOf(ide.Screen.VERSION.substring(1));
			if(v <= myv){
				clean();
				print("No updates avaliable, ");
				print("The latest version is already installed.");
				reader.close();
				return;
			}
			print("Update Available\n");
			print("Omega IDE v" + version);
			print("Download Size : " + reader.nextLine() + "\n");
			print("Whats New!");
			while(reader.hasNextLine())
				print("\t" + reader.nextLine());
			reader.close();
			downBtn.setVisible(true);
               if(!File.separator.equals("/"))
                    print("goto https://github.com/omegaui/omegaide to download the latest version!!");
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
