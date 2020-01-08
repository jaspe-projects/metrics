package eu.jaspe.metrics.logging;

import lombok.Data;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 * @author cjrequena
 * @version 1.0
 * @since JDK1.8
 * @see
 *
 */
@Data
@lombok.Builder
public class ProfiledMeasure {

    /**
     *
     */
    private final String name;
    /**
     *
     */
    private final long value;
}
