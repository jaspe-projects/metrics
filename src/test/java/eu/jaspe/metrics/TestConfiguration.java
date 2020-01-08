package eu.jaspe.metrics;

import eu.jaspe.metrics.aspect.AbstractMetricsAspect;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

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
@EnableAutoConfiguration
@ComponentScan(basePackages = {"eu.jaspe.metrics"}, basePackageClasses = AbstractMetricsAspect.class)
public class TestConfiguration {
}
