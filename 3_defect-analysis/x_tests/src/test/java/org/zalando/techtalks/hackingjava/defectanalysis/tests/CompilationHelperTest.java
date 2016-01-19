package org.zalando.techtalks.hackingjava.defectanalysis.tests;

import static org.junit.Assert.assertThat;

import java.io.File;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import org.junit.Test;

/**
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since   19.01.2016
 */
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
