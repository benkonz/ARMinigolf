package com.example.pickhacks;

public class Rectangle {

    private Point3D bottomLeft;
    private Point3D topRight;

    public Rectangle(Point3D bottomLeftIn, Point3D topRightIn) {
        setBottomLeft(bottomLeftIn);
        setTopRight(topRightIn);
    }

    public Rectangle(float bottomLeftXIn, float bottomLeftYIn, float bottomLeftZIn,
                     float topRightXIn, float topRightYIn, float topRightZIn) {
        this(new Point3D(bottomLeftXIn, bottomLeftYIn, bottomLeftZIn),
                new Point3D(topRightXIn, topRightYIn, topRightZIn));
    }

    public Point3D getBottomLeft() { return bottomLeft; }
    public Point3D getTopRight() { return topRight; }

    public void setBottomLeft(Point3D bottomLeft) { this.bottomLeft = bottomLeft; }
    public void setTopRight(Point3D topRight) { this.topRight = topRight; }
}
