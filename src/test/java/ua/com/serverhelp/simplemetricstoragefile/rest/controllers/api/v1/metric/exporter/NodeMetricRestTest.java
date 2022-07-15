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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AutoConfigureMockMvc
@WithMockUser(username = "specuser", authorities = {"Metrics"})
class NodeMetricRestTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private NodeMetricRest nodeMetricRest;

    @BeforeEach
    void setUp2() throws Exception{
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream fileInput = classLoader.getResourceAsStream("metrics.bin");
        Assertions.assertNotNull(fileInput);
        byte[] metrics = fileInput.readAllBytes();

        mockMvc.perform(MockMvcRequestBuilders.post("/apiv1/metric/exporter/node/")
                                .header("X-Project", "testproj")
                                .header("X-Hostname", "debian")
                                .content(metrics)
                        //.contentType(MediaType.ALL_VALUE)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("Success"));

        nodeMetricRest.processItems();
    }

    @Test
    void receiveData() throws Exception {
        Map<String, List<DataElement>> map = memoryMetricsQueue.getFormattedEvents();
        Assertions.assertEquals(54, map.size());

        List<DataElement> dataElements = map.get("exporter.testproj.debian.node.cpu_seconds_total{\"mode\":\"user\",\"cpu\":\"3\"}");
        Assertions.assertNotNull(dataElements);
        Assertions.assertEquals(1, dataElements.size());

        DataElement dataElement = dataElements.get(0);
        Assertions.assertEquals(0.45, dataElement.getValue());
    }

    @Test
    void checkLATrigger() throws Exception{
        cron.storeMetrics();

        Optional<Trigger> optionalLATrigger=triggerRepository.findById(DigestUtils.md5DigestAsHex("exporter.testproj.debian.node.load15{}".getBytes()));
        Assertions.assertTrue(optionalLATrigger.isPresent());
        Trigger laTrigger=optionalLATrigger.get();
        Assertions.assertTrue(laTrigger.checkTrigger());
    }
}