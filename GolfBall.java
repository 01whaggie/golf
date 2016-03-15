import java.util.ArrayList;

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

    public boolean isInside(double x, double y){
        if ((Math.pow((x-this.position.getX()), 2) + Math.pow((y-this.position.getY()), 2) <= radius*radius ))
        return true;

        return false;
    }

    public double[] isColliding(ArrayList<Double> wall){
        double x1 = wall.get(0);
        double y1 = wall.get(1);
        double x2 = wall.get(2);
        double y2 = wall.get(3);
        double deltaX = x1 - x2;
        double deltaY = (y1 - y2);
        double constant = 0.001;

        for (int j = 0; (((j*constant*deltaX) + x1) < deltaX)  && (((j*constant*deltaY)+ y1) < deltaY); j++){
            if (this.isInside(x1 + deltaX*constant, y1 + deltaY*constant)){
                double coordinates[] = new double[2];
                coordinates[0] = x1 + deltaX*constant;
                coordinates[1] = y1 + deltaY*constant;
                return coordinates;

            }


        }

        return null;
    }

    public static double[] linspace(double a, double b, int points) {
        double max = Math.max(a, b);
        double min = Math.min(a, b);
        double[] d = new double[points];
        for (int i = 0; i < points; i++){
            d[i] = min + i * (max - min) / (points - 1);
        }
        return d;
    }

    public void bounce(ArrayList<Double> walls){
        for (int i = 0; i < walls.size() - 3; i+=4){
            ArrayList<Double> wall = new ArrayList<>();
            double x1 = walls.get(i);
            double y1 = walls.get(i+1);
            double x2 = walls.get(i+2);
            double y2 = walls.get(i+3);

            double[] xs = linspace(x1, x2, 1000);
            double[] ys = linspace(y1, y2, 1000);

            double[] coordinates = this.isColliding(wall);
            if ( coordinates != null){
                Vector vectorWall = new Vector(x2 - coordinates[0], y2 - coordinates[1], 0);
                Vector vectorProjected = (vectorWall.normalize()).crossProduct(velocity.dotProduct(vectorWall.normalize()));
                Vector vectorParallelToNormal = velocity.add(vectorProjected.negative());
                vectorParallelToNormal = vectorParallelToNormal.negative();
                velocity = vectorParallelToNormal.add(vectorProjected);
            }

        }
    }

    public String toString(){
        return String.valueOf(position.getX()) ; //+ " " + position.getY() + " " + position.getZ();
    }

}
