package org.zalando.techtalks.hackingjava.compilertesting;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import org.zalando.techtalks.hackingjava.common.compiler.CompilationResult;

import static java.util.stream.Collectors.toList;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;

public class CompilerRun {

    private final List<File> sources;
    private final List<String> compilerFlags;
    private final List<String> annotationProcessorClasses;
    private final JavaCompiler javaCompiler;
    private final DiagnosticCollector<JavaFileObject> diagnosticListener;
    private final StandardJavaFileManager fileManager;

    public CompilerRun() {
        this.sources = new ArrayList<>();
        this.compilerFlags = new ArrayList<>();
        this.annotationProcessorClasses = new ArrayList<>();
        javaCompiler = ToolProvider.getSystemJavaCompiler();
        diagnosticListener = new DiagnosticCollector<>();
        fileManager = javaCompiler.getStandardFileManager(diagnosticListener, null, null);

    }

    public CompilerRun addSourceFile(final File file, final File... moreFiles) {
        sources.add(file);
        for (final File additionalFile : moreFiles) {
            sources.add(additionalFile);
        }

        return this;
    }

    public CompilerRun addCompilerFlag(final String flag, final String... moreFlags) {
        compilerFlags.add(flag);
        for (final String additionalFlag : moreFlags) {
            compilerFlags.add(additionalFlag);
        }

        return this;
    }

    public CompilerRun setLocation(final StandardLocation location,
                                   final File firstPath,
                                   final File... morePaths) {
        try {
            final List<File> paths = new ArrayList<>(morePaths.length + 1);
            paths.add(firstPath);
            paths.addAll(Arrays.asList(morePaths));
            fileManager.setLocation(location, paths);
            return this;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public CompilerRun addAnnotationProcessor(final String annotationProcessor) {
        annotationProcessorClasses.add(annotationProcessor);
        return this;
    }

    public CompilerRun addAnnotationProcessor(final Class<?> annotationProcessor) {
        annotationProcessorClasses.add(annotationProcessor.getName());
        return this;
    }

    public CompilationResult compile() {
        final Locale locale = Locale.ENGLISH;
        final Writer writer = null;
        final Iterable<? extends JavaFileObject> units = fileManager.getJavaFileObjectsFromFiles(
            this.sources);
        final Iterable<String> options = new ArrayList<>(this.compilerFlags);
        final Iterable<String> annotationProcessors = new ArrayList<>(this.annotationProcessorClasses);

        final boolean success = javaCompiler.getTask(writer,
                                                     fileManager,
                                                     diagnosticListener,
                                                     options,
                                                     annotationProcessors,
                                                     units).call();

        final List<Diagnostic<? extends JavaFileObject>> diagnostics = diagnosticListener
            .getDiagnostics();
        final List<String> errors = diagnostics.stream()
                                               .filter(it -> it.getKind() == ERROR)
                                               .map(it ->
                                                        it.getMessage(locale))
                                               .collect(toList());
        final List<String> warnings = diagnostics.stream()
                                                 .filter(it -> it.getKind() == WARNING)
                                                 .map(it ->
                                                          it.getMessage(locale))
                                                 .collect(toList());

        return new CompilationResult(success, warnings, errors);

    }
}
