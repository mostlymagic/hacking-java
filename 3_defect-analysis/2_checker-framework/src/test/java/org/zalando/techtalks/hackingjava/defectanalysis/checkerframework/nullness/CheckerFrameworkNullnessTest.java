package org.zalando.techtalks.hackingjava.defectanalysis.checkerframework.nullness;

import org.checkerframework.checker.nullness.NullnessChecker;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.engine.DefectAnalysisEngine;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.IllBehaved;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.WellBehaved;
import org.zalando.techtalks.hackingjava.defectanalysis.checkerframework.engine.CheckerFrameworkAnalysisEngine;
import org.zalando.techtalks.hackingjava.defectanalysis.tests.AbstractDefectDetectionTest;

/**
 * @author Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since 21.01.2016
 */
public class CheckerFrameworkNullnessTest extends AbstractDefectDetectionTest {

    @Override protected Class<? extends WellBehaved> wellBehavedClass() {
        return WellbehavedNullness.class;
    }

    @Override protected Class<? extends IllBehaved> illBehavedClass() {
        return IllbehavedNullness.class;
    }

    @Override protected String expectedErrorMessage() {
        return "required: @Initialized @NonNull String";
    }

    @Override protected DefectAnalysisEngine instantiateEngine() {
        return new CheckerFrameworkAnalysisEngine(NullnessChecker.class);
    }
}