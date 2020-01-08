package eu.jaspe.metrics.logging;


import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
public class LoggingWorker implements Runnable {

    /**
     *
     */
    private final BlockingQueue<ProfiledMeasure> measuresQueue = new LinkedBlockingQueue<>();
    /**
     *
     */
    private final Logger log;

    /**
     *
     */
    private boolean keepRunning = true;

    /**
     *
     * @param log
     */
    public LoggingWorker(Logger log) {
        this.log = log;
    }

    /**
     *
     */
    @Override
    public void run() {
        if (log.isTraceEnabled()) {
            log.trace("Monitoring logs enabled");
        }
        while (keepRunning) {
            try {
                final ProfiledMeasure measure = measuresQueue.take();
                log.trace("{}(..) | {}", measure.getName(), measure.getValue());
            } catch (final InterruptedException e) {
                log.error("Blocking queue was interrupted {}", e);
            }
        }
    }

    /**
     *
     */
    public void signalToStop() {
        keepRunning = false;
    }

    /**
     *
     * @param profiledMeasure
     */
    public void enqueue(final ProfiledMeasure profiledMeasure) {
        if (profiledMeasure != null) {
            measuresQueue.add(profiledMeasure);
        }
    }

}
