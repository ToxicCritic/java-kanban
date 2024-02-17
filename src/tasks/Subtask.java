package tasks;

import manager.TaskManager;

public class Subtask extends Task {
    private int epicId;

    @Override
    public String toString() {
        return "[id=" + getId() +
                ", title=" + getTitle() +
                ", description=" + getDescription().length() +
                ", status=" + getStatus() + "]";
    }
    public Subtask(String title, String description, TaskStatus status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}