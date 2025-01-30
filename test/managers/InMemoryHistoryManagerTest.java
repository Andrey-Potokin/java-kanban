package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager manager;
    private Task task;
    private Task task2;

    @BeforeEach
    void setUp() {
        manager = new InMemoryHistoryManager();
        task = new Task(
                0,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1)
        );
        task2 = new Task(
                0,
                "Задача2",
                Status.NEW,
                "Описание задачи2",
                LocalDateTime.of(2025, JANUARY, 1, 1, 0),
                Duration.ofHours(1)
        );
    }

    @Test
    void testCreateTaskToHistory() {
        task.setId(1);

        // Добавляем задачу в историю
        manager.createTaskHistory(task);

        // Получаем историю задач
        List<Task> history = manager.getHistory();

        // Проверяем, что задача добавлена в историю
        assertEquals(1, history.size(), "History должна содержать одну задачу");
        assertEquals(task.getId(), history.getFirst().getId(), "tasks.Task Id должен совпадать");
        assertEquals(task.getTitle(), history.getFirst().getTitle(), "tasks.Task title должен совпадать");
        assertEquals(task.getDescription(), history.getFirst().getDescription(), "tasks.Task description должен совпадать");
        assertEquals(task.getStatus(), history.getFirst().getStatus(), "tasks.Task status должен совпадать");
    }

    @Test
    void testMultipleTasksInHistory() {
        task.setId(1);
        task2.setId(2);

        // Добавляем задачи в историю
        manager.createTaskHistory(task);
        manager.createTaskHistory(task2);

        // Получаем историю задач
        List<Task> history = manager.getHistory();

        // Проверяем, что история содержит две задачи
        assertEquals(2, history.size(), "History должна содержать две задачи");

        // Проверяем, что задачи в истории соответствуют добавленным
        assertEquals(task.getId(), history.get(0).getId(), "Первая задача должна быть task");
        assertEquals(task2.getId(), history.get(1).getId(), "Вторая задача должна быть task2");
    }

    @Test
    void testRemoveFirstTaskFromHistory() {
        task.setId(1);
        task2.setId(2);

        // Добавляем задачи в историю
        manager.createTaskHistory(task);
        manager.createTaskHistory(task2);

        // Удаляем первую задачу из истории
        manager.removeNode(manager.getNodes().get(task.getId()));

        // Получаем историю задач
        List<Task> history = manager.getHistory();

        // Проверяем, что история содержит только одну задачу
        assertEquals(1, history.size(), "History должна содержать одну задачу");
        assertEquals(task2.getId(), history.getFirst().getId(), "Оставшаяся задача должна быть task2");
    }

    /**
     * Удаляем последнюю задачу из истории
     */
    @Test
    void testRemoveLastTaskFromHistory() {
        task.setId(1);
        task2.setId(2);

        // Добавляем задачи в историю
        manager.createTaskHistory(task);
        manager.createTaskHistory(task2);

        // Удаляем последнюю задачу из истории
        manager.removeNode(manager.getNodes().get(task2.getId()));

        // Получаем историю задач
        List<Task> history = manager.getHistory();

        // Проверяем, что история содержит только одну задачу
        assertEquals(1, history.size(), "History должна содержать одну задачу");
        assertEquals(task.getId(), history.getFirst().getId(), "Оставшаяся задача должна быть task");
    }

    @Test
    void testAddSameTaskMultipleTimes() {
        task.setId(1);

        // Добавляем задачу в историю дважды
        manager.createTaskHistory(task);
        manager.createTaskHistory(task);

        // Получаем историю задач
        List<Task> history = manager.getHistory();

        // Проверяем, что история содержит только одну задачу
        assertEquals(1, history.size(), "History должна содержать одну задачу");
    }
}