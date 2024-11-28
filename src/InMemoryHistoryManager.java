import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final DoublyLinkedList<Task> tasksHistory = new DoublyLinkedList<>(); // хранит историю просмотров задач
    final Map<Integer, Node<Task>> nodes = new HashMap<>(); // хранит узлы

    @Override
    public void addTaskHistory(Task task) {
        if (nodes.containsKey(task.getID())) {
            removeNode(nodes.get(task.getID()));
        }
        tasksHistory.linkLast(task);
        nodes.put(task.getID(), tasksHistory.getLastNode());
    }

    @Override
    public List<Task> getHistory() {
        return tasksHistory.getTask();
    }

    @Override
    public void removeNode(Node<Task> node) {
        if (node == null) return;
        tasksHistory.remove(node);
        nodes.remove(node.data.getID());
    }
}

class DoublyLinkedList<T> {

    private Node<T> head;
    private Node<T> tail;
    private int size = 0;

    // добавляет задачу в конец списка
    public void linkLast(T element) {
        final Node<T> oldTail = tail;
        final Node<T> newNode = new Node<>(oldTail, element, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
            newNode.prev = oldTail;
        }
        size++;
    }

    // собирает все задачи
    public List<Task> getTask() {
        List<Task> tasks = new ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            tasks.add((Task) current.data);
            current = current.next;
        }
        return tasks;
    }

    // возвращает последний узел
    public Node<T> getLastNode() {
        return tail;
    }

    public void remove(Node<T> node) {
        if (node == null) return;

        Node<T> prevNode = node.prev;
        Node<T> nextNode = node.next;

        if (prevNode == null) {
            head = nextNode;
        } else {
            prevNode.next = nextNode;
            node.prev = null;
        }

        if (nextNode == null) {
            tail = prevNode;
        } else {
            nextNode.prev = prevNode;
            node.next = null;
        }
        size--;
    }

    public int size() {
        return this.size;
    }
}