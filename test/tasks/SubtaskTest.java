package tasks;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    @Test
    public void testAddSubtask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic(
                1,
                Type.EPIC,
                "Задача",
                Status.NEW,
                "Описание",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 1, 0)
        );
        manager.addEpic(epic);
        Subtask subtask = new Subtask(
                2,
                Type.SUBTASK,
                "Подзадача",
                Status.NEW,
                "Описание подзадачи",
                LocalDateTime.of(2025, JANUARY, 1, 3, 0),
                Duration.ofHours(1),
                1);

        assertEquals(0, manager.getSubtasks().size());
        manager.addSubtask(subtask);

        assertEquals(1, manager.getSubtasks().size());
        assertEquals(subtask, manager.getSubtasks().getFirst(), "Не добавлена подзадача");
    }
}