package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric.exporter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;
import ua.com.serverhelp.simplemetricstoragefile.queue.MemoryMetricsQueue;
import ua.com.serverhelp.simplemetricstoragefile.storage.MetricRepository;
import ua.com.serverhelp.simplemetricstoragefile.storage.ParameterGroupRepository;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "specuser", authorities = {"Metrics"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NodeMetricRestTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemoryMetricsQueue memoryMetricsQueue;
    @Autowired
    private NodeMetricRest nodeMetricRest;
    @Autowired
    private ParameterGroupRepository parameterGroupRepository;
    @Autowired
    private MetricRepository metricRepository;

    @AfterEach
    void tearDown() {
        parameterGroupRepository.deleteAll();
        metricRepository.deleteAll();
    }

    @Test
    void receiveData() throws Exception {
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

        Map<String, List<DataElement>> map = memoryMetricsQueue.getFormattedEvents();
        Assertions.assertEquals(1087, map.size());

        List<DataElement> dataElements = map.get("exporter.testproj.debian.node.schedstat_waiting_seconds_total{\"cpu\":\"1\"}");
        Assertions.assertNotNull(dataElements);
        Assertions.assertEquals(1, dataElements.size());

        DataElement dataElement = dataElements.get(0);
        Assertions.assertEquals(0.402071695, dataElement.getValue());
    }
}