package org.zalando.techtalks.hackingjava.defectanalysis.errorprone.immutable;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import java.util.Date;
import java.util.List;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.immutable.AbstractProperImmutableUser;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.WellBehaved;

@Immutable
public class ProperErrorProneImmutableUser extends AbstractProperImmutableUser implements WellBehaved {
    private final String name;

    @SuppressWarnings("Immutable")
    private final Date birthDate;

    @SuppressWarnings("Immutable")
    private final List<String> nickNames;

    public ProperErrorProneImmutableUser(final String name, final List<String> nickNames, final Date birthDate) {
        this.name = name;
        this.nickNames = ImmutableList.copyOf(nickNames);
        this.birthDate = new Date(birthDate.getTime());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getNickNames() {
        return ImmutableList.copyOf(nickNames);
    }

    @Override
    public Date getBirthDate() {
        return new Date(birthDate.getTime());
    }
}
