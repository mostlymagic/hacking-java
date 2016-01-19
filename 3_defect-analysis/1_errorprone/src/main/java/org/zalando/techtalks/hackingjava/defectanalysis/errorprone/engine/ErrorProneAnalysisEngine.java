package org.zalando.techtalks.hackingjava.defectanalysis.errorprone.engine;

import java.io.File;

import java.util.Collections;

import org.zalando.techtalks.hackingjava.defectanalysis.baseline.engine.CompilationResult;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.engine.DefectAnalysisEngine;

import com.google.errorprone.ErrorProneCompiler;

import com.sun.tools.javac.main.Main;

/**
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since   19.01.2016
 */
public class ErrorProneAnalysisEngine implements DefectAnalysisEngine {
    @Override
    public CompilationResult compile(final File sourceFile) {
        final Main.Result result = ErrorProneCompiler.compile(new String[] {sourceFile.getAbsolutePath()});
        final boolean status = result.isOK();

        return new CompilationResult(status, Collections.<String>emptyList(), Collections.<String>emptyList());
    }
}
