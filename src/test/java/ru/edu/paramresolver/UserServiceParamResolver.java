package ru.edu.paramresolver;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import ru.edu.service.UserService;

public class UserServiceParamResolver implements ParameterResolver {

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return parameterContext.getParameter().getType() == UserService.class;
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
//    return new UserService();

    // кешируем объект. Возвращаем готовый или создаем объект при первом обращении по ключу
    var store = extensionContext.getStore(ExtensionContext.Namespace.create(UserService.class));
    return store.getOrComputeIfAbsent(UserService.class, it -> new UserService());

    // создаем объект для каждого метода, в который делаем DI (тут ключ - название метода)
//    var store = extensionContext.getStore(ExtensionContext.Namespace.create(extensionContext.getTestMethod()));
//    return store.getOrComputeIfAbsent(UserService.class, it -> new UserService());
  }

}
