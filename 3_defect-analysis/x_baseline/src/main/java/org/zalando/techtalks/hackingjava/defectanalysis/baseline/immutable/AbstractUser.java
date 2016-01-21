package org.zalando.techtalks.hackingjava.defectanalysis.baseline.immutable;

import java.util.Date;
import java.util.List;

public abstract class AbstractUser {
    public abstract String getName();

    public abstract List<String> getNickNames();

    public abstract Date getBirthDate();
}
