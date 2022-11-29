package com.gobrs.async.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;

/**
 * The type Gobrs print.
 *
 * @program: gobrs -async-core
 * @ClassName com.gobrs.async.GobrsPrint
 * @description:
 * @author: sizegang
 * @create: 2022 -04-09
 */
public class GobrsPrint {

    /**
     * The Logger.
     */
    static Logger logger = LoggerFactory.getLogger(GobrsPrint.class);


    private static final String GOBRS_ASYNC = " :: Gobrs-Async :: ";

    private static final String GOBRS_ASYNC_GITHUB = "GitHub:  https://github.com/dromara/gobrs-async";

    private static final String GOBRS_ASYNC_GITEE = "Gitee:  https://gitee.com/dromara/gobrs-async";

    private static final String GOBRS_ASYNC_SITE = "Site:  https://docs.sizegang.cn";

    private static final int STRAP_LINE_SIZE = 50;


    /**
     * Print banner.
     */
    public static void printBanner() {
        String banner = "              ___.                             _____                               \n" +
                "   ____   ____\\_ |_________  ______           /  _  \\   _________.__. ____   ____  \n" +
                "  / ___\\ /  _ \\| __ \\_  __ \\/  ___/  ______  /  /_\\  \\ /  ___<   |  |/    \\_/ ___\\ \n" +
                " / /_/  >  <_> ) \\_\\ \\  | \\/\\___ \\  /_____/ /    |    \\\\___ \\ \\___  |   |  \\  \\___ \n" +
                " \\___  / \\____/|___  /__|  /____  >         \\____|__  /____  >/ ____|___|  /\\___  >\n" +
                "/_____/            \\/           \\/                  \\/     \\/ \\/         \\/     \\/ ";

        String version = getVersion();
        version = (version != null) ? " jdk (v" + version + ")" : "no version.";

        StringBuilder padding = new StringBuilder();
        while (padding.length() < STRAP_LINE_SIZE - (version.length() + GOBRS_ASYNC.length())) {
            padding.append(" ");
        }

        System.out.println(AnsiOutput.toString(banner, AnsiColor.GREEN, GOBRS_ASYNC, AnsiColor.DEFAULT,
                padding.toString(), AnsiStyle.FAINT, version, "\n\n", GOBRS_ASYNC_GITHUB, "\n", GOBRS_ASYNC_GITEE, "\n", GOBRS_ASYNC_SITE, "\n"));
        logger.info("Gobrs-Async Load Successful");
    }

    /**
     * get current software package version
     *
     * @return the version
     */
    public static String getVersion() {
        Package pkg = Package.getPackage("java.com.gobrs.async.util");
        return pkg != null ? pkg.getImplementationVersion() : "";
    }

}
