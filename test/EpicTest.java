import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;

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
        subtask = new Subtask("Подзадача", "Описание подзадачи", epic1.getId());
    }

    @Test
    public void testEpicsEqualityById() {
        epic1.setId(1);
        epic2.setId(1);

        assertEquals(epic1, epic2, "Задачи с одинаковыми Id должны быть равны");
    }

    @Test
    void testAddEpicAsSubtaskShouldThrowException() {
        epic1.setId(2);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.addSubtask(subtask);
        });

        assertEquals("Нельзя добавить Эпик как подзадачу.", exception.getMessage());
    }
}