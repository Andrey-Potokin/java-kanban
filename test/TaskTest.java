import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    public static Task task1;
    public static Task task2;

    @BeforeAll
    public static void beforeAll() {
        task1 = new Task("Задача 1", "Описание задачи 1");
        task2 = new Task("Задача 2", "Описание задачи 2");
    }

    @Test
    public void testTasksEqualityById() {
        task1.setID(1);
        task2.setID(1);

        assertEquals(task1, task2, "Задачи с одинаковыми id должны быть равны");
    }
}