Value Objects
-------------

In this chapter we'll deal with the different ways of creating value objects.

This will be our reference data structure:


    User
      - firstName (String)
      - lastName  (String)
      - birthDate (LocalDate)
      - addresses (List[Address])
    
    Address
      - street    (String)
      - zipCode   (int)
      - city     (String)

We will be to create these value objects in three flavors: mutable, immutable and immutable with Builder.
Every solution will have to implement equals(), hashCode(), toString(), getters and (for mutable classes) setters.
Ideally, our value objects will implement Comparable, using lastName, firstName and birthDate (all descending).

These are the approaches I will present:

- [Project Lombok](1_lombok), annotation processing using internal compiler APIs. Powerful, but hacky.
- [Google AutoValue](2_autovalue), annotation processing done "correctly", using abstract classes and
  generated sub-classes. Less powerful, but uses official APIs only.
- [CGLib BeanGenerator](3_cglib), dynamically creating classes at runtime through byte code generation.
  Extremely hacky!
- [JCodeModel](4_jcodemodel), a code generation library which I will use for code generation at build time.

- [Test Suite](x_tests), a common test suite that all above technologies will have to pass