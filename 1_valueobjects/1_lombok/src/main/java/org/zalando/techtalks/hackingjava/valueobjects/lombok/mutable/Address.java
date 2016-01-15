package org.zalando.techtalks.hackingjava.valueobjects.lombok.mutable;

import lombok.Data;

@Data
public class Address {
    private String street;
    private int zipCode;
    private String city;
}
