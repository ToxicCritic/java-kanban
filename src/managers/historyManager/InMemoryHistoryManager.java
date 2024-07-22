package managers.historyManager;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> taskMap;
    private Node<Task> tail;
    private Node<Task> head;

    public InMemoryHistoryManager() {
        this.taskMap = new HashMap<>();
        this.head = null;
        this.tail = null;
    }

    // Метод добавления задачи в конец списка
    private void linkLast(Task element) {
        Node<Task> last = tail;
        Node<Task> newNode = new Node<>(tail, element, null);
        tail = newNode;
        if (last == null) {
            head = newNode;
        }
        else {
            last.setNext(newNode);
        }
        taskMap.put(element.getId(), newNode);
    }

    // Метод удаления узла из списка
    private void removeNode(Node<Task> node) {
        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
        }
        else {
            head = node.getNext();
        }
        if (node.getNext() != null) {
            node.getNext().setPrev(node.getPrev());

        }
        else {
            tail = node.getPrev();
        }
    }

    private List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            history.add(current.getData());
            current = current.getNext();
        }
        return history;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (taskMap.containsKey(task.getId())) {
            removeNode(taskMap.get(task.getId()));
            taskMap.remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        if (taskMap.containsKey(id)) {
            removeNode(taskMap.get(id));
            taskMap.remove(id);
        }
    }
}

