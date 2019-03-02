package com.example.pickhacks;

public class Ball {

    private Point3D point3D;
    private int radius;

    public Ball() {
        this(new Point3D(0,0,0), 0);
    }

    public Ball(Point3D point3DIn) {
        this(point3DIn, 0);
    }

    public Ball(int radiusIn) {
        this(new Point3D(0,0,0), radiusIn);
    }

    public Ball(int xIn, int yIn, int zIn) {
        this(new Point3D(xIn, yIn, zIn), 0);
    }

    public Ball(int xIn, int yIn, int zIn, int radiusIn) {
        this(new Point3D(xIn, yIn, zIn), radiusIn);
    }

    public Ball(Point3D point3DIn, int radiusIn) {
        setPoint3D(point3DIn);
        setRadius(radiusIn);
    }

    public Point3D getPoint3D() { return point3D; }
    public int getX() { return point3D.x; }
    public int getY() { return point3D.y; }
    public int getZ() { return point3D.z; }
    public int getRadius() { return radius; }

    public void setPoint3D(Point3D point3DIn) { point3D = point3DIn; }
    public void setX(int xIn) { point3D.x = xIn; }
    public void setY(int yIn) { point3D.y = yIn; }
    public void setZ(int zIn) { point3D.z = zIn; }
    public void setRadius(int radiusIn) { radius = radiusIn; }

    public boolean CollisonRectangle(Rectangle rectangle) {
        return false;
    }

    public boolean CollisonCylindar() {
        return false;
    }

    public boolean CollisonBall(Ball other) {
        return false;
    }

}
