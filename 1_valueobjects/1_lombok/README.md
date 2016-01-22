Project Lombok
===============

[Project Lombok](https://projectlombok.org/) is a library that provides [JSR 269](https://www.jcp.org/en/jsr/detail?id=269)-style
Annotation Processors for creating value objects. However, it is mis-using (<-- flame-bait) the API by casting the read
only AST interfaces to the implementation types used in the compiler and adding code to the AST.

Advantages:
-----------

You can write classes in a very clean and simple way, e.g.

    @Data
    public class User {
        private String firstName;
        private String lastName;
        private LocalDate birthDate;
    }

Lombok will generate equals() / hashCode() / toString() / getters / setters for you.
If you mark the fields as final, it will generate an all-args constructor instead of the setters.
There are also several more annotations that can be used to extend or fine-tune the behavior.

Disadvantages:
--------------

Since Lombok extends the AST on the fly, it will generate byte code that doesn't correspond to source code.
This can be a nuissance when debugging. Also, Lombok codes against internal undocumented APIs, which could break any moment.
Lombok has to implement every feature separately for JavaC, Eclipse and (through a plugin) IntelliJ, since all of these use
different AST implementations.