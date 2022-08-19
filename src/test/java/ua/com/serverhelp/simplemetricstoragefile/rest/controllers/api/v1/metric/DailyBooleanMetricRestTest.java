package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric;

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
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AutoConfigureMockMvc
@WithMockUser(username = "specuser", authorities = {"Metrics"})
class DailyBooleanMetricRestTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAddEvent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/apiv1/metric/dailyboolean/")
                        .param("path", "test.stage.db.item1")
                        .param("value", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("Success"));
        Map<String, List<DataElement>> map = memoryMetricsQueue.getFormattedEvents();
        Assertions.assertEquals(1, map.size());

        List<DataElement> dataElements = map.get("test.stage.db.item1{}");

        fileDriver.writeMetric("test.stage.db.item1{}", dataElements);

        Assertions.assertNotNull(dataElements);
        Assertions.assertEquals(1, dataElements.size());

        DataElement dataElement = dataElements.get(0);
        Assertions.assertEquals(1.0, dataElement.getValue());

        List<Trigger> triggers = triggerRepository.findAll();
        for (Trigger trigger : triggers) {
            Assertions.assertTrue(trigger.checkTrigger());
        }
        //Check failed Boolean metric
        mockMvc.perform(MockMvcRequestBuilders.get("/apiv1/metric/dailyboolean/")
                        .param("path", "test.stage.db.item1")
                        .param("triggerName", "Boolean trigger %s")
                        .param("value", "false")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("Success"));
        map = memoryMetricsQueue.getFormattedEvents();
        Assertions.assertEquals(1, map.size());

        dataElements = map.get("test.stage.db.item1{}");

        fileDriver.writeMetric("test.stage.db.item1{}", dataElements);
        Optional<Trigger> booleanTrigger = triggerRepository.findById("fed4e1efd1dd8c99a702d8f213358c7a");
        Assertions.assertTrue(booleanTrigger.isPresent());
        Assertions.assertFalse(booleanTrigger.get().checkTrigger());

        //Check failed daily metric
        File file = new File(dirName);
        Assertions.assertTrue(deleteDirectory(file));
        DataElement dataElement1 = new DataElement();
        dataElement1.setTimestamp(Instant.now().minus(25, ChronoUnit.HOURS).getEpochSecond());
        dataElement1.setValue(1.0);
        fileDriver.writeMetric("test.stage.db.item1{}", List.of(dataElement1));

        Optional<Trigger> dailyTrigger = triggerRepository.findById("b5087188fa671f497f2d9d65716976f6");
        Assertions.assertTrue(dailyTrigger.isPresent());
        Assertions.assertFalse(dailyTrigger.get().checkTrigger());

    }
}