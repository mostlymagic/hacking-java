package org.zalando.techtalks.hackingjava.defectanalysis.aspectj.frontend;

import org.zalando.techtalks.hackingjava.defectanalysis.aspectj.persistence.PersistenceTargetClass;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.IllBehaved;

public class IllBehavedFrontendClass implements IllBehaved {
  public void illBehavedMethod() {
    PersistenceTargetClass.dummy();
  }
}
