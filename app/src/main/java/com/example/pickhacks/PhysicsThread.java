package com.example.pickhacks;

import java.util.List;

public class PhysicsThread implements Runnable {

    private final static int MAX_FPS = 60;
    private final static int FRAME_PERIOD = 1000 / MAX_FPS;

    private List<Updatable> objects;

    public PhysicsThread(List<Updatable> objects) {
        this.objects = objects;
    }

    @Override
    public void run() {
        while (true) {
            long beginTime = System.currentTimeMillis();
            for (Updatable object : objects) {
                object.update();
            }
            long timeDiff = System.currentTimeMillis() - beginTime;
            int sleepTime = (int) (FRAME_PERIOD - timeDiff);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
            }
        }
    }
}
