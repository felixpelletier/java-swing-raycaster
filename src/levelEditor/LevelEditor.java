package levelEditor;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import levels.Level;

public class LevelEditor extends JFrame{
	
	private JPanel pan = new JPanel();
	private JPanel mapPan = new JPanel();
	private JPanel buttonPan = new JPanel();
	private Level level;
	
	
	public LevelEditor(){
			
		
		this.addWindowListener(new WindowAdapter(){

			public void windowClosing(WindowEvent e)
			{
				try {
					level.save();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});
			
			this.setTitle("Raycaster Map Editor");
		    this.setSize(800, 800);
		    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    this.setLocationRelativeTo(null);
		    
		    pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
		    pan.add(mapPan);
		    pan.add(buttonPan);
		    this.setContentPane(pan);
		    
		    this.setVisible(true);
		}
	
	private void loadLevel(Level level){
		
		this.level = level;
		TileButton.level = level;
		
		GridLayout gl = new GridLayout(level.getWidth(), level.getHeight());
		gl.setHgap(1);
		gl.setVgap(1);
		
		mapPan.setLayout(gl);
		
		for (int x = 0;x<level.getWidth();x++){
			for(int y = 0;y<level.getHeight();y++){
				mapPan.add(new TileButton(x,y));
			}
		}
		
		this.pack();
		
	}
	
	public static void main(String[] args) {

		Level tempLevel = null;
		try {
			tempLevel = Level.load("tempMap");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new LevelEditor().loadLevel(tempLevel);
		
		
	}
}
