package org.zalando.techtalks.hackingjava.defectanalysis.aspectj;

import org.zalando.techtalks.hackingjava.defectanalysis.baseline.engine.DefectAnalysisEngine;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.IllBehaved;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.WellBehaved;
import org.zalando.techtalks.hackingjava.defectanalysis.tests.AbstractDefectDetectionTest;

/**
 * @author Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since 21.01.2016
 */
public class AspectJPolicyEnforcementTest extends AbstractDefectDetectionTest {

    @Override protected Class<? extends WellBehaved> wellBehavedClass() {
        return WellBehavedClass.class;
    }

    @Override protected Class<? extends IllBehaved> illBehavedClass() {
        return IllBehavedClass.class;
    }

    @Override protected String expectedErrorMessage() {
        return "Hashtable and Vector are deprecated classes";
    }

    @Override protected DefectAnalysisEngine instantiateEngine() {
        return new AspectJPolicyEnforcementEngine();
    }
}