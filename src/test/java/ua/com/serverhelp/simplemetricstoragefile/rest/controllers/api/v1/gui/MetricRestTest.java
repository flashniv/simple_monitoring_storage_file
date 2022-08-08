package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.gui;

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
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@AutoConfigureMockMvc
@WithMockUser(username = "specuser", authorities = {"GUI"})
class MetricRestTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllMetrics() throws Exception {
        memoryMetricsQueue.putEvent(new Event("exporter.testproj.debian.node.filesystem_avail_bytes", "{\"device\":\"/dev/vda1\",\"fstype\":\"vfat\",\"mountpoint\":\"/boot/efi\"}", Instant.now().getEpochSecond(), 0.0));
        memoryMetricsQueue.getFormattedEvents();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/metric/")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("[{\"path\":\"exporter.testproj.debian.node.filesystem_avail_bytes\"}]"));
    }

    @Test
    void getParameterGroupsByMetric() throws Exception {
        memoryMetricsQueue.putEvent(new Event("exporter.testproj.debian.node.filesystem_avail_bytes", "{\"device\":\"/dev/vda1\",\"fstype\":\"vfat\",\"mountpoint\":\"/boot/efi\"}", Instant.now().getEpochSecond(), 0.0));
        memoryMetricsQueue.putEvent(new Event("exporter.testproj.debian.node.filesystem_avail_bytes", "{\"device\":\"/dev/vda2\",\"fstype\":\"ext4\",\"mountpoint\":\"/\"}", Instant.now().getEpochSecond(), 0.0));
        memoryMetricsQueue.getFormattedEvents();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/metric/exporter.testproj.debian.node.filesystem_avail_bytes/parameterGroups")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").isNumber());
    }

    /*@Test
    void getEventsByMetric() throws Exception {
        for (int i = 0; i < 20; i++) {
            memoryMetricsQueue.putEvent(new Event("exporter.testproj.debian.node.filesystem_avail_bytes", "{\"device\":\"/dev/vda1\",\"fstype\":\"vfat\",\"mountpoint\":\"/boot/efi\"}", Instant.now().getEpochSecond() - i * 10, Math.random()));
            memoryMetricsQueue.putEvent(new Event("exporter.testproj.debian.node.filesystem_avail_bytes", "{\"device\":\"/dev/vda2\",\"fstype\":\"ext4\",\"mountpoint\":\"/\"}", Instant.now().getEpochSecond()-i*10, Math.random()));
        }
        Map<String, List<DataElement>> map = memoryMetricsQueue.getFormattedEvents();
        for (Map.Entry<String, List<DataElement>> entry : map.entrySet()) {
            fileDriver.writeMetric(entry.getKey(), entry.getValue());
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/metric/exporter.testproj.debian.node.filesystem_avail_bytes/events")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].timestamp").isNumber());
    }*/
}