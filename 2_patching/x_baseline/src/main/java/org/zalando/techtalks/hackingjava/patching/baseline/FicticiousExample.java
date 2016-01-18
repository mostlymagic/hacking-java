package org.zalando.techtalks.hackingjava.patching.baseline;

import java.util.Iterator;

public class FicticiousExample {

    public static final String RAN_BUGGY_CODE = "ran buggy code";
    public static final String TRUE = "true";

    public Integer yUNoReuseInteger(final int value) {
        System.setProperty(RAN_BUGGY_CODE, TRUE);
        return new Integer(value);
    }

    public String yUStringConcatInLoop(final Iterable<String> data, final String delimiter) {
        System.setProperty(RAN_BUGGY_CODE, TRUE);

        String value = "";
        final Iterator<String> iterator = data.iterator();
        if (iterator.hasNext()) {
            value += iterator.next();
        }
        while (iterator.hasNext()) {
            value += delimiter + iterator.next();
        }

        return value;
    }
}
