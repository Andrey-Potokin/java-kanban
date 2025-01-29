package managers;

import tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    // сохраняет все задачи
    public void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write(toString(getAllTasks()));
        } catch (IOException e) {
            throw new ManagerSaveException("Файл не сохранен!");
        }
    }

    // сохраняет задачу в строку
    public String toString(ArrayList<Task> tasks) {
        StringBuilder sb = new StringBuilder();
        for (Task task : tasks) {
            sb.append(task.getId()).append(", ")
                    .append(task.getType()).append(", ")
                    .append(task.getTitle()).append(", ")
                    .append(task.getStatus()).append(", ")
                    .append(task.getDescription()).append(", ")
                    .append(task.getStartTime()).append(", ")
                    .append(task.getDuration());

            if (task.getType() == Type.EPIC && task instanceof Epic epic) {
                sb.append(", ").append(epic.getEndTime());
            }

            if (task.getType() == Type.SUBTASK && task instanceof Subtask subtask) {
                sb.append(", ").append(subtask.getEpicId());
            }
                    sb.append("\n");
        }
        return sb.toString();
    }

    // восстанавливает данные менеджера из файла при запуске программы.
    public FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager;
        try {
            manager = new FileBackedTaskManager(file);
            List<String> lines = Files.readAllLines(file.toPath());

            for (String line : lines) {
                Task task = fromString(line);
                Type type = task.getType();
                if (type.equals(Type.TASK)) {
                    manager.createTask(task);
                } else if (type.equals(Type.EPIC)) {
                    manager.createEpic((Epic) task);
                } else if (type.equals(Type.SUBTASK)) {
                    manager.createSubtask((Subtask) task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки");
        }
        return manager;
    }

    // создает задачу из строки
    public static Task fromString(String value) {
        String[] split = value.split(", ");
        int id = Integer.parseInt(split[0]);
        String type = split[1];
        String title = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];
        LocalDateTime startTime = LocalDateTime.parse(split[5]);
        Duration duration = Duration.parse(split[6]);

        return switch (type) {
            case "TASK" -> new Task(id, title, status, description, startTime, duration);
            case "EPIC" -> {
                LocalDateTime endTime = LocalDateTime.parse(split[7]);
                yield new Epic(id, title, status, description, startTime, duration, endTime);
            }
            case "SUBTASK" -> {
                int epicId = Integer.parseInt(split[7]);
                yield new Subtask(id, title, status, description, startTime, duration, epicId);
            }
            default -> throw new IllegalArgumentException("Неверный тип задачи");
        };
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }
}

