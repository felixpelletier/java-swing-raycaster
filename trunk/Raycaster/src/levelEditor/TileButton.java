package levelEditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import levels.Level;

	public class TileButton extends JButton implements MouseListener{
		
		public static Level level;
		private int x;
		private int y;
		private int adress;
		private int state = 0;
		private final static Color[] color = {Color.white, Color.DARK_GRAY, Color.ORANGE};
		
		private static Font buttonFont = new Font("Arial",Font.PLAIN,5);
		
		public TileButton(int x, int y){
			
			this.x = x;
			this.y = y;
			this.adress = y*level.getWidth() + x;
			setState(level.getMap()[adress]);
			
			this.setFont(buttonFont);
			this.addMouseListener(this);
			 
		}
		
		private void rollState(){
			setState((state+1) % 3);
		}
		
		private void setState(int state){
			this.state = state;
			this.setBackground(color[state]);
			this.setText(Integer.toString(state));
		}

		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mousePressed(MouseEvent e) {
			if(SwingUtilities.isLeftMouseButton(e)){ 
				rollState();
			}
			
			if(SwingUtilities.isRightMouseButton(e)){ 
				setState(0);
			}
			
		}

		public void mouseReleased(MouseEvent e) {
			if(SwingUtilities.isLeftMouseButton(e)){ 
				
			}
			
			if(SwingUtilities.isRightMouseButton(e)){ 
				
			}
		}

		


		

	}

