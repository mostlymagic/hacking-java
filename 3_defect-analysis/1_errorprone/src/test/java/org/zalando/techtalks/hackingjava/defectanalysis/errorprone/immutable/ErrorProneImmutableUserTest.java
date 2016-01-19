package org.zalando.techtalks.hackingjava.defectanalysis.errorprone.immutable;

import org.zalando.techtalks.hackingjava.defectanalysis.baseline.engine.DefectAnalysisEngine;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.immutable.AbstractFakeImmutableUser;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.immutable.AbstractProperImmutableUser;
import org.zalando.techtalks.hackingjava.defectanalysis.errorprone.engine.ErrorProneAnalysisEngine;
import org.zalando.techtalks.hackingjava.defectanalysis.tests.AbstractImmutabilityDetectionTest;

/**
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since   19.01.2016
 */
public class ErrorProneImmutableUserTest extends AbstractImmutabilityDetectionTest {

    @Override
    protected Class<? extends AbstractProperImmutableUser> properImmutableClass() {
        return ProperErrorProneImmutableUser.class;
    }

    @Override
    protected Class<? extends AbstractFakeImmutableUser> fakeImmutableClass() {
        return FakeErrorProneImmutableUser.class;
    }

    @Override
    protected DefectAnalysisEngine instantiateEngine() {
        return new ErrorProneAnalysisEngine();
    }
}
