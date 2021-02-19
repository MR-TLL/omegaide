package omega.instant.support;
import omega.Screen;
/*
    Copyright (C) 2021 Omega UI. All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
import java.io.PrintWriter;
import omega.database.DataEntry;
import java.util.LinkedList;
import java.io.File;
import omega.database.DataBase;
public class ArgumentManager extends DataBase{
     
     public String run_time_args; // The String containing the run time command
     public String compile_time_args; // The String containing the compile time command
     public String runDir; // The String containing the working directory of the run-time
     public String compileDir; // The String containing the working directory of the compile-time
     public LinkedList<ListUnit> units = new LinkedList<>(); //The set of list-units
     
     /**
      * The Default constructor.
     */
     public ArgumentManager(){
     	super(Screen.getFileView().getProjectPath() + File.separator + ".args");
          load();
          
     }

     /**
      * The method that loades data from the DataBase.
     */
     public void load(){
          compile_time_args = getEntryAt("Compile Time Argument", 0) != null ? getEntryAt("Compile Time Argument", 0).getValue() : "";
          run_time_args = getEntryAt("Run Time Argument", 0) != null ? getEntryAt("Run Time Argument", 0).getValue() : "";                
          compileDir = getEntryAt("Compile Time Working Directory", 0) != null ? getEntryAt("Compile Time Working Directory", 0).getValue() : "";   
          runDir = getEntryAt("Run Time Working Directory", 0) != null ? getEntryAt("Run Time Working Directory", 0).getValue() : "";

          if(!new File(compileDir).exists())
               compileDir = "";
          if(!new File(runDir).exists())
               runDir = "";

          LinkedList<DataEntry> extensions = getEntries("Extensions");
          LinkedList<DataEntry> containers = getEntries("Containers");
          LinkedList<DataEntry> sources = getEntries("Sources");
          LinkedList<DataEntry> bounds = getEntries("Bounds Surrounded");
          if(extensions == null) return;
          for(int i = 0; i < extensions.size(); i++){
               units.add(new ListUnit(extensions.get(i).getValue(), 
                                      containers.get(i).getValue(), 
                                      sources.get(i).getValue(), 
                                      bounds.get(i).getValueAsBoolean()));
          }
     }

     /**
      * The method that generates the required files as specified in the Settings
     */
     public void genLists(){
     	units.forEach(unit->{
               LinkedList<File> files = new LinkedList<>();
               loadFiles(unit.ext, files, new File(unit.sourceDir));
               if(!files.isEmpty())
                    writeList(unit.container, files, unit.sur);
	     });
     }

     /**
      * The method that writes the files specified in the settings
      * @param name = name of the file
      * @param files = the files whose paths are to be written
      * @param sur = if the files are to be surrounded within (") sur = true else vice versa
     */
     public void writeList(String name, LinkedList<File> files, boolean sur){
     	try{
     		PrintWriter writer = new PrintWriter(new File(Screen.getFileView().getProjectPath() + File.separator + name));
               files.forEach(file->{
                    if(sur)
                         writer.println("\"" + file.getAbsolutePath() + "\"");
                    else
                         writer.println(file.getAbsolutePath());
               });
               writer.close();
     	}catch(Exception e){ System.err.println(e); }
     }

     /**
      * The method loads the all the Files of the directory dir in files list
      * which ends with the ext extension
     */
     public void loadFiles(String ext, LinkedList<File> files, File dir){
     	File[] F = dir.listFiles();
          if(F == null || F.length == 0) return;
          for(File fx : F){
               if(!fx.isDirectory() && fx.getName().endsWith(ext))
                    files.add(fx);
               else if(fx.isDirectory())
                    loadFiles(ext, files, fx);
          }
     }

     @Override
     public void save(){
          updateEntry("Compile Time Argument", compile_time_args, 0);
          updateEntry("Run Time Argument", run_time_args, 0);
          updateEntry("Compile Time Working Directory", compileDir, 0);
          updateEntry("Run Time Working Directory", runDir, 0);
          for(int i = 0; i < units.size(); i++){
               ListUnit u = units.get(i);
               updateEntry("Extensions", u.ext, i);
               updateEntry("Containers", u.container, i);
               updateEntry("Sources", u.sourceDir, i);
               updateEntry("Bounds Surrounded", String.valueOf(u.sur), i);
          }
          super.save();
     }
}
