import java.util.ArrayList;

/**
 * Created by Jade on 14-03-16.
 */
public class GolfBall {

    private static double FRICTION_COEFFICIENT = 10;
    private static double BOUNCE_COEFFICIENT = 0.95;
    public static double MAX_KICK_SPEED = 50;

    //private double angle;
    private Vector3D position;
    private Vector3D velocity;
    private double radius;
    private double mass;

    private Map map;

    public GolfBall(Vector3D position, Vector3D velocity, double radius, double mass, Map map){
        this.position = position;
        this.velocity = velocity;
        this.radius = radius;
        this.mass = mass;

        this.map = map;
    }

    public Vector3D getVelocity() { return velocity; }
    public Vector3D getPosition() { return position; }
    public double getRadius() { return radius; }
    public double getMass() { return mass; }

    public void addVelocity(Vector3D dv){
        this.velocity.add(dv);
    }

    public void update(double deltaTime){
        // doing this will make the physics slower, but also more stable
        // by increasing the steps the stability increases, as does the run-time
        // decreasing the steps does the opposite
        int steps = 8;
        for (int i = 0; i < steps; i++) {
            this.update_(deltaTime/steps);
        }
    }

    private void update_(double deltaTime){
        if ( velocity.len() < 0.1) {
            velocity.setXYZ(0,0,0);

        }

        this.bounce(this.map.getWalls());
        
        // friction
        if(velocity.len() > 0){
            double friction = FRICTION_COEFFICIENT * mass;
            Vector3D frictionForce = velocity.copy();
            frictionForce.mult(-1);
            frictionForce.normalize();
            frictionForce.mult(friction);

            Vector3D dv = frictionForce.copy();
            dv.mult(deltaTime / mass);
            velocity.add(dv);
        }

        puttingCheck(deltaTime);

        Vector3D dx = velocity.copy();
        dx.mult(deltaTime);
        position.add(dx);

        boundsCheck();

    }

    public void kick(Vector3D dv){
        dv.limit(MAX_KICK_SPEED);
        this.velocity.add(dv);
    }

    public void puttingCheck(double dt){
        Vector3D holePosition = this.map.getHolePosition();
        double radiusHole = this.map.getHoleRadius();
        Vector3D holeDist = Vector3D.sub(position, holePosition);
        if (holeDist.len() < radiusHole){
            // this.velocity.mult(0.95);
            double slow = 300;
            Vector3D frictionForce = velocity.copy();
            frictionForce.mult(-1);
            frictionForce.normalize();
            frictionForce.mult(slow);

            Vector3D dv = frictionForce.copy();
            dv.mult(dt);
            velocity.add(dv);
            if (/*holeDist.len() < (radiusHole-radius) && */(velocity.len() < 0.5)) {
                this.position.setXYZ(holePosition.x, holePosition.y, holePosition.z);
                this.velocity.setXYZ(0, 0 ,0);
            }
        }
    }

    public void boundsCheck(){
        if(position.x < 0){
            position.x = 0;
            velocity.x = 0;
        }
        if(position.x > map.getWidth()){
            position.x = map.getWidth();
            velocity.x = 0;
        }
         if(position.y < 0){
            position.y = 0;
            velocity.y = 0;
        }
        if(position.y > map.getHeight()){
            position.y = map.getHeight();
            velocity.y = 0;
        }
    }

    public boolean isInside(double x, double y){
        double dx = x-this.position.x;
        double dy = y-this.position.y;
        double dist2 = dx*dx + dy*dy;

        return dist2 <= radius*radius;
    }

    public Vector3D isColliding(double x1, double y1, double x2, double y2){
        Vector3D seg_a = new Vector3D(x1, y1, 0);
        Vector3D seg_b = new Vector3D(x2, y2, 0);
        Vector3D seg_v = Vector3D.sub(seg_b, seg_a);
        Vector3D pt_v  = Vector3D.sub(position, seg_a);

        Vector3D closest = null;
        double len_closest = (pt_v.dot(seg_v)) / seg_v.len();
        if(len_closest < 0){
            closest = seg_a.copy();
        }else if(len_closest > seg_v.len()){
            closest = seg_b.copy();
        }else{
            closest = seg_v.copy();
            closest.normalize();
            closest.mult(len_closest);
            closest.add(seg_a);
        }

        Vector3D dist_v = Vector3D.sub(position, closest);
        // do they intersect?
        if(dist_v.len() <= radius){
            Vector3D offset = dist_v.copy();
            offset.normalize();
            offset.mult(radius-dist_v.len());
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

            Vector3D offset = this.isColliding(x1,y1,x2,y2);
            if ( offset != null){
                this.position.add(offset);
                Vector3D wall_start = new Vector3D(x1, y1, 0);
                Vector3D wall_end = new Vector3D(x2, y2, 0);
                Vector3D wall = Vector3D.sub(wall_end, wall_start);
                Vector3D vectorProjected = wall.copy();
                vectorProjected.normalize();
                vectorProjected.mult(velocity.dot(wall) / wall.len());
                Vector3D vectorParallelToNormal = Vector3D.sub(velocity, vectorProjected);
                vectorParallelToNormal.mult(-1);
                velocity = Vector3D.add(vectorParallelToNormal, vectorProjected);
                velocity.mult(BOUNCE_COEFFICIENT);
            }

        }
    }

    public String toString(){
        return String.valueOf(position.x); //+ " " + position.y + " " + position.z;
    }

}
