package org.zalando.techtalks.hackingjava.valueobjects.cglib;

import com.google.common.base.MoreObjects;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since 15.01.2016
 */
enum MethodHandler {
    EQUALS {
        @SuppressWarnings({"rawtypes", "unchecked"}) @Override
        boolean matches(final Method candidate) {
            return candidate.getName().equals("equals")
                && Arrays.equals(candidate.getParameterTypes(), new Class[]{Object.class});
        }

        @Override Object invoke(final Object instance, final Object[] args) {
            return instance.getClass().isInstance(args[0])
                && DtoFactory.readProperties(instance)
                             .equals(DtoFactory.readProperties(args[0]));
        }
    }, HASHCODE {
        @Override boolean matches(final Method candidate) {
            return candidate.getName().equals("hashCode")
                && candidate.getParameterTypes().length == 0;
        }

        @Override Object invoke(final Object instance,
                                final Object[] args) {
            return Arrays.hashCode(DtoFactory.readProperties(instance).values().toArray());
        }
    }, TOSTRING {
        @Override boolean matches(final Method candidate) {
            return candidate.getName().equals("toString")
                && candidate.getParameterTypes().length == 0;
        }

        @Override Object invoke(final Object instance,
                                final Object[] args) {
            final MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(instance);
            for (final Map.Entry<String, Object> entry : DtoFactory.readProperties(instance)
                                                                   .entrySet()) {
                helper.add(entry.getKey(), entry.getValue());
            }
            return helper.toString();
        }
    };

    public boolean isMatch(final Method candidate) {
        final int modifiers = candidate.getModifiers();
        if (Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) return false;
        return matches(candidate);
    }

    abstract boolean matches(Method candidate);

    abstract Object invoke(Object instance, Object[] args);
}
