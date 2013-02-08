
package utilities;

public class MathDeg{

	private final static float CONV_CONST = (float) (Math.PI/180); 
	
	public static double sin(double angle){
		return Math.sin(Math.toRadians(angle));
		//return Math.sin(angle*CONV_CONST);
	}
	
	public static double cos(double angle){
		//return Math.cos(angle*CONV_CONST);
		return Math.cos(Math.toRadians(angle));
	}
	
	public static double tan(double angle){
		return Math.tan(angle*CONV_CONST);
	}
	public static double atan2(double y,double x){
		//return Math.atan2(y, x) / CONV_CONST; 
		return Math.toDegrees(Math.atan2(y, x)); 
	}
	
}

