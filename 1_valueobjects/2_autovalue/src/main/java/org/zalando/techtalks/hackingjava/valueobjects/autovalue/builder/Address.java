package org.zalando.techtalks.hackingjava.valueobjects.autovalue.builder;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Address {

    public abstract String getStreet();

    public abstract int getZipCode();

    public abstract String getCity();

    public static Builder addressBuilder() {
        return new AutoValue_Address.Builder();
    }

    @AutoValue.Builder public interface Builder {
        Builder street(String street);

        Builder zipCode(int zipCode);

        Builder city(String city);

        Address build();
    }
}
