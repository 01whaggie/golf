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

    public Vector normalize(){
        double length = this.getLength();
        Vector normalizedVector = new Vector(this.getX()/length, this.getY()/length, this.getZ()/length);
        return normalizedVector;
    }

    public double dotProduct(Vector vector2) {
        if (vector2.getLength() != 1) {
            vector2 = vector2.normalize();
        }

        double dotProduct = (this.getX() * vector2.getX()) + (this.getY() * vector2.getY()) + (this.getZ() * vector2.getZ());
        return dotProduct;
    }

    public Vector crossProduct(Vector vector2){
        Vector multipliedVector = new Vector(this.getX()*vector2.getX(), this.getY()*vector2.getY(), this.getZ() * vector2.getZ());
        return multipliedVector;
    }

    public Vector crossProduct(double number){
        return new Vector(this.getX()*number, this.getY()*number, this.getZ()*number);
    }

    public Vector add(Vector vector2){
        return new Vector(this.getX() + vector2.getX(), this.getY() + vector2.getY(), this.getZ() + vector2.getZ());
    }

    public Vector negative(){
        return new Vector(- this.getX(), -this.getY(), -this.getZ());
    }

    @Override
    public String toString(){
        return "Vec3: x=" + x + ", y=" + y + " ,z=" + z;
    }

}
