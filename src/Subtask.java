class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String title, String description, TaskStatus status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    // Геттер для ID эпика
    public int getEpicId() {
        return epicId;
    }
}