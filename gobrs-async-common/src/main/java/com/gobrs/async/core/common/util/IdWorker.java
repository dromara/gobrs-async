package com.gobrs.async.core.common.util;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

/**
 * 雪花算法（SnowFlake）
 *
 * @author sizegang
 * @version 1.0
 */
@Slf4j
public class IdWorker {
    /**
     * @Description:
     **/

    /**
     * @Description:
     **/
    private static final long EPOCH;

    /**
     * @Description:
     **/
    private static final long SEQUENCE_BITS = 6L;

    /**
     * @Description:
     **/
    private static final long WORKER_ID_BITS = 10L;

    /**
     * @Description:
     **/
    private static final long SEQUENCE_MASK = (1 << SEQUENCE_BITS) - 1;

    /**
     * @Description:
     **/
    private static final long WORKER_ID_LEFT_SHIFT_BITS = SEQUENCE_BITS;

    /**
     * @Description:
     **/
    private static final long TIMESTAMP_LEFT_SHIFT_BITS = WORKER_ID_LEFT_SHIFT_BITS + WORKER_ID_BITS;

    /**
     * @Description:
     **/
    private static final long WORKER_ID_MAX_VALUE = 1L << WORKER_ID_BITS;

    /**
     * @Description:
     **/
    private static long workerId;

    /**
     * @Description:
     **/
    static {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, Calendar.APRIL, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        EPOCH = calendar.getTimeInMillis();
        initWorkerId();
    }

    /**
     * @Description:
     **/
    private static long sequence;

    /**
     * @Description:
     **/
    private static long lastTime;

    /**
     * 初始化workerId
     */
    private static void initWorkerId() {
        InetAddress address = getLocalAddress();
        byte[] ipAddressByteArray = address.getAddress();
        setWorkerId((long) (((ipAddressByteArray[ipAddressByteArray.length - 2] & 0B11) << Byte.SIZE) + (ipAddressByteArray[ipAddressByteArray.length - 1] & 0xFF)));
    }

    /**
     * @Description:
     **/
    private static InetAddress getLocalAddress() {
        try {
            for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                if (addresses.hasMoreElements()) {
                    return addresses.nextElement();
                }
            }
        } catch (Exception e) {
            log.debug("Error when getting host ip address: <{}>.", e.getMessage());
            throw new IllegalStateException("Cannot get LocalHost InetAddress, please check your network!");
        }
        return null;
    }

    /**
     * 设置工作进程Id.
     *
     * @param workerId 工作进程Id
     */
    private static void setWorkerId(final Long workerId) {
        if (workerId >= 0L && workerId < WORKER_ID_MAX_VALUE) {
            IdWorker.workerId = workerId;
        } else {
            throw new RuntimeException("workerId is illegal");
        }
    }

    /**
     * @Description: 下一个ID生成算法
     **/
    public static long nextId() {
        long time = System.currentTimeMillis();
        if (lastTime > time) {
            throw new RuntimeException("Clock is moving backwards, last time is %d milliseconds, current time is %d milliseconds" + lastTime);
        }
        if (lastTime == time) {
            if (0L == (sequence = ++sequence & SEQUENCE_MASK)) {
                time = waitUntilNextTime(time);
            }
        } else {
            sequence = 0;
        }
        lastTime = time;
        if (log.isDebugEnabled()) {
            log.debug("{}-{}-{}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(lastTime)), workerId, sequence);
        }
        return ((time - EPOCH) << TIMESTAMP_LEFT_SHIFT_BITS) | (workerId << WORKER_ID_LEFT_SHIFT_BITS) | sequence;
    }

    /**
     * @Description:
     **/
    private static long waitUntilNextTime(final long lastTime) {
        long time = System.currentTimeMillis();
        while (time <= lastTime) {
            time = System.currentTimeMillis();
        }
        return time;
    }
}
