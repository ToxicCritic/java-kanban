package managers;

import managers.historyManager.InMemoryHistoryManager;
import managers.taskManager.InMemoryTaskManager;
import managers.taskManager.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
