package org.zalando.techtalks.hackingjava.defectanalysis.errorprone.immutable;

import com.google.common.collect.ImmutableSet;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.engine.DefectAnalysisEngine;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.IllBehaved;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.WellBehaved;
import org.zalando.techtalks.hackingjava.defectanalysis.errorprone.engine.ErrorProneAnalysisEngine;
import org.zalando.techtalks.hackingjava.defectanalysis.tests.AbstractDefectDetectionTest;

public class ErrorProneImmutableUserTest extends AbstractDefectDetectionTest {

    @Override
    protected Class<? extends WellBehaved> wellBehavedClass() {
        return ProperErrorProneImmutableUser.class;
    }

    @Override
    protected Class<? extends IllBehaved> illBehavedClass() {
        return FakeErrorProneImmutableUser.class;
    }

    @Override protected String expectedErrorMessage() {
        return "http://errorprone.info/bugpattern/Immutable";
    }

    @Override
    protected DefectAnalysisEngine instantiateEngine() {
        return new ErrorProneAnalysisEngine(ImmutableSet.of("Immutable"));
    }
}
