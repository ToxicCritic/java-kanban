package managers;

import managers.historyManager.InMemoryHistoryManager;
import managers.taskManager.FileBackedTaskManager;
import managers.taskManager.InMemoryTaskManager;
import managers.taskManager.TaskManager;

import java.io.File;
import java.nio.file.Paths;


public class Managers {
    public static final String FILENAME = "tasks.csv";

    public static TaskManager getDefault() {
        return FileBackedTaskManager.loadFromFile(new File(System.getProperty("user.dir"), FILENAME));
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
