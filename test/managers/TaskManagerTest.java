package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
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
        taskManager.addTask(task);

        List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size());
        assertEquals(task.getId(), tasks.getFirst().getId());
        assertEquals("Задача", tasks.getFirst().getTitle());
    }

    @Test
    void testAddEpic() {
        taskManager.addEpic(epic);

        List<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size());
        assertEquals(epic.getId(), epics.getFirst().getId());
        assertEquals("Эпик", epics.getFirst().getTitle());
    }

    @Test
    void testAddSubtask() {
        subtask.setEpicId(1);

        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals(subtask.getId(), subtasks.getFirst().getId());
        assertEquals("Подзадача", subtasks.getFirst().getTitle());
    }

    @Test
    void testUpdateTask() {
        taskManager.addTask(task);

        task.setTitle("Новая задача");
        taskManager.updateTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertNotNull(retrievedTask);
        assertEquals(task.getId(), retrievedTask.getId());
        assertEquals("Новая задача", retrievedTask.getTitle());
    }

    @Test
    void testUpdateEpic() {
        taskManager.addEpic(epic);

        epic.setTitle("Новый эпик");
        taskManager.updateEpic(epic);

        Epic retrievedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(retrievedEpic);
        assertEquals(epic, retrievedEpic);
    }

    @Test
    void testUpdateSubtask() {
        subtask.setEpicId(1);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        subtask.setTitle("Новый сабтаск");
        taskManager.updateSubtask(subtask);

        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(retrievedSubtask);
        assertEquals(subtask.getId(), retrievedSubtask.getId());
        assertEquals("Новый сабтаск", retrievedSubtask.getTitle());
    }

    @Test
    void testGetTaskById() {
        taskManager.addTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertNotNull(retrievedTask);
        assertEquals(task.getId(), retrievedTask.getId());
        assertEquals("Задача", retrievedTask.getTitle());
    }

    @Test
    void testGetEpicById() {
        taskManager.addEpic(epic);

        Epic retrievedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(retrievedEpic);
        assertEquals(epic.getId(), retrievedEpic.getId());
        assertEquals("Эпик", retrievedEpic.getTitle());
    }

    @Test
    void testGetSubtaskById() {
        subtask.setEpicId(1);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(retrievedSubtask);
        assertEquals(subtask.getId(), retrievedSubtask.getId());
        assertEquals("Подзадача", retrievedSubtask.getTitle());
    }

    @Test
    void testGetAllTasks() {
        subtask.setEpicId(2);
        List<Task> expectedList = List.of(task, epic, subtask);

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        List<Task> actualList = taskManager.getAllTasks();
        assertEquals(3, actualList.size(), "Список должен быть 3");
        assertEquals(expectedList, actualList, "Список должен быть равен");
    }

    @Test
    void testGetTasks() {
        List<Task> expectedList = List.of(task , task2);

        taskManager.addTask(task);
        taskManager.addTask(task2);

        List<Task> actualList = taskManager.getTasks();
        assertEquals(2, actualList.size(), "Список должен быть 2");
        assertEquals(expectedList, actualList, "Списки должны быть равны");
    }

    @Test
    void testGetEpics() {
        List<Epic> expectedList = List.of(epic, epic2);

        taskManager.addEpic(epic);
        taskManager.addEpic(epic2);

        List<Epic> actualList = taskManager.getEpics();
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

        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        taskManager.addSubtask(subtask2);

        List<Subtask> actualList = taskManager.getSubtasks();
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

        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        taskManager.addSubtask(subtask2);

        List<Subtask> actualList = taskManager.getEpicSubtasks(epic);
        assertEquals(2, actualList.size(), "Список должен быть 2");
        assertEquals(expectedList, actualList, "Списки должны быть равны");
    }

    @Test
    void testDeleteTasks() {
        taskManager.addTask(task);

        taskManager.deleteTasks();

        List<Task> tasks = taskManager.getTasks();
        assertEquals(0, tasks.size(), "Список tasks должен быть пуст после удаления tasks");
    }

    @Test
    void testDeleteEpics() {
        subtask.setEpicId(1);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        taskManager.deleteEpics();

        List<Epic> epics = taskManager.getEpics();
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(0, epics.size(), "Список epics должен быть пуст после удаления epic");
        assertEquals(0, subtasks.size(), "Список subtasks должен быть пуст после удаления epic");
    }

    @Test
    void testDeleteSubtask() {
        taskManager.addEpic(epic);
        subtask.setEpicId(1);
        taskManager.addSubtask(subtask);

        taskManager.deleteSubtasks();

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(0, subtasks.size(), "Список subtasks должен быть пуст после удаления subtask");
    }

    @Test
    void testDeleteTaskById() {
        taskManager.addTask(task);
        taskManager.deleteTaskById(task.getId());

        List<Task> tasks = taskManager.getTasks();
        assertEquals(0, tasks.size(), "Список tasks должен быть пуст после удаления task");
    }

    @Test
    void testDeleteEpicById() {
        taskManager.addEpic(epic);
        subtask.setEpicId(1);
        taskManager.addSubtask(subtask);

        taskManager.deleteEpicById(epic.getId());

        List<Epic> epics = taskManager.getEpics();
        assertEquals(0, epics.size(), "Список epics должен быть пуст после удаления epic");
        assertEquals(
                0,
                taskManager.getSubtasks().size(),
                "Список subtasks должен быть пуст после удаления epic"
        );
    }

    @Test
    void testDeleteSubtaskById() {
        taskManager.addEpic(epic);
        subtask.setEpicId(1);
        taskManager.addSubtask(subtask);

        taskManager.deleteSubtaskById(subtask.getId());

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(0, subtasks.size(), "Список subtasks должен быть пуст после удаления subtask");
        assertEquals(0, epic.getSubtaskList().size(), "tasks.Epic должен быть пуст после удаления subtask");
    }

    @Test
    void testGetPrioritizedTasks() {
        task.setStartTime(LocalDateTime.of(2025, JANUARY, 1, 3, 0));
        task2.setStartTime(LocalDateTime.of(2025, JANUARY, 1, 1, 0));
        epic.setStartTime(LocalDateTime.of(2025, JANUARY, 1, 5, 0));
        List<Task> actualList = List.of(task2, task, epic);

        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.addEpic(epic);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(3, prioritizedTasks.size());
        assertEquals(actualList, prioritizedTasks);
    }
}
