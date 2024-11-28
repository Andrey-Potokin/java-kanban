import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int idCounter = 1;

    // генерация ID
    private int generateID() {
        return idCounter++;
    }

    public void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }

    @Override
    public void addTask(Task task) {
        task.setID(generateID());
        tasks.put(task.getID(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setID(generateID());
        epics.put(epic.getID(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setID(generateID());
        Epic epic = epics.get(subtask.getEpicID());

        if (epic == null) {
            throw new IllegalArgumentException("Сначала нужно создать Эпик");
        } else if (subtask.getID() == subtask.getEpicID()) {
            throw new IllegalArgumentException("Нельзя добавить подзадачу как Эпик.");
        } else if (epic.getID() == subtask.getID()) {
            throw new IllegalArgumentException("Нельзя добавить Эпик как подзадачу.");
        }

        epic.addSubtask(subtask);
        subtasks.put(subtask.getID(), subtask);
        updateEpicStatus(epic);
    }

    @Override
    public Task updateTask(Task task) {
        Integer taskID = task.getID();

        if (taskID == null || !tasks.containsKey(taskID)) {
            return null;
        }

        tasks.replace(taskID, task);
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Integer epicID = epic.getID();
        if (epicID == null || !epics.containsKey(epicID)) {
            return null;
        }
        Epic oldEpic = epics.get(epicID);
        List<Subtask> oldEpicSubtaskList = oldEpic.getSubtaskList();
        if (!oldEpicSubtaskList.isEmpty()) {
            for (Subtask subtask : oldEpicSubtaskList) {
                subtasks.remove(subtask.getID());
            }
        }
        epics.replace(epicID, epic);
        List<Subtask> newEpicSubtaskList = epic.getSubtaskList();
        if (!newEpicSubtaskList.isEmpty()) {
            for (Subtask subtask : newEpicSubtaskList) {
                subtasks.put(subtask.getID(), subtask);
            }
        }
        updateEpicStatus(epic);
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Integer subtaskID = subtask.getID();
        if (subtaskID == null || !subtasks.containsKey(subtaskID)) {
            return null;
        }
        int epicID = subtask.getEpicID();
        Subtask oldSubtask = subtasks.get(subtaskID);
        subtasks.replace(subtaskID, subtask);
        Epic epic = epics.get(epicID);
        List<Subtask> subtaskList = epic.getSubtaskList();
        subtaskList.remove(oldSubtask);
        subtaskList.add(subtask);
        updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public Task getTaskByID(int id) {
        historyManager.addTaskHistory(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicByID(int id) {
        historyManager.addTaskHistory(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        historyManager.addTaskHistory(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        return epic.getSubtaskList();
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void deleteTaskByID(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicByID(int id) {
        List<Subtask> epicSubtasks = epics.get(id).getSubtaskList();
        epics.remove(id);
        for (Subtask subtask : epicSubtasks) {
            subtasks.remove(subtask.getID());
        }
    }

    @Override
    public void deleteSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        int epicID = subtask.getEpicID();
        subtasks.remove(id);
        Epic epic = epics.get(epicID);
        List<Subtask> subtaskList = epic.getSubtaskList();
        subtaskList.remove(subtask);
        epic.setSubtaskList(subtaskList);
        updateEpicStatus(epic);
    }

    void updateEpicStatus(Epic epic) {
        int allIsDoneCount = 0;
        int allIsInNewCount = 0;
        List<Subtask> list = epic.getSubtaskList();

        for (Subtask subtask : list) {
            if (subtask.getStatus() == Status.DONE) {
                allIsDoneCount++;
            }
            if (subtask.getStatus() == Status.NEW) {
                allIsInNewCount++;
            }
        }
        if (allIsDoneCount == list.size()) {
            epic.setStatus(Status.DONE);
        } else if (allIsInNewCount == list.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
