package org.zalando.techtalks.hackingjava.valueobjects.cglib;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.time.LocalDate;
import java.util.List;

import static org.zalando.techtalks.hackingjava.valueobjects.cglib.DtoFactory.createBeanClass;

public class UserFactory {
    public static Object createUser(final String firstName,
                                    final String lastName,
                                    final LocalDate birthDate,
                                    final String street,
                                    final int zipCode,
                                    final String city) {

        final Object address = DtoFactory.instantiateDto(Types.ADDRESS, ImmutableMap.of(
            "street", street, "zipCode", zipCode, "city", city
        ));
        final ImmutableList<Object> addresses = ImmutableList.of(address);
        final Object user = DtoFactory.instantiateDto(Types.USER,
                                                      ImmutableMap.of("firstName", firstName,
                                                                      "lastName", lastName,
                                                                      "addresses", addresses,
                                                                      "birthDate", birthDate),
                                                      ImmutableList.of("lastName",
                                                                       "firstName",
                                                                       "birthDate")
        );
        return user;
    }


    static class Types {
        private static final String PACKAGE = "org.zalando.techtalks.hackingjava.valueobjects" +
            ".cglib.dto";
        private static final Class<?> USER;
        private static final Class<?> ADDRESS;

        static {
            USER = createBeanClass(PACKAGE + ".User",
                                   ImmutableMap.of("firstName", String.class,
                                                   "lastName", String.class,
                                                   "birthDate", LocalDate.class,
                                                   "addresses", List.class));
            ADDRESS = createBeanClass(PACKAGE + ".Address",
                                      ImmutableMap.of("street", String.class,
                                                      "zipCode", int.class,
                                                      "city", String.class));
        }
    }

}
