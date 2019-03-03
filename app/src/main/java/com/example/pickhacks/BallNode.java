package com.example.pickhacks;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.Toast;

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
    private Context context;
    private Vector3 velocity;
    private float gravity;

    public BallNode(Context context, TransformationSystem transformationSystem) {
        super(transformationSystem);
        velocityTracker = null;
        this.context = context;
        velocity = new Vector3(0, 0, 0);
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
                Toast.makeText(context, "You moved the ball!", Toast.LENGTH_SHORT).show();
                velocityTracker.addMovement(motionEvent);
                velocityTracker.computeCurrentVelocity(1);
                velocity.x = velocityTracker.getXVelocity(pointerId) / 1000;
                velocity.y = velocityTracker.getYVelocity(pointerId) / 1000;
                Toast.makeText(context, "x: " + velocity.x + " y: " + velocity.y, Toast.LENGTH_SHORT).show();
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

    @Override
    public void update() {
        Vector3 position = getWorldPosition();

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
        setWorldPosition(newPosition);
    }
}
