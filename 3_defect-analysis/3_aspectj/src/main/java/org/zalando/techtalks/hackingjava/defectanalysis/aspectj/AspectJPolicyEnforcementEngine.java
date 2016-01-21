package org.zalando.techtalks.hackingjava.defectanalysis.aspectj;

import java.io.File;
import org.aspectj.tools.ajc.Main;
import org.zalando.techtalks.hackingjava.common.compiler.CompilationResult;
import org.zalando.techtalks.hackingjava.common.compiler.ForkedRun;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.engine.DefectAnalysisEngine;

import static java.lang.String.format;

public class AspectJPolicyEnforcementEngine implements DefectAnalysisEngine {

    private static final String COMPLIANCE_LEVEL = "1.8";

    @Override
    public CompilationResult compile(final File sourceFile) {
        final File aspectFile = findAspectFile(sourceFile);
        return new ForkedRun(AspectJPolicyEnforcementEngine.class)
                .withArg(Main.class)
                .withArg(format("-%s", COMPLIANCE_LEVEL))
                .withArg("-target").withArg(COMPLIANCE_LEVEL)
                .withArg("-d").tempDirAsArg()
                .withArg(sourceFile.getAbsolutePath())
                .withArg(aspectFile.getAbsolutePath())
                .run();
    }

    private File findAspectFile(final File sourceFile) {
        final String correspondingAspectDir = sourceFile.getParentFile().getAbsolutePath().replaceFirst(
                "^(.*src.main.)java", "$1aspect");
        return new File(correspondingAspectDir, "PolicyEnforcementAspect.aj");
    }
}
