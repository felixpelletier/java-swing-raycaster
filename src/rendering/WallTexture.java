package rendering;
import java.awt.image.BufferedImage;
import java.io.IOException;



public class WallTexture extends Texture {

	private BufferedImage[][] slice;
	
	public WallTexture(String path) throws IOException{
		
		super(path);
		
		slice = new BufferedImage[SHADE_RESOLUTION][texture.getWidth()];
		
		for (int s = 0;s<SHADE_RESOLUTION;s++){
			
			for(int i = 0;i<texture.getWidth();i++){
				slice[s][i] = new BufferedImage(1,texture.getHeight(),texture.getType());
				slice[s][i].getGraphics().drawImage(shades[s], 0, 0, 1, texture.getHeight(), i, 0, i+1, texture.getHeight(), null);
			}
		}
		
	}
	
	public BufferedImage getTextureSlice(int x){
		return slice[0][x];
	}
	
	public BufferedImage getShadedSlice(int x, float brightness){
		if (brightness > 1){
			brightness = 1;
		}
		return slice[(int)(brightness*(SHADE_RESOLUTION-1))][x];
	}

}
