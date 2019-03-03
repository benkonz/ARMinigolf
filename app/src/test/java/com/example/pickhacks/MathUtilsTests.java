package com.example.pickhacks;

import com.google.ar.sceneform.collision.Box;
import com.google.ar.sceneform.collision.Sphere;
import com.google.ar.sceneform.math.Vector3;

import org.junit.Test;

import static org.junit.Assert.*;

public class MathUtilsTests {
    @Test
    public void addition_isCorrect() {
        Box box = new Box();
        box.setSize(new Vector3(5, 5, 5));
        box.setCenter(new Vector3(1, 1, 1));
        Sphere sphere = new Sphere();
        sphere.setCenter(new Vector3(0, 0, 0));
        sphere.setRadius(2);
        Vector3 velocity = new Vector3(1, 1, 1);

        Vector3 reflected = MathUtils.reflect(sphere, velocity, box);
    }
}