package com.gobrs.async.core.scan;

import com.gobrs.async.core.common.scan.Scanner;
import org.springframework.beans.factory.InitializingBean;

/**
 * The type Base scannner.
 *
 * @program: gobrs -async
 * @ClassName BaseScannner
 * @description:
 * @author: sizegang
 * @create: 2023 -01-04
 */
public abstract class BaseScannner implements Scanner, InitializingBean {
    @Override
    public void scan() {
        doScan();
    }

    @Override
    public abstract void doScan();

}
