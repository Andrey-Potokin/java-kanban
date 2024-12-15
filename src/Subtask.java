public class Subtask extends Task {

    private final int epicId;

    public Subtask(String title, String description, Type type, int epicId) {
        super(title, description, type);
        this.epicId = epicId;
    }

    public Subtask(int id, String title, String description, Status status, Type type, int epicId) {
        super(id, title, description, status, type);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", epicId=" + epicId +
                ", status=" + getStatus() +
                '}';
    }
}
