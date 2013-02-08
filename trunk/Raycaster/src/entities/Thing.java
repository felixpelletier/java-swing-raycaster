package entities;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Thing {
	
	public final static int SHADE_RESOLUTION = 64;

	protected Point2D.Double position = new Point2D.Double(1000,1000);
	private BufferedImage sprite = null;
	private BufferedImage[] shades = new BufferedImage[SHADE_RESOLUTION];
	private String type = "";
	
	
	public Thing(String type, int x, int y){
		position = new Point2D.Double(x,y);
		this.setType(type);
		try {
		    sprite = ImageIO.read(getClass().getResource(("sprites/" + type + ".png")));
		} catch (IOException e) {
			System.out.println("Image not found!");
			}
		
		for (int s = 0;s<SHADE_RESOLUTION;s++){
			//RescaleOp filter = new RescaleOp((float)s/SHADE_RESOLUTION,0, null);
			float scale = (float) Math.pow((float)s/SHADE_RESOLUTION, 2);
			float[] scales = {scale,scale,scale,1.0f};
			float[] offsets = {0.0f,0.0f,0.0f,0.0f};
			RescaleOp filter = new RescaleOp(scales,offsets, null);
			BufferedImage shaded = filter.filter(sprite, null);
			//shades[s] = filter.createCompatibleDestImage(sprite, null);
			// filter.filter(sprite, shades[s]);
			
			ColorModel cm = shaded.getColorModel();
			boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
			WritableRaster raster = shaded.copyData(null);
			
			shades[s] = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
			
			//shades[s] = new BufferedImage(1,sprite.getHeight(),sprite.getType());
			//shades[s].getGraphics().drawImage(shaded, 0, 0, null);
			
			
		}
	}
			
	public void setPosition(double x,double y){
		position.setLocation(x, y);
	}
	
	public Point2D.Double getPosition(){
		return position;
	}

	public BufferedImage getSprite() {
		return sprite;
	}
	
	public BufferedImage getShadedSprite(float brightness) {
		if (brightness > 1){
			brightness = 1;
		}
		return shades[(int)(brightness*(SHADE_RESOLUTION-1))];
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
