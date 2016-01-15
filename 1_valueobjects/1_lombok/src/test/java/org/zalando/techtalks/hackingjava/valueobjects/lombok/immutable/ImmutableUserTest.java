package org.zalando.techtalks.hackingjava.valueobjects.lombok.immutable;

import java.time.LocalDate;
import java.util.Arrays;
import org.zalando.techtalks.hackingjava.valueobjects.tests.BaseUserTest;


public class ImmutableUserTest extends BaseUserTest {

    @Override protected Object createUser() {
        return new User("Fred", "Flintstone", LocalDate.MIN, Arrays.asList(
            new Address("301 Cobblestone Way", 70777, "Bedrock")
        ));
    }
}