/**
 * Created by Jade on 14-03-16.
 */
public class Vector {
    private double x, y, z;

    public Vector(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX(){ return x; }
    public double getY(){ return y; }
    public double getZ(){ return z; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setZ(double z) { this.z = z; }

    public double getLength(){
        return Math.sqrt((x*x) + (y*y) + (z*z));
    }

}
