package org.zalando.techtalks.hackingjava.valueobjects.autovalue.plain;

import org.zalando.techtalks.hackingjava.valueobjects.tests.BaseUserTest;

public class PlainUserTest extends BaseUserTest {

    @Override protected Object createUser() {
        return User.create(FIRST_NAME, LAST_NAME, BIRTH_DATE,
                           Address.create(STREET, ZIP_CODE, CITY)
        );
    }
}