package tasks;

public class Subtask extends Task {
    private int epicId;

    @Override
    public String toString() {
        return String.format("ID: %d | Тип: Подзадача | Название: %s | Статус: %s | Описание: %s | ID Эпика: %d",
                getId(), getTitle(), getStatus(), getDescription(), epicId);
    }
    public Subtask(String title, String description, TaskStatus status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}