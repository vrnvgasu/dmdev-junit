package ru.edu.extension;

import java.io.IOException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class ThrowableExtension implements TestExecutionExceptionHandler {

  @Override
  // throwable - ошибка из теста
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    if (throwable instanceof IOException) {
      throw throwable;
    }
  }

}
