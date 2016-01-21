package org.zalando.techtalks.hackingjava.common.compiler;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.util.Arrays.asList;

public final class ForkedRun {
    private static final Joiner SEP_JOINER = Joiner.on(File.pathSeparatorChar);
    private static final String MAVEN_REPO = new File(System.getProperty("user.home"), ".m2/repository")
            .getAbsolutePath();

    private static final String ROOT_PROJECT_PATH = calculateProjectRoot();

    private final Set<Predicate<File>> jarFilePredicates = newHashSet(JarPredicate.values());
    private boolean useJavaC;
    private boolean outputCommand;

    public ForkedRun printCommand() {
        this.outputCommand = true;
        return this;
    }

    public ForkedRun withToolsJar() {
        jarFilePredicates.add(JarPredicate.TOOLS_JAR);
        return this;
    }

    public ForkedRun withJavaC() {
        this.useJavaC = true;
        return this;
    }

    public ForkedRun tempDirAsArg() {
        this.args.add(Files.createTempDir().getAbsolutePath());
        return this;
    }

    public ForkedRun withDebugPort(final int port) {
        vmArgs.addAll(
                asList("-Xdebug", "-Xnoagent", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=" + port));
        return this;
    }

    enum JarPredicate implements Predicate<File> {
        IN_LOCAL_MAVEN_REPO {
            @Override public boolean test(final File file) {
                return file.getAbsolutePath().startsWith(MAVEN_REPO);
            }
        }, IN_PROJECT_DIR {
            @Override public boolean test(final File file) {
                return file.getAbsolutePath().startsWith(ROOT_PROJECT_PATH);
            }
        }, TOOLS_JAR {
            final Set<String> supportedNames = ImmutableSet.of("tools.jar", "classes.jar");

            @Override public boolean test(final File file) {
                return supportedNames.contains(file.getName());
            }
        }
    }

    static String calculateProjectRoot() {
        final File classesLocation = new File(System.getProperty("user.dir"));
        File current = classesLocation;
        while (current != null) {
            if (new File(current, "x_common").isDirectory())
                return current.getAbsolutePath();
            current = current.getParentFile();
        }
        throw new IllegalStateException("Couldn't detect project root. Please fix this hacky logic");
    }

    private final Set<String> bootClassPathMatchers = new HashSet<>();
    private final List<String> vmArgs = newArrayList();
    private final List<String> args = newArrayList();
    private final URLClassLoader referenceClassLoader;
    private final Set<ClassLoader> additionalClassLoaders = newHashSet();

    public ForkedRun withArg(final String arg) {
        args.add(arg);
        return this;
    }

    public ForkedRun withJarPredicate(final Predicate<File> jarPredicate) {
        jarFilePredicates.add(jarPredicate);
        return this;
    }

    public ForkedRun removeLocalRepoJars() {
        jarFilePredicates.remove(JarPredicate.IN_LOCAL_MAVEN_REPO);
        return this;
    }

    public ForkedRun removeProjectJars() {
        jarFilePredicates.remove(JarPredicate.IN_PROJECT_DIR);
        return this;
    }

    public ForkedRun withVmArg(final String arg) {
        vmArgs.add(arg);
        return this;
    }

    public ForkedRun withArg(final Class<?> arg) {
        args.add(arg.getName());
        return this;
    }

    public ForkedRun withBootClassPathMatcher(final String pathElement, final String... morePathElements) {
        bootClassPathMatchers.add(Joiner.on(File.separatorChar).join(Lists.asList(pathElement, morePathElements)));
        return this;
    }

    public ForkedRun withAdditionalClassLoader(final ClassLoader additional) {
        additionalClassLoaders.add(additional);
        return this;
    }

    public ForkedRun withAdditionalClassLoaderFromClass(final Class<?> referenceClass) {
        return withAdditionalClassLoader(referenceClass.getClassLoader());
    }

    public ForkedRun(final ClassLoader referenceClassLoader) {
        if (referenceClassLoader instanceof URLClassLoader) {
            final URLClassLoader urlClassLoader = (URLClassLoader) referenceClassLoader;
            this.referenceClassLoader = urlClassLoader;
        } else throw new IllegalArgumentException("Unsupported ClassLoader: " + referenceClassLoader);
    }

    public ForkedRun(final Class<?> referenceClass) {
        this(referenceClass.getClassLoader());
    }

    public final CompilationResult run() {
        final Set<String> classPathElements = new LinkedHashSet<>();
        final Set<String> bootPathElements = new LinkedHashSet<>();

        try {
            dispatchClassPath(classPathElements, bootPathElements);
            final String javaExe = getExecutable();
            final List<String> commands = buildCommands(classPathElements, bootPathElements, javaExe);
            final ProcessBuilder processBuilder = new ProcessBuilder().command(commands);
            if (outputCommand) System.out.println(Joiner.on(' ').join(commands));
            final Process proc = processBuilder.start();
            final List<String> errorMessages = gatherOutput(proc);
            final int statusCode = proc.waitFor();
            return new CompilationResult(statusCode == 0, Collections.<String>emptyList(), errorMessages);

        } catch (InterruptedException | IOException | URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    private List<String> buildCommands(final Set<String> classPathElements, final Set<String> bootPathElements,
                                       final String javaExe) {
        final List<String> commands = newArrayList();
        if (!bootPathElements.isEmpty()) {
            vmArgs.add("-Xbootclasspath/p:" + SEP_JOINER.join(bootPathElements));
        }
        commands.add(javaExe);
        commands.addAll(vmArgs);
        if (!classPathElements.isEmpty()) {
            commands.add("-cp");
            commands.add(SEP_JOINER.join(classPathElements));
        }
        commands.addAll(args);
        return ImmutableList.copyOf(commands);
    }

    private String getExecutable() {
        return new File(
                System.getProperty("java.home"),
                useJavaC ? "../bin/javac" : "bin/java"
        ).getAbsolutePath();
    }

    private void dispatchClassPath(final Set<String> classPathElements, final Set<String> bootPathElements)
            throws URISyntaxException {
        for (final URL url : getUrLs()) {
            final File jarFile = new File(url.toURI());
            if (!jarFilePredicates.stream().anyMatch((it) -> it.test(jarFile))) continue;
            final String absolutePath = jarFile.getAbsolutePath();
            if (bootClassPathMatchers.stream().anyMatch((it) -> {
                return absolutePath.contains(it);
            })) bootPathElements.add(absolutePath);
            else classPathElements.add(absolutePath);
        }
    }

    private List<String> gatherOutput(final Process proc) throws IOException {
        try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                final BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()))) {
            final List<String> errorMessages = newArrayList();
            String line = null;
            while ((line = stdInput.readLine()) != null) {
                System.out.println(line);
            }
            while ((line = stdError.readLine()) != null) {
                errorMessages.add(line);
            }
            return errorMessages;
        }
    }

    private boolean inProjectRoot(final String absolutePath) {
        return absolutePath.startsWith(ROOT_PROJECT_PATH);
    }

    private boolean inLocalMavenRepo(final String absolutePath) {
        return absolutePath.startsWith(MAVEN_REPO);
    }

    private Iterable<URL> getUrLs() {
        final Set<URL> urls = newLinkedHashSet();
        for (final ClassLoader referenceClassLoader : Lists.asList(this.referenceClassLoader,
                additionalClassLoaders.toArray(new ClassLoader[additionalClassLoaders.size()]))) {

            ClassLoader current = referenceClassLoader;
            while (current != null) {
                if (current instanceof URLClassLoader) {
                    final URLClassLoader urlClassLoader = (URLClassLoader) current;
                    urls.addAll(asList(urlClassLoader.getURLs()));
                } else {
                    throw new IllegalStateException("Bad ClassLoader: " + current);
                }
                current = current.getParent();
            }
        }
        return urls;
    }
}
