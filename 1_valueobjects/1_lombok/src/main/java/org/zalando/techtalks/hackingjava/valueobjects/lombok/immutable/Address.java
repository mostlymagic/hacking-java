package org.zalando.techtalks.hackingjava.valueobjects.lombok.immutable;

import lombok.Data;

@Data
public class Address {
    private final String street;
    private final int zipCode;
    private final String city;
}
