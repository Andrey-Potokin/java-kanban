import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {
    File tempFile;
    FileBackedTaskManager manager;
    FileReader reader;
    BufferedReader br;

    @BeforeEach
    void beforeEach() throws IOException {
        try {
            // Создание временного файла
            tempFile = File.createTempFile("example", ".tmp");
            manager = new FileBackedTaskManager(tempFile);
            reader = new FileReader(tempFile);
            br = new BufferedReader(reader);

            manager.addTask(new Task(1, "Задача", "Описание задачи", Status.NEW, Type.TASK));
            Epic epic = new Epic(2, "Эпик", "Описание эпик", Status.NEW, Type.EPIC);
            manager.addEpic(epic);
            manager.addSubtask(new Subtask(
                    3,
                    "Подзадача",
                    "Описание подзадачи",
                    Status.NEW,
                    Type.SUBTASK,
                    2)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void afterEach() {
        tempFile.deleteOnExit();
    }

    @Test
    void save() throws IOException {
        try {
            String line = br.readLine();

            assertEquals("1, TASK, Задача, NEW, Описание задачи", line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void loadFromFile() throws IOException {
        try {
            List<Task> beforeLoad = manager.getAllTasks();
            manager.loadFromFile(tempFile);
            List<Task> afterLoad = manager.getAllTasks();
            assertEquals(beforeLoad, afterLoad, "Списки до загрузки файла и после не равны.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}