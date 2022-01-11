/**
* OperationPane
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

package omega.ui.panel;
import omega.ui.popup.OPopupWindow;

import omega.io.TabData;
import omega.io.UIManager;

import omega.ui.listener.TabPanelListener;

import omega.Screen;

import java.awt.image.BufferedImage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;

import javax.swing.JPanel;
import javax.swing.JComponent;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;
public class OperationPane extends JPanel{
	
	private Screen screen;
	private static TabPanel tabPane;
	
	private static final String TITLE = "Process Panel";
	private static final String HINT = "There is no process running";
	
	public OperationPane(Screen screen) {
		this.screen = screen;
		setLayout(new BorderLayout());
		tabPane = new TabPanel(TabPanel.TAB_LOCATION_TOP);
		
		setVisible(false);
		UIManager.setData(this);

		tabPane.addTabPanelListener(new TabPanelListener(){
			@Override
			public void tabAdded(TabData tabData){
				add(tabPane, BorderLayout.CENTER);
				setVisible(true);
			}
			
			@Override
			public void tabRemoved(TabData tabData){
				
			}
			
			@Override
			public void goneEmpty(TabPanel panel){
				remove(tabPane);
				setVisible(false);
			}

			@Override
			public void tabActivated(TabData tabData){
				
			}
		});
	}
	
	public void addTab(String name, BufferedImage image, JComponent c, Runnable r) {
		tabPane.addTab(name, name, "", image, c, r);
	}
	
	public void addTab(String name, BufferedImage image, JComponent c, Runnable r, OPopupWindow popup) {
		tabPane.addTab(name, name, "", image, c, r, popup);
	}

	public TabData getTabData(JComponent comp){
		return tabPane.getTabData(comp);
	}
	
	public static int count(String name) {
		int c = -1;
		for(TabData tx : tabPane.getTabs()){
			String var0 = tx.getName();
			if(var0.contains(name))
				c++;
		}
		return c;
	}
	
	@Override
	public void setVisible(boolean value) {
		if(tabPane.isEmpty())
			super.setVisible(false);
		
		super.setVisible(value);
		
		try{
			screen.getToolMenu().oPHidden = value;
			
			if(value) {
				setPreferredSize(new Dimension(screen.getWidth(), getHeight() > 450 ? getHeight() : 450));
				int y = screen.getHeight() - 400;
				screen.compilancePane.setDividerLocation(y);
			}
			
			omega.Screen.getScreen().getToolMenu().operateComp.repaint();
		}
		catch(Exception e) {
			
		}
	}
	
	public void removeTab(String name) {
		tabPane.removeTab(tabPane.getTab(name));
	}
	
	@Override
	public void paint(Graphics graphics){
		if(tabPane.isEmpty()){
			Graphics2D g = (Graphics2D)graphics;
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(c2);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(UIManager.TOOLMENU_COLOR3);
			g.setFont(PX28);
			FontMetrics f = g.getFontMetrics();
			g.drawString(TITLE, getWidth()/2 - f.stringWidth(TITLE)/2, getHeight()/2 - f.getHeight()/2 + f.getAscent() - f.getDescent() + 1);
			g.drawString(HINT, getWidth()/2 - f.stringWidth(HINT)/2, getHeight()/2 - f.getHeight()/2 + f.getAscent() - f.getDescent() + 10 + f.getHeight());
			g.dispose();
		}
		else
			super.paint(graphics);
	}
	
}

