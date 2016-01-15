package org.zalando.techtalks.hackingjava.valueobjects.cglib;

import org.zalando.techtalks.hackingjava.valueobjects.tests.BaseUserTest;

/**
 * @author Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since 15.01.2016
 */
public class UserFactoryTest extends BaseUserTest {

    @Override protected Object createUser() {
        return UserFactory.createUser(FIRST_NAME, LAST_NAME, BIRTH_DATE, STREET, ZIP_CODE, CITY);
    }
}