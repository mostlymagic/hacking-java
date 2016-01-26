# hacking-java

This is the code base for the conference talk "Hacking Java" by Sean Patrick Floyd

The Talk deals with different topics where Java doesn't provide any satifactory mechanism out of the box.
Each section then highlights a few different technologies that offer solutions for these problems.

Currently, the sections are:

- [Value Objects](1_valueobjects) (Creating value objects in Java is a very tedious and error-prone manual process)
- [Patching a third party library](2_patching) in an automated way
- [Build-time defect analysis](3_defect-analysis)

This project contains generated code in some modules. If your IDE reports compilation errors, run `mvn clean install`
from the command line and then re-import or refresh the project in your IDE.

