
public class TestMain {

	public static void main(String[] args){
		GolfBall ball = new GolfBall(new Vector(0, 0, 0), new Vector( 30, 0, 0), 1, 1 );
		for (int i = 0; i < 1000; i++){
			System.out.println(ball);
			ball.update(0.01);
		}
	}
}
