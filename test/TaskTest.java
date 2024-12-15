import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {
    public static Task task1;
    public static Task task2;

    @BeforeAll
    public static void beforeAll() {
        task1 = new Task(1, "Задача 1", "Описание задачи 1", Status.NEW, Type.TASK);
        task2 = new Task(2, "Задача 2", "Описание задачи 2", Status.NEW, Type.TASK);
    }

    @Test
    public void testTasksEqualityById() {
        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковыми Id должны быть равны");
    }
}