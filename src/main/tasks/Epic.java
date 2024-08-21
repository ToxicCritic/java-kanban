package main.tasks;

import main.managers.taskManager.FileBackedTaskManager;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasks;

    public Epic(String title, String description, TaskStatus status) {
        super(title, description, status);
        this.subtasks = new ArrayList<>();
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