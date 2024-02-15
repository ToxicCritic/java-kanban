import java.util.ArrayList;

class Epic extends Task {
    private ArrayList<Integer> subtasks;

    public Epic(int id, String title, String description, TaskStatus status) {
        super(id, title, description, status);
        this.subtasks = new ArrayList<>();
    }

    // Метод для добавления подзадачи в эпик
    public void addSubtask(int subtaskId) {
        subtasks.add(subtaskId);
    }

    // Геттер для списка подзадач
    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }
}