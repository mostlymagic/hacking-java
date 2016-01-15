package org.zalando.techtalks.hackingjava.compilertesting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since   08.07.2015
 */
public final class CompilerMatchers {

    private CompilerMatchers() { }

    /**
     * Returns a Hamcrest Matcher that asserts all the named error messages are in place. Each error is a partial match,
     * so it's sufficient if each error appears as a substring of a larger message. The order of errors is not
     * processed.
     */
    public static Matcher<CompilationResult> hasErrors(final String firstError, final String... moreErrors) {
        final List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add(firstError);
        expectedErrors.addAll(Arrays.asList(moreErrors));
        return new TypeSafeMatcher<CompilationResult>() {
            @Override
            public void describeTo(final Description description) {
                description.appendText("A failed compilation with the following errors ") //
                           .appendValueList("", ", ", "", expectedErrors);
            }

            @Override
            protected boolean matchesSafely(final CompilationResult item) {
                for (final String expectedError : expectedErrors) {
                    boolean found = false;
                    for (final String error : item.getErrors()) {
                        if (error.contains(expectedError)) {
                            found = true;
                            break;
                        }

                    }

                    if (!found) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    public static Matcher<CompilationResult> isSuccessFull() {
        return new TypeSafeMatcher<CompilationResult>() {
            @Override
            protected boolean matchesSafely(final CompilationResult item) {
                return item.isSuccess();
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("A successfull compilation");
            }
        };
    }

}
