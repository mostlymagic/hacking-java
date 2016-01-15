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
            e ->
                def method = clazz.method(JMod.PUBLIC, resolveType(e.value), "get" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, e.key))
                method.body()._return(THIS.ref(e.key))
        }
    }

    private defineConstructor(JDefinedClass clazz, Map fields) {
        def constructor = clazz.constructor(JMod.PUBLIC)
        def body = constructor.body()
        fields.each {
            e ->
                def type = resolveType(e.value)
                def field = clazz.field(JMod.PRIVATE | JMod.FINAL, type, e.key)
                def param = constructor.param(type, e.key)
                param.mods().setFinal(true)
                body.assign(THIS.ref(field), param)
        }

    }

    private defineCompareToMethod(JDefinedClass clazz, List fields) {
        if (fields.empty) return
        clazz._implements(codeModel.ref(Comparable.class).narrow(clazz))

        def compareToMethod = clazz.method(JMod.PUBLIC, int.class, "compareTo")
        compareToMethod.annotate(Override.class)

        def other = compareToMethod.param(clazz, "other")
        other.mods().setFinal(true)

        def body = compareToMethod.body()

        def invocation = codeModel.ref(ComparisonChain.class).staticInvoke("start")
        def currentInvocation = invocation
        fields.each {
            currentInvocation = currentInvocation.invoke("compare").arg(THIS.ref(it)).arg(other.ref(it))
        }
        body._return(currentInvocation.invoke("result"))

    }

    private defineToStringMethod(JDefinedClass clazz, Map fields) {
        def method = clazz.method(JMod.PUBLIC, String.class, "toString")
        method.annotate(Override.class)
        def toStringBody = method.body()

        def invocation = codeModel.ref(MoreObjects.class).staticInvoke("toStringHelper").arg(THIS)
        def currentInvocation = invocation
        fields.each { e -> currentInvocation = currentInvocation.invoke("add").arg(JExpr.lit(e.key)).arg(THIS.ref(e.key)) }
        toStringBody._return(currentInvocation.invoke("toString"))

    }

    private defineHashCodeMethod(JDefinedClass clazz, Map fields) {
        def method = clazz.method(JMod.PUBLIC, int.class, "hashCode")
        method.annotate(Override.class)
        def hashCodeBody = method.body()

        def invocation = codeModel.ref(Objects.class).staticInvoke("hash")
        fields.keySet().each { invocation.arg(THIS.ref(it)) }
        hashCodeBody._return(invocation)
    }

    private defineEqualsMethod(JDefinedClass clazz, Map fields) {
        def equalsMethod = clazz.method(JMod.PUBLIC, boolean.class, "equals")
        equalsMethod.annotate(Override.class)
        def equalsParam = equalsMethod.param(Object.class, "o")
        def equalsBody = equalsMethod.body()
        def firstIf = equalsBody._if(JOp.eq(THIS, equalsParam))
        firstIf._then()._return(JExpr.lit(true))
        def elseBlock = firstIf._else()
        def conditional = elseBlock._if(equalsParam._instanceof(clazz))
        def thenBlock = conditional._then()
        def other = thenBlock.decl(clazz, "other", JExpr.cast(clazz, equalsParam))
        def iterator = fields.keySet().iterator()
        def result = JExpr.lit(true)
        if (iterator.hasNext()) {
            def f = iterator.next()
            result = codeModel.ref(Objects.class).staticInvoke("equals").arg(THIS.ref(f)).arg(other.ref(f))
            while (iterator.hasNext()) {
                f = iterator.next()
                result = JOp.cand(result, codeModel.ref(Objects.class).staticInvoke("equals").arg(THIS.ref(f)).arg(other.ref(f)))
            }
        }
        thenBlock._return(result)
        conditional._else()._return(JExpr.lit(false))
    }
}