package org.zalando.techtalks.hackingjava.valueobjects.lombok.builder;

import com.google.common.collect.ComparisonChain;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User implements Comparable<User> {
    private final String firstName;
    private final String lastName;
    private final LocalDate birthDate;
    private final List<Address> addresses;

    @Override public int compareTo(final User that) {
        return ComparisonChain.start()
                              .compare(this.lastName, that.lastName)
                              .compare(this.firstName, that.firstName)
                              .compare(this.birthDate, that.birthDate)
                              .result();
    }
}
