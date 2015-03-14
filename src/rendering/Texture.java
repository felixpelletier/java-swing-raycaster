package rendering;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.RescaleOp;
import java.io.IOException;

import javax.imageio.ImageIO;



public class Texture {
	
	public final static int SHADE_RESOLUTION = 64;

	protected BufferedImage texture = null;
	protected BufferedImage[] shades = new BufferedImage[SHADE_RESOLUTION]; 
	protected byte[][] shadePixels = new byte[SHADE_RESOLUTION][];
	
	
	public Texture(String path) throws IOException{
		
		texture = ImageIO.read(getClass().getResource((path)));

		shades = new BufferedImage[SHADE_RESOLUTION];
		
		for (int s = 0;s<SHADE_RESOLUTION;s++){
			
			float scale = (float) Math.pow((float)s/SHADE_RESOLUTION, 2);
			RescaleOp filter = new RescaleOp(scale,0, null);
			shades[s] = filter.filter(texture, null);
			shadePixels[s] = ((DataBufferByte) shades[s].getRaster().getDataBuffer()).getData();
		}
		

	}
	
	public int getWidth(){
		return texture.getWidth();
	}
	
	public int getHeight(){
		return texture.getHeight();
	}

	public byte[] getShadedPixelArray(float brightness){
		
		if (brightness > 1){
			brightness = 1;
		}
		return shadePixels[(int)(brightness*(SHADE_RESOLUTION-1))];
		
	}
	
	public byte[] getPixelArray(){
		
		return shadePixels[(SHADE_RESOLUTION-1)];
		
	}
	
	public BufferedImage getShadedTexture(float brightness){
		if (brightness > 1){
			brightness = 1;
		}
		return shades[(int)(brightness*(SHADE_RESOLUTION-1))];
	}
	
	
	public BufferedImage getTexture(){
		return texture;
	}
	
}