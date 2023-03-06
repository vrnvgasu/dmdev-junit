package ru.edu.extension;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ConditionalExtension implements ExecutionCondition {

  @Override
  // стоит ли вызывать тест
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    // возвращаем enabled или disabled
    return System.getProperty("skip") != null
        ? ConditionEvaluationResult.disabled("test is skipped")
        : ConditionEvaluationResult.enabled("enabled by default");
  }

}
