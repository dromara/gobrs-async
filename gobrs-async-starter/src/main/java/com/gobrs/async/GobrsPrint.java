package com.gobrs.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.context.annotation.Configuration;

/**
 * @program: gobrs-async-core
 * @ClassName GobrsPrint
 * @description:
 * @author: sizegang
 * @create: 2022-04-09
 **/
public class GobrsPrint {

    static Logger logger = LoggerFactory.getLogger(GobrsPrint.class);


    private static final String GOBRS_ASYNC = " :: Gobrs-Async :: ";

    private static final String GOBRS_ASYNC_GITHUB = "GitHub:  https://github.com/Memorydoc/gobrs-async";

    private static final String GOBRS_ASYNC_SITE = "Site:  https://docs.sizegang.cn";

    private static final int STRAP_LINE_SIZE = 50;


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
                padding.toString(), AnsiStyle.FAINT, version, "\n\n", GOBRS_ASYNC_GITHUB, "\n", GOBRS_ASYNC_SITE, "\n"));
        logger.info("Gobrs-Async Load Successful");
    }

    public static String getVersion() {
        Package pkg = Package.getPackage("java.util");
        return pkg != null ? pkg.getImplementationVersion() : "";
    }

}
