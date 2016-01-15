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

Our goal will be to create the value objects as immutables with Builders.
Ideally, our value objects will implement Comparable, using lastName, firstName and birthDate (all descending)