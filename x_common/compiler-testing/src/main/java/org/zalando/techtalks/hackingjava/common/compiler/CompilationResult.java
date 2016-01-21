package org.zalando.techtalks.hackingjava.common.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class CompilationResult {

    private final boolean success;
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

    public CompilationResult(final boolean success, final List<String> warnings, final List<String> errors) {
        this.success = success;
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
