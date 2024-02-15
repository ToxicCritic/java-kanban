import java.util.ArrayList;
import java.util.HashMap;

class TaskManager {
    private HashMap<Integer, Task> tasks;

    public TaskManager() {
        tasks = new HashMap<>();
    }

    // Метод для создания задачи
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    // Метод для получения задачи по ID
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    // Метод для удаления задачи по ID
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    // Метод для обновления задачи
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    // Метод для получения списка всех задач
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Метод для получения списка всех подзадач определённого эпика
    public ArrayList<Integer> getAllSubtasksOfEpic(int epicId) {
        ArrayList<Integer> subtasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Subtask && ((Subtask) task).getEpicId() == epicId) {
                subtasks.add(task.getId());
            }
        }
        return subtasks;
    }
}