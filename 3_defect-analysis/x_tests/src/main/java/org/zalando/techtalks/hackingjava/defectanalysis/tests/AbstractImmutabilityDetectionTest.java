package org.zalando.techtalks.hackingjava.defectanalysis.tests;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

import org.junit.Test;

import org.zalando.techtalks.hackingjava.defectanalysis.baseline.engine.CompilationResult;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.immutable.AbstractFakeImmutableUser;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.immutable.AbstractProperImmutableUser;

/**
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since   19.01.2016
 */
public abstract class AbstractImmutabilityDetectionTest extends AbstractCompilerTest {

    protected abstract Class<? extends AbstractProperImmutableUser> properImmutableClass();

    protected abstract Class<? extends AbstractFakeImmutableUser> fakeImmutableClass();

    @Test
    public void detectProperImmutableClass() {
        final CompilationResult result = engine.compile(sourceFileFor(properImmutableClass()));
        assertThat(result.isSuccess(), is(true));
    }

    @Test
    public void detectFakeImmutableClass() {
        final CompilationResult result = engine.compile(sourceFileFor(properImmutableClass()));
        assertThat(result.isSuccess(), is(false));
    }

}
