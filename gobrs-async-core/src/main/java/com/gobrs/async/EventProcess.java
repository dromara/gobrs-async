package com.gobrs.async;

/**
 * @program: gobrs-async-starter
 * @ClassName EventProcess
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class EventProcess<Event> implements Runnable, Cloneable {

    protected ScriptRuntime<Event> runtime;

    private final EventHandler<Event> eventHandler;

    private volatile int unsatisfiedDepdendings;

    private final List<EventHandler<Event>> dependedEventHandlers;

    private Event event;

    private Lock lock;

    EventProcess(EventHandler<Event> eventHandler, int depdending,
                 List<EventHandler<Event>> dependedEventHandlers) {
        this.eventHandler = eventHandler;
        this.unsatisfiedDepdendings = depdending;
        this.dependedEventHandlers = dependedEventHandlers;
    }

    /**
     * Initialize the object cloned from prototype.
     *
     * @param runtime
     * @param t
     */
    void init(ScriptRuntime<Event> runtime, Event t) {
        this.runtime = runtime;
        this.event = t;
    }

    public void run() {
        try {
            eventHandler.onEvent(event);
            //Fix bug find by zhulixin@jd.com which would block processes already satisfy running conditions.
            if (dependedEventHandlers != null) {
                List<EventProcess<Event>> readyProcesses = new ArrayList<EventProcess<Event>>(dependedEventHandlers.size());
                for (int i = 0; i < dependedEventHandlers.size(); i++) {
                    EventProcess<Event> process = runtime
                            .getProcess(dependedEventHandlers.get(i));
                    if (process.decreaseUnsatisfiedDependcies() == 0) {
                        readyProcesses.add(process);
                    }
                }
                if (readyProcesses.size() > 0) {
                    for (int i = (readyProcesses.size() - 1); i > 0; i--) {
                        runtime.startProcess(readyProcesses.get(i));
                    }
                    readyProcesses.get(0).run();
                }
            }
        } catch (Exception e) {
            runtime.markAsError(e);
        }
    }

    boolean hasUnsatisfiedDependcies() {
        lock.lock();
        try {
            return unsatisfiedDepdendings != 0;
        } finally {
            lock.unlock();
        }
    }

    private int decreaseUnsatisfiedDependcies() {
        lock.lock();
        try {
            return --unsatisfiedDepdendings;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public Object clone() {
        try {
            EventProcess<Event> cloned = (EventProcess<Event>) super.clone();
            cloned.lock = new ReentrantLock();
            return cloned;
        } catch (Exception e) {
            throw new InternalError();
        }
    }

}
