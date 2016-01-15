import Dto
import DtoGenerator

class Wrapper {

    def STRING = "java.lang.String"

    def pkg = "org.zalando.techtalks.hackingjava.valueobjects.jcodemodel"

    public exec(project) {
        def addressDto = new Dto(
                packageName: pkg,
                className: "Address",
                properties: [
                        "street" : STRING,
                        "zipCode": "int",
                        "city"   : STRING
                ]
        )
        def userDto = new Dto(
                packageName: pkg,
                className: "User",
                properties: [
                        "firstName": STRING,
                        "lastName" : STRING,
                        "birthDate": "java.time.LocalDate",
                        "addresses": new GenericType(baseType: "java.util.List", params: ["${pkg}.Address"])
                ],
                comparableProperties: ["lastName", "firstName", "birthDate"]
        )

        def output = new File(project.build.directory, "generated-sources/groovy")


        def dtos = [userDto, addressDto] as List
        new DtoGenerator().generate(dtos, output)
        project.compileSourceRoots.add(output.absolutePath);
    }
}