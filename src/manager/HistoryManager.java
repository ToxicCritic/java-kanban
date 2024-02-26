package manager;

import tasks.Task;

import java.util.Collection;

public interface HistoryManager {
    public void add(Task task);

    public Collection<Task> getHistory();
}
