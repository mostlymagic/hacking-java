package org.zalando.techtalks.hackingjava.defectanalysis.tests;

import java.io.File;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class CompilationHelperTest {

    @Test
    public void resolveSourceFile() {
        assertThat(new CompilationHelper().sourceFileFor(CompilationHelper.class), isFile());
    }

    private Matcher<File> isFile() {
        return new TypeSafeMatcher<File>() {
            @Override
            protected boolean matchesSafely(final File item) {
                return item.isFile();
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("an existing file");
            }
        };
    }

}
