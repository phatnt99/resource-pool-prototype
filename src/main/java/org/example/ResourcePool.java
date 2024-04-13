package org.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResourcePool {
    private final ResourceFactory factory;
    private final int capacity;
    public int size;
    private boolean quit;
    public volatile List<Resource> availables;
    public volatile Set<Resource> inUseds;

    public ResourcePool(ResourceFactory factory, int capacity) {
        this.factory = factory;
        this.capacity = capacity;
        availables = new ArrayList<>();
        inUseds = new HashSet<>();
    }

    public synchronized Resource borrowResource() {
        while(!quit) {
            // if not empty, get one existing
            if (!availables.isEmpty()) {
                Resource r = availables.remove(0);
                // check validate and reset state
                if (!factory.validateResource(r)) {
                    // validate fails
                    // create a new replacement
                    r = factory.createResource("r-" + availables.size());
                    inUseds.add(r);
                }
                return r;
            }
            // if empty, check the capacity and current size
            if (size < capacity) {
                Resource r = factory.createResource(String.valueOf(size));
                inUseds.add(r);
                size++;
                return r;
            }
            // if all resource is borrowed, borrowee must wait
            try {
                wait();
            } catch (Exception e) {
                log("Exception when wait");
            }
        }
        return null;
    }

    public synchronized boolean returnResource(Resource r) {
        if (!inUseds.remove(r)) {
            return false;
        }
        availables.add(r);
        notify();
        return true;
    }

    public synchronized void clear() {
        // first make sure all resource borrowed by calling thread were returned
        for (Resource r : inUseds) {
            if (Thread.currentThread().getName().equals(r.getUsedBy())) {
                returnResource(r);
            }
        }
        while (!(availables.size() == size && inUseds.isEmpty())) {
            try {
                wait();
            } catch (Exception e) {
                log("Exception when wait");
            };
            availables.clear();
            size = availables.size();
            notifyAll();
        }
    }

    public synchronized void quit() {
        quit = true;
        notifyAll();
    }
    
    public synchronized void log(String msg) {
        System.out.println(Thread.currentThread().getName() + ": "+ msg);
    }
}
