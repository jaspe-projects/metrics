# Metrics
- Metrics library and monitoring tool for Spring Boot Applications.
- Metrics provides monitoring support for Spring Boot Applications.
- Metrics implements java-dogstatsd-client and only supports Datadog.


## Getting started
1. Add the metrics dependency to your project.
2. Add metrics configuration class

```java
import AbstractMetricsAspect;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"eu.jaspe.metrics"}, basePackageClasses = AbstractMetricsAspect.class)
public class MetricsConfiguration {
}
``` 

3. Add the annotations
    - @Metric(metricName = "metric_name") | Not mandatory, if this is not added then the metric name will be the method name by default.
    - @Timed | Used to send time metrics.
    - @Counted | Used to send count metrics. This will send the total count request, The total count request with status OK and the total count request with status KO.

## Count metrics naming convention

project_name.class_name.metric_name.count

project_name.class_name.metric_name.ok.count

project_name.class_name.metric_name.ko.count

## Time metrics naming convention

project_name.class_name.metric_name.time

project_name.class_name.metric_name.time.gauge

project_name.class_name.metric_name.ok.time

project_name.class_name.metric_name.ko.time

project_name.class_name.metric_name.ok.time.gauge

project_name.class_name.metric_name.ko.time.gauge
