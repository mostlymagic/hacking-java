Google AutoValue
================

[Google AutoValue](https://github.com/google/auto/tree/master/value) is another library that provides
[JSR 269](https://www.jcp.org/en/jsr/detail?id=269)-style Annotation Processors for creating value objects.
It relies on the Value Objects classes being abstract and will generate subclasses of these abstract classes, with a
well-documented naming convention.


Advantages:
-----------

You can write classes in a clean and simple way, e.g.

    @AutoValue
    public abstract class User {
        public abstract String getFirstName();
    
        public abstract String getLastName();
    
        public abstract LocalDate getBirthDate();
    
        public static User create(String firstName, String lastName, LocalDate birthDate) {
            return new AutoValue_User(firstName, lastName, birthDate);
        }
    }

AutoValue will generate equals() / hashCode() / toString() / getters for you in the generated subclass.

Disadvantages:
--------------

AutoValue only supports immutable classes, generated through either builders or factory methods (I'd actually call this
an advantage :-) ).

You have to manually create factory methods in the abstract class.