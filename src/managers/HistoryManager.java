package managers;

import tasks.Task;

import java.util.List;
import java.util.Map;

public interface HistoryManager {
    void addTaskHistory(Task task);

    List<Task> getHistory();

    void removeNode(Node<Task> node);

    Map<Integer, Node<Task>> getNodes();
}