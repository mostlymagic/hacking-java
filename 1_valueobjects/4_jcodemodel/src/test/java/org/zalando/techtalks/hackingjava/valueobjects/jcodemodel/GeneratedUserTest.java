package org.zalando.techtalks.hackingjava.valueobjects.jcodemodel;

import com.google.common.collect.ImmutableList;
import org.zalando.techtalks.hackingjava.valueobjects.tests.BaseUserTest;

public class GeneratedUserTest extends BaseUserTest {
    @Override protected Object createUser() {
        final Address address = new Address(STREET, ZIP_CODE, CITY);
        return new User(FIRST_NAME, LAST_NAME, BIRTH_DATE, ImmutableList.of(address));
    }
}