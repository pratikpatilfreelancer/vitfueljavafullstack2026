package vanguard.concurrent;

import vanguard.util.DispatchCenter;

/**
 * The second standard way to create a thread: implement Runnable and
 * hand it to a Thread instance (see Main.java), rather than extending
 * Thread directly as DispatchThread does.
 *
 * Also demonstrates classic inter-thread communication via
 * wait()/notify() on a shared lock object, distinct from the
 * join()-based coordination used elsewhere in the project.
 */
public class HeartbeatMonitor implements Runnable {

    private final Object lock;
    private volatile boolean running = true;

    public HeartbeatMonitor(Object lock) {
        this.lock = lock;
    }

    public void stop() {
        running = false;
        synchronized (lock) {
            lock.notifyAll(); // wake the monitor immediately if it's waiting
        }
    }

    @Override
    public void run() {
        while (running) {
            synchronized (lock) {
                try {
                    lock.wait(2000); // wait up to 2s, or until notified
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            if (running) {
                DispatchCenter dc = DispatchCenter.getInstance();
                System.out.printf("[heartbeat] active incidents: %d | available resources: %d%n",
                        dc.countActiveIncidents(), dc.countAvailableResources());
            }
        }
        System.out.println("[heartbeat] monitor stopped.");
    }
}
