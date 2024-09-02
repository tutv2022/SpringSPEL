import React, { useState } from "react";
import jexl from "jexl";

function ComplexExpressionEvaluator() {
  const [result, setResult] = useState(null);

  // Define the complex object structure
  const screen = {
    forms: {
      "Form 1": {
        elements: {
          E2: {
            name: "Element 3",
          },
          E3: {
            name: "Element E3",
          },
        },
      },
    },
  };

  const user = {
    role: "Admin",
    name: "John",
  };

  const config = {
    isEnabled: true,
    maxAttempts: 3,
  };

  const handleEvaluate = async () => {
    // Define the expression using multiple variables
    const expression =
      "screen.forms['Form 1'].elements['E2'].name == 'Element 3' && user.role == 'Admin' && config.isEnabled == false";

    try {
      // Evaluate the expression with multiple variables
      const evaluationResult = await jexl.eval(expression, {
        screen,
        user,
        config,
      });
      setResult(evaluationResult ? "True" : "False");
    } catch (error) {
      setResult("Error: Invalid expression");
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
