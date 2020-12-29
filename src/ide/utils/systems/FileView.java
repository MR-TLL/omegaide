package ide.utils.systems;

import java.awt.FlowLayout;
import java.io.File;

import javax.swing.JFileChooser;

import deassembler.Assembly;
import depenUI.DependencyView;
import ide.Screen;
import ide.utils.DataManager;
import ide.utils.DependencyManager;
import ide.utils.ModuleManager;
import ide.utils.ModuleView;
import ide.utils.NativesManager;
import ide.utils.ProjectDataBase;
import ide.utils.ResourceManager;
import ide.utils.UIManager;
import ide.utils.systems.creators.FileCreator;
import search.SearchWindow;

public class FileView extends View {

	private static String projectPath = null;
	private ProjectDataBase projectManager;
	private DependencyManager dependencyManager;
	private NativesManager nativesManager;
	private ResourceManager resourceManager;
	private volatile FileCreator fileCreator;
	private DependencyView dependencyView;
	private ModuleView moduleView;
	private ModuleManager moduleManager;
	private SearchWindow searchWindow;

	public FileView(String title, Screen window) {
		super(title, window);
		dependencyView = new DependencyView(window);
		moduleView = new ModuleView(window);
		searchWindow = new SearchWindow(window);
		setLayout(new FlowLayout());
		init();
		setSize(getWidth(), getHeight());
	}

	private void init() {
		fileCreator = new FileCreator(getScreen());	
	}

	public ProjectDataBase getProjectManager()
	{
		return projectManager;
	}

	public DependencyManager getDependencyManager() {
		return dependencyManager;
	}

	public NativesManager getNativesManager() {
		return nativesManager;
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public DependencyView getDependencyView() {
		return dependencyView;
	}

	public ModuleView getModuleView() {
		return moduleView;
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public String getProjectName()
	{
		if(projectPath == null)
			return "";
		return projectPath.substring(projectPath.lastIndexOf('/')+1);
	}

	public void setProjectPath(String path) {
		if(projectPath != null) {
			if(projectPath.equals(path))
				return;
		}
		new Thread(()->Screen.addAndSaveRecents(path)).start();
		projectPath = path;
		if(Screen.launcher != null)
			Screen.launcher.setVisible(false);
		DataManager.setDefaultProjectPath(projectPath);
		Screen.notify("Loading Project \""+getProjectName()+"\"");
		checkDir(new File(projectPath+"/src"));
		checkDir(new File(projectPath+"/out"));
		checkDir(new File(projectPath+"/bin"));
		checkDir(new File(projectPath+"/res"));
		getScreen().setProject(getProjectName());
		try {
			Screen.getProjectView().getProjectView().setVisible(true);
			if(getScreen().screenHasProjectView) {
				Screen.getProjectView().organizeProjectViewDefaults();
				getScreen().setVisible(true);
			}
			
		}catch(Exception ex) {System.out.println(ex);}
		getScreen().getTabPanel().closeAllTabs();
		Assembly.deassemble();
		Screen.notify("Reading Dependencies and Resource Roots", 1000, null);
		projectManager = new ProjectDataBase();
		dependencyManager = new DependencyManager();
		nativesManager = new NativesManager();
		resourceManager = new ResourceManager();
		moduleManager = new ModuleManager();
		searchWindow.cleanAndLoad(new File(projectPath));
		try {
			Screen.getProjectView().getProjectView().setVisible(true);
		}catch(Exception e) {System.out.println(e);}
		if(Screen.getFileView().getProjectManager().jdkPath == null || !new File(Screen.getFileView().getProjectManager().jdkPath).exists()){
			Screen.notify("No JDK Defined for Project "+Screen.getFileView().getProjectName(), 3000, null);
		}
		else {
			projectManager.readJDK(false);
		}
		Screen.hideNotif();
		try {Screen.getProjectView().reload();}catch(Exception e) {}
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void saveAll() {
		if(projectManager != null)
			projectManager.save();
		if(dependencyManager != null)
			dependencyManager.saveFile();
		if(nativesManager != null)
			nativesManager.saveFile();
		if(resourceManager != null)
			resourceManager.saveData();
	}

	public boolean open(String type) {
		JFileChooser ch = new JFileChooser();
		UIManager.setData(ch);
		Screen.getAccessories().addDeleteButton(ch);
		setVisible(false);
		if(type.equals("Project"))
		{
			ch.setDialogTitle("Open Project -Select/Create an empty folder to create a new poject");
			ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			ch.setMultiSelectionEnabled(false);
			int res = ch.showOpenDialog(getScreen());
			if(res == JFileChooser.APPROVE_OPTION) {
				if(Screen.launcher != null)
					Screen.launcher.setVisible(false);
				Screen.getScreen().setVisible(true);

				saveAll();
				getScreen().closeCurrentProject();
				setProjectPath(ch.getSelectedFile().getPath());
				checkDir(new File(projectPath+"/src"));
				checkDir(new File(projectPath+"/out"));
				checkDir(new File(projectPath+"/bin"));
				checkDir(new File(projectPath+"/res"));
				return true;
			}
		}
		else
		{
			ch.setDialogTitle("Open File");
			ch.setMultiSelectionEnabled(true);
			ch.setCurrentDirectory(new File(projectPath+"/src"));
			ch.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int res = ch.showOpenDialog(getScreen());
			if(res == JFileChooser.APPROVE_OPTION)
			{
				for(File file : ch.getSelectedFiles()) {
					getScreen().loadFile(file);
				}
			}
		}
		return false;
	}

	public void closeProject() {
		saveAll();
		getScreen().saveEssential();
		getScreen().setVisible(false);
		DataManager.setDefaultProjectPath("");
		if(Screen.launcher == null)
			Screen.launcher = new launcher.Launcher();
		Screen.launcher.setVisible(true);
	}
	
	public SearchWindow getSearchWindow() {
		return searchWindow;
	}
	
	public FileCreator getFileCreator() {
		return fileCreator;
	}

	public static void checkDir(File file)
	{
		if(!file.exists())
		{
			file.mkdir();
		}
	}

}
