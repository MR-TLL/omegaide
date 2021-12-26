package omega.tabPane;
import omega.popup.OPopupWindow;

import javax.swing.JComponent;

import java.awt.image.BufferedImage;

import java.awt.Color;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class TabData {
	private String name;
	private String uniqueName;
	private String tooltip;
	private BufferedImage image;
	private JComponent component;
	private Color tabTextColor;
	private Runnable onClose;
	private OPopupWindow popup;
	private TabComp tabComp;
	private TabHolder tabHolder;

	private TabData(){ }

	public TabData(String name, BufferedImage image, JComponent c, Runnable onClose){
		this(name, name, "", image, c, null, onClose);
	}

	public TabData(String name, String uniqueName, BufferedImage image, JComponent c, Runnable onClose){
		this(name, uniqueName, "", image, c, null, onClose);
	}

	public TabData(String name, String uniqueName, String tooltip, BufferedImage image, JComponent c, Runnable onClose){
		this(name, uniqueName, tooltip, image, c, null, onClose);
	}

	public TabData(String name, String uniqueName, String tooltip, BufferedImage image, JComponent c, Color tabTextColor, Runnable onClose){
		setName(name);
		setUniqueName(uniqueName);
		setTooltip(tooltip);
		setImage(image);
		setComponent(c);
		setOnClose(onClose);
		setTabTextColor(tabTextColor);
		setTabHolder(new TabHolder(this));
	}

	public static TabData create(){
		return new TabData();
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof TabData tabObj)
			return tabObj.getUniqueName().equals(getUniqueName());
		return super.equals(obj);
	}

	public java.lang.String getName() {
		return name;
	}
	
	public TabData setName(java.lang.String name) {
		this.name = name;
		return this;
	}
	
	public java.lang.String getUniqueName() {
		return uniqueName;
	}
	
	public TabData setUniqueName(java.lang.String uniqueName) {
		this.uniqueName = uniqueName;
		return this;
	}
	
	public java.lang.String getTooltip() {
		return tooltip;
	}
	
	public TabData setTooltip(java.lang.String tooltip) {
		this.tooltip = tooltip;
		return this;
	}
	
	public java.awt.image.BufferedImage getImage() {
		return image;
	}
	
	public TabData setImage(java.awt.image.BufferedImage image) {
		this.image = image;
		return this;
	}
	
	public JComponent getComponent() {
		return component;
	}
	
	public TabData setComponent(JComponent component) {
		this.component = component;
		return this;
	}
	
	public java.lang.Runnable getOnClose() {
		return onClose == null ? (()->{}) : onClose;
	}
	
	public TabData setOnClose(java.lang.Runnable onClose) {
		this.onClose = onClose;
		return this;
	}

	public java.awt.Color getTabTextColor() {
		return tabTextColor == null ? glow : tabTextColor;
	}
	
	public TabData setTabTextColor(java.awt.Color tabTextColor) {
		this.tabTextColor = tabTextColor;
		return this;
	}

	public OPopupWindow getPopup() {
		return popup;
	}
	
	public TabData setPopup(OPopupWindow popup) {
		this.popup = popup;
		return this;
	}

	public TabComp getTabComp() {
		return tabComp;
	}
	
	public void setTabComp(TabComp tabComp) {
		this.tabComp = tabComp;
	}
	
	public TabHolder getTabHolder() {
		return tabHolder;
	}

	public void setTabHolder(TabHolder tabHolder) {
		this.tabHolder = tabHolder;
	}
	
}
