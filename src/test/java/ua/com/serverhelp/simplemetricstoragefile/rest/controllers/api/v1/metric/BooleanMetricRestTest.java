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
import org.springframework.util.DigestUtils;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AutoConfigureMockMvc
@WithMockUser(username = "specuser", authorities = {"Metrics"})
class BooleanMetricRestTest extends AbstractTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAddEvent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/apiv1/metric/boolean/")
                        .param("path", "test.stage.db.booleanitem1")
                        .param("triggerName", "Boolean trigger %s receive false")
                        .param("value", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("Success"));
        Map<String, List<DataElement>> map = memoryMetricsQueue.getFormattedEvents();
        Assertions.assertEquals(1, map.size());

        List<DataElement> dataElements = map.get("test.stage.db.booleanitem1{}");
        Assertions.assertNotNull(dataElements);
        Assertions.assertEquals(1, dataElements.size());

        DataElement dataElement = dataElements.get(0);
        Assertions.assertEquals(1.0, dataElement.getValue());

        Optional<Trigger> optionalTrigger = triggerRepository.findById(DigestUtils.md5DigestAsHex("test.stage.db.booleanitem1{}".getBytes()));
        Assertions.assertTrue(optionalTrigger.isPresent());
        Trigger trigger = optionalTrigger.get();
        System.out.println(trigger.getConf());
    }
}