package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final DoublyLinkedList<Task> tasksHistory = new DoublyLinkedList<>();

    @Override
    public void createTaskHistory(Task task) {
        if (tasksHistory.getNodes().containsKey(task.getId())) {
            removeNode(tasksHistory.getNodes().get(task.getId()));
        }
        tasksHistory.linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return tasksHistory.getTasks();
    }

    // todo должен быть метод remove(int Id)
    @Override
    public void removeNode(Node<Task> node) {
        tasksHistory.remove(node);
    }

    // todo тогда этот метод не понадобится
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

            nodes.put(((Task) element).getId(), newNode);
        }

        public List<Task> getTasks() {
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
                nodes.remove(((Task) node.getData()).getId());
            }
        }



        public int size() {
            return this.size;
        }
    }
}