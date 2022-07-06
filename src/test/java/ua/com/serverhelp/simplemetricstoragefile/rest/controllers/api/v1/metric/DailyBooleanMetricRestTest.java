package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;
import ua.com.serverhelp.simplemetricstoragefile.queue.MemoryMetricsQueue;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DailyBooleanMetricRestTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemoryMetricsQueue memoryMetricsQueue;

    @Test
    void getAddEvent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/apiv1/metric/dailyboolean/")
                        .param("path","test.stage.db.item1")
                        .param("value", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("Success"));
        Map<String, List<DataElement>> map=memoryMetricsQueue.getFormattedEvents();
        Assertions.assertEquals(1, map.size());

        List<DataElement> dataElements=map.get("test.stage.db.item1{}");
        Assertions.assertNotNull(dataElements);
        Assertions.assertEquals(1, dataElements.size());

        DataElement dataElement=dataElements.get(0);
        Assertions.assertEquals(1.0, dataElement.getValue());
    }
}