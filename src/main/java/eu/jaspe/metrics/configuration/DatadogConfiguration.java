package eu.jaspe.metrics.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 *
 * @author cjrequena
 * @version 1.0
 * @see
 * @since JDK1.8
 */
@Data
@Configuration
@ConfigurationProperties("metrics.datadog")
public class DatadogConfiguration {


    /**
     * This flag enables or disables the datadog reporter
     */
    private boolean enabled;
    /**
     * common case: localhost
     */
    public String statsdHost;
    /**
     *
     */
    public Integer statsdPort;
    /**
     * prefix to any stats; may be null or empty string
     */
    public String statsdPrefix;
    /**
     * Datadog extension: Constant tags, always applied
     */
    public String tags;

}
