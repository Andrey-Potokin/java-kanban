import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void testAddTaskToHistory() {
        Task task = new Task("Test Task", "Description");
        task.setID(1);

        // Добавляем задачу в историю
        historyManager.addTaskHistory(task);

        // Получаем историю задач
        List<Task> history = historyManager.getHistory();

        // Проверяем, что задача добавлена в историю
        assertEquals(1, history.size(), "History should contain one task");
        assertEquals(task.getID(), history.get(0).getID(), "Task ID should match");
        assertEquals(task.getTitle(), history.get(0).getTitle(), "Task title should match");
        assertEquals(task.getDescription(), history.get(0).getDescription(), "Task description should match");
        assertEquals(task.getStatus(), history.get(0).getStatus(), "Task status should match");
    }

    @Test
    void testMultipleTasksInHistory() {
        Task task1 = new Task("Test Task 1", "Description 1");
        task1.setID(1);

        Task task2 = new Task("Test Task 2", "Description 2");
        task2.setID(2);

        // Добавляем задачи в историю
        historyManager.addTaskHistory(task1);
        historyManager.addTaskHistory(task2);

        // Получаем историю задач
        List<Task> history = historyManager.getHistory();

        // Проверяем, что история содержит две задачи
        assertEquals(2, history.size(), "History should contain two tasks");

        // Проверяем, что задачи в истории соответствуют добавленным
        assertEquals(task1.getID(), history.get(0).getID(), "First task in history should be task1");
        assertEquals(task2.getID(), history.get(1).getID(), "Second task in history should be task2");
    }

    @Test
    void testHistoryLimit() {
        for (int i = 1; i <= 15; i++) {
            Task task = new Task("Task " + i, "Description " + i);
            task.setID(i);
            historyManager.addTaskHistory(task);
        }

        // Получаем историю задач
        List<Task> history = historyManager.getHistory();

        // Проверяем, что история содержит только последние 10 задач
        assertEquals(10, history.size(), "History should contain only the last 10 tasks");

        // Проверяем, что последние добавленные задачи в истории соответствуют ожидаемым
        for (int i = 6; i <= 15; i++) {
            assertEquals(i, history.get(i - 6).getID(), "Task ID should match for task " + i);
        }
    }
}