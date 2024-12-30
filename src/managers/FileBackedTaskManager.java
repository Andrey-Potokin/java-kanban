package managers;

import tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTaskManager() {
    }

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
                    .append(task.getDescription());

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
                    manager.addTask(task);
                } else if (type.equals(Type.EPIC)) {
                    manager.addEpic((Epic) task);
                } else if (type.equals(Type.SUBTASK)) {
                    manager.addSubtask((Subtask) task);
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
        Type type = Type.valueOf(split[1]);
        String title = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];

        if (split[1].equals("TASK")) {
            return new Task(id, title, description, status);
        } else if (split[1].equals("EPIC")) {
            return new Epic(id, title, description, status);
        } else {
            int epicId = Integer.parseInt(split[5]);
            return new Subtask(id, title, description, status, epicId);
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
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

