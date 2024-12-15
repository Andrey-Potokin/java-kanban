import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubtaskTest {
    public static Epic epic;
    public static Subtask subtask1;
    public static Subtask subtask2;
    public static InMemoryTaskManager manager;

    @BeforeAll
    public static void beforeAll() {
        manager = new InMemoryTaskManager();
        epic = new Epic( "Задача", "Описание", Type.EPIC);
        manager.addEpic(epic);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Type.SUBTASK, 1);
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Type.SUBTASK, 1);
    }

    @Test
    public void testSubtasksEqualityById() {
        subtask1.setId(1);
        subtask2.setId(1);

        assertEquals(subtask1, subtask2, "Задачи с одинаковыми Id должны быть равны");
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