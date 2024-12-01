public class Subtask extends Task {

    private final int epicID;

    public Subtask(String title, String description, int epicID) {
        super(title, description);
        this.epicID = epicID;
    }

    public Subtask(int id, String title, String description, Status status, int epicID) {
        super(id, title, description, status);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getID() +
                ", epicID=" + epicID +
                ", status=" + getStatus() +
                '}';
    }
}
