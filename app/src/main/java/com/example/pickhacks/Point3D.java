package com.example.pickhacks;

public class Point3D {
    public int x;
    public int y;
    public int z;

    public Point3D(int xIn, int yIn, int zIn)
    {
        x = xIn;
        y = yIn;
        z = zIn;
    }
    public double getDistance(int x1, int y1, int z1)
    {
        return Math.sqrt(Math.pow(x - x1, 2) + Math.pow(y - y1, 2) + Math.pow(z - z1, 2));
    }
    public double getDistance(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
    }


}
