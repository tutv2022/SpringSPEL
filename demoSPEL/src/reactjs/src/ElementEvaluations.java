#{JSON(financialReport).prop("isReturn").boolValue() && JSON(financialReport).prop("prevTask").stringValue() == "verifyFinancialReport"}

public class ScreenValidator {

    public static boolean validateScreen(Screen screen) {
        for (Form form : screen.getForms().values()) {
            for (Element element : form.getElements().values()) {
                if (!ElementValidator.validateElementValue(element)) {
                    return false;
                }
            }
        }
        return true;
    }
}

public class ElementValidator {

    public static boolean validateElementValue(Element element) {
        String type = element.getType();
        String value = element.getValue();

        if ("Integer".equals(type)) {
            try {
                Integer.parseInt(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        // Add more type checks here if needed

        return true;
    }
}

import com.example.demospel.model.Element;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class ElementConditionEvaluator {

    public static Object evaluateCondition(Element element) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext(element);
        return parser.parseExpression(element.getCondition()).getValue(context);
    }
}


import com.example.demospel.model.Screen;
import com.example.demospel.model.Form;
import com.example.demospel.model.Element;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class ScreenConditionEvaluator {

    public static void evaluateAllConditions(Screen screen) {
        ExpressionParser parser = new SpelExpressionParser();

        for (Form form : screen.getForms().values()) {
            for (Element element : form.getElements().values()) {
                StandardEvaluationContext context = new StandardEvaluationContext(element);
                Object result = parser.parseExpression(element.getCondition()).getValue(context);
                System.out.println("Result of evaluating condition for element " + element.getName() + ": " + result);
            }
        }
    }
}

public class ScreenComparator {

    public static boolean compareScreens(Screen screen1, Screen screen2) {
        Map<String, Form> forms1 = screen1.getForms();
        Map<String, Form> forms2 = screen2.getForms();

        for (String formKey : forms1.keySet()) {
            if (!forms2.containsKey(formKey)) {
                return false;
            } else {
                Map<String, Element> elements1 = forms1.get(formKey).getElements();
                Map<String, Element> elements2 = forms2.get(formKey).getElements();

                for (String elementKey : elements1.keySet()) {
                    if (!elements2.containsKey(elementKey)) {
                        return false;
                    } else if (!elements1.get(elementKey).getValue().equals(elements2.get(elementKey).getValue())) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}


import React, { useState } from 'react';
import jexl from 'jexl';

function ComplexExpressionEvaluator() {
    const [result, setResult] = useState(null);

    // Define your objects
    const screen = {
        forms: {
            'Form 1': {
                elements: {
                    'E2': {
                        name: 'Element 3'
                    }
                }
            }
        }
    };

    const user = {
        role: 'Admin',
        name: 'John'
    };

    const config = {
        isEnabled: true,
        maxAttempts: 3
    };

    // Preprocess the expression to remove '#'
    const preprocessExpression = (expression) => {
        return expression.replace(/#/g, '');
    };

    const handleEvaluate = async () => {
        // Expression returned from backend
        let expression = "#screen.forms['Form 1'].elements['E2'].name == 'Element 3' && #user.role == 'Admin' && #config.isEnabled == true";

        // Preprocess the expression to remove '#'
        expression = preprocessExpression(expression);

        try {
            // Evaluate the expression using jexl
            const evaluationResult = await jexl.eval(expression, { screen, user, config });
            setResult(evaluationResult ? 'True' : 'False');
        } catch (error) {
            setResult('Error: Invalid expression');
        }
    };

    return (
        <div>
            <h2>Complex Expression Evaluator</h2>
            <button onClick={handleEvaluate}>Evaluate</button>
            {result !== null && (
                <div>
                    <h3>Result: {result}</h3>
                </div>
            )}
        </div>
    );
}

export default ComplexExpressionEvaluator;


import React, { useState, useEffect } from 'react';
import axios from 'axios';

function Screen() {
  const [screen, setScreen] = useState(null);

  useEffect(() => {
    axios.get('http://localhost:8080/screen')
      .then(response => {
        setScreen(response.data);
      })
      .catch(error => {
        console.error('There was an error!', error);
      });
  }, []);

  if (!screen) {
    return <div>Loading...</div>;
  }

  return (
    <div>
      <h1>{screen.name}</h1>
      {Object.values(screen.forms).map((form, index) => (
        <div key={index}>
          <h2>{form.name}</h2>
          {Object.values(form.elements).map((element, index) => (
            <div key={index}>
              <h3>{element.name}</h3>
              <p>Type: {element.type}</p>
              <p>Code: {element.code}</p>
              <p>Condition: {element.condition}</p>
              <p>Value: {element.value}</p>
            </div>
          ))}
        </div>
      ))}
    </div>
  );
}

export default Screen;


import React, { useState, useEffect } from 'react';
import jexl from 'jexl';

function DynamicForm() {
    const [form, setForm] = useState({
        name: "Sample Screen",
        forms: {
            "Form 1": {
                name: "Form 1",
                elements: {
                    "E1": {
                        name: "Element 1",
                        type: "text",
                        code: "E1",
                        condition: "#form.elements['E2'].name == 'Element 2'",
                        value: "Sample Value 1",
                        isVisible: true, // To track visibility
                    },
                    "E2": {
                        name: "Element 2",
                        type: "number",
                        code: "E2",
                        condition: "#form.elements['E2'].name == 'Element 2'",
                        value: "123",
                        isVisible: true, // To track visibility
                    }
                }
            }
        },
        status: "ACTIVE"
    });

    // Function to preprocess and evaluate conditions
    const evaluateConditions = () => {
        const updatedForm = { ...form };
        const elements = updatedForm.forms['Form 1'].elements;

        for (const key in elements) {
            if (elements.hasOwnProperty(key)) {
                const element = elements[key];

                // Preprocess the condition
                let condition = element.condition.replace(/#form/g, 'form.forms["Form 1"]');

                try {
                    // Evaluate the condition using jexl
                    const result = jexl.evalSync(condition, { form: updatedForm });
                    element.isVisible = result;
                } catch (error) {
                    console.error("Error evaluating condition:", error);
                }
            }
        }

        setForm(updatedForm);
    };

    // Function to handle input changes
    const handleInputChange = (elementCode, newValue) => {
        const updatedForm = { ...form };
        updatedForm.forms['Form 1'].elements[elementCode].value = newValue;
        setForm(updatedForm);
        evaluateConditions(); // Re-evaluate conditions after value changes
    };

    // Initial evaluation on component mount
    useEffect(() => {
        evaluateConditions();
    }, []);

    // Rendering form elements
    const renderElements = () => {
        const elements = form.forms['Form 1'].elements;
        return Object.keys(elements).map(key => {
            const element = elements[key];

            if (!element.isVisible) return null;

            return (
                <div key={key}>
                    <label>{element.name}</label>
                    <input
                        type={element.type}
                        value={element.value}
                        onChange={(e) => handleInputChange(element.code, e.target.value)}
                    />
                </div>
            );
        });
    };

    return (
        <div>
            <h2>{form.name}</h2>
            {renderElements()}
        </div>
    );
}

export default DynamicForm;

import React, { useState, useEffect } from 'react';
import jexl from 'jexl';

function DynamicForm() {
    const [form, setForm] = useState(null); // Initially, the form data is null
    const [loading, setLoading] = useState(true); // Loading state

    // Fetch data from REST API when the component mounts
    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await fetch('/api/form'); // Replace with your REST API endpoint
                const data = await response.json();
                
                // Add the isVisible property to each element
                const updatedData = { ...data };
                const elements = updatedData.forms['Form 1'].elements;
                
                for (const key in elements) {
                    if (elements.hasOwnProperty(key)) {
                        elements[key].isVisible = true; // Initialize all elements as visible
                    }
                }

                setForm(updatedData);
                evaluateConditions(updatedData); // Evaluate conditions after fetching data
                setLoading(false);
            } catch (error) {
                console.error("Error fetching form data:", error);
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    // Function to preprocess and evaluate conditions
    const evaluateConditions = (updatedForm) => {
        const elements = updatedForm.forms['Form 1'].elements;

        for (const key in elements) {
            if (elements.hasOwnProperty(key)) {
                const element = elements[key];

                // Preprocess the condition
                let condition = element.condition.replace(/#form/g, 'form.forms["Form 1"]');

                try {
                    // Evaluate the condition using jexl
                    const result = jexl.evalSync(condition, { form: updatedForm });
                    element.isVisible = result;
                } catch (error) {
                    console.error("Error evaluating condition:", error);
                }
            }
        }

        setForm({ ...updatedForm }); // Update the form state
    };

    // Function to handle input changes
    const handleInputChange = (elementCode, newValue) => {
        const updatedForm = { ...form };
        updatedForm.forms['Form 1'].elements[elementCode].value = newValue;
        setForm(updatedForm);
        evaluateConditions(updatedForm); // Re-evaluate conditions after value changes
    };

    // Rendering form elements
    const renderElements = () => {
        const elements = form.forms['Form 1'].elements;
        return Object.keys(elements).map(key => {
            const element = elements[key];

            if (!element.isVisible) return null;

            return (
                <div key={key}>
                    <label>{element.name}</label>
                    <input
                        type={element.type}
                        value={element.value}
                        onChange={(e) => handleInputChange(element.code, e.target.value)}
                    />
                </div>
            );
        });
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    if (!form) {
        return <div>Error loading form</div>;
    }

    return (
        <div>
            <h2>{form.name}</h2>
            {renderElements()}
        </div>
    );
}

export default DynamicForm;

