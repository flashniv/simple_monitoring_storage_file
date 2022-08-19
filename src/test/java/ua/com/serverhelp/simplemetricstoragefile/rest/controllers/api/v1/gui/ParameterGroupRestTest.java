package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.gui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.entities.metric.Metric;
import ua.com.serverhelp.simplemetricstoragefile.entities.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AutoConfigureMockMvc
@WithMockUser(username = "specuser", authorities = {"GUI"})
class ParameterGroupRestTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getEventsByParameterGroup() throws Exception {
        for (int i = 0; i < 20; i++) {
            memoryMetricsQueue.putEvent(new Event("exporter.testproj.debian.node.filesystem_avail_bytes", "{\"device\":\"/dev/vda1\",\"fstype\":\"vfat\",\"mountpoint\":\"/boot/efi\"}", Instant.now().getEpochSecond() - i * 10, Math.random()));
        }
        memoryMetricsQueue.putEvent(new Event("exporter.testproj.debian.node.filesystem_avail_bytes", "{\"device\":\"/dev/vda2\",\"fstype\":\"ext4\",\"mountpoint\":\"/\"}", Instant.now().getEpochSecond(), 0.0));
        Map<String, List<DataElement>> map = memoryMetricsQueue.getFormattedEvents();
        for (Map.Entry<String, List<DataElement>> entry : map.entrySet()) {
            fileDriver.writeMetric(entry.getKey(), entry.getValue());
        }

        Optional<Metric> optionalMetric = metricRepository.findById("exporter.testproj.debian.node.filesystem_avail_bytes");
        Assertions.assertTrue(optionalMetric.isPresent());
        Optional<ParameterGroup> optionalParameterGroup = parameterGroupRepository.findByMetricAndJson(optionalMetric.get(), "{\"device\":\"/dev/vda1\",\"fstype\":\"vfat\",\"mountpoint\":\"/boot/efi\"}");
        Assertions.assertTrue(optionalParameterGroup.isPresent());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/parameterGroup/" + optionalParameterGroup.get().getId() + "/events")
                        .param("begin", Instant.now().minus(10, ChronoUnit.HOURS).toString())
                        .param("end", Instant.now().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].timestamp").isNumber());

    }
}