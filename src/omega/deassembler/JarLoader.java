package omega.deassembler;
import java.util.zip.*;
import java.util.jar.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;
public class JarLoader {
	public String jarPath;
	public LinkedList<ByteReader> readers = new LinkedList<>();
	public LinkedList<String> classNames = new LinkedList<>();
	public ClassLoader loader;
	public JarLoader(String jarPath){
		this.jarPath = jarPath;
		load();
	}
	public JarLoader(){
		try{
			this.jarPath = "System JMods";
			loader = getClass().getClassLoader();
               Assembly.add("java.lang.Object", loadReader("java.lang.Object"));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void putClassName(String className){
		classNames.add(className);
	}
	private void load(){
		try{
			File file = new File(jarPath);
			if(!file.exists())
				return;
			loadClassNames();
			loader = URLClassLoader.newInstance(new URL[]{
				file.toURL()
			});
			loadAllClasses();
		}
		catch(Exception e){
			System.err.println(e);
		}
	}
	
	private void loadClassNames(){
		readJar();
	}
	
	public void readJar(){
		try{
			try(JarFile rtJarFile = new JarFile(jarPath)){
				for(Enumeration<JarEntry> enums = rtJarFile.entries(); enums.hasMoreElements();){
					JarEntry jarEntry = enums.nextElement();
					String name = jarEntry.getName();
					if(!name.endsWith("/")) {
						String classPath = convertJarPathToPackagePath(name);
						if(classPath != null) {
							classNames.add(classPath);
						}
					}
				}
			}
		}
		catch(Exception e) {
			System.err.println(e);
		}
	}
	public static String convertJarPathToPackagePath(String zipPath){
		if(zipPath == null || zipPath.contains("$") || !zipPath.endsWith(".class") || zipPath.startsWith("META-INF"))
			return null;
		zipPath = zipPath.substring(0, zipPath.lastIndexOf('.'));
		StringTokenizer tok = new StringTokenizer(zipPath, "/");
		zipPath = "";
		while(tok.hasMoreTokens())
			zipPath += tok.nextToken() + ".";
		zipPath = zipPath.substring(0, zipPath.length() - 1);
		return zipPath.equals("module-info") ? null : zipPath;
	}
	public ByteReader getReader(String className){
		for(ByteReader br : readers){
			if(br.className.equals(className))
				return br;
		}
		return null;
	}
	public void loadAllClasses(){
		try{
			classNames.forEach(this::loadReader);
		}
		catch(Exception e){
			System.err.println(e);
		}
	}
	public ByteReader loadReader(String className){
		ByteReader br = null;
		try{
               for(ByteReader brx : readers){
                    if(brx.className.equals(className))
                         return brx;
               }
			Class c = loader.loadClass(className);
               br = new ByteReader(c);
			readers.add(br);
		}
		catch(Exception e){
			//e.printStackTrace();
		}
		return br;
	}
	public Class loadClassNoAppend(String className){
		try{
			return loader.loadClass(className);
		}
		catch(Exception e){
			//e.printStackTrace();
		}
		return null;
	}
	
	public void close(){
		try{
			if(loader instanceof URLClassLoader)
				((URLClassLoader)loader).close();
			readers.forEach(r->r.close());
			readers.clear();
			classNames.clear();
		}
		catch(Exception e){
			System.err.println(e);
		}
	}
	public static synchronized JarLoader prepareSystemModuleLoader(){
		return new JarLoader();
	}
	
	public static synchronized JarLoader prepareModule(String modulePath){
		try{
			System.out.println("Preparing Module ... " + modulePath.substring(modulePath.lastIndexOf(File.separator)));
			new File("readable-module-data.jar").delete();
			ZipFile moduleFile = new ZipFile(modulePath);
			ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream("readable-module-data.jar"));
			for(Enumeration enums = moduleFile.entries(); enums.hasMoreElements();){
				ZipEntry entry = (ZipEntry)enums.nextElement();
				String name = entry.getName();
				if((name.startsWith("classes") && !name.contains("module-info")) || name.startsWith("resources")){
					zipOutputStream.putNextEntry(new ZipEntry(name.substring(name.indexOf('/') + 1)));
					InputStream in = moduleFile.getInputStream(entry);
					while(in.available() > 0)
						zipOutputStream.write(in.read());
					zipOutputStream.flush();
				}
			}
			zipOutputStream.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return new JarLoader("readable-module-data.jar");
	}
	@Override
	public String toString(){
		return jarPath;
	}
	public static void main(String[] args){
		JarLoader loader = new JarLoader("/home/ubuntu/Documents/Omega IDE/lib/openjfx-11.0.2_linux-x64_bin-sdk/javafx-sdk-11.0.2/lib/javafx.base.jar");
		System.out.println(loader.getReader("javafx.util.Builder"));
		loader.getReader("javafx.util.Builder").dataMembers.forEach(System.out::println);
	}
}
