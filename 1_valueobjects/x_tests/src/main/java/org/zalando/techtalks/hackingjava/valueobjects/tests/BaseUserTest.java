package org.zalando.techtalks.hackingjava.valueobjects.tests;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;

public abstract class BaseUserTest {
    protected static final String FIRST_NAME = "Fred";
    protected static final String LAST_NAME = "Flintstone";
    protected static final LocalDate BIRTH_DATE = LocalDate.MIN;
    protected static final String STREET = "301 Cobblestone Way";
    protected static final int ZIP_CODE = 70777;
    protected static final String CITY = "Bedrock";

    @Test
    public void equalsAndHashCodeAreSymmetrical() {
        final Object user1 = createUser();
        final Object user2 = createUser();
        assertThat(user1, is(equalTo(user2)));
        assertThat(user2, is(equalTo(user1)));
        assertThat(user1.hashCode(), is(equalTo(user2.hashCode())));
    }

    @Test
    public void toStringIsConsistent() {
        assertThat(createUser().toString(), is(equalTo(createUser().toString())));
        final String s = createUser().toString();
        assertThat(s, containsString(FIRST_NAME));
        assertThat(s, containsString(LAST_NAME));
        assertThat(s, containsString(CITY));
    }

    @SuppressWarnings({"unchecked", "rawtypes"}) @Test
    public void compareToIsSymmetrical() {
        final Object left = createUser();
        final Object right = createUser();
        assertThat(left, instanceOf(Comparable.class));
        assertThat(right, instanceOf(Comparable.class));
        assertThat(((Comparable) left).compareTo(right),
                   is(equalTo(((Comparable) right).compareTo(left))));
    }

    @Test
    public void propertyMapHasCorrectValues() {
        final Object instance = createUser();
        final Map<String, Object> userPropertyMap = getPropertyMap(instance);
        assertThat(userPropertyMap, hasEntry("firstName", FIRST_NAME));
        assertThat(userPropertyMap, hasEntry("lastName", LAST_NAME));
        assertThat(userPropertyMap, hasEntry("birthDate", BIRTH_DATE));
        assertThat(userPropertyMap, hasKey("addresses"));


        final Object addresses = userPropertyMap.get("addresses");
        assertThat(addresses, is(instanceOf(List.class)));
        final List<?> list = List.class.cast(addresses);
        assertThat(list, hasSize(1));

        final Map<String, Object> addressPropertyMap = getPropertyMap(list.get(0));
        assertThat(addressPropertyMap, hasEntry("street", STREET));
        assertThat(addressPropertyMap, hasEntry("zipCode", ZIP_CODE));
        assertThat(addressPropertyMap, hasEntry("city", CITY));


    }

    private static Map<String, Object> getPropertyMap(final Object instance) {
        final Map<String, Object> propertyMap = new TreeMap<>();
        try {
            Arrays.stream(Introspector.getBeanInfo(instance.getClass(), Object.class)
                                      .getPropertyDescriptors())
                  .filter((it) -> it.getReadMethod() != null)
                  .forEach((propertyDescriptor) -> {
                      final Method method = propertyDescriptor.getReadMethod();
                      try {
                          final Object result = method.invoke(instance);
                          propertyMap.put(propertyDescriptor.getName(), result);
                      } catch (IllegalAccessException | InvocationTargetException e) {
                          throw new IllegalStateException(e);
                      }
                  });
        } catch (IntrospectionException e) {
            throw new IllegalStateException(e);
        }
        return propertyMap;
    }

    protected abstract Object createUser();
}
