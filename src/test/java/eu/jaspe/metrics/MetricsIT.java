package eu.jaspe.metrics;

import eu.jaspe.metrics.service.DatadogService;
import lombok.extern.log4j.Log4j2;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

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
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfiguration.class})
@TestPropertySource(locations = "/config.properties")
@ActiveProfiles({"local","integration-test"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetricsIT {

    @Autowired
    DatadogService datadogService;

    @Test
    public void count_test()  {
        for (int i = 0; i < 10; i++) {
            datadogService.count("metrics.test", 1L, null);
        }
    }
}
