package org.zalando.techtalks.hackingjava.valueobjects.lombok.builder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {
    private final String street;
    private final int zipCode;
    private final String city;
}
