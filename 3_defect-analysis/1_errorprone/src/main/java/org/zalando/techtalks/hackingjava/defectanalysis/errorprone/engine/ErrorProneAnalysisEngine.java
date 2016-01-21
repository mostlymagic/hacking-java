package org.zalando.techtalks.hackingjava.defectanalysis.errorprone.engine;

import java.io.File;

import java.net.URISyntaxException;

import java.util.Set;

import org.zalando.techtalks.hackingjava.common.compiler.CompilationResult;
import org.zalando.techtalks.hackingjava.common.compiler.ForkedRun;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.engine.DefectAnalysisEngine;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.immutable.AbstractUser;

import com.google.common.base.Joiner;

import com.google.errorprone.ErrorProneCompiler;

public class ErrorProneAnalysisEngine implements DefectAnalysisEngine {

    private final Set<String> activatedChecks;

    public ErrorProneAnalysisEngine(final Set<String> activatedChecks) {
        this.activatedChecks = activatedChecks;
    }

    static String jarPathFromClass(final Class<?> referenceClass) {
        try {
            return new File(referenceClass.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public CompilationResult compile(final File sourceFile) {
        return new ForkedRun(ErrorProneAnalysisEngine.class).withAdditionalClassLoaderFromClass(AbstractUser.class)
                                                            .withBootClassPathMatcher("com", "google", "errorprone")
                                                            .withBootClassPathMatcher("org", "checkerframework")
                                                            .withArg(ErrorProneCompiler.class)
                                                            .withArg("-Xep:" + Joiner.on(',').join(activatedChecks))
                                                            .withArg(sourceFile.getAbsolutePath()).run();

    }

}
