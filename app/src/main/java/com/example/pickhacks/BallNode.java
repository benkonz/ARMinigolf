package com.example.pickhacks;

import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;

public class BallNode extends Node implements Node.OnTouchListener {

    private VelocityTracker velocityTracker;
    private Ball ball;

    public BallNode(Ball ball) {
        velocityTracker = null;
        this.ball = ball;
    }

    public Ball getBall() {
        return ball;
    }

    @Override
    public boolean onTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
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
                velocityTracker.computeCurrentVelocity(1000);
                Log.d("", "X Velocity: " + velocityTracker.getXVelocity(pointerId));
                Log.d("", "Y Velocity: " + velocityTracker.getYVelocity(pointerId));
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                velocityTracker.recycle();
                break;
        }
        return true;
    }
}
