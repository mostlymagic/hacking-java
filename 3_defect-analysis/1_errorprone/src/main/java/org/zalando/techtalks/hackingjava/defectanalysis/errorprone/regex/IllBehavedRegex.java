package org.zalando.techtalks.hackingjava.defectanalysis.errorprone.regex;

import java.util.Arrays;
import java.util.List;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.IllBehaved;

public class IllBehavedRegex implements IllBehaved {
    public static List<String> splitByBadPattern(String input) {
        return Arrays.asList(input.split("[a-z"));
    }

}
