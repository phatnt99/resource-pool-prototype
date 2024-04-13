package org.example;

public class ResourceFactory {
    public Resource createResource(String name) {
        Resource r = new Resource();
        r.setName(name);
        r.setUsedBy(Thread.currentThread().getName());

        return r;
    }

    public boolean validateResource(Object o) {
        if (!(o instanceof Resource)) {
            return false;
        }
        // reset state of resource
        ((Resource) o).setState(0);

        return true;
    }
}
