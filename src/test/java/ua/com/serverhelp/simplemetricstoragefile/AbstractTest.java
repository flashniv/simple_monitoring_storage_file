package ua.com.serverhelp.simplemetricstoragefile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import ua.com.serverhelp.simplemetricstoragefile.filedriver.FileDriver;
import ua.com.serverhelp.simplemetricstoragefile.queue.MemoryMetricsQueue;
import ua.com.serverhelp.simplemetricstoragefile.storage.*;
import ua.com.serverhelp.simplemetricstoragefile.utils.config.Cron;

import java.io.File;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractTest {
    @Autowired
    protected Cron cron;
    @Autowired
    protected TriggerRepository triggerRepository;
    @Autowired
    protected MetricRepository metricRepository;
    @Autowired
    protected ParameterGroupRepository parameterGroupRepository;
    @Autowired
    protected MemoryMetricsQueue memoryMetricsQueue;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected FileDriver fileDriver;
    @Value("${metric-storage.metrics-directory}")
    protected String dirName;

    public boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    @BeforeEach
    public void setUp() {
        File file = new File(dirName);
        deleteDirectory(file);
        parameterGroupRepository.deleteAll();
        metricRepository.deleteAll();
        triggerRepository.deleteAll();
    }

}
