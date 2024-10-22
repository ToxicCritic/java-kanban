package main.tasks;

import main.managers.taskManager.FileBackedTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtasks;
    private LocalDateTime endTime;

    public Epic(String title, String description, TaskStatus status) {
        super(title, description, status);
        this.subtasks = new ArrayList<>();
        this.endTime = LocalDateTime.MIN;
    }

    public Epic(String title, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        super(title, description, status, duration, startTime);
        this.subtasks = new ArrayList<>();
        this.endTime = LocalDateTime.MIN;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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