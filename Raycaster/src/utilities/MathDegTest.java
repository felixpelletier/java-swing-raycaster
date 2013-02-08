package utilities;

import static org.junit.Assert.*;

import org.junit.Test;

public class MathDegTest {

	@Test
	public void testRad() {
		for(long x = 0;x < 1000000000;x++){
			Math.sin(0.53242434);
		}
	}
	
	@Test
	public void testDeg() {
		for(long x = 0;x < 1000000000;x++){
			MathDeg.sin(30.50564811);
		}
	}
	
	

}
