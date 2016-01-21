package org.zalando.techtalks.hackingjava.valueobjects.cglib;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static java.beans.Introspector.getBeanInfo;

class DtoFactory {
    static Class<?> createBeanClass(
    /* fully qualified class name */
    final String className,
    /* bean properties, name -> type */
    final Map<String, Class<?>> properties) {

        final BeanGenerator beanGenerator = new BeanGenerator();

    /* use our own hard coded class name instead of a real naming policy */
        beanGenerator.setNamingPolicy(new NamingPolicy() {
            @Override public String getClassName(final String prefix,
                                                 final String source,
                                                 final Object key,
                                                 final Predicate names) {
                return className;
            }
        });
        BeanGenerator.addProperties(beanGenerator, properties);
        return (Class<?>) beanGenerator.createClass();
    }

    static Object instantiateDto(final Class<?> dtoClass, final Map<String, Object> propertyMap) {
        return instantiateDto(dtoClass, propertyMap, ImmutableList.of());
    }

    static Object instantiateDto(final Class<?> dtoClass,
                                 final Map<String, Object> propertyMap,
                                 final List<String> comparableProperties) {
        try {
            final Object dto = dtoClass.newInstance();
            assignProperties(propertyMap, dto);

            return comparableProperties.isEmpty() ? createProxy(dto) : createComparableProxy(dto,
                                                                                             comparableProperties);

        } catch (InstantiationException | IllegalAccessException | IntrospectionException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Object createProxy(final Object dto) {
        final Enhancer e = new Enhancer(); /* the class will extend from the real class */
        e.setSuperclass(dto.getClass());
        e.setCallback(new HandlerBasedMethodInterceptor(dto));
        return e.create();
    }

    private static Object createComparableProxy(final Object dto,
                                                final List<String> propertyNames) {
        final Enhancer e = new Enhancer(); /* the class will extend from the real class */
        e.setSuperclass(dto.getClass());
        e.setInterfaces(new Class[]{Comparable.class});
        e.setCallback(new HandlerBasedMethodInterceptor(dto, propertyNames));
        return e.create();
    }

    static Map<String, Object> readProperties(final Object dto) {
        final Object deProxied = deProxy(dto);
        final Map<String, Object> propertyMap = newLinkedHashMap();
        try {
            propertyDescriptors(deProxied).filter((desc) -> desc.getReadMethod() != null).forEach(

                (desc) -> {
                    try {
                        propertyMap.put(desc.getName(), desc.getReadMethod().invoke(deProxied));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new IllegalStateException(e);
                    }
                });
        } catch (IntrospectionException e) { throw new IllegalStateException(e); }
        return propertyMap;
    }

    private static void assignProperties(final Map<String, Object> propertyMap,
                                         final Object dto) throws IntrospectionException {
        final Map<String, Method> setterMap = newHashMapWithExpectedSize(propertyMap.size());
        final Object deProxied = deProxy(dto);
        propertyDescriptors(deProxied).filter(
            (d) -> d.getWriteMethod() != null).forEach((desc) -> {
            setterMap.put(desc.getName(), desc.getWriteMethod());
        });
        propertyMap.entrySet()
                   .stream()
                   .forEach((entry) -> {
                       final String propertyKey = entry.getKey();
                       final Method setter = setterMap.get(propertyKey);
                       if (setter == null) throw new IllegalArgumentException(String.format(
                           "Unknown property: %s",
                           propertyKey));
                       try {
                           setter.invoke(deProxied, entry.getValue());
                       } catch (IllegalAccessException | InvocationTargetException e) {
                           throw new IllegalStateException(e);
                       }
                   });
    }

    private static Stream<PropertyDescriptor> propertyDescriptors(final Object dto) throws
                                                                                    IntrospectionException {

        return Arrays.stream(getBeanInfo(dto.getClass(), Object.class).getPropertyDescriptors());
    }

    static Object deProxy(final Object o) {
        if (o instanceof Factory) {
            final Factory factory = (Factory) o;
            final Callback callback = factory.getCallback(0);
            if (callback instanceof Supplier) {
                final Supplier<?> supplier = (Supplier<?>) callback;
                return supplier.get();
            }
        }
        return o;
    }


    private static class HandlerBasedMethodInterceptor implements MethodInterceptor,
                                                                  Supplier<Object> {
        private final Object dto;
        private final List<String> comparablePropertyNames;

        public HandlerBasedMethodInterceptor(final Object dto) {
            this.dto = dto;
            comparablePropertyNames = ImmutableList.of();
        }

        public HandlerBasedMethodInterceptor(
            final Object dto, final List<String> comparablePropertyNames) {
            this.dto = dto;
            this.comparablePropertyNames = ImmutableList.copyOf(comparablePropertyNames);
        }

        private static final Method COMPARE_TO;

        static {
            try {
                COMPARE_TO = Comparable.class.getDeclaredMethod("compareTo", Object.class);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override public Object intercept(final Object obj,
                                          final Method method,
                                          final Object[] args,
                                          final MethodProxy proxy) throws Throwable {
            for (final MethodHandler handler : MethodHandler.values()) {
                if (handler.matches(method)) return handler.invoke(dto, args);
            }
            if (Objects.equals(method, COMPARE_TO)) return compare(dto, deProxy(args[0]));
            return method.invoke(dto, args);
        }

        @Override public Object get() {
            return dto;
        }

        @SuppressWarnings("rawtypes")
        private int compare(final Object o1, final Object o2) {
            final Map<String, Object> left = readProperties(o1);
            final Map<String, Object> right = readProperties(o2);
            final ComparisonChain chain = ComparisonChain.start();
            for (final String propertyName : comparablePropertyNames) {
                chain.compare((Comparable) left.get(propertyName),
                              (Comparable) right.get(propertyName));
            }
            return chain.result();
        }
    }
} 