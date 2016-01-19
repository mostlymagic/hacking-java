package org.zalando.techtalks.hackingjava.defectanalysis.baseline.immutable;

import java.util.Date;
import java.util.List;

public abstract class AbstractFakeImmutableUser extends AbstractUser {
    public abstract void setName(String name);

    public abstract void setNickNames(List<String> nickNames);

    public abstract void setBirthDate(Date birthDate);

}
