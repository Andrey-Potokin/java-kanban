import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateEpicStatus(Epic epic);

    void updateSubtask(Subtask subtask);

    Task getTaskById(int Id);

    Epic getEpicById(int Id);

    Subtask getSubtaskById(int Id);

    List<Task> getAllTasks();

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Subtask> getEpicSubtasks(Epic epic);

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    void deleteTaskById(int Id);

    void deleteEpicById(int Id);

    void deleteSubtaskById(int Id);

    List<Task> getHistory();
}
