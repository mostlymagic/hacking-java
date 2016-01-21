package org.zalando.techtalks.hackingjava.valueobjects.autovalue.plain;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Address {

    public abstract String getStreet();

    public abstract int getZipCode();

    public abstract String getCity();

    public static Address create(final String street, final int zipCode, final String city) {
        return new AutoValue_Address(street, zipCode, city);
    }
}
