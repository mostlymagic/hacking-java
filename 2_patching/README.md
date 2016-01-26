Patching a 3rd party library
============================

This may seem like an artificial use case, but unfortunately it's pretty common: For whatever reason, you are forced
to use a third party library that has known defects. The library devs do not fix the issues or integrate your pull
request.

Or perhaps it's a long-requested feature they don't want to integrate, but that's essential to you.

Anyway, the scenario here is this:

You want to be able to do an automated deployment of the library, with your fix incorporated.
To achieve that, you can work on the source code, or on the byte code.

- [AspectJ](1_aspectj) allows you to recompile Java classes and extend their behavior in an aspect-oriented way. This
  may be very useful for recurring code patterns
- [JavaParser](2_javaparser) is a parser with full support for Java 8 syntax and AST modification capabilites.

Most important for this use case:

- [The Baseline](x_baseline), which will stand in for the 3rd party lib that needs fixing
- [A Test suite](x_tests), which will work against the fixed lib and assert the defects are gone