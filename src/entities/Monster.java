package entities;

public abstract class Monster extends Thing {

	public final static int STILL = 0;
	public final static int ROAMING = 1;
	public final static int FOLLOWING = 2;
	
	protected float speed;

	protected int state = STILL;

	Player target;
	
	public void follow(Player target){
		this.target = target;
		state = FOLLOWING;
	}
	
	public Monster(String type, int x, int y) {
		super(type, x, y);
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public abstract void update();

}
