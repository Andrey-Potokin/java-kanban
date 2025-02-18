package managers;

import exceptions.TaskIntersectionException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int idCounter = 1;

    /**
     * Генерирует идентификатор задачи
     */
    private int generateId() {
        return idCounter++;
    }

    /**
     * Добавляет задачу без пересечения с другими задачами.
     *
     * @param task задача, которую нужно добавить
     * @throws TaskIntersectionException если задача пересекается с другими задачами
     */
    @Override
    public void createTask(Task task) {
        if (task.getId() == 0) {
            if (!isValidIntersection(task)) {
                task.setId(generateId());
            } else {
                throw new TaskIntersectionException("Пересечение задач");
            }
        }
        tasks.put(task.getId(), task);
    }

    /**
     * Добавляет Epic без пересечения с другими задачами.
     *
     * @param epic
     * @throws TaskIntersectionException если задача пересекается с другими задачами
     */
    @Override
    public void createEpic(Epic epic) {
        if (epic.getId() == 0) {
            if (!isValidIntersection(epic)) {
                epic.setId(generateId());
            } else {
                throw new TaskIntersectionException("Пересечение задач");
            }
        }
        epics.put(epic.getId(), epic);
    }

    /**
     * Добавляет Subtask без пересечения с другими задачами.
     *
     * @param subtask
     * @throws TaskIntersectionException если задача пересекается с другими задачами
     */
    @Override
    public void createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            if (subtask.getId() == 0) {
                if (!isValidIntersection(subtask)) {
                    subtask.setId(generateId());
                } else {
                    throw new TaskIntersectionException("Пересечение задач");
                }
            }
        } else {
            throw new IllegalArgumentException("Сначала нужно создать Эпик");
        }
        validateSubtaskAndEpic(subtask, epic);
        epic.createSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
        updateEpicStartTime(epic);
        updateEpicEndTime(epic);
    }

    public void validateSubtaskAndEpic(Subtask subtask, Epic epic) {
        if (subtask.getId() == subtask.getEpicId()) {
            throw new IllegalArgumentException("Нельзя добавить подзадачу как Эпик.");
        } else if (epic.getId() == subtask.getId()) {
            throw new IllegalArgumentException("Нельзя добавить Эпик как подзадачу.");
        }
    }

    /**
     * Обновляет задачу.
     *
     * @param task задача, которую нужно обновить
     */
    @Override
    // TODO переписать метод с учетом полей с null значениями и без метода replace
    public void updateTask(Task task) {
        if (!isValidIntersection(task)) {
            int taskId = task.getId();
            tasks.replace(taskId, task);
        } else {
            throw new TaskIntersectionException("Пересечение задач");
        }
    }

    /**
     * Обновляет эпик и его подзадачи.
     *
     * @param epic эпик, который нужно обновить
     */
    @Override
    // TODO переписать метод с учетом полей с null значениями и без метода replace
    public void updateEpic(Epic epic) {
        if (!isValidIntersection(epic)) {
            // Получение идентификатора
            int epicId = epic.getId();
            // Получение старого эпика из карты эпиков
            Epic oldEpic = epics.get(epicId);
            // Получение списка подзадач старого эпика
            List<Subtask> oldEpicSubtaskList = oldEpic.getSubtaskList();
            // Если список подзадач старого эпика не пустой
            if (!oldEpicSubtaskList.isEmpty()) {
                // Удаление всех подзадач старого эпика из карты подзадач
                for (Subtask subtask : oldEpicSubtaskList) {
                    subtasks.remove(subtask.getId());
                }
            }
            // Замена старого эпика на новый эпик в карте эпиков
            epics.replace(epicId, epic);
            // Получение списка подзадач нового эпика
            List<Subtask> newEpicSubtaskList = epic.getSubtaskList();
            // Если список подзадач нового эпика не пустой
            if (!newEpicSubtaskList.isEmpty()) {
                // Добавление всех подзадач нового эпика в карту подзадач
                for (Subtask subtask : newEpicSubtaskList) {
                    subtasks.put(subtask.getId(), subtask);
                }
            }
            // Обновление статуса эпика
            updateEpicStatus(epic);
        } else {
            throw new TaskIntersectionException("Пересечение задач");
        }
    }

    @Override
    // TODO переписать метод с учетом полей с null значениями и без метода replace
    public void updateSubtask(Subtask subtask) {
        if (!isValidIntersection(subtask)) {
            int subtaskId = subtask.getId();
            int epicId = subtask.getEpicId();
            Subtask oldSubtask = subtasks.get(subtaskId);
            subtasks.replace(subtaskId, subtask);
            Epic epic = epics.get(epicId);
            List<Subtask> subtaskList = epic.getSubtaskList();
            subtaskList.remove(oldSubtask);
            subtaskList.add(subtask);
            updateEpicStatus(epic);
            updateEpicStartTime(epic);
            updateEpicEndTime(epic);
        } else {
            throw new TaskIntersectionException("Пересечение задач");
        }
    }

    @Override
    public Task getTaskById(int id) throws IllegalArgumentException {
        if (tasks.containsKey(id)) {
            historyManager.createTaskHistory(tasks.get(id));
            return tasks.get(id);
        } else {
            throw new IllegalArgumentException("Задача с таким id (" + id + ") не найдена");

        }
    }

    @Override
    public Epic getEpicById(int id) throws IllegalArgumentException {
        if (epics.containsKey(id)) {
            historyManager.createTaskHistory(epics.get(id));
            return epics.get(id);
        } else {
            throw new IllegalArgumentException("Эпик с таким id (" + id + ") не найден");
        }
    }

    @Override
    public Subtask getSubtaskById(int id) throws IllegalArgumentException {
        if (subtasks.containsKey(id)) {
            historyManager.createTaskHistory(subtasks.get(id));
            return subtasks.get(id);
        } else {
            throw new IllegalArgumentException("Подзадача с таким id (" + id + ") не найдена");
        }
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

    /**
     * Устанавливает статус Epic в зависимости от статуса подзадач.
     *
     * @param epic
     */
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

    /**
     * Устанавливает время начала Epic. Начало Epic совпадает с началом самой ранней подзадачи.
     *
     * @param epic
     */
    @Override
    public void updateEpicStartTime(Epic epic) {
        epic.getSubtaskList().stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .min(Comparator.comparing(Subtask::getStartTime))
                .ifPresent(subtask -> epic.setStartTime(subtask.getStartTime()));
    }

    /**
     * Устанавливает время окончания Epic. Конец Epic совпадает с концом самой последней подзадачи.
     *
     * @param epic
     */
    @Override
    public void updateEpicEndTime(Epic epic) {
        epic.getSubtaskList().stream()
                .filter(subtask -> subtask.getEndTime() != null)
                .max(Comparator.comparing(Subtask::getEndTime))
                .ifPresent(subtask -> epic.setEndTime(subtask.getEndTime()));
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    /**
     * Возвращает список всех задач, отсортированных по времени начала.
     */
    @Override
    public Set<Task> getPrioritizedTasks() {
        Set<Task> set = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        set.addAll(getAllTasks());
        return set;
    }

    /**
     * Проверяет, пересекается ли указанная задача с другими задачами.
     *
     * @param task задача, которую нужно проверить
     * @return true, если задача пересекается с другими задачами, false в противном случае
     */
    @Override
    public boolean isValidIntersection(Task task) {
        return getAllTasks().stream()
                .anyMatch(otherTask -> task.getEndTime().isAfter(otherTask.getStartTime()) &&
                        otherTask.getEndTime().isAfter(task.getStartTime()));
    }
}