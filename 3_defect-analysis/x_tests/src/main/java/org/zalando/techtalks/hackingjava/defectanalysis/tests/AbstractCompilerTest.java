package org.zalando.techtalks.hackingjava.defectanalysis.tests;

import org.junit.Before;

import org.zalando.techtalks.hackingjava.defectanalysis.baseline.engine.DefectAnalysisEngine;

/**
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since   19.01.2016
 */
public abstract class AbstractCompilerTest extends CompilationHelper {

    DefectAnalysisEngine engine;

    protected abstract DefectAnalysisEngine instantiateEngine();

    @Before
    public void setupEngine() {
        this.engine = instantiateEngine();
    }
}
