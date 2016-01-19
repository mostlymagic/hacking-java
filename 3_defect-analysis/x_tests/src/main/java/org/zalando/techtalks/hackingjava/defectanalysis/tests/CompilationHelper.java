package org.zalando.techtalks.hackingjava.defectanalysis.tests;

import java.io.File;

import java.net.URI;
import java.net.URISyntaxException;

public class CompilationHelper {
    public File sourceFileFor(final Class<?> targetClass) {
        try {
            final URI uri = targetClass.getProtectionDomain().getCodeSource().getLocation().toURI();
            final File targetClassesDir = new File(uri);
            final File projectDir = targetClassesDir.getParentFile();
            final File sourceBaseDir = new File(projectDir.getParentFile(), "src/main/java/");
            return new File(sourceBaseDir, targetClass.getName().replace('.', '/') + ".java");
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
