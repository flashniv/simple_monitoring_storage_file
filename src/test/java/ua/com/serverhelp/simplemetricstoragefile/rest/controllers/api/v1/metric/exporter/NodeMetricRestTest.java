package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric.exporter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.DigestUtils;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AutoConfigureMockMvc
@WithMockUser(username = "specuser", authorities = {"Metrics"})
class NodeMetricRestTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private NodeMetricRest nodeMetricRest;

    @BeforeEach
    void setUp2() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream debianInput = classLoader.getResourceAsStream("metrics.bin");
        Assertions.assertNotNull(debianInput);
        byte[] metrics = debianInput.readAllBytes();

        mockMvc.perform(MockMvcRequestBuilders.post("/apiv1/metric/exporter/node/")
                                .header("X-Project", "testproj")
                                .header("X-Hostname", "debian")
                                .content(metrics)
                        //.contentType(MediaType.ALL_VALUE)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("Success"));
        //Click
        InputStream clickInput = classLoader.getResourceAsStream("click.bin");
        Assertions.assertNotNull(clickInput);
        byte[] clickMetrics = clickInput.readAllBytes();

        mockMvc.perform(MockMvcRequestBuilders.post("/apiv1/metric/exporter/node/")
                                .header("X-Project", "testproj")
                                .header("X-Hostname", "click")
                                .content(clickMetrics)
                        //.contentType(MediaType.ALL_VALUE)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("Success"));

        nodeMetricRest.processItems();
    }

    @Test
    void receiveData() {
        Map<String, List<DataElement>> map = memoryMetricsQueue.getFormattedEvents();
        Map<String, List<DataElement>> debianMap = map.entrySet().stream()
                .filter(stringListEntry -> stringListEntry.getKey().contains("debian"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Assertions.assertEquals(53, debianMap.size());

        List<DataElement> dataElements = map.get("exporter.testproj.debian.node.cpu_seconds_total{\"mode\":\"user\",\"cpu\":\"3\"}");
        Assertions.assertNotNull(dataElements);
        Assertions.assertEquals(1, dataElements.size());

        DataElement dataElement = dataElements.get(0);
        Assertions.assertEquals(0.45, dataElement.getValue());

        Map<String, List<DataElement>> clickMap = map.entrySet().stream()
                .filter(stringListEntry -> stringListEntry.getKey().contains("click"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Assertions.assertEquals(27, clickMap.size());
    }

    @Test
    void checkLATrigger() throws Exception {
        cron.storeMetrics();

        Optional<Trigger> optionalLATrigger = triggerRepository.findById(DigestUtils.md5DigestAsHex("exporter.testproj.debian.node.load15{}".getBytes()));
        Assertions.assertTrue(optionalLATrigger.isPresent());
        Trigger laTrigger = optionalLATrigger.get();
        Assertions.assertTrue(laTrigger.checkTrigger());
        //else
        fileDriver.writeMetric("exporter.testproj.debian.node.load15{}", List.of(new DataElement(Instant.now().getEpochSecond() + 100, 10.0)));
        Assertions.assertFalse(laTrigger.checkTrigger());
    }

    @Test
    void checkUnreachableTrigger() throws Exception {
        Optional<Trigger> optionalLATrigger = triggerRepository.findById(DigestUtils.md5DigestAsHex("exporter.testproj.debian.node.load1515min{}".getBytes()));
        Assertions.assertTrue(optionalLATrigger.isPresent());
        Trigger unreachableTrigger = optionalLATrigger.get();
        Assertions.assertThrows(Exception.class, () -> unreachableTrigger.checkTrigger());
        //else
        fileDriver.writeMetric("exporter.testproj.debian.node.load15{}", List.of(new DataElement(Instant.now().getEpochSecond() - 1000, 1.0)));
        Assertions.assertFalse(unreachableTrigger.checkTrigger());
        fileDriver.writeMetric("exporter.testproj.debian.node.load15{}", List.of(new DataElement(Instant.now().getEpochSecond() - 100, 2.0)));
        Assertions.assertTrue(unreachableTrigger.checkTrigger());
    }

    @Test
    void checkDFTrigger() throws Exception {
        Optional<Trigger> optionalDFTrigger = triggerRepository.findById(DigestUtils.md5DigestAsHex("exporter.testproj.debian.node.filesystem_size_bytes{\"device\":\"/dev/vda1\",\"fstype\":\"vfat\",\"mountpoint\":\"/boot/efi\"}".getBytes()));
        Assertions.assertTrue(optionalDFTrigger.isPresent());
        Trigger dfTrigger = optionalDFTrigger.get();
        Assertions.assertThrows(Exception.class, dfTrigger::checkTrigger);
        //else
        fileDriver.writeMetric("exporter.testproj.debian.node.filesystem_size_bytes{\"device\":\"/dev/vda1\",\"fstype\":\"vfat\",\"mountpoint\":\"/boot/efi\"}", List.of(new DataElement(Instant.now().getEpochSecond() - 100, 5.36576E8)));
        fileDriver.writeMetric("exporter.testproj.debian.node.filesystem_avail_bytes{\"device\":\"/dev/vda1\",\"fstype\":\"vfat\",\"mountpoint\":\"/boot/efi\"}", List.of(new DataElement(Instant.now().getEpochSecond() - 100, 5.32963328E8)));
        Assertions.assertTrue(dfTrigger.checkTrigger());
        fileDriver.writeMetric("exporter.testproj.debian.node.filesystem_size_bytes{\"device\":\"/dev/vda1\",\"fstype\":\"vfat\",\"mountpoint\":\"/boot/efi\"}", List.of(new DataElement(Instant.now().getEpochSecond(), 5.36576E8)));
        fileDriver.writeMetric("exporter.testproj.debian.node.filesystem_avail_bytes{\"device\":\"/dev/vda1\",\"fstype\":\"vfat\",\"mountpoint\":\"/boot/efi\"}", List.of(new DataElement(Instant.now().getEpochSecond(), 5.36576E7)));
        Assertions.assertFalse(dfTrigger.checkTrigger());
        //Check click DF trigger
        Optional<Trigger> optionalClickDFTrigger = triggerRepository.findById(DigestUtils.md5DigestAsHex("exporter.testproj.click.node.filesystem_size{\"device\":\"/dev/md3\",\"fstype\":\"ext4\",\"mountpoint\":\"/\"}".getBytes()));
        Assertions.assertTrue(optionalClickDFTrigger.isPresent());
        Trigger clickDFTrigger=optionalClickDFTrigger.get();
        Assertions.assertThrows(Exception.class, clickDFTrigger::checkTrigger);
        fileDriver.writeMetric("exporter.testproj.click.node.filesystem_size{\"device\":\"/dev/md3\",\"fstype\":\"ext4\",\"mountpoint\":\"/\"}", List.of(new DataElement(Instant.now().getEpochSecond() - 100, 5.36576E8)));
        fileDriver.writeMetric("exporter.testproj.click.node.filesystem_avail{\"device\":\"/dev/md3\",\"fstype\":\"ext4\",\"mountpoint\":\"/\"}", List.of(new DataElement(Instant.now().getEpochSecond() - 100, 5.32963328E8)));
        Assertions.assertTrue(clickDFTrigger.checkTrigger());
        fileDriver.writeMetric("exporter.testproj.click.node.filesystem_size{\"device\":\"/dev/md3\",\"fstype\":\"ext4\",\"mountpoint\":\"/\"}", List.of(new DataElement(Instant.now().getEpochSecond(), 5.36576E8)));
        fileDriver.writeMetric("exporter.testproj.click.node.filesystem_avail{\"device\":\"/dev/md3\",\"fstype\":\"ext4\",\"mountpoint\":\"/\"}", List.of(new DataElement(Instant.now().getEpochSecond(), 5.36576E7)));
        Assertions.assertFalse(clickDFTrigger.checkTrigger());

    }

    @Test
    void checkDFInodesTrigger() throws Exception {
        Optional<Trigger> optionalDFInodesTrigger = triggerRepository.findById(DigestUtils.md5DigestAsHex("exporter.testproj.debian.node.filesystem_files{\"device\":\"/dev/vda2\",\"fstype\":\"ext4\",\"mountpoint\":\"/\"}".getBytes()));
        Assertions.assertTrue(optionalDFInodesTrigger.isPresent());
        Trigger dfInodesTrigger = optionalDFInodesTrigger.get();
        Assertions.assertThrows(Exception.class, dfInodesTrigger::checkTrigger);
        //else
        fileDriver.writeMetric("exporter.testproj.debian.node.filesystem_files{\"device\":\"/dev/vda2\",\"fstype\":\"ext4\",\"mountpoint\":\"/\"}", List.of(new DataElement(Instant.now().getEpochSecond() - 100, 5.36576E8)));
        fileDriver.writeMetric("exporter.testproj.debian.node.filesystem_files_free{\"device\":\"/dev/vda2\",\"fstype\":\"ext4\",\"mountpoint\":\"/\"}", List.of(new DataElement(Instant.now().getEpochSecond() - 100, 5.32963328E8)));
        Assertions.assertTrue(dfInodesTrigger.checkTrigger());
        fileDriver.writeMetric("exporter.testproj.debian.node.filesystem_files{\"device\":\"/dev/vda2\",\"fstype\":\"ext4\",\"mountpoint\":\"/\"}", List.of(new DataElement(Instant.now().getEpochSecond(), 5.36576E8)));
        fileDriver.writeMetric("exporter.testproj.debian.node.filesystem_files_free{\"device\":\"/dev/vda2\",\"fstype\":\"ext4\",\"mountpoint\":\"/\"}", List.of(new DataElement(Instant.now().getEpochSecond(), 5.36576E7)));
        Assertions.assertFalse(dfInodesTrigger.checkTrigger());
    }
}