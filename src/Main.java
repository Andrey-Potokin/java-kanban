public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        manager.addTask(new Task("Задача1", "Описание задачи1"));
        manager.addTask(new Task("Задача2", "Описание задачи2"));
        Epic epic = new Epic("Эпик1", "Описание эпик1");
        manager.addEpic(epic);
        manager.addSubtask(new Subtask("Подзадача1", "Описание подзадачи1", epic.getID()));
        manager.addSubtask(new Subtask("Подзадача2", "Описание подзадачи2", epic.getID()));
        manager.addSubtask(new Subtask("Подзадача3", "Описание подзадачи3", epic.getID()));
        manager.addEpic(new Epic("Эпик2", "Описание эпик2"));

        manager.getTaskByID(2);
        manager.getTaskByID(1);
        manager.getTaskByID(2);
        manager.getEpicByID(3);
        manager.getEpicByID(7);
        manager.getSubtaskByID(5);
        manager.getSubtaskByID(4);
        manager.getSubtaskByID(4);

        System.out.println(manager.getHistory());
    }
}