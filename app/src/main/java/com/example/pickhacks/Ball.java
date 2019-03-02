package com.example.pickhacks;

public class Ball {

    private Point3D point3D;
    private Point3D velocity;
    private int radius;

    public Ball() {
        this(new Point3D(0, 0, 0), 0);
    }

    public Ball(Point3D point3DIn) {
        this(point3DIn, 0);
    }

    public Ball(int radiusIn) {
        this(new Point3D(0, 0, 0), radiusIn);
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

    public Point3D getPoint3D() {
        return point3D;
    }

    public int getX() {
        return point3D.x;
    }

    public int getY() {
        return point3D.y;
    }

    public int getZ() {
        return point3D.z;
    }

    public int getRadius() {
        return radius;
    }

    public void setPoint3D(Point3D point3DIn) {
        point3D = point3DIn;
    }

    public void setX(int xIn) {
        point3D.x = xIn;
    }

    public void setY(int yIn) {
        point3D.y = yIn;
    }

    public void setZ(int zIn) {
        point3D.z = zIn;
    }

    public void setRadius(int radiusIn) {
        radius = radiusIn;
    }

    public boolean CollisonRectangle(Rectangle rectangle) {
        return CollisonBall(
                new Ball(
                        Math.max(rectangle.getBottomLeft().x, Math.min(getX(), rectangle.getTopRight().x)),
                        Math.max(rectangle.getBottomLeft().y, Math.min(getY(), rectangle.getTopRight().y)),
                        Math.max(rectangle.getBottomLeft().z, Math.min(getZ(), rectangle.getTopRight().z)),
                        0));
    }

    public boolean CollisonCylindar() {
        return false;
    }

    public static Point3D normalize(Point3D point3D) {
        int magnitude = (int) Math.sqrt((double) (point3D.x * point3D.x) +
                (double) (point3D.y * point3D.y) +
                (double) (point3D.z * point3D.z)
        );
        return new Point3D(
                point3D.x / magnitude,
                point3D.y / magnitude,
                point3D.z / magnitude
        );
    }

    public static int dot(Point3D point3D1, Point3D point3D2) {
        return point3D1.x * point3D2.x +
                point3D1.y * point3D2.y +
                point3D1.z * point3D2.z;
    }

    public static Point3D sub(Point3D point3D1, Point3D point3D2) {
        return new Point3D(
                point3D1.x - point3D2.x,
                point3D1.y - point3D2.y,
                point3D1.z - point3D2.z
        );
    }

    public static Point3D scale(int factor, Point3D point3D) {
        return new Point3D(
                point3D.x * factor,
                point3D.y * factor,
                point3D.z * factor
        );
    }

    public static Point3D cross(Point3D point3D1, Point3D point3D2) {
        return new Point3D(
                point3D1.y * point3D2.z - point3D1.z * point3D2.y,
                    point3D1.x * point3D2.z - point3D1.z * point3D2.x,
                point3D1.x * point3D2.y - point3D1.y - point3D2.x
        );
    }

    public void reflect(Rectangle rectangle) {
        Point3D n = normalize(cross(rectangle.getBottomLeft(),
                new Point3D(rectangle.getTopRight().x,
                        rectangle.getTopRight().y - rectangle.getBottomLeft().y, rectangle.getBottomLeft().z)
        ));
        sub(velocity, scale(2, scale(dot(n, velocity), n)));
    }

    public boolean CollisonBall(Ball other) {
        return Math.sqrt((getX() - other.getX()) * (getX() - other.getX()) +
                (getY() - other.getY()) * (getY() - other.getY()) +
                getZ() - other.getZ()) * (getZ() - other.getZ())
                < (double) (getRadius() + other.getRadius());
    }
}
