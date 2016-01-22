CGLib BeanGenerator
===================


[CGLib](https://github.com/cglib/cglib) is a high level byte code manipulation library based on [ASM](http://asm.ow2.org/).
CGLib provides utilities for dynamic byte code generation. I'll be using it to generate Value Object types on the fly at
runtime, using a combination of the BeanGenerator mechanism, which generates bean properties, and a dynamic proxy for
providing equals() / hashCode() and toString(), since BeanGenerator doesn't offer this functionality.

The DtoFactory class provides a factory method for creating Value types:

    static Class<?> createBeanClass(String className, Map<String, Class<?>> properties)

This is a technology demonstration, not a recommendation. There may be scenarios where this may be useful in practice,
e.g. a Content Management System which lets you add new types at runtime and is backed by an ORM could be such a scenario.
But in general I'd be inclined to say: Don't try this at home.