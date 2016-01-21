package org.zalando.techtalks.hackingjava.patching.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zalando.techtalks.hackingjava.patching.baseline.FicticiousExample;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

public abstract class FicticiousExamplePatchTest {

    private FicticiousExample fe;

    @After
    public void checkForEvilSystemProperty() throws Exception {
        assertThat(System.getProperty(FicticiousExample.RAN_BUGGY_CODE), is(nullValue()));
    }

    @Before
    public void initObject() throws Exception {
        fe = new FicticiousExample();
    }

    @Test
    public void assertCorrectIntegerBehavior() {
        assertThat(fe.yUNoReuseInteger(123), is(123));
        assertThat(fe.yUNoReuseInteger(123), is(sameInstance(123)));
        assertThat(fe.yUNoReuseInteger(12345678), is(not(sameInstance(12345678))));
    }

    @Test
    public void assertCorrectStringBehavior() {
        assertThat(fe.yUStringConcatInLoop(asList("foo", "bar", "baz"), ", "), is("foo, bar, baz"));
    }

}
