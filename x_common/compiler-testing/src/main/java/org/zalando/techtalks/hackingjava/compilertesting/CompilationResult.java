package org.zalando.techtalks.hackingjava.compilertesting;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since   08.07.2015
 */
public final class CompilationResult {

    private final boolean success;
    private final File outputFolder;
    private final List<String> warnings;
    private final List<String> errors;

    public boolean isSuccess() {
        return success;
    }

    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public File getOutputFolder() {
        return outputFolder;
    }

    public CompilationResult(final boolean success, final List<String> warnings, final List<String> errors,
            final File outputFolder) {
        this.success = success;
        this.outputFolder = outputFolder;
        this.warnings = new ArrayList<>(warnings);
        this.errors = new ArrayList<>(errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, warnings, errors);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final CompilationResult other = (CompilationResult) obj;
        return Objects.equals(this.success, other.success) && Objects.equals(this.warnings, other.warnings)
                && Objects.equals(this.errors, other.errors);
    }

    @Override
    public String toString() {
        return "CompilationResult{" + "success=" + success + ", warnings=" + warnings + ", errors=" + errors + '}';
    }
}
