package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ConstantDoubleExpression;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.MathDoubleExpression;

class MathDoubleExpressionTest extends AbstractTest {

    @Test
    void getJSON() throws Exception {
        MathDoubleExpression mathDoubleExpression = new MathDoubleExpression(new ConstantDoubleExpression(1.0), new ConstantDoubleExpression(2.0), "+");
        System.out.println(mathDoubleExpression.getJSON());
    }

    @Test
    void getValue() throws Exception {
        MathDoubleExpression mathDoubleExpression = new MathDoubleExpression(new ConstantDoubleExpression(1.0), new ConstantDoubleExpression(2.0), "+");
        Assertions.assertEquals(3.0, mathDoubleExpression.getValue());
    }

    @Test
    void initialize() throws Exception {
        MathDoubleExpression mathDoubleExpression = new MathDoubleExpression();

        mathDoubleExpression.initialize("{\"arg2\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ConstantDoubleExpression\",\"parameters\":{\"value\":2}},\"arg1\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ConstantDoubleExpression\",\"parameters\":{\"value\":1}},\"operation\":\"+\"}\n");
        Assertions.assertEquals(3.0, mathDoubleExpression.getValue());
    }
}