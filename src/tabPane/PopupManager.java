package tabPane;
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
import java.awt.Desktop;
import popup.*;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import java.io.File;

import javax.swing.*;

import ide.Screen;
import ide.utils.Editor;
import ide.utils.UIManager;
import ide.utils.systems.EditorTools;
import ide.utils.systems.creators.FileOperationManager;
import importIO.ImportManager;

public class PopupManager {
	public static final byte SOURCE_FILE = 0;
	public static final byte NON_SOURCE_FILE = 1;
	public static OPopupWindow createPopup(byte type, Editor editor, Screen screen) {
		OPopupWindow popup = new OPopupWindow("Tab Menu", ide.Screen.getScreen(), 0, false);
		
		if(type == SOURCE_FILE) {
               popup.createItem("Run as Main Class", IconManager.runImage, ()->{
                    Screen.getRunView().setMainClassPath(editor.currentFile.getAbsolutePath());
                    Screen.getRunView().run();
               })
               .createItem("Run Project", IconManager.runImage, ()->Screen.getRunView().run())
               .createItem("Build Project", IconManager.buildImage, ()->Screen.getBuildView().compileProject())
               .createItem("Save", IconManager.fileImage, ()->editor.saveCurrentFile())
               .createItem("Save As", IconManager.fileImage, ()->{
                    editor.saveFileAs();
                    Screen.getProjectView().reload();
               })
               .createItem("Discard", IconManager.closeImage, ()->{
                    editor.reloadFile();
                    screen.getTabPanel().remove(editor);
               })
               .createItem("Reload", null, ()->editor.reloadFile()).width(300);
		}
		else {
			popup.createItem("Save", IconManager.fileImage, ()->editor.saveCurrentFile())
               .createItem("Save As", IconManager.fileImage, ()->{
                    editor.saveFileAs();
                    Screen.getProjectView().reload();
               })
               .createItem("Discard", IconManager.closeImage, ()->{
                    editor.reloadFile();
                    screen.getTabPanel().remove(editor);
               })
               .createItem("Reload", null, ()->editor.reloadFile()).width(300);
		}
          popup.createItem("Copy Path (\"path\")", IconManager.fileImage, ()->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("\""+editor.currentFile.getAbsolutePath()+"\""), null));
          popup.createItem("Copy Path", IconManager.fileImage, ()->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(editor.currentFile.getAbsolutePath()), null));
		popup.createItem("Open in Desktop", IconManager.fileImage, ()->Screen.openInDesktop(editor.currentFile));
		return popup;
	}

	public static void createTreePopup(OPopupWindow popup, File file) {
          popup.createItem("New Directory", IconManager.projectImage, ()->Screen.getFileView().getFileCreator().showDirView(file.getAbsolutePath()))
          .createItem("New File", IconManager.fileImage, ()->Screen.getFileView().getFileCreator().showFileView(file.getAbsolutePath()))
          .createItem("New Class", IconManager.classImage, ()->Screen.getFileView().getFileCreator().show("Class"))
          .createItem("New Interface", IconManager.interImage, ()->Screen.getFileView().getFileCreator().show("interface"))
          .createItem("New Enum", IconManager.enumImage, ()->Screen.getFileView().getFileCreator().show("enum"))
          .createItem("New Annotation", IconManager.annImage, ()->Screen.getFileView().getFileCreator().show("@interface"))
          .createItem("Open in Desktop", IconManager.fileImage, ()->Screen.openInDesktop(file))
          .createItem("Open On Right Tab Panel", IconManager.fileImage, ()->Screen.getScreen().loadFileOnRightTabPanel(file))
          .createItem("Open On Bottom Tab Panel", IconManager.fileImage, ()->Screen.getScreen().loadFileOnBottomTabPanel(file))
          .createItem("Delete", IconManager.closeImage, ()->{
               if(!file.isDirectory()){
                    Editor editor = ide.Screen.getScreen().getTabPanel().findEditor(file);
                    if(editor != null)
                         ide.Screen.getScreen().getTabPanel().remove(editor);
               }
               if(file.isDirectory()){
                    try{
                         int res0 = JOptionPane.showConfirmDialog(Screen.getScreen(), "Do you want to delete Directory " + file.getName() + "?", "Delete or not?", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);;
                         if(res0 != JOptionPane.YES_OPTION)
                              return;
                         Editor.deleteDir(file);
                    }catch(Exception e){ System.err.println(e); }
               }
               else
                    Editor.deleteFile(file);
               Screen.getProjectView().reload();
               ImportManager.readSource(EditorTools.importManager);
          })
          .createItem("Refresh", null, ()->Screen.getProjectView().reload())
          .createItem("Rename", IconManager.fileImage, ()->{
               Editor editor = ide.Screen.getScreen().getTabPanel().findEditor(file);
               if(editor != null) ide.Screen.getScreen().getTabPanel().remove(editor);
               Screen.getProjectView().getFileOperationManager().rename("Rename " + file.getName(), "rename", file);
               Screen.getProjectView().reload();
               ImportManager.readSource(EditorTools.importManager);
          });
		
          popup.createItem("Copy Path (\"path\")", IconManager.fileImage, ()->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("\"" + file.getAbsolutePath() + "\""), null));
          popup.createItem("Copy Path", IconManager.fileImage, ()->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(file.getAbsolutePath()), null));
	}
}
