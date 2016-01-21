package org.zalando.techtalks.hackingjava.defectanalysis.checkerframework.nullness;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.IllBehaved;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since 21.01.2016
 */
public class IllbehavedNullness implements IllBehaved {

    @NonNull
    public String nullInNullOut(@Nullable String input) {
        if (input == null) return input;
        return input.toUpperCase().trim();
    }

    @NonNull
    private String initallyNull;

    public void assignField(@Nullable String value) {
        initallyNull = checkNotNull(value, "Value required");
    }

    @Nullable
    public String getValue() {
        if (initallyNull == null) initallyNull = "wasNull";
        return initallyNull;
    }
}
