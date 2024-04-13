package org.example;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        ResourcePool pool = new ResourcePool(new ResourceFactory(), 2);
        for (int i = 0; i < 3; i++) {
            Thread t = new Thread(() -> {
                Resource r = pool.borrowResource();
                if (new Random().nextInt(100) % 2 == 0) {
                    pool.clear();
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    pool.returnResource(r);
                }
            });
            t.start();
        }
    }

    public static void log(String msg) {
        System.out.println(Thread.currentThread().getName() + ": "+ msg);
    }
}