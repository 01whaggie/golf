/**
 * Created by Jade on 14-03-16.
 */
public class GolfBall {

    //private double angle;
    private Vector position;
    private Vector velocity;
    private double radius;
    private double mass;

    public GolfBall(Vector position, Vector velocity, double radius, double mass ){
        this.position = position;
        this.velocity = velocity;
        this.radius = radius;
        this.mass = mass;
    }

    public Vector getVelocity() { return velocity; }
    public Vector getPosition() { return position; }
    public double getRadius() { return radius; }
    public double getMass() { return mass; }

    public void update(double deltaTime){
        if ( velocity.getLength() < 0.01) {
            velocity.setX(0);
            velocity.setY(0);
            velocity.setZ(0);
        }

        else {
            this.applyFriction();
            position.setX(position.getX() + (velocity.getX() * deltaTime));
            position.setY(position.getY() + (velocity.getY() * deltaTime));
            position.setZ(position.getZ() + (velocity.getZ() * deltaTime));
        }


    }

    public void applyFriction(){
        velocity.setX((velocity.getX())*0.95);
        velocity.setY((velocity.getY())*0.95);
        velocity.setZ((velocity.getZ())*0.95);

    }

    public String toString(){
        return String.valueOf(position.getX()) ; //+ " " + position.getY() + " " + position.getZ();
    }

}
