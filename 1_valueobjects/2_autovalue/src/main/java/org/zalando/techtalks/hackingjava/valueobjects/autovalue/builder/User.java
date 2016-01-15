package org.zalando.techtalks.hackingjava.valueobjects.autovalue.builder;

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

    public static Builder userBuilder() {return new AutoValue_User.Builder();}

    @AutoValue.Builder interface Builder {
        Builder firstName(String firstName);

        Builder lastName(String lastName);

        Builder birthDate(LocalDate birthDate);

        Builder addresses(List<Address> addresses);

        default Builder addresses(Address address, Address... moreAddresses) {
            return addresses(ImmutableList.copyOf(Lists.asList(address, moreAddresses)));
        }

        User build();
    }

    @Override public int compareTo(final User that) {
        return ComparisonChain.start()
                              .compare(this.getLastName(), that.getLastName())
                              .compare(this.getFirstName(), that.getFirstName())
                              .compare(this.getBirthDate(), that.getBirthDate())
                              .result();
    }
}
