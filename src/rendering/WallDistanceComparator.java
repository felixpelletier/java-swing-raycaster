package rendering;

import java.util.Comparator;


public class WallDistanceComparator implements Comparator<WallSlice>{

	@Override
	public int compare(WallSlice wall1, WallSlice wall2) {
		if (wall1.getDistance() < wall2.getDistance()){
			return 1;
		}
		else if (wall1.getDistance() > wall2.getDistance()){
			return -1;
		}
		else{
			return 0;
		}
	}
	
}