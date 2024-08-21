package main.tasks;

import main.managers.taskManager.FileBackedTaskManager;

import java.util.ArrayList;

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

    @Override
    public String toCsvString() {
        return String.format("%d,EPIC,%s,%s,%s,\n", getId(), getTitle(), getStatus(), getDescription());
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