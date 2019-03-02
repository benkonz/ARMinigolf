package com.example.pickhacks;

public class Point3D {
    public float x;
    public float y;
    public float z;

    public Point3D(float xIn, float yIn, float zIn)
    {
        x = xIn;
        y = yIn;
        z = zIn;
    }
    public double getDistance(float x1, float y1, float z1)
    {
        return Math.sqrt(Math.pow(x - x1, 2) + Math.pow(y - y1, 2) + Math.pow(z - z1, 2));
    }
    public double getDistance(float x1, float y1, float z1, float x2, float y2, float z2)
    {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
    }


}
