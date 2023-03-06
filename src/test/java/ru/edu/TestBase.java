package ru.edu;

import org.junit.jupiter.api.extension.ExtendWith;
import ru.edu.extension.GlobalExtension;

@ExtendWith({ // расширения наследуются в тестах
    GlobalExtension.class
})
public abstract class TestBase {

}
