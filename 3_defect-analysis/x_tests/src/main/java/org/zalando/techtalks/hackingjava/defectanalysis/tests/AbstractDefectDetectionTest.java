package org.zalando.techtalks.hackingjava.defectanalysis.tests;

import org.junit.Test;
import org.zalando.techtalks.hackingjava.common.compiler.CompilationResult;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.IllBehaved;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.WellBehaved;

import static org.junit.Assert.assertThat;

public abstract class AbstractDefectDetectionTest extends AbstractCompilerTest {

    protected abstract Class<? extends WellBehaved> wellBehavedClass();

    protected abstract Class<? extends IllBehaved> illBehavedClass();

    @Test
    public void detectWellBehavedClass() {
        final CompilationResult result = engine.compile(sourceFileFor(wellBehavedClass()));
        assertThat(result, isSuccess());
    }

    @Test
    public void detectIllBehavedClass() {
        final CompilationResult result = engine.compile(sourceFileFor(illBehavedClass()));
        assertThat(result, isFailureWithExpectedMessage(expectedErrorMessage()));
    }

    protected abstract String expectedErrorMessage();

}
