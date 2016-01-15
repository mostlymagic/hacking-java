package org.zalando.techtalks.hackingjava.common.reflection;

import java.util.Objects;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.zalando.techtalks.hackingjava.common.reflection.ReflectionHelper.instantiate;
import static org.zalando.techtalks.hackingjava.common.reflection.ReflectionHelper.invokeMethod;

/**
 * @author Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since 15.07.2015
 */
public class ReflectionHelperTest {

    static class NotMuchHere {
        @Override
        public boolean equals(final Object obj) {
            return obj instanceof NotMuchHere;
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

    @Test
    public void instantiateWithDefaultConstructor() {

        assertThat(instantiate(NotMuchHere.class),
                   CoreMatchers.is(CoreMatchers.equalTo(new NotMuchHere())));
    }

    static class TwoArgs {
        private final int foo;
        private final String bar;

        public TwoArgs(final int foo, final String bar) {
            this.foo = foo;
            this.bar = bar;
        }

        @Override
        public int hashCode() {
            return Objects.hash(foo, bar);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            } else if (obj instanceof TwoArgs) {
                final TwoArgs other = (TwoArgs) obj;
                return Objects.equals(this.foo, other.foo) && Objects.equals(this.bar, other.bar);
            } else {
                return false;
            }
        }
    }

    @Test
    public void instantiateWithParams() {
        assertThat(instantiate(TwoArgs.class, 123, "abc"),
                   CoreMatchers.is(CoreMatchers.equalTo(new TwoArgs(123, "abc"))));
    }

    @Test
    public void invokeMethodWithoutParam() {
        class MethodWithoutParam {
            String foo() {
                return "bar";
            }
        }
        assertThat(invokeMethod(new MethodWithoutParam(), "foo"), CoreMatchers.is("bar"));
    }

    @Test
    public void invokeMethodWithParams() {
        class MethodWithTwoParams {
            String foo(final String fizz, final int buzz) {
                return fizz + buzz;
            }
        }
        assertThat(invokeMethod(new MethodWithTwoParams(), "foo", "abc", 123),
                   CoreMatchers.is("abc123"));
    }

}
