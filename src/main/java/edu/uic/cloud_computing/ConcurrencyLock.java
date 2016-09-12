package edu.uic.cloud_computing;

/**
 * Created by gabe on 9/11/16.
 */
public class ConcurrencyLock {
    private boolean locked = false;

    public synchronized boolean isLocked() {
        return locked;
    }

    public synchronized void openLock() {
        locked = false;
    }

    public synchronized void closeLock() {
        locked = true;
    }
}
