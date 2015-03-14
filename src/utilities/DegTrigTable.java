package utilities;
public class DegTrigTable {
	
	private static final int RESOLUTION = 2048;
	
	private static final double CONV_CONST = RESOLUTION / 360;
	
	private static final double AMPLITUDE = 2147483647;
	
	private static int[] sinTable = new int[RESOLUTION];
	private static int[] cosTable = new int[RESOLUTION];
	
	public DegTrigTable(){
		generateSinTable();
		generateCosTable();
	}
	
	private static void generateSinTable() {

		for (int x = 0; x < RESOLUTION; x++) {
			sinTable[x] = (int) (MathDeg.sin(x / CONV_CONST) * 2147483647) ;
		}

	}
	
	public double sin(double angle){
		
		angle = normalisedAngle(angle);
		
		int index = getIndex(angle);
		
		return sinTable[index] / AMPLITUDE;
		
	}

	private static void generateCosTable() {

		for (int x = 0; x < RESOLUTION; x++) {
			cosTable[x] = (int) (MathDeg.cos(x / CONV_CONST) * 2147483647) ;
		}

	}
	
	public double cos(double angle){
		
		angle = normalisedAngle(angle);
		
		int index = getIndex(angle);
		
		if (index < 0){
			System.out.println(angle);
		}
		return cosTable[index] / AMPLITUDE;
		
	}
	
	private static double normalisedAngle(double angle) {
		if (angle < 0){
			angle += 360;
		}
		
		return angle % 360;
	}

	private static int getIndex(double angle){
		return (int) (angle * CONV_CONST);
	}
	
	public static void main(String[] args) {

		
	}
	

	/*private static void generateTanTable() {

		float[] table = new float[360];
		for (int x = 0; x < 360; x++) {
			table[x] = (float) Math.tan(x*Math.PI/180);
		}

		return table;

	}*/

}
