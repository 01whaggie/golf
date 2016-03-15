import java.util.ArrayList;

/**
 * Created by Jade on 14-03-16.
 */
public class GolfBall {

    private static double FRICTION_COEFFICIENT = 1.5;

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
        
        // friction
        // this.applyFriction();
        double friction = FRICTION_COEFFICIENT * mass;
        Vector frictionForce = velocity.negative().normalize().crossProduct(friction);
        Vector dv = frictionForce.crossProduct(deltaTime / mass);

        velocity = velocity.add(dv);

        position.setX(position.getX() + (velocity.getX() * deltaTime));
        position.setY(position.getY() + (velocity.getY() * deltaTime));
        position.setZ(position.getZ() + (velocity.getZ() * deltaTime));

    }

    public void applyFriction(){
        velocity.setX((velocity.getX())*FRICTION_COEFFICIENT);
        velocity.setY((velocity.getY())*FRICTION_COEFFICIENT);
        velocity.setZ((velocity.getZ())*FRICTION_COEFFICIENT);

    }

    public boolean isInside(double x, double y){
        if ((Math.pow((x-this.position.getX()), 2) + Math.pow((y-this.position.getY()), 2) <= radius*radius ))
            return true;

        return false;
    }

    public Vector isColliding(double x1, double y1, double x2, double y2){
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
            Vector offset = dist_v.normalize().crossProduct(radius-dist_v.getLength());
            return offset;
        }else{
            return null;
        }
    }

    public void bounce(ArrayList<Double> walls){
        for (int i = 0; i < walls.size()/4; i++){
            double x1 = walls.get(4*i);
            double y1 = walls.get(4*i+1);
            double x2 = walls.get(4*i+2);
            double y2 = walls.get(4*i+3);

            Vector offset = this.isColliding(x1,y1,x2,y2);
            if ( offset != null){
                this.position = position.add(offset);
                Vector wall_start = new Vector(x1, y1, 0);
                Vector wall_end = new Vector(x2, y2, 0);
                Vector wall = wall_end.add(wall_start.negative());
                Vector vectorProjected = (wall.normalize()).crossProduct(velocity.dotProduct(wall.normalize()));
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
