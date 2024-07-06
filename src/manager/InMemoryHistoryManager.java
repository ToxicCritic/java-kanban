package manager;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> history;

    public InMemoryHistoryManager() {
        this.history = new LinkedList<>();
    }

    @Override
    public LinkedList<Task> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {
        if (history.size() >= 10) {
            history.removeFirst();
        }
        history.addLast(task);
    }

    @Override
    public void remove(int id) {

    }
}

class HistoryLinkedList<T> {
    public Node<T> head;
    public Node<T> tail;
    private int size = 0;

    public HistoryLinkedList() {
        this.head = null;
        this.tail = null;
    }

    public void linkLast(T element) {
        Node<T> last = tail;
        Node<T> newNode = new Node<>(tail, element, null);
        tail = newNode;
        if (last == null)
            head = newNode;
        else
            last.next = newNode;
        size++;
    }

    public List<T> getTasks() {
        List<T> history = new ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            history.add(current.data);
            current = current.next;
        }
        return history;
    }

    public void remove() {

    }
}
