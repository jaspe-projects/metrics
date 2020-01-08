package eu.jaspe.metrics.aspect;

import eu.jaspe.metrics.annotation.Counted;
import eu.jaspe.metrics.annotation.Metric;
import eu.jaspe.metrics.annotation.Timed;
import eu.jaspe.metrics.logging.LoggingWorker;
import eu.jaspe.metrics.logging.ProfiledMeasure;
import eu.jaspe.metrics.service.DatadogService;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Method;

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
@Log4j2
@Aspect
@Component
public abstract class AbstractMetricsAspect {

  @Autowired
  protected DatadogService datadogService;

  private Thread loggingWorkerThread = null;

  private LoggingWorker loggingWorker = null;

  @PostConstruct
  public void init() {
    startLoggingWorkers();
  }

  @PreDestroy
  public void preDestroy() {
    stopLoggingWorkers();
  }

  @Pointcut("execution(public * *(..))")
  public void publicMethodsPointCut() {
    // Pointcut methods need no body
  }

  @Pointcut("within(@org.springframework.stereotype.Controller *)")
  public void controllerBeanPointCut() {
    // Pointcut methods need no body
  }

  @Pointcut("within(@org.springframework.web.bind.annotation.RequestMapping *)")
  public void requestMappings() {
    // Pointcut methods need no body
  }

  @Pointcut("@annotation(eu.jaspe.metrics.annotation.Timed)")
  public void timedMethodPointCut() {
    // Pointcut MethodsPointCut need no body
  }

  @Pointcut("@annotation(eu.jaspe.metrics.annotation.Counted)")
  public void countedMethodPointCut() {
    // Pointcut MethodsPointCut need no body
  }

  @Around("timedMethodPointCut()")
  public Object timedMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    final String metricName = getMetricName(proceedingJoinPoint).toString();
    final String[] metricTags = getTimedTags(proceedingJoinPoint);
    long duration = System.currentTimeMillis();
    try {
      return proceedingJoinPoint.proceed();
    } finally {
      duration = System.currentTimeMillis() - duration;
      time(metricName, duration, metricTags);
      gauge(metricName + ".time", duration, metricTags); // TODO review
    }
  }

  @Around("countedMethodPointCut()")
  public Object countedMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    boolean error = false;
    String metricName = getMetricName(proceedingJoinPoint).toString();
    final String[] metricTags = getCountedTags(proceedingJoinPoint);
    try {
      return proceedingJoinPoint.proceed();
    } catch (Throwable t) {
      error = true;
      throw t;
    } finally {
      count(metricName, 1L, metricTags); // Sends the total count no matter if it is OK or KO
      if (!error) {
        count(metricName + ".ok", 1L, metricTags);
      } else {
        count(metricName + ".ko", 1L, metricTags);
      }
    }
  }

  protected void time(final String metricName, long value, String[] metricTags) {
    if (isLogEnable()) {
      loggingWorker.enqueue(ProfiledMeasure.builder().name(metricName + ".time").value(value).build());
    }
    datadogService.time(metricName, value, metricTags);
  }

  protected void count(final String metricName, long value, String[] metricTags) {
    if (isLogEnable()) {
      loggingWorker.enqueue(ProfiledMeasure.builder().name(metricName + ".count").value(value).build());
    }
    datadogService.count(metricName, value, metricTags);
  }

  protected void gauge(final String metricName, long value, String[] metricTags) {
    if (isLogEnable()) {
      loggingWorker.enqueue(ProfiledMeasure.builder().name(metricName + ".gauge").value(value).build());
    }
    datadogService.gauge(metricName, value, metricTags);
  }

  protected void histogram(final String metricName, long value, String[] metricTags) {
    if (isLogEnable()) {
      loggingWorker.enqueue(ProfiledMeasure.builder().name(metricName + ".histogram").value(value).build());
    }
    datadogService.histogram(metricName, value, metricTags);
  }

  protected static StringBuilder getMetricName(ProceedingJoinPoint proceedingJoinPoint) {
    MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
    Method method = methodSignature.getMethod();
    Metric metric = method.getAnnotation(Metric.class);
    final StringBuilder metricName = new StringBuilder();
    if (proceedingJoinPoint.getTarget() != null) {
      metricName.append(proceedingJoinPoint.getTarget().getClass().getSimpleName());
      //metricName.append(proceedingJoinPoint.getTarget().getClass().getCanonicalName());
      if (metric != null && metric.metricName() != null && !metric.metricName().isEmpty()) {
        metricName.append(".").append(metric.metricName());
      } else {
        metricName.append(".").append(proceedingJoinPoint.getSignature().getName());
      }
    } else {
      metricName.append(proceedingJoinPoint.getSignature().getDeclaringType().getSimpleName());
    }
    return metricName;
  }

  /**
   *
   * @param proceedingJoinPoint
   * @return
   */
  protected static String[] getTimedTags(ProceedingJoinPoint proceedingJoinPoint) {
    MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
    Method method = methodSignature.getMethod();
    return method.getAnnotation(Timed.class).tags();
  }

  /**
   *
   * @param proceedingJoinPoint
   * @return
   */
  protected static String[] getCountedTags(ProceedingJoinPoint proceedingJoinPoint) {
    MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
    Method method = methodSignature.getMethod();
    return method.getAnnotation(Counted.class).tags();
  }

  /**
   *
   */
  public void startLoggingWorkers() {
    if (loggingWorker == null) {
      loggingWorker = new LoggingWorker(log);
      loggingWorkerThread = new Thread(loggingWorker);
      loggingWorkerThread.setDaemon(true);
      loggingWorkerThread.start();
    }
  }

  /**
   *
   */
  public void stopLoggingWorkers() {
    if (loggingWorkerThread != null) {
      loggingWorker.signalToStop();
      loggingWorkerThread.interrupt();
    }
  }

  /**
   *
   * @return
   */
  private boolean isLogEnable() {
    return log.isTraceEnabled() && loggingWorker != null;
  }
}
