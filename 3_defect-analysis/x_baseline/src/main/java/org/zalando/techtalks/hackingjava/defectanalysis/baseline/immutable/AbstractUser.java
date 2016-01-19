package org.zalando.techtalks.hackingjava.defectanalysis.baseline.immutable;

import java.util.Date;
import java.util.List;

/**
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since   19.01.2016
 */
public abstract class AbstractUser {
    public abstract String getName();

    public abstract List<String> getNickNames();

    public abstract Date getBirthDate();
}
