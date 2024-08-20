package tasks;

import managers.taskManager.FileBackedTaskManager;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private final ArrayList<Integer> subtasks;

    public Epic(String title, String description, TaskStatus status) {
        super(title, description, status);
        this.subtasks = new ArrayList<>();
    }

    @Override
    public void addToManager(FileBackedTaskManager manager) {
        manager.createEpic(this);
    }

    public void addSubtask(int subtaskId) {
        subtasks.add(subtaskId);
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void removeSubtask(Integer subtaskId) {
        subtasks.remove(subtaskId);
    }
}