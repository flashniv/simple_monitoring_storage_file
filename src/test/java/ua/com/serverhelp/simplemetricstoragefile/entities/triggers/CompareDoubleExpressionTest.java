package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.*;

class CompareDoubleExpressionTest extends AbstractTest {

    @Test
    void getJSON() throws ExpressionException {
        CompareDoubleExpression compareDoubleExpression = new CompareDoubleExpression();
        compareDoubleExpression.setOperation("<");
        Expression<Double> arg1 = new ConstantDoubleExpression();
        Expression<Double> arg2 = new ConstantDoubleExpression();

        arg1.initialize("{\"value\":10.323123}");
        arg2.initialize("{\"value\":10.324123}");

        compareDoubleExpression.setArg1(arg1);
        compareDoubleExpression.setArg2(arg2);

        System.out.println(compareDoubleExpression.getJSON());
    }

    @Test
    void getValue() throws ExpressionException {
        CompareDoubleExpression compareDoubleExpression = new CompareDoubleExpression();
        compareDoubleExpression.setOperation("<");
        Expression<Double> arg1 = new ConstantDoubleExpression();
        Expression<Double> arg2 = new ConstantDoubleExpression();

        arg1.initialize("{\"value\":10.323123}");
        arg2.initialize("{\"value\":10.324123}");

        compareDoubleExpression.setArg1(arg1);
        compareDoubleExpression.setArg2(arg2);
        Assertions.assertTrue(compareDoubleExpression.getValue());

        compareDoubleExpression.setArg1(new ConstantDoubleExpression(0.5));
        compareDoubleExpression.setArg2(new ConstantDoubleExpression(0.1));
        Assertions.assertFalse(compareDoubleExpression.getValue());
    }

    @Test
    void getNewInstanceByJSON() throws ExpressionException {
        CompareDoubleExpression compareDoubleExpression = new CompareDoubleExpression();

        compareDoubleExpression.initialize("{" +
                "\"operation\":\"<\"," +
                "\"arg2\":{" +
                "\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ConstantDoubleExpression\"," +
                "\"parameters\":{\"value\":10.324123}}," +
                "\"arg1\":{" +
                "\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ConstantDoubleExpression\"," +
                "\"parameters\":{\"value\":10.323123}}" +
                "}");

        Assertions.assertTrue(compareDoubleExpression.getValue());
    }

    @Test
    void getJSONForBoolean() throws Exception {
        CompareDoubleExpression compareDoubleExpression = new CompareDoubleExpression();
        compareDoubleExpression.setOperation("<");
        Expression<Double> arg1 = new ConstantDoubleExpression(0.5);
        Expression<Double> arg2 = new ReadLastValueOfMetricExpression("test.stage.db.booleanitem1", "{}", dirName);

        compareDoubleExpression.setArg1(arg1);
        compareDoubleExpression.setArg2(arg2);

        System.out.println(compareDoubleExpression.getJSON());
    }
}