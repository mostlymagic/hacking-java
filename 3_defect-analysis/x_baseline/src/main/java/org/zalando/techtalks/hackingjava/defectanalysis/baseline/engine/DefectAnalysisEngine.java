package org.zalando.techtalks.hackingjava.defectanalysis.baseline.engine;

import java.io.File;
import org.zalando.techtalks.hackingjava.common.compiler.CompilationResult;

public interface DefectAnalysisEngine {
    CompilationResult compile(File sourceFile);
}
