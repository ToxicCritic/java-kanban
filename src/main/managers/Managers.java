package main.managers;

import main.managers.historyManager.InMemoryHistoryManager;
import main.managers.taskManager.FileBackedTaskManager;
import main.managers.taskManager.TaskManager;

import java.io.File;


public class Managers {
    public static final String FILENAME = "tasks.csv";

    public static TaskManager getDefault() {
        return FileBackedTaskManager.loadFromFile(new File(System.getProperty("user.dir") + "/src/main/", FILENAME));
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
