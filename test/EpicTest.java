import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EpicTest {
    public static Epic epic1;
    public static Epic epic2;
    public static Subtask subtask;
    public static InMemoryTaskManager manager;

    @BeforeAll
    public static void beforeAll() {
        manager = new InMemoryTaskManager();
        epic1 = new Epic("Задача 1", "Описание задачи 1");
        manager.addEpic(epic1);
        epic2 = new Epic("Задача 2", "Описание задачи 2");
        subtask = new Subtask("Подзадача", "Описание подзадачи", epic1.getID());
    }

    @Test
    public void testEpicsEqualityById() {
        epic1.setID(1);
        epic2.setID(1);

        assertEquals(epic1, epic2, "Задачи с одинаковыми id должны быть равны");
    }

    @Test
    void testAddEpicAsSubtaskShouldThrowException() {
        epic1.setID(2);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.addSubtask(subtask);
        });

        assertEquals("Нельзя добавить Эпик как подзадачу.", exception.getMessage());
    }
}