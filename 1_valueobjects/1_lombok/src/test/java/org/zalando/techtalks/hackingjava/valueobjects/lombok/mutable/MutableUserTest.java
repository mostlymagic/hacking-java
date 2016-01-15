package org.zalando.techtalks.hackingjava.valueobjects.lombok.mutable;

import java.time.LocalDate;
import java.util.Arrays;
import org.zalando.techtalks.hackingjava.valueobjects.tests.BaseUserTest;


public class MutableUserTest extends BaseUserTest {


    @Override protected User createUser() {
        final User user = new User();
        user.setFirstName("Fred");
        user.setLastName("Flintstone");
        user.setBirthDate(LocalDate.MIN);
        final Address address = new Address();
        user.setAddresses(Arrays.asList(address));
        address.setStreet("301 Cobblestone Way");
        address.setZipCode(70777);
        address.setCity("Bedrock");
        return user;
    }

}