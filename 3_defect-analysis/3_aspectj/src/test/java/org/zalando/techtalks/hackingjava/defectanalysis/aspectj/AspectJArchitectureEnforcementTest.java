package org.zalando.techtalks.hackingjava.defectanalysis.aspectj;

import org.zalando.techtalks.hackingjava.defectanalysis.aspectj.frontend.IllBehavedFrontendClass;
import org.zalando.techtalks.hackingjava.defectanalysis.aspectj.service.WellBehavedServiceClass;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.engine.DefectAnalysisEngine;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.IllBehaved;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.WellBehaved;
import org.zalando.techtalks.hackingjava.defectanalysis.tests.AbstractDefectDetectionTest;

/**
 * @author Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since 21.01.2016
 */
public class AspectJArchitectureEnforcementTest extends AbstractDefectDetectionTest {

  @Override protected Class<? extends WellBehaved> wellBehavedClass() {
    return WellBehavedServiceClass.class;
  }

  @Override protected Class<? extends IllBehaved> illBehavedClass() {
    return IllBehavedFrontendClass.class;
  }

  @Override protected String expectedErrorMessage() {
    return "The persistence package may not be accessed from the frontend package";
  }

  @Override protected DefectAnalysisEngine instantiateEngine() {
    return new AspectJPolicyEnforcementEngine();
  }
}
