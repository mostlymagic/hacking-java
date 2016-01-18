import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.body.VariableDeclaratorId
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.stmt.*
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.Type
import com.github.javaparser.ast.visitor.VoidVisitorAdapter

public class Wrapper {

    public exec(project, baseDir) {
        def sourceFile = new File(baseDir, "org/zalando/techtalks/hackingjava/patching/baseline/FicticiousExample.java")
        def compilationUnit = JavaParser.parse(sourceFile)
        applyPatch(compilationUnit)
        sourceFile.text = compilationUnit.toString()
        project.compileSourceRoots.add(baseDir);
    }


    private applyPatch(CompilationUnit compilationUnit) {

        compilationUnit.accept(new PatchVisitor(), null)
    }

}

class PatchVisitor extends VoidVisitorAdapter<Void> {

    public void visit(final MethodDeclaration n, final Object arg) {
        if (n.name == "yUStringConcatInLoop") {
            n.body = new BlockStmt()
            patchStringMethod(n.body, n.parameters[0], n.parameters[1])
        } else if (n.name == "yUNoReuseInteger") {
            n.body = new BlockStmt()
            patchIntegerMethod(n.body, n.parameters[0])
        } else super.visit(n, arg)
    }

    private patchIntegerMethod(BlockStmt blockStatement, Parameter parameter) {

        Type type = new ClassOrInterfaceType("Integer")
        def typeExpr = new TypeExpr()
        typeExpr.type = type
        MethodCallExpr methodCall = new MethodCallExpr(typeExpr, "valueOf")
        methodCall.args.add(new NameExpr(parameter.id.name))

        blockStatement.getStmts().add(new ReturnStmt(methodCall))
    }

    private patchStringMethod(BlockStmt blockStatement, Parameter iterableParameter, Parameter delimParameter) {

        def stringBuilderType = new ClassOrInterfaceType("StringBuilder")
        def sbId = new VariableDeclaratorId("sb")
        def stringBuilderDeclaration = new VariableDeclarationExpr(stringBuilderType, [
                new VariableDeclarator(sbId, new ObjectCreationExpr(null, stringBuilderType, []))
        ])

        def iteratorType = new ClassOrInterfaceType("Iterator<String>")
        def iterableIteratorCall = new MethodCallExpr(new NameExpr(iterableParameter.id.name), "iterator")
        def iteratorId = new VariableDeclaratorId("iterator")
        def iteratorDeclaration = new VariableDeclarationExpr(iteratorType, [new VariableDeclarator(iteratorId, iterableIteratorCall)])


        def iteratorExpr = new NameExpr(iteratorId.name)
        def sbExpr = new NameExpr(sbId.name)

        blockStatement.stmts.addAll([
                new ExpressionStmt(stringBuilderDeclaration),
                new ExpressionStmt(iteratorDeclaration),
                new IfStmt(
                        new MethodCallExpr(iteratorExpr, "hasNext"),
                        new BlockStmt([
                                new ExpressionStmt(new MethodCallExpr(sbExpr, "append", [new MethodCallExpr(iteratorExpr, "next")])),
                                new WhileStmt(new MethodCallExpr(iteratorExpr, "hasNext"), new BlockStmt([
                                        new ExpressionStmt(new MethodCallExpr(
                                                new MethodCallExpr(sbExpr, "append", [new NameExpr(delimParameter.id.name)]),
                                                "append", [new MethodCallExpr(iteratorExpr, "next")]
                                        ))

                                ]))
                        ]),
                        null
                ),
                new ReturnStmt(new MethodCallExpr(sbExpr, "toString"))
        ])

    }

    private deleteChildren(BlockStmt blockStatement) {
        new ArrayList(blockStatement.childrenNodes).each {
            it -> it.parentNode = null
        }
    }

}