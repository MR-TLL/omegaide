package omega.utils;
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
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import omega.Screen;
import omega.database.DataBase;
import omega.database.DataEntry;

public class UIManager extends DataBase {

	public static String fontName = "Ubuntu Mono";
     public static int fontSize = 16;
     public static int fontState = Font.BOLD;
	private static final Font font = omega.settings.Screen.PX12;
	public static Color glow;
	public static Color c1;
	public static Color c2;
	public static Color c3;

     // IDE Component Colors -- Default Mode -- LIGHT

     public static Color TOOLMENU_COLOR1 = new Color(26, 36, 219);
     public static Color TOOLMENU_COLOR1_SHADE = new Color(26, 36, 219, 40);
     public static Color TOOLMENU_COLOR2 = new Color(223, 33, 15);
     public static Color TOOLMENU_COLOR2_SHADE = new Color(223, 33, 15, 40);
     public static Color TOOLMENU_COLOR3 = new Color(126, 20, 219);
     public static Color TOOLMENU_COLOR3_SHADE = new Color(126, 20, 219, 40);
     public static Color TOOLMENU_COLOR4 = new Color(255, 158, 14);
     public static Color TOOLMENU_COLOR4_SHADE = new Color(255, 158, 14, 40);
     

	public UIManager(Screen screen) {
		super(".omega-ide" + File.separator + ".ui");
          loadData();
	}

     public static void loadHighlight(){
          if(isDarkMode())
               glow = Color.WHITE;
          else 
               glow = Color.BLACK;
     }

	public void loadData() {
		try {
			DataEntry e = getEntryAt("Font", 0);
			if(e == null) return;
			setFontName(e.getValue());
               setFontSize(getEntryAt("Font", 1).getValueAsInt());
               setFontState(getEntryAt("Font", 2).getValueAsInt());
			if(!isDarkMode()) {
				c3 = omega.settings.Screen.color4;
				c1 = new Color(0, 0, 255, 40);
				c2 = Color.WHITE;
			}
			else {
                    c1 = Color.decode("#563F2E");
                    c2 = Color.decode("#282828");
                    c3 = Color.decode("#ABB0BC");
                    TOOLMENU_COLOR1 = Color.decode("#f0b40f");
                    TOOLMENU_COLOR1_SHADE = new Color(TOOLMENU_COLOR1.getRed(), TOOLMENU_COLOR1.getGreen(), TOOLMENU_COLOR1.getBlue(), 40);
                    TOOLMENU_COLOR2 = Color.decode("#D34D42");
                    TOOLMENU_COLOR2_SHADE = new Color(TOOLMENU_COLOR2.getRed(), TOOLMENU_COLOR2.getGreen(), TOOLMENU_COLOR2.getBlue(), 40);
                    TOOLMENU_COLOR3 = Color.decode("#27AA55");
                    TOOLMENU_COLOR3_SHADE = new Color(TOOLMENU_COLOR3.getRed(), TOOLMENU_COLOR3.getGreen(), TOOLMENU_COLOR3.getBlue(), 40);
                    TOOLMENU_COLOR4 = Color.decode("#EB7201");
                    TOOLMENU_COLOR4_SHADE = new Color(TOOLMENU_COLOR4.getRed(), TOOLMENU_COLOR4.getGreen(), TOOLMENU_COLOR4.getBlue(), 40);
			}
		}
		catch(Exception e) {
		     e.printStackTrace();
	     }
	}
	
	public static void reset() {
		fontName = "Ubuntu Mono";
		fontSize = 16;
          fontState = Font.BOLD;
	}

	public static void setData(Editor editor) {
		editor.setFont(new Font(fontName, fontState, fontSize));
	}

	public static void setData(int fontSize, String fontName, int fontState) {
		UIManager.fontSize = fontSize;
		UIManager.fontName = fontName;
          UIManager.fontState = fontState;
	}

	public static void setData(Component c) {
		c.setBackground(c2);
		c.setForeground(c3);
		c.setFont(font);
	}

	@Override
	public void save() {
		clear();
		addEntry("Font", fontName);
          addEntry("Font", fontSize+"");
          addEntry("Font", fontState+"");
		super.save();
	}

     public static File loadDefaultFile(String fileName, String resName){
          File file = new File(fileName);
     	try{
               if(file.exists()) return file;
               InputStream in = UIManager.class.getResourceAsStream("/" + resName);
               OutputStream out = new FileOutputStream(file);
               while(in.available() > 0)
                    out.write(in.read());
               out.close();
               in.close();
     	}catch(Exception e){ e.printStackTrace(); }
          return file;
     }
	
	public static boolean isDarkMode() {
          return DataManager.getTheme().equals("dark");
	}

	public static void setFontName(String fontName) {
		UIManager.fontName = fontName;
	}

     public static void setFontSize(int fontSize) {
          UIManager.fontSize = fontSize;
     }

     public static void setFontState(int fontState) {
          UIManager.fontState = fontState;
     }

}
