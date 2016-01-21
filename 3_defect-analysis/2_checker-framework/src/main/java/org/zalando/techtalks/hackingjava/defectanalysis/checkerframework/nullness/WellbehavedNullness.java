package org.zalando.techtalks.hackingjava.defectanalysis.checkerframework.nullness;

import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.PolyNull;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.WellBehaved;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since 21.01.2016
 */
public class WellbehavedNullness implements WellBehaved {

    @PolyNull
    public String nullInNullOut(@PolyNull String input) {
        if (input == null) return input;
        return input.toUpperCase().trim();
    }

    @MonotonicNonNull
    private String initallyNull;

    public void assignField(@NonNull String value) {
        initallyNull = checkNotNull(value, "Value required");
    }

    @Nonnull
    public String getValue() {
        if (initallyNull == null) initallyNull = "wasNull";
        return initallyNull;
    }
}
