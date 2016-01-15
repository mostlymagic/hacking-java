package org.zalando.techtalks.hackingjava.valueobjects.lombok.builder;

import org.zalando.techtalks.hackingjava.valueobjects.tests.BaseUserTest;

import static java.util.Arrays.asList;

public class BuilderUserTest extends BaseUserTest {

    @Override protected Object createUser() {
        return User.builder()
                   .firstName(FIRST_NAME)
                   .lastName(LAST_NAME)
                   .birthDate(BIRTH_DATE)
                   .addresses(asList(
                       Address.builder()
                              .street(STREET)
                              .zipCode(ZIP_CODE)
                              .city(CITY)
                              .build()
                   )).build();
    }
}