package levels;

public class Level {
	
	public final static int TILE_SIZE = 64;

	String name ="";
	byte[] map;
	
	public String getName() {
		return name;
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
	

}
