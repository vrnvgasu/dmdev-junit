package ru.edu.extension;

import java.lang.reflect.Field;
import lombok.Getter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import ru.edu.service.UserService;

// TestInstancePostProcessor - его использует спринг
public class PostProcessingExtension implements TestInstancePostProcessor {

  @Override
  // testInstance - объект тестового класса
  public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
    System.out.println("post processing extension");
    var declaredFields = testInstance.getClass().getDeclaredFields();

    // спринг пробегается по всем свойствам объекта теста
    // когда встречает аннотации, то что-нибудь делает или инжектит данные
    for (Field field: declaredFields) {
      if (field.isAnnotationPresent(Getter.class)) {
        field.set(testInstance, new UserService(null));
      }
    }

  }

}
