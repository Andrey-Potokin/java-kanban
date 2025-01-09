package tasks;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    // TODO проверить какие тесты должны быть

    @Test
    public void testEpicStatusIsNewWhenAllSubtasksAreNew() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic(
                1,
                Type.EPIC,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 1, 0)
        );
        Subtask subtask1 = new Subtask(
                2,
                Type.SUBTASK,
                "Подзадача",
                Status.NEW,
                "Описание подзадачи",
                LocalDateTime.of(2025, JANUARY, 1, 2, 0),
                Duration.ofHours(1),
                1
        );
        Subtask subtask2 = new Subtask(
                3,
                Type.SUBTASK,
                "Подзадача",
                Status.NEW,
                "Описание подзадачи",
                LocalDateTime.of(2025, JANUARY, 1, 4, 0),
                Duration.ofHours(1),
                1
        );
        manager.addEpic(epic);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(Status.NEW, epic.getStatus(), "Статус задачи должен быть NEW");
    }

    @Test
    public void testEpicStatusIsDoneWhenAllSubtasksAreDone() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic(
                1,
                Type.EPIC,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 1, 0)
        );
        Subtask subtask1 = new Subtask(
                2,
                Type.SUBTASK,
                "Подзадача",
                Status.DONE,
                "Описание подзадачи",
                LocalDateTime.of(2025, JANUARY, 1, 2, 0),
                Duration.ofHours(1),
                1
        );
        Subtask subtask2 = new Subtask(
                3,
                Type.SUBTASK,
                "Подзадача",
                Status.DONE,
                "Описание подзадачи",
                LocalDateTime.of(2025, JANUARY, 1, 4, 0),
                Duration.ofHours(1),
                1
        );
        manager.addEpic(epic);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(Status.DONE, epic.getStatus(), "Статус задачи должен быть DONE");
    }

    @Test
    public void testEpicStatusIsInProgressWhenSubtasksAreNewAndDone() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic(
                1,
                Type.EPIC,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 1, 0)
        );
        Subtask subtask1 = new Subtask(
                2,
                Type.SUBTASK,
                "Подзадача",
                Status.NEW,
                "Описание подзадачи",
                LocalDateTime.of(2025, JANUARY, 1, 2, 0),
                Duration.ofHours(1),
                1
        );
        Subtask subtask2 = new Subtask(
                3,
                Type.SUBTASK,
                "Подзадача",
                Status.DONE,
                "Описание подзадачи",
                LocalDateTime.of(2025, JANUARY, 1, 4, 0),
                Duration.ofHours(1),
                1
        );
        manager.addEpic(epic);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус задачи должен быть IN_PROGRESS");
    }

    @Test
    public void testEpicStatusIsInProgressWhenAllSubtasksAreInProcess() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic(
                1,
                Type.EPIC,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 1, 0)
        );
        Subtask subtask1 = new Subtask(
                2,
                Type.SUBTASK,
                "Подзадача",
                Status.IN_PROGRESS,
                "Описание подзадачи",
                LocalDateTime.of(2025, JANUARY, 1, 2, 0),
                Duration.ofHours(1),
                1
        );
        Subtask subtask2 = new Subtask(
                3,
                Type.SUBTASK,
                "Подзадача",
                Status.IN_PROGRESS,
                "Описание подзадачи",
                LocalDateTime.of(2025, JANUARY, 1, 4, 0),
                Duration.ofHours(1),
                1
        );
        manager.addEpic(epic);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус задачи должен быть IN_PROGRESS");
    }
}