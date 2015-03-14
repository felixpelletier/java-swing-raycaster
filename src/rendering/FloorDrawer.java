package rendering;

import java.awt.Graphics;

import entities.Player;

import levels.Level;
import utilities.MathDeg;

public class FloorDrawer extends Thread{

	final static int WIDTH = RenderWindow.WIDTH;
	final static int HEIGHT = RenderWindow.HEIGHT;
	
	private static double[] floorDist = getFloorDistanceCache();
	private static float[] floorBrightness = getFloorBrightnessCache();
	
	private double offsetTable[] = RenderWindow.getAngleOffsetTable();
	
	private int start;
	private int stop;
	byte[] floorPixels;
	Texture texture;
	WallSlice[] wall;
	Player player;
	
	public FloorDrawer(int start, int stop,byte[] floorPixels,Texture texture,WallSlice[] wall,Player player){
		this.start = start;
		this.stop = stop;
		this.floorPixels= floorPixels;
		this.texture = texture;
		this.wall = wall;
		this.player = player;
	}
	
	private static float[] getFloorBrightnessCache(){
		
		float[] brightness = new float[HEIGHT];
		
		for(int y = (HEIGHT/2) + 1; y<HEIGHT;y++){
			double ajustedDist = Math.sqrt((floorDist[y]*floorDist[y]) + ((RenderWindow.WALL_HEIGHT/2) * (RenderWindow.WALL_HEIGHT/2)));
			brightness[y] = (float) (RenderWindow.BRIGHT_MULT /  ajustedDist);
			
			if (brightness[y] > 1){
				brightness[y] = 1;
			}
		}
		
		return brightness;
		
	}
	
	private static double[] getFloorDistanceCache(){
		
		double[] distance = new double[HEIGHT];
		
		for(int y = (HEIGHT/2) + 1; y<HEIGHT;y++){
			distance[y] = ((RenderWindow.WALL_HEIGHT/2) * RenderWindow.PLANE_DIST / (y - (HEIGHT/2)));
		}
		
		return distance;
	}
	
	public void run(){
		for(int x = start;x<=stop;x++){
			double angleOffset = offsetTable[x];
			double angle = (player.getAngle() + angleOffset) % 360;
			
			if (angle<0){
				angle+=360;
			}
			
			int initialY = (HEIGHT/2) + (wall[x].getHeight()/2);

			if (initialY < HEIGHT && initialY > (HEIGHT/2)){
				while ( initialY >= HEIGHT || floorDist[initialY] > 2000){
					initialY+=1;
				}
				
				for(int y = initialY; y<HEIGHT;y++){
					double ajustedfloorDist = floorDist[y] / MathDeg.cos(angleOffset);
					//double floorEyeDistance = Point2D.distance(0, 0, floorDist, (WALL_HEIGHT/2)) / MathDeg.cos(angleOffset);
					
					float floorPosX = (float) (player.getPosition().x + MathDeg.cos(angle)*ajustedfloorDist);
					float floorPosY = (float) (player.getPosition().y + MathDeg.sin(angle)*ajustedfloorDist);
					
					//float floorPosX = 4;
					//float floorPosY = 4;
					
					//int texturePosX = (int) ((int)(floorPosX % Level.TILE_SIZE) * (texture.getWidth()-1) / Level.TILE_SIZE);
					//int texturePosY = (int) ((int)(floorPosY % Level.TILE_SIZE) * (texture.getHeight()-1) / Level.TILE_SIZE);

					
					int texturePosX = (int) ((((int)(floorPosX) & Level.TILE_SIZE-1) * (texture.getWidth()-1) / Level.TILE_SIZE)) ;
					int texturePosY = (int) ((((int)(floorPosY) & Level.TILE_SIZE-1)  * (texture.getHeight()-1) / Level.TILE_SIZE));
					
					byte[] texturePixels = texture.getPixelArray();
					
					//int pixel = texture.getShadedTexture((float) (BRIGHT_MULT / floorDist[y])).getRGB(texturePosX, texturePosY);
					//int pixel = texture.getTexture().getRGB(texturePosX, texturePosY);
					//floorBuffer.setRGB(x, y, pixel);
					
					for(int o = 0; o<3;o++){
						floorPixels[(y*WIDTH + x)*3 + o] = (byte) (texturePixels[(texturePosY*texture.getWidth() + texturePosX)*3 + o] * floorBrightness[y]);
					}
					//floorBuffer.setRGB(x, y, pixel);
				}
			}
		}
	}
	
	
}
