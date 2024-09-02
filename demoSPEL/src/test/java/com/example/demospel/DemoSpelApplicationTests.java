package com.example.demospel;

import com.example.demospel.model.Element;
import com.example.demospel.model.Form;
import com.example.demospel.model.Screen;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class DemoSpelApplicationTests {

    @Test
    void contextLoads() {

        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        Student student = new Student("Admin", "John");

        // Set the object in the context
//        context.setVariable("student", student);
        context.setVariable("student.role", student.getRole());
        context.setVariable("student.name", student.getName());

//        context.lookupVariable()

        // Evaluate the expression with the object variable
        boolean bResult = parser.parseExpression("#student.role=='Admin' and #student.name=='John'").getValue(context, Boolean.class);

        Assertions.assertEquals(bResult, true);

    }

    @Test
    void testScreen() {


// Create Element objects
        Element element1 = new Element("Element 1", "text", "E1", "none", "Sample Value 1");
        Element element2 = new Element("Element 2", "number", "E2", "none", "123");

// Create a Map to store the Elements
        Map<String, Element> elementsMap = new HashMap<>();
        elementsMap.put(element1.getCode(), element1);
        elementsMap.put(element2.getCode(), element2);

// Create a Form object and add the Elements to it
        Form form = new Form("Form 1", elementsMap);

// Create a Map to store the Forms
        Map<String, Form> formsMap = new HashMap<>();
        formsMap.put(form.getName(), form);

// Create a Screen object and add the Form to it
        Screen screen = new Screen("Sample Screen", formsMap);

        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        context.setVariable("screen", screen);

        boolean bResult = parser.parseExpression("#screen.forms['Form 1'].elements['E2'].name =='Element 3' ").getValue(context, Boolean.class);

        Assertions.assertEquals(bResult, true);


    }

}
