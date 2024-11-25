import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    public static Epic epic;
    public static Subtask subtask1;
    public static Subtask subtask2;
    public static InMemoryTaskManager manager;

    @BeforeAll
    public static void beforeAll() {
        manager = new InMemoryTaskManager();
        epic = new Epic("Задача", "Описание");
        manager.addEpic(epic);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", 1);
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", 1);
    }

    @Test
    public void testSubtasksEqualityById() {
        subtask1.setID(1);
        subtask2.setID(1);

        assertEquals(subtask1, subtask2, "Задачи с одинаковыми id должны быть равны");
    }

    @Test
    public void testAddSubtaskAsEpicShouldThrowException() {
        manager.setIdCounter(1);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.addSubtask(subtask1);
        });

        assertEquals("Нельзя добавить подзадачу как Эпик.", exception.getMessage());
    }
}