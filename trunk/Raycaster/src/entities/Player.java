package entities;

import java.awt.geom.Point2D;

import utilities.MathDeg;


public class Player{

	public static final double SPEED = 10;
	private String name;
	//private Point2D.Double position = new Point2D.Double(226.07927744637215,357.9295005055905);
	//private double angle = 60;
	
	private Point2D.Double position = new Point2D.Double(1982.3176763241142,2306.0915650974084);
	private double angle = 300;
	double angleStep = 3;

	public Player(String newName){
		name = newName;
	}

	public Player(double x,double y){
		position = new Point2D.Double(x,y);
	}

	public Player(double x,double y,double initAngle){
		setAngle(initAngle);
		position = new Point2D.Double(x,y);
	}

	public Player(String newName,double x,double y,double initAngle){
		position = new Point2D.Double(x,y);
		name = newName;
	}

	public Player(String newName,double x,double y){
		name = newName;
		position = new Point2D.Double(x,y);
	}

	public Player(){

	}

	public void setAngle(double newAngle){
		angle = newAngle % 360;
		if (angle < 0){
			angle += 360;
		}
	}

	public double getAngle(){
		return angle;
	}

	public String getName(){
		return name;
	}

	public Point2D.Double getPosition(){
		return position;
	}
	
	public void setPosition(double x,double y){
		position.setLocation(x, y);
	}
	
	public void rotateLeft(){
		setAngle(angle-angleStep);
	}
	
	public void rotateRight(){
		setAngle(angle+angleStep);
	}
	
	public void moveForward(){
		position.setLocation(position.x + MathDeg.cos(angle)*SPEED, position.y + MathDeg.sin(angle)*SPEED);
	}
	
	public void moveBackward(){
		position.setLocation(position.x - MathDeg.cos(angle)*SPEED, position.y - MathDeg.sin(angle)*SPEED);
	}
	
	public void moveLeft(){
		position.setLocation(position.x - MathDeg.cos(angle)*SPEED, position.y - MathDeg.sin(angle)*SPEED);
	}
	
	public void moveRight(){
		position.setLocation(position.x + MathDeg.cos(angle)*SPEED, position.y + MathDeg.sin(angle)*SPEED);
	}
	
	public double getDistance(double x,double y){
		return position.distance(x, y);
	}
	
	public double getDistance(Point2D.Double point){
		return position.distance(point);
	}
	
	public void addPosition(double x,double y){
		position.setLocation(position.x + x,position.y + y);
	}




}