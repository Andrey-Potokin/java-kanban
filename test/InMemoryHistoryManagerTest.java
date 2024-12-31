import managers.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void testAddTaskToHistory() {
        Task task = new Task(1, "Задача", "Описание", Status.NEW);
        task.setId(1);

        // Добавляем задачу в историю
        historyManager.addTaskHistory(task);

        // Получаем историю задач
        List<Task> history = historyManager.getHistory();

        // Проверяем, что задача добавлена в историю
        assertEquals(1, history.size(), "History should contain one task");
        assertEquals(task.getId(), history.get(0).getId(), "tasks.Task Id should match");
        assertEquals(task.getTitle(), history.get(0).getTitle(), "tasks.Task title should match");
        assertEquals(task.getDescription(), history.get(0).getDescription(), "tasks.Task description should match");
        assertEquals(task.getStatus(), history.get(0).getStatus(), "tasks.Task status should match");
    }

    @Test
    void testMultipleTasksInHistory() {
        Task task1 = new Task(1, "Задача 1", "Описание 1", Status.NEW);
        task1.setId(1);

        Task task2 = new Task(2, "Задача 2", "Описание 2", Status.NEW);
        task2.setId(2);

        // Добавляем задачи в историю
        historyManager.addTaskHistory(task1);
        historyManager.addTaskHistory(task2);

        // Получаем историю задач
        List<Task> history = historyManager.getHistory();

        // Проверяем, что история содержит две задачи
        assertEquals(2, history.size(), "History should contain two tasks");

        // Проверяем, что задачи в истории соответствуют добавленным
        assertEquals(task1.getId(), history.get(0).getId(), "First task in history should be task1");
        assertEquals(task2.getId(), history.get(1).getId(), "Second task in history should be task2");
    }

    @Test
    void testRemoveNodeFromHistory() {
        Task task1 = new Task(1, "Задача 1", "Описание 1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task(2, "Задача 2", "Описание 2", Status.NEW);
        task2.setId(2);

        // Добавляем задачи в историю
        historyManager.addTaskHistory(task1);
        historyManager.addTaskHistory(task2);

        // Удаляем первую задачу из истории
        historyManager.removeNode(historyManager.getNodes().get(task1.getId()));

        // Получаем историю задач
        List<Task> history = historyManager.getHistory();

        // Проверяем, что история содержит только одну задачу
        assertEquals(1, history.size(), "History should contain one task after removal");
        assertEquals(task2.getId(), history.get(0).getId(), "Remaining task Id should match");
    }



    @Test
    void testAddSameTaskMultipleTimes() {
        Task task = new Task(1, "Задача", "Описание", Status.NEW);
        task.setId(1);

        // Добавляем задачу в историю дважды
        historyManager.addTaskHistory(task);
        historyManager.addTaskHistory(task);

        // Получаем историю задач
        List<Task> history = historyManager.getHistory();

        // Проверяем, что история содержит только одну задачу
        assertEquals(1, history.size(), "History should contain one unique task");
    }
}