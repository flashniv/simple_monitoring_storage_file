package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.lang.reflect.InvocationTargetException;

class TriggerTest {
    private Trigger trigger;

    @BeforeEach
    void setUp() {
        trigger = new Trigger();
        trigger.setId(DigestUtils.md5DigestAsHex("test.stage.db.booleanitem1{}".getBytes()));
        trigger.setName("Test trigger");
        trigger.setConf("{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.CompareDoubleExpression\",\"parameters\":{\"operation\":\"<\",\"arg2\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.ConstantDoubleExpression\",\"parameters\":{\"value\":0.6}},\"arg1\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.ConstantDoubleExpression\",\"parameters\":{\"value\":0.5}}}}");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void checkTrigger() throws ClassNotFoundException, InvocationTargetException, ExpressionException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Assertions.assertTrue(trigger.checkTrigger());
    }
}