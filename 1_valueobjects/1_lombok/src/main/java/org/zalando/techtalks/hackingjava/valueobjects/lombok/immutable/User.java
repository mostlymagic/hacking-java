package org.zalando.techtalks.hackingjava.valueobjects.lombok.immutable;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ComparisonChain;

import lombok.Data;

@Data
public class User implements Comparable<User> {
    private final String firstName;
    private final String lastName;
    private final LocalDate birthDate;
    private final List<Address> addresses;

    public User(final String firstName, final String lastName, final LocalDate birthDate,
            final List<Address> addresses) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.addresses = new ArrayList<>(addresses);
    }

    public List<Address> getAddresses() {
        return Collections.unmodifiableList(addresses);
    }

    @Override
    public int compareTo(final User that) {
        return ComparisonChain.start()
                              .compare(this.lastName, that.lastName)
                              .compare(this.firstName, that.firstName)
                              .compare(this.birthDate, that.birthDate)
                              .result();
    }

}
