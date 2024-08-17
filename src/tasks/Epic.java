package tasks;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private final ArrayList<Integer> subtasks;

    public Epic(String title, String description, TaskStatus status) {
        super(title, description, status);
        this.subtasks = new ArrayList<>();
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Тип: Эпик | Название: %s | Статус: %s | Описание: %s | ID подзадач: %s",
                getId(), getTitle(), getStatus(), getDescription(), subtasks.toString());
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