package org.chiefss.smarttracker.application.utils;

public class Counter {

    private int count;

    public Counter() {
        this.count = 0;
    }

    public void increase() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }
}
