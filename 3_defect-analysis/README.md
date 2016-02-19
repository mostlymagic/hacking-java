Defect Analysis
===============

Here we'll go into different techniques of resolving bugs or policy violations at compile time.

Traditionally, this has been achieved through separate runs of static analysis on source code or byte code basis,
using tools like CheckStyle, PMD, FindBugs etc. While these tools are still valid and necessary, I'll focus on tools
that plug into the compile lifecycle.

The current selection of frameworks is:

- [ErrorProne](1_errorprone) is a library created by Google that wraps the Javac compiler and rejects well known
  bug patterns at compile time.
- The [CheckerFramework](2_checker-framework) is a library that embraces Java 8 annotations and offers many annotation-
  based matchers that can also reject bugs at compile time.
- [AspectJ](3_aspectj) can be used as a Policy Enforcement framework, allowing you to create custom compiler errors
  based on class or package architecture rules
  
Helper Projects:
  
- [Base Line](x_baseline) contains classes or interfaces that can be reused in the individual projects
- [Tests](x_tests) contains helper classes for testing the individual techniques
  