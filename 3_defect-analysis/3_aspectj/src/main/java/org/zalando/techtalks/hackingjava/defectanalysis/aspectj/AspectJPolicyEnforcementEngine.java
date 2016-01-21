package org.zalando.techtalks.hackingjava.defectanalysis.aspectj;

import java.io.File;

import org.aspectj.tools.ajc.Main;

import org.zalando.techtalks.hackingjava.common.compiler.CompilationResult;
import org.zalando.techtalks.hackingjava.common.compiler.ForkedRun;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.engine.DefectAnalysisEngine;

/**
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since   21.01.2016
 */
public class AspectJPolicyEnforcementEngine implements DefectAnalysisEngine {
    @Override
    public CompilationResult compile(final File sourceFile) {
        final File aspectFile = findAspectFile(sourceFile);
        return new ForkedRun(AspectJPolicyEnforcementEngine.class).withArg(Main.class).withArg("-1.8")
                                                                  .withArg("-target").withArg("1.8")
                                                                  .withArg(sourceFile.getAbsolutePath())
                                                                  .withArg(aspectFile.getAbsolutePath()).run();
    }

    private File findAspectFile(final File sourceFile) {
        final String correspondingAspectDir = sourceFile.getParentFile().getAbsolutePath().replaceFirst(
                "^(.*src.main.)java", "$1aspect");
        return new File(correspondingAspectDir, "PolicyEnforcementAspect.aj");
    }
}
