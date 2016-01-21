package org.zalando.techtalks.hackingjava.defectanalysis.checkerframework.engine;

import java.io.File;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.zalando.techtalks.hackingjava.common.compiler.CompilationResult;
import org.zalando.techtalks.hackingjava.common.compiler.ForkedRun;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.engine.DefectAnalysisEngine;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.immutable.AbstractUser;

public class CheckerFrameworkAnalysisEngine implements DefectAnalysisEngine {

    private final Class<? extends BaseTypeChecker> checkerClass;

    public CheckerFrameworkAnalysisEngine(final Class<? extends BaseTypeChecker> checkerClass) {
        this.checkerClass = checkerClass;
    }

    @Override public CompilationResult compile(final File sourceFile) {
        return new ForkedRun(CheckerFrameworkAnalysisEngine.class)
                .withAdditionalClassLoaderFromClass(AbstractUser.class)
                .withJavaC()
                .withArg("-d").tempDirAsArg()
                .withArg("-processor").withArg(checkerClass)
                .withArg(sourceFile.getAbsolutePath())
                .run();
    }
}
