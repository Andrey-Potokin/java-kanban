import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final DoublyLinkedList<Task> tasksHistory = new DoublyLinkedList<>();

    @Override
    public void addTaskHistory(Task task) {
        if (tasksHistory.getNodes().containsKey(task.getID())) {
            removeNode(tasksHistory.getNodes().get(task.getID()));
        }
        tasksHistory.linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return tasksHistory.getTask();
    }

    @Override
    public void removeNode(Node<Task> node) {
        tasksHistory.remove(node);
    }

    @Override
    public Map<Integer, Node<Task>> getNodes() {
        return tasksHistory.getNodes();
    }


    private static class DoublyLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;
        private int size = 0;
        private final Map<Integer, Node<T>> nodes = new HashMap<>();

        public Map<Integer, Node<T>> getNodes() {
            return nodes;
        }

        public void linkLast(T element) {
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.setNext(newNode);
                newNode.setPrev(oldTail);
            }
            size++;

            nodes.put(((Task) element).getID(), newNode);
        }

        public List<Task> getTask() {
            List<Task> tasks = new ArrayList<>();
            Node<T> current = head;
            while (current != null) {
                tasks.add((Task) current.getData());
                current = current.getNext();
            }
            return tasks;
        }

        public Node<T> getLastNode() {
            return tail;
        }

        public void remove(Node<T> node) {
            if (node == null) return;

            if (node == head) {
                head = node.getNext();
            } else {
                node.getPrev().setNext(node.getNext());
            }

            if (node == tail) {
                tail = node.getPrev();
            } else {
                node.getNext().setPrev(node.getPrev());
            }

            size--;

            node.setPrev(null);
            node.setNext(null);

            if (node.getData() instanceof Task) {
                nodes.remove(((Task) node.getData()).getID());
            }
        }



        public int size() {
            return this.size;
        }
    }
}