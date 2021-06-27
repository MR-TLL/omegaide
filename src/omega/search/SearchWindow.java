/**
  * 
  * Copyright (C) 2021 Omega UI

  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.

  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.

  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package omega.search;
import omega.tree.*;
import java.awt.Color;
import omega.comp.NoCaretField;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import omega.comp.TextComp;
import omega.launcher.Door;
import omega.utils.IconManager;
import omega.utils.UIManager;
import omega.Screen;
import omega.tree.FileTree;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import static omega.utils.UIManager.*;
import static omega.settings.Screen.*;
public class SearchWindow extends JDialog{
	private JPanel panel;
	private JScrollPane scrollPane;
	private LinkedList<File> files;
	private int blocks = -40;
	private NoCaretField field;
	private LinkedList<Door> doors;
	private int pointer;
     private BufferedImage textImage;
     private BufferedImage imageImage;
     private BufferedImage allImage;
	private Screen screen;

     private int pressX;
     private int pressY;
     
	public SearchWindow(Screen f){
		super(f, false);
		this.screen = f;
          files = new LinkedList<>();
          doors = new LinkedList<>();
          setLayout(null);
          setUndecorated(true);
		setTitle("Search Files across the Project");
		setIconImage(f.getIconImage());
		setSize(500, 300);
		setLocationRelativeTo(null);
          setResizable(false);
          
		scrollPane = new JScrollPane(panel = new JPanel(null));
          scrollPane.setBackground(c2);
          scrollPane.setBounds(0, 60, getWidth(), getHeight() - 60);
          add(scrollPane);

          TextComp titleComp = new TextComp(getTitle(), TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, c2, null);
          titleComp.setBounds(0, 0, getWidth() - 60, 30);
          titleComp.setClickable(false);
          titleComp.setFont(PX14);
          titleComp.setArc(0, 0);
          titleComp.addMouseListener(new MouseAdapter(){
               @Override
               public void mousePressed(MouseEvent e){
               	pressX = e.getX();
                    pressY = e.getY();
                    field.grabFocus();
               }
          });
          titleComp.addMouseMotionListener(new MouseAdapter(){
               @Override
               public void mouseDragged(MouseEvent e){
               	setLocation(e.getXOnScreen() - pressX, e.getYOnScreen() - pressY);
               }
          });
          add(titleComp);

          TextComp closeComp = new TextComp("x", TOOLMENU_COLOR2_SHADE, c2, TOOLMENU_COLOR2, ()->setVisible(false));
          closeComp.setBounds(getWidth() - 30, 0, 30, 30);
          closeComp.setFont(PX14);
          closeComp.setArc(0, 0);
          add(closeComp);

          TextComp reloadComp = new TextComp("#", "Click to Reload File Tree", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->cleanAndLoad(new File(Screen.getFileView().getProjectPath())));
          reloadComp.setBounds(getWidth() - 60, 0, 30, 30);
          reloadComp.setArc(0, 0);
          reloadComp.setFont(PX14);
          add(reloadComp);

          field = new NoCaretField("", "Type File Name", TOOLMENU_COLOR2, c2, TOOLMENU_COLOR3);
          field.setBounds(0, 30, getWidth(), 30);
          field.setFont(PX16);
		field.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(!doors.isEmpty()) {
					if(e.getKeyCode() == KeyEvent.VK_UP && pointer > 0) {
						doors.get(pointer).set(false);
						doors.get(--pointer).set(true);
						scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() - 40);
					}
					else if(e.getKeyCode() == KeyEvent.VK_DOWN && pointer + 1< doors.size()) {
						doors.get(pointer).set(false);
						doors.get(++pointer).set(true);
						scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() + 40);
					}
					else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						setVisible(false);
						screen.loadFile(new File(doors.get(pointer).getPath()));
					}
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN && e.getKeyCode() != KeyEvent.VK_ENTER)
					list(field.getText());
			}
		});
          add(field);
          addKeyListener(field);
          
		omega.utils.UIManager.setData(panel);
          
          //Creating File Image of size 32, 32 here
          textImage = IconManager.fluentfileImage;
          imageImage = IconManager.fluentimagefileImage;
          allImage = IconManager.fluentanyfileImage;
	}

	public void list(String text){
		doors.forEach(panel::remove);
		doors.clear();
		blocks = -40;
		files.forEach(file->{
			if(file.getName().contains(text)){
                    String ext = file.getName();
                    if(ext.contains("."))
                         ext = ext.substring(ext.lastIndexOf('.'));
				Door door = new Door(file.getAbsolutePath(), getPreferredImage(file), ()->{
					setVisible(false);
					screen.loadFile(file);
				});
				door.setBounds(0, blocks += 40, getWidth(), 40);
                    door.setToolTipText(file.getAbsolutePath());
                    door.setBackground(c2);
                    door.setForeground(switch(ext){
                         case ".txt", ".groovy", ".kt", ".java", ".xml", "properties", ".rs", ".py", ".js", ".html", ".sh", ".c", ".cpp" -> TOOLMENU_COLOR2;
                         case ".png", ".jpg", ".bmp", ".jpeg" -> TOOLMENU_COLOR3;
                         default -> TOOLMENU_COLOR1;
                    });
				panel.add(door);
				doors.add(door);
			}
		});
		panel.setPreferredSize(new Dimension(getWidth(), blocks));
		scrollPane.repaint();
		scrollPane.getVerticalScrollBar().setVisible(true);
		scrollPane.getVerticalScrollBar().setValue(0);
		scrollPane.getVerticalScrollBar().repaint();
		repaint();
		if(!doors.isEmpty()) {
			doors.get(pointer = 0).set(true);
		}
		doLayout();
	}

	public void cleanAndLoad(File f){
		this.files.clear();
		load(f);
		omega.tree.FileTree.sort(this.files);
	}

	public void load(File f){
		File[] files = f.listFiles();
		if(files == null || files.length == 0) return;
		for(File file : files){
			if(file.isDirectory()) load(file);
			else if(!file.getName().endsWith(".class")) this.files.add(file);
		}
	}

     public BufferedImage getPreferredImage(File file){
          if(file.isDirectory()){
               File[] files = file.listFiles();
               for(File fx : files){
                    if(fx.getName().equals(".projectInfo"))
                         return IconManager.fluentfolderImage;
               }
               return IconManager.fluentplainfolderImage;
          }
          if(file.getName().contains(".")){
               String ext = file.getName().substring(file.getName().lastIndexOf('.'));
               if(ext.equals(".png") || ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".bmp")
               || ext.equals(".gif") || ext.equals(".svg") || ext.equals(".ico") || ext.equals(".jp2"))
                    return IconManager.fluentimagefileImage;
               else if(ext.equals(".txt") || ext.equals(".java") || ext.equals(".cpp") || ext.equals(".py") || ext.equals(".rs") || ext.equals(".class") || ext.equals(".groovy"))
                    return IconManager.fluentfileImage;
               else if(ext.equals(".js") || ext.equals(".html") || ext.equals(".php") || ext.equals(".css"))
                    return IconManager.fluentwebImage;
               else if(ext.equals(".sh") || ext.equals(".run") || ext.equals(".dll") || ext.equals(".so"))
                    return IconManager.fluentshellImage;
               else if(ext.equalsIgnoreCase(".appimage") || ext.equals(".deb"))
                    return IconManager.fluentlinuxImage;
               else if(ext.equals(".cmd") || ext.equals(".bat") || ext.equals(".exe") || ext.equals(".msi"))
                    return IconManager.fluentwindowsImage;
               else if(ext.equals(".dmg"))
                    return IconManager.fluentmacImage;
          }
          return IconManager.fluentanyfileImage;
     }

     @Override
     public void setVisible(boolean value){
     	super.setVisible(value);
     	if(value){
     		field.grabFocus();
     	}
     }
}

