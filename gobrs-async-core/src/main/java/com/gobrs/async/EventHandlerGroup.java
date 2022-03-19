package com.gobrs.async;

/**
 * @program: gobrs-async-starter
 * @ClassName EventHandlerGroup
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventHandlerGroup<Event> {

    private final Script<Event> script;

    private List<EventHandler<Event>> eventHandlers;

    EventHandlerGroup(Script<Event> script,
                      List<EventHandler<Event>> eventHandlers) {
        synchronized (script) {
            if (script.isReady()) {
                throw new IllegalStateException(
                        "script is ready, cannot be edit any more.");
            }
            this.script = script;
            this.eventHandlers = new ArrayList<EventHandler<Event>>(
                    eventHandlers.size());
            copyList(eventHandlers, this.eventHandlers);
            for (EventHandler<Event> handler : eventHandlers) {
                script.addDependency(handler, null);
            }
        }
    }

    /**
     * Used with {@linkplain Sirector#after} class to imply the dependency
     * between event handlers.
     *
     * The following example:
     *
     * <pre>
     * sirector.after(eventHandlerA).then(eventHandlerB);
     * </pre>
     *
     * means eventHandlerA must be called before eventHandlerB in the
     * transaction type.
     *
     * <p>
     * The method can be called many times.
     * </p>
     *
     * @param eventHandlers
     *            event handlers to depend other handlers
     * @return the event transaction being composed
     * @throws IllegalStateException
     *             if event handler in the parameter is not added to the
     *             transaction yet or <tt>ready()</tt> method of the same
     *             sirector instance has been called.
     *
     *
     * @see EventHandlerGroup#then(EventHandler...)
     * @see Sirector#begin(EventHandler...)
     */
    public EventHandlerGroup<Event> then(EventHandler<Event>... eventHandlers) {
        synchronized (script) {
            if (script.isReady()) {
                throw new IllegalStateException(
                        "script is ready, cannot be edit any more.");
            }
            for (EventHandler<Event> from : this.eventHandlers) {
                for (EventHandler<Event> to : eventHandlers) {
                    script.addDependency(from, to);
                }
            }
            for (EventHandler<Event> to : eventHandlers) {
                script.addDependency(to, null);
            }
            this.eventHandlers = new ArrayList<EventHandler<Event>>(
                    eventHandlers.length);
            copyList(Arrays.asList(eventHandlers), this.eventHandlers);
            return this;
        }
    }

    private void copyList(List<EventHandler<Event>> src,
                          List<EventHandler<Event>> dest) {
        for (EventHandler<Event> s : src) {
            dest.add(s);
        }
    }

}
