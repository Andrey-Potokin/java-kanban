import managers.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManagersTest {

    @Test
    void testGetDefaultTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Менеджер не должен иметь значение null");
        assertTrue(taskManager instanceof InMemoryTaskManager, "Должен возвращать экземпляр managers.InMemoryTaskManager");
    }

    @Test
    void testGetDefaultHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Менеджер не должен иметь значение null");
        assertTrue(historyManager instanceof InMemoryHistoryManager, "Должен возвращать экземпляр managers.InMemoryHistoryManager");
    }
}