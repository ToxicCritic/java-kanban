package tasks;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private ArrayList<Integer> subtasks;

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