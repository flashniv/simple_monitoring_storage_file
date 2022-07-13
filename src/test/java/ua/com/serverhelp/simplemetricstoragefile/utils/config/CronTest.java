package ua.com.serverhelp.simplemetricstoragefile.utils.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.TriggerStatus;
import ua.com.serverhelp.simplemetricstoragefile.filedriver.FileDriver;
import ua.com.serverhelp.simplemetricstoragefile.queue.MemoryMetricsQueue;
import ua.com.serverhelp.simplemetricstoragefile.storage.TriggerRepository;

import java.io.File;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CronTest {
    @Autowired
    private Cron cron;
    @Autowired
    private TriggerRepository triggerRepository;
    @Autowired
    private MemoryMetricsQueue memoryMetricsQueue;
    @Autowired
    private FileDriver fileDriver;
    @Value("${metric-storage.metrics-directory}")
    private String dirName;

    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    @AfterEach
    void tearDown() {
        File file = new File(dirName);
        Assertions.assertTrue(deleteDirectory(file));
    }

    @Test
    void checkTriggers() {
        String id=DigestUtils.md5DigestAsHex("test.stage.db.booleanitem1{}".getBytes());
        Trigger trigger = new Trigger();

        trigger.setId(id);
        trigger.setName("Test trigger");
        trigger.setConf("{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.CompareDoubleExpression\",\"parameters\":{\"operation\":\"<\",\"arg2\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.ReadLastValueOfMetricExpression\",\"parameters\":{\"metricName\":\"test.stage.db.booleanitem1\",\"parameterGroup\":\"{}\"}},\"arg1\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.ConstantDoubleExpression\",\"parameters\":{\"value\":0.5}}}}");

        triggerRepository.save(trigger);
        //check new trigger state
        Assertions.assertEquals(TriggerStatus.UNCHECKED, trigger.getLastStatus());

        //Check trigger without data
        cron.checkTriggers();

        Optional<Trigger> optionalTrigger=triggerRepository.findById(id);
        Assertions.assertTrue(optionalTrigger.isPresent());
        Assertions.assertEquals(TriggerStatus.ERROR, optionalTrigger.get().getLastStatus());

        //check trigger to error
        Event event=new Event("test.stage.db.booleanitem1","{}", Instant.now().getEpochSecond(), 0.0);
        memoryMetricsQueue.putEvent(event);
        cron.storeMetrics();
        cron.checkTriggers();

        optionalTrigger=triggerRepository.findById(id);
        Assertions.assertTrue(optionalTrigger.isPresent());
        Assertions.assertEquals(TriggerStatus.FAILED, optionalTrigger.get().getLastStatus());

        //check trigger to ok
        event=new Event("test.stage.db.booleanitem1","{}", Instant.now().getEpochSecond(), 1.0);
        memoryMetricsQueue.putEvent(event);
        cron.storeMetrics();
        cron.checkTriggers();

        optionalTrigger=triggerRepository.findById(id);
        Assertions.assertTrue(optionalTrigger.isPresent());
        Assertions.assertEquals(TriggerStatus.OK, optionalTrigger.get().getLastStatus());
    }
}