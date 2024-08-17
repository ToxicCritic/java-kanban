package managers;

import managers.historyManager.InMemoryHistoryManager;
import managers.taskManager.FileBackedTaskManager;
import managers.taskManager.InMemoryTaskManager;
import managers.taskManager.TaskManager;

import java.io.File;
import java.nio.file.Paths;

public class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager( new File(System.getProperty("user.dir"), "tasks.csv"));
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
