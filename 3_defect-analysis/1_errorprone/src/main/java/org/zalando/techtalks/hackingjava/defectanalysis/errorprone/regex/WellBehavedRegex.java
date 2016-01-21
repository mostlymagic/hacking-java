package org.zalando.techtalks.hackingjava.defectanalysis.errorprone.regex;

import java.util.Arrays;
import java.util.List;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.WellBehaved;

public class WellBehavedRegex implements WellBehaved {
    public static List<String> splitByBadPattern(String input) {
        return Arrays.asList(input.split("[a-z]"));
    }
}
