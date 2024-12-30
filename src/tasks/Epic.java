package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtaskList = new ArrayList<>();
    private Type type = Type.EPIC;

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
    }

    public void addSubtask(Subtask subtask) {
        subtaskList.add(subtask);
    }

    public void clearSubtasks() {
        subtaskList.clear();
    }

    public List<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void setSubtaskList(List<Subtask> subtaskList) {
        this.subtaskList = subtaskList;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "title= " + getTitle() + '\'' +
                ", description = " + getDescription() + '\'' +
                ", id=" + getId() +
                ", subtaskList.size = " + subtaskList.size() +
                ", status = " + getStatus() +
                ", type=" + type +
                '}';
    }
}