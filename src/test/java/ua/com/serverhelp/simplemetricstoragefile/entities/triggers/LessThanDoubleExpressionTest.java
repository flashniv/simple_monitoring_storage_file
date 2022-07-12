package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LessThanDoubleExpressionTest {

    @Test
    void getJSON() throws ExpressionException {
        LessThanDoubleExpression lessThanDoubleExpression = new LessThanDoubleExpression();
        Expression<Double> arg1 = new ConstantDoubleExpression();
        Expression<Double> arg2 = new ConstantDoubleExpression();

        arg1.initialize("{\"value\":10.323123}");
        arg2.initialize("{\"value\":10.324123}");

        lessThanDoubleExpression.setArg1(arg1);
        lessThanDoubleExpression.setArg2(arg2);

        System.out.println(lessThanDoubleExpression.getJSON());
    }

    @Test
    void getValue() throws ExpressionException {
        LessThanDoubleExpression lessThanDoubleExpression = new LessThanDoubleExpression();
        Expression<Double> arg1 = new ConstantDoubleExpression();
        Expression<Double> arg2 = new ConstantDoubleExpression();

        arg1.initialize("{\"value\":10.323123}");
        arg2.initialize("{\"value\":10.324123}");

        lessThanDoubleExpression.setArg1(arg1);
        lessThanDoubleExpression.setArg2(arg2);
        Assertions.assertTrue(lessThanDoubleExpression.getValue());
    }

    @Test
    void getNewInstanceByJSON() throws ExpressionException {
        LessThanDoubleExpression lessThanDoubleExpression = new LessThanDoubleExpression();

        lessThanDoubleExpression.initialize("{" +
                "\"arg2\":{" +
                "\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.ConstantDoubleExpression\"," +
                "\"parameters\":{\"value\":10.324123}}," +
                "\"arg1\":{" +
                "\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.ConstantDoubleExpression\"," +
                "\"parameters\":{\"value\":10.323123}}" +
                "}");

        Assertions.assertTrue(lessThanDoubleExpression.getValue());
    }

    @Test
    void getJSONForBoolean() throws Exception{
        LessThanDoubleExpression lessThanDoubleExpression = new LessThanDoubleExpression();
        Expression<Double> arg1 = new ConstantDoubleExpression(0.5);
        Expression<Double> arg2 = new ReadLastValueOfMetricExpression("test.stage.db.booleanitem1","{}");

        lessThanDoubleExpression.setArg1(arg1);
        lessThanDoubleExpression.setArg2(arg2);

        System.out.println(lessThanDoubleExpression.getJSON());
    }
}