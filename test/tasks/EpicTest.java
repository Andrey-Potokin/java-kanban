package tasks;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    InMemoryTaskManager manager;
    Epic epic;
    Epic epic2;
    Subtask subtask;
    Subtask subtask2;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
        epic = new Epic(
                1,
                Type.EPIC,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 1, 0)
        );
        epic2 = new Epic(
                2,
                Type.EPIC,
                "Задача2",
                Status.NEW,
                "Описание задачи2",
                LocalDateTime.of(2025, JANUARY, 1, 1, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 2, 0)
        );
        subtask = new Subtask(
                3,
                Type.SUBTASK,
                "Подзадача",
                Status.NEW,
                "Описание подзадачи",
                LocalDateTime.of(2025, JANUARY, 1, 2, 0),
                Duration.ofHours(1),
                1
        );
        subtask2 = new Subtask(
                4,
                Type.SUBTASK,
                "Подзадача2",
                Status.NEW,
                "Описание подзадачи",
                LocalDateTime.of(2025, JANUARY, 1, 3, 0),
                Duration.ofHours(1),
                1
        );
    }

    @Test
    public void testEpicEqualityById() {
        epic.setId(1);
        epic2.setId(1);

        assertEquals(epic, epic2, "Задачи должны быть равны, так как их id равны");
    }

    @Test
    public void testEpicStatusIsNewWhenAllSubtasksAreNew() {
        manager.addEpic(epic);
        manager.addSubtask(subtask);
        manager.addSubtask(subtask2);

        assertEquals(Status.NEW, epic.getStatus(), "Статус задачи должен быть NEW");
    }

    @Test
    public void testEpicStatusIsDoneWhenAllSubtasksAreDone() {
        subtask.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);

        manager.addEpic(epic);
        manager.addSubtask(subtask);
        manager.addSubtask(subtask2);

        assertEquals(Status.DONE, epic.getStatus(), "Статус задачи должен быть DONE");
    }

    @Test
    public void testEpicStatusIsInProgressWhenSubtasksAreNewAndDone() {
        subtask.setStatus(Status.NEW);
        subtask2.setStatus(Status.DONE);

        manager.addEpic(epic);
        manager.addSubtask(subtask);
        manager.addSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус задачи должен быть IN_PROGRESS");
    }

    @Test
    public void testEpicStatusIsInProgressWhenAllSubtasksAreInProcess() {
        subtask.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);

        manager.addEpic(epic);
        manager.addSubtask(subtask);
        manager.addSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус задачи должен быть IN_PROGRESS");
    }
}