import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void testAddTask() throws IOException {
        Task task = new Task(1, "Задача", "Описание", Status.NEW);
        taskManager.addTask(task);

        List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size());
        assertEquals(task.getId(), tasks.get(0).getId());
        assertEquals("Задача", tasks.get(0).getTitle());
    }

    @Test
    void testAddEpic() {
        Epic epic = new Epic(1, "Эпик", "Описание эпика", Status.NEW);
        taskManager.addEpic(epic);

        List<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size());
        assertEquals(epic.getId(), epics.get(0).getId());
        assertEquals("Эпик", epics.get(0).getTitle());
    }

    @Test
    void testAddSubtask() {
        Epic epic = new Epic(1, "Эпик", "Описание эпика", Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask(
                2,
                "Сабтаск",
                "Описание сабтаска",
                Status.NEW,
                1
        );
        taskManager.addSubtask(subtask);

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals(subtask.getId(), subtasks.get(0).getId());
        assertEquals("Сабтаск", subtasks.get(0).getTitle());
    }

    @Test
    void testGetTaskById() throws IOException {
        Task task = new Task(1, "Задача", "Описание", Status.NEW);
        taskManager.addTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertNotNull(retrievedTask);
        assertEquals(task.getId(), retrievedTask.getId());
        assertEquals("Задача", retrievedTask.getTitle());
    }

    @Test
    void testGetEpicById() {
        Epic epic = new Epic(1, "Эпик", "Описание эпика", Status.NEW);
        taskManager.addEpic(epic);

        Epic retrievedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(retrievedEpic);
        assertEquals(epic.getId(), retrievedEpic.getId());
        assertEquals("Эпик", retrievedEpic.getTitle());
    }

    @Test
    void testGetSubtaskById() {
        Epic epic = new Epic(1, "Эпик", "Описание эпика", Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask(
                2,
                "Сабтаск",
                "Описание сабтаска",
                Status.NEW,
                1
        );
        taskManager.addSubtask(subtask);

        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(retrievedSubtask);
        assertEquals(subtask.getId(), retrievedSubtask.getId());
        assertEquals("Сабтаск", retrievedSubtask.getTitle());
    }

    @Test
    void testTaskImmutabilityOnAdd() throws IOException {
        // Создаем задачу с определенными полями
        Task originalTask = new Task(1, "Задача", "Описание",Status.NEW);
        originalTask.setId(1); // Устанавливаем конкретный Id

        // Сохраняем значения полей оригинальной задачи
        String originalTitle = originalTask.getTitle();
        String originalDescription = originalTask.getDescription();
        Status originalStatus = originalTask.getStatus();

        // Добавляем задачу в менеджер
        taskManager.addTask(originalTask);

        // Получаем добавленную задачу из менеджера
        Task addedTask = taskManager.getTaskById(1);

        // Проверяем, что поля добавленной задачи совпадают с оригинальной задачей
        assertEquals(originalTitle, addedTask.getTitle(), "Title should remain unchanged");
        assertEquals(originalDescription, addedTask.getDescription(), "Description should remain unchanged");
        assertEquals(originalStatus, addedTask.getStatus(), "tasks.Status should remain unchanged");

        // Проверяем, что Id остался тем же
        assertEquals(1, addedTask.getId(), "Id should remain unchanged");
    }

    @Test
    void testDeleteTaskById() throws IOException {
        Task task = new Task(1, "Задача", "Описание", Status.NEW);
        taskManager.addTask(task);
        taskManager.deleteTaskById(task.getId());

        List<Task> tasks = taskManager.getTasks();
        assertEquals(0, tasks.size(), "Tasks list should be empty after deletion");
    }

    @Test
    void testDeleteEpicById() {
        Epic epic = new Epic(1, "Эпик", "Описание эпика", Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask(
                2,
                "Сабтаск",
                "Описание сабтаска",
                Status.NEW,
                1
        );
        taskManager.addSubtask(subtask);

        taskManager.deleteEpicById(epic.getId());

        List<Epic> epics = taskManager.getEpics();
        assertEquals(0, epics.size(), "Epics list should be empty after deletion");
        assertEquals(0, taskManager.getSubtasks().size(), "Subtasks list should be empty after deleting epic");
    }

    @Test
    void testDeleteSubtaskById() {
        Epic epic = new Epic(1, "Эпик", "Описание эпика", Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask(
                2,
                "Сабтаск",
                "Описание сабтаска",
                Status.NEW,
                1
        );
        taskManager.addSubtask(subtask);

        taskManager.deleteSubtaskById(subtask.getId());

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(0, subtasks.size(), "Subtasks list should be empty after deletion");
        assertEquals(0, epic.getSubtaskList().size(), "tasks.Epic should have no subtasks after deletion");
    }

    @Test
    void testUpdateEpicStatus() {
        Epic epic = new Epic(1, "Эпик", "Описание эпика", Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask(2, "Сабтаск 1", "Описание 1", Status.NEW, 1);
        subtask1.setStatus(Status.DONE);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask(3, "Сабтаск 2", "Описание 2", Status.NEW, 1);
        subtask2.setStatus(Status.NEW);
        taskManager.addSubtask(subtask2);

        taskManager.updateEpicStatus(epic);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "tasks.Epic status should be IN_PROGRESS when it has both DONE and NEW subtasks");

        // Удалим подзадачу с статусом NEW и проверим статус
        taskManager.deleteSubtaskById(subtask2.getId());

        taskManager.updateEpicStatus(epic);

        assertEquals(Status.DONE, epic.getStatus(), "tasks.Epic status should be DONE when all subtasks are DONE");
    }

}