package com.gobrs.async.core.timer;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * The type Gobrs future task.
 *
 * @param <V> the type parameter
 * @program: gobrs -async
 * @ClassName GobrsFutureTask
 * @description:
 * @author: sizegang
 * @create: 2022 -12-13
 */
public class GobrsFutureTask<V> implements RunnableFuture<V> {

    /**
     * The constant STOP_STAMP.
     */
    public static final int STOP_STAMP = 5;

    /**
     * Synchronization control for FutureTask
     */
    private final Sync sync;

    /**
     * Creates a <tt>FutureTask</tt> that will, upon running, execute the
     * given <tt>Callable</tt>.
     *
     * @param callable the callable task
     * @throws NullPointerException if callable is null
     */
    public GobrsFutureTask(Callable<V> callable) {
        if (callable == null)
            throw new NullPointerException();

        sync = new Sync(callable);
    }

    /**
     * Creates a <tt>FutureTask</tt> that will, upon running, execute the
     * given <tt>Runnable</tt>, and arrange that <tt>get</tt> will return
     * the given result on successful completion.
     *
     * @param runnable the runnable task
     * @param result   the result to return on successful completion. If you                 don't need a particular result, consider using                 constructions of the form:                 <tt>Future&lt;?&gt; f = new FutureTask&lt;Object&gt;(runnable, null)</tt>
     * @throws NullPointerException if runnable is null
     */
    public GobrsFutureTask(Runnable runnable, V result) {
        sync = new Sync(Executors.callable(runnable, result));
    }

    @Override
    public boolean isCancelled() {
        return sync.innerIsCancelled();
    }

    @Override
    public boolean isDone() {
        return sync.innerIsDone();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return sync.innerCancel(mayInterruptIfRunning, false);
    }

    /**
     * Cancel boolean.
     *
     * @param mayInterruptIfRunning the may interrupt if running
     * @param stopBehind            the stop behind
     * @return the boolean
     */
    public boolean cancel(boolean mayInterruptIfRunning, boolean stopBehind) {
        return sync.innerCancel(mayInterruptIfRunning, stopBehind);
    }

    /**
     *
     * @param mayStopIfRunning the may stop if running
     * @return boolean
     */
    public boolean isMayStopIfRunning(boolean mayStopIfRunning) {
        if (sync.runner.getState() != Thread.State.RUNNABLE) {
            return cancel(true);
        }
        return sync.innerStop(mayStopIfRunning);
    }

    /**
     * @throws CancellationException {@inheritDoc}
     */
    @Override
    public V get() throws InterruptedException, ExecutionException {
        return sync.innerGet();
    }

    /**
     * @throws CancellationException {@inheritDoc}
     */
    public V get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        return sync.innerGet(unit.toNanos(timeout));
    }

    /**
     * Protected method invoked when this task transitions to state
     * <tt>isDone</tt> (whether normally or via cancellation). The default
     * implementation does nothing. Subclasses may override this method to
     * invoke completion callbacks or perform bookkeeping. Note that you can
     * query status inside the implementation of this method to determine
     * whether this task has been cancelled.
     */
    protected void done() {
    }

    /**
     * Sets the result of this Future to the given value unless this future
     * has already been set or has been cancelled. This method is invoked
     * internally by the <tt>run</tt> method upon successful completion of
     * the computation.
     *
     * @param v the value
     */
    protected void set(V v) {
        sync.innerSet(v);
    }

    /**
     * Causes this future to report an <tt>ExecutionException</tt> with the
     * given throwable as its cause, unless this Future has already been set
     * or has been cancelled. This method is invoked internally by the
     * <tt>run</tt> method upon failure of the computation.
     *
     * @param t the cause of failure
     */
    protected void setException(Throwable t) {
        sync.innerSetException(t);
    }

    // The following (duplicated) doc comment can be removed once
    //
    // 6270645: Javadoc comments should be inherited from most derived
    // superinterface or superclass
    // is fixed.

    /**
     * Sets this Future to the result of its computation unless it has been
     * cancelled.
     */
    @Override
    public void run() {
        sync.innerRun();
    }

    /**
     * Executes the computation without setting its result, and then resets
     * this Future to initial state, failing to do so if the computation
     * encounters an exception or is cancelled. This is designed for use
     * with tasks that intrinsically execute more than once.
     *
     * @return true if successfully run and reset
     */
    protected boolean runAndReset() {
        return sync.innerRunAndReset();
    }

    /**
     * Gets sync state.
     *
     * @return the sync state
     */
    public Integer getSyncState() {
        return sync.$$getState();
    }

    /**
     * Force stop if running boolean.
     *
     * @param mayStopIfRunning the may stop if running
     * @return the boolean
     */
    public boolean forceStopIfRunning(boolean mayStopIfRunning) {
        return sync.innerStop(mayStopIfRunning);
    }

    /**
     * Synchronization control for FutureTask. Note that this must be a
     * non-static inner class in order to invoke the protected <tt>done</tt>
     * method. For clarity, all inner class support methods are same as
     * outer, prefixed with "inner".
     * <p>
     * Uses AQS sync state to represent run status
     */
    private final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = -7828117401763700385L;

        /**
         * State value representing that task is ready to run
         */
        private static final int READY = 0;
        /**
         * State value representing that task is running
         */
        private static final int RUNNING = 1;
        /**
         * State value representing that task ran
         */
        private static final int RAN = 2;
        /**
         * State value representing that task was cancelled
         */
        private static final int CANCELLED = 4;

        /**
         * The underlying callable
         */
        private final Callable<V> callable;
        /**
         * The result to return from get()
         */
        private V result;
        /**
         * The exception to throw from get()
         */
        private Throwable exception;

        /**
         * The thread running task. When nulled after set/cancel, this
         * indicates that the results are accessible. Must be volatile, to
         * ensure visibility upon completion.
         */
        private volatile Thread runner;


        /**
         * $$ get state int.
         *
         * @return the int
         */
        public int $$getState() {
            return getState();
        }

        /**
         * Instantiates a new Sync.
         *
         * @param callable the callable
         */
        Sync(Callable<V> callable) {
            this.callable = callable;
        }

        private boolean ranOrCancelled(int state) {
            return (state & (RAN | CANCELLED)) != 0;
        }

        /**
         * Implements AQS base acquire to succeed if ran or cancelled
         */
        @Override
        protected int tryAcquireShared(int ignore) {
            return innerIsDone() ? 1 : -1;
        }

        /**
         * Implements AQS base release to always signal after setting final
         * done status by nulling runner thread.
         */
        @Override
        protected boolean tryReleaseShared(int ignore) {
            runner = null;
            return true;
        }

        /**
         * Inner is cancelled boolean.
         *
         * @return the boolean
         */
        boolean innerIsCancelled() {
            return getState() == CANCELLED;
        }

        /**
         * Inner is done boolean.
         *
         * @return the boolean
         */
        boolean innerIsDone() {
            return ranOrCancelled(getState()) && runner == null;
        }

        /**
         * Inner get v.
         *
         * @return the v
         * @throws InterruptedException the interrupted exception
         * @throws ExecutionException   the execution exception
         */
        V innerGet() throws InterruptedException, ExecutionException {
            acquireSharedInterruptibly(0);
            if (getState() == CANCELLED)
                throw new CancellationException();
            if (exception != null)
                throw new ExecutionException(exception);
            return result;
        }

        /**
         * Inner get v.
         *
         * @param nanosTimeout the nanos timeout
         * @return the v
         * @throws InterruptedException the interrupted exception
         * @throws ExecutionException   the execution exception
         * @throws TimeoutException     the timeout exception
         */
        V innerGet(long nanosTimeout) throws InterruptedException,
                ExecutionException, TimeoutException {
            if (!tryAcquireSharedNanos(0, nanosTimeout))
                throw new TimeoutException();
            if (getState() == CANCELLED)
                throw new CancellationException();
            if (exception != null)
                throw new ExecutionException(exception);
            return result;
        }

        /**
         * Inner set.
         *
         * @param v the v
         */
        void innerSet(V v) {
            for (; ; ) {
                int s = getState();
                if (s == RAN)
                    return;
                if (s == CANCELLED) {
                    // aggressively release to set runner to null,
                    // in case we are racing with a cancel request
                    // that will try to interrupt runner
                    releaseShared(0);
                    return;
                }
                if (compareAndSetState(s, RAN)) {
                    result = v;
                    releaseShared(0);
                    done();
                    return;
                }
            }
        }

        /**
         * Inner set exception.
         *
         * @param t the t
         */
        void innerSetException(Throwable t) {
            for (; ; ) {
                int s = getState();
                if (s == RAN)
                    return;
                if (s == CANCELLED) {
                    // aggressively release to set runner to null,
                    // in case we are racing with a cancel request
                    // that will try to interrupt runner
                    releaseShared(0);
                    return;
                }
                if (compareAndSetState(s, RAN)) {
                    exception = t;
                    releaseShared(0);
                    done();
                    return;
                }
            }
        }

        /**
         * Inner stop boolean.
         *
         * @param mayStopIfRunning the may stop if running
         * @return boolean
         */
        boolean innerStop(boolean mayStopIfRunning) {
            for (; ; ) {
                int s = getState();
                if (ranOrCancelled(s))
                    return false;
                if (compareAndSetState(s, CANCELLED))
                    break;
            }
            if (mayStopIfRunning) {
                Thread r = runner;
                if (r != null) {
                    r.stop();//这里调用线程stop方法
                    setState(STOP_STAMP);
                }
            }
            releaseShared(0);
            done();

            return true;
        }

        /**
         * Inner cancel boolean.
         *
         * @param mayInterruptIfRunning the may interrupt if running
         * @param stopBehind            the stop behind
         * @return the boolean
         */
        boolean innerCancel(boolean mayInterruptIfRunning, boolean stopBehind) {
            for (; ; ) {
                int s = getState();
                if (ranOrCancelled(s))
                    return false;
                if (compareAndSetState(s, CANCELLED))
                    break;
            }
            if (mayInterruptIfRunning) {
                Thread r = runner;
                if (r != null) {
                    r.interrupt();
                }
            }
            releaseShared(0);
            done();
            return true;
        }

        /**
         * Inner run.
         */
        void innerRun() {
            if (!compareAndSetState(READY, RUNNING))
                return;

            runner = Thread.currentThread();
            if (getState() == RUNNING) { // recheck after setting thread
                V result;
                try {
                    result = callable.call();
                } catch (Throwable ex) {
                    setException(ex);
                    return;
                }
                set(result);
            } else {
                releaseShared(0); // cancel
            }
        }

        /**
         * Inner run and reset boolean.
         *
         * @return the boolean
         */
        boolean innerRunAndReset() {
            if (!compareAndSetState(READY, RUNNING))
                return false;
            try {
                runner = Thread.currentThread();
                if (getState() == RUNNING)
                    callable.call(); // don't set result
                runner = null;
                return compareAndSetState(RUNNING, READY);
            } catch (Throwable ex) {
                setException(ex);
                return false;
            }
        }
    }

}
