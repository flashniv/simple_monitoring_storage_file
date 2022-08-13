package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric.exporter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@AutoConfigureMockMvc
@WithMockUser(username = "specuser", authorities = {"Metrics"})
class ProcessMetricRestTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProcessMetricRest processMetricRest;

    void setUp2() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream fileInput = classLoader.getResourceAsStream("proc.bin");
        Assertions.assertNotNull(fileInput);
        byte[] metrics = fileInput.readAllBytes();

        mockMvc.perform(MockMvcRequestBuilders.post("/apiv1/metric/exporter/process/")
                                .header("X-Project", "testproj")
                                .header("X-Hostname", "debian")
                                .content(metrics)
                        //.contentType(MediaType.ALL_VALUE)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("Success"));

        processMetricRest.processItems();
    }

    @Test
    void receiveData() throws Exception {
        setUp2();
        Map<String, List<DataElement>> map = memoryMetricsQueue.getFormattedEvents();

        Assertions.assertEquals(68, map.size());
        Assertions.assertTrue(map.containsKey("exporter.testproj.process.debian.namedprocess_namegroup_num_procs{\"groupname\":\"octologsd\"}"));
        List<DataElement> dataElements = map.get("exporter.testproj.process.debian.namedprocess_namegroup_num_procs{\"groupname\":\"octologsd\"}");
        Assertions.assertEquals(1, dataElements.size());
        DataElement dataElement = dataElements.get(0);
        Assertions.assertEquals(1.0, dataElement.getValue());
    }

    @Test
    void createTriggerIfNotExist() throws Exception {
        setUp2();
        cron.storeMetrics();
        List<Trigger> triggers = triggerRepository.findAll();
        for (Trigger trigger : triggers) {
            Assertions.assertTrue(trigger.checkTrigger());
        }
    }
}