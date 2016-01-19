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
            n.body = new BlockStmt() // delete existing method body
            patchStringMethod(n.body, n.parameters[0], n.parameters[1])
        } else if (n.name == "yUNoReuseInteger") {
            n.body = new BlockStmt() // delete existing method body
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

    private patchStringMethod(BlockStmt blockStatement, Parameter iterable, Parameter delim) {

        def sbType = new ClassOrInterfaceType("StringBuilder")
        def sbId = new VariableDeclaratorId("sb")
        def sbDecl = new VariableDeclarationExpr(sbType, [
                new VariableDeclarator(sbId, new ObjectCreationExpr(null, sbType, []))
        ])

        def itType = new ClassOrInterfaceType("Iterator<String>")
        def itCall = new MethodCallExpr(new NameExpr(iterable.id.name), "iterator")
        def itId = new VariableDeclaratorId("iterator")
        def itDecl = new VariableDeclarationExpr(itType, [new VariableDeclarator(itId, itCall)])


        def itExpr = new NameExpr(itId.name)
        def sbExpr = new NameExpr(sbId.name)

        blockStatement.stmts.addAll([
                new ExpressionStmt(sbDecl),
                new ExpressionStmt(itDecl),
                new IfStmt(
                        new MethodCallExpr(itExpr, "hasNext"),
                        new BlockStmt([
                                new ExpressionStmt(new MethodCallExpr(sbExpr, "append", [new MethodCallExpr(itExpr, "next")])),
                                new WhileStmt(new MethodCallExpr(itExpr, "hasNext"), new BlockStmt([
                                        new ExpressionStmt(new MethodCallExpr(
                                                new MethodCallExpr(sbExpr, "append", [new NameExpr(delim.id.name)]),
                                                "append", [new MethodCallExpr(itExpr, "next")]
                                        ))
                                ]))
                        ]),
                        null  // <-- no else block
                ),
                new ReturnStmt(new MethodCallExpr(sbExpr, "toString"))
        ])

    }

}