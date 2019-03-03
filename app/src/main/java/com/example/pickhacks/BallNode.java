package com.example.pickhacks;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.Toast;

import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.collision.Box;
import com.google.ar.sceneform.collision.CollisionShape;
import com.google.ar.sceneform.collision.Sphere;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.BaseTransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

public class BallNode extends BaseTransformableNode implements Updatable {

    private VelocityTracker velocityTracker;
    private Vector3 velocity;
    private Sphere sphere;
    private Sphere goalSphere;
    private boolean isWinner = false;
    private Context context;
    private Box[] map;

    public BallNode(TransformationSystem transformationSystem, Sphere sphere, Sphere goalSphere, Context context, Box[] map) {
        super(transformationSystem);
        velocityTracker = null;
        this.sphere = sphere;
        this.goalSphere = goalSphere;
        velocity = new Vector3(0, 0, 0);
        this.context = context;
        this.map = map;
    }

    @Override
    public boolean onTouchEvent(HitTestResult hitTestResult, MotionEvent motionEvent) {
        int index = motionEvent.getActionIndex();
        int action = motionEvent.getActionMasked();
        int pointerId = motionEvent.getPointerId(index);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker.clear();
                }
                velocityTracker.addMovement(motionEvent);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                velocityTracker.addMovement(motionEvent);
                velocityTracker.computeCurrentVelocity(1);
                velocity.x = velocityTracker.getXVelocity(pointerId) / 1000;
                velocity.y = velocityTracker.getYVelocity(pointerId) / 1000;
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                velocityTracker.recycle();
                velocityTracker = null;
                break;
        }
        return true;
    }

    boolean collidesWithSphere(Sphere sphere) {
        float deltaXSquared = Math.abs(sphere.getCenter().x) - Math.abs(this.sphere.getCenter().x);
        deltaXSquared *= deltaXSquared;
        float deltaYSquared = Math.abs(sphere.getCenter().y) - Math.abs(this.sphere.getCenter().y);
        deltaYSquared *= deltaYSquared;
        float deltaZSquared = Math.abs(sphere.getCenter().z) - Math.abs(this.sphere.getCenter().z);
        deltaZSquared *= deltaZSquared;

        float sumRadiiSquared = sphere.getRadius() + this.sphere.getRadius();
        sumRadiiSquared *= sumRadiiSquared;

        return deltaXSquared + deltaYSquared + deltaZSquared <= sumRadiiSquared;
    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        if (isWinner) {
            Toast.makeText(context, "YOU ARE A WINNER", Toast.LENGTH_SHORT).show();
            isWinner = false;
        }
    }

    public boolean sphereCollidesWithBox(Sphere sphere, Box box) {
        float circularDistanceX = Math.abs(sphere.getCenter().x - box.getCenter().x);
        float circularDistanceY = Math.abs(sphere.getCenter().y - box.getCenter().y);
        float circularDistanceZ = Math.abs(sphere.getCenter().z - box.getCenter().z);

        if (circularDistanceX > box.getSize().x / 2 + sphere.getRadius()) return false;
        if (circularDistanceY > box.getSize().y / 2 + sphere.getRadius()) return false;
        if (circularDistanceZ > box.getSize().z / 2 + sphere.getRadius()) return false;

        if (circularDistanceX <= (box.getSize().x / 2)) return true;
        if (circularDistanceY <= (box.getSize().y / 2)) return true;
        if (circularDistanceZ <= (box.getSize().z / 2)) return true;

        float cornerDistnace_sq = (circularDistanceX - box.getSize().x / 2) * (circularDistanceX - box.getSize().x / 2) +
                (circularDistanceY - box.getSize().y / 2) * (circularDistanceY - box.getSize().y / 2) +
                (circularDistanceZ - box.getSize().z / 2) * (circularDistanceZ - box.getSize().z / 2);

        return (cornerDistnace_sq <= (sphere.getRadius() * sphere.getRadius()));
    }

    @Override
    public void update() {
        Vector3 position = getWorldPosition();

        if (collidesWithSphere(goalSphere)) {
            isWinner = true;
        }

        for (Box box : map) {
            float circularDistanceX = Math.abs(sphere.getCenter().x - box.getCenter().x);
            float circularDistanceY = Math.abs(sphere.getCenter().y - box.getCenter().y);
            float circularDistanceZ = Math.abs(sphere.getCenter().z - box.getCenter().z);

            float cornerDistnace_sq = (circularDistanceX - box.getSize().x / 2) * (circularDistanceX - box.getSize().x / 2) +
                    (circularDistanceY - box.getSize().y / 2) * (circularDistanceY - box.getSize().y / 2) +
                    (circularDistanceZ - box.getSize().z / 2) * (circularDistanceZ - box.getSize().z / 2);

            if (sphereCollidesWithBox(sphere, box)) {
                if ((circularDistanceX <= (box.getSize().x / 2))) {
                    velocity.x *= -1;
                } else if (circularDistanceZ > box.getSize().z / 2 + sphere.getRadius()) {
                    velocity.y *= -1;
                } else if (cornerDistnace_sq <= (sphere.getRadius() * sphere.getRadius())) {
                    velocity.x *= -1;
                    velocity.y *= -1;
                }
            }
        }

        Node overlappedNode = getScene().overlapTest(this);
        if (overlappedNode != null) {
            CollisionShape collisionShape = overlappedNode.getCollisionShape();
            Log.d("TESTING", "THERE IS A COLLISION");
            if (collisionShape instanceof Box) {
                Log.d("TESTING", "BOX COLLISION");
                Vector3 boxCenter = ((Box) collisionShape).getCenter();
                Vector3 boxSize = ((Box) collisionShape).getCenter();
                float xDiff = Math.min(Math.abs(position.x - (boxCenter.x + boxSize.x)), Math.abs(position.x - (boxCenter.x - boxSize.x)));
                float zDiff = Math.min(Math.abs(position.z - (boxCenter.z + boxSize.z)), Math.abs(position.z - (boxCenter.z - boxSize.z)));
                if (xDiff > zDiff) {
                    Log.d("TESTING", "HORIZONTAL COLLISION");
                    // horizontal collision
                    velocity.x *= -1;
                } else if (xDiff < zDiff) {
                    Log.d("TESTING", "VERTICAL COLLISION");
                    // vertical collision
                    velocity.y *= -1;
                } else {
                    // flip both
                    velocity.x *= -1;
                    velocity.y *= -1;
                }
            }
        }

        Vector3 newPosition = new Vector3(position.x + velocity.x, position.y, position.z + velocity.y);
        velocity.x *= .99;
        velocity.y *= .99;
        sphere.setCenter(new Vector3(sphere.getCenter().x + velocity.x, sphere.getCenter().y, sphere.getCenter().z + velocity.y));
        setWorldPosition(newPosition);
    }
}
