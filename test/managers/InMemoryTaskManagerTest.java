package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.*;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
        task = new Task(
                0,
                Type.TASK,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1)
        );
        task2 = new Task(
                0,
                Type.TASK,
                "Задача2",
                Status.NEW,
                "Описание задачи2",
                LocalDateTime.of(2025, JANUARY, 1, 1, 0),
                Duration.ofHours(1)
        );
        epic = new Epic(
                0,
                Type.EPIC,
                "Эпик",
                Status.NEW,
                "Описание эпик",
                LocalDateTime.of(2025, JANUARY, 1, 2, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 3, 0)
        );
        epic2 = new Epic(
                0,
                Type.EPIC,
                "Эпик2",
                Status.NEW,
                "Описание эпик2",
                LocalDateTime.of(2025, JANUARY, 1, 4, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 5, 0)
        );
        subtask = new Subtask(
                0,
                Type.SUBTASK,
                "Подзадача",
                Status.NEW,
                "Описание подзадачи",
                LocalDateTime.of(2025, JANUARY, 1, 6, 0),
                Duration.ofHours(1),
                0
        );
        subtask2 = new Subtask(
                0,
                Type.SUBTASK,
                "Подзадача2",
                Status.NEW,
                "Описание подзадачи2",
                LocalDateTime.of(2025, JANUARY, 1, 7, 0),
                Duration.ofHours(1),
                0
        );
    }

    @Test
    void testTaskImmutabilityOnAdd() {
        task.setId(1);
        String originalTitle = task.getTitle();
        String originalDescription = task.getDescription();
        Status originalStatus = task.getStatus();

        manager.addTask(task);
        Task addedTask = manager.getTaskById(1);

        assertEquals(originalTitle, addedTask.getTitle(), "Заголовок должен остаться тем же");
        assertEquals(originalDescription, addedTask.getDescription(), "Описание должно остаться тем же");
        assertEquals(originalStatus, addedTask.getStatus(), "tasks.Status должен остаться тем же");
        assertEquals(1, addedTask.getId(), "Id должен остаться тем же");
    }

    /**
     * Тест проверяет, что задачи не пересекаются во времени.
     */
    @Test
    void testAddTaskWithoutIntersection() {
        task.setStartTime(LocalDateTime.of(2020, 1, 1, 0, 0));
        task.setDuration(Duration.ofHours(1));

        task2.setStartTime(LocalDateTime.of(2020, 1, 1, 2, 0));
        task2.setDuration(Duration.ofHours(1));


        manager.addTask(task);
        assertDoesNotThrow(() -> manager.addTask(task2));
    }

    /**
     * Тест проверяет, что задачи пересекаются во времени.
     */
    @Test
    void testAddTaskWithIntersection() {
        task.setStartTime(LocalDateTime.of(2025, JANUARY, 1, 0, 0));
        task.setDuration(Duration.ofHours(2));

        task2.setStartTime(LocalDateTime.of(2025, JANUARY, 1, 1, 1));
        task2.setDuration(Duration.ofHours(1));

        manager.addTask(task);
        assertThrows(IllegalArgumentException.class, () -> manager.addTask(task2),
                "tasks.addTask должен выбрасывать исключение IllegalArgumentException");
    }

    @Test
    public void testAddSubtaskWithoutEpic() {
        assertThrows(IllegalArgumentException.class, () -> manager.addSubtask(subtask),
                "addSubtask() должен выбрасывать исключение IllegalArgumentException");
    }
}