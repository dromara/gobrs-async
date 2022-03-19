package com.gobrs.async;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.concurrent.ExecutorService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @program: gobrs-async-starter
 * @ClassName ScriptRuntimeBuilder
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/

class ScriptRuntimeBuilder<Event> {

    private final Script<Event> script;

    private final ExecutorService executorService;

    private IdentityHashMap<EventHandler<Event>, EventProcess<Event>> processPrototypeMap = new IdentityHashMap<EventHandler<Event>, EventProcess<Event>>();

    ScriptRuntimeBuilder(Script<Event> script, ExecutorService executorService) {
        this.script = script;
        this.executorService = executorService;
        preparePrototypes();
    }

    private void preparePrototypes() {

        /**
         * Copy depended event handler map from script map to avoid aside effect
         * to Script.
         */
        Map<EventHandler<Event>, List<EventHandler<Event>>> dependedHandlerMap = copyEventHandlerMap(script.getdenpendedEventHandlers());
        /**
         * Compute depending event handler map. We can compute only depending
         * count, but that is not the key to the performance.
         */
        Map<EventHandler<Event>, List<EventHandler<Event>>> dependingHandlerMap = new HashMap<EventHandler<Event>, List<EventHandler<Event>>>();
        for (EventHandler<Event> handler : dependedHandlerMap.keySet()) {
            dependingHandlerMap.put(handler,
                    new ArrayList<EventHandler<Event>>(1));
        }
        for (EventHandler<Event> eventHandler : dependedHandlerMap.keySet()) {
            for (EventHandler<Event> depended : dependedHandlerMap
                    .get(eventHandler)) {
                dependingHandlerMap.get(depended).add(eventHandler);
            }
        }
        /**
         * Compute event handlers that do not depended by any other event
         * handlers and make them to depend SciptEndEventHandler.
         */
        ScriptEndEventHandler scriptEndEventHandler = new ScriptEndEventHandler();
        List<EventHandler<Event>> scriptEndDependingHandlers = new ArrayList<EventHandler<Event>>(
                1);
        for (EventHandler<Event> handler : dependedHandlerMap.keySet()) {
            List<EventHandler<Event>> dependedHandlers = dependedHandlerMap
                    .get(handler);
            if (dependedHandlers.isEmpty()) {
                scriptEndDependingHandlers.add(handler);
                dependedHandlerMap.get(handler).add(scriptEndEventHandler);
            }
        }
        dependedHandlerMap.put(scriptEndEventHandler,
                new ArrayList<EventHandler<Event>>(0));
        dependingHandlerMap.put(scriptEndEventHandler,
                scriptEndDependingHandlers);

        /**
         * Prepare process prototypes
         */
        for (EventHandler<Event> handler : dependedHandlerMap.keySet()) {
            EventProcess<Event> process;
            if (handler != scriptEndEventHandler) {
                process = new EventProcess<Event>(handler, dependingHandlerMap
                        .get(handler).size(), dependedHandlerMap.get(handler));
            } else {
                process = new ScriptEndEventProcess(handler,
                        dependingHandlerMap.get(handler).size(),
                        dependedHandlerMap.get(handler));
            }
            processPrototypeMap.put(handler, process);
        }
    }

    private Map<EventHandler<Event>, List<EventHandler<Event>>> copyEventHandlerMap(Map<EventHandler<Event>, List<EventHandler<Event>>> handlerMap) {
        IdentityHashMap<EventHandler<Event>, List<EventHandler<Event>>> rt = new IdentityHashMap<EventHandler<Event>, List<EventHandler<Event>>>();
        for (EventHandler<Event> eventHandler : handlerMap.keySet()) {
            rt.put(eventHandler,
                    new ArrayList<EventHandler<Event>>(handlerMap
                            .get(eventHandler)));
        }
        return rt;
    }

    ScriptRuntime<Event> build(Event event, long timeout) {
        return build(event, timeout, null);
    }

    @SuppressWarnings("unchecked")
    ScriptRuntime<Event> build(Event event, long timeout,
                               Callback<Event> callback) {
        IdentityHashMap<EventHandler<Event>, EventProcess<Event>> newProcessMap = new IdentityHashMap<EventHandler<Event>, EventProcess<Event>>(
                processPrototypeMap.size());
        ScriptRuntime<Event> runtime = new ScriptRuntime<Event>(event,
                executorService, newProcessMap, callback, timeout);
        for (EventHandler<Event> handler : processPrototypeMap.keySet()) {
            EventProcess<Event> newProcess = (EventProcess<Event>) processPrototypeMap
                    .get(handler).clone();
            newProcess.init(runtime, event);
            newProcessMap.put(handler, newProcess);
        }
        return runtime;
    }

    /**
     * ScriptEndEventHandler will do work to complete the whole ScriptRuntime
     * process.
     *
     */
    private class ScriptEndEventProcess extends EventProcess<Event> {

        ScriptEndEventProcess(EventHandler<Event> handler, int depdending,
                              List<EventHandler<Event>> dependedEventHandlers) {
            super(handler, depdending, dependedEventHandlers);
        }

        @Override
        public void run() {
            runtime.markAsCompleted();
        }

    }

    private class ScriptEndEventHandler implements EventHandler<Event> {
        @Override
        public void onEvent(Event event) {
            /**
             * should never be called, ScriptEndEventHandlerProcess has override
             * run method.
             */
        }
    }

}
