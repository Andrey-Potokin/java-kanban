package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    Task task;
    Task task2;
    Epic epic;
    Epic epic2;
    Subtask subtask;
    Subtask subtask2;

    @BeforeEach
    public abstract void setUp() throws IOException;

    @Test
    void testAddTask() {
        manager.addTask(task);

        List<Task> tasks = manager.getTasks();
        assertEquals(1, tasks.size());
        assertEquals(task.getId(), tasks.getFirst().getId());
        assertEquals("Задача", tasks.getFirst().getTitle());
    }

    @Test
    void testAddEpic() {
        manager.addEpic(epic);

        List<Epic> epics = manager.getEpics();
        assertEquals(1, epics.size());
        assertEquals(epic.getId(), epics.getFirst().getId());
        assertEquals("Эпик", epics.getFirst().getTitle());
    }

    @Test
    void testAddSubtask() {
        subtask.setEpicId(1);

        manager.addEpic(epic);
        manager.addSubtask(subtask);

        List<Subtask> subtasks = manager.getSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals(subtask.getId(), subtasks.getFirst().getId());
        assertEquals("Подзадача", subtasks.getFirst().getTitle());
    }

    @Test
    void testGetTaskById() {
        manager.addTask(task);

        Task retrievedTask = manager.getTaskById(task.getId());
        assertNotNull(retrievedTask);
        assertEquals(task.getId(), retrievedTask.getId());
        assertEquals("Задача", retrievedTask.getTitle());
    }

    @Test
    void testGetEpicById() {
        manager.addEpic(epic);

        Epic retrievedEpic = manager.getEpicById(epic.getId());
        assertNotNull(retrievedEpic);
        assertEquals(epic.getId(), retrievedEpic.getId());
        assertEquals("Эпик", retrievedEpic.getTitle());
    }

    @Test
    void testGetSubtaskById() {
        subtask.setEpicId(1);
        manager.addEpic(epic);
        manager.addSubtask(subtask);

        Subtask retrievedSubtask = manager.getSubtaskById(subtask.getId());
        assertNotNull(retrievedSubtask);
        assertEquals(subtask.getId(), retrievedSubtask.getId());
        assertEquals("Подзадача", retrievedSubtask.getTitle());
    }

    @Test
    void testGetAllTasks() {
        subtask.setEpicId(2);
        List<Task> expectedList = List.of(task, epic, subtask);

        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(subtask);

        List<Task> actualList = manager.getAllTasks();
        assertEquals(3, actualList.size(), "Список должен быть 3");
        assertEquals(expectedList, actualList, "Список должен быть равен");
    }

    @Test
    void testGetTasks() {
        List<Task> expectedList = List.of(task , task2);

        manager.addTask(task);
        manager.addTask(task2);

        List<Task> actualList = manager.getTasks();
        assertEquals(2, actualList.size(), "Список должен быть 2");
        assertEquals(expectedList, actualList, "Списки должны быть равны");
    }

    @Test
    void testGetEpics() {
        List<Epic> expectedList = List.of(epic, epic2);

        manager.addEpic(epic);
        manager.addEpic(epic2);

        List<Epic> actualList = manager.getEpics();
        assertEquals(2, actualList.size(), "Список должен быть 2");
        assertEquals(expectedList, actualList, "Списки должны быть равны");
    }

    @Test
    void testGetSubtasks() {
        List<Subtask> expectedList = List.of(subtask, subtask2);
        subtask.setEpicId(1);
        subtask.setId(2);
        subtask2.setEpicId(1);
        subtask2.setId(3);

        manager.addEpic(epic);
        manager.addSubtask(subtask);
        manager.addSubtask(subtask2);

        List<Subtask> actualList = manager.getSubtasks();
        assertEquals(2, actualList.size(), "Список должен быть 2");
        assertEquals(expectedList, actualList, "Списки должны быть равны");
    }

    @Test
    void testGetEpicSubtasks() {
        List<Subtask> expectedList = List.of(subtask, subtask2);
        subtask.setEpicId(1);
        subtask.setId(2);
        subtask2.setEpicId(1);
        subtask2.setId(3);

        manager.addEpic(epic);
        manager.addSubtask(subtask);
        manager.addSubtask(subtask2);

        List<Subtask> actualList = manager.getEpicSubtasks(epic);
        assertEquals(2, actualList.size(), "Список должен быть 2");
        assertEquals(expectedList, actualList, "Списки должны быть равны");
    }

    @Test
    void testDeleteTasks() {
        manager.addTask(task);

        manager.deleteTasks();

        List<Task> tasks = manager.getTasks();
        assertEquals(0, tasks.size(), "Список tasks должен быть пуст после удаления tasks");
    }

    @Test
    void testDeleteEpics() {
        subtask.setEpicId(1);
        manager.addEpic(epic);
        manager.addSubtask(subtask);

        manager.deleteEpics();

        List<Epic> epics = manager.getEpics();
        List<Subtask> subtasks = manager.getSubtasks();
        assertEquals(0, epics.size(), "Список epics должен быть пуст после удаления epic");
        assertEquals(0, subtasks.size(), "Список subtasks должен быть пуст после удаления epic");
    }

    @Test
    void testDeleteSubtask() {
        manager.addEpic(epic);
        subtask.setEpicId(1);
        manager.addSubtask(subtask);

        manager.deleteSubtasks();

        List<Subtask> subtasks = manager.getSubtasks();
        assertEquals(0, subtasks.size(), "Список subtasks должен быть пуст после удаления subtask");
    }

    @Test
    void testDeleteTaskById() {
        manager.addTask(task);
        manager.deleteTaskById(task.getId());

        List<Task> tasks = manager.getTasks();
        assertEquals(0, tasks.size(), "Список tasks должен быть пуст после удаления task");
    }

    @Test
    void testDeleteEpicById() {
        manager.addEpic(epic);
        subtask.setEpicId(1);
        manager.addSubtask(subtask);

        manager.deleteEpicById(epic.getId());

        List<Epic> epics = manager.getEpics();
        assertEquals(0, epics.size(), "Список epics должен быть пуст после удаления epic");
        assertEquals(
                0,
                manager.getSubtasks().size(),
                "Список subtasks должен быть пуст после удаления epic"
        );
    }

    @Test
    void testDeleteSubtaskById() {
        manager.addEpic(epic);
        subtask.setEpicId(1);
        manager.addSubtask(subtask);

        manager.deleteSubtaskById(subtask.getId());

        List<Subtask> subtasks = manager.getSubtasks();
        assertEquals(0, subtasks.size(), "Список subtasks должен быть пуст после удаления subtask");
        assertEquals(0, epic.getSubtaskList().size(), "tasks.Epic должен быть пуст после удаления subtask");
    }

    @Test
    void testUpdateEpicStartTime() {
        subtask.setStartTime(LocalDateTime.of(2025, JANUARY, 1, 2, 0));
        subtask2.setStartTime(LocalDateTime.of(2025, JANUARY, 1, 6, 0));
        subtask2.setDuration(Duration.ofHours(1));
        subtask2.getEndTime();
        epic.setSubtaskList(List.of(subtask, subtask2));

        manager.updateEpicStartTime(epic);

        LocalDateTime expectedEpicStartTime = LocalDateTime.of(2025, JANUARY, 1, 2, 0);
        assertEquals(expectedEpicStartTime, epic.getStartTime());
    }

    @Test
    void testUpdateEpicEndTime() {
        subtask.setStartTime(LocalDateTime.of(2025, JANUARY, 1, 1, 0));
        subtask.setDuration(Duration.ofHours(1));
        subtask.getEndTime();
        subtask2.setStartTime(LocalDateTime.of(2025, JANUARY, 1, 6, 0));
        subtask2.setDuration(Duration.ofHours(1));
        subtask2.getEndTime();
        epic.setSubtaskList(List.of(subtask, subtask2));

        manager.updateEpicEndTime(epic);

        LocalDateTime expectedEpicEndTime = LocalDateTime.of(2025, JANUARY, 1, 7, 0);
        assertEquals(expectedEpicEndTime, epic.getEndTime());
    }

    @Test
    void testGetPrioritizedTasks() {
        task.setStartTime(LocalDateTime.of(2025, JANUARY, 1, 3, 0));
        task2.setStartTime(LocalDateTime.of(2025, JANUARY, 1, 1, 0));
        epic.setStartTime(LocalDateTime.of(2025, JANUARY, 1, 5, 0));
        List<Task> actualList = List.of(task2, task, epic);

        manager.addTask(task);
        manager.addTask(task2);
        manager.addEpic(epic);

        List<Task> prioritizedTasks = List.copyOf(manager.getPrioritizedTasks());
        assertEquals(3, prioritizedTasks.size());
        assertEquals(actualList, prioritizedTasks);
    }
}
