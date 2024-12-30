package managers;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

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

    // генерация Id
    private int generateId() {
        return idCounter++;
    }

    public void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }

    @Override
    public void addTask(Task task) {
        if (task.getId() == 0) {
            task.setId(generateId());
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic.getId() == 0) {
            epic.setId(generateId());
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask.getId() == 0) {
            subtask.setId(generateId());
        }
        Epic epic = epics.get(subtask.getEpicId());

        if (epic == null) {
            throw new IllegalArgumentException("Сначала нужно создать Эпик");
        } else if (subtask.getId() == subtask.getEpicId()) {
            throw new IllegalArgumentException("Нельзя добавить подзадачу как Эпик.");
        } else if (epic.getId() == subtask.getId()) {
            throw new IllegalArgumentException("Нельзя добавить Эпик как подзадачу.");
        }

        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
    }

    @Override
    public void updateTask(Task task) {
        int taskId = task.getId();
        tasks.replace(taskId, task);
    }

    @Override
    public void updateEpic(Epic epic) {
        int epicId = epic.getId();
        Epic oldEpic = epics.get(epicId);
        List<Subtask> oldEpicSubtaskList = oldEpic.getSubtaskList();
        if (!oldEpicSubtaskList.isEmpty()) {
            for (Subtask subtask : oldEpicSubtaskList) {
                subtasks.remove(subtask.getId());
            }
        }
        epics.replace(epicId, epic);
        List<Subtask> newEpicSubtaskList = epic.getSubtaskList();
        if (!newEpicSubtaskList.isEmpty()) {
            for (Subtask subtask : newEpicSubtaskList) {
                subtasks.put(subtask.getId(), subtask);
            }
        }
        updateEpicStatus(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int subtaskId = subtask.getId();
        int epicId = subtask.getEpicId();
        Subtask oldSubtask = subtasks.get(subtaskId);
        subtasks.replace(subtaskId, subtask);
        Epic epic = epics.get(epicId);
        List<Subtask> subtaskList = epic.getSubtaskList();
        subtaskList.remove(oldSubtask);
        subtaskList.add(subtask);
        updateEpicStatus(epic);
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.addTaskHistory(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.addTaskHistory(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.addTaskHistory(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subtasks.values());
        return allTasks;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
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
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        List<Subtask> epicSubtasks = epics.get(id).getSubtaskList();
        epics.remove(id);
        for (Subtask subtask : epicSubtasks) {
            subtasks.remove(subtask.getId());
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        int epicId = subtask.getEpicId();
        subtasks.remove(id);
        Epic epic = epics.get(epicId);
        List<Subtask> subtaskList = epic.getSubtaskList();
        subtaskList.remove(subtask);
        epic.setSubtaskList(subtaskList);
        updateEpicStatus(epic);
    }

    @Override
    public void updateEpicStatus(Epic epic) {
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
