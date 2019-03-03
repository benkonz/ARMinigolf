package com.example.pickhacks;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.collision.Box;
import com.google.ar.sceneform.collision.Sphere;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    ArFragment arFragment;
    ModelRenderable sphere;
    Sphere ball;
    ModelRenderable wall;
    ModelRenderable goal;
    Box[] map;
    boolean hasLoaded = false;
    List<Updatable> physicsObjects;
    Sphere modelPlayerSphere = new Sphere(0.1f, new Vector3(0.0f, 0.0f, 0.0f));
    Sphere modelGoalSphere = new Sphere(0.1f, new Vector3(0.0f, 0.0f, -1.0f));
    ViewRenderable menuViewRenderable;
    Renderable menuRenderable;
    boolean menued = false;
    Vector3 origin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_main);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        Toast t = Toast.makeText(this, "Looking for Plane Surface!", Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
        physicsObjects = new ArrayList<>();


        //initialize and create boxes on the map
        map = new Box[3];
        map[0] = new Box();
        map[0].setSize(new Vector3(0.2f, 0.15f, 1.2f));
        map[0].setCenter(new Vector3(-0.4f, 0.0f, -0.6f));

        map[1] = new Box();
        map[1].setSize(new Vector3(0.2f, 0.15f, 1.2f));
        map[1].setCenter(new Vector3(0.4f, 0.0f, -0.6f));

        map[2] = new Box();
        map[2].setSize(new Vector3(1f, 0.15f, 0.2f));
        map[2].setCenter(new Vector3(0.0f, 0.0f, -1.2f));

        ball = new Sphere();
        ball.setRadius(0.1f);
        ball.setCenter(new Vector3(0.0f, 0f, 0.0f));

        MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.WHITE))
                .thenAccept(
                        material -> {
                            sphere =
                                    ShapeFactory.makeSphere(ball.getRadius(), ball.getCenter(), material);
                        }).exceptionally(throwable -> {
            Toast toast = Toast.makeText(this, "Unable draw Shape", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return null;
        });

        MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.RED))
                .thenAccept(material -> {
                    goal = ShapeFactory.makeSphere(0.1f, new Vector3(0.0f, 0f, -1.0f), material);
                }).exceptionally(throwable -> {
            Toast.makeText(this, "Unable draw Shape", Toast.LENGTH_SHORT).show();
            return null;
        });

        MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.BLACK))
                .thenAccept(
                        material -> {
                            wall =
                                    ShapeFactory.makeCube(map[0].getSize(), map[0].getCenter(), material);
                        }).exceptionally(throwable -> {
            Toast toast = Toast.makeText(this, "Unable draw Shape", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return null;
        });


        arFragment.setOnTapArPlaneListener((HitResult hitResult, Plane plane, MotionEvent motionEvent) ->
        {
            if (sphere == null) {
                return;
            }
            if (!hasLoaded) {
                spawnSphere(hitResult, sphere);
                origin = ball.getCenter();

                Anchor wallAnchor = hitResult.createAnchor();
                AnchorNode wallAnchorNode = new AnchorNode(wallAnchor);
                wallAnchorNode.setParent(arFragment.getArSceneView().getScene());

                for (int i = 0; i < map.length; i++) {
                    wall = ShapeFactory.makeCube(map[i].getSize(), map[i].getCenter(), wall.getMaterial());
                    Node n = new Node();
                    n.setParent(wallAnchorNode);
                    n.setRenderable(wall);
                }

                Anchor goalAnchor = hitResult.createAnchor();
                AnchorNode goalAnchorNode = new AnchorNode(goalAnchor);
                goalAnchorNode.setParent(arFragment.getArSceneView().getScene());
                goal = ShapeFactory.makeSphere(0.1f, new Vector3(0.0f, 0.0f, -1.0f), goal.getMaterial());
                Node goalNode = new Node();
                goalNode.setCollisionShape(modelGoalSphere);
                goalNode.setParent(goalAnchorNode);
                goalNode.setRenderable(goal);


                CompletableFuture<ViewRenderable> menuStage = ViewRenderable.builder().setView(this, R.layout.menu_layout).build();
                menuStage.handle((notUsed, throwable) -> {
                    try {
                        Log.d("MENU", "tring to get menu view renderable");
                        menuViewRenderable = menuStage.get();

                        Anchor menuAnchor = hitResult.createAnchor();
                        AnchorNode menuAnchorNode = new AnchorNode(menuAnchor);
                        menuAnchorNode.setParent(arFragment.getArSceneView().getScene());
                        Node menuNode = new Node();
                        menuNode.setParent(menuAnchorNode);
                        menuNode.setRenderable(menuViewRenderable);
                        menuNode.setLocalPosition(new Vector3(0.0f, 1.0f, 0.0f));

                        View menuView = menuViewRenderable.getView();
                        SeekBar levelSelectBar = menuView.findViewById(R.id.levelSelectBar);
                        //levelSelectBar.setProgress(0);
                        levelSelectBar.setOnSeekBarChangeListener(
                                new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        int ratio = progress/3;
                                        //change level stuff here;
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {

                                    }

                                }
                        );

                        menued = true;
                    }
                    catch(InterruptedException | ExecutionException e) {
                        Log.d("MENU", "got an exception, menuStage: " + menuStage + "menuViewRenderable: " + menuViewRenderable);
                    }
                    return null;
                });
                Log.d("MENU", "menuViewRenderable:" + menuViewRenderable);

                hasLoaded = true;
            }

            if(!menued) {
                CompletableFuture<ViewRenderable> menuStage = ViewRenderable.builder().setView(this, R.layout.menu_layout).build();
                menuStage.handle((notUsed, throwable) -> {
                    try {
                        Log.d("MENU", "string to get menu view renderable");
                        menuViewRenderable = menuStage.get();

                        Anchor menuAnchor = hitResult.createAnchor();
                        AnchorNode menuAnchorNode = new AnchorNode(menuAnchor);
                        menuAnchorNode.setParent(arFragment.getArSceneView().getScene());
                        Node menuNode = new Node();
                        menuNode.setParent(menuAnchorNode);
                        menuNode.setRenderable(menuViewRenderable);
                        menuNode.setLocalPosition(new Vector3(0.0f, 1.0f, 0.0f));

                        menued = true;
                    }
                    catch(InterruptedException | ExecutionException e) {
                        Log.d("MENU", "got an exception, menuStage: " + menuStage + "menuViewRenderable: " + menuViewRenderable);
                    }
                    return null;
                });
            }

            Runnable runnable = new PhysicsThread(physicsObjects);
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(runnable);
        });
    }

    public boolean spawnSphere(HitResult hitResult, ModelRenderable sph) {
        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        BallNode s = new BallNode(arFragment.getTransformationSystem(), modelPlayerSphere, modelGoalSphere, this);
        s.setParent(anchorNode);
        s.setRenderable(sph);
        s.select();
        physicsObjects.add(s);
        return true;
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
