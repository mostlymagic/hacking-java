package org.zalando.techtalks.hackingjava.valueobjects.autovalue.builder;

import org.zalando.techtalks.hackingjava.valueobjects.tests.BaseUserTest;

import static org.zalando.techtalks.hackingjava.valueobjects.autovalue.builder.Address
    .addressBuilder;
import static org.zalando.techtalks.hackingjava.valueobjects.autovalue.builder.User.userBuilder;

public class BuilderUserTest extends BaseUserTest {

    @Override protected Object createUser() {
        return userBuilder()
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .birthDate(BIRTH_DATE)
            .addresses(
                addressBuilder()
                    .street(STREET)
                    .zipCode(ZIP_CODE)
                    .city(CITY)
                    .build())
            .build();
    }
}