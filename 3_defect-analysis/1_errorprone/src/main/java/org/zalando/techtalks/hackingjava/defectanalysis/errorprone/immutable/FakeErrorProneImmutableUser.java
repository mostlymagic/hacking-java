package org.zalando.techtalks.hackingjava.defectanalysis.errorprone.immutable;

import java.util.Date;
import java.util.List;

import org.zalando.techtalks.hackingjava.defectanalysis.baseline.immutable.AbstractFakeImmutableUser;

import com.google.errorprone.annotations.Immutable;

/**
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since   19.01.2016
 */
@Immutable
public class FakeErrorProneImmutableUser extends AbstractFakeImmutableUser {
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
