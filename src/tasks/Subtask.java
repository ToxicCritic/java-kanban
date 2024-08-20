package tasks;
import managers.taskManager.FileBackedTaskManager;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String title, String description, TaskStatus status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    @Override
    public void addToManager(FileBackedTaskManager manager) {
        manager.createSubtask(this);
    }

    public int getEpicId() {
        return epicId;
    }
}