package com.gobrs.async.spi.test.portocol;

import com.gobrs.async.spi.SPI;

/**
 * The interface Protocol.
 *
 * @program: gobrs -async
 * @ClassName Order
 * @description:
 * @author: sizegang
 * @create: 2022 -12-13
 */
@SPI("hession")
public interface Protocol {

    /**
     * Serialize string.
     *
     * @return the string
     */
    String serialize();

}
