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
    void testRemoveNodeFromHistory() {
        Task task1 = new Task("Test Task 1", "Description 1");
        task1.setID(1);
        Task task2 = new Task("Test Task 2", "Description 2");
        task2.setID(2);

        // Добавляем задачи в историю
        historyManager.addTaskHistory(task1);
        historyManager.addTaskHistory(task2);

        // Удаляем первую задачу из истории
        historyManager.removeNode(historyManager.getNodes().get(task1.getID()));

        // Получаем историю задач
        List<Task> history = historyManager.getHistory();

        // Проверяем, что история содержит только одну задачу
        assertEquals(1, history.size(), "History should contain one task after removal");
        assertEquals(task2.getID(), history.get(0).getID(), "Remaining task ID should match");
    }



    @Test
    void testAddSameTaskMultipleTimes() {
        Task task = new Task("Test Task", "Description");
        task.setID(1);

        // Добавляем задачу в историю дважды
        historyManager.addTaskHistory(task);
        historyManager.addTaskHistory(task);

        // Получаем историю задач
        List<Task> history = historyManager.getHistory();

        // Проверяем, что история содержит только одну задачу
        assertEquals(1, history.size(), "History should contain one unique task");
    }
}