package org.zalando.techtalks.hackingjava.common.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

public class ReflectionHelper {

    public static <T> T instantiate(final Class<? extends T> type, final Object... params) {
        try {
            final Constructor<T> constructor = findCompatibleConstructor(type, params);
            constructor.setAccessible(true);
            return constructor.newInstance(params);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Object invokeMethod(final Object targetObject,
                                      final String methodName,
                                      final Object... params) {
        try {
            final List<Method> methodsByName = findMethodsByName(targetObject.getClass(),
                                                                 methodName);
            final Method method =
                methodsByName.stream()                                                            //
                    .filter(candidate -> candidate.getParameterCount() == params.length) //
                    .findFirst()                                                         //
                    .orElseThrow(ReflectionHelper::noAppropriateMethodFound);

            method.setAccessible(true);
            return method.invoke(targetObject, params); //
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

    }

    private static List<Method> findMethodsByName(final Class<?> type, final String methodName) {
        final ArrayList<Method> methods = new ArrayList<>();

        Class<?> currentType = type;
        while (true) {
            Arrays.asList(type.getDeclaredMethods()).stream()            //
                .filter(method -> method.getName().equals(methodName)) //
                .forEach(item -> methods.add(item));
            currentType = currentType.getSuperclass();
            if (currentType == null) {
                break;
            }
        }

        return methods;
    }

    private static <T> Constructor<T> findCompatibleConstructor(final Class<? extends T> type,
                                                                final Object[] prms) {

        @SuppressWarnings("unchecked")
        // we know this is correct, but can't prove it to the compiler
        final Constructor<T> constructor =

            (Constructor<T>) Arrays.asList(type.getDeclaredConstructors())                //
                .stream()                                              //
                .filter(cto -> cto.getParameterCount() == prms.length) //
                .filter(paramsCompatible(prms)).findFirst()            //
                .orElseThrow(ReflectionHelper::noAppropriateConstructorFound);
        return constructor;

    }

    private static Predicate<Executable> paramsCompatible(final Object[] prms) {
        return new Predicate<Executable>() {
            @Override
            public boolean test(final Executable executable) {
                return argumentLengthsCompatible(prms, executable) && argumentTypesCompatible(
                    executable,
                    prms);
            }

            private boolean argumentLengthsCompatible(final Object[] prms,
                                                      final Executable executable) {
                if (executable.isVarArgs()) {
                    return prms.length >= executable.getParameterCount() - 1;
                } else {
                    return prms.length == executable.getParameterCount();
                }
            }

        };
    }

    public static boolean argumentTypesCompatible(final Executable executable,
                                                  final Object[] prms) {
        final Queue<Object> remainingParams = new LinkedList<>(Arrays.asList(prms));
        for (final Parameter parameter : executable.getParameters()) {
            if (parameter.isVarArgs()) {
                final Class<?> componentType = parameter.getType().getComponentType();
                for (final Object remainingParam : remainingParams) {
                    if (!typeIsCompatible(remainingParam, componentType)) {
                        return false;
                    }
                }
            } else {
                if (remainingParams.isEmpty()) {
                    return false;
                }

                final Object paramObject = remainingParams.poll();

                if (!typeIsCompatible(paramObject, parameter.getType())) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean typeIsCompatible(final Object paramObject, final Class<?> type) {
        return wrapPrimitive(type).isInstance(paramObject) || (paramObject == null && !type
            .isPrimitive());
    }

    public static Class<?> wrapPrimitive(final Class<?> type) {
        if (type.isPrimitive()) {
            if (type == int.class) {
                return Integer.class;
            }

            if (type == long.class) {
                return Long.class;
            }

            if (type == boolean.class) {
                return Boolean.class;
            }

            if (type == short.class) {
                return Short.class;
            }

            if (type == double.class) {
                return Double.class;
            }

            if (type == float.class) {
                return Float.class;
            }

            if (type == byte.class) {
                return Byte.class;
            }

            throw new IllegalStateException("Unknown primitive type: " + type);

        } else {
            return type;
        }
    }

    private static RuntimeException noAppropriateConstructorFound() {
        throw new IllegalStateException();
    }

    private static RuntimeException noAppropriateMethodFound() {
        throw new IllegalStateException();
    }

}
