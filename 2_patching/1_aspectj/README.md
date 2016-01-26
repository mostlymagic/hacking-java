AspectJ
=======

[AspectJ](http://www.eclipse.org/aspectj/) is an aspect-oriented extension language for Java that has been around since
2001. Originally developed by [PARC](https://en.wikipedia.org/wiki/PARC_%28company%29), it is now a part of the
[Eclipse foundation](https://www.eclipse.org/).

> AspectJ can be implemented in many ways, including source-weaving or bytecode-weaving,
> and directly in the virtual machine (VM). In all cases, the AspectJ program becomes a valid
> Java program that runs in a Java VM. Classes affected by aspects are binary-compatible with
> unaffected classes (to remain compatible with classes compiled with the unaffected originals).
> Supporting multiple implementations allows the language to grow as technology changes,
> and being Java-compatible ensures platform availability.

Source: [Wikipedia](https://en.wikipedia.org/wiki/AspectJ)
 
AspectJ aspects usually have to parts: pointcuts and advices
A pointcut is a selector that matches certain behavior, e.g. access to a field with a given name pattern
An advice is code that will be applied to that selector, enhancing or replacing the existing behavior.
The resulting byte code is indistinguishable from plain Java, but it has a runtime dependency to the AspectJ
Runtime Library.

AspectJ comes in two flavors:

- The traditional approach uses .aj files with pointcut and advice signatures in AspectJ's proprietary language,
whereas advice bodies are written in Java. I am using this flavor in the example, to highlight the difference between
original Java and AspectJ enrichment.

- The @AspectJ approach defines both pointcuts and aspects in Java code with annotations, which contain the AspectJ
selector expressions. This syntax is also used by Spring AOP, which implements Aspects at runtime through proxying
instead of byte code modification at compile time. 