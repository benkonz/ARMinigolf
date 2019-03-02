package com.example.pickhacks;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.collision.Box;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    ArFragment arFragment;
    ModelRenderable sphere;
    ModelRenderable wall;
    Box[] map;
    boolean hasSpawnedBall = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_main);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        map = new Box[61];
        for (int i = 0; i < 30; i++) {
            map[i] = new Box();
            map[i].setSize(new Vector3(0.2f, 0.15f, 0.2f));
            map[i].setCenter(new Vector3(-0.4f, 0.0f, -0.04f * i));
        }
        for (int i = 30; i < 60; i++) {
            map[i] = new Box();
            map[i].setSize(new Vector3(0.2f, 0.15f, 0.2f));
            map[i].setCenter(new Vector3(0.4f, 0.0f, -0.04f * (i % (map.length/2))));
        }
        map[60] = new Box();
        map[60].setSize(new Vector3(1f, 0.15f, 0.2f));
        map[60].setCenter(new Vector3(0.0f, 0.0f, -1.2f));



        MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.WHITE))
                .thenAccept(
                        material -> {
                            sphere =
                                    ShapeFactory.makeSphere(0.1f, new Vector3(0.0f, 0f, 0.0f), material); }).exceptionally(throwable -> {
                    Toast toast = Toast.makeText(this, "Unable draw Shape", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return null;
                });

        MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.BLACK))
                .thenAccept(
                        material -> {
                            wall =
                                    ShapeFactory.makeCube(map[0].getSize(), map[0].getCenter(), material); }).exceptionally(throwable -> {
            Toast toast = Toast.makeText(this, "Unable draw Shape", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return null;
        });

        arFragment.setOnTapArPlaneListener((HitResult hitResult, Plane plane, MotionEvent motionEvent) ->

        {
            if (sphere == null)
            {
                return;
            }

            if (!hasSpawnedBall)
            {
                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());

                TransformableNode s = new TransformableNode(arFragment.getTransformationSystem());
                s.setParent(anchorNode);
                s.setRenderable(sphere);
                s.select();

                Anchor wallAnchor = hitResult.createAnchor();
                AnchorNode wallAnchorNode = new AnchorNode(wallAnchor);
                wallAnchorNode.setParent(arFragment.getArSceneView().getScene());

                for (int i =0; i < map.length; i++) {
                    final int pos = i;
                    MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.BLACK))
                            .thenAccept(
                                    material -> {
                                        wall =
                                                ShapeFactory.makeCube(map[pos].getSize(), map[pos].getCenter(), material); }).exceptionally(throwable -> {
                        Toast toast = Toast.makeText(this, "Unable draw Shape", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return null;
                    });

                    Node n = new Node();
                    n.setParent(wallAnchorNode);
                    n.setRenderable(wall);
                }
                hasSpawnedBall = true;
            }
            /**else {

                if (wall == null)
                {
                    return;
                }

                Anchor wallAnchor = hitResult.createAnchor();
                AnchorNode wallAnchorNode = new AnchorNode(wallAnchor);
                wallAnchorNode.setParent(arFragment.getArSceneView().getScene());

                Node n = new Node();
                n.setParent(wallAnchorNode);
                n.setRenderable(wall);
            }*/
        });

    }


    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }
}
