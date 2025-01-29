package tasks;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
    InMemoryTaskManager manager;
    Epic epic;
    Subtask subtask;
    Subtask subtask2;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
        epic = new Epic(
                1,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 1, 0)
        );
        subtask = new Subtask(
                2,
                "Подзадача",
                Status.NEW,
                "Описание подзадачи",
                LocalDateTime.of(2025, JANUARY, 1, 2, 0),
                Duration.ofHours(1),
                1
        );
        subtask2 = new Subtask(
                3,
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
        subtask.setId(2);
        subtask2.setId(2);

        assertEquals(subtask, subtask2, "Задачи должны быть равны, так как их id равны");
    }
}