package omega.comp;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
public class EdgeComp extends JComponent {
     private String text = "";
     public Color color1;
     public Color color2;
     public Color color3;
     public Runnable action;
     public int offset;
     public volatile boolean enter = false;
     public volatile boolean hoverEnabled = false;
     public volatile boolean useFlatLineAtBack = false;
     public EdgeComp(String text, Color color1, Color color2, Color color3, Runnable r){
          setText(text);
          setColors(color1, color2, color3);
          setOnAction(r);
          addMouseListener(new MouseAdapter(){
               @Override
               public void mouseEntered(MouseEvent e){
                    enter = true;
                    repaint();
               }
               @Override
               public void mouseExited(MouseEvent e){
                    enter = false;
                    repaint();
               }
               @Override
               public void mousePressed(MouseEvent e){
                    if(!isEnabled())
                         return;
                    enter = false;
                    repaint();
                    if(getOnAction() == null)
                         return;
                    getOnAction().run();
               }
          });
     }
     @Override
     public void paint(Graphics graphics){
          Graphics2D g = (Graphics2D)graphics;
          g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(color2);
          offset = getHeight()/2;
          int[] x = {0, getWidth() - offset, getWidth(), getWidth() - offset, 0, useFlatLineAtBack ? 0 : offset, 0};
          int[] y = {0, 0, getHeight()/2, getHeight(), getHeight(), getHeight()/2, 0};
          g.fillPolygon(x, y, x.length);
          g.setColor(color3);
          g.drawString(text, getWidth()/2 - g.getFontMetrics().stringWidth(text)/2, getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
          if(enter || !isEnabled()){
               int[] ex = {0, getWidth() - offset, getWidth(), getWidth() - offset, 0, useFlatLineAtBack ? 0 : offset, 0};
               int[] ey = {0, 0, getHeight()/2, getHeight() - 1, getHeight() - 1, getHeight()/2, 0};
               g.setColor(color1);
               g.drawPolygon(ex, ey, ex.length);
          }
     }
     
     public void setColors(Color c1, Color c2, Color c3){
          color1 = c1;
          color2 = c2;
          color3 = c3;
          repaint();
     }
     
     public java.lang.String getText() {
          return text;
     }
     
     public void setText(java.lang.String text) {
          this.text = text;
          repaint();
     }

     public void setUseFlatLineAtBack(boolean useFlatLineAtBack) {
          this.useFlatLineAtBack = useFlatLineAtBack;
          repaint();
     }
     public boolean isUseFlatLineAtBack() {
          return useFlatLineAtBack;
     }

     public java.lang.Runnable getOnAction() {
          return action;
     }
     
     public void setOnAction(java.lang.Runnable action) {
          this.action = action;
     }
}
