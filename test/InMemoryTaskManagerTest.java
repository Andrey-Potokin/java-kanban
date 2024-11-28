import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void testAddTask() {
        Task task = new Task("Test Task", "Description");
        taskManager.addTask(task);

        List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size());
        assertEquals(task.getID(), tasks.get(0).getID());
        assertEquals("Test Task", tasks.get(0).getTitle());
    }

    @Test
    void testAddEpic() {
        Epic epic = new Epic("Test Epic", "Epic Description");
        taskManager.addEpic(epic);

        List<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size());
        assertEquals(epic.getID(), epics.get(0).getID());
        assertEquals("Test Epic", epics.get(0).getTitle());
    }

    @Test
    void testAddSubtask() {
        Epic epic = new Epic("Test Epic", "Epic Description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Subtask Description", epic.getID());
        taskManager.addSubtask(subtask);

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals(subtask.getID(), subtasks.get(0).getID());
        assertEquals("Test Subtask", subtasks.get(0).getTitle());
    }

    @Test
    void testGetTaskByID() {
        Task task = new Task("Test Task", "Description");
        taskManager.addTask(task);

        Task retrievedTask = taskManager.getTaskByID(task.getID());
        assertNotNull(retrievedTask);
        assertEquals(task.getID(), retrievedTask.getID());
        assertEquals("Test Task", retrievedTask.getTitle());
    }

    @Test
    void testGetEpicByID() {
        Epic epic = new Epic("Test Epic", "Epic Description");
        taskManager.addEpic(epic);

        Epic retrievedEpic = taskManager.getEpicByID(epic.getID());
        assertNotNull(retrievedEpic);
        assertEquals(epic.getID(), retrievedEpic.getID());
        assertEquals("Test Epic", retrievedEpic.getTitle());
    }

    @Test
    void testGetSubtaskByID() {
        Epic epic = new Epic("Test Epic", "Epic Description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Subtask Description", epic.getID());
        taskManager.addSubtask(subtask);

        Subtask retrievedSubtask = taskManager.getSubtaskByID(subtask.getID());
        assertNotNull(retrievedSubtask);
        assertEquals(subtask.getID(), retrievedSubtask.getID());
        assertEquals("Test Subtask", retrievedSubtask.getTitle());
    }

    @Test
    void testIDConflict() {
        // Создаем задачу с заданным ID
        Task taskWithSpecificID = new Task("Task with Specific ID", "Description");
        taskWithSpecificID.setID(1); // Устанавливаем конкретный ID
        taskManager.addTask(taskWithSpecificID);

        // Создаем задачу с автоматически сгенерированным ID
        Task taskWithGeneratedID = new Task("Task with Generated ID", "Description");
        taskManager.addTask(taskWithGeneratedID); // Менеджер должен сгенерировать уникальный ID

        // Получаем все задачи из менеджера
        List<Task> allTasks = taskManager.getTasks();

        // Проверяем, что обе задачи добавлены и имеют разные ID
        assertEquals(2, allTasks.size());

        // Проверяем, что ID задачи с конкретным ID равен 1
        assertEquals(1, taskWithSpecificID.getID());

        // Проверяем, что ID задачи с автоматически сгенерированным ID не равен 1
        assertNotEquals(1, taskWithGeneratedID.getID());

        // Проверяем, что ID автоматически сгенерированного задания уникален
        for (Task task : allTasks) {
            if (task != taskWithSpecificID) {
                assertNotEquals(taskWithSpecificID.getID(), task.getID());
            }
        }
    }

    @Test
    void testTaskImmutabilityOnAdd() {
        // Создаем задачу с определенными полями
        Task originalTask = new Task("Original Task", "This is a description");
        originalTask.setID(1); // Устанавливаем конкретный ID

        // Сохраняем значения полей оригинальной задачи
        String originalTitle = originalTask.getTitle();
        String originalDescription = originalTask.getDescription();
        Status originalStatus = originalTask.getStatus();

        // Добавляем задачу в менеджер
        taskManager.addTask(originalTask);

        // Получаем добавленную задачу из менеджера
        Task addedTask = taskManager.getTaskByID(1);

        // Проверяем, что поля добавленной задачи совпадают с оригинальной задачей
        assertEquals(originalTitle, addedTask.getTitle(), "Title should remain unchanged");
        assertEquals(originalDescription, addedTask.getDescription(), "Description should remain unchanged");
        assertEquals(originalStatus, addedTask.getStatus(), "Status should remain unchanged");

        // Проверяем, что ID остался тем же
        assertEquals(1, addedTask.getID(), "ID should remain unchanged");
    }

    @Test
    void testDeleteTaskByID() {
        Task task = new Task("Test Task", "Description");
        taskManager.addTask(task);
        taskManager.deleteTaskByID(task.getID());

        List<Task> tasks = taskManager.getTasks();
        assertEquals(0, tasks.size(), "Tasks list should be empty after deletion");
    }

    @Test
    void testDeleteEpicByID() {
        Epic epic = new Epic("Test Epic", "Epic Description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Subtask Description", epic.getID());
        taskManager.addSubtask(subtask);

        taskManager.deleteEpicByID(epic.getID());

        List<Epic> epics = taskManager.getEpics();
        assertEquals(0, epics.size(), "Epics list should be empty after deletion");
        assertEquals(0, taskManager.getSubtasks().size(), "Subtasks list should be empty after deleting epic");
    }

    @Test
    void testDeleteSubtaskByID() {
        Epic epic = new Epic("Test Epic", "Epic Description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Subtask Description", epic.getID());
        taskManager.addSubtask(subtask);

        taskManager.deleteSubtaskByID(subtask.getID());

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(0, subtasks.size(), "Subtasks list should be empty after deletion");
        assertEquals(0, epic.getSubtaskList().size(), "Epic should have no subtasks after deletion");
    }

    @Test
    void testUpdateEpicStatus() {
        Epic epic = new Epic("Test Epic", "Epic Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getID());
        subtask1.setStatus(Status.DONE);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic.getID());
        subtask2.setStatus(Status.NEW);
        taskManager.addSubtask(subtask2);

        taskManager.updateEpicStatus(epic);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic status should be IN_PROGRESS when it has both DONE and NEW subtasks");

        // Удалим подзадачу с статусом NEW и проверим статус
        taskManager.deleteSubtaskByID(subtask2.getID());

        taskManager.updateEpicStatus(epic);

        assertEquals(Status.DONE, epic.getStatus(), "Epic status should be DONE when all subtasks are DONE");
    }

}