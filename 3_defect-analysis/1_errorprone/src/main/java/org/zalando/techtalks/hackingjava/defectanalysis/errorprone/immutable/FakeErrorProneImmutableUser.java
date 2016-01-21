package org.zalando.techtalks.hackingjava.defectanalysis.errorprone.immutable;

import com.google.errorprone.annotations.Immutable;
import java.util.Date;
import java.util.List;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.immutable.AbstractFakeImmutableUser;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.IllBehaved;

@Immutable
public class FakeErrorProneImmutableUser extends AbstractFakeImmutableUser implements IllBehaved {
    private String name;
    private List<String> nickNames;
    private Date birthDate;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public List<String> getNickNames() {
        return nickNames;
    }

    @Override
    public void setNickNames(final List<String> nickNames) {
        this.nickNames = nickNames;
    }

    @Override
    public Date getBirthDate() {
        return birthDate;
    }

    @Override
    public void setBirthDate(final Date birthDate) {
        this.birthDate = birthDate;
    }
}
