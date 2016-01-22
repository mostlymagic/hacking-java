Value Objects Test Suite
========================

This is a test suite that is used by all the different value object implementations.
Since it doesn't know name or nature of the individual classes, the tests have to work on a very abstract level.

equals(), hashCode() and toString() are tested by instantiating the same object twice, using constants provided in the
test base class, and then running assertions based on the expected behavior.

Getters are tested by using the JavaBeans [Introspector](https://docs.oracle.com/javase/8/docs/api/java/beans/Introspector.html)
mechanism, creating a Map of bean properties and making assertions based on that map.