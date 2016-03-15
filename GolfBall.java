import java.util.ArrayList;

/**
 * Created by Jade on 14-03-16.
 */
public class GolfBall {

    private static double FRICTION = 1.0;

    //private double angle;
    private Vector position;
    private Vector velocity;
    private double radius;
    private double mass;

    private Map map;

    public GolfBall(Vector position, Vector velocity, double radius, double mass, Map map){
        this.position = position;
        this.velocity = velocity;
        this.radius = radius;
        this.mass = mass;

        this.map = map;
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

        this.bounce(this.map.getWalls());

        this.applyFriction();

        position.setX(position.getX() + (velocity.getX() * deltaTime));
        position.setY(position.getY() + (velocity.getY() * deltaTime));
        position.setZ(position.getZ() + (velocity.getZ() * deltaTime));

    }

    public void applyFriction(){
        velocity.setX((velocity.getX())*FRICTION);
        velocity.setY((velocity.getY())*FRICTION);
        velocity.setZ((velocity.getZ())*FRICTION);

    }

    public boolean isInside(double x, double y){
        if ((Math.pow((x-this.position.getX()), 2) + Math.pow((y-this.position.getY()), 2) <= radius*radius ))
            return true;

        return false;
    }

    public double[] isColliding(double x1, double y1, double x2, double y2){
        // double deltaX = x1 - x2;
        // double deltaY = (y1 - y2);
        // double constant = 0.001;

        // for (int j = 0; (((j*constant*deltaX) + x1) < deltaX)  && (((j*constant*deltaY)+ y1) < deltaY); j++){
        //     if (this.isInside(x1 + deltaX*constant, y1 + deltaY*constant)){
        //         double coordinates[] = new double[2];
        //         coordinates[0] = x1 + deltaX*constant;
        //         coordinates[1] = y1 + deltaY*constant;
        //         return coordinates;
        //     }
        // }
        Vector seg_a = new Vector(x1, y1, 0);
        Vector seg_b = new Vector(x2, y2, 0);
        Vector seg_v = seg_b.add(seg_a.negative());
        Vector pt_v  = position.add(seg_a.negative());

        Vector closest = null;
        double len_closest = pt_v.dotProduct(seg_v.normalize());
        if(len_closest < 0){
            closest = seg_a;
        }else if(len_closest > seg_v.getLength()){
            closest = seg_b;
        }else{
            closest = seg_v.normalize().crossProduct(len_closest);
            closest = closest.add(seg_a);
        }
        Vector dist_v = position.add(closest.negative());
        if(dist_v.getLength() <= radius){
            return new double[2];
        }else{
            return null;
        }
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
            double x1 = walls.get(i);
            double y1 = walls.get(i+1);
            double x2 = walls.get(i+2);
            double y2 = walls.get(i+3);

            // double[] xs = linspace(x1, x2, 1000);
            // double[] ys = linspace(y1, y2, 1000);

            double[] coordinates = this.isColliding(x1,y2,x2,y2);
            if ( coordinates != null){
                System.out.println("hit wall");
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
