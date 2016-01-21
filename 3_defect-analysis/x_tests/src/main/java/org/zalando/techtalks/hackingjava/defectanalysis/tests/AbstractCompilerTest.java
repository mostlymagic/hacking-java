package org.zalando.techtalks.hackingjava.defectanalysis.tests;

import com.google.common.base.Joiner;
import java.util.List;
import java.util.stream.Collectors;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Before;
import org.zalando.techtalks.hackingjava.common.compiler.CompilationResult;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.engine.DefectAnalysisEngine;

public abstract class AbstractCompilerTest extends CompilationHelper {

    DefectAnalysisEngine engine;

    protected abstract DefectAnalysisEngine instantiateEngine();

    @Before
    public void setupEngine() {
        this.engine = instantiateEngine();
    }

    protected Matcher<CompilationResult> isSuccess() {
        return new TypeSafeDiagnosingMatcher<CompilationResult>() {
            @Override public void describeTo(final Description description) {
                description.appendText("Successful compilation");
            }

            @Override protected boolean matchesSafely(final CompilationResult item,
                                                      final Description mismatchDescription) {
                final boolean success = item.isSuccess();
                if (!success) mismatchDescription.appendText(Joiner.on('\n').join(item.getErrors()));
                return success;
            }

            ;
        };

    }

    protected Matcher<CompilationResult> isFailureWithExpectedMessage(String message) {
        return new TypeSafeDiagnosingMatcher<CompilationResult>() {
            @Override public void describeTo(final Description description) {
                description.appendText("Compilation Failure");
            }

            @Override protected boolean matchesSafely(final CompilationResult item,
                                                      final Description mismatchDescription) {
                boolean falseSuccess = item.isSuccess();
                final String errorMessagesAsBlock = Joiner.on('\n').join(item.getErrors());
                if (!errorMessagesAsBlock.contains(message)) {
                    falseSuccess = true;
                    mismatchDescription.appendText("expected error message: ").appendText(message)
                            .appendText(" but got ");
                }
                mismatchDescription.appendText(errorMessagesAsBlock);
                return !falseSuccess;
            }

            ;
        };

    }

    private Iterable<SelfDescribing> convertErrors(final List<String> errors) {
        return errors.stream().map((it) -> {
            return new SelfDescribing() {
                @Override public void describeTo(final Description description) {
                    description.appendText(it);
                }
            };
        }).collect(Collectors.toList());
    }
}
