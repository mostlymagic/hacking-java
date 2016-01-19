package org.zalando.techtalks.hackingjava.defectanalysis.errorprone.immutable;

import java.util.Date;
import java.util.List;

import org.zalando.techtalks.hackingjava.defectanalysis.baseline.immutable.AbstractProperImmutableUser;

import com.google.common.collect.ImmutableList;

import com.google.errorprone.annotations.Immutable;

/**
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since   19.01.2016
 */
@Immutable
public class ProperErrorProneImmutableUser extends AbstractProperImmutableUser {
    private final String name;
    private final List<String> nickNames;
    private final Date birthDate;

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
