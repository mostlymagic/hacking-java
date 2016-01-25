import com.google.common.base.CaseFormat
import com.google.common.base.MoreObjects
import com.google.common.collect.ComparisonChain
import com.helger.jcodemodel.*

class DtoGenerator {
    def THIS = JExpr._this()
    def codeModel = new JCodeModel()

    public generate(List<Dto> dtos, File targetDir) {
        dtos.each {
            def clazz = codeModel._package(it.packageName)._class(JMod.PUBLIC, it.className)
            defineConstructor(clazz, it.properties)
            defineGetters(clazz, it.properties)
            defineEqualsMethod(clazz, it.properties)
            defineHashCodeMethod(clazz, it.properties)
            defineToStringMethod(clazz, it.properties)
            defineCompareToMethod(clazz, it.comparableProperties)
        }
        targetDir.mkdirs()
        codeModel.build(targetDir)
    }

    private AbstractJClass resolveGenericType(GenericType type) {
        def baseType = codeModel.ref(type.baseType)

        def params = type.params.collect { resolveType(it) }
        return baseType.narrow(
                params as AbstractJClass[]
        )
    }


    private AbstractJType resolveType(type) {
        if (GenericType.class.isInstance(type)) return resolveGenericType(type)
        else return codeModel.ref(type)

    }

    private defineGetters(JDefinedClass clazz, Map fields) {
        fields.each {
            entry ->
                def method = clazz.method(JMod.PUBLIC, resolveType(entry.value),      // public [type] get[Property]
                        entry.value == 'boolean' ? 'is' : 'get'
                                + CaseFormat.LOWER_CAMEL.to(
                                CaseFormat.UPPER_CAMEL, entry.key))
                method.body()._return(THIS.ref(entry.key))                            // return [property]
        }
    }

    private defineConstructor(JDefinedClass clazz, Map fields) {
        def constructor = clazz.constructor(JMod.PUBLIC)                              // public [ClassName]()
        def body = constructor.body()
        fields.each {
            entry ->
                def type = resolveType(entry.value)
                def field = clazz.field(JMod.PRIVATE | JMod.FINAL, type, entry.key)   // private final [Type] [field];
                def param = constructor.param(type, entry.key)                        // final [Type] [param] 
                param.mods().setFinal(true)
                body.assign(THIS.ref(field), param)                                   // this.[field] = [param]
        }

    }

    private defineCompareToMethod(JDefinedClass clazz, List fields) {
        if (fields.empty) return
        clazz._implements(codeModel.ref(Comparable.class).narrow(clazz))

        def compareToMethod = clazz.method(JMod.PUBLIC, int.class, "compareTo") // @Override public int compareTo
        compareToMethod.annotate(Override.class)

        def other = compareToMethod.param(clazz, "other")                       // (final [ClassName] other)
        other.mods().setFinal(true)

        def body = compareToMethod.body()

        def invocation = codeModel.ref(ComparisonChain.class)                   // ComparisonChain.start()
                .staticInvoke("start")
        def currentInvocation = invocation
        fields.each {
            currentInvocation = currentInvocation.invoke("compare")             // .add(this.property1, other.property1)
                    .arg(THIS.ref(it))
                    .arg(other.ref(it))
        }
        body._return(currentInvocation.invoke("result"))                        // return [invocation].result()

    }

    private defineToStringMethod(JDefinedClass clazz, Map fields) {
        def method = clazz.method(JMod.PUBLIC, String.class, "toString")   // @Override public String toString()
        method.annotate(Override.class)
        def toStringBody = method.body()

        def invocation = codeModel.ref(MoreObjects.class)                  // MoreObjects.toStringHelper(this)
                .staticInvoke("toStringHelper")
                .arg(THIS)
        def current = invocation
        fields.each { entry ->
            current = current.invoke("add")                                // .add("property1", this.property1)
                    .arg(JExpr.lit(entry.key))
                    .arg(THIS.ref(entry.key))
        }
        toStringBody._return(current.invoke("toString"))                   // return [invocation].toString()

    }

    private defineHashCodeMethod(JDefinedClass clazz, Map fields) {
        def method = clazz.method(JMod.PUBLIC, int.class, "hashCode")      // @Override public int hashCode()
        method.annotate(Override.class)
        def hashCodeBody = method.body()

        def invocation = codeModel.ref(Objects.class).staticInvoke("hash") // return Objects.hash(this.property1, ...)
        fields.keySet().each { invocation.arg(THIS.ref(it)) }
        hashCodeBody._return(invocation)
    }

    private defineEqualsMethod(JDefinedClass clazz, Map fields) {

        def equalsMethod = clazz.method(
                JMod.PUBLIC, boolean.class, "equals")
        equalsMethod.annotate(Override.class)
        def eqParam = equalsMethod.param(Object.class, "o")                // @Override public boolean equals(Object o) 
        def equalsBody = equalsMethod.body()

        def firstIf = equalsBody._if(JOp.eq(THIS, eqParam))
        firstIf._then()._return(JExpr.lit(true))                           // if(o == this) return true
        def elseBlock = firstIf._else()
        def conditional = elseBlock._if(eqParam._instanceof(clazz))        // else if (o instanceof [ClassName])
        def then = conditional._then()
        def other = then.decl(clazz, "other", JExpr.cast(clazz, eqParam))  // other = ([ClassName]) o
        def iterator = fields.keySet().iterator()
        def result = JExpr.lit(true)
        if (iterator.hasNext()) {
            def f = iterator.next()
            result = codeModel.ref(Objects.class)                          // Objects.equals(this.foo, other.foo)
                    .staticInvoke("equals")
                    .arg(THIS.ref(f)).arg(other.ref(f))
            while (iterator.hasNext()) {
                f = iterator.next()
                result = JOp.cand(result,
                        codeModel.ref(Objects.class)                       // && Objects.equals(this.bar, other.bar)
                                .staticInvoke("equals")
                                .arg(THIS.ref(f)).arg(other.ref(f)))
            }
        }
        then._return(result)
        conditional._else()._return(JExpr.lit(false))                      // else return false
    }
}