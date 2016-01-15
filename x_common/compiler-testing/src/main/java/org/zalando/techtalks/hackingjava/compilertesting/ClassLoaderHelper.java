package org.zalando.techtalks.hackingjava.compilertesting;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since   10.07.2015
 */
public final class ClassLoaderHelper {

    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();

    private static final Map<File, ClassLoader> CLASSLOADER_MAP = new ConcurrentHashMap<>(16);

    private ClassLoaderHelper() { }

    public static Class<?> loadClass(final File baseFolder, final String qualifiedType) {
        try {
            return getClassLoaderForDirectory(baseFolder).loadClass(qualifiedType);
        } catch (ClassNotFoundException | MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static ClassLoader getClassLoaderForDirectory(final File baseFolder) throws MalformedURLException {
        final Lock readLock = LOCK.readLock();
        readLock.lock();
        if (CLASSLOADER_MAP.containsKey(baseFolder)) {
            readLock.unlock();
            return CLASSLOADER_MAP.get(baseFolder);
        } else {
            final Lock writeLock = LOCK.writeLock();
            writeLock.lock();
            try {
                final URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {baseFolder.toURI().toURL()});
                CLASSLOADER_MAP.put(baseFolder, classLoader);
                return classLoader;
            } finally {
                writeLock.unlock();
            }
        }
    }

}
