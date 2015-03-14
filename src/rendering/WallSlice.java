package rendering;
import java.awt.geom.Point2D;



public class WallSlice {

	private static final double WALL_HEIGHT = 400;
	private int position;
	private double distance;
	private Point2D.Double location;
	private byte texture;
	
	
	public int getHeight(){
		return (int) (WALL_HEIGHT / distance * 277);
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public byte getTexture() {
		return texture;
	}

	public void setTexture(byte texture) {
		this.texture = texture;
	}
	
	public WallSlice(int position,Point2D.Double location,double distance,byte texture){
		this.position = position;
		this.location = location;
		this.distance = distance;
		this.texture = texture;
	}

	public Point2D.Double getLocation() {
		return location;
	}

	public void setLocation(Point2D.Double location) {
		this.location = location;
	}
	
}

