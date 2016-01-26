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
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.lang.String.format;
import static java.util.Arrays.asList;

/**
 * This class encapsulates a call to the {@code java} executable (or optionally {@code javac}) in a separate, forked
 * process, with the classpath copied from the local process. Additionally, it allows you to move classpath entries to
 * the bootclasspath, in order to overwrite default JDK functionality. This class is stateful and not thread safe.
 */
public final class ForkedRun {
    private static final Joiner SEP_JOINER;
    private static final String MAVEN_REPO;
    private static final String ROOT_PROJECT_PATH;

    // by default, all classpath elements that come either from inside the project root or the local maven repository
    // are included in the forked classpath
    private final Set<JarPredicate> jarFilePredicates = EnumSet.of(
            JarPredicate.IN_LOCAL_MAVEN_REPO,
            JarPredicate.IN_PROJECT_DIR);
    private boolean useJavaC;
    private boolean outputCommand;

    private final Set<String> bootClassPathMatchers = newHashSet();
    private final List<String> vmArgs = newArrayList();
    private final List<String> args = newArrayList();
    private final ClassLoader referenceClassLoader;
    private final Set<ClassLoader> additionalClassLoaders = newHashSet();

    /**
     * Activate the flag to output the full command line statement to standard out before executing it.
     */
    public ForkedRun printCommand() {
        this.outputCommand = true;
        return this;
    }

    /**
     * Explicitly add the tools.jar / classes.jar to the forked classpath. Since JDK 8, these are already present on the
     * classpath by default.
     */
    public ForkedRun withToolsJar() {
        jarFilePredicates.add(JarPredicate.TOOLS_JAR);
        return this;
    }

    /**
     * Activate the flag to use javac instead of java as target executable.
     */
    public ForkedRun withJavaC() {
        this.useJavaC = true;
        return this;
    }

    /**
     * Create a temporary directory and store its full path as command line argument.
     */
    public ForkedRun tempDirAsArg() {
        this.args.add(Files.createTempDir().getAbsolutePath());
        return this;
    }

    /**
     * Activate debugging in the forked process, with the specified port.
     */
    public ForkedRun withDebugPort(final int port) {
        vmArgs.addAll(
                asList("-Xdebug", "-Xnoagent",
                        format("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=%d", port)));
        return this;
    }

    /**
     * Set a vm argument in the forked process.
     */
    public ForkedRun withArg(final String arg) {
        args.add(arg);
        return this;
    }

    /**
     * Set a command line argument in the forked process.
     */
    public ForkedRun withArg(final Class<?> arg) {
        args.add(arg.getName());
        return this;
    }

    /**
     * Convert all class path elements that contain this partial path signature to bootclasspath elements. The
     * individual arguments should be parts of the directory path, usually Maven groupId and artifactId, e.g. "org",
     * "springframework", "spring-aop".
     */
    public ForkedRun withBootClassPathMatcher(final String pathElement, final String... morePathElements) {
        bootClassPathMatchers.add(Joiner.on(File.separatorChar).join(Lists.asList(pathElement, morePathElements)));
        return this;
    }

    /**
     * Adds all classpath elements from the ClassLoader owning the referenced class.
     */
    public ForkedRun withAdditionalClassLoaderFromClass(final Class<?> referenceClass) {
        return withAdditionalClassLoader(referenceClass.getClassLoader());
    }

    /**
     * Adds all classpath elements from the referenced ClassLoader.
     */
    public ForkedRun withAdditionalClassLoader(final ClassLoader additional) {
        additionalClassLoaders.add(additional);
        return this;
    }

    /**
     * Instantiate with reference ClassLoader.
     */
    public ForkedRun(final ClassLoader referenceClassLoader) {
        if (referenceClassLoader instanceof URLClassLoader) {
            final URLClassLoader urlClassLoader = (URLClassLoader) referenceClassLoader;
            this.referenceClassLoader = urlClassLoader;
        } else throw new IllegalArgumentException("Unsupported ClassLoader: " + referenceClassLoader);
    }

    /**
     * Instantiate with ClassLoader from reference class.
     */
    public ForkedRun(final Class<?> referenceClass) {
        this(referenceClass.getClassLoader());
    }

    /**
     * Execute the actual forked process and create a CompilationResult object. This method should only be called once!
     */
    public final CompilationResult run() {
        final Set<String> classPathElements = new LinkedHashSet<>();
        final Set<String> bootPathElements = new LinkedHashSet<>();

        try {
            dispatchClassPath(classPathElements, bootPathElements);
            final String javaExe = getExecutable();
            final List<String> commands = buildCommands(classPathElements, bootPathElements, javaExe);
            final ProcessBuilder processBuilder = new ProcessBuilder().command(commands);
            if (outputCommand) System.out.println(Joiner.on(' ').join(commands));
            processBuilder.directory(Files.createTempDir());
            final Process proc = processBuilder.start();
            final List<String> errorMessages = gatherOutput(proc);
            final int statusCode = proc.waitFor();
            return new CompilationResult(statusCode == 0, Collections.<String>emptyList(), errorMessages);

        } catch (InterruptedException | IOException | URISyntaxException e) {
            throw new IllegalStateException(e);
        }
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

    // collect all URLs from all ClassLoaders
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

    private String getExecutable() {
        return new File(
                System.getProperty("java.home"),
                useJavaC ? "../bin/javac" : "bin/java"
        ).getAbsolutePath();
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

    static {
        SEP_JOINER = Joiner.on(File.pathSeparatorChar);
        MAVEN_REPO = new File(System.getProperty("user.home"), ".m2/repository").getAbsolutePath();
        ROOT_PROJECT_PATH = calculateProjectRoot();
    }

    static String calculateProjectRoot() {
        File current = new File(System.getProperty("user.dir"));
        while (current != null) {
            if (new File(current, "x_common").isDirectory())
                return current.getAbsolutePath();
            current = current.getParentFile();
        }
        throw new IllegalStateException("Couldn't detect project root. Please fix this hacky logic");
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
}
