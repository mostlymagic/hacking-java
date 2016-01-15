package org.zalando.techtalks.hackingjava.valueobjects.autovalue.plain;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.time.LocalDate;
import java.util.List;

@AutoValue
public abstract class User implements Comparable<User> {
    public abstract String getFirstName();

    public abstract String getLastName();

    public abstract LocalDate getBirthDate();

    public abstract List<Address> getAddresses();

    public static User create(final String firstName,
                              final String lastName,
                              final LocalDate birthDate,
                              final Address address,
                              final Address... moreAddresses) {
        return new AutoValue_User(firstName,
                                  lastName,
                                  birthDate,
                                  ImmutableList.copyOf(Lists.asList(address, moreAddresses)));
    }

    @Override public int compareTo(final User that) {
        return ComparisonChain.start()
                              .compare(this.getLastName(), that.getLastName())
                              .compare(this.getFirstName(), that.getFirstName())
                              .compare(this.getBirthDate(), that.getBirthDate())
                              .result();
    }
}
