JCodeModel
==========

[JCodeModel](https://github.com/phax/jcodemodel) is an open source fork of Sun's defunct
[JCodeModel](https://codemodel.java.net/) project, which was originally created as a code generator library for JAXB.
The fork updates the grammar to Java 8.

I am using a groovy script to generate the code, because it's easiest to add groovy code to the Maven build process
(no custom plugin is needed). By doing Code Generation from scratch, we can fine-tune the generated code to our exact
requirements (use Java 7+ or Guava in equals/hashCode?, make defensive copies of mutable fields? use getClass() or
instanceof in equals(), a common superclass etc.)

This is a low-tech alternative to annotation based solutions, but it relies on mechanisms outside of
javac, so your IDE needs to understand the build process (in this case Maven, though similar setups could easily be
created for others).

I have used this approach in several projects and would recommend it if you have custom requirements regarding your
value objects.

A generated Value Object can look like this:

    public class Address {
        private final String street;
        private final int zipCode;
        private final String city;
    
        public Address(final String street, final int zipCode, final String city) {
            this.street = street;
            this.zipCode = zipCode;
            this.city = city;
        }
    
        public String getStreet() {
            return this.street;
        }
    
        public int getZipCode() {
            return this.zipCode;
        }
    
        public String getCity() {
            return this.city;
        }
    
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else {
                if (o instanceof Address) {
                    Address other = ((Address) o);
                    return ((Objects.equals(this.street, other.street)
                            && Objects.equals(this.zipCode, other.zipCode))
                            && Objects.equals(this.city, other.city));
                } else {
                    return false;
                }
            }
        }
    
        @Override
        public int hashCode() {
            return Objects.hash(this.street, this.zipCode, this.city);
        }
    
        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                              .add("street", this.street)
                              .add("zipCode", this.zipCode)
                              .add("city", this.city).toString();
        }
    }

or it can have any other style. Note that the formatting capabilities of JCodeModel are not that advanced.
Perfectionists may want to run a separate Code Formatter tool on the generated sources.