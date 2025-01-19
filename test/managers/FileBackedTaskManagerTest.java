package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File tempFile;
    FileReader reader;
    BufferedReader br;

    @Override
    @BeforeEach
    public void setUp() throws IOException {
        // Создание временного файла
        tempFile = File.createTempFile("example", ".tmp");
        manager = new FileBackedTaskManager(tempFile);
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
    void testSave() throws IOException {
        reader = new FileReader(tempFile);
        br = new BufferedReader(reader);

        manager.addTask(task);

        String line = br.readLine();
        assertEquals("1, TASK, Задача, NEW, Описание задачи, 2025-01-01T00:00, PT1H", line);
    }

    @Test
    void testLoadFromFile() {
        manager.addTask(task);
        manager.addEpic(epic);
        subtask.setEpicId(2);
        manager.addSubtask(subtask);

        List<Task> beforeLoad = manager.getAllTasks();
        manager.loadFromFile(tempFile);
        List<Task> afterLoad = manager.getAllTasks();
        assertEquals(beforeLoad, afterLoad, "Списки до загрузки файла и после не равны.");
    }
}