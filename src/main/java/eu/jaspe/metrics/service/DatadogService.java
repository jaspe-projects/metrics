package eu.jaspe.metrics.service;

import eu.jaspe.metrics.configuration.DatadogConfiguration;
import com.timgroup.statsd.Event;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.ServiceCheck;
import com.timgroup.statsd.StatsDClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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
@Service
@Log4j2
public class DatadogService {

  /**
   *
   */
  private StatsDClient statsDClient;

  /**
   *
   */
  @Autowired
  private DatadogConfiguration datadogConfiguration;

  /**
   *
   */
  @PostConstruct
  public void start() {
    if (statsDClient == null && datadogConfiguration.isEnabled()) {
      if (datadogConfiguration.getStatsdPort() == null || datadogConfiguration.getStatsdHost() == null || "".equals(datadogConfiguration.getStatsdHost())) {
        log.error("Datadog disabled: host and port ara mandatory");
      } else {
        try {
          String[] tagsArray = StringUtils.tokenizeToStringArray(datadogConfiguration.getTags(), ",", true, true);
          statsDClient = new NonBlockingStatsDClient(datadogConfiguration.getStatsdPrefix(), datadogConfiguration.getStatsdHost(), datadogConfiguration.getStatsdPort(), tagsArray);
        } catch (Exception e) {
          log.error("Error configuring Datadog client", e);
        }
      }
    }
  }

  /**
   *
   */
  @PreDestroy
  public void preDestroy() {
    stop();
  }

  /**
   *
   */
  public void stop() {
    if (statsDClient != null) {
      statsDClient.stop();
      statsDClient = null;
    }
  }

  /**
   *
   * @param metricName
   * @param value
   */
  public void count(String metricName, long value, String[] metricTags) {
    if (isEnabled()) {
      statsDClient.count(metricName + ".count", value, metricTags);
    }
  }

  /**
   *
   * @param metricName
   * @param value
   */
  public void gauge(String metricName, double value, String[] metricTags) {
    if (isEnabled()) {
      statsDClient.gauge(metricName + ".gauge", value, metricTags);
    }
  }

  /**
   *
   * @param metricName
   * @param value
   */
  public void gauge(String metricName, long value, String[] metricTags) {
    if (isEnabled()) {
      statsDClient.gauge(metricName + ".gauge", value, metricTags);
    }
  }

  /**
   *
   * @param metricName
   * @param value
   */
  public void time(String metricName, long value, String[] metricTags) {
    if (isEnabled()) {
      statsDClient.time(metricName + ".time", value, metricTags);
    }
  }

  /**
   *
   * @param metricName
   * @param value
   */
  public void histogram(String metricName, double value, String[] metricTags) {
    if (isEnabled()) {
      statsDClient.histogram(metricName + ".histogram", value, metricTags);
    }
  }

  /**
   *
   * @param metricName
   * @param value
   */
  public void histogram(String metricName, long value, String[] metricTags) {
    if (isEnabled()) {
      statsDClient.histogram(metricName + ".hist", value, metricTags);
    }
  }

  /**
   *
   * @param event
   * @param tags
   */
  public void recordEvent(Event event, String... tags) {
    if (isEnabled()) {
      statsDClient.recordEvent(event, tags);
    }
  }

  /**
   *
   * @param name
   * @param status
   * @param message
   * @param tags
   */
  public void serviceCheck(String name, ServiceCheck.Status status, String message, String ... tags) {
    if (isEnabled()) {
      ServiceCheck sc = ServiceCheck
        .builder()
        .withName(name)
        .withStatus(status)
        .withMessage(message)
        .withTags(tags)
        .build();
      statsDClient.serviceCheck(sc);
    }
  }

  /**
   *
   * @return
   */
  private boolean isEnabled() {
    return datadogConfiguration.isEnabled() && statsDClient != null;
  }
}
