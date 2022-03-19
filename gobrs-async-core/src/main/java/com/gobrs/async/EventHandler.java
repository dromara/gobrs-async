package com.gobrs.async;

/**
 * @program: gobrs-async-starter
 * @ClassName EventHandler
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/
public interface EventHandler<Event> {

    /**
     * Called when event is published by {@link Sirector#publish(Object)}
     * synchronously or {@link Sirector#publish(Object, Callback)}
     * asynchronously.
     *
     * @param event
     *            event to be handled
     */
    public void onEvent(Event event);

}
