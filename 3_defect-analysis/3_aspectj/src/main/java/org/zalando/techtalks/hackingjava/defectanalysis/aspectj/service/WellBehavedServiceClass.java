package org.zalando.techtalks.hackingjava.defectanalysis.aspectj.service;

import org.zalando.techtalks.hackingjava.defectanalysis.aspectj.persistence.PersistenceTargetClass;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.WellBehaved;

public class WellBehavedServiceClass implements WellBehaved {
  public void wellBehavedMethod() {
    PersistenceTargetClass.dummy();
  }
}
