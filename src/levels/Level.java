package levels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Level implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3628454486243020829L;
	public final static int TILE_SIZE = 64;
	public final static String FILE_EXT = ".rlv";

	String name ="";
	byte[] map;
	
	public String getName() {
		return name;
	}
	
	public int getWidth(){
		return (int) Math.sqrt(map.length);
	}
	
	public int getHeight(){
		return getWidth();
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getMap() {
		return map;
	}

	public void setMap(byte[] map) {
		this.map = map;
	}

	public Level(String name,byte[] map){
		this.name = name;
		this.map = map;
	}
	
	public Level(byte[] map){
		this.map = map;
	}
	
	public void save() throws IOException{
		
		FileOutputStream saveFile = new FileOutputStream("tempMap" + FILE_EXT);
		
		ObjectOutputStream save = new ObjectOutputStream(saveFile);
		
		save.writeObject(this);
		
		save.close();
		
	}
	
	public static Level load(String name) throws IOException{
		FileInputStream inputFile = new FileInputStream(name + FILE_EXT);;
		ObjectInputStream inputObject = new ObjectInputStream(inputFile);
		Level inputLevel = null;
		
		try {
			inputLevel = (Level) inputObject.readObject();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return inputLevel;
	}
	
	

}
