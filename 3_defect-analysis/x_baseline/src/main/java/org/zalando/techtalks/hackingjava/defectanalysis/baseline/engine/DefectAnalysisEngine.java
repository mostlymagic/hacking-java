package org.zalando.techtalks.hackingjava.defectanalysis.baseline.engine;

import java.io.File;

public interface DefectAnalysisEngine {
    CompilationResult compile(File sourceFile);
}
