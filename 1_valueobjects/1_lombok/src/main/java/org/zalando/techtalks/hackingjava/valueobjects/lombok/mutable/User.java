package org.zalando.techtalks.hackingjava.valueobjects.lombok.mutable;

import com.google.common.collect.ComparisonChain;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class User implements Comparable<User> {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private List<Address> addresses;

    @Override public int compareTo(final User that) {
        return ComparisonChain.start()
                              .compare(this.lastName, that.lastName)
                              .compare(this.firstName, that.firstName)
                              .compare(this.birthDate, that.birthDate)
                              .result();
    }
}
