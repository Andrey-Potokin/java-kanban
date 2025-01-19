package managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void testGetDefaultTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Менеджер не должен иметь значение null");
        assertInstanceOf(InMemoryTaskManager.class, taskManager, "Должен возвращать экземпляр managers.InMemoryTaskManager");
    }

    @Test
    void testGetDefaultHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Менеджер не должен иметь значение null");
        assertInstanceOf(InMemoryHistoryManager.class, historyManager, "Должен возвращать экземпляр managers.InMemoryHistoryManager");
    }
}