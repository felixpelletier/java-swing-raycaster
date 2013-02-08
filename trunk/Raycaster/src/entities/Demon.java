package entities;
import utilities.MathDeg;


public class Demon extends Monster{
	
	private float speed = 4.0f;
	
	public final static int MIN_DISTANCE = 125;
	
	
	
	int state = STILL;
	
	public Demon(String type, int x, int y) {
		super(type, x, y);
	}
	
	public void follow(Player target){
		this.target = target;
		state = FOLLOWING;
	}

	public void update(){
		switch(state){
		case FOLLOWING:
			if (target.getPosition().distance(position) > MIN_DISTANCE){
				
				speed = (float) ((1-(123/target.getPosition().distance(position))) * 8) + 0;
				//System.out.println(speed);
				double angle = MathDeg.atan2(position.y- target.getPosition().y, position.x- target.getPosition().x) % 360;
				if(Math.abs((angle - target.getAngle()) % 360) > 40 && Math.abs((angle - target.getAngle())  % 360) < 320){
					//System.out.println("moving: "  + Math.abs((angle - target.getAngle()) % 360));
					position.setLocation(position.x - MathDeg.cos(angle)*speed, position.y - MathDeg.sin(angle)*speed);
				}
				else{
					
					//System.out.println("still: " + Math.abs((angle - target.getAngle()) % 360));
				}
			}
			else{
				System.exit(0);
			}
			break;
		}
	}
}
