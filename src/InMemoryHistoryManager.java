import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> tasksHistory = new ArrayList<>();
    private static final int MAX_NUMBER_TASKS_IN_HISTORY = 10;


    @Override
    public void addTaskHistory(Task task) {
        if (tasksHistory.size() == MAX_NUMBER_TASKS_IN_HISTORY) {
            tasksHistory.removeFirst();
        }
        tasksHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }
}
