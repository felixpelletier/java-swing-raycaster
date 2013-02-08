package utilities;

public class Angle {

	private double angle;
	
	public Angle(double angle){
		setAngle(angle);
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
		if (angle < 0){
			angle+=360;
		}
	}
}
