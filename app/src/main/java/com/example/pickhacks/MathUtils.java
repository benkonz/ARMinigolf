package com.example.pickhacks;

import com.google.ar.sceneform.collision.Box;
import com.google.ar.sceneform.collision.Sphere;
import com.google.ar.sceneform.math.Vector3;

public class MathUtils {

    private static class Plane {
        Vector3 tr;
        Vector3 bl;

        Plane(Vector3 tr, Vector3 bl) {
            this.tr = tr;
            this.bl = bl;
        }
    }

    public static Vector3 reflect(Sphere sphere, Vector3 velocity, Box box) {
        // 1) deconstruct the box into 4 different planes
        Vector3 plane1TopRight = new Vector3(box.getCenter().x - (box.getSize().x / 2),
                box.getCenter().y + (box.getSize().y / 2),
                box.getCenter().z + (box.getSize().z / 2)
        );
        Vector3 plane1BottomLeft = new Vector3(box.getCenter().x - (box.getSize().x / 2),
                box.getCenter().y - (box.getSize().y / 2),
                box.getCenter().z + (box.getSize().z / 2)
        );
        Plane p1 = new Plane(plane1TopRight, plane1BottomLeft);

        Vector3 plane2TopRight = new Vector3(box.getCenter().x + (box.getSize().x / 2),
                box.getCenter().y + (box.getSize().y / 2),
                box.getCenter().z - (box.getSize().z / 2)
        );
        Vector3 plane2BottomLeft = new Vector3(box.getCenter().x - (box.getSize().x / 2),
                box.getCenter().y - (box.getSize().y / 2),
                box.getCenter().z - (box.getSize().z / 2)
        );
        Plane p2 = new Plane(plane2TopRight, plane2BottomLeft);

        Vector3 plane3TopRight = new Vector3(box.getCenter().x + (box.getSize().x / 2),
                box.getCenter().y + (box.getSize().y / 2),
                box.getCenter().z - (box.getSize().z / 2)
        );
        Vector3 plane3BottomLeft = new Vector3(box.getCenter().x + (box.getSize().x / 2),
                box.getCenter().y - (box.getSize().y / 2),
                box.getCenter().z + (box.getSize().z / 2)
        );
        Plane p3 = new Plane(plane3TopRight, plane3BottomLeft);

        Vector3 plane4TopRight = new Vector3(box.getCenter().x - (box.getSize().x / 2),
                box.getCenter().y + (box.getSize().y / 2),
                box.getCenter().z + (box.getSize().z / 2)
        );
        Vector3 plane4BottomLeft = new Vector3(box.getCenter().x + (box.getSize().x / 2),
                box.getCenter().y - (box.getSize().y / 2),
                box.getCenter().z + (box.getSize().z / 2)
        );
        Plane p4 = new Plane(plane4TopRight, plane4BottomLeft);

        Plane[] planes = new Plane[]{p1, p2, p3, p4};
        // 2) check for a collision between the 4 planes
        Plane collision = null;
        for (Plane p : planes) {
            if (planesCollide(sphere, p)) {
                collision = p;
                break;
            }
        }
        // 3) reflect the vector onto the plane
        if (collision != null) {
            return reflectPlane(velocity, collision);
        } else {
            return null;
        }
    }

    public static boolean planesCollide(Sphere sphere, Plane p) {
        Vector3 p_normal = Vector3.cross(p.bl, p.tr).normalized();
        float d = Vector3.dot(Vector3.subtract(sphere.getCenter(), p.bl), p_normal);
        return d < sphere.getRadius();
    }

    public static Vector3 reflectPlane(Vector3 vec, Plane p) {
        Vector3 p_normal = Vector3.cross(p.bl, p.tr).normalized();
        return Vector3.subtract(vec, scale(2, scale(Vector3.dot(p_normal, vec), p_normal)));
    }

    public static Vector3 scale(float f, Vector3 vector3) {
        return new Vector3(vector3.x * f, vector3.y * f, vector3.z);
    }
}
