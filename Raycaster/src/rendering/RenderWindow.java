package rendering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JFrame;

import levels.Level;

import utilities.DegTrigTable;
import utilities.MathDeg;

import entities.*;

public class RenderWindow extends JFrame implements KeyListener, MouseMotionListener {
	
	final static int WIDTH = 1280;
	final static int HEIGHT = 720;
	final static int WINDOW_WIDTH = 1280;
	final static int WINDOW_HEIGHT = 720;
	final static int WALL_HEIGHT = 400;
	final static int PLANE_DIST = 277;
	static int VIEWANGLE = 65;
	final static int THRESHOLD = 12800;
	
	static final double BRIGHT_MULT = 400;

	/*final static float sinTable[] = DegTrigTable.generateSinTable();
	final static float cosTable[] = DegTrigTable.generateCosTable();
	final static float tanTable[] = DegTrigTable.generateTanTable();*/
	
	final static DegTrigTable trigTable = new DegTrigTable();
	
	final static double offsetTable[] = getAngleOffsetTable();

	Graphics g;

	int oldMousePos = 0;

	private static final long serialVersionUID = 7470042170540119325L;
	private static final Color[] COLOR_INDEX = {null,Color.DARK_GRAY.darker(),Color.DARK_GRAY,Color.GRAY,Color.lightGray};

	//private BufferedImage[] wallTextures = new BufferedImage[2];
	private WallTexture[] wallTextures = new WallTexture[2];
	private Texture floorTexture;

	int temp = 0;
	int temp2 = 0;

	long timeStep=0;
	double lightIntensity;
	byte[] map;
	private int mapSize;
	private int tileCount;

	boolean LEFT_PRESSED = false;
	boolean RIGHT_PRESSED = false;
	boolean UP_PRESSED = false;
	boolean DOWN_PRESSED = false;

	Player player;

	private ArrayList<Thing> Things = new ArrayList<Thing>();
	private ArrayList<Monster> Monsters = new ArrayList<Monster>();
	
	public boolean initialised = false;
	
	double[] floorDist = getFloorDistanceCache();
	float[] floorBrightness = getFloorBrightnessCache();
	

	public RenderWindow(Level level) {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
		getContentPane().setBackground(Color.BLACK);
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);

		this.createBufferStrategy(3);
		
		g = this.getGraphics();

		player = new Player();

		try {
			wallTextures[0] =  new WallTexture("textures/wall1.jpg");
			wallTextures[1] =  new WallTexture("textures/wall2.jpg");
			floorTexture = new Texture("textures/tileFloor.jpg");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Things.add(new Thing("cheese",2445,443));
		Things.add(new Thing("dices",2817,2778));
		Things.add(new Thing("green_ball",225,1000));
		Monsters.add(new Demon("demon",2000,2000));
		Monsters.add(new Demon("demon2",1000,2000));
		/*Monsters.add(new Monster("red_ball",2000,2000));
		Monsters.add(new Monster("red_ball",2500,2500));
		Monsters.add(new Monster("red_ball",200,2000));*/
		
		
		Things.addAll(Monsters);

		Monsters.get(0).follow(player);
		//Monsters.get(0).follow(player);
		
		this.map = level.getMap();
		tileCount = (int)Math.sqrt(map.length);
		mapSize = tileCount * Level.TILE_SIZE;

		this.addKeyListener(this);
		addMouseMotionListener(this);
		
		initialised = true;
		
		//Raycaster.launchTime = new Date().getTime();
	}

	public void render(){

		
		timeStep++;
		lightIntensity = 0.40 * Math.cos(timeStep / 50.0f) + 0.6;
		
		//updateMonsters(Monsters);
		
		BufferStrategy bf = this.getBufferStrategy();
		//g.clearRect(0, 0, WIDTH, HEIGHT);

		try 
		{
			g = bf.getDrawGraphics();
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, WIDTH*2, HEIGHT*2);

			Collections.sort(Things, new Comparator<Thing>() {
				@Override public int compare(Thing thing1, Thing thing2) {
					return (int) (player.getDistance(thing2.getPosition()) - player.getDistance(thing1.getPosition()));
				}
			});

			WallSlice[] wall= new WallSlice[WIDTH];

			for (int x = 0; x < WIDTH; x++) {
				wall[x] = getWallSlice(x);
			}

			drawFloors(g,floorTexture, wall);

			Arrays.sort(wall,new WallDistanceComparator());

			if (LEFT_PRESSED){
				player.rotateLeft();
			}
			if (RIGHT_PRESSED){
				player.rotateRight();
			}
			if (UP_PRESSED){
				if(wall[WIDTH-1].getDistance() > Player.SPEED*2){
					player.moveForward();
				}
			}
			if (DOWN_PRESSED){
				double dist= Math.abs(getWallSlice((int) ((180 + ((double)VIEWANGLE/2)) / ((double)VIEWANGLE/WIDTH))).getDistance());
				if(dist > Player.SPEED*2){
					player.moveBackward();
				}
			}

			int thingIndex = 0;
			double nextThingDist= player.getDistance(Things.get(0).getPosition());

			for (int x = 0; x < WIDTH; x++) {

				while (thingIndex < Things.size() && wall[x].getDistance() < nextThingDist){
					drawThing(g,Things.get(thingIndex));
					thingIndex++;
					if (thingIndex < Things.size()){
						nextThingDist= player.getDistance(Things.get(thingIndex).getPosition());
					}
				}

				drawWall(g,wall[x]);

			}

			while (thingIndex < Things.size()){
				drawThing(g,Things.get(thingIndex));
				thingIndex++;
				if (thingIndex < Things.size()){
					nextThingDist= player.getDistance(Things.get(thingIndex).getPosition());
				}
			}

		} finally {
			g.dispose();
		}


		bf.show();

	}
	
	private float[] getFloorBrightnessCache(){
		
		float[] brightness = new float[HEIGHT];
		
		for(int y = (HEIGHT/2) + 1; y<HEIGHT;y++){
			brightness[y] = (float) (BRIGHT_MULT / floorDist[y]);
			
			if (brightness[y] > 1){
				brightness[y] = 1;
			}
		}
		
		return brightness;
	}
	
	private double[] getFloorDistanceCache(){
		
		double[] distance = new double[HEIGHT];
		
		for(int y = (HEIGHT/2) + 1; y<HEIGHT;y++){
			distance[y] = ((WALL_HEIGHT/2) * PLANE_DIST / (y - (HEIGHT/2)));
		}
		
		return distance;
	}
	
	/*private void drawFloors(Graphics g,Texture texture, WallSlice[] wall) {
		
		BufferedImage floorBuffer = new BufferedImage(WIDTH,HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		
		byte[] floorPixels = ((DataBufferByte)floorBuffer.getRaster().getDataBuffer()).getData();
		
		//byte[] floorPixels = new byte[10000000];
		
		//DataBufferByte textureDataBuffer = (DataBufferByte) texture.getTexture().getRaster().getDataBuffer();
		//byte[] texturePixels = textureDataBuffer.getData();
		
		for(int x = 0;x<WIDTH;x++){
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
					double ajustedfloorDist = (int) (floorDist[y] / MathDeg.cos(angleOffset));
					
					//double floorEyeDistance = Point2D.distance(0, 0, floorDist, (WALL_HEIGHT/2)) / MathDeg.cos(angleOffset);
					
					float floorPosX = (float) (player.getPosition().x + MathDeg.cos(angle)*ajustedfloorDist);
					float floorPosY = (float) (player.getPosition().y + MathDeg.sin(angle)*ajustedfloorDist);
					
					//float floorPosX = 4;
					//float floorPosY = 4;
					
					//int texturePosX = (int) ((int)(floorPosX % Level.TILE_SIZE) * (texture.getWidth()-1) / Level.TILE_SIZE);
					//int texturePosY = (int) ((int)(floorPosY % Level.TILE_SIZE) * (texture.getHeight()-1) / Level.TILE_SIZE);

					
					int texturePosX = (int) (((int)(floorPosX) & Level.TILE_SIZE-1) * (texture.getWidth()-1) / Level.TILE_SIZE);
					int texturePosY = (int) (((int)(floorPosY) & Level.TILE_SIZE-1)  * (texture.getHeight()-1) / Level.TILE_SIZE);
					
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
		
		g.drawImage(floorBuffer, getInsets().left, getInsets().top, null);
		
		
		
	}*/
	
	private void drawFloors(Graphics g,Texture texture, WallSlice[] wall) {
		
	int threadCount = Runtime.getRuntime().availableProcessors();
	
	BufferedImage floorBuffer = new BufferedImage(WIDTH,HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
	
	byte[] floorPixels = ((DataBufferByte)floorBuffer.getRaster().getDataBuffer()).getData();
	
	FloorDrawer[] drawers = new FloorDrawer[threadCount];
	for(int t = 0;t<threadCount;t++){
		drawers[t] = new FloorDrawer((int) (WIDTH/threadCount) * t,(int) (WIDTH/threadCount) * (t+1) - 1,floorPixels,texture,wall,player);
		drawers[t].start();
		
	}
	
	for (FloorDrawer drawer : drawers) {
		  try {
			drawer.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	
	//byte[] floorPixels = new byte[10000000];
	
	//DataBufferByte textureDataBuffer = (DataBufferByte) texture.getTexture().getRaster().getDataBuffer();
	//byte[] texturePixels = textureDataBuffer.getData();
	
	
	g.drawImage(floorBuffer, getInsets().left, getInsets().top, null);
	
}

	public void updateMonsters(ArrayList<Monster> Monsters){
		for(Monster monster : Monsters){
			monster.update();
		}
	}

	public void drawThings(Graphics g,ArrayList<Thing> Things){

		Collections.sort(Things, new Comparator<Thing>() {
			@Override public int compare(Thing thing1, Thing thing2) {
				return (int) (player.getDistance(thing2.getPosition()) - player.getDistance(thing1.getPosition()));
			}
		});

		for (int x = 0; x<Things.size();x++){
			drawThing(g,Things.get(x));
		}

	}

	public void drawThing(Graphics g,Thing thing){

		double distance = player.getDistance(thing.getPosition());
		int height = (int) (thing.getSprite().getHeight() / distance * 277);
		int width = height * thing.getSprite().getWidth() / thing.getSprite().getHeight();

		double angle = MathDeg.atan2(thing.getPosition().y- player.getPosition().y, thing.getPosition().x- player.getPosition().x)-player.getAngle();
		if(angle < -180){
			angle+=360;
		}

		int pos = (int)(angle*(WIDTH)/(VIEWANGLE)) + WIDTH/2 ;
		//g.drawLine(x + getInsets().left, (HEIGHT-wallHeight) / 2 + getInsets().top, x + getInsets().left, (HEIGHT+wallHeight) / 2 + getInsets().top);
		BufferedImage shadedThing = thing.getShadedSprite((float) (BRIGHT_MULT / distance));

		g.drawImage(shadedThing, (pos-width/2) + getInsets().left, (HEIGHT-height) / 2 + getInsets().top, (pos+width/2) + getInsets().left,(HEIGHT+height) / 2 + getInsets().top,0,0,shadedThing.getWidth(),shadedThing.getHeight(), null);
	
	}

	public static double[] getAngleOffsetTable(){
		double[] offsets = new double[WIDTH];
		for (int x = 0; x < WIDTH; x++){
			offsets[x] = getAngleOffset(x);
		}
		return offsets;
	}
	
	private static double getAngleOffset(int x){
		return x*((double)VIEWANGLE/WIDTH) - VIEWANGLE/2;
	}

	public WallSlice getWallSlice(int x){

		double angleOffset = getAngleOffset(x);
		double angle = (player.getAngle() + angleOffset) % 360;

		if (angle < 0){
			angle += 360;
		}

		if(angleOffset < 0){
			angleOffset+=360;
		}

		Point2D.Double wallPosition = getWallPosition(angle);
		double wallDistance = player.getDistance(wallPosition.x,wallPosition.y) * MathDeg.cos(angleOffset);

		return new WallSlice(x,wallPosition,wallDistance,getWall(wallPosition.x,wallPosition.y));

		//drawWall(g,x,wallDistance,COLOR_INDEX[getWall(wallPosition.x,wallPosition.y)]);

	}


	private void drawWall(Graphics g,WallSlice wall){

		g.setColor(COLOR_INDEX[wall.getTexture()]);

		WallTexture wallTexture = wallTextures[wall.getTexture()%2];

		int wallHeight = wall.getHeight();
		int wallOffset;

		if ((wall.getLocation().x+1) % Level.TILE_SIZE <= 1){
			wallOffset = (int) (((wall.getLocation().y+1) % Level.TILE_SIZE) * wallTexture.getWidth() / Level.TILE_SIZE);
		}
		else{
			wallOffset = (int) (((wall.getLocation().x+1) % Level.TILE_SIZE) * wallTexture.getWidth() / Level.TILE_SIZE);
		}

		//int wallOffset = (int) (((wall.getLocation().x+1) % Level.TILE_SIZE) + ((wall.getLocation().y+1) % Level.TILE_SIZE) * wallTexture.getWidth() / Level.TILE_SIZE);

		if (wallHeight > 1){

			BufferedImage shadedSlice = wallTexture.getShadedSlice(wallOffset, (float) (BRIGHT_MULT / wall.getDistance()));

			g.drawImage(shadedSlice, wall.getPosition() + getInsets().left, (HEIGHT-wallHeight) / 2 + getInsets().top, wall.getPosition() + getInsets().left + 1,(HEIGHT+wallHeight) / 2 + getInsets().top,0,0,1,wallTexture.getHeight(), null);

		}

	}

	@SuppressWarnings("unused")
	private BufferedImage shade(BufferedImage src, double distance){

		float brightness = (float) (BRIGHT_MULT / distance);

		if (brightness>1){
			brightness = 1.0f;
		}

		RescaleOp filter = new RescaleOp(brightness,0, null);
		return filter.filter(src, null);

		//return src;
	}
	
	private BufferedImage getShadeCache(){
		return null;
		
	}

	private Point2D.Double getWallPosition(double angle) {

		double nextWallX;
		double nextWallY;
		double xWallPosition = player.getPosition().x;
		double yWallPosition = player.getPosition().y;

		int xMult = 0;
		int yMult = 0;

		if (angle < 90 || angle > 270){
			xMult = 1;
		}
		else if(angle != 90 && angle != 270){
			xMult = -1;
		}

		if (angle < 180){
			yMult = 1;
		}
		else if(angle != 0 && angle != 180){
			yMult = -1;
		}


		if (xMult == 1){
			xWallPosition = Math.floor(player.getPosition().x/Level.TILE_SIZE) * Level.TILE_SIZE + 64;
		}
		else{
			xWallPosition = Math.floor(player.getPosition().x/Level.TILE_SIZE) * Level.TILE_SIZE -1;
		}

		yWallPosition =	player.getPosition().y + (xWallPosition-player.getPosition().x)*MathDeg.tan(angle);
		double yStep = Math.abs(Level.TILE_SIZE*MathDeg.tan(angle)) * yMult;

		while(getWall(xWallPosition,yWallPosition) == 0 && player.getDistance(xWallPosition,yWallPosition) < THRESHOLD){
			xWallPosition += Level.TILE_SIZE * xMult;
			yWallPosition += yStep;
		}

		if (yMult == 1){
			nextWallY = Math.floor(player.getPosition().y/Level.TILE_SIZE) * Level.TILE_SIZE + 64;
		}
		else{
			nextWallY = Math.floor(player.getPosition().y/Level.TILE_SIZE) * Level.TILE_SIZE - 1 ;
		}

		nextWallX =	player.getPosition().x + (nextWallY - player.getPosition().y)/MathDeg.tan(angle);
		double xStep = Math.abs(Level.TILE_SIZE/MathDeg.tan(angle)) * xMult;

		while(getWall(nextWallX,nextWallY) == 0 && player.getDistance(nextWallX,nextWallY) < THRESHOLD){
			nextWallY += Level.TILE_SIZE * yMult;
			nextWallX += xStep;
		}

		if(player.getDistance(nextWallX,nextWallY) < player.getDistance(xWallPosition,yWallPosition)){
			xWallPosition = nextWallX;
			yWallPosition = nextWallY;
		}

		return new Point2D.Double(xWallPosition,yWallPosition);


	}

	private byte getWall(double xWallPosition, double yWallPosition) {
		if(xWallPosition >= 0 && xWallPosition < mapSize && yWallPosition >= 0 && yWallPosition < mapSize){
			return map[(int)(Math.floor(yWallPosition/Level.TILE_SIZE) * tileCount + (int)(xWallPosition/Level.TILE_SIZE))];
		}
		else{
			return 0;
		}
	}

	public void paint(Graphics g) {

		super.paint(g);



	}

	public void keyPressed(KeyEvent e) {

		switch(e.getKeyCode()){
		case KeyEvent.VK_LEFT:
			//player.moveLeft();
			LEFT_PRESSED = true;
			//player.rotateLeft();
			break;
		case KeyEvent.VK_RIGHT:
			RIGHT_PRESSED = true;
			//player.moveRight();
			//player.rotateRight();
			break;
		case KeyEvent.VK_UP:
			UP_PRESSED = true;
			//player.moveForward();
			break;
		case KeyEvent.VK_DOWN:
			DOWN_PRESSED = true;
			//player.moveBackward();
			break;
		case KeyEvent.VK_W:
			temp++;
			break;
		case KeyEvent.VK_S:
			temp--;
			break;
		case KeyEvent.VK_E:
			temp2++;
			break;
		case KeyEvent.VK_D:
			temp2--;
			break;
		case KeyEvent.VK_Q:
			VIEWANGLE++;
			break;
		case KeyEvent.VK_A:
			VIEWANGLE--;
			break;
		case KeyEvent.VK_P:
			System.out.println(player.getPosition());
			System.out.println(player.getAngle());
			System.out.println("VIEWANGLE= " + VIEWANGLE);
			System.out.println("temp= " + temp + " temp2= " + temp2);
			break;
		}



	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()){
		case KeyEvent.VK_LEFT:
			LEFT_PRESSED = false;
			break;
		case KeyEvent.VK_RIGHT:
			RIGHT_PRESSED = false;
			break;
		case KeyEvent.VK_UP:
			UP_PRESSED = false;
			break;
		case KeyEvent.VK_DOWN:
			DOWN_PRESSED = false;
			break;
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {

		/*double move = (e.getX()-oldMousePos) * 0.1;

			player.setAngle(player.getAngle() + move);

			oldMousePos = e.getX();

			mouseCenterer.mouseMove(WIDTH, HEIGHT);*/

	}

}
