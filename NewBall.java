


/**
 * Created by Jade and Presian and Misha on 19-04-2016.
 */


public class NewBall{

    private static double FRICTION_COEFFICIENT = 10;
    private static double BOUNCE_COEFFICIENT = 0.95;
    public static double MAX_KICK_SPEED = 50;

    //private double angle;
    private Vector3D position;
    private Vector3D velocity;
    private double radius;
    private double mass;
    private int score;
    public enum State{PLAYING, START, HOLE};
    private State state;

    private Map map;

    public NewBall(Vector3D position, Vector3D velocity, double radius, double mass, Map map){
        this.position = position;
        this.velocity = velocity;
        this.radius = radius;
        this.mass = mass;
        this.state = State.START;
        this.map = map;
    }

    public Vector3D getVelocity() { return velocity; }
    public Vector3D getPosition() { return position; }
    public double getRadius() { return radius; }
    public double getMass() { return mass; }
    public State getState() { return state; }
    public int getScore() { return score; }

    public void addScore(){ score++; }
    public void addVelocity(Vector3D dv){
        this.velocity.add(dv);
    }

    public boolean collideTriangle(Vector3D A, Vector3D B, Vector3D C) {
        A.sub(this.position);
        B.sub(this.position);
        C.sub(this.position);
        double rr = this.radius*this.radius;

        Vector3D V = Vector3D.substract(B,A).cross(Vector3D.substract(C,A));

        double d = A.dot(V);
        double e = V.dot(V);
        boolean sep1 = d*d > rr*e;

        double aa = A.dot(A);
        double ab = A.dot(B);
        double ac = A.dot(C);
        double bb = B.dot(B);
        double bc = B.dot(C);
        double cc = C.dot(C);

        boolean sep2 = (aa > rr) & (ab > aa) & (ac > aa);
        boolean sep3 = (bb > rr) & (ab > bb) & (bc > bb);
        boolean sep4 = (cc > rr) & (ac > cc) & (bc > cc);

        Vector3D AB = Vector3D.substract(B, A);
        Vector3D BC = Vector3D.substract(C, B);
        Vector3D CA = Vector3D.substract(C, A);

        double d1 = ab - aa;
        double d2 = bc - bb;
        double d3 = ac - cc;

        double e1 = AB.dot(AB);
        double e2 = BC.dot(BC);
        double e3 = CA.dot(CA);

        Vector3D Q1 = Vector3D.substract(Vector3D.mult(A,e1), Vector3D.mult(AB,d1));
        Vector3D Q2 = Vector3D.substract(Vector3D.mult(B,e2), Vector3D.mult(BC,d2));
        Vector3D Q3 = Vector3D.substract(Vector3D.mult(C,e3), Vector3D.mult(CA,d3));

        Vector3D QC = Vector3D.substract(Vector3D.mult(C,e1), Q1);
        Vector3D QA = Vector3D.substract(Vector3D.mult(A,e2), Q2);
        Vector3D QB = Vector3D.substract(Vector3D.mult(B,e3), Q3);


        boolean sep5 = Q1.dot(Q1) > rr * e1 * e1 && Q1.dot(QC) > 0;
        boolean sep6 = Q2.dot(Q2) > rr * e2 * e2 && Q2.dot(QA) > 0;
        boolean sep7 = Q3.dot(Q3) > rr * e3 * e3 && Q3.dot(QB) > 0;

        boolean separated = (sep1 || sep2 || sep3 || sep4 || sep5 || sep6 || sep7);

        return separated;
    }

    public void update(double deltaTime){
    }

    public void reset(){
    }

    public void kick(Vector3D dv){
    }



    public static void main(String[] args){
        Vector3D v1 = new Vector3D(0,0,0);
        Vector3D v2 = new Vector3D(2, 0, 0);
        Vector3D v3 = new Vector3D(2, 2, 0);

        Vector3D position = new Vector3D(1.5, 0.5, 0);
        Map map = new Map();
        Vector3D velocity = new Vector3D(0, 0, 0);

        NewBall ball = new NewBall(position, velocity, 0.5, 0, map);
        System.out.println(ball.collideTriangle(v1, v2, v3));

    }


}